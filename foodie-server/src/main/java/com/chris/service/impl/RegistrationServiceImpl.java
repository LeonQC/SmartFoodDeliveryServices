package com.chris.service.impl;

import com.chris.dto.RegistrationDTO;
import com.chris.entity.Merchant;
import com.chris.entity.User;
import com.chris.exception.UsernameAlreadyExistsException;
import com.chris.mapper.RegistrationMapper;
import com.chris.repository.UserRepository;
import com.chris.service.RegistrationService;
import com.chris.utils.GeoUtil;
import com.chris.utils.GoogleGeocodingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.chris.constant.MessageConstant.USERNAME_ALREADY_EXISTS;

@Service
public class RegistrationServiceImpl implements RegistrationService {
    @Autowired
    private RegistrationMapper mapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GoogleGeocodingUtil googleGeocodingUtil;

    @Override
    public void register(RegistrationDTO dto) {
        String username = dto.getUser().getUsername();
        if (userRepository.existsByUsername(username)) {
            throw new UsernameAlreadyExistsException(USERNAME_ALREADY_EXISTS);
        }
        User user = mapper.toUser(dto);

        // 1. 如果是商家，自动补经纬度/location
        if (user.getMerchant() != null) {
            Merchant m = user.getMerchant();
            String fullAddress = buildFullAddress(
                    m.getAddress(), m.getCity(), m.getState(), m.getZipcode(), m.getCountry()
            );
            Optional<double[]> latLng = googleGeocodingUtil.fetchLatLng(fullAddress); // 自己实现HTTP调用Google API
            if (latLng.isPresent()) {
                Double lat = latLng.get()[0];
                Double lng = latLng.get()[1];
                m.setLatitude(lat);
                m.setLongitude(lng);
                m.setLocation(GeoUtil.makePoint(lng, lat)); // 经度在前
            } else {
                // 可以抛异常/或用默认值
                throw new RuntimeException("未能根据地址获取经纬度");
            }
        }

        userRepository.save(user);
    }

    // 拼完整地址
    private String buildFullAddress(String address, String city, String state, String zipcode, String country) {
        return Stream.of(address, city, state, zipcode, country)
                .filter(s -> s != null && !s.isEmpty())
                .collect(Collectors.joining(", "));
    }
}
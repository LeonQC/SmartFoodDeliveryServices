package com.chris.service.impl;

import com.chris.dto.ProfileUpdateDTO;
import com.chris.entity.Merchant;
import com.chris.entity.User;
import com.chris.exception.UserNotFoundException;
import com.chris.mapper.ProfileMapper;
import com.chris.repository.UserRepository;
import com.chris.service.ProfileService;
import com.chris.utils.GeoUtil;
import com.chris.utils.GoogleGeocodingUtil;
import com.chris.vo.profileVOs.ProfileVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.chris.constant.MessageConstant.USER_NOT_FOUND;

@Service
public class ProfileServiceImpl implements ProfileService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileMapper mapper;

    @Autowired
    private GoogleGeocodingUtil googleGeocodingUtil;

    @Override
    @Transactional(readOnly = true)
    public ProfileVO getProfile(Long userId) {
        User user = userRepository.findDetailedByUserId(userId)
                                  .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));
        return mapper.toProfileVO(user);
    }

    @Override
    @Transactional
    public ProfileVO updateProfile(Long userId, ProfileUpdateDTO dto) {
        User user = userRepository.findDetailedByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));

        // 1. 更新 User 本身（包括把 profileCompleted 设为 true）
        mapper.updateUserFromDto(dto, user);
        user.setUpdateTime(LocalDateTime.now());
        // 2. 根据角色更新子实体
        switch (user.getRole()) {
            case MERCHANT -> {
                // 映射DTO, 其中不包括Lat Lng Location, 需要自己补充
                mapper.updateMerchantFromDto(dto, user.getMerchant());
                // 拿到Merchant中的地址信息，生成LatLng
                Merchant m = user.getMerchant();
                String fullAddress = buildFullAddress(
                        m.getAddress(),
                        m.getCity(),
                        m.getState(),
                        m.getZipcode(),
                        m.getCountry()
                );
                Optional<double[]> latLng = googleGeocodingUtil.fetchLatLng(fullAddress);
                double[] coords = latLng.orElseThrow(() -> new RuntimeException("未能根据地址获取经纬度"));
                m.setLatitude(coords[0]);
                m.setLongitude(coords[1]);
                m.setLocation(GeoUtil.makePoint(coords[1], coords[0]));
            }
            case CLIENT   -> mapper.updateClientFromDto(dto, user.getClient());
            case RIDER    -> mapper.updateRiderFromDto(dto, user.getRider());
        }
        // 3. 持久化并返回最新的 VO
        user = userRepository.save(user);
        return mapper.toProfileVO(user);
    }

    private String buildFullAddress(String address, String city, String state, String zipcode, String country) {
        return Stream.of(address, city, state, zipcode, country)
                .filter(s -> s != null && !s.isEmpty())
                .collect(Collectors.joining(", "));
    }
}

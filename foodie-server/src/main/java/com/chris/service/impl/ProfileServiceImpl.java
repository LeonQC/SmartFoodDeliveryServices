package com.chris.service.impl;

import com.chris.dto.ProfileUpdateDTO;
import com.chris.entity.Merchant;
import com.chris.entity.User;
import com.chris.exception.UserNotFoundException;
import com.chris.mapper.ProfileMapper;
import com.chris.repository.UserRepository;
import com.chris.service.ProfileService;
import com.chris.service.S3Service;
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

    @Autowired
    private S3Service s3Service;

    @Override
    @Transactional(readOnly = true)
    public ProfileVO getProfile(Long userId) {
        User user = userRepository.findDetailedByUserId(userId)
                                  .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));
        return mapper.toProfileVO(user);
    }

    @Override
    @Transactional
    public void updateProfile(Long userId, ProfileUpdateDTO dto) {
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

                String oldKey = user.getMerchant().getMerchantImage();
                String tempKey = dto.getMerchantImage();
                if (tempKey != null && !tempKey.isBlank() && !tempKey.equals(oldKey)) {
                    // 删掉旧的
                    if (oldKey != null && !oldKey.isBlank()) {
                        s3Service.deleteImage(oldKey);
                    }
                    // 持久化新的
                    String permKey = s3Service.persistTemporaryImage(tempKey, "Merchants");
                    user.getMerchant().setMerchantImage(permKey);
                }
            }
            case CLIENT -> {
                mapper.updateClientFromDto(dto, user.getClient());

                String oldKey = user.getClient().getAvatar();
                String tempKey = dto.getAvatar(); // 前端传来的，回显时可能是永久 key

                // 只有当 tempKey 真不一样，且非空，才做更新
                if (tempKey != null && !tempKey.isBlank() && !tempKey.equals(oldKey)) {
                    // 删掉旧的
                    if (oldKey != null && !oldKey.isBlank()) {
                        s3Service.deleteImage(oldKey);
                    }
                    // 持久化新的
                    String permKey = s3Service.persistTemporaryImage(tempKey, "Clients");
                    user.getClient().setAvatar(permKey);
                }
            }
            case RIDER -> {
                mapper.updateRiderFromDto(dto, user.getRider());
                String oldKey = user.getRider().getAvatar();
                String tempKey = dto.getAvatar();

                // 只有当 tempKey 真不一样，且非空，才做更新
                if (tempKey != null && !tempKey.isBlank() && !tempKey.equals(oldKey)) {
                    // 删掉旧的
                    if (oldKey != null && !oldKey.isBlank()) {
                        s3Service.deleteImage(oldKey);
                    }
                    // 持久化新的
                    String permKey = s3Service.persistTemporaryImage(tempKey, "Riders");
                    user.getRider().setAvatar(permKey);
                }
            }
        }
        // 3. 持久化并返回最新的 VO
        user = userRepository.save(user);
        mapper.toProfileVO(user);
    }

    private String buildFullAddress(String address, String city, String state, String zipcode, String country) {
        return Stream.of(address, city, state, zipcode, country)
                .filter(s -> s != null && !s.isEmpty())
                .collect(Collectors.joining(", "));
    }
}

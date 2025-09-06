package com.chris.service.impl;

import com.chris.dto.RiderLocationDTO;
import com.chris.entity.Rider;
import com.chris.exception.UserNotFoundException;
import com.chris.repository.UserRepository;
import com.chris.service.RiderService;
import com.chris.vo.resultVOs.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.chris.constant.MessageConstant.USER_NOT_FOUND;
import static com.chris.constant.RedisConstant.RIDER_LOCATION_KEY;
import static com.chris.constant.RedisConstant.RIDER_ONLINE_KEY;

@Service
public class RiderServiceImpl implements RiderService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result<Short> getStatus(Long userId) {
        // 1. 先查 Redis 在线集合
        Boolean isOnline = stringRedisTemplate.opsForSet().isMember(RIDER_ONLINE_KEY, String.valueOf(userId));
        if (Boolean.TRUE.equals(isOnline)) {
            // 在线：直接返回激活状态 1（可接单）
            return Result.success((short) 1);
        }
        // 不在线或 Redis 没查到，再查数据库 riderStatus 字段（有可能是未注册/离线/未激活）
        Rider rider = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND))
                .getRider();
        return Result.success(rider.getRiderStatus());
    }

    @Override
    @Transactional
    public void changeStatus(Long userId, Short newStatus) {
        Rider rider = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND))
                .getRider();
        rider.setRiderStatus(newStatus);

        String userIdStr = String.valueOf(userId);
        if (newStatus == 1) {
            // 上线，加入在线集合
            stringRedisTemplate.opsForSet().add(RIDER_ONLINE_KEY, userIdStr);
        } else {
            // 下线，移除在线集合、移除GEO位置
            stringRedisTemplate.opsForSet().remove(RIDER_ONLINE_KEY, userIdStr);
            stringRedisTemplate.opsForGeo().remove(RIDER_LOCATION_KEY, userIdStr);
        }
    }

    @Override
    public void updateLocation(Long userId, RiderLocationDTO dto) {
        String userIdStr = String.valueOf(userId);

        // 1. 先查 Redis
        Boolean isOnline = stringRedisTemplate.opsForSet().isMember(RIDER_ONLINE_KEY, userIdStr);
        if (!Boolean.TRUE.equals(isOnline)) {
            // 2. Redis 不在，兜底查库，防止 Redis 异常或短暂失步
            Rider rider = userRepository.findByUserId(userId)
                    .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND))
                    .getRider();
            if (rider.getRiderStatus() != 1) {
                throw new IllegalStateException("当前不是激活状态，无法上报位置");
            }
            // 补充 Redis 在线集合，保证后续高并发效率
            stringRedisTemplate.opsForSet().add(RIDER_ONLINE_KEY, userIdStr);
        }

        double lng = dto.getLongitude();
        double lat = dto.getLatitude();
        stringRedisTemplate.opsForGeo().add(RIDER_LOCATION_KEY, new Point(lng, lat), userIdStr);
        // 再幂等加一遍在线集合，保证一致性
        stringRedisTemplate.opsForSet().add(RIDER_ONLINE_KEY, userIdStr);
    }
}

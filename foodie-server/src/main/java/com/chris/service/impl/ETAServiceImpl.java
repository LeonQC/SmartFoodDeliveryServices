package com.chris.service.impl;

import com.chris.entity.AddressBook;
import com.chris.entity.Merchant;
import com.chris.exception.AddressBookNotFoundException;
import com.chris.exception.MerchantNotFoundException;
import com.chris.repository.AddressBookRepository;
import com.chris.repository.MerchantRepository;
import com.chris.service.ETAService;
import com.chris.utils.GoogleGeocodingUtil;
import com.chris.utils.GoogleMapAPIUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ETAServiceImpl implements ETAService {
    @Autowired
    private StringRedisTemplate redis;
    @Autowired
    private AddressBookRepository addressBookRepository;
    @Autowired
    private MerchantRepository merchantRepository;
    @Autowired
    private GoogleGeocodingUtil googleGeocodingUtil;
    @Autowired
    private GoogleMapAPIUtil googleMapAPIUtil;

    @Override
    public long estimateDeliveryTime(Long merchantId, Long addressId, Long userId) {

        LocalDateTime now = LocalDateTime.now(ZoneId.of("America/Chicago"));
        int dayOfWeek = now.getDayOfWeek().getValue() % 7;
        int hour = now.getHour();

        long baseClient = 5 * 60 * 1000L; // 5分钟付款时间兜底
        long baseMerchant = 20 * 60 * 1000L; // 20分钟接单+备餐时间兜底
        long baseRider = 10 * 60 * 1000L; // 10分钟等待骑手接单+取餐时间兜底

        String clientPayKey = String.format("eta:client:%s:pay", userId);
        String merchantKey = String.format("eta:merchant:%d:%d:%d:acceptPrepare", merchantId, dayOfWeek, hour);
        String waitRiderKey = String.format("eta:merchant:%d:%d:%d:waitRider", merchantId, dayOfWeek, hour);

        Long clientPay = safeGet(redis, clientPayKey, baseClient);
        Long acceptPrepare = safeGet(redis, merchantKey, baseMerchant);
        Long waitRider = safeGet(redis, waitRiderKey, baseRider);


        // 1. 查出送餐地址坐标
        AddressBook address = addressBookRepository.findById(addressId)
                .orElseThrow(() -> new AddressBookNotFoundException("地址无效"));
        String fullAddress = Stream.of(
                        address.getAddressLine1(),
                        address.getAddressLine2(),
                        address.getCity(),
                        address.getState(),
                        address.getZipcode(),
                        address.getCountry()
                )
                .filter(s -> s != null && !s.isEmpty())
                .collect(Collectors.joining(", "));
        Optional<double[]> latLng = googleGeocodingUtil.fetchLatLng(fullAddress);
        double[] coords = latLng.orElseThrow(() -> new RuntimeException("未能根据地址获取经纬度"));
        double destLat = coords[0];
        double destLng = coords[1];

        // 2. 查出商家坐标
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new MerchantNotFoundException("商家不存在"));
        double originLat = merchant.getLatitude();
        double originLng = merchant.getLongitude();

        // 3. 先查缓存，节省请求次数
        int dispatchSeconds = 0;
        String cacheKey = String.format("google:eta:%s:%s:%s:%s",
                originLat, originLng, destLat, destLng);

        String cached = redis.opsForValue().get(cacheKey);
        if (cached != null) {
            dispatchSeconds = Integer.parseInt(cached);
        } else {
            // 4. 调用 Google Maps Directions API 获取预计送达时间（单位：分钟）
            dispatchSeconds = googleMapAPIUtil.estimateDurationSeconds(originLat, originLng, destLat, destLng, "driving")
                    .orElse(15*60);
            // 5. 写入缓存（有效期10分钟，可根据业务调整）
            redis.opsForValue().set(cacheKey, String.valueOf(dispatchSeconds), 10, TimeUnit.MINUTES);
        }

        // 5. 到达时间 = 当前时间 + ETA（毫秒转分钟/秒）
        long etaSeconds = (clientPay + acceptPrepare + waitRider) / 1000 + dispatchSeconds;

        return (long)Math.ceil((double) etaSeconds / 60);
    }

    private Long safeGet(StringRedisTemplate redis, String key, long defaultVal) {
        try {
            String val = redis.opsForValue().get(key);
            return val != null ? Long.parseLong(val) : defaultVal;
        } catch (Exception e) {
            return defaultVal;
        }
    }
}

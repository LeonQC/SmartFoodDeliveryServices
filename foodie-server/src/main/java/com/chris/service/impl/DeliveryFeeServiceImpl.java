package com.chris.service.impl;

import com.chris.entity.AddressBook;
import com.chris.entity.Merchant;
import com.chris.exception.AddressBookNotFoundException;
import com.chris.exception.MerchantNotFoundException;
import com.chris.repository.AddressBookRepository;
import com.chris.repository.MerchantRepository;
import com.chris.service.DeliveryFeeService;
import com.chris.utils.DeliveryFeeUtil;
import com.chris.utils.GeoUtil;
import com.chris.utils.GoogleGeocodingUtil;
import com.chris.vo.DeliveryFeeVO;
import com.chris.vo.resultVOs.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.chris.constant.MessageConstant.ADDRESS_NOT_FOUND;
import static com.chris.constant.MessageConstant.MERCHANT_NOT_FOUND;

@Service
public class DeliveryFeeServiceImpl implements DeliveryFeeService {
    @Autowired
    private AddressBookRepository addressBookRepository;
    @Autowired
    private GoogleGeocodingUtil googleGeocodingUtil;
    @Autowired
    private MerchantRepository merchantRepository;

    @Override
    public Result<DeliveryFeeVO> calcFee(Long merchantId, Long addressId) {
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new MerchantNotFoundException(MERCHANT_NOT_FOUND));

        AddressBook ab = addressBookRepository.findById(addressId)
                .orElseThrow(() -> new AddressBookNotFoundException(ADDRESS_NOT_FOUND));

        String fullAddress = Stream.of(
                ab.getAddressLine1(),
                ab.getAddressLine2(),
                ab.getCity(),
                ab.getState(),
                ab.getZipcode(),
                ab.getCountry()
                )
                .filter(s -> s != null && !s.isEmpty())
                .collect(Collectors.joining(", "));
        Optional<double[]> latLng = googleGeocodingUtil.fetchLatLng(fullAddress);
        double[] coords = latLng.orElseThrow(() -> new RuntimeException("未能根据地址获取经纬度"));
        Double distance = GeoUtil.distance(coords[1], coords[0], merchant.getLocation());

        BigDecimal fee = DeliveryFeeUtil.calcFee(distance);
        fee = fee.setScale(2, RoundingMode.HALF_UP);

        DeliveryFeeVO deliveryFeeVO = new DeliveryFeeVO();
        deliveryFeeVO.setDeliveryFee(fee);
        deliveryFeeVO.setDistance(distance);

        return Result.success(deliveryFeeVO);
    }
}

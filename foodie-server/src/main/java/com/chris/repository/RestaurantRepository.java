package com.chris.repository;

import com.chris.entity.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;


public interface RestaurantRepository extends JpaRepository<Merchant, Long>, JpaSpecificationExecutor<Merchant>, MerchantRepositoryCustom {

    Merchant findByMerchantId(Long merchantId);
}

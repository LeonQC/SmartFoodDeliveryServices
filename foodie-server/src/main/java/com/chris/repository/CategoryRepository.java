package com.chris.repository;

import com.chris.entity.Category;
import com.chris.entity.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByMerchant_User_UserId(Long userId);

    boolean existsByMerchantAndName(Merchant m, String name);

    long countByMerchantUserUserId(Long userId);

    long countByMerchantUserUserIdAndStatus(Long userId, short i);
}

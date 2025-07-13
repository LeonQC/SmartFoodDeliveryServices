package com.chris.repository;

import com.chris.entity.AddressBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AddressBookRepository extends JpaRepository<AddressBook, Long> {

    List<AddressBook> findAllByClient_User_UserId(Long userId);

    List<AddressBook> findAllByAddressIdInAndClient_User_UserId(List<Long> addressId, Long userId);

    boolean existsByClient_User_UserIdAndIsDefaultTrue(Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE AddressBook a SET a.isDefault = false WHERE a.client.user.userId = :userId")
    void resetDefaultByUserId(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE AddressBook a SET a.isDefault = true WHERE a.addressId = :addressId")
    void setDefaultByAddressId(@Param("addressId") Long addressId);
}

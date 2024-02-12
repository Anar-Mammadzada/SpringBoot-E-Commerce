package com.shopme.customer;

import com.shopme.common.entity.Customer;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface CustomerRepository extends CrudRepository<Customer, Integer> {

    @Query("select c from  Customer c where c.email = ?1")
    Customer findByEmail(String email);

    @Query("select c from Customer c where c.verificationCode = ?1")
    Customer findByVerificationCode(String code);

    @Query("update Customer c set c.enabled = true, c.verificationCode = null where c.id = ?1")
    @Modifying
    void enable(Integer id);
}

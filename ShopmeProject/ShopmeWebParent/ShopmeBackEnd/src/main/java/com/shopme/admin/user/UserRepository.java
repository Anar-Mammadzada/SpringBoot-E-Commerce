package com.shopme.admin.user;

import com.shopme.common.entity.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends PagingAndSortingRepository<User, Integer> {
    @Query("select u from User u where u.email = :email")
    User getUserByEmail(@Param("email") String email);

    Long countById(Integer id);

    @Query("update User u set u.enabled = ?2 where u.id = ?1")
    @Modifying
    void updateEnabledStatus(Integer id, boolean enabled);
}

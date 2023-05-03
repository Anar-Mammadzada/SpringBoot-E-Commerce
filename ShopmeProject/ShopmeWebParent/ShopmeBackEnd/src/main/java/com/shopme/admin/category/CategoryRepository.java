package com.shopme.admin.category;

import com.shopme.common.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface CategoryRepository extends PagingAndSortingRepository<Category, Integer> {

    @Query("select c from Category c where c.parent.id is null ")
     List<Category> findRootCategories(Sort sort);

    @Query("select c from Category c where c.parent.id is null ")
    Page<Category> findRootCategories(Pageable pageable);

    @Query("select c from Category c where c.name like %?1%")
    public Page<Category> search(String keyword, Pageable pageable);
     Long countById(Integer id);

     Category findByName(String name);

     Category findByAlias(String alias);

     @Query("update Category c set c.enabled=?2 where c.id=?1")
     @Modifying
     public void updateEnabledStatus(Integer id, boolean enabled);


}

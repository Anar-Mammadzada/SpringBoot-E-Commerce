package com.shopme.admin.category;

import com.shopme.common.entity.Category;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface CategoryRepository extends PagingAndSortingRepository<Category, Integer> {

    @Query("select c from Category c where c.parent.id is null ")
     List<Category> findRootCategories(Sort sort);

     Category findByName(String name);

     Category findByAlias(String alias);


}

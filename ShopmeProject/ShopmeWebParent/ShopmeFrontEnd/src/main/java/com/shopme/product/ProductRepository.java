package com.shopme.product;

import com.shopme.common.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ProductRepository extends PagingAndSortingRepository<Product, Integer> {

    @Query("select p from Product p where p.enabled = true " +
            "and (p.category.id = ?1 or p.category.allParentIDs like %?2%)" +
            "order by p.name ASC ")
    Page<Product> listByCategory(Integer categoryId, String categoryIdMatch, Pageable pageable);

    Product findByAlias(String alias);

    @Query("select p from Product p where p.enabled = true " +
            "and p.name like %?1% " +
            "or p.shortDescription like %?1% " +
            "or p.fullDescription like %?1%")
    Page<Product> search(String keyword, Pageable pageable);
}

package com.shopme.admin.brand;

import com.shopme.common.entity.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface BrandRepository extends PagingAndSortingRepository<Brand, Integer> {
     Long countById(Integer id);

     Brand findByName(String name);

     @Query("select b from Brand b where b.name like %?1%")
     Page<Brand> findAll(String keyword, Pageable pageable);

     @Query("select new Brand(b.id, b.name) from Brand b order by b.name ASC ")
     List<Brand> findAll();

}

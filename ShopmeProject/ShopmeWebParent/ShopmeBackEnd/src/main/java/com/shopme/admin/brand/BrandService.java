package com.shopme.admin.brand;

import com.shopme.common.entity.Brand;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BrandService {
    private final BrandRepository repo;

    public BrandService(BrandRepository repo) {
        this.repo = repo;
    }

    public List<Brand> listAll(){
       return  (List<Brand>) repo.findAll();
    }
}

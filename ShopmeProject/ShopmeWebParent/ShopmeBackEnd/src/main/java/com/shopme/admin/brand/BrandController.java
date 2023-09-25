package com.shopme.admin.brand;

import com.shopme.common.entity.Brand;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class BrandController {

    private final BrandService service;

    public BrandController(BrandService service) {
        this.service = service;
    }

    @GetMapping("/brands")
    public String listAll(Model model){
        List<Brand> listBrands = service.listAll();
        model.addAttribute("listBrands", listBrands);
        return "brands/brands";
    }
}

package com.shopme.admin.brand;

import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.category.CategoryPageInfo;
import com.shopme.admin.category.CategoryService;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
public class BrandController {

    private final BrandService brandService;
    private final CategoryService categoryService;

    @Autowired
    public BrandController(BrandService brandService, CategoryService categoryService) {
        this.brandService = brandService;
        this.categoryService = categoryService;
    }

    @GetMapping("/brands")
    public String listFirstPage(Model model){
        return listByPage(1, model, "name", "asc", null);
    }

    @GetMapping("/brands/page/{pageNum}")
    public String listByPage(@PathVariable(name = "pageNum") int pageNum,
                             Model model, @Param("sortField") String sortField, @Param("sortDir") String sortDir,
                             @Param("keyword") String keyword){
       Page<Brand> page = brandService.listByPage(pageNum, sortField, sortDir, keyword);
       List<Brand> listBrands = page.getContent();

        long startCount = (pageNum - 1) * brandService.BRANDS_PER_PAGE + 1;
        long endCount = startCount + brandService.BRANDS_PER_PAGE - 1;
        if (endCount > page.getTotalElements()) {
            endCount = page.getTotalElements();
        }
        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("keyword", keyword);
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);

        model.addAttribute("listBrands", listBrands);
        model.addAttribute("reverseSortDir", reverseSortDir);
        return "brands/brands";
    }

    @GetMapping("/brands/new")
    public String newBrand(Model model){
        List<Category> listCategories = categoryService.listCategoriesUsedInForm();
        model.addAttribute("listCategories", listCategories);
        model.addAttribute("brand", new Brand());
        model.addAttribute("pageTitle", "Create New Brand");

        return "brands/brand_form";
    }

    @PostMapping("/brands/save")
    public String saveBrand(Brand brand, @RequestParam("fileImage")MultipartFile multipartFile, RedirectAttributes ra) throws IOException {
       if(!multipartFile.isEmpty()){
           String filename = StringUtils.cleanPath(multipartFile.getOriginalFilename());
           brand.setLogo(filename);
           Brand savedBrand =  brandService.save(brand);
           String uploadDir ="../brand-logos/" + savedBrand.getId();

           FileUploadUtil.cleanDir(uploadDir);
           FileUploadUtil.saveFile(uploadDir, filename, multipartFile);
       }else{
           brandService.save(brand);
       }
       ra.addFlashAttribute("message", "The brand has been saved successfully.");
       return "redirect:/brands";
    }

    @GetMapping("/brands/edit/{id}")
    public String editBrand(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes ra){
        try {
            Brand brand = brandService.get(id);
            List<Category> listCategories = categoryService.listCategoriesUsedInForm();

            model.addAttribute("brand", brand);
            model.addAttribute("listCategories", listCategories);
            model.addAttribute("pageTitle","Edit Brand(ID: " + ")");

            return "brands/brand_form";
        }catch (BrandNotFoundException ex){
            ra.addFlashAttribute("message", ex.getMessage());
            return "redirect:/brands";
        }
    }

    @GetMapping("/brands/delete/{id}")
    public String deleteBrand(@PathVariable("id") Integer id, Model model, RedirectAttributes ra){
        try {
            brandService.delete(id);
            String brandDir = "../brand-logos/" + id;
            FileUploadUtil.removeDir(brandDir);
            ra.addFlashAttribute("message", "The Brand ID " + id + " has been deleted successfully");
        }catch (BrandNotFoundException ex){
            ra.addFlashAttribute("message", ex.getMessage());
        }
        return "redirect:/brands";
    }
}

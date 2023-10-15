package com.shopme.admin.product;

import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.brand.BrandService;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Product;
import com.shopme.common.entity.ProductImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
public class ProductController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);

    private ProductService productService;

    private BrandService brandService;
    private final ProductRepository productRepository;

    @Autowired
    public ProductController(ProductService productService, BrandService brandService,
                             ProductRepository productRepository) {
        this.productService = productService;
        this.brandService = brandService;
        this.productRepository = productRepository;
    }

    @GetMapping("/products")
    public String listAll(Model model){
        List<Product> listProducts = productService.listAll();
        model.addAttribute("listProducts", listProducts);
        return "products/products";
    }

    @GetMapping("/products/new")
    public String newProduct(Model model){
        List<Brand> listBrands = brandService.listAll();
        Product product = new Product();
        product.setEnabled(true);
        product.setInStock(true);
        model.addAttribute("product", product);
        model.addAttribute("listBrands", listBrands);
        model.addAttribute("pageTitle", "Create New Product");
        return "products/product_form";
    }

    @PostMapping("/products/save")
    public String saveProduct(Product product, RedirectAttributes ra,
                         @RequestParam("fileImage") MultipartFile mainImageMultipart,
                         @RequestParam("extraImage") MultipartFile [] extraImageMultiparts,
                         @RequestParam(name = "detailIDs", required = false) String[] detailIDs,
                         @RequestParam(name = "detailNames", required = false) String[] detailNames,
                         @RequestParam(name = "detailValues", required = false) String[] detailValues,
                         @RequestParam(name = "imageIDs", required = false) String[] imageIDs,
                         @RequestParam(name = "imageNames", required = false) String[] imageNames) throws IOException {
          setMainImageName(mainImageMultipart, product);
          setExistingExtraImageNames(imageIDs, imageNames, product);
        setNewExtraImageNames(extraImageMultiparts, product);
          setProductDetails(detailIDs, detailNames, detailValues, product);

            Product savedProduct =  productService.save(product);

            saveUploadedImages(mainImageMultipart, extraImageMultiparts, savedProduct);

            deleteExtraImagesWereRemovedOnForm(product);

        ra.addFlashAttribute("message", "The product has been saved successfully.");
        return "redirect:/products";
    }

    private void deleteExtraImagesWereRemovedOnForm(Product product){
        String extraImageDir = "../product-images/" + product.getId() + "/extras";
        Path dirPath = Paths.get(extraImageDir);
        try {
            Files.list(dirPath).forEach(file ->{
                String fileName = file.toFile().getName();
                if (!product.containsImageName(fileName)){
                    try{
                        Files.delete(file);
                        LOGGER.info("Deleted extra image: " + fileName);
                    }catch (IOException ex){
                        LOGGER.error("Could not delete extra image: " + fileName);
                    }
                }

            });
        }catch (IOException e){
            LOGGER.error("Could not list directory: " + dirPath);
        }
    }

    private void setExistingExtraImageNames(String [] imageIDs, String[] imageNames, Product product){
        if (imageIDs == null || imageIDs.length == 0) return;
        Set<ProductImage> images = new HashSet<>();

        for (int count = 0; count < imageIDs.length; count ++){
            Integer id = Integer.parseInt(imageIDs[count]);
            String name = imageNames[count];
            images.add(new ProductImage(id, name, product));
        }

        product.setImages(images);

    }

    private void setProductDetails(String [] detailIDs, String [] detailNames, String [] detailValues, Product product){
        if (detailNames == null || detailNames.length == 0) return;

        for (int count =0; count < detailNames.length; count++){
            String name = detailNames[count];
            String value = detailValues[count];
            Integer id = Integer.parseInt(detailIDs[count]);
            if (id != 0){
                product.addDetail(id, name, value);
            }else if(!name.isEmpty() && !value.isEmpty()){
                product.addDetail(name, value);
            }
        }

    }

    private void saveUploadedImages(MultipartFile mainImageMultipart,
                                    MultipartFile [] extraImageMultiparts, Product savedProduct) throws IOException {
        if (!mainImageMultipart.isEmpty()) {
            String filename = StringUtils.cleanPath(mainImageMultipart.getOriginalFilename());
            String uploadDir = "../product-images/" + savedProduct.getId();
            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, filename, mainImageMultipart);
        }
        if(extraImageMultiparts.length > 0){
            String uploadDir = "../product-images/" + savedProduct.getId() + "/extras";
            for (MultipartFile multipartFile : extraImageMultiparts) {
                if (multipartFile.isEmpty()) continue;
                String filename = StringUtils.cleanPath(multipartFile.getOriginalFilename());
                FileUploadUtil.saveFile(uploadDir, filename, multipartFile);
                }
            }
    }

    private void setNewExtraImageNames(MultipartFile [] extraImageMultiparts, Product product){
        if(extraImageMultiparts.length > 0){
            for (MultipartFile multipartFile : extraImageMultiparts){
                if (!multipartFile.isEmpty()){
                    String filename = StringUtils.cleanPath(multipartFile.getOriginalFilename());
                    if (!product.containsImageName(filename)){
                        product.addExtraImage(filename);
                    }

                }
            }
        }
    }


    private void setMainImageName(MultipartFile mainImageMultipart, Product product) {
        if (!mainImageMultipart.isEmpty()) {
            String filename = StringUtils.cleanPath(mainImageMultipart.getOriginalFilename());
            product.setMainImage(filename);
        }
    }

    @GetMapping("/products/{id}/enabled/{status}")
    public String updateProductEnabledStatus(@PathVariable("id") Integer id,
                @PathVariable("status") boolean enabled, RedirectAttributes ra){
        productService.updateProductEnabledStatus(id, enabled);
        String status = enabled ? "enabled" : "disabled";
        String message = "The product ID " + id + " has been " + status;
        ra.addFlashAttribute("message", message);

        return "redirect:/products";

    }

    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable("id") Integer id, Model model, RedirectAttributes ra){
        try {
            productService.delete(id);
            String productExtraImagesDir = "../product-images/" + id + "/extras";
            String productImagesDir = "../product-images/" + id;
            FileUploadUtil.removeDir(productExtraImagesDir);
            FileUploadUtil.removeDir(productImagesDir);
            ra.addFlashAttribute("message","The product ID " + id + " has been deleted successfully");
        } catch (ProductNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());
        }

        return "redirect:/products";

    }

    @GetMapping("/products/edit/{id}")
    public String editProduct(@PathVariable("id") Integer id, Model model, RedirectAttributes ra){
        try {
            Product product = productService.get(id);
            List<Brand> listBrands = brandService.listAll();
            Integer numberOfExistingExtraImages = product.getImages().size();
            model.addAttribute("product", product);
            model.addAttribute("listBrands", listBrands);
            model.addAttribute("pageTitle", "Edit Product (ID: " + id + ")");
            model.addAttribute("numberOfExistingExtraImages", numberOfExistingExtraImages);

            return "/products/product_form";
        } catch (ProductNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());
            return "redirect:/products";
        }

    }

    @GetMapping("/products/detail/{id}")
    public String viewProductDetails(@PathVariable("id") Integer id, Model model, RedirectAttributes ra){
        try {
            Product product = productService.get(id);
            model.addAttribute("product", product);
            return "/products/product_detail_modal";
        } catch (ProductNotFoundException e) {
            ra.addFlashAttribute("message", e.getMessage());
            return "redirect:/products";
        }

    }
}

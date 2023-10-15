package com.shopme.admin.product;

import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.Product;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.Date;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class ProductRepositoryTests {

    @Autowired
    private ProductRepository repo;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testCreateProduct(){
        Brand brand = entityManager.find(Brand.class, 37);
        Category category = entityManager.find(Category.class, 5);
        Product product = new Product();
        product.setName("Acer Aspire Desktop ");
        product.setAlias("acer_aspire_desktop");
        product.setShortDescription("Short description for Acer Aspire");
        product.setFullDescription("Full description for Acer Aspire");
        product.setBrand(brand);
        product.setCategory(category);
        product.setPrice(678);
        product.setCost(600);
        product.setEnabled(true);
        product.setInStock(true);
        product.setCreatedTime(new Date());
        product.setUpdatedTime(new Date());

        Product savedProduct = repo.save(product);

        Assertions.assertThat(savedProduct).isNotNull();
        Assertions.assertThat(savedProduct.getId()).isGreaterThan(0);
    }

    @Test
    public void testListAllProducts(){
        Iterable<Product> products = repo.findAll();
        products.forEach(System.out::println);
    }

    @Test
    public void testGetProduct(){
        Integer id = 2;
        Product product = repo.findById(id).get();
        System.out.println(product);

        Assertions.assertThat(product).isNotNull();
    }

    @Test
    public void testUpdateProduct(){
        Integer id = 1;
        Product product = repo.findById(id).get();
        product.setPrice(499);
        repo.save(product);

        Product updatedProduct = entityManager.find(Product.class, id);
        Assertions.assertThat(updatedProduct.getPrice()).isEqualTo(499);
    }

    @Test
    public void testDeleteProduct(){
        Integer id = 3;
        repo.deleteById(id);
        Optional<Product> result = repo.findById(id);
        Assertions.assertThat(!result.isPresent());
    }

    @Test
    public void testSaveProductWithImages(){
        Integer productId = 1;
        Product product = repo.findById(productId).get();

        product.setMainImage("main image.jpg");
        product.addExtraImage("extra image1.png");
        product.addExtraImage("extra image2.png");
        product.addExtraImage("extra image3.png");

        Product savedProduct = repo.save(product);

        Assertions.assertThat(savedProduct.getImages().size()).isEqualTo(3);
    }

    @Test
    public void testSaveProductWithDetails(){
        Integer productId = 1;
        Product product = repo.findById(productId).get();
        product.addDetail("Device memory", "128 GB");
        product.addDetail("CPU Model", "MediaTEK");
        product.addDetail("OS", "Android 10");

        Product savedProduct = repo.save(product);
        Assertions.assertThat(savedProduct.getDetails()).isNotEmpty();

    }

}

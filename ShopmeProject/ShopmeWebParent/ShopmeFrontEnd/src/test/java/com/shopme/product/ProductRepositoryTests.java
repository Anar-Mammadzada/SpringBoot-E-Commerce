package com.shopme.product;

import com.shopme.common.entity.Product;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ProductRepositoryTests {

    private ProductRepository repo;

    @Autowired
    public ProductRepositoryTests(ProductRepository repo) {
        this.repo = repo;
    }

    @Test
    public void testFindByAlias(){
        String alias = "canon-eos-m50";
        Product product = repo.findByAlias(alias);
        Assertions.assertThat(product).isNotNull();
    }
}

package com.shopme.category;

import com.shopme.common.entity.Category;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CategoryRepositoryTests {

    private CategoryRepository repo;

    @Autowired
    public CategoryRepositoryTests(CategoryRepository repo) {
        this.repo = repo;
    }

    @Test
    public void testListEnabledCategories(){
        List<Category> allEnabled = repo.findAllEnabled();
        allEnabled.forEach(category -> {
            System.out.println(category.getName() + " ( " + category.isEnabled() + " ) ");
        });
    }

    @Test
    public void testFindCategoryByAlias(){
        String alias = "electronics";
        Category category = repo.findByAliasEnabled(alias);

        Assertions.assertThat(category).isNotNull();
    }
}

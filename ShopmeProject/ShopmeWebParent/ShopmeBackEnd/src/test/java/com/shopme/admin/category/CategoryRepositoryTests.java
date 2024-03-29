package com.shopme.admin.category;

import com.shopme.common.entity.Category;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Set;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class CategoryRepositoryTests {

    @Autowired
    private CategoryRepository repo;

    @Test
    public void testCreateRootCategory(){
        Category category = new Category("Electronics");
        Category savedCategory = repo.save(category);
        Assertions.assertThat(savedCategory.getId()).isGreaterThan(0);

    }

    @Test
    public void testCreateSubCategory(){
        Category parent = new Category(8);
        Category subCategory = new Category("iPhone",parent);

        Category savedCategory = repo.save(subCategory);

        Assertions.assertThat(savedCategory.getId()).isGreaterThan(0);
    }

    @Test
    public void testGetCategory(){
        Category category = repo.findById(1).get();
        System.out.println(category.getName());

        Set<Category> children = category.getChildren();

        for(Category subCategory: children){
            System.out.println(subCategory.getName());
        }
        Assertions.assertThat(children.size()).isGreaterThan(0);
    }

    @Test
    public void testPrintHierarchicalCategories(){
        Iterable<Category> categories = repo.findAll();

        for(Category category: categories){
            if (category.getParent() == null){
                System.out.println(category.getName());

                Set<Category> children = category.getChildren();

                for (Category subCategory : children){
                    System.out.println("--" + subCategory.getName());
                    printChildren(subCategory, 1);
                }
            }
        }
    }

    private void printChildren(Category parent, int subLevel){
        int newSubLevel = subLevel + 1;

        Set<Category> children = parent.getChildren();

        for (Category subCategory : children){
            for (int i = 0; i < newSubLevel; i++) {
                System.out.print("--");
            }

            System.out.println(subCategory.getName());

            printChildren(subCategory, newSubLevel);
        }

    }

    @Test
    public void testListRootCategories(){
       List<Category> listRootCategories = repo.findRootCategories(Sort.by("name").ascending());
       listRootCategories.forEach(cat -> System.out.println(cat.getName()));
    }

    @Test
    public void testFindByName(){
        String name ="Computers";
        Category category = repo.findByName(name);

        Assertions.assertThat(category).isNotNull();
        Assertions.assertThat(category.getName()).isEqualTo(name);
    }

    @Test
    public void testFindByAlias(){
        String alias ="Electronics";
        Category category = repo.findByAlias(alias);

        Assertions.assertThat(category).isNotNull();
        Assertions.assertThat(category.getAlias()).isEqualTo(alias);
    }
}

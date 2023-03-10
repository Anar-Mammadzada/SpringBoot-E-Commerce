package com.shopme.admin.user;

import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

import java.util.List;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class UserRepositoryTest {

    @Autowired
    private UserRepository repo;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testCreateNewUserWithOneRole(){
        Role roleAdmin = entityManager.find(Role.class,1);
        User userAnar = new User("anarmammadza@gmail.com","anar1988","Anar","Mammadzada");
        userAnar.addRole(roleAdmin);

        User savedUser = repo.save(userAnar);

        Assertions.assertThat(savedUser.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreateNewUserWithTwoRoles(){
        User userRufat = new User("rufat@yahoo.com","rufat1991","Rufat","Aliyev");
        Role roleEditor = new Role(3);
        Role roleAssistant = new Role(5);
        userRufat.addRole(roleEditor);
        userRufat.addRole(roleAssistant);

        User savedUser = repo.save(userRufat);

        Assertions.assertThat(savedUser.getId()).isGreaterThan(0);
    }

    @Test
    public void testListAllUsers(){
       Iterable<User> listUsers =  repo.findAll();
       listUsers.forEach(user -> System.out.println(user));
    }

    @Test
    public void testGetUserById(){
       User userAnar =  repo.findById(1).get();
        System.out.println(userAnar);
       Assertions.assertThat(userAnar).isNotNull();
    }

    @Test
    public void testUpdateUserDetails(){
        User userAnar =  repo.findById(1).get();
        userAnar.setEnabled(true);
        userAnar.setEmail("arunas@gmail.com");
        repo.save(userAnar);
    }

    @Test
    public void testUpdateUserRoles(){
        User userRufat =  repo.findById(2).get();
        Role roleEditor = new Role(3);
        Role roleSalesperson = new Role(2);
        userRufat.getRoles().remove(roleEditor);
        userRufat.addRole(roleSalesperson);

        repo.save(userRufat);

    }

    @Test
    public void testDeleteUserById(){
        Integer userId = 2;
        repo.deleteById(userId);
    }

    @Test
    public void testGetUserByEmail(){
        String email = "rufat@yahoo.com";
        User user = repo.getUserByEmail(email);
        Assertions.assertThat(user).isNotNull();
    }

    @Test
    public void testCountById(){
        Integer id = 1;
        Long countById = repo.countById(id);
        Assertions.assertThat(countById).isNotNull().isGreaterThan(0);
    }

    @Test
    public void testDisabledUser(){
        Integer id = 1;
        repo.updateEnabledStatus(id,false);
    }

    @Test
    public void testEnabledUser(){
        Integer id = 1;
        repo.updateEnabledStatus(id,true);
    }

    @Test
    public void testListFirstPage(){
        int pageNumber = 0;
        int pageSize = 4;
        Pageable pageable = PageRequest.of(pageNumber,pageSize);
        Page<User> page = repo.findAll(pageable);
        List<User> listUsers = page.getContent();
        listUsers.forEach(user -> System.out.println(user));

        Assertions.assertThat(listUsers.size()).isEqualTo(pageSize);
    }

    @Test
    public void testSearchUsers(){
        String keyword = "bruce";
        int pageNumber = 0;
        int pageSize = 4;
        Pageable pageable = PageRequest.of(pageNumber,pageSize);
        Page<User> page = repo.findAll(keyword, pageable);
        List<User> listUsers = page.getContent();
        listUsers.forEach(user -> System.out.println(user));

        Assertions.assertThat(listUsers.size()).isGreaterThan(0);

    }

}

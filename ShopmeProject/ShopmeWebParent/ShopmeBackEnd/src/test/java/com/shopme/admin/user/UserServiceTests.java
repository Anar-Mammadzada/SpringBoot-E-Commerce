package com.shopme.admin.user;

import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class UserServiceTests {

    @MockBean
    private UserRepository repo;

    @MockBean
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService service;



    @Test
    public void testGetByEmail(){
        User user = new User();
        user.setEmail("anarmammadza@gmail.com");
        Mockito.when(repo.getUserByEmail("anarmammadza@gmail.com")).thenReturn(user);

        User result = service.getByEmail("anarmammadza@gmail.com");

        Assertions.assertThat("anarmammadza@gmail.com").isEqualTo(result.getEmail());
    }

    @Test
    public void testListAll(){
        User user1 = new User("ugur@gmail.com","ugur2017", "Ugur", "Mammadzada");
        User user2 = new User("sanan@gmail.com","sanan1991", "Sanan", "Mammadzada");
        User user3 = new User("ayaz@gmail.com","ayaz1988", "Ayaz", "Quliyev");

        List<User> mockUsers = new ArrayList<>();
        mockUsers.add(user1);
        mockUsers.add(user2);
        mockUsers.add(user3);

        Mockito.when(repo.findAll(Sort.by("firstName").ascending())).thenReturn(mockUsers);

        List<User> result = service.listAll();

        Assertions.assertThat(mockUsers).isEqualTo(result);
    }

    @Test
    public void testListByPage(){
        int pageNum = 1;
        String sortField = "firstName";
        String sortDir = "asc";
        String keyword = "test";

        User user1 = new User("ugur@gmail.com","ugur2017", "Ugur", "Mammadzada");
        User user2 = new User("sanan@gmail.com","sanan1991", "Sanan", "Mammadzada");
        User user3 = new User("ayaz@gmail.com","ayaz1988", "Ayaz", "Quliyev");

        List<User> mockUsers = new ArrayList<>();
        mockUsers.add(user1);
        mockUsers.add(user2);
        mockUsers.add(user3);

        Page<User> mockPage = new PageImpl<>(mockUsers);

        Mockito.when(repo.findAll("test", PageRequest.of(pageNum - 1, UserService.USERS_PER_PAGE, Sort.by(sortField).ascending())))
                .thenReturn(mockPage);

        Page<User> result =  service.listByPage(pageNum, sortField, sortDir, keyword);

        Assertions.assertThat(mockPage).isEqualTo(result);
    }

    @Test
    public void testListRoles(){

        List<Role> mockRoles = new ArrayList<>();
         Role role = new Role();
         role.setName("Admin");
         role.setName("User");
         role.setName("Editor");
         role.setName("Salesperson");
         mockRoles.addAll(List.of(role));

         Mockito.when(roleRepository.findAll()).thenReturn(mockRoles);

         List<Role> result =  service.listRoles();

         Assertions.assertThat(mockRoles).isEqualTo(result);

    }

    @Test
    public void testSaveUser(){
        User mockUser = new User();
        mockUser.setId(1);
        mockUser.setEmail("ugur@gmail.com");
        mockUser.setPassword("ugur2017");
        mockUser.setFirstName("Ugur");
        mockUser.setLastName("Mammadzada");

        Mockito.when(repo.save(any(User.class))).thenReturn(mockUser);
        Mockito.when(repo.findById(1)).thenReturn(Optional.of(mockUser));

        Mockito.when(passwordEncoder.encode("ugur2017")).thenReturn("ugur2017");


        User savedUser = service.save(mockUser);

        verify(repo, times(1)).save(mockUser);

        verify(passwordEncoder, times(1)).encode("ugur2017");

       Assertions.assertThat("ugur2017").isEqualTo(savedUser.getPassword());

    }

    @Test
    public void testUpdateAccount(){
        User userInForm = new User();
        userInForm.setId(1);
        userInForm.setEmail("arunas@gmail.com");
        userInForm.setPassword("arunas1988");
        userInForm.setFirstName("Arunas");
        userInForm.setLastName("Mammadzada");

        User userInDB = new User();
        userInDB.setId(1);
        userInDB.setEmail("ugur@gmail.com");
        userInDB.setPassword("ugur2017");
        userInDB.setFirstName("Ugur");
        userInDB.setLastName("Mammad");

        Mockito.when(passwordEncoder.encode("arunas1988")).thenReturn("arunas1988");

        Mockito.when(repo.findById(1)).thenReturn(Optional.of(userInDB));
        Mockito.when(repo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));


        User result = service.updateAccount(userInForm);

        Assertions.assertThat("arunas1988").isEqualTo(result.getPassword());
        Assertions.assertThat("Arunas").isEqualTo(result.getFirstName());
        Assertions.assertThat("Mammadzada").isEqualTo(result.getLastName());

    }

    @Test
    public void testIsEmailUniqueCreatingNewUser(){
        Mockito.when(repo.getUserByEmail("arunasma@gmail.com")).thenReturn(null);

        boolean result = service.isEmailUnique(null, "arunasma@gmail.com");

        Assertions.assertThat(result).isTrue();
    }

    @Test
    public void testIsEmailUniqueUpdateUserWithTheSameEmail(){
        User mockUser = new User();
        mockUser.setId(1);
        mockUser.setEmail("arunas@gmail.com");
        mockUser.setPassword("arunas1988");
        mockUser.setFirstName("Arunas");
        mockUser.setLastName("Mammadzada");

        User existingUser = new User();
        existingUser.setId(1);
        existingUser.setEmail("arunas@gmail.com");

        Mockito.when(repo.getUserByEmail("arunas@gmail.com")).thenReturn(existingUser);

        boolean result = service.isEmailUnique(1, "arunas@gmail.com");

        Assertions.assertThat(result).isTrue();

    }

    @Test
    public void testGetUserWhenUserExists() throws UserNotFoundException {
        User mockUser = new User();
        mockUser.setId(1);
        mockUser.setEmail("arunas@gmail.com");
        mockUser.setPassword("arunas1988");
        mockUser.setFirstName("Arunas");
        mockUser.setLastName("Mammadzada");

        Mockito.when(repo.findById(1)).thenReturn(Optional.of(mockUser));

        User result = service.get(1);

        Assertions.assertThat(mockUser).isEqualTo(result);
    }

    @Test
    public void testGetUserWhenUserDoesntExist(){
        Mockito.when(repo.findById(1)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> service.get(1));
    }

    @Test
    public void testDeleteUserWhenUserExists(){
        Mockito.when(repo.countById(1)).thenReturn(1L);

        assertDoesNotThrow(() -> service.delete(1));

    }

    @Test
    public void testDeleteUserWhenUserDoesntExist(){

        Mockito.when(repo.countById(1)).thenReturn(0L);

        assertThrows(UserNotFoundException.class, () -> service.delete(1));
    }

}

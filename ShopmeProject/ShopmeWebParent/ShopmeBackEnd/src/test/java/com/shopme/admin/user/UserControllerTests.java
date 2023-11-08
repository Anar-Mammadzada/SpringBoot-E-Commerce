package com.shopme.admin.user;

import com.shopme.admin.user.controller.UserController;
import com.shopme.common.entity.User;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService service;

    @MockBean
    private UserRepository repo;

    @Test
    public void testExportToExcel() throws Exception {
        List<User> listUsers = new ArrayList<>();
        listUsers.add(new User("anar@gmail.com", "arunas", "Anar", "Mammadzada"));
        listUsers.add(new User("ugur@gmail.com", "uguras", "Ugur", "Mammadzada"));
        listUsers.add(new User("sanan@gmail.com", "sananas", "Sanan", "Mammadzada"));
        listUsers.add(new User("ayaz@gmail.com", "ayazas", "Ayaz", "Quliyev"));

        Mockito.when(service.listAll()).thenReturn(listUsers);

        mockMvc.perform(get("/users/export/excel")).andExpect(status().isOk());

    }

    @Test
    public void testDeleteUserExists() throws Exception {
        // User ID that exists
        int userId = 1;


        doNothing().when(service).delete(userId);

        mockMvc.perform(get("/users/delete/{id}", userId))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("message", "The user ID " + userId + " has been deleted successfully"));
    }

    @Test
    public void testDeleteUserNotFound() throws Exception {

        int userId = 536;

        doThrow(new UserNotFoundException("Could not find any user with ID " + userId)).when(service).delete(userId);

        mockMvc.perform(get("/users/delete/{id}", userId))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("message", "Could not find any user with ID " + userId));
    }
}

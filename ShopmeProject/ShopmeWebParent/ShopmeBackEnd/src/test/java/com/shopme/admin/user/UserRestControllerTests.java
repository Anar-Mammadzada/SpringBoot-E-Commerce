package com.shopme.admin.user;

import com.shopme.admin.user.controller.UserRestController;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserRestController.class)
public class UserRestControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService service;

    @MockBean
    private UserRepository repo;

    @Test
    public void testCheckDuplicateEmailResponse() throws Exception {
        Integer userId = 1;
        String email = "ugur@gmail.com";
        Mockito.when(service.isEmailUnique(userId, email)).thenReturn(false);

        mockMvc.perform(post("/users/check_email")
                .param("id", "1")
                .param("email", email)
                .with(csrf())).andExpect(status().isOk());
    }
}

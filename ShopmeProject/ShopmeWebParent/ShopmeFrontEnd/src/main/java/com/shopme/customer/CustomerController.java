package com.shopme.customer;

import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class CustomerController {

    private CustomerService service;

    @Autowired
    public CustomerController(CustomerService service) {
        this.service = service;
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model){
        List<Country> listCountries = service.listAllCountries();
        model.addAttribute("pageTitle","Customer Registration");
        model.addAttribute("customer",new Customer());
        model.addAttribute("listCountries",listCountries);
        return "register/register_form";
    }
}

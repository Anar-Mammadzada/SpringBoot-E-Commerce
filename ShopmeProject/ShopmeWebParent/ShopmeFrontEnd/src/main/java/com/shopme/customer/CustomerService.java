package com.shopme.customer;

import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;
import com.shopme.setting.CountryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private CountryRepository countryRepo;

    private CustomerRepository customerRepo;

    @Autowired
    public CustomerService(CountryRepository countryRepo, CustomerRepository customerRepo) {
        this.countryRepo = countryRepo;
        this.customerRepo = customerRepo;
    }

    public List<Country> listAllCountries(){
        return countryRepo.findAllByOrderByNameAsc();
    }

    public boolean isEmailUnique(String email){
        Customer customer = customerRepo.findByEmail(email);
        return customer == null;
    }
}
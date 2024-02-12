package com.shopme.customer;

import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;
import com.shopme.setting.CountryRepository;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class CustomerService {

    private CountryRepository countryRepo;

    private CustomerRepository customerRepo;

    private PasswordEncoder passwordEncoder;

    @Autowired
    public CustomerService(CountryRepository countryRepo, CustomerRepository customerRepo, PasswordEncoder passwordEncoder) {
        this.countryRepo = countryRepo;
        this.customerRepo = customerRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Country> listAllCountries(){
        return countryRepo.findAllByOrderByNameAsc();
    }

    public boolean isEmailUnique(String email){
        Customer customer = customerRepo.findByEmail(email);
        return customer == null;
    }

    public void registerCustomer(Customer customer){
        encodePassword(customer);
        customer.setEnabled(false);
        customer.setCreatedTime(new Date());

        String randomCode = RandomString.make(64);
        customer.setVerificationCode(randomCode);

        customerRepo.save(customer);
    }

    private void encodePassword(Customer customer) {
        String encodedPassword = passwordEncoder.encode(customer.getPassword());
        customer.setPassword(encodedPassword);
    }

    public boolean verify(String verificationCode){
        Customer customer = customerRepo.findByVerificationCode(verificationCode);
        if (customer == null || customer.isEnabled()){
            return false;
        }else {
            customerRepo.enable(customer.getId());
            return true;
        }
    }

}

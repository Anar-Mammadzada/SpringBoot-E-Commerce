package com.shopme.customer;

import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;
import org.assertj.core.api.Assert;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.saml2.Saml2RelyingPartyProperties;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.Date;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class CustomerRepositoryTests {

    @Autowired
    private CustomerRepository repo;
    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testCreateCustomer1(){
        Integer countryId = 234;

        Country country = entityManager.find(Country.class, countryId);

        Customer customer = new Customer();
        customer.setCountry(country);
        customer.setFirstName("Ugur");
        customer.setLastName("Mammadzada");
        customer.setPassword("password123");
        customer.setEmail("ugur@gmail.com");
        customer.setPhoneNumber("051-318-18-50");
        customer.setAddressLine1("53 Siyazan");
        customer.setCity("Siyazan");
        customer.setState("Quba-Xachmaz");
        customer.setPostalCode("5300");
        customer.setCreatedTime(new Date());
        Customer savedCustomer = repo.save(customer);

        Assertions.assertThat(savedCustomer).isNotNull();
        Assertions.assertThat(savedCustomer.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreateCustomer2(){
        Integer countryId = 106;

        Country country = entityManager.find(Country.class, countryId);

        Customer customer = new Customer();
        customer.setCountry(country);
        customer.setFirstName("Ayaz");
        customer.setLastName("Guliyev");
        customer.setPassword("password1988");
        customer.setEmail("ayaz@gmail.com");
        customer.setPhoneNumber("055-389-87-07");
        customer.setAddressLine1("53 Delhi");
        customer.setCity("Punjab");
        customer.setState("New Delhi");
        customer.setPostalCode("9700");
        customer.setCreatedTime(new Date());
        Customer savedCustomer = repo.save(customer);

        Assertions.assertThat(savedCustomer).isNotNull();
        Assertions.assertThat(savedCustomer.getId()).isGreaterThan(0);
    }

    @Test
    public void testListCustomers(){
        Iterable<Customer> customers = repo.findAll();
        customers.forEach(System.out::println);

        Assertions.assertThat(customers).hasSizeGreaterThan(1);
    }

    @Test
    public void testUpdateCustomer(){
        Integer customerId = 1;
        String lastname = "Hasanov";

        Customer customer = repo.findById(customerId).get();
        customer.setLastName(lastname);
        customer.setEnabled(true);

        Customer updatedCustomer = repo.save(customer);
        Assertions.assertThat(updatedCustomer.getLastName()).isEqualTo(lastname);
    }

    @Test
    public void testGetCustomer(){
        Integer id = 2;
        Optional<Customer> findById = repo.findById(id);

        Assertions.assertThat(findById).isPresent();
        Customer customer = findById.get();
        System.out.println(customer);
    }

    @Test
    public void testDeleteCustomer(){
        Integer id = 2;
        repo.deleteById(id);

        Optional<Customer> findById = repo.findById(id);

        Assertions.assertThat(findById).isNotPresent();
    }

    @Test
    public void testFindByEmail(){
        String email = "ugur@gmail.com";

        Customer byEmail = repo.findByEmail(email);
        Assertions.assertThat(byEmail).isNotNull();

        System.out.println(byEmail);
    }

    @Test
    public void findByVerificationCode(){
        String code = "code_123";

        Customer byVerificationCode = repo.findByVerificationCode(code);

        Assertions.assertThat(byVerificationCode).isNotNull();

        System.out.println(byVerificationCode);
    }

    @Test
    public void enableCustomer(){
        Integer id = 1;

        repo.enable(id);

        Customer customer = repo.findById(id).get();

        Assertions.assertThat(customer.isEnabled()).isTrue();
    }

}

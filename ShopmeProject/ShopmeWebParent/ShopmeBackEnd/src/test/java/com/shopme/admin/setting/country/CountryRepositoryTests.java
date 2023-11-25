package com.shopme.admin.setting.country;

import com.shopme.common.entity.Country;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class CountryRepositoryTests {

    private CountryRepository repo;

    @Autowired
    public CountryRepositoryTests(CountryRepository repo) {
        this.repo = repo;
    }

    @Test
    public void testCreateCountry(){
        Country country = repo.save(new Country("Azerbaijan", "AZE"));
        Assertions.assertThat(country).isNotNull();
        Assertions.assertThat(country.getId()).isGreaterThan(0);
    }

    @Test
    public void testListCountries(){
        List<Country> listCountries = repo.findAllByOrderByNameAsc();
        listCountries.forEach(System.out :: println);
        Assertions.assertThat(listCountries.size()).isGreaterThan(0);
    }

    @Test
    public void updateCountry(){
        Integer id = 1;
        String name = "Azerbaijan";
        Country country = repo.findById(id).get();
        country.setName(name);

        Country updatedCountry = repo.save(country);

        Assertions.assertThat(updatedCountry.getName()).isEqualTo(name);
    }

    @Test
    public void testGet(){
        Integer id = 1;
        Country country = repo.findById(id).get();
        Assertions.assertThat(country).isNotNull();
    }

    @Test
    public void testDeleteCountry(){
        Integer id = 1;
        repo.deleteById(id);
        Optional<Country> findById = repo.findById(id);
        Assertions.assertThat(findById.isEmpty());
    }
}

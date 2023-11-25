package com.shopme.admin.setting.state;

import com.shopme.common.entity.Country;
import com.shopme.common.entity.State;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class StateRepositoryTests {

    private StateRepository repo;
    private TestEntityManager entityManager;

    @Autowired
    public StateRepositoryTests(StateRepository repo, TestEntityManager entityManager) {
        this.repo = repo;
        this.entityManager = entityManager;
    }


    @Test
    public void testCreateStatesInAzerbaijan(){
        Integer countryId = 2;
        Country country = entityManager.find(Country.class, countryId);

        State state = new State("Siyazan", country);

        State savedState = repo.save(state);
        Assertions.assertThat(savedState).isNotNull();
        Assertions.assertThat(savedState.getId()).isGreaterThan(0);
    }

    @Test
    public  void testListStatesByCountry(){
        Integer countryId = 2;
        Country country = entityManager.find(Country.class, countryId);
        List<State> states = repo.findByCountryOrderByNameAsc(country);
        states.forEach(System.out :: println);

        Assertions.assertThat(states.size()).isGreaterThan(0);

    }

    @Test
    public void testUpdateState(){
        Integer stateId = 1;
        String stateName = "Qusar";
        State state = repo.findById(stateId).get();

        state.setName(stateName);
        State updatedState = repo.save(state);

        Assertions.assertThat(updatedState.getName()).isEqualTo(stateName);

    }

    @Test
    public void testGetState(){
        Integer stateId = 1;
        Optional<State> findById = repo.findById(stateId);

        Assertions.assertThat(findById.isPresent());
    }

    @Test
    public void testDeleteState(){
        Integer stateId = 1;
        repo.deleteById(stateId);

        Optional<State> findById = repo.findById(stateId);

        Assertions.assertThat(findById.isEmpty());
    }
}

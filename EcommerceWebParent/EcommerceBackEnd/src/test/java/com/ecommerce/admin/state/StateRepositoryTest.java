package com.ecommerce.admin.state;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.ecommerce.common.entity.Country;
import com.ecommerce.common.entity.State;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(true)
public class StateRepositoryTest {

	@Autowired
	private StateRepository repo;

	@Autowired
	private TestEntityManager entityManager;

	@Test
	public void testCreateState() {
		Country us = entityManager.find(Country.class, 1);
		Country vietnam = entityManager.find(Country.class, 2);

		State newyork = new State("New York", us);
		State penn = new State("Pennsylvania", us);

		State hanoi = new State("Hanoi", vietnam);
		State hcm = new State("Ho Chi Minh City", vietnam);

		repo.saveAll(List.of(newyork, penn, hanoi, hcm));
	}

	@Test
	public void testListAll() {
		Country us = entityManager.find(Country.class, 1);
		List<State> states = repo.findByCountryOrderByNameAsc(us);

		states.forEach(System.out::println);
		assertThat(states.size()).isGreaterThan(0);
	}
}

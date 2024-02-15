package com.ecommerce.admin.country;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.ecommerce.common.entity.Country;
import com.ecommerce.common.entity.Currency;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(true)
public class CountryRepositoryTest {

	@Autowired
	CountryRepository repo;

	@Test
	public void testCreateCountry() {
		Country us = new Country("America", "us");
		Country vietnam = new Country("Vietnam", "vn");

		repo.saveAll(List.of(us, vietnam));
	}

	@Test
	public void testListAll() {
		List<Country> countries = repo.findAllByOrderByNameAsc();
		
		countries.forEach(System.out::println);
		assertThat(countries.size()).isGreaterThan(0);
	}
}

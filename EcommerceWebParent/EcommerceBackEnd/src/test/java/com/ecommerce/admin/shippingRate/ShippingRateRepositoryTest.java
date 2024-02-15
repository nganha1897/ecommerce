package com.ecommerce.admin.shippingRate;

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
import com.ecommerce.common.entity.ShippingRate;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(true)
public class ShippingRateRepositoryTest {
	@Autowired
	private ShippingRateRepository repo;

	@Autowired
	private TestEntityManager entityManager;
	
	@Test
	public void testCreateShippingRate() {
		Integer countryId = 242;
		Country country = entityManager.find(Country.class, countryId);
		ShippingRate shippingRate = new ShippingRate();
		
		shippingRate.setRate(3.0f);
		shippingRate.setDays(21);
		shippingRate.setCodSupported(true);
		shippingRate.setCountry(country);
		shippingRate.setState("Hanoi");
		
		repo.save(shippingRate);
		assertThat(shippingRate.getId()).isGreaterThan(0);
	}
	
	@Test 
	public void testFindByCountryAndState() {
		Integer countryId = 242;
		ShippingRate shippingRates = repo.findByCountryAndState(countryId, "Hanoi");
		assertThat(shippingRates).isNotNull();
 	}
	
	@Test
	public void testUpdateCODSupport() {
		repo.updateCODSupport(1, true);
		ShippingRate sr = repo.findById(1).get();
		assertThat(sr.isCodSupported()).isTrue();
	}
}

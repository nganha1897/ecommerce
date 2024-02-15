package com.ecommerce.shipping;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.ecommerce.common.entity.Country;
import com.ecommerce.common.entity.ShippingRate;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class ShippingRateRepositoryTest {

	@Autowired
	private ShippingRateRepository repo;
	
	@Test
	public void testFindByCountryAndState() {
		ShippingRate rate = repo.findByCountryAndState(new Country(234), "New York");
		assertThat(rate).isNotNull();
		System.out.println(rate);
	}
}

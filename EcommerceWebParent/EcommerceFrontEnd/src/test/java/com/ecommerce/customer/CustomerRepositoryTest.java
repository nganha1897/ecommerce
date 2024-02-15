package com.ecommerce.customer;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.ecommerce.common.entity.AuthenticationType;
import com.ecommerce.common.entity.Country;
import com.ecommerce.common.entity.Customer;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(true)
public class CustomerRepositoryTest {

	@Autowired
	private CustomerRepository repo;

	@Autowired
	private TestEntityManager entityManager;

	@Test
	public void testCreateCustomer() {
		Integer countryId = 234;
		Country us = entityManager.find(Country.class, countryId);

		Customer c = new Customer();
		c.setEmail("fdcio@gmail.com");
		c.setPassword("abcd1234");
		c.setFirstName("Gwen");
		c.setLastName("Stacy");
		c.setPhoneNumber("125236789");
		c.setAddressLine1("ded street");
		c.setCity("New York");
		c.setState("New York");
		c.setCountry(us);
		c.setPostalCode("10110");
		c.setCreatedTime(new Date());
		// c.setVerificationCode("1634");

		Customer savedCustomer = repo.save(c);
		assertThat(savedCustomer).isNotNull();

	}

	@Test
	public void testFindByEmail() {
		String email = "fdcio@gmail.com";
		Customer c = repo.findByEmail(email);
		System.out.println(c.getId());
		assertThat(c.getId()).isGreaterThan(0);
	}

	@Test
	public void testFindByVerificationCode() {
		String code = "1234";
		Customer c = repo.findByVerificationCode(code);
		System.out.println(c.getId());
		assertThat(c.getId()).isGreaterThan(0);
	}

	@Test
	public void testEnable() {
		Integer customerId = 1;
		repo.enable(customerId);
		Customer c = repo.findById(customerId).get();
		assertThat(c.isEnabled()).isEqualTo(true);
	}

	@Test
	public void TestUpdateAuthenticationType() {
		Integer id = 1;
		repo.updateAuthenticationType(1, AuthenticationType.DATABASE);
		Customer customer = repo.findById(id).get();
		assertThat(customer.getAuthenticationType()).isEqualTo(AuthenticationType.DATABASE);
	}
}

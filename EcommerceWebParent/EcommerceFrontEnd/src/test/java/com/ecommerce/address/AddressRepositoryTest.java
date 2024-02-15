package com.ecommerce.address;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.ecommerce.common.entity.Address;
import com.ecommerce.common.entity.Country;
import com.ecommerce.common.entity.Customer;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(true)
public class AddressRepositoryTest {
	@Autowired
	private AddressRepository repo;

	@Autowired
	private TestEntityManager entityManager;
	
	@Test
	public void testAddNew() {
		Integer countryId = 234;
		Country us = entityManager.find(Country.class, countryId);
		
		Address address = new Address();
		address.setFirstName("Jst");
		address.setLastName("Nguyen");
		address.setPhoneNumber("1564789");
		address.setAddressLine1("abc");
		address.setCity("New York");
		address.setState("New York");
		address.setPostalCode("1236");
		address.setCustomer(new Customer(1));
		address.setCountry(us);
		address.setDefaultForShipping(true);
        
		repo.save(address);
		assertThat(address.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testFindByCustomer() {
		List<Address> listCustomers = repo.findByCustomer(new Customer(1));
		listCustomers.forEach(System.out::println);
		assertThat(listCustomers.size()).isGreaterThan(0);
	}
	
	@Test
	public void testFindByIdAndCustomer() {
		Address address = repo.findByIdAndCustomer(1, 44);
		System.out.println(address);
		assertThat(address.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testSetDefaultAddress() {
		Integer addressId = 5;
		repo.setDefaultAddress(addressId);
		Address address = repo.findById(addressId).get();
		assertThat(address.isDefaultForShipping()).isTrue();
	}
	
	@Test
	public void testSetNonDefaultAddress() {
		Integer addressId = 5;
		Integer customerId = 1;
		repo.setNonDefaultForOthers(addressId, customerId);
	}
	
	@Test
	public void testFindDefaultByCustomer() {
		Address address = repo.findDefaultByCustomer(5);
		assertThat(address).isNotNull();
		System.out.println(address);
	}
}

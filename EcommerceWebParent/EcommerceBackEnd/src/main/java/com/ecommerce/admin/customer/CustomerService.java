package com.ecommerce.admin.customer;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.admin.country.CountryRepository;
import com.ecommerce.admin.paging.PagingAndSortingHelper;
import com.ecommerce.common.entity.Country;
import com.ecommerce.common.entity.Customer;
import com.ecommerce.common.exception.CustomerNotFoundException;

@Service
@Transactional
public class CustomerService {
	public static final int CUSTOMERS_PER_PAGE = 10;
	@Autowired
	private CustomerRepository customerRepo;

	@Autowired
	private CountryRepository countryRepo;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public List<Customer> listAll() {
		return (List<Customer>) customerRepo.findAll(Sort.by("firstName").ascending());
	}

	public void listByPage(int pageNum, PagingAndSortingHelper helper) {
		helper.listEntities(pageNum, CUSTOMERS_PER_PAGE, customerRepo);
	}
		

	public Customer get(Integer id) throws CustomerNotFoundException {
		try {
			return customerRepo.findById(id).get();
		} catch (NoSuchElementException e) {
			throw new CustomerNotFoundException("Could not find any customer with Id " + id);
		}
	}

	public void updateCustomerEnabledStatus(Integer id, boolean enabled) {
		customerRepo.updateEnabledStatus(id, enabled);
	}

	public void delete(Integer id) throws CustomerNotFoundException {
		Long countById = customerRepo.countById(id);
		if (countById == null || countById == 0) {
			throw new CustomerNotFoundException("Could not find any customer with Id " + id);
		}
		customerRepo.deleteById(id);
	}

	public List<Country> listAllCountries() {
		return (List<Country>) countryRepo.findAllByOrderByNameAsc();
	}

	public boolean isEmailUnique(Integer id, String email) {
		Customer customerByEmail = customerRepo.findByEmail(email);

		if (customerByEmail != null && customerByEmail.getId() != id) {
			return false;
		}

		return true;
	}

	public Customer save(Customer customerInForm) {
		Customer customerInDB = customerRepo.findById(customerInForm.getId()).get();
		if (customerInForm.getPassword().isEmpty()) {
			customerInForm.setPassword(customerInDB.getPassword());
		} else {
			String encodedPassword = passwordEncoder.encode(customerInForm.getPassword());
			customerInForm.setPassword(encodedPassword);
		} 
		customerInForm.setCreatedTime(customerInDB.getCreatedTime());
		customerInForm.setEnabled(customerInDB.isEnabled());
		customerInForm.setVerificationCode(customerInDB.getVerificationCode());
		customerInForm.setAuthenticationType(customerInDB.getAuthenticationType());
		customerInForm.setResetPasswordToken(customerInDB.getResetPasswordToken());
		return customerRepo.save(customerInForm);
	}

}

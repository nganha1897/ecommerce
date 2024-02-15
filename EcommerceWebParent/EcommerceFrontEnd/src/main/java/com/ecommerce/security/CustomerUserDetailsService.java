package com.ecommerce.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.ecommerce.common.entity.Customer;
import com.ecommerce.customer.CustomerRepository;

public class CustomerUserDetailsService implements UserDetailsService {

	@Autowired
	CustomerRepository repo;
	
	@Override
	public CustomerUserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Customer customer = repo.findByEmail(email);
		if (customer != null) {
			return new CustomerUserDetails(customer);
		} 
		throw new UsernameNotFoundException("Could not find customer with email: " + email);
	}

}

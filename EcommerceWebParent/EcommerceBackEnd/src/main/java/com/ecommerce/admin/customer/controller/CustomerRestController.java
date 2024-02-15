package com.ecommerce.admin.customer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.admin.customer.CustomerService;

@RestController
public class CustomerRestController {
	@Autowired
    private CustomerService service;
	
	@PostMapping("/customers/check_email")
	public String checkDuplicateEmail(Integer id, String email) {
		return service.isEmailUnique(id, email) ? "OK" : "Duplicated";
	}
}

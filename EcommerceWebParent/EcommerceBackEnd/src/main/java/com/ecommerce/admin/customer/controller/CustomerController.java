package com.ecommerce.admin.customer.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ecommerce.admin.customer.CustomerCsvExporter;
import com.ecommerce.admin.customer.CustomerService;
import com.ecommerce.admin.paging.PagingAndSortingHelper;
import com.ecommerce.admin.paging.PagingAndSortingParam;
import com.ecommerce.common.entity.Country;
import com.ecommerce.common.entity.Customer;
import com.ecommerce.common.exception.CustomerNotFoundException;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class CustomerController {

	@Autowired
	private CustomerService service;

	@GetMapping("/customers")
	public String listFirstPage(Model model) {
		return getRedirectURL();
	}

	@GetMapping("/customers/page/{pageNum}")
	public String listByPage(
			@PagingAndSortingParam(listName = "listCustomers", moduleURL = "/customers") PagingAndSortingHelper helper,
			@PathVariable(name = "pageNum") int pageNum) {

		service.listByPage(pageNum, helper);	
		return "customers/customers";
	}

	@PostMapping("/customers/save")
	public String saveCustomer(Customer customer, RedirectAttributes redirectAttributes) {
		service.save(customer);

		redirectAttributes.addFlashAttribute("message", "The customer has been saved successfully");

		return getRedirectURL();
	}
	
	@GetMapping("/customers/edit/{id}")
	public String editBrand(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes, Model model) {
		try {
		    Customer customer = service.get(id);
		    List<Country> listCountries = service.listAllCountries();
		    model.addAttribute("customer", customer);
		    model.addAttribute("listCountries", listCountries);
		    model.addAttribute("pageTitle", "Edit Customer (Id: " + id + ")");
		    return "customers/customer_form";
		} catch (CustomerNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}
		
		return getRedirectURL();
	}

	@GetMapping("/customers/detail/{id}")
	public String viewCustomerDetails(@PathVariable("id") Integer id, Model model,
			RedirectAttributes redirectAttributes) {
		try {
			Customer customer = service.get(id);
			model.addAttribute("customer", customer);
			return "customers/customer_details";
		} catch (CustomerNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}
		return getRedirectURL();
	}

	@GetMapping("/customers/{id}/enabled/{status}")
	public String updateCustomerEnabledStatus(@PathVariable(name = "id") Integer id,
			@PathVariable("status") boolean enabled, RedirectAttributes redirectAttributes) {
		service.updateCustomerEnabledStatus(id, enabled);
		redirectAttributes.addFlashAttribute("message",
				"The customer " + id + " has been " + (enabled ? "enabled" : "disabled"));
		return getRedirectURL();
	}

	@GetMapping("/customers/delete/{id}")
	public String deleteCustomer(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes,
			Model model) {
		try {
			service.delete(id);
			redirectAttributes.addFlashAttribute("message", "The customer Id " + id + " has been deleted successfully");
		} catch (CustomerNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}

		return getRedirectURL();
	}

	@GetMapping("/customers/export/csv")
	public void exportToCSV(HttpServletResponse response) throws IOException {
		List<Customer> listCustomers = service.listAll();
		CustomerCsvExporter exporter = new CustomerCsvExporter();
		exporter.export(listCustomers, response);
	}
	
	private String getRedirectURL() {
		return "redirect:/customers/page/1?sortField=firstName&sortDir=asc";
	}
}

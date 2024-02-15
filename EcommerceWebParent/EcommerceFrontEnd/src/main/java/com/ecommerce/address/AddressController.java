package com.ecommerce.address;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ecommerce.Utility;
import com.ecommerce.common.entity.Address;
import com.ecommerce.common.entity.Country;
import com.ecommerce.common.entity.Customer;
import com.ecommerce.customer.CustomerService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class AddressController {
	@Autowired
	private AddressService addressService;

	@Autowired
	private CustomerService customerService;

	@GetMapping("/address_book")
	public String showAddressBook(Model model, HttpServletRequest request) {
		Customer customer = getAuthenticatedCustomer(request);
		List<Address> listAddresses = addressService.listAddressBook(customer);

		boolean usePrimaryAddressAsDefault = true;

		for (Address ad : listAddresses) {
			if (ad.isDefaultForShipping()) {
				usePrimaryAddressAsDefault = false;
				break;
			}
		}

		model.addAttribute("usePrimaryAddressAsDefault", usePrimaryAddressAsDefault);
		model.addAttribute("listAddresses", listAddresses);
		model.addAttribute("customer", customer);
		return "address_book/addresses";
	}

	@GetMapping("/address_book/new")
	public String newAddress(Model model) {
		List<Country> listCountries = customerService.listAllCountries();
		model.addAttribute("address", new Address());
		model.addAttribute("listCountries", listCountries);
		model.addAttribute("pageTitle", "Create New Address");

		return "address_book/address_form";
	}

	@PostMapping("/address_book/save")
	public String saveAddress(Address address, HttpServletRequest request, RedirectAttributes redirectAttributes) {
		Customer customer = getAuthenticatedCustomer(request);
		address.setDefaultForShipping(false);
		address.setCustomer(customer);
		addressService.save(address);
		
		String redirectOption = request.getParameter("redirect");
		String redirectURL = getRedirectURL();

		if ("checkout".equals(redirectOption)) {
			redirectURL += "?redirect=checkout";
		}
		
		redirectAttributes.addFlashAttribute("message", "The address has been saved successfully");
		return redirectURL;
	}

	@GetMapping("/address_book/edit/{id}")
	public String editAddress(@PathVariable(name = "id") Integer id, HttpServletRequest request,
			RedirectAttributes redirectAttributes, Model model) {
		Customer customer = getAuthenticatedCustomer(request);
		Address address = addressService.get(id, customer.getId());
		List<Country> listCountries = customerService.listAllCountries();
		model.addAttribute("address", address);
		model.addAttribute("listCountries", listCountries);
		model.addAttribute("pageTitle", "Edit Address (Id: " + id + ")");
		return "address_book/address_form";

	}

	@GetMapping("/address_book/delete/{id}")
	public String deleteAddress(@PathVariable(name = "id") Integer id, HttpServletRequest request,
			RedirectAttributes redirectAttributes, Model model) {
		Customer customer = getAuthenticatedCustomer(request);
		addressService.delete(id, customer.getId());
		redirectAttributes.addFlashAttribute("message", "The address Id " + id + " has been deleted successfully");

		return getRedirectURL();
	}

	@GetMapping("/address_book/default/{id}")
	public String setDefaultAddress(@PathVariable(name = "id") Integer id, HttpServletRequest request, Model model) {
		Customer customer = getAuthenticatedCustomer(request);
		addressService.setDefaultAddress(id, customer.getId());

		String redirectOption = request.getParameter("redirect");
		String redirectURL = getRedirectURL();

		if ("cart".equals(redirectOption)) {
			redirectURL = "redirect:/cart";
		} else if ("checkout".equals(redirectOption)) {
			redirectURL = "redirect:/checkout";
		}

		return redirectURL;
	}

	private Customer getAuthenticatedCustomer(HttpServletRequest request) {
		String email = Utility.getEmailOfAuthenticatedCustomer(request);
		return customerService.getCustomerByEmail(email);
	}

	private String getRedirectURL() {
		return "redirect:/address_book";
	}
}

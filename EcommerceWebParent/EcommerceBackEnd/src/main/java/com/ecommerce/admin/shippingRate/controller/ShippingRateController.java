package com.ecommerce.admin.shippingRate.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ecommerce.admin.customer.CustomerService;
import com.ecommerce.admin.paging.PagingAndSortingHelper;
import com.ecommerce.admin.paging.PagingAndSortingParam;
import com.ecommerce.admin.shippingRate.ShippingRateAlreadyExistsException;
import com.ecommerce.admin.shippingRate.ShippingRateNotFoundException;
import com.ecommerce.admin.shippingRate.ShippingRateService;
import com.ecommerce.common.entity.Country;
import com.ecommerce.common.entity.Customer;
import com.ecommerce.common.entity.ShippingRate;
import com.ecommerce.common.exception.CustomerNotFoundException;

@Controller
public class ShippingRateController {
    @Autowired
    private ShippingRateService shippingRateService;
    
    @Autowired
    private CustomerService customerService;
    
    @GetMapping("/shippingRates")
	public String listFirstPage() {
		return getRedirectURL();
	}
    
    @GetMapping("/shippingRates/page/{pageNum}")
	public String listByPage(
			@PagingAndSortingParam(listName = "listShippingRates", moduleURL = "/shippingRates") PagingAndSortingHelper helper,
			@PathVariable(name = "pageNum") int pageNum) {
    	shippingRateService.listByPage(pageNum, helper);
		return "shippingRates/shipping_rates";
	}
    
    @GetMapping("/shippingRates/new")
	public String newShippingRate(Model model) {
    	List<Country> listCountries = customerService.listAllCountries();
		ShippingRate sr = new ShippingRate();
		sr.setCodSupported(false);

		model.addAttribute("shippingRate", sr);
		model.addAttribute("listCountries", listCountries);
		model.addAttribute("pageTitle", "Create New Rate");

		return "shippingRates/shipping_rate_form";
	}
    
    @GetMapping("/shippingRates/{id}/enabled/{status}")
	public String updateCODSupport(@PathVariable(name = "id") Integer id,
			@PathVariable("status") boolean enabled, RedirectAttributes redirectAttributes) {
    	shippingRateService.updateCODSupport(id, enabled);
		redirectAttributes.addFlashAttribute("message",
				"The COD support of the shipping rate " + id + " has been " + (enabled ? "enabled" : "disabled"));
		return getRedirectURL();
	}
    
    @PostMapping("/shippingRates/save")
	public String saveShippingRate(ShippingRate shippingRate, RedirectAttributes redirectAttributes) {
    	try {
			shippingRateService.save(shippingRate);
			redirectAttributes.addFlashAttribute("message", "The shipping rate has been saved successfully");
		} catch (ShippingRateAlreadyExistsException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}
		return getRedirectURL();
	}
    
    @GetMapping("/shippingRates/delete/{id}")
	public String deleteShippingRate(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes,
			Model model) {
		try {
			shippingRateService.delete(id);
			redirectAttributes.addFlashAttribute("message", "The shipping rate Id " + id + " has been deleted successfully");
		} catch (ShippingRateNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}

		return getRedirectURL();
	}
    
    @GetMapping("/shippingRates/edit/{id}")
	public String editShippingRate(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes, Model model) {
		try {
		    ShippingRate shippingRate = shippingRateService.get(id);
		    List<Country> listCountries = customerService.listAllCountries();
		    model.addAttribute("shippingRate", shippingRate);
		    model.addAttribute("listCountries", listCountries);
		    model.addAttribute("pageTitle", "Edit Rate (Id: " + id + ")");
		    return "shippingRates/shipping_rate_form";
		} catch (ShippingRateNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}
		
		return getRedirectURL();
	}
    
    private String getRedirectURL() {
		return "redirect:/shippingRates/page/1?sortField=country&sortDir=asc";
	}
    
    
}

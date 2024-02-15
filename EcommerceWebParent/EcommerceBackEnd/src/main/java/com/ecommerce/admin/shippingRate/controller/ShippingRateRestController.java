package com.ecommerce.admin.shippingRate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.admin.shippingRate.ShippingRateNotFoundException;
import com.ecommerce.admin.shippingRate.ShippingRateService;

@RestController
public class ShippingRateRestController {
	@Autowired
	private ShippingRateService service;
	
	@PostMapping("/get_shipping_cost")
	public String getShippingCost(Integer productId, Integer countryId, String state) throws ShippingRateNotFoundException {
		float shippingCost = service.calculateShippingCost(productId, countryId, state);
		return String.valueOf(shippingCost);
	}
}

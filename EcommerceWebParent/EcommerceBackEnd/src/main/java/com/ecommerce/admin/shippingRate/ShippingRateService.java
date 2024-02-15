package com.ecommerce.admin.shippingRate;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.admin.paging.PagingAndSortingHelper;
import com.ecommerce.admin.product.ProductRepository;
import com.ecommerce.common.entity.ShippingRate;
import com.ecommerce.common.entity.product.Product;

@Service
@Transactional
public class ShippingRateService {
	public static final int SHIPPING_RATES_PER_PAGE = 10;
	private static final int DIM_DIVISOR = 139;
	@Autowired
	private ShippingRateRepository shippingRateRepo;
	
	@Autowired
	private ProductRepository productRepo;

	public void listByPage(int pageNum, PagingAndSortingHelper helper) {
		helper.listEntities(pageNum, SHIPPING_RATES_PER_PAGE, shippingRateRepo);
	}
	
	public void updateCODSupport(Integer id, boolean enabled) {
		shippingRateRepo.updateCODSupport(id, enabled);
	}
	
	public ShippingRate save(ShippingRate shippingRateInForm) throws ShippingRateAlreadyExistsException {
		ShippingRate shippingRateInDB = shippingRateRepo.findByCountryAndState(shippingRateInForm.getCountry().getId(), shippingRateInForm.getState());
		if (shippingRateInDB != null) {
			if (shippingRateInForm.getId() == null || shippingRateInForm.getId() != shippingRateInDB.getId()) {
				throw new ShippingRateAlreadyExistsException("There's already a rate for the destination "
						+ shippingRateInForm.getState() + ", " + shippingRateInForm.getCountry().getName());
			}
		}
		return shippingRateRepo.save(shippingRateInForm);
	}
	
	public void delete(Integer id) throws ShippingRateNotFoundException {
		Long countById = shippingRateRepo.countById(id);
		if (countById == null || countById == 0) {
			throw new ShippingRateNotFoundException("Could not find any shipping rate with Id " + id);
		}
		shippingRateRepo.deleteById(id);
	}

	public ShippingRate get(Integer id) throws ShippingRateNotFoundException {
		try {
			return shippingRateRepo.findById(id).get();
		} catch (NoSuchElementException e) {
			throw new ShippingRateNotFoundException("Could not find any shipping rate with Id " + id);
		}
	}
	
	public float calculateShippingCost(Integer productId, Integer countryId, String state) throws ShippingRateNotFoundException {
		ShippingRate shippingRate = shippingRateRepo.findByCountryAndState(countryId, state);
		
		if (shippingRate == null) {
			throw new ShippingRateNotFoundException("No shipping rate found for the given "
					+ "destination. You need to enter shipping cost manually");
		}
		
		 Product product = productRepo.findById(productId).get();
		 
		 float dimWeight = product.getLength() * product.getHeight() * product.getWidth() / DIM_DIVISOR;
		 
		 float finalWeight = product.getWeight() > dimWeight ? product.getWeight() : dimWeight;
		 
		 return finalWeight * shippingRate.getRate();
	}
}

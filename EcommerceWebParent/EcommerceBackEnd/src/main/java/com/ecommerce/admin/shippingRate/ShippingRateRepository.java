package com.ecommerce.admin.shippingRate;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.ecommerce.admin.paging.SearchRepository;
import com.ecommerce.common.entity.ShippingRate;

public interface ShippingRateRepository extends SearchRepository<ShippingRate, Integer> {

	public Long countById(Integer id);
	
	@Query("SELECT r FROM ShippingRate r WHERE r.country.id = ?1 AND r.state = ?2")
	public ShippingRate findByCountryAndState(Integer countryId, String state);
	
	@Query("SELECT r FROM ShippingRate r WHERE r.country.name LIKE %?1% OR r.state LIKE %?1%")
	public Page<ShippingRate> findAll(String keyword, Pageable pageable);
	
	@Query("UPDATE ShippingRate r SET r.codSupported = ?2 WHERE r.id = ?1")
    @Modifying
	public void updateCODSupport(Integer id, boolean enabled);
}

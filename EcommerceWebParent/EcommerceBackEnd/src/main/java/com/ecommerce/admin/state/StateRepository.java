package com.ecommerce.admin.state;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.ecommerce.common.entity.Country;
import com.ecommerce.common.entity.State;

public interface StateRepository extends PagingAndSortingRepository<State, Integer>, CrudRepository<State, Integer> {

	public List<State> findByCountryOrderByNameAsc(Country country);
}

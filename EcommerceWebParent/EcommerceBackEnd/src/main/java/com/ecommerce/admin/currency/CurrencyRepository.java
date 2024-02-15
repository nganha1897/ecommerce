package com.ecommerce.admin.currency;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.ecommerce.common.entity.Currency;

public interface CurrencyRepository extends CrudRepository<Currency, Integer>, PagingAndSortingRepository<Currency, Integer> {

	public List<Currency> findAllByOrderByNameAsc();
}

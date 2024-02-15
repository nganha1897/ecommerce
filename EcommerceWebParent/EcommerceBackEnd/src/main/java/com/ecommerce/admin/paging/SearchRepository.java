package com.ecommerce.admin.paging;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.ecommerce.common.entity.product.Product;

@NoRepositoryBean
public interface SearchRepository<T, ID> extends PagingAndSortingRepository<T, ID>, CrudRepository<T, ID> {
    public Page<T> findAll(String keyword, Pageable pageable);
}

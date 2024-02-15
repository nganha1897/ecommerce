package com.ecommerce.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ecommerce.common.entity.Customer;
import com.ecommerce.common.entity.order.Order;

public interface OrderRepository extends JpaRepository<Order, Integer>{
    
	@Query("SELECT o FROM Order o WHERE o.customer.id = ?1")
	public Page<Order> findAll(Integer customerId, Pageable pageable);
	
	@Query("SELECT o FROM Order o JOIN o.orderDetails od "
			+ "WHERE o.customer.id = ?2 AND "
			+ "od.product.name LIKE %?1% OR CAST(o.status AS string) LIKE %?1%")
	public Page<Order> findAll(String keyword, Integer customerId, Pageable pageable);
	
	public Order findByIdAndCustomer(Integer orderId, Customer customer);
}

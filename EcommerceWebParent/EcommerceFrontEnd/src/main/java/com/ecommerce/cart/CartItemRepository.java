package com.ecommerce.cart;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.ecommerce.common.entity.CartItem;
import com.ecommerce.common.entity.Customer;
import com.ecommerce.common.entity.product.Product;

public interface CartItemRepository extends PagingAndSortingRepository<CartItem, Integer>, CrudRepository<CartItem, Integer> {
    
	public List<CartItem> findByCustomer(Customer customer);
	
	public CartItem findByCustomerAndProduct(Customer customer, Product product);
	
	@Modifying
	@Query("UPDATE CartItem i SET i.quantity = ?1 WHERE i.customer.id = ?2 AND i.product.id = ?3")
	public void updateQuantity(Integer quantity, Integer customerId, Integer productId);
	
	@Modifying
	@Query("DELETE FROM CartItem i WHERE i.customer.id = ?1 AND i.product.id = ?2")
	public void deleteByCustomerAndProduct(Integer customerId, Integer productId);
	
	
	@Modifying
	@Query("DELETE CartItem c WHERE c.customer.id = ?1")
	public void deleteByCustomer(Integer customerId);
	
}

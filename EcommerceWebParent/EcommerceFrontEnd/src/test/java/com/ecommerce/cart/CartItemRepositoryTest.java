package com.ecommerce.cart;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.ecommerce.common.entity.CartItem;
import com.ecommerce.common.entity.Customer;
import com.ecommerce.common.entity.product.Product;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(true)
public class CartItemRepositoryTest {

	@Autowired
	private CartItemRepository repo;
	
	@Autowired
	private TestEntityManager entityManager;
	
	@Test
	public void testSaveItem() {
		Integer customerId = 10;
		Integer productId = 8;
		Customer customer = entityManager.find(Customer.class, customerId);
		Product product = entityManager.find(Product.class, productId);
		
		CartItem item = new CartItem();
		item.setCustomer(customer);
		item.setProduct(product);
		item.setQuantity(3);
		CartItem savedItem = repo.save(item);
		
		assertThat(savedItem.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testFindByCustomer() {
		Integer customerId = 10;
		Customer customer = entityManager.find(Customer.class, customerId);
		
		List<CartItem> itemList = repo.findByCustomer(customer);
		itemList.forEach(System.out::println);
		assertThat(itemList.size()).isEqualTo(2);
	}
	
	@Test
	public void testFindByCustomerAndProduct() {
		Integer customerId = 10;
		Integer productId = 8;
		Customer customer = entityManager.find(Customer.class, customerId);
		Product product = entityManager.find(Product.class, productId);
		
		CartItem item = repo.findByCustomerAndProduct(customer, product);
		System.out.println(item);
		assertThat(item).isNotNull();
	}
	
	@Test
	public void testUpdateQuantity() {
		Integer customerId = 1;
		Integer productId = 1;
		int quantity = 4;
		Customer customer = entityManager.find(Customer.class, customerId);
		Product product = entityManager.find(Product.class, productId);
		
		repo.updateQuantity(quantity, customerId, productId);
		CartItem item = repo.findByCustomerAndProduct(customer, product);
		assertThat(item.getQuantity()).isEqualTo(quantity);
		
	}
	
	@Test
	public void testDeleteByCustomerAndProduct() {
		Integer customerId = 10;
		Integer productId = 10;
		Customer customer = entityManager.find(Customer.class, customerId);
		Product product = entityManager.find(Product.class, productId);
		
		repo.deleteByCustomerAndProduct(customerId, productId);
		CartItem item = repo.findByCustomerAndProduct(customer, product);
		assertThat(item).isNull();
	}
}

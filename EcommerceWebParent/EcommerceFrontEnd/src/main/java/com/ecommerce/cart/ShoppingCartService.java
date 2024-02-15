package com.ecommerce.cart;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.common.entity.CartItem;
import com.ecommerce.common.entity.Customer;
import com.ecommerce.common.entity.product.Product;
import com.ecommerce.product.ProductRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ShoppingCartService {
	
	@Autowired
	private CartItemRepository cartItemRepo;
	
	@Autowired
	private ProductRepository productRepo;
	
    public Integer addProduct(Integer productId, Integer quantity, Customer customer) throws ShoppingCartException {
    	Integer updatedQuantity = quantity;
    	Product product = new Product(productId);
    	
    	CartItem cartItem = cartItemRepo.findByCustomerAndProduct(customer, product);
    	
    	if (cartItem != null) {
    		updatedQuantity = cartItem.getQuantity() + quantity;
    		if (updatedQuantity > 5) {
    			throw new ShoppingCartException("Could not add more than 5 items total to shopping cart!");
    		}
    	} else {
    	    cartItem = new CartItem();
    	    cartItem.setCustomer(customer);
    	    cartItem.setProduct(product);
    	}
    	cartItem.setQuantity(updatedQuantity);
    	cartItemRepo.save(cartItem);
    	
    	return updatedQuantity;
    }
    
    public List<CartItem> listCartItems(Customer customer) {
    	return cartItemRepo.findByCustomer(customer);
    }
    
    public float updateQuantity(Integer productId, Integer quantity, Customer customer) {
    	cartItemRepo.updateQuantity(quantity, customer.getId(), productId);
    	Product product = productRepo.findById(productId).get();
    	float subTotal = product.getDiscountPrice() * quantity;
    	return subTotal;
    }
    
    public void removeProduct(Integer productId, Customer customer) {
    	cartItemRepo.deleteByCustomerAndProduct(customer.getId(), productId);
    }
    
    public void deleteByCustomer(Customer customer) {
    	cartItemRepo.deleteByCustomer(customer.getId());
    }
}

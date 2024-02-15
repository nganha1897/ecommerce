package com.ecommerce.admin.order;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.ecommerce.common.entity.Customer;
import com.ecommerce.common.entity.order.Order;
import com.ecommerce.common.entity.order.OrderDetail;
import com.ecommerce.common.entity.order.OrderStatus;
import com.ecommerce.common.entity.order.OrderTrack;
import com.ecommerce.common.entity.order.PaymentMethod;
import com.ecommerce.common.entity.product.Product;

@DataJpaTest(showSql = true)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(true)
public class OrderRepositoryTest {
    @Autowired 
    private OrderRepository repo;
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Test
    public void testCreateOrder() {
    	Customer customer = entityManager.find(Customer.class, 10);
    	Product product = entityManager.find(Product.class, 20);
    	Product product2 = entityManager.find(Product.class, 40);
    	
    	Order mainOrder = new Order();
    	mainOrder.setOrderTime(new Date());
    	mainOrder.setCustomer(customer);
    	mainOrder.copyAddressFromCustomer();
    	
    	mainOrder.setShippingCost(30);
    	mainOrder.setProductCost(product.getCost() + product2.getCost());
    	mainOrder.setTax(0);
    	float subtotal = product.getPrice() + product2.getPrice();
    	mainOrder.setSubtotal(subtotal);
    	mainOrder.setTotal(subtotal + 30);
    	
    	mainOrder.setPaymentMethod(PaymentMethod.PAYPAL);
    	mainOrder.setStatus(OrderStatus.PACKAGED);
    	mainOrder.setDeliverDate(new Date());
    	mainOrder.setDeliverDays(3);
    	
    	OrderDetail orderDetail = new OrderDetail();
    	orderDetail.setProduct(product);
    	orderDetail.setOrder(mainOrder);
    	orderDetail.setProductCost(product.getCost());
    	orderDetail.setShippingCost(10);
    	orderDetail.setQuantity(1);
    	orderDetail.setSubtotal(product.getPrice());
    	orderDetail.setUnitPrice(product.getPrice());
    	
    	OrderDetail orderDetail2 = new OrderDetail();
    	orderDetail2.setProduct(product2);
    	orderDetail2.setOrder(mainOrder);
    	orderDetail2.setProductCost(product2.getCost());
    	orderDetail2.setShippingCost(20);
    	orderDetail2.setQuantity(2);
    	orderDetail2.setSubtotal(product2.getPrice() * 2);
    	orderDetail2.setUnitPrice(product2.getPrice());
    	
    	mainOrder.getOrderDetails().add(orderDetail);
    	mainOrder.getOrderDetails().add(orderDetail2);
    	
    	Order savedOrder = repo.save(mainOrder);
    	assertThat(savedOrder.getId()).isGreaterThan(0);
    }
    
    @Test
    public void testUpdateOrderTracks() {
    	Order order = repo.findById(15).get();
    	OrderTrack track = new OrderTrack();
    	track.setOrder(order);
    	track.setUpdatedTime(new Date());
    	track.setStatus(OrderStatus.DELIVERED);
    	track.setNotes(OrderStatus.DELIVERED.defaultDescription());
    	
    	List<OrderTrack> orderTracks = order.getOrderTracks();
    	orderTracks.add(track);
    	Order updatedOrder = repo.save(order);
    	
    	//assertThat(updatedOrder.getOrderTracks()).hasSize(1);
    }
    
    @Test
    public void testFindByOrderTimeBetween() throws ParseException {
    	DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
    	Date startTime = dateFormatter.parse("2021-08-01");
    	Date endTime = dateFormatter.parse("2021-08-31");
    	
    	List<Order> listOrders = repo.findByOrderTimeBetween(startTime, endTime);
    	assertThat(listOrders.size()).isGreaterThan(0);
    	
    	for (Order order : listOrders) {
    		System.out.printf("%s | %s | %.2f | %.2f | %.2f \n", 
    				order.getId(), order.getOrderTime(), order.getProductCost(),
    				order.getSubtotal(), order.getTotal());
    	}
    }
}

package com.ecommerce.order;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.ecommerce.checkout.CheckoutInfo;
import com.ecommerce.common.entity.Address;
import com.ecommerce.common.entity.CartItem;
import com.ecommerce.common.entity.Customer;
import com.ecommerce.common.entity.order.Order;
import com.ecommerce.common.entity.order.OrderDetail;
import com.ecommerce.common.entity.order.OrderStatus;
import com.ecommerce.common.entity.order.OrderTrack;
import com.ecommerce.common.entity.order.PaymentMethod;
import com.ecommerce.common.entity.product.Product;
import com.ecommerce.common.exception.OrderNotFoundException;

@Service
public class OrderService {
	public static final int ORDERS_PER_PAGE = 5;
	@Autowired
	private OrderRepository repo;

	public Order createOrder(Customer customer, Address address, List<CartItem> cartItems, PaymentMethod paymentMethod,
			CheckoutInfo checkoutInfo) {
		Order newOrder = new Order();
		newOrder.setOrderTime(new Date());
		
		addTrack(newOrder, OrderStatus.NEW);
		
		if (paymentMethod.equals(PaymentMethod.PAYPAL)) {
			newOrder.setStatus(OrderStatus.PAID);
			addTrack(newOrder, OrderStatus.PAID);
		} else {
			newOrder.setStatus(OrderStatus.NEW);
		}
		
		newOrder.setCustomer(customer);
		newOrder.setProductCost(checkoutInfo.getProductCost());
		newOrder.setSubtotal(checkoutInfo.getProductTotal());
		newOrder.setShippingCost(checkoutInfo.getShippingCostTotal());
		newOrder.setTax(0.0f);
		newOrder.setTotal(checkoutInfo.getPaymentTotal());
		newOrder.setPaymentMethod(paymentMethod);
		newOrder.setDeliverDays(checkoutInfo.getDeliverDays());
		newOrder.setDeliverDate(checkoutInfo.getDeliverDate());

		if (address == null) {
			newOrder.copyAddressFromCustomer();
		} else {
			newOrder.copyShippingAddress(address);
		}

		Set<OrderDetail> orderDetails = newOrder.getOrderDetails();
		for (CartItem item : cartItems) {
			Product product = item.getProduct();
			OrderDetail detail = new OrderDetail();
			detail.setOrder(newOrder);
			detail.setProduct(product);
			detail.setQuantity(item.getQuantity());
			detail.setUnitPrice(product.getDiscountPrice());
			detail.setProductCost(product.getCost());
			detail.setSubtotal(item.getSubTotal());
			detail.setShippingCost(item.getShippingCost());

			orderDetails.add(detail);
		}
        
		
		
		return repo.save(newOrder);
	}
	
	public void addTrack(Order order, OrderStatus status) {
		List<OrderTrack> orderTracks = order.getOrderTracks();
		OrderTrack track = new OrderTrack();
		track.setOrder(order);
		track.setUpdatedTime(order.getOrderTime());
		track.setStatus(status);
		track.setNotes(status.defaultDescription());
		orderTracks.add(track);
	}
	
	public Page<Order> listForCustomerByPage(Customer customer, int pageNum, String sortField, String sortDir, String keyword) {
		Sort sort = Sort.by(sortField);

		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

		Pageable pageable = PageRequest.of(pageNum - 1, ORDERS_PER_PAGE, sort);
		Page<Order> page = null;

		if (keyword != null) {
			page = repo.findAll(keyword, customer.getId(), pageable);
		} else {
			page = repo.findAll(customer.getId(), pageable);
		}
		
		return page;
	}
	
	public Order getOrder(Integer orderId, Customer customer) throws OrderNotFoundException {
		Order order = repo.findByIdAndCustomer(orderId, customer);
		if (order == null) {
			throw new OrderNotFoundException("Could not find any order with Id " + orderId);
		}
		return order;
	}
	
	public void setOrderReturnRequest(OrderReturnRequest request, Customer customer) throws OrderNotFoundException {
		Order order = repo.findByIdAndCustomer(request.getOrderId(), customer);
		if (order == null) {
			throw new OrderNotFoundException("Could not find any order with Id " + request.getOrderId());
		}
		
		if (order.isReturnRequested()) 
			return;
		
		OrderTrack track = new OrderTrack();
		track.setOrder(order);
		track.setUpdatedTime(new Date());
		track.setStatus(OrderStatus.RETURN_REQUESTED);
		
		String notes = "Reason: " + request.getReason();
		if (!"".equals(request.getNote())) {
			notes += ". " + request.getNote();
		}
		
		track.setNotes(notes);
		order.getOrderTracks().add(track);
		order.setStatus(OrderStatus.RETURN_REQUESTED);
		
		repo.save(order);
	}
}

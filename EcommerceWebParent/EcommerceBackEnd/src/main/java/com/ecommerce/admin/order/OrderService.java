package com.ecommerce.admin.order;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.admin.paging.PagingAndSortingHelper;
import com.ecommerce.common.entity.order.Order;
import com.ecommerce.common.entity.order.OrderStatus;
import com.ecommerce.common.entity.order.OrderTrack;
import com.ecommerce.common.exception.OrderNotFoundException;

@Service
@Transactional
public class OrderService {
	public static final int ORDERS_PER_PAGE = 10;
	@Autowired
	private OrderRepository repo;

	public void listByPage(int pageNum, PagingAndSortingHelper helper) {
        String sortField = helper.getSortField();
        String sortDir = helper.getSortDir();
        String keyword = helper.getKeyword();
        
        Sort sort = null;
        
        if ("destination".equals(sortField)) {
        	sort = Sort.by("country").and(Sort.by("state")).and(Sort.by("city"));
        } else {
        	sort = Sort.by(sortField);
        }
        
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		Pageable pageable = PageRequest.of(pageNum - 1, ORDERS_PER_PAGE, sort);

        Page<Order> page = null;
        
		if (keyword != null && !keyword.isEmpty()) {
			page = repo.findAll(keyword, pageable);
		} else {
		    page = repo.findAll(pageable);
		}
		
		helper.updateModelAttributes(pageNum, page);
	}
	
	public void delete(Integer id) throws OrderNotFoundException {
		Long countById = repo.countById(id);
		if (countById == null || countById == 0) {
			throw new OrderNotFoundException("Could not find any order with Id " + id);
		}
		repo.deleteById(id);
	}
	
	public Order get(Integer id) throws OrderNotFoundException {
		try {
			return repo.findById(id).get();
		} catch (NoSuchElementException e) {
			throw new OrderNotFoundException("Could not find any order with Id " + id);
		}
	}

	public void save(Order orderInForm) {
		Order orderInDB = repo.findById(orderInForm.getId()).get();
		orderInForm.setOrderTime(orderInDB.getOrderTime());
		orderInForm.setCustomer(orderInDB.getCustomer());
		
		repo.save(orderInForm);
	}
	
	public void updateStatus(Integer orderId, String status) {
		Order orderInDB = repo.findById(orderId).get();
		OrderStatus statusToUpdate = OrderStatus.valueOf(status);
		
		if (!orderInDB.hasStatus(statusToUpdate)) {
			List<OrderTrack> orderTracks = orderInDB.getOrderTracks();
			
			OrderTrack track = new OrderTrack();
			track.setOrder(orderInDB);
			track.setStatus(statusToUpdate);
			track.setUpdatedTime(new Date());
			track.setNotes(statusToUpdate.defaultDescription());
			
			orderTracks.add(track);
			
			orderInDB.setStatus(statusToUpdate);
			repo.save(orderInDB);
		}
	}
}

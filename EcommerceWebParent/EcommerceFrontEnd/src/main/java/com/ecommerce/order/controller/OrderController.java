package com.ecommerce.order.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ecommerce.Utility;
import com.ecommerce.common.entity.Customer;
import com.ecommerce.common.entity.order.Order;
import com.ecommerce.common.exception.OrderNotFoundException;
import com.ecommerce.customer.CustomerService;
import com.ecommerce.order.OrderService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class OrderController {
	@Autowired
	private OrderService orderService;

	@Autowired
	private CustomerService customerService;

	@GetMapping("/orders")
	public String listFirstPage() {
		return getRedirectURL();
	}

	@GetMapping("/orders/page/{pageNum}")
	public String listOrdersByPage(@PathVariable("pageNum") int pageNum, String sortField, String sortDir,
			String orderKeyword, Model model, HttpServletRequest request) {
		Customer customer = getAuthenticatedCustomer(request);
		Page<Order> listOrders = orderService.listForCustomerByPage(customer, pageNum, sortField, sortDir,
				orderKeyword);
		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

		int pageSize = listOrders.getSize();

		long startCount = (pageNum - 1) * pageSize + 1;
		long endCount = startCount + pageSize - 1;
		if (endCount > listOrders.getTotalElements()) {
			endCount = listOrders.getTotalElements();
		}

		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", listOrders.getTotalPages());
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems", listOrders.getTotalElements());

		model.addAttribute("listOrders", listOrders);
		model.addAttribute("moduleURL", "/orders");
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", reverseSortDir);
		model.addAttribute("orderKeyword", orderKeyword);
		return "order/orders_customer";
	}

	@GetMapping("/orders/detail/{id}")
	public String viewOrderDetails(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes,
			HttpServletRequest request) {
		Customer customer = getAuthenticatedCustomer(request);
		try {
			Order order = orderService.getOrder(id, customer);
			model.addAttribute("order", order);
			return "order/order_detail_modal";
		} catch (OrderNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}
		return getRedirectURL();
	}

	private String getRedirectURL() {
		return "redirect:/orders/page/1?sortField=orderTime&sortDir=desc";
	}

	private Customer getAuthenticatedCustomer(HttpServletRequest request) {
		String email = Utility.getEmailOfAuthenticatedCustomer(request);
		return customerService.getCustomerByEmail(email);
	}
}

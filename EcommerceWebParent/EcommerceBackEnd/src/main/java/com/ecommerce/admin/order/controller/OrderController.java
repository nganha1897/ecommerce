package com.ecommerce.admin.order.controller;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ecommerce.admin.customer.CustomerService;
import com.ecommerce.admin.order.OrderService;
import com.ecommerce.admin.paging.PagingAndSortingHelper;
import com.ecommerce.admin.paging.PagingAndSortingParam;
import com.ecommerce.admin.security.EcommerceUserDetails;
import com.ecommerce.admin.setting.SettingService;
import com.ecommerce.common.entity.Country;
import com.ecommerce.common.entity.order.Order;
import com.ecommerce.common.entity.order.OrderDetail;
import com.ecommerce.common.entity.order.OrderStatus;
import com.ecommerce.common.entity.order.OrderTrack;
import com.ecommerce.common.entity.product.Product;
import com.ecommerce.common.entity.setting.Setting;
import com.ecommerce.common.exception.OrderNotFoundException;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class OrderController {

	@Autowired
	private OrderService orderService;

	@Autowired
	private SettingService settingService;

	@Autowired
	private CustomerService customerService;

	@GetMapping("/orders")
	public String listFirstPage() {
		return getRedirectURL();
	}

	@GetMapping("/orders/page/{pageNum}")
	public String listByPage(
			@PagingAndSortingParam(listName = "listOrders", moduleURL = "/orders") PagingAndSortingHelper helper,
			@PathVariable(name = "pageNum") int pageNum, HttpServletRequest request,
			@AuthenticationPrincipal EcommerceUserDetails loggedUser) {
		orderService.listByPage(pageNum, helper);
		loadCurrencySettings(request);
		
		if (!loggedUser.hasRole("Admin") && !loggedUser.hasRole("Salesperson") && loggedUser.hasRole("Shipper")) {
			return "orders/order_shipper";
		}
		return "orders/orders";
	}

	@GetMapping("/orders/detail/{id}")
	public String viewOrderDetails(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes,
			HttpServletRequest request, @AuthenticationPrincipal EcommerceUserDetails loggedUser) {
		try {
			Order order = orderService.get(id);
			loadCurrencySettings(request);
			
			boolean isVisibleForAdminOrSalesperson = false;
			if (loggedUser.hasRole("Admin") || loggedUser.hasRole("Salesperson")) {
				isVisibleForAdminOrSalesperson = true;
			}
			model.addAttribute("order", order);
			model.addAttribute("isVisibleForAdminOrSalesperson", isVisibleForAdminOrSalesperson);
			return "orders/order_detail_modal";
		} catch (OrderNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}
		return getRedirectURL();
	}

	@GetMapping("/orders/delete/{id}")
	public String deleteOrder(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes,
			Model model) {
		try {
			orderService.delete(id);
			redirectAttributes.addFlashAttribute("message", "The order Id " + id + " has been deleted successfully");
		} catch (OrderNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}

		return getRedirectURL();
	}

	@GetMapping("/orders/edit/{id}")
	public String editOrder(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes,
			HttpServletRequest request) {
		try {
			Order order = orderService.get(id);
			List<Country> listCountries = customerService.listAllCountries();
			loadCurrencySettings(request);
			model.addAttribute("order", order);
			model.addAttribute("listCountries", listCountries);
			model.addAttribute("pageTitle", "Edit Order Id: " + id);

			return "orders/order_form";
		} catch (OrderNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}
		return getRedirectURL();
	}

	@PostMapping("/orders/save")
	public String saveOrder(Order order, HttpServletRequest request, RedirectAttributes ra) {
		String countryName = request.getParameter("countryName");
		order.setCountry(countryName);

		updateProductDetails(order, request);
		updateOrderTracks(order, request);

		orderService.save(order);

		ra.addFlashAttribute("message", "The order Id " + order.getId() + " has been successfully!");
		return getRedirectURL();
	}

	private void updateOrderTracks(Order order, HttpServletRequest request) {
		String[] trackIds = request.getParameterValues("trackId");
		String[] trackNotes = request.getParameterValues("trackNote");
		String[] trackStatuses = request.getParameterValues("trackStatus");
		String[] trackDates = request.getParameterValues("trackDate");
		
		List<OrderTrack> orderTracks = order.getOrderTracks();
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
		
		for(int i=0; i<trackIds.length; i++) {
			OrderTrack track = new OrderTrack();
			track.setOrder(order);
			
			Integer trackId = Integer.parseInt(trackIds[i]);
			if (trackId > 0) {
			    track.setId(trackId);	
			}
			try {
			track.setUpdatedTime(dateFormatter.parse(trackDates[i]));
			} catch (ParseException e) {
	    		e.printStackTrace();
	    	}
			
			track.setStatus(OrderStatus.valueOf(trackStatuses[i]));
			track.setNotes(trackNotes[i]);
			
			orderTracks.add(track);
		}
		
		if (trackStatuses.length > 0)
		    order.setStatus(OrderStatus.valueOf(trackStatuses[trackStatuses.length-1]));
	}

	private void updateProductDetails(Order order, HttpServletRequest request) {
		String[] detailIds = request.getParameterValues("detailId");
		String[] productIds = request.getParameterValues("productId");
		String[] productCosts = request.getParameterValues("productDetailCost");
		String[] quantities = request.getParameterValues("quantity");
		String[] productPrices = request.getParameterValues("productPrice");
		String[] productSubtotals = request.getParameterValues("productSubtotal");
		String[] productShipCosts = request.getParameterValues("shippingCost");
		Set<OrderDetail> orderDetails = order.getOrderDetails();

		for (int i = 0; i < detailIds.length; i++) {
			OrderDetail detail = new OrderDetail();
			detail.setOrder(order);
			Integer detailId = Integer.parseInt(detailIds[i]);
			if (detailId > 0) {
				detail.setId(detailId);
			}
			detail.setProduct(new Product(Integer.parseInt(productIds[i])));
			detail.setProductCost(Float.parseFloat(productCosts[i]));
			detail.setQuantity(Integer.parseInt(quantities[i]));
			detail.setShippingCost(Float.parseFloat(productShipCosts[i]));
			detail.setUnitPrice(Float.parseFloat(productPrices[i]));
			detail.setSubtotal(Float.parseFloat(productSubtotals[i]));

			orderDetails.add(detail);
		}
	}

	private void loadCurrencySettings(HttpServletRequest request) {
		List<Setting> currencySettings = settingService.getCurrencySettings();
		for (Setting setting : currencySettings) {
			request.setAttribute(setting.getKey(), setting.getValue());
		}
	}

	private String getRedirectURL() {
		return "redirect:/orders/page/1?sortField=orderTime&sortDir=desc";
	}
}

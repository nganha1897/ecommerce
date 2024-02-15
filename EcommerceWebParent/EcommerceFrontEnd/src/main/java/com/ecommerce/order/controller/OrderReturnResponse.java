package com.ecommerce.order.controller;

public class OrderReturnResponse {
    private Integer orderId;

	public OrderReturnResponse() {
	}

	public OrderReturnResponse(Integer orderId) {
		super();
		this.orderId = orderId;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}
    
    
}

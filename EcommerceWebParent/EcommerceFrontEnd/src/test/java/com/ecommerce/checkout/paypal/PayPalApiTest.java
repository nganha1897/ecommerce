package com.ecommerce.checkout.paypal;



import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class PayPalApiTest {
    private static final String BASE_URL = "https://api-m.sandbox.paypal.com";
    private static final String GET_ORDER_API = "/v2/checkout/orders/";
    private static final String CLIENT_ID = "AWT-s-UHrPUq-CV_nXmx-n56778Mn6kWaXNA-6KQTDFVBkZXhIYQwY5YIJ3X1oiFobAoHj5Cv_O4G7bc";
    private static final String CLIENT_SECRET = "EKLZC7d9G0cogGDzEV4KBHsCH_V_ifqR0PN6nkipNJ4I4-OXT1-hWtv9EEC5qTdlkxFzAyHoHZQrkEtq";
    
    @Test
    public void testGetOrderDetails() {
    	String orderId = "3L108048HN7253115";
    	String requestURL = BASE_URL + GET_ORDER_API + orderId;
    	
    	HttpHeaders headers = new HttpHeaders();
    	List<MediaType> list = new ArrayList<>();
    	headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    	headers.add("Accept-Language", "en_US");
    	headers.setBasicAuth(CLIENT_ID, CLIENT_SECRET);
    	
    	HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);
    	RestTemplate restTemplate = new RestTemplate();
    	
    	ResponseEntity<PayPalOrderResponse> response = restTemplate.exchange(requestURL, HttpMethod.GET, request, PayPalOrderResponse.class);
    	
    	PayPalOrderResponse orderResponse = response.getBody();
    	System.out.println("Order Id: " + orderResponse.getId());
    	System.out.println("Validated: " + orderResponse.validate(orderId));
     }
}



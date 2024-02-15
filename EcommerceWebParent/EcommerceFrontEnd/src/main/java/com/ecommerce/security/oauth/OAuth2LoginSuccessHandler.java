package com.ecommerce.security.oauth;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.ecommerce.common.entity.AuthenticationType;
import com.ecommerce.common.entity.Customer;
import com.ecommerce.customer.CustomerService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

	@Autowired
	private CustomerService customerService;
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws ServletException, IOException {
		CustomerOAuth2User oauth2User = (CustomerOAuth2User) authentication.getPrincipal();
		
		String name = oauth2User.getName();
		String email = oauth2User.getEmail();
		String countryCode = request.getLocale().getCountry();
		
		Customer customerByEmail = customerService.getCustomerByEmail(email);
		if (customerByEmail == null) {
			customerService.addNewCustomerUponOAuthLogin(name, email, countryCode);
		} else {
			oauth2User.setFullName(customerByEmail.getFullName());
			customerService.updateAuthenticationType(customerByEmail, AuthenticationType.GOOGLE);
		}

		super.onAuthenticationSuccess(request, response, authentication);
	}

}

package com.ecommerce.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.ecommerce.security.oauth.CustomerOAuth2UserService;
import com.ecommerce.security.oauth.OAuth2LoginSuccessHandler;

@Configuration
public class SecurityConfiguration {

	@Autowired
	private CustomerOAuth2UserService oauth2UserService;

	@Bean
	public OAuth2LoginSuccessHandler oauth2LoginHandler() {
		return new OAuth2LoginSuccessHandler();
	}

	@Bean
	public DatabaseLoginSuccessHandler databaseLoginHandler() {
		return new DatabaseLoginSuccessHandler();
	}

	@Bean
	public UserDetailsService customerUserDetailsService() {
		return new CustomerUserDetailsService();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

		authProvider.setUserDetailsService(customerUserDetailsService());
		authProvider.setPasswordEncoder(passwordEncoder());

		return authProvider;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(
				(authorize) -> authorize
						.requestMatchers("/account_details", "/update_account_details", "/cart", "/address_book/**",
								"/checkout", "/place_order", "/process_paypal_order", "/orders/**")
						.authenticated().anyRequest().permitAll())
				.formLogin(login -> login.loginPage("/login").usernameParameter("email")
						.successHandler(databaseLoginHandler()).permitAll())
				.oauth2Login(login -> login.loginPage("/login")
						.userInfoEndpoint(userInfo -> userInfo.userService(oauth2UserService))
						.successHandler(oauth2LoginHandler()))
				.logout(logout -> logout.permitAll()).rememberMe(Customizer.withDefaults())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS));
		;
		;

		http.authenticationProvider(authenticationProvider());
		return http.build();
	}

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return (web) -> web.ignoring().requestMatchers("/images/**", "/js/**", "/webjars/**");
	}

}

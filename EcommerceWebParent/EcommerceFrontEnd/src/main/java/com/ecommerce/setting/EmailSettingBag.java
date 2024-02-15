package com.ecommerce.setting;

import java.util.List;

import com.ecommerce.common.entity.setting.Setting;
import com.ecommerce.common.entity.setting.SettingBag;

public class EmailSettingBag extends SettingBag {

	public EmailSettingBag(List<Setting> listSettings) {
		super(listSettings);
	}
	
	public String getHost() {
		return super.getValue("MAIL_HOST");
	}
	
	public String getUsername() {
		return super.getValue("MAIL_USERNAME");
	}

	public int getPort() {
		return Integer.parseInt(super.getValue("MAIL_PORT"));
	}
	
	public String getFromAddress() {
		return super.getValue("MAIL_FROM");
	}
	
	public String getPassword() {
		return super.getValue("MAIL_PASSWORD");
	}
	
	public String getSenderName() {
		return super.getValue("MAIL_SENDER_NAME");
	}
	
	public String getSmtpAuth() {
		return super.getValue("SMTP_AUTH");
	}
	
	public String getSmtpSecured() {
		return super.getValue("SMTP_SECURED");
	}
	
	public String getCustomerVerifySubject() {
		return super.getValue("CUSTOMER_VERIFY_SUBJECT");
	}
	
	public String getCustomerVerifyContent() {
		return super.getValue("CUSTOMER_VERIFY_CONTENT");
	}
	
	public String getOrderConfirmationSubject() {
		return super.getValue("ORDER_CONFIRMATION_SUBJECT");
	}
	
	public String getOrderConfirmationContent() {
		return super.getValue("ORDER_CONFIRMATION_CONTENT");
	}
}

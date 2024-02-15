package com.ecommerce.admin;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import jakarta.servlet.http.HttpServletResponse;

public class AbstractExporter {
	public void setResponseHeader(HttpServletResponse response, String contentType, String extension, String entity) throws IOException {
    	DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
    	String timestamp = dateFormatter.format(new Date());
    	String fileName = entity + "_" + timestamp + extension;
    	
    	response.setContentType(contentType);
    	
    	String headerKey = "Content-Disposition";
    	String headerValue = "attachment; filename=" + fileName;
        response.setHeader(headerKey, headerValue);
	}
}

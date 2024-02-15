package com.ecommerce.admin.user.export;

import java.io.IOException;
import java.util.List;

import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.ecommerce.admin.AbstractExporter;
import com.ecommerce.common.entity.User;

import jakarta.servlet.http.HttpServletResponse;

public class UserCsvExporter extends AbstractExporter {
    public void export(List<User> listUsers, HttpServletResponse response) throws IOException {
    	super.setResponseHeader(response, "text/csv", ".csv", "users");
        
        ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), 
        		CsvPreference.STANDARD_PREFERENCE);
        
        String[] csvHeader = {"User ID", "Email", "First Name", "Last Name", "Roles", "Enabled"};
        String[] fieldMapping = {"id", "email", "firstName", "lastName", "roles", "enabled"};
        
        csvWriter.writeHeader(csvHeader);
        
        for (User u : listUsers) {
        	csvWriter.write(u, fieldMapping);
        }
        
        csvWriter.close();
    }
}

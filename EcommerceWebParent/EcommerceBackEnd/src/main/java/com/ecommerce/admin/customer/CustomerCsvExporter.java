package com.ecommerce.admin.customer;

import java.io.IOException;
import java.util.List;

import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.ecommerce.admin.AbstractExporter;
import com.ecommerce.common.entity.Customer;

import jakarta.servlet.http.HttpServletResponse;

public class CustomerCsvExporter extends AbstractExporter {
	public void export(List<Customer> listCustomers, HttpServletResponse response) throws IOException {
		super.setResponseHeader(response, "text/csv", ".csv", "customers");

		ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);

		String[] csvHeader = { "User ID", "Email", "First Name", "Last Name", "Country" };
		String[] fieldMapping = { "id", "email", "firstName", "lastName", "country" };

		csvWriter.writeHeader(csvHeader);

		for (Customer c : listCustomers) {
			csvWriter.write(c, fieldMapping);
		}

		csvWriter.close();
	}
}

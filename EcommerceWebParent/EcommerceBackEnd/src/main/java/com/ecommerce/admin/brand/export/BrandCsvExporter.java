package com.ecommerce.admin.brand.export;

import java.io.IOException;
import java.util.List;

import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.ecommerce.admin.AbstractExporter;
import com.ecommerce.common.entity.Brand;

import jakarta.servlet.http.HttpServletResponse;

public class BrandCsvExporter extends AbstractExporter {
	public void export(List<Brand> listBrands, HttpServletResponse response) throws IOException {
		super.setResponseHeader(response, "text/csv", ".csv", "brands");

		ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);

		String[] csvHeader = { "Brand ID", "Brand Name", "Categories" };
		String[] fieldMapping = { "id", "name", "categories" };

		csvWriter.writeHeader(csvHeader);

		for (Brand b : listBrands) {
			csvWriter.write(b, fieldMapping);
		}

		csvWriter.close();
	}
}

package com.ecommerce.admin.brand.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.admin.brand.BrandNotFoundException;
import com.ecommerce.admin.brand.BrandNotFoundRestException;
import com.ecommerce.admin.brand.BrandService;
import com.ecommerce.common.entity.Brand;
import com.ecommerce.common.entity.Category;

@RestController
public class BrandRestController {
	@Autowired
    private BrandService service;
	
	@PostMapping("/brands/check_unique")
	public String checkUnique(Integer id, String name) {
		return service.checkUnique(id, name);
	}
	
	@GetMapping("/brands/{id}/categories")
	public List<CategoryDTO> listCategoriesByBrand(@PathVariable(name = "id") Integer brandId) throws BrandNotFoundRestException {
		List<CategoryDTO> listCategoryDTO = new ArrayList<>();
		try {
			Brand brand = service.get(brandId);
			Set<Category> categories = brand.getCategories();
			for (Category ca : categories) {
				CategoryDTO caDTO = new CategoryDTO(ca.getId(), ca.getName());
				listCategoryDTO.add(caDTO);
			}
			return listCategoryDTO;
		} catch(BrandNotFoundException e) {
			throw new BrandNotFoundRestException();
		}
	}
}

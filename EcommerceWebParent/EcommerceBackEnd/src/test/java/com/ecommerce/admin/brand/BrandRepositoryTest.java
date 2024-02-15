package com.ecommerce.admin.brand;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.ecommerce.admin.category.CategoryRepository;
import com.ecommerce.common.entity.Brand;
import com.ecommerce.common.entity.Category;

@DataJpaTest(showSql = true)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(true)
public class BrandRepositoryTest {
	@Autowired
	private BrandRepository brandRepo;

//	@Autowired
//	private CategoryRepository catRepo;
//
//	@Test
//	public void testCreateBrand() {
//		Set<Category> categories = new HashSet<>();
//		Category memory = catRepo.findByName("Memory");
//		if (memory != null) {
//			System.out.println(memory.getId() + " " + memory.getName());
//			categories.add(memory);
//		}
//		Category hard_drive = catRepo.findByName("Internal Hard Drives");
//		if (hard_drive != null) {
//			System.out.println(hard_drive.getId() + " " + hard_drive.getName());
//			categories.add(hard_drive);
//		}
//		Brand samsung = new Brand("Samsung", categories);
//		Brand savedSamsung = brandRepo.save(samsung);
//        assertThat(savedSamsung.getId()).isGreaterThan(0);
//	}
	
	@Test 
	public void testGetBrandById() {
		Brand brand = brandRepo.findById(2).get();
		System.out.println(brand.getName());
		Set<Category> categories = brand.getCategories();
		
		for (Category c : categories) {
			System.out.println(c.getName());
		}
		
		assertThat(categories.size()).isGreaterThan(0);
	}
	
	@Test
	public void testUpdateBrand() {
		Brand brand = brandRepo.findById(3).get();
		String newName = "Samsung Electronics";
		if (brand != null) {
			brand.setName(newName);
		}
		assertThat(brand.getName()).isEqualTo(newName);
	}
	
	@Test
	public void testDeleteBrand() {
		Brand apple = brandRepo.findById(2).get();
		brandRepo.delete(apple);
		assertThat(brandRepo.existsById(2)).isEqualTo(false);
	}
	
	@Test
	public void testFindAll() {
		Iterable<Brand> brands = brandRepo.findAll();
		brands.forEach(System.out::println);
		assertThat(brands).isNotEmpty();
	}
}

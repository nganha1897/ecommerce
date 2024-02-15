package com.ecommerce.admin.category;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.ecommerce.common.entity.Category;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(true)
public class CategoryRepositoryTest {
	@Autowired
	private CategoryRepository repo;

	@Test
	public void testCreateRootCategory() {
		Category category = new Category("Electronics");
		Category savedCategory = repo.save(category);

		assertThat(savedCategory.getId()).isGreaterThan(0);
	}

	@Test
	public void testCreateSubCategory() {
		Category parent = new Category(7);
		Category memory = new Category("Iphone", parent);
		//Category smartPhones = new Category("Smartphones", parent);
		//repo.saveAll(List.of(cameras, smartPhones));
		Category savedCategory = repo.save(memory);

		assertThat(savedCategory.getId()).isGreaterThan(0);
	}
	
	
	@Test
	public void testGetCategory() {
		Category category = repo.findById(2).get();
		System.out.println(category.getName());
		Set<Category> children = category.getChildren();
		
		for (Category c : children) {
			System.out.println(c.getName());
		}
		
		assertThat(children.size()).isGreaterThan(0);
	}
	
	@Test
	public void testPrintHierarchicalCategories() {
		Iterable<Category> categories = repo.findAll();
		
		for (Category ca : categories) {
			if (ca.getParent() == null) {
				System.out.println(ca.getName());
			
			    for (Category sub : ca.getChildren()) 
				    printChildren(sub, 1);
			}
		}
	}
	
	private void printChildren(Category category, int level) {
		for (int i=0; i<level; i++) {
			System.out.print("--");
		}
		System.out.println(category.getName());
		for (Category subCategory : category.getChildren()) {
			printChildren(subCategory, level + 1);
		}
	}
	
	@Test
	public void testFindByName() {
		String name = "Computers";
		Category category = repo.findByName(name);
		assertThat(category).isNotNull();
		assertThat(category.getName()).isEqualTo(name);
	}
	
	@Test
	public void testFindByAlias() {
		String alias = "book2";
		Category category = repo.findByAlias(alias);
		assertThat(category).isNotNull();
		assertThat(category.getAlias()).isEqualTo(alias);
	}
 }

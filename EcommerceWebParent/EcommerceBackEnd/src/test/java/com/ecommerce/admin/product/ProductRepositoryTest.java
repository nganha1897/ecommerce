package com.ecommerce.admin.product;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.ecommerce.common.entity.Brand;
import com.ecommerce.common.entity.Category;
import com.ecommerce.common.entity.product.Product;

@DataJpaTest(showSql = true)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(true)
public class ProductRepositoryTest {
	
    @Autowired
    private ProductRepository repo;
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Test
    public void testCreateProduct() {
    	Brand brand = entityManager.find(Brand.class, 37);
    	Category category = entityManager.find(Category.class, 5);
    	
    	Product product = new Product();
    	product.setName("Acer Desktop");
    	product.setAlias("acer_desktop");
    	product.setShortDescription("A good computer Acer");
    	product.setFullDescription("This is a very good computer Acer full description");
    	
    	product.setBrand(brand);
    	product.setCategory(category);
    	
    	product.setPrice(678);
    	product.setCost(600);
    	product.setEnabled(true);
    	product.setInStock(true);
    	product.setCreatedTime(new Date());
    	product.setUpdatedTime(new Date());
    	
    	Product savedProduct = repo.save(product);
    	
    	assertThat(savedProduct).isNotNull();
    	assertThat(savedProduct.getId()).isGreaterThan(0);
    }
    
    @Test
    public void testListAllProducts() {
    	Iterable<Product> iterableProducts = repo.findAll();
    	
    	iterableProducts.forEach(System.out::println);
    }
    
    @Test
    public void testGetProduct() {
    	Product product = repo.findById(2).get();
    	System.out.println(product);
    	assertThat(product).isNotNull();
    }
    
    @Test
    public void testUpdateProduct() {
    	Integer id = 1;
    	Product product = repo.findById(id).get();
    	product.setPrice(499);
    	repo.save(product);
    	Product updatedProduct = entityManager.find(Product.class, id);
    	assertThat(updatedProduct.getPrice()).isEqualTo(499);
    }
    
    @Test
    public void testDeleteProduct() {
    	repo.deleteById(3);
    	assertThat(repo.findById(3)).isEmpty();
    }
    
    @Test
    public void testSaveProductWithImages() {
    	Product product = repo.findById(1).get();
    	product.setMainImage("main image.jpg");
    	product.addExtraImage("extra 1.png");
    	product.addExtraImage("extra 2.png");
    	product.addExtraImage("extra 3.png");
    	Product savedProduct = repo.save(product);
    	assertThat(savedProduct.getImages().size()).isEqualTo(3);
    }
    
    @Test
    public void testSaveProductWithDetail() {
    	Integer productId = 1;
    	Product product = repo.findById(productId).get();
    	
    	product.addDetail("Detail Memory", "128 GB");
    	product.addDetail("Detail Memory X", "180 GB");
    	
    	Product savedProduct = repo.save(product);
    	assertThat(savedProduct.getDetails()).isNotNull();
    }
    
  
}

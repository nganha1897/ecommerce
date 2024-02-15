package com.ecommerce.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

import com.ecommerce.common.entity.Role;
import com.ecommerce.common.entity.User;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace=Replace.NONE)
@Rollback(true)
public class UserRepositoryTest {
	 @Autowired
	 private UserRepository repo;
	 
	 @Autowired
	 private TestEntityManager entityManager;
	 
	 @Test
	 public void testCreateNewUserWithOneRole() {
		 Role roleAdmin = entityManager.find(Role.class, 1);
		 User htran = new User("htran@gmail.com", "a", "Ha", "Tran");
		 htran.addRole(roleAdmin);
		 
		 User savedUser = repo.save(htran);
		 assertThat(savedUser.getId()).isGreaterThan(0);
	 }
	 
	 @Test
	 public void testCreateNewUserWithTwoRoles() {
		 Role roleEditor = new Role(3);
		 Role roleAssistant = new Role(5);
		 User alice = new User("alice@gmail.com", "b", "Alice", "James");
		 alice.addRole(roleEditor);
		 alice.addRole(roleAssistant);
		 
		 User savedUser = repo.save(alice);
		 assertThat(savedUser.getId()).isGreaterThan(0);
	 }
	 
	 @Test
	 public void testListAllUsers() {
		 Iterable<User> listUsers = repo.findAll();
		 listUsers.forEach(user -> System.out.println(user));
	 }
	 
	 @Test
	 public void testGetUserById() {
		 User userHa = repo.findById(1).get();
		 System.out.println(userHa);
		 assertThat(userHa).isNotNull();
	 }
	 
	 @Test
	 public void testUpdateUserDetails() {
		 User userHa = repo.findById(1).get();
		 userHa.setEnabled(true);
		 userHa.setEmail("htran@google.com");
		 
		 repo.save(userHa);
	 }
	 
	 @Test
	 public void testUpdateUserRoles() {
		 User userAlice = repo.findById(2).get();
		 Role roleEditor = new Role(3);
		 Role roleSalesperson = new Role(2);
		 
		 userAlice.getRoles().remove(roleEditor);
		 userAlice.addRole(roleSalesperson);
		 
		 repo.save(userAlice);
	 }
	 
	 @Test
	 public void testDeleteUser() {
		 Integer userId = 2;
		 repo.deleteById(userId);
	 }
	 
	 @Test
	 public void testGetUserByEmail() {
		 String email = "htran@google.com";
		 User user = repo.getUserByEmail(email);
		 assertThat(user).isNotNull();
	 }
	 
	 @Test
	 public void testCountById() {
		 Integer id = 4;
		 Long countById = repo.countById(id);
		 assertThat(countById).isNotNull().isGreaterThan(0);
	 }
	 
	 @Test 
	 public void testDisableUser() {
		 Integer id = 1;
		 repo.updateEnabledStatus(id, false);
	 }
	 
	 @Test 
	 public void testEnableUser() {
		 Integer id = 1;
		 repo.updateEnabledStatus(id, true);
	 }
	 
	 @Test
	 public void testListFirstPage() {
		 int pageNumber = 0;
		 int pageSize = 4;
		 Pageable pageable = PageRequest.of(pageNumber, pageSize);
		 Page<User> page = repo.findAll(pageable);
		 List<User> listUsers = page.getContent();
		 
		 listUsers.forEach(user -> System.out.println(user));
		 assertThat(listUsers.size()).isEqualTo(pageSize);	 
	 }
	 
	 @Test
	 public void testSearchUsers() {
		 String keyword = "bruce";
		 int pageNumber = 0;
		 int pageSize = 4;
		 Pageable pageable = PageRequest.of(pageNumber, pageSize);
		 Page<User> page = repo.findAll(keyword, pageable);
		 List<User> listUsers = page.getContent();
		 
		 listUsers.forEach(user -> System.out.println(user));
		 assertThat(listUsers.size()).isGreaterThan(0); 
	 }
	 
}

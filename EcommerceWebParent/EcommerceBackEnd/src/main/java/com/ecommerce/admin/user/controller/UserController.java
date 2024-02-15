package com.ecommerce.admin.user.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ecommerce.admin.AmazonS3Util;
import com.ecommerce.admin.FileUploadUtil;
import com.ecommerce.admin.paging.PagingAndSortingHelper;
import com.ecommerce.admin.paging.PagingAndSortingParam;
import com.ecommerce.admin.user.UserNotFoundException;
import com.ecommerce.admin.user.UserService;
import com.ecommerce.admin.user.export.UserCsvExporter;
import com.ecommerce.admin.user.export.UserExcelExporter;
import com.ecommerce.admin.user.export.UserPdfExporter;
import com.ecommerce.common.entity.Role;
import com.ecommerce.common.entity.User;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class UserController {
	
	@Autowired
    private UserService service;
    
	@GetMapping("/users")
	public String listFirstPage() {
		return getRedirectURL();
	}
	
	@GetMapping("/users/page/{pageNum}") 
	public String listByPage(@PagingAndSortingParam(listName = "listUsers", moduleURL = "/users") PagingAndSortingHelper helper, @PathVariable(name="pageNum") int pageNum) {
	    service.listByPage(pageNum, helper);
		
		return "users/users";
	}
	
	
	@GetMapping("/users/new")
	public String newUser(Model model) {
		List<Role> listRoles = service.listRoles();
		User user = new User();
		user.setEnabled(true);
		model.addAttribute("user", user);
		model.addAttribute("listRoles", listRoles);
		model.addAttribute("pageTitle", "Create New User");
		return "users/user_form";
	}
	
	@PostMapping("/users/save")
	public String saveUser(User user, RedirectAttributes redirectAttributes,
			@RequestParam("image") MultipartFile multipartFile ) throws IOException {
		if (!multipartFile.isEmpty()) {
		    String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
		    user.setPhotos(fileName);
		    User savedUser = service.save(user);
		    
		    String uploadDir = "user-photos/" + savedUser.getId();
		    AmazonS3Util.removeFolder(uploadDir);
    		AmazonS3Util.uploadFile(uploadDir, fileName, multipartFile.getInputStream());
		} else {
			if (user.getPhotos().isEmpty()) user.setPhotos(null);
			service.save(user);
		}
		
		redirectAttributes.addFlashAttribute("message", "The user has been saved successfully");

		return getRedirectURLtoAffectedUser(user);
	}

	private String getRedirectURLtoAffectedUser(User user) {
		return "redirect:/users/page/1?sortField=id&sortDir=asc&keyword=" + user.getEmail();
	}
	
	private String getRedirectURL() {
		return "redirect:/users/page/1?sortField=firstName&sortDir=asc"; 
	}
	
	@GetMapping("/users/edit/{id}")
	public String editUser(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes, Model model) {
		try {
		    User user = service.get(id);
		    List<Role> listRoles = service.listRoles();
		    model.addAttribute("user", user);
		    model.addAttribute("pageTitle", "Edit User (Id: " + id + ")");
		    model.addAttribute("listRoles", listRoles);
		    return "users/user_form";
		} catch (UserNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}
		
		return getRedirectURL();
	}
		
	@GetMapping("/users/delete/{id}")
	public String deleteUser(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes, Model model) {
		try {
		    service.delete(id);
		    redirectAttributes.addFlashAttribute("message", "The user Id " + id + " has been deleted successfully");
		} catch (UserNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}
		
		return getRedirectURL();
	}
	
	@GetMapping("/users/{id}/enabled/{status}")
	public String updateUserEnabledStatus(@PathVariable(name = "id") Integer id, 
			@PathVariable("status") boolean enabled,
			RedirectAttributes redirectAttributes) {
		service.updateUserEnabledStatus(id, enabled);
		redirectAttributes.addFlashAttribute("message", "The user " + id + " has been " + (enabled ? "enabled" : "disabled"));
		return getRedirectURL();
	}
	
	@GetMapping("/users/export/csv")
	public void exportToCSV(HttpServletResponse response) throws IOException {
		List<User> listUsers = service.listAll();
		UserCsvExporter exporter = new UserCsvExporter();
		exporter.export(listUsers, response);
	}
	
	@GetMapping("/users/export/excel")
	public void exportToExcel(HttpServletResponse response) throws IOException {
		List<User> listUsers = service.listAll();
		UserExcelExporter exporter = new UserExcelExporter();
		exporter.export(listUsers, response);
	}
	
	@GetMapping("/users/export/pdf")
	public void exportToPdf(HttpServletResponse response) throws IOException {
		List<User> listUsers = service.listAll();
		UserPdfExporter exporter = new UserPdfExporter();
		exporter.export(listUsers, response);
	}
	
}

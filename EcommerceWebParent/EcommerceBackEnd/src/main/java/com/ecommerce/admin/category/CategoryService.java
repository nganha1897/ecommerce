package com.ecommerce.admin.category;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.admin.AmazonS3Util;
import com.ecommerce.admin.FileUploadUtil;
import com.ecommerce.common.entity.Category;
import com.ecommerce.common.exception.CategoryNotFoundException;

@Service
@Transactional
public class CategoryService {
	public static final int ROOT_CATEGORIES_PER_PAGE = 4;
	@Autowired
	private CategoryRepository repo;

	public List<Category> listAll() {
		Sort sort = Sort.by("name").ascending();

		List<Category> categoryList = new ArrayList<>();
		Iterable<Category> categoriesInDB = repo.findAll(sort);

		for (Category ca : categoriesInDB) {
			if (ca.getParent() == null) {
				listSubCategories(categoryList, ca, 1);
			}
		}
		return categoryList;
	}

	public List<Category> listByPage(CategoryPageInfo pageInfo, int pageNum, String sortDir, String keyword) {
		Sort sort = Sort.by("name");
		int sortDirMult = 0;
		if (sortDir.equals("asc")) {
			sort = sort.ascending();
			sortDirMult = 1;
		} else {
			sort = sort.descending();
			sortDirMult = -1;
		}
		List<Category> categoryList = new ArrayList<>();
		Pageable pageable = PageRequest.of(pageNum - 1, ROOT_CATEGORIES_PER_PAGE, sort);
		Page<Category> pageCategories;

		if (keyword != null) {
			pageCategories = repo.findAll(keyword, pageable);
		} else {
			pageCategories = repo.findAllRootCategories(pageable);
		}
		List<Category> categoriesInDB = pageCategories.getContent();

		pageInfo.setTotalElements(pageCategories.getTotalElements());
		pageInfo.setTotalPages(pageCategories.getTotalPages());

		if (keyword != null) {
			for (Category ca : categoriesInDB) {
				categoryList.add(ca);
			}
		} else {
			for (Category ca : categoriesInDB) {
				if (ca.getParent() == null) {
					listSubCategories(categoryList, ca, sortDirMult);
				}
			}
		}
		return categoryList;
	}

	private void listSubCategories(List<Category> categoryList, Category curCategory, int sortDirMult) {
		categoryList.add(curCategory);
		List<Category> subCategoryList = new ArrayList<>(curCategory.getChildren());
		Collections.sort(subCategoryList, (c1, c2) -> c1.getName().compareTo(c2.getName()) * sortDirMult);
		for (Category subCategory : subCategoryList) {
			listSubCategories(categoryList, subCategory, sortDirMult);
		}
	}

	public Category save(Category category) {
		Category parent = category.getParent();
		if (parent != null) {
			String allParentIds = parent.getAllParentIDs() == null ? "-" : parent.getAllParentIDs();
			allParentIds += String.valueOf(parent.getId()) + "-";
			category.setAllParentIDs(allParentIds);
		}
		return repo.save(category);
	}

	public Category get(Integer id) throws CategoryNotFoundException {
		try {
			return repo.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new CategoryNotFoundException("Could not find any category with Id " + id);
		}
	}

	public String checkUnique(Integer id, String name, String alias) {
		boolean isCreatingNew = (id == null || id == 0);

		Category categoryByName = repo.findByName(name);

		if (isCreatingNew) {
			if (categoryByName != null) {
				return "Duplicated Name";
			} else {
				Category categoryByAlias = repo.findByAlias(alias);
				if (categoryByAlias != null) {
					return "Duplicated Alias";
				}
			}
		} else {
			if (categoryByName != null && categoryByName.getId() != id) {
				return "Duplicated Name";
			} else {
				Category categoryByAlias = repo.findByAlias(alias);
				if (categoryByAlias != null && categoryByAlias.getId() != id) {
					return "Duplicated Alias";
				}
			}
		}

		return "OK";
	}

	public void updateCategoryEnabledStatus(Integer id, boolean enabled) {
		repo.updateEnabledStatus(id, enabled);

	}

	public void delete(Integer id) throws CategoryNotFoundException {
		Long countById = repo.countById(id);
		if (countById == null || countById == 0) {
			throw new CategoryNotFoundException("Could not find any category with Id " + id);
		}
		String categoryDir = "category-images/" + id;
		AmazonS3Util.removeFolder(categoryDir);
		repo.deleteById(id);
	}

}
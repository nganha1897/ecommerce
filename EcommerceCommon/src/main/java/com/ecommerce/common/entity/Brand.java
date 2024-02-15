package com.ecommerce.common.entity;

import java.util.HashSet;
import java.util.Set;

import com.ecommerce.common.Constants;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "brands")
public class Brand extends IdBasedEntity {

	@Column(length = 45, nullable = false, unique = true)
	private String name;

	@Column(length = 128, nullable = false)
	private String logo;

	@ManyToMany()
    @JoinTable(
    		name = "brands_categories",
    		joinColumns = @JoinColumn(name = "brand_id"),
    		inverseJoinColumns = @JoinColumn(name = "category_id")
    		)
    private Set<Category> categories = new HashSet<>();
	
	public Brand() {
	}
	
	public Brand(Integer id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public Brand(String name) {
		this.name = name;
		this.logo = "brand-logo.png";
	}
	
	public Brand(String name, Set<Category> categories) {
		this.name = name;
		this.logo = "brand-logo.png";
		this.categories = categories;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public Set<Category> getCategories() {
		return categories;
	}

	public void setCategories(Set<Category> categories) {
		this.categories = categories;
	}

	@Override
	public String toString() {
		return "Brand [id=" + id + ", name=" + name + ", categories=" + categories + "]";
	}

	@Transient
	public String getImagePath() {
		if (this.id == null)
			return "/images/image-thumbnail.png";
		return Constants.S3_BASE_URI + "/brand-logos/" + this.id + "/" + this.logo;
	}
}

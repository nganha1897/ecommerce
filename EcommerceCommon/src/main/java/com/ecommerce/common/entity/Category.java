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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "categories")
public class Category extends IdBasedEntity {
	@Column(length = 128, nullable = false, unique = true)
    private String name;
	@Column(length = 64, nullable = false, unique = true)
    private String alias;
	@Column(length = 128, nullable = false)
    private String image;
    private boolean enabled;
    @Column(name="all_parent_ids", length = 256, nullable = true)
    private String allParentIDs;
    
    @Transient
    private String formatName;
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Category parent;
    
    @OneToMany(mappedBy = "parent")
    @OrderBy("name asc")
    private Set<Category> children = new HashSet<>();
    
    public Category() {
    }
    
    public Category(Integer id) {
        this.id = id;	
    }
    
	public Category(String name) {
		this.name = name;
		this.alias = name;
		this.image = "default.png";
	}
	
	public Category(String name, Category parent) {
		this(name);
		this.parent = parent;
	}

	public Category(Integer id, String name, String alias) {
		this.id = id;
		this.name = name;
		this.alias = alias;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getAllParentIDs() {
		return allParentIDs;
	}

	public void setAllParentIDs(String allParentIDs) {
		this.allParentIDs = allParentIDs;
	}

	public Category getParent() {
		return parent;
	}

	public void setParent(Category parent) {
		this.parent = parent;
	}

	public Set<Category> getChildren() {
		return children;
	}

	public void setChildren(Set<Category> children) {
		this.children = children;
	}
	
	@Transient
	public String getImagePath() {
		if (this.id == null)
			return "/images/image-thumbnail.png";
		return Constants.S3_BASE_URI + "/category-images/" + this.id + "/" + this.image;
	}
	
	@Transient
	public boolean hasChildren() {
		return this.children.size() > 0;
	}
	
	@Transient
	private int getLevel() {
		if (parent == null) {
			return 0;
		}
		else {
			return parent.getLevel() + 1;
		}
	}
	@Transient
	public String getFormatName() {
		if (formatName == null) {
			setFormatName("--");
		}
		return this.formatName;
	}
	
	@Transient
	public void setFormatName(String prefix) {
		StringBuilder formatName = new StringBuilder();
		for (int i=0; i<getLevel(); i++) {
			formatName.append(prefix);
		}
		formatName.append(name);
		this.formatName = formatName.toString();
	}

	@Override
	public String toString() {
		return name;
	}
}

package com.ecommerce.common.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "countries")
public class Country extends IdBasedEntity {

	@Column(nullable = false, length = 45, unique = true)
	private String name;

	@Column(nullable = false, length = 5, unique = true)
	private String code;
	
	@OneToMany(mappedBy = "country", cascade = CascadeType.ALL)
	private Set<State> states = new HashSet<>();
	
	

	public Country() {	
	}
	
	public Country(Integer id) {	
		this.id = id;
	}
	
	public Country(String name, String code) {
		this.name = name;
		this.code = code;
	}
	
	public Country(String name) {
		this.name = name;
	}
	
	public Country(Integer id, String name, String code) {
		this.id = id;
		this.name = name;
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

//	public Set<State> getStates() {
//		return states;
//	}
//
//	public void setStates(Set<State> states) {
//		this.states = states;
//	}

	@Override
	public String toString() {
		return "Country [name=" + name + ", code=" + code + "]";
	}
	
	
}

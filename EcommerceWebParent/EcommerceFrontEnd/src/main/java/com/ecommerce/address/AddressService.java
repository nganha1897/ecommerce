package com.ecommerce.address;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.common.entity.Address;
import com.ecommerce.common.entity.Customer;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class AddressService {
    @Autowired
    private AddressRepository repo;
    
    public List<Address> listAddressBook(Customer customer) {
    	return repo.findByCustomer(customer);
    }
    
    public void save(Address addressInForm) {
		repo.save(addressInForm);
	}

	public Address get(Integer addressId, Integer customerId) {
		return repo.findByIdAndCustomer(addressId, customerId);
	}
	
	public void delete(Integer addressId, Integer customerId) {
		repo.deleteByIdAndCustomer(addressId, customerId);
	}
	
	public void setDefaultAddress(Integer addressId, Integer customerId) {
		if (addressId > 0)
		    repo.setDefaultAddress(addressId);
		
		repo.setNonDefaultForOthers(addressId, customerId);
	}
	
	public Address getDefaultAddress(Customer customer) {
		return repo.findDefaultByCustomer(customer.getId());
		
	}
}

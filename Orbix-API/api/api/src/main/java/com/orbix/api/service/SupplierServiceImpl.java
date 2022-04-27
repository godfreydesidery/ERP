/**
 * 
 */
package com.orbix.api.service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.orbix.api.accessories.Formater;
import com.orbix.api.domain.SalesInvoice;
import com.orbix.api.domain.Supplier;
import com.orbix.api.exceptions.InvalidOperationException;
import com.orbix.api.exceptions.NotFoundException;
import com.orbix.api.models.RecordModel;
import com.orbix.api.repositories.SupplierRepository;

import io.swagger.models.properties.StringProperty.Format;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author GODFREY
 *
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SupplierServiceImpl implements SupplierService {
	
	private final SupplierRepository supplierRepository;

	@Override
	public Supplier save(Supplier supplier) {
		validateSupplier(supplier);
		Supplier sup = supplierRepository.save(supplier);
		if(sup.getCode().equals("NA")) {
			sup.setCode(generateSupplierCode(sup));
			sup = supplierRepository.save(sup);
		}	
		log.info("Saving customer to database");
		return supplierRepository.save(supplier);
	}

	@Override
	public Supplier get(Long id) {
		return supplierRepository.findById(id).get();
	}
	
	@Override
	public Supplier getByCode(String code) {
		Optional<Supplier> supplier = supplierRepository.findByCode(code);
		if(!supplier.isPresent()) {
			throw new NotFoundException("Supplier not found");
		}
		return supplier.get();
	}

	@Override
	public Supplier getByName(String name) {
		Optional<Supplier> supplier = supplierRepository.findByName(name);
		if(!supplier.isPresent()) {
			throw new NotFoundException("Supplier not found");
		}
		return supplier.get();
	}

	@Override
	public boolean delete(Supplier supplier) {
		if(allowDelete(supplier) == true) {
			supplierRepository.delete(supplier);
			return true;
		}else {
			throw new InvalidOperationException("Deleting this Supplier is not allowed");
		}
	}

	@Override
	public List<Supplier> getAll() {
		log.info("Fetching all suppliers");
		return supplierRepository.findAll();
	}
	
	private boolean validateSupplier(Supplier supplier) {
		/**
		 * Put validation logic, throw Invalid exception if not valid
		 */
		
		return true;
	}
	
	private boolean allowDelete(Supplier supplier) {
		/**
		 * Put logic to allow till deletion, return false if not allowed, else return true
		 */		
		return false;
	}

	@Override
	public List<String> getNames() {
		return supplierRepository.getActiveNames();
	}
	
	private String generateSupplierCode(Supplier supplier) {
		Long number = supplier.getId();		
		String sNumber = number.toString();
		return "SPL-"+Formater.formatSix(sNumber);
	}
	
	
	@Override
	public RecordModel requestSupplierCode() {
		Long id = 1L;
		try {
			id = supplierRepository.getLastId() + 1;
		}catch(Exception e) {}
		RecordModel model = new RecordModel();
		model.setCode("S"+Formater.formatSix(id.toString()));		
		return model;
	}	


}

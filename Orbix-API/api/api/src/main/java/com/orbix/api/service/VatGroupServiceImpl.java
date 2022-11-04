/**
 * 
 */
package com.orbix.api.service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.orbix.api.accessories.Formater;
import com.orbix.api.domain.VatGroup;
import com.orbix.api.exceptions.InvalidEntryException;
import com.orbix.api.exceptions.InvalidOperationException;
import com.orbix.api.exceptions.NotFoundException;
import com.orbix.api.models.RecordModel;
import com.orbix.api.repositories.VatGroupRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Godfrey
 *
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class VatGroupServiceImpl implements VatGroupService{
	private final VatGroupRepository vatGroupRepository;

	@Override
	public VatGroup save(VatGroup vatGroup) {
		if(validateVatGroup(vatGroup)) {
			return vatGroupRepository.save(vatGroup);
		}else {
			throw new InvalidEntryException("Invalid VAT entry");
		}
	}

	@Override
	public VatGroup get(Long id) {
		return vatGroupRepository.findById(id).get();
	}
	
	@Override
	public VatGroup getByCode(String code) {
		Optional<VatGroup> vatGroup = vatGroupRepository.findByCode(code);
		if(!vatGroup.isPresent()) {
			throw new NotFoundException("VatGroup not found");
		}
		return vatGroup.get();
	}

	@Override
	public boolean delete(VatGroup vatGroup) {
		if(allowDelete(vatGroup) == true) {
			vatGroupRepository.delete(vatGroup);
			return true;
		}else {
			throw new InvalidOperationException("Deleting this VatGroup is not allowed");
		}
	}

	@Override
	public List<VatGroup> getAll() {
		log.info("Fetching all VAT groups");
		return vatGroupRepository.findAll();
	}
	
	private boolean validateVatGroup(VatGroup vatGroup) {
		/**
		 * Put validation logic, throw Invalid exception if not valid
		 */
		if(vatGroup.getValue() >= 0 && vatGroup.getValue() <= 100) {
			return true;
		}
		return false;
	}
	
	private boolean allowDelete(VatGroup vatGroup) {
		/**
		 * Put logic to allow till deletion, return false if not allowed, else return true
		 */		
		return false;
	}

	@Override
	public List<String> getCodes() {
		return vatGroupRepository.getCodes();
	}
		
}

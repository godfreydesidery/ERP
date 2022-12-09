/**
 * 
 */
package com.orbix.api.service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.orbix.api.accessories.Formater;
import com.orbix.api.domain.SalesAgent;
import com.orbix.api.domain.SalesAgent;
import com.orbix.api.exceptions.InvalidOperationException;
import com.orbix.api.exceptions.NotFoundException;
import com.orbix.api.models.RecordModel;
import com.orbix.api.repositories.EmployeeRepository;
import com.orbix.api.repositories.SalesAgentRepository;

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
public class SalesAgentServiceImpl implements SalesAgentService{
	private final SalesAgentRepository salesAgentRepository;

	@Override
	public SalesAgent save(SalesAgent salesAgent) {
		validateAgent(salesAgent);
		log.info("Saving sale agent to the database");
		SalesAgent c = salesAgentRepository.saveAndFlush(salesAgent);		
		if(c.getNo().equals("NA")) {
			c.setNo(generateAgentNo(c));
			c = salesAgentRepository.saveAndFlush(c);
		}
		return salesAgentRepository.save(c);
	}

	@Override
	public SalesAgent get(Long id) {
		return salesAgentRepository.findById(id).get();
	}
	
	@Override
	public SalesAgent getByNo(String no) {
		Optional<SalesAgent> salesAgent = salesAgentRepository.findByNo(no);
		if(!salesAgent.isPresent()) {
			throw new NotFoundException("SalesAgent not found");
		}
		return salesAgent.get();
	}

	@Override
	public boolean delete(SalesAgent salesAgent) {
		if(allowDelete(salesAgent)) {
			salesAgentRepository.delete(salesAgent);
		}else {
			return false;
		}
		return true;
	}

	@Override
	public List<SalesAgent> getAll() {
		log.info("Fetching all sales agents");
		return salesAgentRepository.findAll();
	}
	
	private boolean validateAgent(SalesAgent salesAgent) {
		/**
		 * Put validation logic, throw Invalid exception if not valid
		 */
		
		
		return true;
	}
	
	private boolean allowDelete(SalesAgent salesAgent) {
		/**
		 * Put logic to allow till deletion, return false if not allowed, else return true
		 */
		
		throw new InvalidOperationException("Deleting the selected salesAgent is not allowed");
		
		//return true;
	}

	

	@Override
	public SalesAgent getByName(String name) {
		Optional<SalesAgent> salesAgent = salesAgentRepository.findByName(name);
		if(!salesAgent.isPresent()) {
			throw new NotFoundException("Sales Agent not found");
		}
		return salesAgent.get();
	}
	
	@Override
	public List<String> getNames() {
		return salesAgentRepository.getActiveNames();
	}
	
	private String generateAgentNo(SalesAgent salesAgent) {
		Long number = salesAgent.getId();		
		String sNumber = number.toString();
		return "SA"+sNumber;
	}
	
	@Override
	public RecordModel requestSalesAgentNo() {
		Long id = 1L;
		try {
			id = salesAgentRepository.getLastId() + 1;
		}catch(Exception e) {}
		RecordModel model = new RecordModel();
		model.setNo("SA"+id.toString());		
		return model;
	}	
}

/**
 * 
 */
package com.orbix.api.service;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.orbix.api.accessories.Formater;
import com.orbix.api.domain.Debt;
import com.orbix.api.domain.DebtTracker;
import com.orbix.api.domain.SalesAgent;
import com.orbix.api.exceptions.InvalidEntryException;
import com.orbix.api.models.DebtModel;
import com.orbix.api.models.DebtTrackerModel;
import com.orbix.api.models.LpoModel;
import com.orbix.api.models.RecordModel;
import com.orbix.api.repositories.DebtRepository;
import com.orbix.api.repositories.DebtTrackerRepository;

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
public class DebtTrackerServiceImpl implements DebtTrackerService {
	private final DebtTrackerRepository debtTrackerRepository;
	private final DebtRepository debtRepository;

	@Override
	public boolean create(DebtTracker debtTracker) {
		if(debtTracker.getAmount() <= 0) {
			throw new InvalidEntryException("Invalid debt amount");
		}
		if(debtTracker.getAmount() > debtTracker.getDebt().getBalance()) {
			throw new InvalidEntryException("Amount exceeds the sales debt balance");
		}
		Debt debt = debtTracker.getDebt();
		debt.setBalance(debt.getBalance() - debtTracker.getAmount());
		debt = debtRepository.saveAndFlush(debt);
		debtTracker.setDebt(debt);
		
		DebtTracker d = debtTrackerRepository.saveAndFlush(debtTracker);
		if(d.getNo().equals("NA")) {
			d.setNo(generateDebtTrackerNo(d));
			d = debtTrackerRepository.saveAndFlush(d);
		}		
		DebtTrackerModel model = new DebtTrackerModel();
		model.setId(d.getId());
		model.setNo(d.getNo());
		model.setStatus(d.getStatus());
		model.setAmount(d.getAmount());
		model.setPaid(d.getPaid());
		model.setBalance(d.getBalance());
		model.setOfficerIncharge(d.getOfficerIncharge());
		model.setInceptionDay(d.getInceptionDay());
		model.setCustomer(d.getCustomer());
		model.setDebt(d.getDebt());		
		model.setComments(d.getComments());		
		return true;		
	}

	@Override
	public DebtTracker pay(DebtTracker debtTracker, double amount) {
		/**
		 * First register debt payment, to be implemented later
		 * then pay debt
		 */
		if(amount <= 0) {
			throw new InvalidEntryException("Invalid amount");
		}else if(amount > debtTracker.getBalance()) {
			throw new InvalidEntryException("Payment amount exceeds debt balance");
		}
		debtTracker.setBalance(debtTracker.getAmount() - amount);
		if(debtTracker.getBalance() == 0) {
			debtTracker.setStatus("PAID");
		}else {
			debtTracker.setStatus("PARTIAL");
		}
		return debtTrackerRepository.saveAndFlush(debtTracker);
	}
	
	private String generateDebtTrackerNo(DebtTracker debtTracker) {
		Long number = debtTracker.getId();		
		String sNumber = number.toString();
		return Formater.formatWithCurrentDate("DBT",sNumber);
	}
	
	@Override
	public RecordModel requestDebtTrackerNo() {
		Long id = 1L;
		try {
			id = debtTrackerRepository.getLastId() + 1;
		}catch(Exception e) {}
		RecordModel model = new RecordModel();
		model.setNo(Formater.formatWithCurrentDate("DBT",id.toString()));
		return model;
	}

	@Override
	public List<DebtTracker> getAll() {
		// TODO Auto-generated method stub
		return null;
	}	
	
	

}

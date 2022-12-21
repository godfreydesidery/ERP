/**
 * 
 */
package com.orbix.api.service;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.orbix.api.domain.Employee;
import com.orbix.api.domain.PackingList;
import com.orbix.api.domain.SalesAgent;
import com.orbix.api.domain.User;
import com.orbix.api.accessories.Formater;
import com.orbix.api.domain.Debt;
import com.orbix.api.domain.Debt;
import com.orbix.api.exceptions.InvalidEntryException;
import com.orbix.api.models.DebtModel;
import com.orbix.api.models.RecordModel;
import com.orbix.api.repositories.DayRepository;
import com.orbix.api.repositories.DebtHistoryRepository;
import com.orbix.api.repositories.DebtRepository;

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
public class DebtServiceImpl implements DebtService {
	
	private final DebtRepository debtRepository;
	private final DebtHistoryService debtHistoryService;

	@Override
	public Debt create(Debt debt, User user) {
		/**
		 * First register debt payment, to be implemented later
		 * then pay debt
		 */
		Debt d = debtRepository.saveAndFlush(debt);
		if(d.getNo().equals("NA")) {
			d.setNo(generateDebtNo(d));
		}
		d = debtRepository.saveAndFlush(d);
		/**
		 * Register history
		 */
		debtHistoryService.create(debt.getBalance(), 0, debt, null, user, "New sales debt created "+debt.getNo());
		return d;
		
	}

	@Override
	public Debt pay(Debt debt, double amount, User user) {	
		/**
		 * Register history
		 */
		debtHistoryService.create(debt.getBalance(), amount, debt, null, user, "Received from sales debt "+debt.getNo());		
		/**
		 * Now receive debt
		 */
		if(amount <= 0) {
			throw new InvalidEntryException("Invalid amount");
		}else if(amount > debt.getAmount()) {
			throw new InvalidEntryException("Payment amount exceeds debt amount");
		}
		debt.setAmount(debt.getAmount() - amount);
		return debtRepository.saveAndFlush(debt);
	}
	
	@Override
	public List<DebtModel> getBySalesAgentAndApprovedOrPartial(SalesAgent salesAgent) {
		List<String> statuses = new ArrayList<String>();
		statuses.add("PENDING");
		statuses.add("PARTIAL");
		List<Debt> debts = debtRepository.findBySalesAgentAndPendingOrPartial(salesAgent, statuses);
		List<DebtModel> models = new ArrayList<DebtModel>();
		for(Debt d : debts) {
			DebtModel model = new DebtModel();
			model.setId(d.getId());
			model.setNo(d.getNo());
			model.setSalesAgent(d.getSalesAgent());
			model.setStatus(d.getStatus());
			model.setAmount(d.getAmount());
			model.setBalance(d.getBalance());
//			if(inv.getCreatedAt() != null && inv.getCreatedBy() != null) {
//				model.setCreated(dayRepository.findById(inv.getCreatedAt()).get().getBussinessDate() +" "+ userRepository.getAlias(inv.getCreatedBy()));
//			}
//			if(inv.getApprovedAt() != null && inv.getApprovedBy() != null) {
//				model.setApproved(dayRepository.findById(inv.getApprovedAt()).get().getBussinessDate() +" "+ userRepository.getAlias(inv.getApprovedBy()));
//			}			
			models.add(model);
		}
		return models;
	}
	
	private String generateDebtNo(Debt debt) {
		Long number = debt.getId();		
		String sNumber = number.toString();
		//return "DBT-"+Formater.formatNine(sNumber);
		return Formater.formatWithCurrentDate("SDB",sNumber);
	}
	
	

}

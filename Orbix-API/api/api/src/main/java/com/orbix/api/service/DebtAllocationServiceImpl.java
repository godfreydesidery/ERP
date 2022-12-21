/**
 * 
 */
package com.orbix.api.service;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.orbix.api.accessories.Formater;
import com.orbix.api.domain.DebtAllocation;
import com.orbix.api.domain.SalesAgent;
import com.orbix.api.domain.SalesList;
import com.orbix.api.domain.Debt;
import com.orbix.api.exceptions.InvalidOperationException;
import com.orbix.api.exceptions.NotFoundException;
import com.orbix.api.repositories.DebtAllocationRepository;
import com.orbix.api.repositories.SalesAgentRepository;
import com.orbix.api.repositories.SalesListRepository;
import com.orbix.api.repositories.UserRepository;
import com.orbix.api.repositories.DayRepository;
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
public class DebtAllocationServiceImpl implements DebtAllocationService {
	private final DebtAllocationRepository debtAllocationRepository;
	private final SalesAgentRepository salesAgentRepository;
	private final DebtRepository debtRepository;
	private final UserService userService;
	private final DayService dayService;
	private final DayRepository dayRepository;
	private final SalesListRepository salesListRepository;
	
	private final DebtHistoryService debtHistoryService;
	private final UserRepository userRepository;

	@Override
	public boolean allocate(SalesAgent salesAgent, Debt debt, HttpServletRequest request) {
		Optional<SalesAgent> e = salesAgentRepository.findById(salesAgent.getId());
		if(!e.isPresent()) {
			throw new NotFoundException("SalesAgent not found in database");
		}
		Optional<Debt> d = debtRepository.findById(debt.getId());
		if(!d.isPresent()) {
			throw new NotFoundException("Debt not found in database");
		}
		if(d.get().getSalesList() != null) {
			if(e.get().getId() != d.get().getSalesList().getSalesAgent().getId()) {
				throw new InvalidOperationException("SalesAgent and invoice do not match");
			}
		}else {
			throw new InvalidOperationException("Debt has no reference");
		}
		
		double salesAgentBalance = salesAgent.getBalance();
		if(salesAgentBalance <= 0) {
			throw new InvalidOperationException("Could not process, no balance available");
		}
		double referenceBalance = 0;
		double debtAllocationAmount = 0;
		if(d.get().getSalesList() != null) {
			referenceBalance = d.get().getSalesList().getTotalDeficit();
			if(referenceBalance <= 0) {
				throw new InvalidOperationException("Could not process, reference document has no deficit");
			}
			Optional<SalesList> p = salesListRepository.findById(d.get().getSalesList().getId());
			
			debtHistoryService.create(debt.getBalance(), salesAgentBalance, debt, null, userRepository.findById(userService.getUserId(request)).get(), "Debt allocation "+debt.getNo());
			
			if(salesAgentBalance >= debt.getBalance()) {
				double balance = debt.getBalance();
				p.get().setTotalOther(p.get().getTotalOther() + balance);
				p.get().setTotalDeficit(p.get().getTotalDeficit() - balance);
				salesListRepository.saveAndFlush(p.get());
				double newSalesAgentBalance = salesAgentBalance - debt.getBalance();
				balance = 0;
				debtAllocationAmount = debt.getBalance();
				salesAgent.setBalance(newSalesAgentBalance);
				debt.setBalance(balance);
				debt.setStatus("PAID");
				salesAgentRepository.saveAndFlush(salesAgent);
				debtRepository.saveAndFlush(debt);				
			}else if(salesAgentBalance < debt.getBalance()) {
				p.get().setTotalOther(p.get().getTotalOther() + salesAgentBalance);
				p.get().setTotalDeficit(p.get().getTotalDeficit() - salesAgentBalance);
				salesListRepository.saveAndFlush(p.get());
				double newSalesAgentBalance = 0;
				double balance = debt.getBalance() - salesAgentBalance;
				debtAllocationAmount = salesAgentBalance;
				salesAgent.setBalance(newSalesAgentBalance);
				debt.setBalance(balance);
				debt.setStatus("PARTIAL");
				salesAgentRepository.saveAndFlush(salesAgent);
				debtRepository.saveAndFlush(debt);
			}			
		}
		
		DebtAllocation debtAllocation = new DebtAllocation();
		debtAllocation.setNo("NA");
		debtAllocation.setDebt(debt);
		debtAllocation.setAmount(debtAllocationAmount);
		debtAllocation.setCreatedBy(userService.getUserId(request));
		debtAllocation.setCreatedAt(dayRepository.getCurrentBussinessDay().getId());
		DebtAllocation a = debtAllocationRepository.saveAndFlush(debtAllocation);
		if(a.getNo().equals("NA")) {
			a.setNo(generateDebtAllocationNo(a));
			a = debtAllocationRepository.saveAndFlush(a);
		}
		return true;		
	}
	
	private String generateDebtAllocationNo(DebtAllocation debtAllocation) {
		Long number = debtAllocation.getId();		
		String sNumber = number.toString();
		return Formater.formatWithCurrentDate("DAC",sNumber);
	}
}

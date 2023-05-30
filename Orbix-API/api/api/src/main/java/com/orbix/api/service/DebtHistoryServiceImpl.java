/**
 * 
 */
package com.orbix.api.service;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.orbix.api.domain.Debt;
import com.orbix.api.domain.DebtHistory;
import com.orbix.api.domain.DebtTracker;
import com.orbix.api.domain.SalesInvoice;
import com.orbix.api.domain.User;
import com.orbix.api.repositories.DayRepository;
import com.orbix.api.repositories.DebtHistoryRepository;

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
public class DebtHistoryServiceImpl implements DebtHistoryService {
	
	private final DebtHistoryRepository debtHistoryRepository;
	private final DayRepository dayRepository;

	@Override
	public DebtHistory create(double amount, double paid, Debt debt, SalesInvoice salesInvoice, DebtTracker debtTracker, User user, String reference) {
		DebtHistory dh = new DebtHistory();
		dh.setAmount(amount);
		dh.setPaid(paid);
		dh.setBalance(amount - paid);
		dh.setDebt(debt);
		dh.setSalesInvoice(salesInvoice);
		dh.setDebtTracker(debtTracker);
		dh.setDay(dayRepository.getCurrentBussinessDay());
		dh.setCreatedBy(user.getId());
		dh.setCreatedAt(dayRepository.getCurrentBussinessDay().getId());
		dh.setReference(reference);
		return debtHistoryRepository.saveAndFlush(dh);		
	}

}

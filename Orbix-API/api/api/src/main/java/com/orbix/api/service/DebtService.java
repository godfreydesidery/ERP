/**
 * 
 */
package com.orbix.api.service;

import java.util.List;

import com.orbix.api.domain.Debt;
import com.orbix.api.domain.Employee;
import com.orbix.api.domain.SalesAgent;
import com.orbix.api.domain.User;
import com.orbix.api.models.DebtModel;
import com.orbix.api.models.RecordModel;

/**
 * @author GODFREY
 *
 */
public interface DebtService {
	public Debt create(Debt debt, User user);
	public Debt pay(Debt debt, double amount, User user);
	List<DebtModel>getBySalesAgentAndApprovedOrPartial(SalesAgent salesAgent);
	
}

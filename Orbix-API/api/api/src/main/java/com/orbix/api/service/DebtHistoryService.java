/**
 * 
 */
package com.orbix.api.service;

import com.orbix.api.domain.Debt;
import com.orbix.api.domain.DebtHistory;
import com.orbix.api.domain.DebtTracker;
import com.orbix.api.domain.SalesDebt;
import com.orbix.api.domain.SalesInvoice;
import com.orbix.api.domain.User;

/**
 * @author Godfrey
 *
 */
public interface DebtHistoryService {
	public DebtHistory create(double amount, double paid, Debt debt, SalesInvoice salesInvoice, DebtTracker debtTracker, User user, String reference);	
}

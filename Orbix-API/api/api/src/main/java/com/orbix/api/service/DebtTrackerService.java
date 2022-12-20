/**
 * 
 */
package com.orbix.api.service;

import java.util.List;

import com.orbix.api.domain.DebtTracker;
import com.orbix.api.models.DebtTrackerModel;
import com.orbix.api.models.RecordModel;

/**
 * @author Godfrey
 *
 */
public interface DebtTrackerService {
	public boolean create(DebtTracker debtTracker);
	public DebtTracker pay(DebtTracker debtTracker, double amount);
	public RecordModel requestDebtTrackerNo();
	List<DebtTracker>getAll();
}

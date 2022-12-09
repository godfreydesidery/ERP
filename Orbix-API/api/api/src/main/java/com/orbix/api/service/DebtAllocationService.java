/**
 * 
 */
package com.orbix.api.service;

import javax.servlet.http.HttpServletRequest;

import com.orbix.api.domain.Debt;
import com.orbix.api.domain.SalesAgent;

/**
 * @author GODFREY
 *
 */
public interface DebtAllocationService {
	boolean allocate(SalesAgent salesAgent, Debt debt, HttpServletRequest request);
}

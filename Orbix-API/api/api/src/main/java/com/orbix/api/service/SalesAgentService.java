/**
 * 
 */
package com.orbix.api.service;

import java.util.List;

import com.orbix.api.domain.Employee;
import com.orbix.api.domain.SalesAgent;
import com.orbix.api.models.RecordModel;

/**
 * @author Godfrey
 *
 */
public interface SalesAgentService {
	SalesAgent save(SalesAgent salesAgent);
	SalesAgent get(Long id);
	SalesAgent getByNo(String no);
	SalesAgent getByName(String name);
	boolean delete(SalesAgent salesAgent);
	List<SalesAgent>getAll(); //edit this to limit the number, for perfomance.
	List<String> getNames();
	RecordModel requestSalesAgentNo();
}

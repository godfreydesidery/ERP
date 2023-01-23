/**
 * 
 */
package com.orbix.api.service;

import java.util.List;

import com.orbix.api.domain.Employee;
import com.orbix.api.domain.SalesAgent;
import com.orbix.api.models.LCustomerModel;
import com.orbix.api.models.LProductModel;
import com.orbix.api.models.LSalesListObjectModel;
import com.orbix.api.models.RecordModel;
import com.orbix.api.models.WMSSalesModel;

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
	
	LSalesListObjectModel passIn(String passName, String passCode);
	List<LCustomerModel> loadCustomers();
	List<LProductModel> loadAvailableProducts(String salesListNo, Long salesAgentId);
	
	Object confirmSale(WMSSalesModel sale);
}

/**
 * 
 */
package com.orbix.api.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orbix.api.domain.Debt;
import com.orbix.api.domain.Employee;
import com.orbix.api.domain.SalesAgent;

/**
 * @author GODFREY
 *
 */
public interface DebtRepository extends JpaRepository<Debt, Long> {
	
	/**
	 * @param customer
	 * @param string
	 * @return
	 */
	@Query("SELECT d FROM Debt d WHERE d.salesAgent =:salesAgent AND d.status IN (:statuses)")
	List<Debt> findBySalesAgentAndPendingOrPartial(SalesAgent salesAgent, List<String> statuses);
	
	@Query("SELECT MAX(d.id) FROM Debt d")
	Long getLastId();
}

/**
 * 
 */
package com.orbix.api.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orbix.api.domain.SalesAgent;
import com.orbix.api.domain.SalesAgentCustomer;

/**
 * @author Godfrey
 *
 */
public interface SalesAgentCustomerRepository extends JpaRepository<SalesAgentCustomer, Long> {

	/**
	 * @param salesAgent
	 * @return
	 */
	List<SalesAgentCustomer> findAllBySalesAgent(SalesAgent salesAgent);

	/**
	 * @param salesAgent
	 * @return
	 */
	Optional<SalesAgentCustomer> findBySalesAgent(SalesAgent salesAgent);

	/**
	 * @param name
	 * @param salesAgent
	 * @return
	 */
	Optional<SalesAgentCustomer> findByNameAndSalesAgent(String name, SalesAgent salesAgent);

}

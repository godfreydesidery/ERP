/**
 * 
 */
package com.orbix.api.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orbix.api.domain.SalesAgent;

/**
 * @author Godfrey
 *
 */
public interface SalesAgentRepository extends JpaRepository<SalesAgent, Long> {

	/**
	 * @param no
	 * @return
	 */
	Optional<SalesAgent> findByNo(String no);

	/**
	 * @param name
	 * @return
	 */
	Optional<SalesAgent> findByName(String name);
	
	@Query("SELECT s.name FROM SalesAgent s WHERE s.active =1")
	List<String> getActiveNames();
	
	@Query("SELECT MAX(s.id) FROM SalesAgent s")
	Long getLastId();

}

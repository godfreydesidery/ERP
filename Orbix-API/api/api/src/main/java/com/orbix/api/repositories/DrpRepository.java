/**
 * 
 */
package com.orbix.api.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orbix.api.domain.Drp;

/**
 * @author Godfrey
 *
 */
public interface DrpRepository extends JpaRepository<Drp, Long> {
	
	/**
	 * @param no
	 * @return
	 */
	Optional<Drp> findByNo(String no);
	
	@Query("SELECT MAX(d.id) FROM Drp d")
	Long getLastId();
	
	@Query("SELECT d FROM Drp d WHERE d.status IN (:statuses)")
	List<Drp> findAllVissible(List<String> statuses);
	
	@Query("SELECT d FROM Drp d WHERE d.status =:status")
	List<Drp> findAllApproved(String status);

	/**
	 * @return
	 */
	Drp findTopByOrderByIdDesc();
}

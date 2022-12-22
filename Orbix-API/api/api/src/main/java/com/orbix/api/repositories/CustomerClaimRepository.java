/**
 * 
 */
package com.orbix.api.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orbix.api.domain.CustomerClaim;
import com.orbix.api.domain.ProductToProduct;

/**
 * @author Godfrey
 *
 */
public interface CustomerClaimRepository extends JpaRepository<CustomerClaim, Long> {

	/**
	 * @param no
	 * @return
	 */
	Optional<CustomerClaim> findByNo(String no);
	
	@Query("SELECT MAX(c.id) FROM CustomerClaim c")
	Long getLastId();

	/**
	 * @param statuses
	 * @return
	 */
	@Query("SELECT c FROM CustomerClaim c WHERE c.status IN (:statuses)")
	List<CustomerClaim> findAllVissible(List<String> statuses);
	
	@Query("SELECT c FROM CustomerClaim c WHERE c.status =:status")
	List<CustomerClaim> findAllApproved(String status);

}

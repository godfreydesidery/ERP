/**
 * 
 */
package com.orbix.api.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orbix.api.domain.ClaimedProduct;
import com.orbix.api.domain.CustomerClaim;

/**
 * @author Godfrey
 *
 */
public interface ClaimedProductRepository extends JpaRepository<ClaimedProduct, Long> {

	/**
	 * @param customerClaim
	 * @return
	 */
	List<ClaimedProduct> findByCustomerClaim(CustomerClaim customerClaim);

}

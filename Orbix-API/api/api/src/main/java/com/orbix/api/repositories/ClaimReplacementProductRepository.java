/**
 * 
 */
package com.orbix.api.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orbix.api.domain.ClaimReplacementProduct;
import com.orbix.api.domain.CustomerClaim;
import com.orbix.api.domain.Product;

/**
 * @author Godfrey
 *
 */
public interface ClaimReplacementProductRepository extends JpaRepository<ClaimReplacementProduct, Long> {

	/**
	 * @param customerClaim
	 * @return
	 */
	List<ClaimReplacementProduct> findByCustomerClaim(CustomerClaim customerClaim);

	/**
	 * @param product
	 * @param customerClaim
	 * @return
	 */
	Optional<ClaimReplacementProduct> findByProductAndCustomerClaim(Product product, CustomerClaim customerClaim);

}

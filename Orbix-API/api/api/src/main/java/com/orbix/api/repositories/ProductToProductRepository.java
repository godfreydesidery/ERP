/**
 * 
 */
package com.orbix.api.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orbix.api.domain.ProductToMaterial;
import com.orbix.api.domain.ProductToProduct;

/**
 * @author Godfrey
 *
 */
public interface ProductToProductRepository extends JpaRepository<ProductToProduct, Long> {

	/**
	 * @param no
	 * @return
	 */
	Optional<ProductToProduct> findByNo(String no);
	
	@Query("SELECT MAX(p.id) FROM ProductToProduct p")
	Long getLastId();

	/**
	 * @param statuses
	 * @return
	 */
	@Query("SELECT p FROM ProductToProduct p WHERE p.status IN (:statuses)")
	List<ProductToProduct> findAllVissible(List<String> statuses);
	
	@Query("SELECT p FROM ProductToProduct p WHERE p.status =:status")
	List<ProductToProduct> findAllApproved(String status);

}

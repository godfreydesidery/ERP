/**
 * 
 */
package com.orbix.api.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orbix.api.domain.Drp;
import com.orbix.api.domain.DrpDetail;
import com.orbix.api.domain.Product;

/**
 * @author Godfrey
 *
 */
public interface DrpDetailRepository extends JpaRepository<DrpDetail, Long> {
	/**
	 * @param drp
	 * @return
	 */
	List<DrpDetail> findByDrp(Drp drp);

	/**
	 * @param drp
	 * @param product
	 * @return
	 */
	Optional<DrpDetail> findByDrpAndProduct(Drp drp, Product product);

}

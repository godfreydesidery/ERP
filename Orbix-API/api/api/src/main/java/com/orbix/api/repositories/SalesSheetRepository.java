/**
 * 
 */
package com.orbix.api.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orbix.api.domain.SalesList;
import com.orbix.api.domain.SalesSheet;

/**
 * @author Godfrey
 *
 */
public interface SalesSheetRepository extends JpaRepository<SalesSheet, Long> {

	/**
	 * @param salesList
	 * @return
	 */
	Optional<SalesSheet> findBySalesList(SalesList salesList);

}

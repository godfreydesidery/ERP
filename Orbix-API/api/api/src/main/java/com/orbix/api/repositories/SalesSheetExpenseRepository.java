/**
 * 
 */
package com.orbix.api.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orbix.api.domain.SalesSheet;
import com.orbix.api.domain.SalesSheetExpense;

/**
 * @author Godfrey
 *
 */
public interface SalesSheetExpenseRepository extends JpaRepository<SalesSheetExpense, Long> {

	/**
	 * @param sheet
	 * @return
	 */
	List<SalesSheetExpense> findAllBySalesSheet(SalesSheet sheet);

}

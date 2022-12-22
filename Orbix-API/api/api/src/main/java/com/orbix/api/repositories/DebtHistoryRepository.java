/**
 * 
 */
package com.orbix.api.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orbix.api.domain.DebtHistory;
import com.orbix.api.domain.DebtTracker;

/**
 * @author Godfrey
 *
 */
public interface DebtHistoryRepository extends JpaRepository<DebtHistory, Long> {

	/**
	 * @param debtTracker
	 * @return
	 */
	List<DebtHistory> findAllByDebtTracker(DebtTracker debtTracker);

}

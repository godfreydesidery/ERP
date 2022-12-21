/**
 * 
 */
package com.orbix.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orbix.api.domain.DebtHistory;

/**
 * @author Godfrey
 *
 */
public interface DebtHistoryRepository extends JpaRepository<DebtHistory, Long> {

}

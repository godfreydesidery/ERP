/**
 * 
 */
package com.orbix.api.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orbix.api.domain.Receipt;

/**
 * @author GODFREY
 *
 */
public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
	/**
	 * @param no
	 * @return
	 */
	Optional<Receipt> findByNo(String no);
}

/**
 * 
 */
package com.orbix.api.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orbix.api.domain.CartHeld;
import com.orbix.api.domain.Till;

/**
 * @author Godfrey
 *
 */
public interface CartHeldRepository extends JpaRepository<CartHeld, Long> {

	/**
	 * @param till
	 * @return
	 */
	List<CartHeld> findByTill(Till till);

}

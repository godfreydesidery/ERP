/**
 * 
 */
package com.orbix.api.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orbix.api.domain.Supplier;
import com.orbix.api.domain.VatGroup;

/**
 * @author Godfrey
 *
 */
public interface VatGroupRepository extends JpaRepository<VatGroup, Long> {
	
	/**
	 * @param code
	 * @return
	 */
	Optional<VatGroup> findByCode(String code);	
	
	@Query("SELECT v.code FROM VatGroup v")
	List<String> getCodes();
}

/**
 * 
 */
package com.orbix.api.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orbix.api.domain.MaterialToMaterial;
import com.orbix.api.domain.MaterialToMaterial;

/**
 * @author Godfrey
 *
 */
public interface MaterialToMaterialRepository extends JpaRepository<MaterialToMaterial, Long> {

	/**
	 * @param no
	 * @return
	 */
	Optional<MaterialToMaterial> findByNo(String no);
	
	@Query("SELECT MAX(p.id) FROM MaterialToMaterial p")
	Long getLastId();

	/**
	 * @param statuses
	 * @return
	 */
	@Query("SELECT p FROM MaterialToMaterial p WHERE p.status IN (:statuses)")
	List<MaterialToMaterial> findAllVissible(List<String> statuses);
	
	@Query("SELECT p FROM MaterialToMaterial p WHERE p.status =:status")
	List<MaterialToMaterial> findAllApproved(String status);
}

/**
 * 
 */
package com.orbix.api.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orbix.api.domain.MaterialToMaterialInitial;
import com.orbix.api.domain.Material;
import com.orbix.api.domain.MaterialToMaterial;

/**
 * @author Godfrey
 *
 */
public interface MaterialToMaterialInitialRepository extends JpaRepository<MaterialToMaterialInitial, Long> {
	/**
	 * @param materialToMaterial
	 * @return
	 */
	List<MaterialToMaterialInitial> findByMaterialToMaterial(MaterialToMaterial materialToMaterial);

	/**
	 * @param material
	 * @param materialToMaterial
	 * @return
	 */
	Optional<MaterialToMaterialInitial> findByMaterialAndMaterialToMaterial(Material material,
			MaterialToMaterial materialToMaterial);
}

/**
 * 
 */
package com.orbix.api.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orbix.api.domain.MaterialToMaterialFinal;
import com.orbix.api.domain.Material;
import com.orbix.api.domain.MaterialToMaterial;

/**
 * @author Godfrey
 *
 */
public interface MaterialToMaterialFinalRepository extends JpaRepository<MaterialToMaterialFinal, Long> {

	/**
	 * @param materialToMaterial
	 * @return
	 */
	List<MaterialToMaterialFinal> findByMaterialToMaterial(MaterialToMaterial materialToMaterial);

	/**
	 * @param material
	 * @param materialToMaterial
	 * @return
	 */
	Optional<MaterialToMaterialFinal> findByMaterialAndMaterialToMaterial(Material material,
			MaterialToMaterial materialToMaterial);
}

/**
 * 
 */
package com.orbix.api.service;

import java.util.List;

import com.orbix.api.domain.MaterialToMaterial;
import com.orbix.api.domain.MaterialToMaterialFinal;
import com.orbix.api.domain.MaterialToMaterialInitial;
import com.orbix.api.models.MaterialToMaterialFinalModel;
import com.orbix.api.models.MaterialToMaterialInitialModel;
import com.orbix.api.models.MaterialToMaterialModel;
import com.orbix.api.models.RecordModel;

/**
 * @author Godfrey
 *
 */
public interface MaterialToMaterialService {
	MaterialToMaterialModel save(MaterialToMaterial materialToMaterial);
	MaterialToMaterialModel get(Long id);
	MaterialToMaterialModel getByNo(String no);
	boolean delete(MaterialToMaterial materialToMaterial);
	List<MaterialToMaterialModel>getAllVisible();
	MaterialToMaterialInitialModel saveInitial(MaterialToMaterialInitial materialToMaterialInitial);
	MaterialToMaterialFinalModel saveFinal(MaterialToMaterialFinal materialToMaterialFinal);
	MaterialToMaterialInitialModel getInitial(Long id);
	MaterialToMaterialFinalModel getFinal(Long id);
	boolean deleteInitial(MaterialToMaterialInitial materialToMaterialInitial);
	boolean deleteFinal(MaterialToMaterialFinal materialToMaterialFinal);
	List<MaterialToMaterialInitialModel>getAllInitials(MaterialToMaterial materialToMaterial);
	List<MaterialToMaterialFinalModel>getAllFinals(MaterialToMaterial materialToMaterial);
	boolean archive(MaterialToMaterial materialToMaterial);
	boolean archiveAll();
	MaterialToMaterialModel post(MaterialToMaterial materialToMaterial);
	RecordModel requestMTMNo();
}

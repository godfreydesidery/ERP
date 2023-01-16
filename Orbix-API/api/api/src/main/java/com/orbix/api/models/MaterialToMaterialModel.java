/**
 * 
 */
package com.orbix.api.models;

import java.util.List;

import lombok.Data;

/**
 * @author Godfrey
 *
 */
@Data
public class MaterialToMaterialModel {
	Long id = null;
	String no = "";
	String status = "";
	String reason = "";
	String comments = "";
	String created = "";
	String approved = "";
	
	List<MaterialToMaterialInitialModel> materialToMaterialInitials;
	List<MaterialToMaterialFinalModel> materialToMaterialFinals;
}

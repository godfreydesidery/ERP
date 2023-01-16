/**
 * 
 */
package com.orbix.api.models;

import com.orbix.api.domain.Material;
import com.orbix.api.domain.MaterialToMaterial;

import lombok.Data;

/**
 * @author Godfrey
 *
 */
@Data
public class MaterialToMaterialInitialModel {
	Long id;
	double qty = 0;
	double costPriceVatIncl = 0;
	double costPriceVatExcl = 0;	
    MaterialToMaterial materialToMaterial;	
    Material material;
}

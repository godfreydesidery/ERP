/**
 * 
 */
package com.orbix.api.models;

import com.orbix.api.domain.Drp;
import com.orbix.api.domain.Product;

import lombok.Data;

/**
 * @author Godfrey
 *
 */
@Data
public class DrpDetailModel {
	Long id = null;
	double qty = 0;
	double costPriceVatIncl = 0;
	double costPriceVatExcl = 0;
	Product product = null;
	Drp drp = null;
}

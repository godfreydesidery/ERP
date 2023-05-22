/**
 * 
 */
package com.orbix.api.models;

import com.orbix.api.domain.Product;

import lombok.Data;

/**
 * @author Godfrey
 *
 */
@Data
public class WebPosModel {
	Long id = null;
	double qty = 0;
	double costPriceVatIncl = 0;
	double costPriceVatExcl = 0;
	double sellingPriceVatIncl = 0;
	double sellingPriceVatExcl = 0;
	String created = "";
	Product product = null;
}

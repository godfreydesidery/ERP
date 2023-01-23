/**
 * 
 */
package com.orbix.api.models;

import lombok.Data;

/**
 * @author Godfrey
 *
 */
@Data
public class WMSProductModel {
	Long id;
	String barcode;
	String code;
	String description;
	double sellingPriceVatIncl;
	double qty;
}

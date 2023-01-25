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
public class SalesSheetSaleDetailModel {
	Long id = null;
	String code = "";
	String barcode = "";
	String description = "";
	double qty = 0;
	double price = 0;
}

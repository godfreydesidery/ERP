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
public class SalesSheetModel {
	Long id = null;
	String no = "";
	List<SalesSheetSaleModel> salesSheetSales;
}

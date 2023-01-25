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
public class SalesSheetSaleModel {
	Long id = null;
	String no = "";
	String customerName = "";
	String customerMobile = "";
	String customerLocation = "";
	double totalAmount = 0;
	double totalPaid = 0;
	double totalDiscount = 0;
	double totalCharges = 0;
	double totalDue = 0;
	List<SalesSheetSaleDetailModel> salesSheetSaleDetails;	
}

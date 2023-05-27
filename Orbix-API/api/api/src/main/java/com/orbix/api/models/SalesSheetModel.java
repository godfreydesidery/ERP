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
	double totalSales = 0;
	double totalPaid = 0;
	double totalDiscount = 0;
	double totalCharges = 0;
	double totalDue = 0;
	
	String salesAgentName = "";
	String confirmed = "";
	List<SalesSheetSaleModel> salesSheetSales;
	List<SalesSheetExpenseModel> salesSheetExpenses;
}

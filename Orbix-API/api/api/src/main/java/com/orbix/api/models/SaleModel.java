/**
 * 
 */
package com.orbix.api.models;

import java.util.List;

import com.orbix.api.domain.Day;
import com.orbix.api.domain.Till;

import lombok.Data;

/**
 * @author GODFREY
 *
 */
@Data
public class SaleModel {
	Long id = null;	
	double salesDiscount = 0;
	double salesExpenses = 0;
	double salesCommission = 0;
	String created = "";	
    Day day = null;
    Till till = null;
    String reference = "";
    List<SaleDetailModel> saleDetails;
}

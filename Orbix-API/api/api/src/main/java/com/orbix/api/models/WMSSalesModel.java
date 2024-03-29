/**
 * 
 */
package com.orbix.api.models;

import java.util.List;

import com.orbix.api.domain.Day;
import com.orbix.api.domain.Product;
import com.orbix.api.domain.Till;

import lombok.Data;

/**
 * @author Godfrey
 *
 */
@Data
public class WMSSalesModel {
	Long id = null;	
	String no = "";
	String salesListNo = "";
	String customerName = "";	
	String customerMobile = "";
	String customerLocation = "";
	double totalAmount = 0;
	double totalDiscount = 0;
	double totalCharges = 0;
	double totalPaid = 0;
	double totalDue = 0;
    List<WMSProductModel> products;
}

/**
 * 
 */
package com.orbix.api.models;

import java.time.LocalDate;
import java.util.List;

import com.orbix.api.domain.Customer;

import lombok.Data;

/**
 * @author GODFREY
 *
 */
@Data
public class QuotationModel {
	Long id = null;
	String no = "";
	String status;
	LocalDate quotationDate;
	String comments;
	Customer customer = null;
	String created = "";
	String approved = "";
	
	String billingAddress = "";
	String shippingAddress = "";
	double totalVat = 0;
	double amountVatExcl = 0;
	double amountVatIncl = 0;
	double discount = 0;
	double otherCharges = 0;
	double netAmount = 0;
	
	List<QuotationDetailModel> quotationDetails;	
}

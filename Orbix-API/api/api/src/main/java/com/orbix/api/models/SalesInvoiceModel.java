/**
 * 
 */
package com.orbix.api.models;

import java.time.LocalDate;
import java.util.List;

import com.orbix.api.domain.Customer;
import com.orbix.api.domain.SalesAgent;

import lombok.Data;

/**
 * @author GODFREY
 *
 */
@Data
public class SalesInvoiceModel {
	Long id = null;
	String no = "";
	String status = "";
	String comments = "";
	double balance = 0;
	Customer customer = null;
	SalesAgent salesAgent = null;
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
	
	List<SalesInvoiceDetailModel> salesInvoiceDetails;	
}

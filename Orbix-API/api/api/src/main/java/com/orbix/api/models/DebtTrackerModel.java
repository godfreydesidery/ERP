/**
 * 
 */
package com.orbix.api.models;

import com.orbix.api.domain.Customer;
import com.orbix.api.domain.Day;
import com.orbix.api.domain.Debt;
import com.orbix.api.domain.SalesAgent;
import com.orbix.api.domain.SalesInvoice;

import lombok.Data;

/**
 * @author Godfrey
 *
 */
@Data
public class DebtTrackerModel {
	Long id = null;
    String no = "";
    String status = "";
	double amount = 0;
	double paid = 0;
	double balance = 0;	
	String comments = "";
    String created = "";
    SalesAgent officerIncharge = null;
    Day inceptionDay = null;
    Customer customer = null;
    Debt debt = null;   
    SalesInvoice salesInvoice = null;   
}

/**
 * 
 */
package com.orbix.api.models;

import com.orbix.api.domain.PackingList;
import com.orbix.api.domain.SalesAgent;

import lombok.Data;

/**
 * @author GODFREY
 *
 */
@Data
public class DebtModel {
    Long id = null;
    String no = "";
    String status = "";
	double amount = 0;
	double balance = 0;	
    String created = "";
    SalesAgent salesAgent = null;
    PackingList packingList = null;
}

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
 * @author Godfrey
 *
 */
@Data
public class SalesExpenseModel {
	Long id = null;
	String description = "";	
	double amount = 0;	
}

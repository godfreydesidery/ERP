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
public class WMSExpenseModel {
	Long id = null;	
	String salesListNo = "";
	String description = "";
	double amount = 0;
}

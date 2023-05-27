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
public class SalesSheetExpenseModel {
	Long id = null;
	String description = "";
	double amount = 0;
}

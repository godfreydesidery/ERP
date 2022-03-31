/**
 * 
 */
package com.orbix.api.reports.models;

import java.time.LocalDate;

import com.orbix.api.domain.Product;

/**
 * @author Godfrey
 *
 */
public interface ProductStockCardReport {
	LocalDate getDate();
	String getCode();
	String getDescription();
	double getQtyIn();
	double getQtyOut();
	double getBalance();
	String getReference();
}

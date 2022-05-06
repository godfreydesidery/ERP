/**
 * 
 */
package com.orbix.api.reports.service;

import java.time.LocalDate;
import java.util.List;

import com.orbix.api.domain.Product;
import com.orbix.api.domain.Supplier;
import com.orbix.api.reports.models.LpoReport;
import com.orbix.api.reports.models.NegativeStockReport;

/**
 * @author Godfrey
 *
 */
public interface LpoReportService {
	List<LpoReport> getLpoReport(
			LocalDate from,
			LocalDate to,
			Supplier supplier,
			List<Product> products);	
}

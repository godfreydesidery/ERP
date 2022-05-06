/**
 * 
 */
package com.orbix.api.reports.service;

import java.time.LocalDate;
import java.util.List;

import com.orbix.api.domain.Product;
import com.orbix.api.domain.Supplier;
import com.orbix.api.reports.models.GrnReport;
import com.orbix.api.reports.models.LpoReport;

/**
 * @author Godfrey
 *
 */
public interface GrnReportService {
	List<GrnReport> getGrnReport(
			LocalDate from,
			LocalDate to,
			Supplier supplier,
			List<Product> products);	
}

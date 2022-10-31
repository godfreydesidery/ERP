/**
 * 
 */
package com.orbix.api.reports.service;

import java.time.LocalDate;
import java.util.List;

import com.orbix.api.reports.models.ProductListingReport;

/**
 * @author Godfrey
 *
 */
public interface ProductListingReportService {
	List<ProductListingReport> getProductListingReport(
			LocalDate from,
			LocalDate to,
			String tillNo);
}

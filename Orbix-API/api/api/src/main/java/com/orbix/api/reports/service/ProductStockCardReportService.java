/**
 * 
 */
package com.orbix.api.reports.service;

import java.time.LocalDate;
import java.util.List;

import com.orbix.api.reports.models.ProductStockCardReport;
import com.orbix.api.reports.models.ProductionReport;

/**
 * @author Godfrey
 *
 */
public interface ProductStockCardReportService {
	List<ProductStockCardReport> getProductStockCardReport(
			LocalDate from,
			LocalDate to);
}

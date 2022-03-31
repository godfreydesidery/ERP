/**
 * 
 */
package com.orbix.api.reports.service;

import java.time.LocalDate;
import java.util.List;

import com.orbix.api.reports.models.FastMovingProductsReport;
import com.orbix.api.reports.models.SlowMovingProductsReport;

/**
 * @author Godfrey
 *
 */
public interface SlowMovingProductsReportService {
	List<SlowMovingProductsReport> getSlowMovingProductsReport(
			LocalDate from,
			LocalDate to);
}

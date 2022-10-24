/**
 * 
 */
package com.orbix.api.reports.service;

import java.time.LocalDate;
import java.util.List;

import com.orbix.api.reports.models.DailyProductionReport;
import com.orbix.api.reports.models.MaterialUsageReport;

/**
 * @author GODFREY
 *
 */
public interface ProductionReportService {
	List<DailyProductionReport> getDailyProductionReport(
			LocalDate from,
			LocalDate to);
	List<MaterialUsageReport> getMaterialUsageReport(
			LocalDate from,
			LocalDate to);
}

/**
 * 
 */
package com.orbix.api.reports.service;

import java.time.LocalDate;
import java.util.List;

import com.orbix.api.reports.models.DailyProductionReport;

/**
 * @author GODFREY
 *
 */
public interface ProductionReportService {
	List<DailyProductionReport> getDailyProductionReport(
			LocalDate from,
			LocalDate to);
}

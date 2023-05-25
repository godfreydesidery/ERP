/**
 * 
 */
package com.orbix.api.reports.service;

import java.time.LocalDate;
import java.util.List;

import com.orbix.api.domain.Product;
import com.orbix.api.domain.Supplier;
import com.orbix.api.reports.models.DebtTrackerReport;
import com.orbix.api.reports.models.GrnReport;

/**
 * @author Godfrey
 *
 */
public interface DebtTrackerReportService {
	
	List<DebtTrackerReport> getDebtTrackerReport(
			LocalDate from,
			LocalDate to);
}

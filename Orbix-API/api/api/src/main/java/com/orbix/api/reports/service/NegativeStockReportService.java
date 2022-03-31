/**
 * 
 */
package com.orbix.api.reports.service;

import java.time.LocalDate;
import java.util.List;

import com.orbix.api.reports.models.NegativeStockReport;
import com.orbix.api.reports.models.ProductStockCardReport;

/**
 * @author Godfrey
 *
 */
public interface NegativeStockReportService {
	List<NegativeStockReport> getNegativeStockReport();		
}

/**
 * 
 */
package com.orbix.api.reports.service;

import java.time.LocalDate;
import java.util.List;

import com.orbix.api.domain.Department;
import com.orbix.api.domain.LevelFour;
import com.orbix.api.domain.LevelOne;
import com.orbix.api.domain.LevelThree;
import com.orbix.api.domain.LevelTwo;
import com.orbix.api.domain.Product;
import com.orbix.api.domain.SubCategory;
import com.orbix.api.domain.SubClass;
import com.orbix.api.domain.Supplier;
import com.orbix.api.domain.Category;
import com.orbix.api.domain.Class;
import com.orbix.api.reports.models.DailySalesReport;
import com.orbix.api.reports.models.DailySummaryReport;
import com.orbix.api.reports.models.SupplySalesReport;

/**
 * @author GODFREY
 *
 */
public interface SalesReportService {
	List<DailySalesReport> getDailySalesReport(
			LocalDate fromDate,
			LocalDate toDate,
			String salesAgentName,
			Department department,
			Class clas,
			SubClass subClass,
			Category category,
			SubCategory subCategory,
			List<LevelOne> levelOnes,
			List<LevelTwo> levelTwos,
			List<LevelThree> levelThrees,
			List<LevelFour> levelFours);
	
	List<DailySummaryReport> getDailySummaryReport(
			LocalDate fromDate,
			LocalDate toDate);
			
	
	List<SupplySalesReport> getSupplySalesReport(
			LocalDate from,
			LocalDate to,
			String salesAgentName,
			Supplier supplier,
			List<Product> products);
	
}

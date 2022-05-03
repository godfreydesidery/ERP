/**
 * 
 */
package com.orbix.api.reports.service;

import java.time.LocalDate;
import java.util.List;

import com.orbix.api.domain.Category;
import com.orbix.api.domain.Class;
import com.orbix.api.domain.Department;
import com.orbix.api.domain.LevelFour;
import com.orbix.api.domain.LevelOne;
import com.orbix.api.domain.LevelThree;
import com.orbix.api.domain.LevelTwo;
import com.orbix.api.domain.Product;
import com.orbix.api.domain.SubCategory;
import com.orbix.api.domain.SubClass;
import com.orbix.api.domain.Supplier;
import com.orbix.api.reports.models.DailySalesReport;
import com.orbix.api.reports.models.ProductStockCardReport;
import com.orbix.api.reports.models.SupplierStockStatusReport;

/**
 * @author Godfrey
 *
 */
public interface SupplierStockStatusReportService {
	List<SupplierStockStatusReport> getSupplierStockStatusReport(
			Supplier supplier,
			List<Product> products);
	
	
	
	//List<SupplierStockStatusReport> getSupplierStockStatusReportBySupplier(String name);
	//List<SupplierStockStatusReport> getSupplierStockStatusReportByProducts(List<Product> products);
	//List<SupplierStockStatusReport> getSupplierStockStatusReportAll();
}

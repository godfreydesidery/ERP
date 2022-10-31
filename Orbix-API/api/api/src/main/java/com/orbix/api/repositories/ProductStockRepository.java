/**
 * 
 */
package com.orbix.api.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orbix.api.domain.Day;
import com.orbix.api.domain.Product;
import com.orbix.api.domain.ProductStock;
import com.orbix.api.reports.models.NegativeStockReport;
import com.orbix.api.reports.models.ProductStockSummaryReport;
import com.orbix.api.reports.models.SupplySalesReport;

/**
 * @author GODFREY
 *
 */
public interface ProductStockRepository extends JpaRepository<ProductStock, Long> {

	/**
	 * @param p
	 * @param day
	 * @return
	 */
	Optional<ProductStock> findByProductAndDay(Product p, Day day);
	
	@Query(
			value = "SELECT\r\n" + 
					"`days`.`bussiness_date` AS `date`,\r\n" + 
					"SUM(CASE WHEN `product_stocks`.`opening_stock` > 0 THEN `product_stocks`.`opening_stock`*`product_stocks`.`selling_price_vat_incl` ELSE 0 END) AS `openingStockValue`,\r\n" + 
					"SUM(CASE WHEN `product_stocks`.`closing_stock` > 0 THEN `product_stocks`.`closing_stock`*`product_stocks`.`selling_price_vat_incl` ELSE 0 END) AS `closingStockValue`\r\n" + 
					"FROM\r\n" + 
					"(SELECT * FROM `days` WHERE `bussiness_date` BETWEEN :from AND :to)`days`\r\n" + 
					"LEFT JOIN\r\n" + 
					"`product_stocks`\r\n" + 
					"ON\r\n" + 
					"`days`.`id`=`product_stocks`.`day_id`\r\n" + 
					"GROUP BY\r\n" + 
					"`date`",
					nativeQuery = true					
			)
	List<ProductStockSummaryReport> getProductStockSummaryReport(LocalDate from, LocalDate to);
	
}

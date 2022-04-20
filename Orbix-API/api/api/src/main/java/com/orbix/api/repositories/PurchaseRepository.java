/**
 * 
 */
package com.orbix.api.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orbix.api.domain.Purchase;
import com.orbix.api.reports.models.DailyPurchaseReport;
import com.orbix.api.reports.models.DailySalesReport;

/**
 * @author Godfrey
 *
 */
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
	
	
	@Query(
			value = "SELECT\r\n" + 
					"`days`.`bussiness_date` AS `date`,\r\n" + 
					"SUM(`purchase_details`.`qty`*`purchase_details`.`cost_price_vat_incl`) AS `amount`\r\n" + 
					"FROM\r\n" + 
					"(SELECT * FROM `days` WHERE `bussiness_date` BETWEEN :from AND :to)`days`\r\n" + 
					"JOIN\r\n" + 
					"`purchases`\r\n" + 
					"ON\r\n" + 
					"`days`.`id`=`purchases`.`day_id`\r\n" + 
					"JOIN\r\n" + 
					"`purchase_details`\r\n" + 
					"ON\r\n" + 
					"`purchase_details`.`purchase_id`=`purchases`.`id`\r\n" + 
					"GROUP BY\r\n" + 
					"`date`",
					nativeQuery = true					
			)
	List<DailyPurchaseReport> getDailyPurchaseReport(LocalDate from, LocalDate to);
}

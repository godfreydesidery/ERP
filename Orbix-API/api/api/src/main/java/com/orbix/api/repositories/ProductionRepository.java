/**
 * 
 */
package com.orbix.api.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orbix.api.domain.Lpo;
import com.orbix.api.domain.Production;
import com.orbix.api.reports.models.DailyProductionReport;
import com.orbix.api.reports.models.MaterialUsageReport;
import com.orbix.api.reports.models.SupplySalesReport;

/**
 * @author GODFREY
 *
 */
public interface ProductionRepository extends JpaRepository<Production, Long> {

	/**
	 * @param no
	 * @return
	 */
	Optional<Production> findByNo(String no);
	
	@Query("SELECT p FROM Production p WHERE p.status IN (:statuses)")
	List<Production> findAllVissible(List<String> statuses);
	
	@Query("SELECT MAX(p.id) FROM Production p")
	Long getLastId();
	
	@Query(
			value = "SELECT \r\n" +
					"`days`.`bussiness_date` AS `date`,\r\n" + 
					"`products`.`code` AS `code`,\r\n" + 
					"`products`.`description` AS `description`,\r\n" + 
					"SUM(`production_products`.`qty`*`production_products`.`selling_price_vat_incl`) AS `amount`,\r\n" +
					"SUM(`production_products`.`qty`) AS `qty`\r\n" + 
					"FROM\r\n" + 
					"`production_products`\r\n" + 
					"JOIN `days` ON\r\n" + 
					"`days`.`id`=`production_products`.`verified_at`\r\n" + 
					"JOIN `products` ON\r\n" + 
					"`production_products`.`product_id`=`products`.`id`\r\n" + 				
					"WHERE\r\n" + 
					"`days`.`bussiness_date` BETWEEN :from AND :to \r\n" + 
					"GROUP BY `code`,`date`\r\n" +
					"ORDER BY `date`",
					nativeQuery = true					
			)
	List<DailyProductionReport> getDailyProductionReport(LocalDate from, LocalDate to);
	
	@Query(
			value = "SELECT \r\n" +
					"`days`.`bussiness_date` AS `date`,\r\n" + 
					"`materials`.`code` AS `code`,\r\n" + 
					"`materials`.`description` AS `description`,\r\n" + 
					"SUM(`production_materials`.`qty`*`production_materials`.`cost_price_vat_incl`) AS `amount`,\r\n" +
					"SUM(`production_materials`.`qty`) AS `qty`\r\n" + 
					"FROM\r\n" + 
					"`production_materials`\r\n" + 
					"JOIN `days` ON\r\n" + 
					"`days`.`id`=`production_materials`.`verified_at`\r\n" + 
					"JOIN `materials` ON\r\n" + 
					"`production_materials`.`material_id`=`materials`.`id`\r\n" + 				
					"WHERE\r\n" + 
					"`days`.`bussiness_date` BETWEEN :from AND :to \r\n" + 
					"GROUP BY `code`,`date`\r\n" +
					"ORDER BY `date`",
					nativeQuery = true					
			)
	List<MaterialUsageReport> getMaterialUsageReport(LocalDate from, LocalDate to);


}

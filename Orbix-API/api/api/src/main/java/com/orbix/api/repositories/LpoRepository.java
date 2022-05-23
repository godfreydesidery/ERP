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
import com.orbix.api.reports.models.LpoReport;
import com.orbix.api.reports.models.ProductListingReport;

/**
 * @author GODFREY
 *
 */
public interface LpoRepository extends JpaRepository<Lpo, Long> {

	/**
	 * @param no
	 * @return
	 */
	Optional<Lpo> findByNo(String no);
	
	@Query("SELECT MAX(l.id) FROM Lpo l")
	Long getLastId();
	
	@Query("SELECT l FROM Lpo l WHERE l.status IN (:statuses)")
	List<Lpo> findAllVissible(List<String> statuses);
	
	@Query("SELECT l FROM Lpo l WHERE l.status =:status")
	List<Lpo> findAllReceived(String status);

	/**
	 * @return
	 */
	Lpo findTopByOrderByIdDesc();
	
	
	
	
	
	
	
	
	
	
	@Query(
			value = "SELECT\r\n" + 
					"`days`.`bussiness_date` AS `date`,\r\n" + 
					"`products`.`code` AS `code`,\r\n" + 
					"`products`.`description` AS `description`,\r\n" + 
					"`suppliers`.`name` AS `supplierName`,\r\n" +
					"`lpo_details`.`qty` AS `qty`,\r\n" + 
					"`lpo_details`.`qty`*`lpo_details`.`cost_price_vat_incl` AS `amount`,\r\n" + 
					"`lpos`.`no` AS `lpoNo`\r\n" + 					
					"FROM\r\n" + 
					"`lpos`\r\n" + 
					"JOIN `days` ON\r\n" + 
					"`days`.`id`=`lpos`.`approved_at`\r\n" +
					"JOIN `lpo_details` ON\r\n" + 
					"`lpo_details`.`lpo_id`=`lpos`.`id`\r\n" + 															
					"JOIN `products` ON\r\n" + 
					"`products`.`id`=`lpo_details`.`product_id`\r\n" +
					"JOIN `suppliers` ON\r\n" + 
					"`suppliers`.`id`=`products`.`supplier_id`\r\n" +
					"WHERE\r\n" +
					"`days`.`bussiness_date` BETWEEN :from AND :to AND `lpos`.`status` IN ('APPROVED', 'PRINTED')\r\n" + 					
					"ORDER BY\r\n" + 
					"`date` ASC",
					nativeQuery = true					
			)
	List<LpoReport> getLpoReportAll(LocalDate from, LocalDate to);
	
	@Query(
			value = "SELECT\r\n" + 
					"`days`.`bussiness_date` AS `date`,\r\n" + 
					"`products`.`code` AS `code`,\r\n" + 
					"`products`.`description` AS `description`,\r\n" + 
					"`suppliers`.`name` AS `supplierName`,\r\n" +
					"`lpo_details`.`qty` AS `qty`,\r\n" + 
					"`lpo_details`.`qty`*`lpo_details`.`cost_price_vat_incl` AS `amount`,\r\n" + 
					"`lpos`.`no` AS `lpoNo`\r\n" + 					
					"FROM\r\n" + 
					"`lpos`\r\n" + 
					"JOIN `days` ON\r\n" + 
					"`days`.`id`=`lpos`.`approved_at`\r\n" +
					"JOIN `lpo_details` ON\r\n" + 
					"`lpo_details`.`lpo_id`=`lpos`.`id`\r\n" + 															
					"JOIN `products` ON\r\n" + 
					"`products`.`id`=`lpo_details`.`product_id`\r\n" +
					"JOIN `suppliers` ON\r\n" + 
					"`suppliers`.`id`=`products`.`supplier_id`\r\n" +
					"WHERE\r\n" +
					"`days`.`bussiness_date` BETWEEN :from AND :to AND `products`.`code` IN (:codes) AND `lpos`.`status` IN ('APPROVED', 'PRINTED')\r\n" + 					
					"ORDER BY\r\n" + 
					"`date` ASC",
					nativeQuery = true					
			)
	List<LpoReport> getLpoReportByProducts(LocalDate from, LocalDate to, List<String> codes);
	
	
}

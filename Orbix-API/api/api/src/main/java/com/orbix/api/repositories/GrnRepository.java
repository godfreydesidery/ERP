/**
 * 
 */
package com.orbix.api.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orbix.api.domain.Grn;
import com.orbix.api.domain.Lpo;
import com.orbix.api.reports.models.GrnReport;
import com.orbix.api.reports.models.LpoReport;

/**
 * @author GODFREY
 *
 */
public interface GrnRepository extends JpaRepository<Grn, Long> {

	/**
	 * @param lpo
	 * @return
	 */
	Optional<Grn> findByLpo(Lpo lpo);

	/**
	 * @param no
	 * @return
	 */
	Optional<Grn> findByNo(String no);
	
	@Query("SELECT g FROM Grn g WHERE g.status IN (:statuses)")
	List<Grn> findAllVissible(List<String> statuses);
	
	@Query("SELECT g FROM Grn g WHERE g.status =:status")
	List<Grn> findAllReceived(String status);
	
	@Query("SELECT MAX(g.id) FROM Grn g")
	Long getLastId();
	
	
	
	
	
	
	
	
	@Query(
			value = "SELECT\r\n" + 
					"`days`.`bussiness_date` AS `date`,\r\n" + 
					"`products`.`code` AS `code`,\r\n" + 
					"`products`.`description` AS `description`,\r\n" + 
					"`suppliers`.`name` AS `supplierName`,\r\n" +
					"`grn_details`.`qty_received` AS `qty`,\r\n" + 
					"`grn_details`.`qty_received`*`grn_details`.`supplier_price_vat_incl` AS `amount`,\r\n" + 
					"`grns`.`no` AS `grnNo`\r\n" + 					
					"FROM\r\n" + 
					"`grns`\r\n" + 
					"JOIN `days` ON\r\n" + 
					"`days`.`id`=`grns`.`approved_at`\r\n" +
					"JOIN `grn_details` ON\r\n" + 
					"`grn_details`.`grn_id`=`grns`.`id`\r\n" + 															
					"JOIN `products` ON\r\n" + 
					"`products`.`id`=`grn_details`.`product_id`\r\n" +
					"JOIN `suppliers` ON\r\n" + 
					"`suppliers`.`id`=`products`.`supplier_id`\r\n" +
					"WHERE\r\n" +
					"`days`.`bussiness_date` BETWEEN :from AND :to\r\n" + 
					//"GROUP BY `date`,`code`,`description`,`receiptNo`,`cashier`,`tillNo`\r\n" + 
					//"GROUP BY `date`,`code`,`description`\r\n" + 
					"ORDER BY\r\n" + 
					"`date` ASC",
					nativeQuery = true					
			)
	List<GrnReport> getGrnReportAll(LocalDate from, LocalDate to);
	
	@Query(
			value = "SELECT\r\n" + 
					"`days`.`bussiness_date` AS `date`,\r\n" + 
					"`products`.`code` AS `code`,\r\n" + 
					"`products`.`description` AS `description`,\r\n" + 
					"`suppliers`.`name` AS `supplierName`,\r\n" +
					"`grn_details`.`qty_received` AS `qty`,\r\n" + 
					"`grn_details`.`qty_received`*`grn_details`.`supplier_price_vat_incl` AS `amount`,\r\n" + 
					"`grns`.`no` AS `grnNo`\r\n" + 					
					"FROM\r\n" + 
					"`grns`\r\n" + 
					"JOIN `days` ON\r\n" + 
					"`days`.`id`=`grns`.`approved_at`\r\n" +
					"JOIN `grn_details` ON\r\n" + 
					"`grn_details`.`grn_id`=`grns`.`id`\r\n" + 															
					"JOIN `products` ON\r\n" + 
					"`products`.`id`=`grn_details`.`product_id`\r\n" +
					"JOIN `suppliers` ON\r\n" + 
					"`suppliers`.`id`=`products`.`supplier_id`\r\n" +
					"WHERE\r\n" +
					"`days`.`bussiness_date` BETWEEN :from AND :to AND `products`.`code` IN (:codes)\r\n" + 				
					"ORDER BY\r\n" + 
					"`date` ASC",
					nativeQuery = true					
			)
	List<GrnReport> getGrnReportByProducts(LocalDate from, LocalDate to, List<String> codes);
	

}

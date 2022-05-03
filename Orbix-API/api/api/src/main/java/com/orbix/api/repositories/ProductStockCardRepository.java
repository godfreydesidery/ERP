/**
 * 
 */
package com.orbix.api.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orbix.api.domain.ProductStockCard;
import com.orbix.api.reports.models.DailySalesReport;
import com.orbix.api.reports.models.ProductStockCardReport;

/**
 * @author GODFREY
 *
 */
public interface ProductStockCardRepository extends JpaRepository<ProductStockCard, Long> {
	
	@Query(
			value = "SELECT\r\n" + 
					"`days`.`bussiness_date` AS `date`,\r\n" +
					"`products`.`code` AS `code`,\r\n" +
					"`products`.`description` AS `description`,\r\n" +
					"`product_stock_cards`.`qty_in` AS `qtyIn`,\r\n" +
					"`product_stock_cards`.`qty_out` AS `qtyOut`,\r\n" +
					"`product_stock_cards`.`balance` AS `balance`,\r\n" +
					"`product_stock_cards`.`reference` AS `reference`\r\n" +
					"FROM\r\n" + 
					"(SELECT * FROM `days` WHERE `bussiness_date` BETWEEN :from AND :to)`days`\r\n" + 
					"JOIN\r\n" + 
					"`product_stock_cards`\r\n" + 
					"ON\r\n" + 
					"`days`.`id`=`product_stock_cards`.`day_id`\r\n" + 
					"JOIN\r\n" + 
					"`products`\r\n" + 
					"ON\r\n" + 
					"`product_stock_cards`.`product_id`=`products`.`id`\r\n",
					nativeQuery = true					
			)
	List<ProductStockCardReport> getStockCardReportAll(LocalDate from, LocalDate to);
	
	@Query(
			value = "SELECT\r\n" + 
					"`days`.`bussiness_date` AS `date`,\r\n" +
					"`suppliers`.`name` AS `supplierName`,\r\n" +
					"`products`.`code` AS `code`,\r\n" +
					"`products`.`description` AS `description`,\r\n" +
					"`product_stock_cards`.`qty_in` AS `qtyIn`,\r\n" +
					"`product_stock_cards`.`qty_out` AS `qtyOut`,\r\n" +
					"`product_stock_cards`.`balance` AS `balance`,\r\n" +
					"`product_stock_cards`.`reference` AS `reference`\r\n" +
					"FROM\r\n" + 
					"(SELECT * FROM `days` WHERE `bussiness_date` BETWEEN :from AND :to)`days`\r\n" + 
					"JOIN\r\n" + 
					"`product_stock_cards`\r\n" + 
					"ON\r\n" + 
					"`days`.`id`=`product_stock_cards`.`day_id`\r\n" + 
					"JOIN\r\n" + 
					"`products`\r\n" + 
					"ON\r\n" + 
					"`product_stock_cards`.`product_id`=`products`.`id`\r\n" +
					"JOIN\r\n" + 
					"`suppliers`\r\n" + 
					"ON\r\n" + 
					"`products`.`supplier_id`=`suppliers`.`id`\r\n" +
					"WHERE\r\n" + 
					"`suppliers`.`name`=:supplierName\r\n",  
					nativeQuery = true					
			)
	List<ProductStockCardReport> getStockCardReportBySupplier(LocalDate from, LocalDate to, String supplierName);
	
	@Query(
			value = "SELECT\r\n" + 
					"`days`.`bussiness_date` AS `date`,\r\n" +
					"`products`.`code` AS `code`,\r\n" +
					"`products`.`description` AS `description`,\r\n" +
					"`product_stock_cards`.`qty_in` AS `qtyIn`,\r\n" +
					"`product_stock_cards`.`qty_out` AS `qtyOut`,\r\n" +
					"`product_stock_cards`.`balance` AS `balance`,\r\n" +
					"`product_stock_cards`.`reference` AS `reference`\r\n" +
					"FROM\r\n" + 
					"(SELECT * FROM `days` WHERE `bussiness_date` BETWEEN :from AND :to)`days`\r\n" + 
					"JOIN\r\n" + 
					"`product_stock_cards`\r\n" + 
					"ON\r\n" + 
					"`days`.`id`=`product_stock_cards`.`day_id`\r\n" + 
					"JOIN\r\n" + 
					"`products`\r\n" + 
					"ON\r\n" + 
					"`product_stock_cards`.`product_id`=`products`.`id`\r\n" +
					"WHERE\r\n" + 
					"`products`.`code` IN (:codes)\r\n",
					nativeQuery = true					
			)
	List<ProductStockCardReport> getStockCardReportByProduct(LocalDate from, LocalDate to, List<String> codes);
}

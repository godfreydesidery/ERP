/**
 * 
 */
package com.orbix.api.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.orbix.api.domain.Product;
import com.orbix.api.domain.Sale;
import com.orbix.api.reports.models.DailySalesReport;
import com.orbix.api.reports.models.DailySummaryReport;
import com.orbix.api.reports.models.FastMovingProductsReport;
import com.orbix.api.reports.models.ProductListingReport;
import com.orbix.api.reports.models.SlowMovingProductsReport;
import com.orbix.api.reports.models.SupplySalesReport;

/**
 * @author GODFREY
 *
 */
@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
	
	/*@Query("SELECT day.bussinessDate AS date, "
			+ "SUM(saleDetail.qty*saleDetail.sellingPriceVatIncl) AS amount, "
			+ "SUM(saleDetail.qty*saleDetail.discount) AS discount, "
			+ "SUM(saleDetail.qty*saleDetail.tax) AS tax "
			+ "FROM "
			+ "(SELECT d FROM Day d WHERE d.bussinessDate BETWEEN :from AND :to) Day day "
			+ "JOIN "
			+ "Sale sale "
			+ "ON "
			+ "day=sale.day "
			+ "JOIN "
			+ "SaleDetail saleDetail "
			+ "ON "
			+ "saleDetail.sale=sale "
			+ "GROUP BY "
			+ "date")*/
	@Query(
			value = "SELECT\r\n" + 
					"`days`.`bussiness_date` AS `date`,\r\n" + 
					"SUM(`sale_details`.`qty`*`sale_details`.`selling_price_vat_incl`) AS `amount`,\r\n" + 
					"SUM(`sale_details`.`qty`*`sale_details`.`discount`) AS `discount`,\r\n" + 
					"SUM(`sale_details`.`qty`*`sale_details`.`tax`) AS `tax`\r\n" + 
					"FROM\r\n" + 
					"(SELECT * FROM `days` WHERE `bussiness_date` BETWEEN :from AND :to)`days`\r\n" + 
					"JOIN\r\n" + 
					"`sales`\r\n" + 
					"ON\r\n" + 
					"`days`.`id`=`sales`.`day_id`\r\n" + 
					"JOIN\r\n" + 
					"`sale_details`\r\n" + 
					"ON\r\n" + 
					"`sale_details`.`sale_id`=`sales`.`id`\r\n" + 
					"GROUP BY\r\n" + 
					"`date`",
					nativeQuery = true					
			)
	List<DailySalesReport> getDailySalesReport(LocalDate from, LocalDate to);
	
	@Query(
			value = "SELECT\r\n" + 
					"`days`.`bussiness_date` AS `date`,\r\n" + 
					"`product_stocks`.`opening_stock`*`product_stocks`.`selling_price_vat_incl` AS `openingStockValue`,\r\n" +
					"`product_stocks`.`closing_stock`*`product_stocks`.`selling_price_vat_incl` AS `closingStockValue`,\r\n" +
					"SUM(CASE WHEN `grns`.`status`='RECEIVED' OR `grns`.`status`='ARCHIVED' THEN `grns`.`invoice_amount` ELSE 0 END) AS `purchaseOnCredit`,\r\n" +
					"SUM(CASE WHEN `sales_receipts`.`status`='APPROVED' OR `sales_receipts`.`status`='ARCHIVED' THEN `sales_receipts`.`amount` ELSE 0 END) AS `amountPaid`,\r\n" +
					"SUM(CASE WHEN `sales`.`type`='Cash' THEN `sale_details`.`qty`*`sale_details`.`selling_price_vat_incl` ELSE 0 END) AS `cashSales`,\r\n" +
					"SUM(CASE WHEN `sales`.`type`='Credit' THEN `sale_details`.`qty`*`sale_details`.`selling_price_vat_incl` ELSE 0 END) AS `creditSales`,\r\n" +
					"SUM(CASE WHEN `sale_details`.`qty` > 0 THEN `sale_details`.`qty`*(`sale_details`.`selling_price_vat_incl` - `sale_details`.`cost_price_vat_incl`) ELSE 0 END) AS `grossMargin`\r\n" +
					"FROM\r\n" + 
					"(SELECT * FROM `days` WHERE `bussiness_date` BETWEEN :from AND :to)`days`\r\n" + 
					"LEFT JOIN\r\n" + 
					"`sales`\r\n" + 
					"ON\r\n" + 
					"`days`.`id`=`sales`.`day_id`\r\n" + 
					"LEFT JOIN\r\n" + 
					"`sale_details`\r\n" + 
					"ON\r\n" + 
					"`sale_details`.`sale_id`=`sales`.`id`\r\n" + 
					"LEFT JOIN\r\n" + 
					"`product_stocks`\r\n" + 
					"ON\r\n" + 
					"`product_stocks`.`day_id`=`days`.`id`\r\n" + 
					"LEFT JOIN\r\n" + 
					"`grns`\r\n" + 
					"ON\r\n" + 
					"`grns`.`approved_at`=`days`.`id`\r\n" + 
					"LEFT JOIN\r\n" + 
					"`sales_receipts`\r\n" + 
					"ON\r\n" + 
					"`sales_receipts`.`approved_at`=`days`.`id`\r\n" + 
					"GROUP BY\r\n" + 
					"`date`",
					nativeQuery = true					
			)
	List<DailySummaryReport> getDailySummaryReport(LocalDate from, LocalDate to);
	
	@Query(
			value = "SELECT\r\n" + 
					"`days`.`bussiness_date` AS `date`,\r\n" + 
					"`products`.`code` AS `code`,\r\n" + 
					"`products`.`description` AS `description`,\r\n" + 
					"`sale_details`.`qty` AS `qty`,\r\n" + 
					"`sale_details`.`qty`*`sale_details`.`selling_price_vat_incl` AS `amount`,\r\n" + 
					"`users`.`alias` AS `cashier`,\r\n" + 
					"`receipts`.`no` AS `receiptNo`,\r\n" + 
					"`sales_invoices`.`no` as `invoiceNo`,\r\n" + 
					"`tills`.`no` AS `tillNo`\r\n" + 
					"FROM\r\n" + 
					"`sales`\r\n" + 
					"JOIN `days` ON\r\n" + 
					"`days`.`id`=`sales`.`day_id`\r\n" +
					"JOIN `sale_details` ON\r\n" + 
					"`sale_details`.`sale_id`=`sales`.`id`\r\n" + 
					"LEFT JOIN `users` ON\r\n" + 
					"`users`.`id`=`sales`.`created_by`\r\n" + 
					"LEFT JOIN `receipts` ON\r\n" + 
					"`receipts`.`id`=`sales`.`receipt_id`\r\n" + 
					"LEFT JOIN `sales_invoices` ON\r\n" + 
					"`sales_invoices`.`id` =`sales`.`sales_invoice_id`\r\n" + 
					"LEFT JOIN `tills` ON\r\n" + 
					"`tills`.`id`=`sales`.`till_id`\r\n" + 
					"JOIN `products` ON\r\n" + 
					"`products`.`id`=`sale_details`.`product_id`\r\n" +
					"WHERE\r\n" +
					"`days`.`bussiness_date` BETWEEN :from AND :to\r\n" + 
					//"GROUP BY `date`,`code`,`description`,`receiptNo`,`cashier`,`tillNo`\r\n" + 
					//"GROUP BY `date`,`code`,`description`\r\n" + 
					"ORDER BY\r\n" + 
					"`date` ASC",
					nativeQuery = true					
			)
	List<ProductListingReport> getProductListingReport(LocalDate from, LocalDate to);
	
	@Query(
			value = "SELECT \r\n" +
					"`products`.`code` AS `code`,\r\n" + 
					"`products`.`description` AS `description`,\r\n" + 
					"`products`.`stock` AS `stock`,\r\n" + 
					"SUM(`sale_details`.`qty`) AS `qty`,\r\n" + 
					"SUM(`sale_details`.`qty`*(`sale_details`.`selling_price_vat_incl`-`sale_details`.`discount`)) AS `amount`,\r\n" + 
					"SUM(`sale_details`.`qty`*`sale_details`.`discount`) AS `discount`,\r\n" + 
					"SUM(`sale_details`.`qty`*`sale_details`.`tax`) AS `tax`,\r\n" + 
					"SUM(`sale_details`.`qty`*(`sale_details`.`selling_price_vat_incl`-`sale_details`.`cost_price_vat_incl`-`sale_details`.`discount`)) AS `profit`\r\n" + 
					"FROM\r\n" + 
					"`sales`\r\n" + 
					"JOIN `days` ON\r\n" + 
					"`days`.`id`=`sales`.`day_id`\r\n" + 
					"JOIN `sale_details` ON\r\n" + 
					"`sale_details`.`sale_id`=`sales`.`id`\r\n" + 
					"JOIN `products` ON\r\n" + 
					"`sale_details`.`product_id`=`products`.`id`\r\n" + 
					"JOIN `suppliers` ON\r\n" + 
					"`suppliers`.`id`=`products`.`supplier_id`\r\n" + 
					"WHERE\r\n" + 
					"`days`.`bussiness_date` BETWEEN :from AND :to AND `suppliers`.`name`=:supplierName\r\n" + 
					"GROUP BY `code`",
					nativeQuery = true					
			)
	List<SupplySalesReport> getSupplySalesReport(LocalDate from, LocalDate to, String supplierName);
	
	@Query(
			value = "SELECT \r\n" +
					"`products`.`code` AS `code`,\r\n" + 
					"`products`.`description` AS `description`,\r\n" + 
					"`products`.`stock` AS `stock`,\r\n" + 
					"SUM(`sale_details`.`qty`) AS `qty`,\r\n" + 
					"SUM(`sale_details`.`qty`*(`sale_details`.`selling_price_vat_incl`-`sale_details`.`discount`)) AS `amount`,\r\n" + 
					"SUM(`sale_details`.`qty`*`sale_details`.`discount`) AS `discount`,\r\n" + 
					"SUM(`sale_details`.`qty`*`sale_details`.`tax`) AS `tax`,\r\n" + 
					"SUM(`sale_details`.`qty`*(`sale_details`.`selling_price_vat_incl`-`sale_details`.`cost_price_vat_incl`-`sale_details`.`discount`)) AS `profit`\r\n" + 
					"FROM\r\n" + 
					"`sales`\r\n" + 
					"JOIN `days` ON\r\n" + 
					"`days`.`id`=`sales`.`day_id`\r\n" + 
					"JOIN `sale_details` ON\r\n" + 
					"`sale_details`.`sale_id`=`sales`.`id`\r\n" + 
					"JOIN `products` ON\r\n" + 
					"`sale_details`.`product_id`=`products`.`id`\r\n" + 
					"JOIN `suppliers` ON\r\n" + 
					"`suppliers`.`id`=`products`.`supplier_id`\r\n" + 
					"WHERE\r\n" + 
					"`days`.`bussiness_date` BETWEEN :from AND :to AND `products`.`code` IN (:codes)\r\n" + 
					"GROUP BY `code`",
					nativeQuery = true					
			)
	List<SupplySalesReport> getSupplySalesReportByProducts(LocalDate from, LocalDate to, List<String> codes);
	
	
	@Query(
			value = "SELECT \r\n" +
					"`products`.`code` AS `code`,\r\n" + 
					"`products`.`description` AS `description`,\r\n" + 
					"`products`.`stock` AS `stock`,\r\n" + 
					"SUM(`sale_details`.`qty`) AS `qty`,\r\n" + 
					"SUM(`sale_details`.`qty`*(`sale_details`.`selling_price_vat_incl`-`sale_details`.`discount`)) AS `amount`,\r\n" + 
					"SUM(`sale_details`.`qty`*`sale_details`.`discount`) AS `discount`,\r\n" + 
					"SUM(`sale_details`.`qty`*`sale_details`.`tax`) AS `tax`,\r\n" + 
					"SUM(`sale_details`.`qty`*(`sale_details`.`selling_price_vat_incl`-`sale_details`.`cost_price_vat_incl`-`sale_details`.`discount`)) AS `profit`\r\n" + 
					"FROM\r\n" + 
					"`sales`\r\n" + 
					"JOIN `days` ON\r\n" + 
					"`days`.`id`=`sales`.`day_id`\r\n" + 
					"JOIN `sale_details` ON\r\n" + 
					"`sale_details`.`sale_id`=`sales`.`id`\r\n" + 
					"JOIN `products` ON\r\n" + 
					"`sale_details`.`product_id`=`products`.`id`\r\n" + 
					"JOIN `suppliers` ON\r\n" + 
					"`suppliers`.`id`=`products`.`supplier_id`\r\n" + 
					"WHERE\r\n" + 
					"`days`.`bussiness_date` BETWEEN :from AND :to \r\n" + 
					"GROUP BY `code`",
					nativeQuery = true					
			)
	List<SupplySalesReport> getSupplySalesReportAll(LocalDate from, LocalDate to);
	
	
	@Query(
			value = "SELECT\r\n" + 
					"`products`.`code` AS `code`,\r\n" + 
					"SUM(`sale_details`.`qty`) AS `qty`,\r\n" + 
					"`products`.`description` AS `description`,\r\n" + 
					"`products`.`stock` as `stock`\r\n" + 
					"FROM\r\n" + 
					"`sales`\r\n" + 
					"JOIN `days` ON\r\n" + 
					"`days`.`id`=`sales`.`day_id`\r\n" + 
					"JOIN `sale_details` ON\r\n" + 
					"`sale_details`.`sale_id`=`sales`.`id`\r\n" + 
					"JOIN `products` ON\r\n" + 
					"`sale_details`.`product_id`=`products`.`id`\r\n" + 
					"WHERE\r\n" + 
					"`days`.`bussiness_date` BETWEEN :fromDate AND :toDate\r\n" + 
					"GROUP BY `code`\r\n" + 
					"ORDER BY `qty` DESC",
					nativeQuery = true					
			)
	List<FastMovingProductsReport>getFastMovingProducts(LocalDate fromDate, LocalDate toDate);
	
	@Query(
			value = "SELECT\r\n" + 
					"`products`.`code` AS `code`,\r\n" + 
					"SUM(`sale_details`.`qty`) AS `qty`,\r\n" + 
					"`products`.`description` AS `description`,\r\n" + 
					"`products`.`stock` as `stock`\r\n" + 
					"FROM\r\n" + 
					"`sales`\r\n" + 
					"JOIN `days` ON\r\n" + 
					"`days`.`id`=`sales`.`day_id`\r\n" + 
					"JOIN `sale_details` ON\r\n" + 
					"`sale_details`.`sale_id`=`sales`.`id`\r\n" + 
					"JOIN `products` ON\r\n" + 
					"`sale_details`.`product_id`=`products`.`id`\r\n" + 
					"WHERE\r\n" + 
					"`days`.`bussiness_date` BETWEEN :fromDate AND :toDate\r\n" + 
					"GROUP BY `code`\r\n" + 
					"ORDER BY `qty` ASC",
					nativeQuery = true					
			)
	List<SlowMovingProductsReport>getSlowMovingProducts(LocalDate fromDate, LocalDate toDate);

	
	

}

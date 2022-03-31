/**
 * 
 */
package com.orbix.api.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orbix.api.domain.Product;
import com.orbix.api.reports.models.DailySalesReport;
import com.orbix.api.reports.models.NegativeStockReport;
import com.orbix.api.reports.models.ProductStockCardReport;
import com.orbix.api.reports.models.SupplierStockStatusReport;

/**
 * @author GODFREY
 *
 */
public interface ProductRepository extends JpaRepository<Product, Long> {
	/**
	 * @param barcode
	 * @return
	 */
	Optional<Product> findByBarcode(String barcode);

	/**
	 * @param code
	 * @return
	 */
	Optional<Product> findByCode(String code);

	/**
	 * @param description
	 * @return
	 */
	Optional<Product> findByDescription(String description);

	/**
	 * @param commonName
	 * @return
	 */
	Optional<Product> findByCommonName(String commonName);
	
	@Query("SELECT p.description FROM Product p WHERE p.active =1 AND p.sellable=1")
	List<String> getSellableProductDescriptions();
	
	
	
	@Query(
			value = "SELECT\r\n" + 
					"`products`.`code` AS `code`,\r\n" +
					"`products`.`description` AS `description`,\r\n" +
					"`products`.`stock` AS `stock`\r\n" +
					"FROM\r\n" +
					"`products`\r\n"+
					"WHERE\r\n"+
					"`products`.`stock` < 0",
					nativeQuery = true					
			)
	List<NegativeStockReport> getNegativeStockReport();
	
	
	@Query(
			value = "SELECT\r\n" + 
					"`suppliers`.`name` AS `supplierName`,\r\n" + 
					"`products`.`code` AS `code`,\r\n" +
					"`products`.`description` AS `description`,\r\n" +
					"`products`.`stock` AS `stock`\r\n" +
					"FROM\r\n" + 
					"(SELECT * FROM `suppliers` WHERE `name`=:name)`suppliers`\r\n" + 
					"JOIN\r\n" + 
					"`products`\r\n" + 
					"ON\r\n" + 
					"`products`.`supplier_id`=`suppliers`.`id`\r\n",
					nativeQuery = true					
			)
	List<SupplierStockStatusReport> getSupplySalesReport(String name);

}

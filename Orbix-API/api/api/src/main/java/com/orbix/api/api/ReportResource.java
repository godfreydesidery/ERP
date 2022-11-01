/**
 * 
 */
package com.orbix.api.api;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.orbix.api.domain.Product;
import com.orbix.api.domain.Supplier;
import com.orbix.api.reports.models.DailyProductionReport;
import com.orbix.api.reports.models.DailyPurchaseReport;
import com.orbix.api.reports.models.DailySalesReport;
import com.orbix.api.reports.models.DailySummaryReport;
import com.orbix.api.reports.models.FastMovingProductsReport;
import com.orbix.api.reports.models.GrnReport;
import com.orbix.api.reports.models.LpoReport;
import com.orbix.api.reports.models.MaterialUsageReport;
import com.orbix.api.reports.models.NegativeStockReport;
import com.orbix.api.reports.models.ProductListingReport;
import com.orbix.api.reports.models.ProductStockCardReport;
import com.orbix.api.reports.models.ProductStockSummaryReport;
import com.orbix.api.reports.models.PurchaseSummaryReport;
import com.orbix.api.reports.models.SalesSummaryReport;
import com.orbix.api.reports.models.SlowMovingProductsReport;
import com.orbix.api.reports.models.SupplierStockStatusReport;
import com.orbix.api.reports.models.SupplySalesReport;
import com.orbix.api.reports.service.FastMovingProductsReportService;
import com.orbix.api.reports.service.GrnReportService;
import com.orbix.api.reports.service.LpoReportService;
import com.orbix.api.reports.service.NegativeStockReportService;
import com.orbix.api.reports.service.ProductListingReportService;
import com.orbix.api.reports.service.ProductStockCardReportService;
import com.orbix.api.reports.service.ProductionReportService;
import com.orbix.api.reports.service.PurchaseReportService;
import com.orbix.api.reports.service.SalesReportService;
import com.orbix.api.reports.service.SlowMovingProductsReportService;
import com.orbix.api.reports.service.SupplierStockStatusReportService;
import com.orbix.api.repositories.ProductStockRepository;
import com.orbix.api.repositories.PurchaseRepository;
import com.orbix.api.repositories.SaleRepository;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * @author GODFREY
 *
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ReportResource {
	
	private final ProductStockCardReportService productStockCardServiceReport;
	private final SalesReportService salesReportService;
	private final PurchaseReportService purchaseReportService;
	private final ProductionReportService productionReportService;
	private final NegativeStockReportService negativeStockReportService;
	private final SupplierStockStatusReportService supplierStockStatusReportService;
	private final ProductListingReportService productListingReportService;
	private final FastMovingProductsReportService fastMovingProductsReportService;
	private final SlowMovingProductsReportService slowMovingProductsReportService;
	private final LpoReportService lpoReportService;
	private final GrnReportService grnReportService;
	private final ProductStockRepository productStockRepository;
	private final SaleRepository saleRepository;
	private final PurchaseRepository purchaseRepository;
	
	
	@PostMapping("/reports/daily_sales_report")
	public ResponseEntity<List<DailySalesReport>> dailySalesReport(
			@RequestBody DailySalesReportArgs args){
		return ResponseEntity.ok().body(salesReportService.getDailySalesReport(args.from, args.to, null, null, null, null, null, null, null, null, null));
	}
	
	@PostMapping("/reports/daily_purchase_report")
	public ResponseEntity<List<DailyPurchaseReport>> dailyPurchaseReport(
			@RequestBody DailyPurchaseReportArgs args){
		return ResponseEntity.ok().body(purchaseReportService.getDailyPurchaseReport(args.from, args.to, null, null, null, null, null, null, null, null, null));
	}
	
	@PostMapping("/reports/daily_summary_report")
	public ResponseEntity<List<DailySummaryReport>> dailySummaryReport(
			@RequestBody DailySummaryReportArgs args){
		return ResponseEntity.ok().body(salesReportService.getDailySummaryReport(args.from, args.to));
	}
	
	@PostMapping("/reports/purchase_summary_report")
	public ResponseEntity<List<PurchaseSummaryReport>> purchaseSummaryReport(
			@RequestBody PurchaseSummaryReportArgs args){
		return ResponseEntity.ok().body(purchaseRepository.getPurchaseSummaryReport(args.from, args.to));
	}
	
	@PostMapping("/reports/sales_summary_report")
	public ResponseEntity<List<SalesSummaryReport>> salesSummaryReport(
			@RequestBody SalesSummaryReportArgs args){
		return ResponseEntity.ok().body(saleRepository.getSalesSummaryReport(args.from, args.to));
	}
	
	@PostMapping("/reports/product_stock_summary_report")
	public ResponseEntity<List<ProductStockSummaryReport>> productStockSummaryReport(
			@RequestBody ProductStockSummaryReportArgs args){
		return ResponseEntity.ok().body(productStockRepository.getProductStockSummaryReport(args.from, args.to));
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	
	@PostMapping("/reports/negative_stock_report")
	public ResponseEntity<List<NegativeStockReport>> negativeStockReport(
			@RequestBody NegativeStockReportArgs args){
		return ResponseEntity.ok().body(negativeStockReportService.getNegativeStockReport());
	}
	
	@PostMapping("/reports/product_stock_card_report")
	public ResponseEntity<List<ProductStockCardReport>> productStockCardReport(
			@RequestBody ProductStockCardReportArgs args){
		return ResponseEntity.ok().body(productStockCardServiceReport.getProductStockCardReport(args.from, args.to, args.supplier, args.products));
	}
	
	@PostMapping("/reports/supply_sales_report")
	public ResponseEntity<List<SupplySalesReport>> supplySalesReport(
			@RequestBody SupplySalesReportArgs args){
		return ResponseEntity.ok().body(salesReportService.getSupplySalesReport(args.from, args.to, args.supplier, args.products));
	}	
	
	@PostMapping("/reports/supplier_stock_status_report")
	public ResponseEntity<List<SupplierStockStatusReport>> supplierStockStatusReport(
			@RequestBody SupplierStockStatusReportArgs args){
		return ResponseEntity.ok().body(supplierStockStatusReportService.getSupplierStockStatusReport(args.supplier, args.products));			
	}	
	
	@PostMapping("/reports/daily_production_report")
	public ResponseEntity<List<DailyProductionReport>> productionReport(
			@RequestBody ProductionReportArgs args){
		return ResponseEntity.ok().body(productionReportService.getDailyProductionReport(args.from, args.to));
	}	
	
	@PostMapping("/reports/material_usage_report")
	public ResponseEntity<List<MaterialUsageReport>> materialUsageReport(
			@RequestBody ProductionReportArgs args){
		return ResponseEntity.ok().body(productionReportService.getMaterialUsageReport(args.from, args.to));
	}	
	
	@PostMapping("/reports/product_listing_report")
	public ResponseEntity<List<ProductListingReport>> productListingReport(
			@RequestBody ProductListingReportArgs args){
		return ResponseEntity.ok().body(productListingReportService.getProductListingReport(args.from, args.to, args.tillNo));
	}	
	
	@PostMapping("/reports/daily_sales_report_by_till")
	public ResponseEntity<List<DailySalesReport>> dailySalesReport(
			@RequestBody ProductListingReportArgs args){
		return ResponseEntity.ok().body(saleRepository.getDailySalesReportByTill(args.from, args.to, args.tillNo));
	}	
	
	@PostMapping("/reports/fast_moving_products_report")
	public ResponseEntity<List<FastMovingProductsReport>> fastMovingProductsReport(
			@RequestBody FastMovingProductsReportArgs args){
		return ResponseEntity.ok().body(fastMovingProductsReportService.getFastMovingProductsReport(args.from, args.to));
	}	
	@PostMapping("/reports/slow_moving_products_report")
	public ResponseEntity<List<SlowMovingProductsReport>> slowMovingProductsReport(
			@RequestBody SlowMovingProductsReportArgs args){
		return ResponseEntity.ok().body(slowMovingProductsReportService.getSlowMovingProductsReport(args.from, args.to));
	}
	
	@PostMapping("/reports/lpo_report")
	public ResponseEntity<List<LpoReport>> lpoReport(
			@RequestBody LpoReportArgs args){
		return ResponseEntity.ok().body(lpoReportService.getLpoReport(args.from, args.to, args.supplier, args.products));
	}
	
	@PostMapping("/reports/grn_report")
	public ResponseEntity<List<GrnReport>> grnReport(
			@RequestBody GrnReportArgs args){
		return ResponseEntity.ok().body(grnReportService.getGrnReport(args.from, args.to, args.supplier, args.products));
	}
}

@Data
class DailySalesReportArgs {
	LocalDate from;
	LocalDate to;
	
}

@Data
class TotalSalesReportArgs {
	LocalDate from;
	LocalDate to;
	String tillNo;
}

@Data
class DailyPurchaseReportArgs {
	LocalDate from;
	LocalDate to;
	
}

@Data
class DailySummaryReportArgs {
	LocalDate from;
	LocalDate to;
	
}

@Data
class PurchasesAndSalesSummaryReportArgs {
	LocalDate from;
	LocalDate to;
	
}

@Data
class NegativeStockReportArgs {
	LocalDate from;
	LocalDate to;
	
}

@Data
class ProductStockCardReportArgs {
	LocalDate from;
	LocalDate to;
	Supplier supplier;
	List<Product> products;
}

@Data
class SupplySalesReportArgs {
	LocalDate from;
	LocalDate to;
	Supplier supplier;
	List<Product> products;
}

@Data
class SupplierStockStatusReportArgs {
	Supplier supplier;
	List<Product> products;
}

@Data
class ProductionReportArgs {
	LocalDate from;
	LocalDate to;
}

@Data
class FastMovingProductsReportArgs {
	LocalDate from;
	LocalDate to;
}

@Data
class SlowMovingProductsReportArgs {
	LocalDate from;
	LocalDate to;
}

@Data
class ProductListingReportArgs {
	LocalDate from;
	LocalDate to;
	String tillNo;
}

@Data
class LpoReportArgs {
	LocalDate from;
	LocalDate to;
	Supplier supplier;
	List<Product> products;
}

@Data
class GrnReportArgs {
	LocalDate from;
	LocalDate to;
	Supplier supplier;
	List<Product> products;
}

@Data
class PurchaseSummaryReportArgs{
	LocalDate from;
	LocalDate to;
}

@Data
class SalesSummaryReportArgs{
	LocalDate from;
	LocalDate to;
}

@Data
class ProductStockSummaryReportArgs{
	LocalDate from;
	LocalDate to;
}


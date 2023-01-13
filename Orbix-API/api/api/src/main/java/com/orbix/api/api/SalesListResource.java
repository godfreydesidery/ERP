/**
 * 
 */
package com.orbix.api.api;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.orbix.api.domain.Customer;
import com.orbix.api.domain.PackingList;
import com.orbix.api.domain.PackingListDetail;
import com.orbix.api.domain.SalesAgent;
import com.orbix.api.domain.SalesList;
import com.orbix.api.domain.SalesListAmendment;
import com.orbix.api.domain.SalesListDetail;
import com.orbix.api.domain.SalesSheet;
import com.orbix.api.domain.SalesSheetSale;
import com.orbix.api.domain.SalesSheetSaleDetail;
import com.orbix.api.domain.Product;
import com.orbix.api.domain.ProductStockCard;
import com.orbix.api.exceptions.InvalidEntryException;
import com.orbix.api.exceptions.InvalidOperationException;
import com.orbix.api.exceptions.NotFoundException;
import com.orbix.api.models.PackingListDetailModel;
import com.orbix.api.models.SalesListDetailModel;
import com.orbix.api.models.SalesListModel;
import com.orbix.api.repositories.CustomerRepository;
import com.orbix.api.repositories.DayRepository;
import com.orbix.api.repositories.SalesAgentRepository;
import com.orbix.api.repositories.SalesListAmendmentRepository;
import com.orbix.api.repositories.SalesListDetailRepository;
import com.orbix.api.repositories.SalesListRepository;
import com.orbix.api.repositories.SalesSheetRepository;
import com.orbix.api.repositories.ProductRepository;
import com.orbix.api.service.DayService;
import com.orbix.api.service.ProductStockCardService;
import com.orbix.api.service.SalesListService;
import com.orbix.api.service.UserService;

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
public class SalesListResource {
	private final UserService userService;
	private final DayService dayService;
	private final SalesListService salesListService;
	private final SalesListRepository salesListRepository;
	private final SalesListDetailRepository salesListDetailRepository;
	private final CustomerRepository customerRepository;
	private final SalesAgentRepository salesAgentRepository;
	private final ProductRepository productRepository;
	private final SalesSheetRepository salesSheetRepository;
	private final DayRepository dayRepository;
	private final ProductStockCardService productStockCardService;
	private final SalesListAmendmentRepository salesListAmendmentRepository;
	
	@GetMapping("/sales_lists")
	public ResponseEntity<List<SalesListModel>>getSalesLists(){
		return ResponseEntity.ok().body(salesListService.getAllVisible());
	}
	
	@GetMapping("/sales_lists/get")
	public ResponseEntity<SalesListModel> getSalesList(
			@RequestParam(name = "id") Long id){
		return ResponseEntity.ok().body(salesListService.get(id));
	}
	
	@GetMapping("/sales_lists/get_by_no")
	public ResponseEntity<SalesListModel> getSalesListByNo(
			@RequestParam(name = "no") String no){
		return ResponseEntity.ok().body(salesListService.getByNo(no));
	}
	
	@GetMapping("/sales_list_details/get_by_sales_list")
	public ResponseEntity<List<SalesListDetailModel>>getSalesListDetails(
			@RequestParam(name = "id") Long id){		
		return ResponseEntity.ok().body(salesListService.getAllDetails(salesListRepository.findById(id).get()));
	}
	
	@PostMapping("/sales_lists/create")
	@PreAuthorize("hasAnyAuthority('SALES_LIST-CREATE')")
	public ResponseEntity<SalesListModel>createSalesList(
			@RequestBody SalesList salesList,
			HttpServletRequest request){
		Optional<Customer> c = customerRepository.findByNo(salesList.getCustomer().getNo());
		if(!c.isPresent()) {
			throw new NotFoundException("Customer not found");
		}
		Optional<SalesAgent> e = salesAgentRepository.findByNo(salesList.getSalesAgent().getNo());
		if(!e.isPresent()) {
			throw new NotFoundException("SalesAgent not found");
		}
		SalesList inv = new SalesList();
		inv.setNo("NA");
		inv.setCustomer(c.get());
		inv.setSalesAgent(e.get());
		inv.setStatus("PENDING");
		inv.setComments(salesList.getComments());	
		inv.setCreatedBy(userService.getUserId(request));
		inv.setCreatedAt(dayService.getDayId());
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/sales_lists/create").toUriString());
		return ResponseEntity.created(uri).body(salesListService.save(inv));
	}
	
	@PutMapping("/sales_lists/update")
	@PreAuthorize("hasAnyAuthority('SALES_LIST-CREATE','PACKING_LIST-UPDATE')")
	public ResponseEntity<SalesListModel>updateSalesList(
			@RequestBody SalesList salesList,
			HttpServletRequest request){
		Optional<Customer> c = customerRepository.findByNo(salesList.getCustomer().getNo());
		if(!c.isPresent()) {
			throw new NotFoundException("Customer not found");
		}
		Optional<SalesAgent> e = salesAgentRepository.findByNo(salesList.getSalesAgent().getNo());
		if(!e.isPresent()) {
			throw new NotFoundException("SalesAgent not found");
		}
		Optional<SalesList> l = salesListRepository.findById(salesList.getId());
		if(!l.isPresent()) {
			throw new NotFoundException("PACKING_LIST not found");
		}
		if(!l.get().getStatus().equals("PENDING")) {
			throw new InvalidOperationException("Editing not allowed, only Pending Sales Issues can be edited");
		}
		List<SalesListDetail> d = salesListDetailRepository.findBySalesList(l.get());
		int i = 0;
		for(@SuppressWarnings("unused") SalesListDetail dt : d) {
			i = 1;
			break;
		}
		if(i > 0 && !l.get().getCustomer().equals(c.get())) {
			throw new InvalidOperationException("Changing Customer is not allowed for non blank Sales Issues");
		}		
		l.get().setCustomer(c.get());
		l.get().setSalesAgent(e.get());
		l.get().setComments(salesList.getComments());
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/sales_lists/update").toUriString());
		return ResponseEntity.created(uri).body(salesListService.save(l.get()));
	}
	
	@PutMapping("/sales_lists/approve")
	@PreAuthorize("hasAnyAuthority('SALES_LIST-APPROVE')")
	public ResponseEntity<SalesListModel>postSalesList(
			@RequestBody SalesList salesList,
			HttpServletRequest request){		
		Optional<SalesList> l = salesListRepository.findById(salesList.getId());
		if(!l.isPresent()) {
			throw new NotFoundException("Sales List not found");
		}
		if(l.get().getStatus().equals("PENDING")) {
			l.get().setTotalReturns(salesList.getTotalReturns());
			l.get().setTotalDamages(salesList.getTotalDamages());
			l.get().setTotalDeficit(salesList.getTotalDeficit());
			l.get().setTotalDiscounts(salesList.getTotalDiscounts());
			l.get().setTotalExpenditures(salesList.getTotalExpenditures());
			l.get().setTotalBank(salesList.getTotalBank());
			l.get().setTotalCash(salesList.getTotalCash());
			
			l.get().setApprovedBy(userService.getUserId(request));
			l.get().setApprovedAt(dayService.getDayId());
			l.get().setStatus("APPROVED");
		}else {
			throw new InvalidOperationException("Could not approve, only PENDING Sales List can be approved");
		}
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/sales_lists/approve").toUriString());
		return ResponseEntity.created(uri).body(salesListService.approve(l.get(), request));
	}
	
	@PutMapping("/sales_lists/cancel")
	@PreAuthorize("hasAnyAuthority('SALES_LIST-CANCEL')")
	public ResponseEntity<SalesListModel>cancelSalesList(
			@RequestBody SalesList salesList,
			HttpServletRequest request){		
		Optional<SalesList> l = salesListRepository.findById(salesList.getId());
		if(!l.isPresent()) {
			throw new NotFoundException("PACKING_LIST not found");
		}
		if(l.get().getStatus().equals("PENDING") || l.get().getStatus().equals("BLANK")) {
			l.get().setStatus("CANCELED");
		}else {
			throw new InvalidOperationException("Could not cancel, only Pending or Approved PACKING_LISTs can be canceled");
		}
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/sales_lists/cancel").toUriString());
		return ResponseEntity.created(uri).body(salesListService.save(l.get()));
	}
	
	@PutMapping("/sales_lists/archive")
	@PreAuthorize("hasAnyAuthority('SALES_LIST-UPDATE','SALES_LIST-ARCHIVE')")
	public ResponseEntity<Boolean>archiveSalesList(
			@RequestBody SalesList salesList,
			HttpServletRequest request){		
		Optional<SalesList> l = salesListRepository.findById(salesList.getId());
		if(!l.isPresent()) {
			throw new NotFoundException("PACKING_LIST not found");
		}
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/sales_lists/archive").toUriString());
		return ResponseEntity.created(uri).body(salesListService.archive(l.get()));
	}
	
	@PutMapping("/sales_lists/archive_all")
	@PreAuthorize("hasAnyAuthority('SALES_LIST-UPDATE','SALES_LIST-ARCHIVE')")
	public ResponseEntity<Boolean>archiveSalesLists(){			
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/sales_lists/archive_all").toUriString());
		return ResponseEntity.created(uri).body(salesListService.archiveAll());
	}
	
	@PostMapping("/sales_list_details/save")
	@PreAuthorize("hasAnyAuthority('SALES_LIST-CREATE','SALES_LIST-UPDATE')")
	public ResponseEntity<SalesListDetailModel>createSalesListDetail(
			@RequestBody SalesListDetail salesListDetail){
		
		if(salesListDetail.getQtySold() < 0) {
			throw new InvalidEntryException("Quantity sold must be positive");
		}
		if(salesListDetail.getQtyOffered() < 0) {
			throw new InvalidEntryException("Quantity offered must be positive");
		}
		if(salesListDetail.getQtyReturned() < 0) {
			throw new InvalidEntryException("Quantity returned must be positive");
		}
		if(salesListDetail.getQtyDamaged() < 0) {
			throw new InvalidEntryException("Quantity damaged must be positive");
		}					
		Optional<SalesList> l = salesListRepository.findById(salesListDetail.getSalesList().getId());
		if(!l.isPresent()) {
			throw new NotFoundException("PACKING_LIST not found");
		}		
		
		Optional<Product> p = productRepository.findById(salesListDetail.getProduct().getId());
		if(!p.isPresent()) {
			throw new NotFoundException("Product not found");
		}
		Optional<SalesListDetail> d = salesListDetailRepository.findBySalesListAndProduct(l.get(), p.get());
		SalesListDetail detail = new SalesListDetail();
		if(d.isPresent()) {
			/**
			 * Update existing detail
			 */
			detail = d.get();
			
			if(l.get().getStatus().equals("PENDING")) {
				if(salesListDetail.getTotalPacked() != salesListDetail.getQtySold() + salesListDetail.getQtyOffered() + salesListDetail.getQtyReturned() + salesListDetail.getQtyDamaged()) {
					if(salesListDetail.getQtySold() + salesListDetail.getQtyOffered() + salesListDetail.getQtyReturned() + salesListDetail.getQtyDamaged() != 0) {
						throw new InvalidEntryException("Total quantity must be a sum of qty sold, qty offered, qty returned and qty damaged");
					}
				}
				detail.setQtySold(salesListDetail.getQtySold());
				detail.setQtyOffered(salesListDetail.getQtyOffered());
				detail.setQtyReturned(salesListDetail.getQtyReturned());
				detail.setQtyDamaged(salesListDetail.getQtyDamaged());
			}
			
		}else {
			/**
			 * Throw exception
			 */
			throw new NotFoundException("Detail not found");
		}		
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/sales_list_details/save").toUriString());
		return ResponseEntity.created(uri).body(salesListService.saveDetail(detail));
	}
	
	@GetMapping("/sales_list_details/get")
	public ResponseEntity<SalesListDetailModel>getDetail(
			@RequestParam(name = "id") Long id){		
		Optional<SalesListDetail> d = salesListDetailRepository.findById(id);
		if(!d.isPresent()) {
			throw new NotFoundException("Detail not found");
		}		
		SalesListDetailModel detail = new SalesListDetailModel();
		detail.setId(d.get().getId());
		detail.setProduct((d.get().getProduct()));
		detail.setTotalPacked((d.get().getTotalPacked()));
		detail.setSellingPriceVatIncl((d.get().getSellingPriceVatIncl()));
		detail.setSellingPriceVatExcl((d.get().getSellingPriceVatExcl()));
		detail.setQtySold((d.get().getQtySold()));
		detail.setQtyOffered((d.get().getQtyOffered()));
		detail.setQtyReturned((d.get().getQtyReturned()));
		detail.setQtyDamaged((d.get().getQtyDamaged()));
		
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/sales_list_details/get").toUriString());
		return ResponseEntity.created(uri).body(detail);
	}
	
	@DeleteMapping("/sales_list_details/delete")
	@PreAuthorize("hasAnyAuthority('SALES_LIST-DELETE')")
	public ResponseEntity<Boolean> deleteDetail(
			@RequestParam(name = "id") Long id){		
		Optional<SalesListDetail> d = salesListDetailRepository.findById(id);
		if(!d.isPresent()) {
			throw new NotFoundException("Detail not found");
		}
		SalesList salesList = d.get().getSalesList();
		if(!salesList.getStatus().equals("PENDING")) {
			throw new InvalidOperationException("Editing not allowed, only pending PACKING_LIST can be edited");
		}
		//please update transactions concerning this deletion
		salesListDetailRepository.delete(d.get());		
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/sales_list_details/delete").toUriString());
		return ResponseEntity.created(uri).body(true);
	}
	
	@PostMapping("/sales_list_details/change")
	@PreAuthorize("hasAnyAuthority('SALES_LIST-CREATE','SALES_LIST-UPDATE')")
	public boolean changeDetailQty(
			@RequestBody DetailChange detailChange, HttpServletRequest request){
		change(detailChange, request);
				
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/sales_list_details/change").toUriString());
		return true;
	}
	
	
	private double getTotalSoldQtyFromSalesSheet(SalesList salesList, Product product) {
		double qty = 0;
		Optional<SalesSheet> ss = salesSheetRepository.findBySalesList(salesList);
		if(!ss.isPresent()) {
			return 0;
		}
		SalesSheet salesSheet = ss.get();
		List<SalesSheetSale> salesSheetSales = salesSheet.getSalesSheetSales();
		for(SalesSheetSale s : salesSheetSales) {
			List<SalesSheetSaleDetail> salesSheetSaleDetails = s.getSalesSheetSaleDetails();
			for(SalesSheetSaleDetail d : salesSheetSaleDetails) {
				if(d.getProduct() == product) {
					qty = qty + d.getQty();
				}
			}
		}
		return qty;
	}
	
	
	
	private boolean change(DetailChange change, HttpServletRequest request) {
		boolean changed = false;
		if(change.finalQty == 0) {
			throw new InvalidOperationException("Deducting to zero is not allowed, please consider removing the product from the sales list");
		}
		if(change.finalQty < 0) {
			throw new InvalidEntryException("Negative is not allowed");
		}
		Optional<SalesListDetail> d = salesListDetailRepository.findById(change.getId());
		if(!d.isPresent()) {
			throw new NotFoundException("Detail not found");
		}
		if(change.originalQty != d.get().getTotalPacked()) {
			throw new InvalidOperationException("Operation failed, please reload the document");
		}
		SalesList salesList = d.get().getSalesList();
		if(!salesList.getStatus().equals("PENDING")) {
			throw new InvalidOperationException("Only Pending sales list can be edited");
		}
		if(d.get().getQtySold() != 0 || d.get().getQtyReturned() != 0 || d.get().getQtyOffered() != 0 || d.get().getQtyDamaged() != 0) {
			throw new InvalidOperationException("Fields are already filled, please unfil the fields before editing qty");
		}
		double qtyAlreadySold = getTotalSoldQtyFromSalesSheet(d.get().getSalesList(), d.get().getProduct());
		double remainingQty = d.get().getTotalPacked() - qtyAlreadySold;
		double diff = change.finalQty - change.originalQty;
		
		if(diff == 0) {
			throw new InvalidOperationException("No change detected");
		}
		if(diff < 0) {
			if(remainingQty < Math.abs(diff)) {
				throw new InvalidOperationException("Could not change, qty deducted exceeds sold qty");
			}
		}
		/**
		 * Update qty on sales list
		 */
		d.get().setTotalPacked(change.finalQty);
		salesListDetailRepository.saveAndFlush(d.get());
		
		/**
		 * Fill sales list ammendment, to be done later
		 */
		
		
		/**
		 * Update stocks
		 */
		Product product =productRepository.findById(d.get().getProduct().getId()).get();
		double stock = product.getStock();
		if(diff < 0) {
			stock = stock + (Math.abs(diff));							
		}else {
			stock = stock - (Math.abs(diff));
		}
		product.setStock(stock);
		productRepository.saveAndFlush(product);
		
		/**
		 * Update stock cards
		 */
		ProductStockCard stockCard = new ProductStockCard();
		if(diff < 0) {
			stockCard.setQtyIn(Math.abs(diff));
			stockCard.setProduct(product);
			stockCard.setBalance(stock);
			stockCard.setDay(dayRepository.getCurrentBussinessDay());
			stockCard.setReference("Returned in sales list ammendment. Ref #: "+salesList.getNo());
		}else {
			stockCard.setQtyOut(Math.abs(diff));
			stockCard.setProduct(product);
			stockCard.setBalance(stock);
			stockCard.setDay(dayRepository.getCurrentBussinessDay());
			stockCard.setReference("Issued in sales list ammendment. Ref #: "+salesList.getNo());
		}
		productStockCardService.save(stockCard);
		
		/**
		 * Update Sales list amendment file
		 */
		SalesListAmendment amendment = new SalesListAmendment();
		amendment.setSalesList(salesList);
		amendment.setOriginalQty(change.getOriginalQty());
		amendment.setFinalQty(change.getFinalQty());
		amendment.setAmendedBy(userService.getUserId(request));
		amendment.setAmendedAt(dayService.getDayId());
		amendment.setProduct(product);
		if(diff < 0) {
			amendment.setReference("Deducted "+Math.abs(diff)+" units of "+product.getCode()+" "+product.getDescription());
		}else {
			amendment.setReference("Added "+Math.abs(diff)+" units of "+product.getCode()+" "+product.getDescription());
		}		
		salesListAmendmentRepository.saveAndFlush(amendment);
		
		return changed;
	}
	
	@PostMapping("/sales_list_details/add")
	@PreAuthorize("hasAnyAuthority('SALES_LIST-CREATE','SALES_LIST-UPDATE')")
	public ResponseEntity<SalesListDetailModel>addSalesListDetail(
			@RequestBody SalesListDetail salesListDetail, HttpServletRequest request){
		
		if(salesListDetail.getTotalPacked() <= 0) {
			throw new InvalidEntryException("Qty must be more than zero");
		}
						
		Optional<SalesList> l = salesListRepository.findById(salesListDetail.getSalesList().getId());
		if(!l.isPresent()) {
			throw new NotFoundException("SALES_LIST not found");
		}
		if(!l.get().getStatus().equals("PENDING")) {
			throw new InvalidOperationException("Only Pending sales list can be edited");
		}
		
		Optional<Product> p = productRepository.findById(salesListDetail.getProduct().getId());
		if(!p.isPresent()) {
			throw new NotFoundException("Product not found");
		}
		Optional<SalesListDetail> d = salesListDetailRepository.findBySalesListAndProduct(l.get(), p.get());
		SalesListDetail detail = new SalesListDetail();
		if(d.isPresent()) {
			/**
			 * Throw an exception, product already exist
			 */
			throw new InvalidOperationException("Could not add, Product already exist in list");		
		}else {
			/**
			 * Create new detail
			 */
			detail.setSalesList(l.get());			
			detail.setProduct(salesListDetail.getProduct());
			detail.setTotalPacked(salesListDetail.getTotalPacked());
			detail.setQtySold(0);
			detail.setQtyReturned(0);
			detail.setQtyOffered(0);
			detail.setQtyDamaged(0);
			detail.setCostPriceVatIncl(salesListDetail.getCostPriceVatIncl());
			detail.setCostPriceVatExcl(salesListDetail.getCostPriceVatExcl());
			detail.setSellingPriceVatIncl(salesListDetail.getSellingPriceVatIncl());
			detail.setSellingPriceVatExcl(salesListDetail.getSellingPriceVatExcl());
			
			detail = salesListDetailRepository.saveAndFlush(detail);
			
			/**
			 * Update stock
			 */
			Product product =productRepository.findById(detail.getProduct().getId()).get();
			double stock = product.getStock();			
			stock = stock - salesListDetail.getTotalPacked();
			product.setStock(stock);
			productRepository.saveAndFlush(product);
			
			/**
			 * Update stock card
			 */
			ProductStockCard stockCard = new ProductStockCard();
			stockCard.setQtyOut(salesListDetail.getTotalPacked());
			stockCard.setProduct(product);
			stockCard.setBalance(stock);
			stockCard.setDay(dayRepository.getCurrentBussinessDay());
			stockCard.setReference("Issued in sales list ammendment. Ref #: "+salesListDetail.getSalesList().getNo());
			productStockCardService.save(stockCard);
			
			/**
			 * Update Sales list amendment file
			 */
			SalesListAmendment amendment = new SalesListAmendment();
			amendment.setSalesList(salesListDetail.getSalesList());
			amendment.setOriginalQty(0);
			amendment.setFinalQty(salesListDetail.getTotalPacked());
			amendment.setAmendedBy(userService.getUserId(request));
			amendment.setAmendedAt(dayService.getDayId());
			amendment.setProduct(product);				
			amendment.setReference("Added "+salesListDetail.getTotalPacked()+" units of "+product.getCode()+" "+product.getDescription());
			salesListAmendmentRepository.saveAndFlush(amendment);
		}		
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/sales_list_details/add").toUriString());
		return ResponseEntity.created(uri).body(salesListService.saveDetail(detail));
	}
	
}

@Data
class DetailChange{
	Long id;
	double originalQty;
	double finalQty;
}

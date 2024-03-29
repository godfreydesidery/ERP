/**
 * 
 */
package com.orbix.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.orbix.api.accessories.Formater;
import com.orbix.api.domain.Debt;
import com.orbix.api.domain.PackingListDetail;
import com.orbix.api.domain.SalesList;
import com.orbix.api.domain.SalesListDetail;
import com.orbix.api.domain.SalesSheet;
import com.orbix.api.domain.SalesSheetExpense;
import com.orbix.api.domain.SalesSheetSale;
import com.orbix.api.domain.SalesSheetSaleDetail;
import com.orbix.api.domain.Product;
import com.orbix.api.domain.ProductDamage;
import com.orbix.api.domain.ProductOffer;
import com.orbix.api.domain.ProductStockCard;
import com.orbix.api.domain.Sale;
import com.orbix.api.domain.SaleDetail;
import com.orbix.api.exceptions.InvalidEntryException;
import com.orbix.api.exceptions.InvalidOperationException;
import com.orbix.api.exceptions.NotFoundException;
import com.orbix.api.models.PackingListDetailModel;
import com.orbix.api.models.SalesListDetailModel;
import com.orbix.api.models.SalesListModel;
import com.orbix.api.repositories.DayRepository;
import com.orbix.api.repositories.SalesListDetailRepository;
import com.orbix.api.repositories.SalesListRepository;
import com.orbix.api.repositories.SalesSheetRepository;
import com.orbix.api.repositories.ProductRepository;
import com.orbix.api.repositories.SaleDetailRepository;
import com.orbix.api.repositories.SaleRepository;
import com.orbix.api.repositories.UserRepository;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author GODFREY
 *
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SalesListServiceImpl implements SalesListService {
	private final SalesListRepository salesListRepository;
	private final SalesListDetailRepository salesListDetailRepository;
	private final UserRepository userRepository;
	private final UserService userService;
	private final DayRepository dayRepository;
	private final ProductRepository productRepository;
	private final ProductStockCardService productStockCardService;
	private final SaleRepository saleRepository;
	private final SaleDetailRepository saleDetailRepository;
	private final ProductDamageService productDamageService;
	private final ProductOfferService productOfferService;
	private final DebtService debtService;
	private final SalesSheetRepository salesSheetRepository;

	@Override
	public SalesListModel save(SalesList salesList) {
		if(!validate(salesList)) {
			throw new InvalidEntryException("Could not save, Sales List invalid");
		}
		salesList = salesListRepository.saveAndFlush(salesList);
		if(salesList.getNo().equals("NA")) {
			salesList.setNo(generateSalesListNo(salesList));
			salesList = salesListRepository.save(salesList);
		}			
		double totalAmountPacked = 0;
		double totalSales = 0;
		double totalReturns = 0;
		double totalOffered = 0;
		double totalDamages = 0;
		double totalDiscounts = salesList.getTotalDiscounts();
		double totalExpenditures = salesList.getTotalExpenditures();
		double totalCommission = salesList.getTotalCommission();
		double totalBank = salesList.getTotalBank();
		double totalCash = salesList.getTotalCash();
		double totalDeficit = salesList.getTotalDeficit();
		
		SalesListModel model = new SalesListModel();
		model.setId(salesList.getId());
		model.setNo(salesList.getNo());
		model.setIssueDate(salesList.getIssueDate());
		model.setCustomer(salesList.getCustomer());
		model.setSalesAgent(salesList.getSalesAgent());
		model.setStatus(salesList.getStatus());
		model.setComments(salesList.getComments());
		model.setTotalBank(salesList.getTotalBank());
		model.setTotalCash(salesList.getTotalCash());
		model.setTotalDamages(salesList.getTotalDamages());
		model.setTotalDeficit(salesList.getTotalDeficit());
		model.setTotalDiscounts(salesList.getTotalDiscounts());
		model.setTotalExpenditures(salesList.getTotalExpenditures());
		model.setTotalCommission(salesList.getTotalCommission());
		model.setTotalReturns(salesList.getTotalReturns());
		if(salesList.getCreatedAt() != null && salesList.getCreatedBy() != null) {
			model.setCreated(dayRepository.findById(salesList.getCreatedAt()).get().getBussinessDate() +" "+ userRepository.getAlias(salesList.getCreatedBy()));
		}
		if(salesList.getApprovedAt() != null && salesList.getApprovedBy() != null) {
			model.setApproved(dayRepository.findById(salesList.getApprovedAt()).get().getBussinessDate() +" "+ userRepository.getAlias(salesList.getApprovedBy()));
		}
		if(salesList.getPostedAt() != null && salesList.getPostedBy() != null) {
			model.setPosted(dayRepository.findById(salesList.getPostedAt()).get().getBussinessDate() +" "+ userRepository.getAlias(salesList.getPostedBy()));
		}
		List<SalesListDetailModel> details = new ArrayList<SalesListDetailModel>();
		List<SalesListDetail> ds = salesListDetailRepository.findBySalesList(salesList);
		if(!ds.isEmpty()) {
			for(SalesListDetail d : ds) {
				SalesListDetailModel detail = new SalesListDetailModel();
				detail.setId(d.getId());
				detail.setProduct(d.getProduct());
				detail.setQtyDamaged(d.getQtyDamaged());
				detail.setTotalPacked(d.getTotalPacked());
				detail.setQtyOffered(d.getQtyOffered());
				detail.setQtyReturned(d.getQtyReturned());
				detail.setQtySold(d.getQtySold());
				detail.setSellingPriceVatIncl(d.getSellingPriceVatIncl());
				detail.setSellingPriceVatExcl(d.getSellingPriceVatExcl());
				details.add(detail);
				
				totalAmountPacked = totalAmountPacked + d.getTotalPacked() * d.getSellingPriceVatIncl();
				totalSales = totalSales + d.getQtySold() * d.getSellingPriceVatIncl();
				totalReturns = totalReturns + d.getQtyReturned() * d.getSellingPriceVatIncl();
				totalOffered = totalOffered + d.getQtyOffered() * d.getSellingPriceVatIncl();
				totalDamages = totalDamages + d.getQtyDamaged() * d.getSellingPriceVatIncl();	
			}
			model.setSalesListDetails(details);

			model.setTotalAmountPacked(totalAmountPacked);
			model.setTotalSales(totalSales);
			model.setTotalOffered(totalOffered);
			model.setTotalReturns(totalReturns);
			model.setTotalDamages(totalDamages);
			
			model.setTotalDiscounts(totalDiscounts);
			model.setTotalExpenditures(totalExpenditures);
			model.setTotalCommission(totalCommission);
			model.setTotalBank(totalBank);
			model.setTotalCash(totalCash);
			model.setTotalDeficit(totalDeficit);
		}
		
		return model;
	}

	@Override
	public SalesListModel get(Long id) {
		SalesListModel model = new SalesListModel();
		Optional<SalesList> pcl = salesListRepository.findById(id);
		if(!pcl.isPresent()) {
			throw new NotFoundException("Sales List not found");
		}
		model.setId(pcl.get().getId());
		model.setNo(pcl.get().getNo());
		model.setIssueDate(pcl.get().getIssueDate());
		model.setCustomer(pcl.get().getCustomer());
		model.setSalesAgent(pcl.get().getSalesAgent());
		model.setStatus(pcl.get().getStatus());
		model.setComments(pcl.get().getComments());
		
		double totalPreviousReturns = 0;
		double totalAmountIssued = 0;
		double totalAmountPacked = 0;
		double totalSales = 0;
		double totalReturns = 0;
		double totalOffered = 0;
		double totalDamages = 0;
		double totalDiscounts = pcl.get().getTotalDiscounts();
		double totalExpenditures = pcl.get().getTotalExpenditures();
		double totalCommission = pcl.get().getTotalCommission();
		double totalBank = pcl.get().getTotalBank();
		double totalCash = pcl.get().getTotalCash();
		double totalDeficit = pcl.get().getTotalDeficit();
		
		if(pcl.get().getCreatedAt() != null && pcl.get().getCreatedBy() != null) {
			model.setCreated(dayRepository.findById(pcl.get().getCreatedAt()).get().getBussinessDate() +" "+ userRepository.getAlias(pcl.get().getCreatedBy()));
		}
		if(pcl.get().getApprovedAt() != null && pcl.get().getApprovedBy() != null) {
			model.setApproved(dayRepository.findById(pcl.get().getApprovedAt()).get().getBussinessDate() +" "+ userRepository.getAlias(pcl.get().getApprovedBy()));
		}
		if(pcl.get().getPostedAt() != null && pcl.get().getPostedBy() != null) {
			model.setPosted(dayRepository.findById(pcl.get().getPostedAt()).get().getBussinessDate() +" "+ userRepository.getAlias(pcl.get().getPostedBy()));
		}
		List<SalesListDetail> salesListDetails = pcl.get().getSalesListDetails();
		List<SalesListDetailModel> details = new ArrayList<SalesListDetailModel>();
		for(SalesListDetail d : salesListDetails) {
			SalesListDetailModel detail = new SalesListDetailModel();
			detail.setId(d.getId());
			detail.setProduct(d.getProduct());
			detail.setQtyDamaged(d.getQtyDamaged());
			detail.setTotalPacked(d.getTotalPacked());
			detail.setQtyOffered(d.getQtyOffered());
			detail.setQtyReturned(d.getQtyReturned());
			detail.setQtySold(d.getQtySold());
			detail.setSellingPriceVatIncl(d.getSellingPriceVatIncl());
			detail.setSellingPriceVatExcl(d.getSellingPriceVatExcl());
			details.add(detail);

			totalAmountPacked = totalAmountPacked + d.getTotalPacked() * d.getSellingPriceVatIncl();
			totalSales = totalSales + d.getQtySold() * d.getSellingPriceVatIncl();
			totalReturns = totalReturns + d.getQtyReturned() * d.getSellingPriceVatIncl();
			totalOffered = totalOffered + d.getQtyOffered() * d.getSellingPriceVatIncl();
			totalDamages = totalDamages + d.getQtyDamaged() * d.getSellingPriceVatIncl();	
		}
		model.setSalesListDetails(details);
		
		model.setTotalPreviousReturns(totalPreviousReturns);
		model.setTotalAmountIssued(totalAmountIssued);
		model.setTotalAmountPacked(totalAmountPacked);
		model.setTotalSales(totalSales);
		model.setTotalOffered(totalOffered);
		model.setTotalReturns(totalReturns);
		model.setTotalDamages(totalDamages);
		
		model.setTotalDiscounts(totalDiscounts);
		model.setTotalExpenditures(totalExpenditures);
		model.setTotalCommission(totalCommission);
		model.setTotalBank(totalBank);
		model.setTotalCash(totalCash);
		model.setTotalDeficit(totalDeficit);
		
		return model;
	}

	@Override
	public SalesListModel getByNo(String no) {
		SalesListModel model = new SalesListModel();
		Optional<SalesList> pcl = salesListRepository.findByNo(no);
		if(!pcl.isPresent()) {
			throw new NotFoundException("Sales List not found");
		}
		model.setId(pcl.get().getId());
		model.setNo(pcl.get().getNo());
		model.setIssueDate(pcl.get().getIssueDate());
		model.setCustomer(pcl.get().getCustomer());
		model.setSalesAgent(pcl.get().getSalesAgent());
		model.setStatus(pcl.get().getStatus());
		model.setComments(pcl.get().getComments());
		
		double totalPreviousReturns = 0;
		double totalAmountIssued = 0;
		double totalAmountPacked = 0;
		double totalSales = 0;
		double totalReturns = 0;
		double totalOffered = 0;
		double totalDamages = 0;
		double totalDiscounts = pcl.get().getTotalDiscounts();
		double totalExpenditures = pcl.get().getTotalExpenditures();
		double totalCommission = pcl.get().getTotalCommission();
		double totalBank = pcl.get().getTotalBank();
		double totalCash = pcl.get().getTotalCash();
		double totalDeficit = pcl.get().getTotalDeficit();
		
		if(pcl.get().getCreatedAt() != null && pcl.get().getCreatedBy() != null) {
			model.setCreated(dayRepository.findById(pcl.get().getCreatedAt()).get().getBussinessDate() +" "+ userRepository.getAlias(pcl.get().getCreatedBy()));
		}
		if(pcl.get().getApprovedAt() != null && pcl.get().getApprovedBy() != null) {
			model.setApproved(dayRepository.findById(pcl.get().getApprovedAt()).get().getBussinessDate() +" "+ userRepository.getAlias(pcl.get().getApprovedBy()));
		}	
		if(pcl.get().getPostedAt() != null && pcl.get().getPostedBy() != null) {
			model.setPosted(dayRepository.findById(pcl.get().getPostedAt()).get().getBussinessDate() +" "+ userRepository.getAlias(pcl.get().getPostedBy()));
		}	
		List<SalesListDetail> salesListDetails = pcl.get().getSalesListDetails();
		List<SalesListDetailModel> details = new ArrayList<SalesListDetailModel>();
		for(SalesListDetail d : salesListDetails) {
			SalesListDetailModel detail = new SalesListDetailModel();
			detail.setId(d.getId());
			detail.setProduct(d.getProduct());
			detail.setQtyDamaged(d.getQtyDamaged());
			detail.setTotalPacked(d.getTotalPacked());
			detail.setQtyOffered(d.getQtyOffered());
			detail.setQtyReturned(d.getQtyReturned());
			detail.setQtySold(d.getQtySold());
			detail.setSellingPriceVatIncl(d.getSellingPriceVatIncl());
			detail.setSellingPriceVatExcl(d.getSellingPriceVatExcl());
			details.add(detail);

			totalAmountPacked = totalAmountPacked + d.getTotalPacked() * d.getSellingPriceVatIncl();
			totalSales = totalSales + d.getQtySold() * d.getSellingPriceVatIncl();
			totalReturns = totalReturns + d.getQtyReturned() * d.getSellingPriceVatIncl();
			totalOffered = totalOffered + d.getQtyOffered() * d.getSellingPriceVatIncl();
			totalDamages = totalDamages + d.getQtyDamaged() * d.getSellingPriceVatIncl();
		}
		model.setSalesListDetails(details);
		
		model.setTotalPreviousReturns(totalPreviousReturns);
		model.setTotalAmountIssued(totalAmountIssued);
		model.setTotalAmountPacked(totalAmountPacked);
		model.setTotalSales(totalSales);
		model.setTotalOffered(totalOffered);
		model.setTotalReturns(totalReturns);
		model.setTotalDamages(totalDamages);
		
		model.setTotalDiscounts(totalDiscounts);
		model.setTotalExpenditures(totalExpenditures);
		model.setTotalCommission(totalCommission);
		model.setTotalBank(totalBank);
		model.setTotalCash(totalCash);
		model.setTotalDeficit(totalDeficit);
		
		return model;
	}

	@Override
	public boolean delete(SalesList salesList) {
		if(!allowDelete(salesList)) {
			throw new InvalidOperationException("Deleting the selected Sales Issue is not allowed");
		}
		salesListRepository.delete(salesList);
		return true;
	}

	@Override
	public List<SalesListModel> getAllVisible() {
		List<String> statuses = new ArrayList<String>();
		statuses.add("BLANK");
		statuses.add("PENDING");
		statuses.add("APPROVED");
		statuses.add("POSTED");
		List<SalesList> salesLists = salesListRepository.findAllVissible(statuses);
		List<SalesListModel> models = new ArrayList<SalesListModel>();
		for(SalesList pcl : salesLists) {
			SalesListModel model = new SalesListModel();
			model.setId(pcl.getId());
			model.setNo(pcl.getNo());
			model.setCustomer(pcl.getCustomer());
			model.setSalesAgent(pcl.getSalesAgent());
			model.setStatus(pcl.getStatus());
			model.setComments(pcl.getComments());
			model.setTotalBank(pcl.getTotalBank());
			model.setTotalCash(pcl.getTotalCash());
			model.setTotalDamages(pcl.getTotalDamages());
			model.setTotalDeficit(pcl.getTotalDeficit());
			model.setTotalDiscounts(pcl.getTotalDiscounts());
			model.setTotalExpenditures(pcl.getTotalExpenditures());
			model.setTotalReturns(pcl.getTotalReturns());
			
			if(pcl.getCreatedAt() != null && pcl.getCreatedBy() != null) {
				model.setCreated(dayRepository.findById(pcl.getCreatedAt()).get().getBussinessDate() +" "+ userRepository.getAlias(pcl.getCreatedBy()));
			}
			if(pcl.getApprovedAt() != null && pcl.getApprovedBy() != null) {
				model.setApproved(dayRepository.findById(pcl.getApprovedAt()).get().getBussinessDate() +" "+ userRepository.getAlias(pcl.getApprovedBy()));
			}	
			if(pcl.getPostedAt() != null && pcl.getPostedBy() != null) {
				model.setPosted(dayRepository.findById(pcl.getPostedAt()).get().getBussinessDate() +" "+ userRepository.getAlias(pcl.getPostedBy()));
			}				
			models.add(model);
		}
		return models;
	}

	@Override
	public SalesListDetailModel saveDetail(SalesListDetail salesListDetail) {
		if(!validateDetail(salesListDetail)) {
			throw new InvalidEntryException("Could not save detail, Invalid entry");
		}
		SalesListDetailModel detail = new SalesListDetailModel();
		SalesListDetail d = salesListDetailRepository.save(salesListDetail);
		detail.setId(d.getId());
		detail.setProduct(d.getProduct());
		detail.setQtyDamaged(d.getQtyDamaged());
		detail.setTotalPacked(d.getTotalPacked());
		detail.setQtyOffered(d.getQtyOffered());
		detail.setQtyReturned(d.getQtyReturned());
		detail.setQtySold(d.getQtySold());
		detail.setSellingPriceVatIncl(d.getSellingPriceVatIncl());
		detail.setSellingPriceVatExcl(d.getSellingPriceVatExcl());
		return detail;
	}

	@Override
	public SalesListDetailModel getDetail(Long id) {
		SalesListDetailModel detail = new SalesListDetailModel();
		Optional<SalesListDetail> d = salesListDetailRepository.findById(id);
		if(!d.isPresent()) {
			throw new NotFoundException("Sales List detail not found");
		}
		detail.setId(d.get().getId());
		detail.setProduct(d.get().getProduct());
		detail.setQtyDamaged(d.get().getQtyDamaged());
		detail.setTotalPacked(d.get().getTotalPacked());
		detail.setQtyOffered(d.get().getQtyOffered());
		detail.setQtyReturned(d.get().getQtyReturned());
		detail.setQtySold(d.get().getQtySold());
		detail.setSellingPriceVatIncl(d.get().getSellingPriceVatIncl());
		detail.setSellingPriceVatExcl(d.get().getSellingPriceVatExcl());
		return detail;
	}

	@Override
	public List<SalesListDetailModel> getAllDetails(SalesList salesList) {
		List<SalesListDetail> details = salesListDetailRepository.findBySalesList(salesList);
		List<SalesListDetailModel> models = new ArrayList<SalesListDetailModel>();
		for(SalesListDetail detail : details) {
			SalesListDetailModel model = new SalesListDetailModel();
			model.setId(detail.getId());
			model.setProduct(detail.getProduct());
			model.setQtyDamaged(detail.getQtyDamaged());
			model.setTotalPacked(detail.getTotalPacked());
			model.setQtyOffered(detail.getQtyOffered());
			model.setQtyReturned(detail.getQtyReturned());
			model.setQtySold(detail.getQtySold());
			model.setSellingPriceVatIncl(detail.getSellingPriceVatIncl());
			model.setSellingPriceVatExcl(detail.getSellingPriceVatExcl());
			models.add(model);
		}
		return models;	
	}

	@Override
	public boolean archive(SalesList salesList) {
		if(!salesList.getStatus().equals("APPROVED")) {
			throw new InvalidOperationException("Could not process, only a approved sales list can be archived");
		}
		if(salesList.getTotalDeficit() > 0) {
			throw new InvalidOperationException("Could not process, non debt free document can not be archived");
		}
		salesList.setStatus("ARCHIVED");
		salesListRepository.saveAndFlush(salesList);
		return true;
	}

	@Override
	public boolean archiveAll() {
		List<SalesList> salesLists = salesListRepository.findAllPosted("APPROVED");
		if(salesLists.isEmpty()) {
			throw new NotFoundException("No Sales List to archive");
		}
		for(SalesList p : salesLists) {	
			if(!(p.getTotalDeficit() > 0)) {
				p.setStatus("ARCHIVED");
				salesListRepository.saveAndFlush(p);
			}		
		}
		return true;
	}
	
	@Override
	public SalesListModel approve(SalesList salesList, HttpServletRequest request) {
		
		SalesList slsl = salesListRepository.saveAndFlush(salesList);
		List<SalesListDetail> details = slsl.getSalesListDetails();
		Sale sale = new Sale();
		sale.setSalesDiscount(salesList.getTotalDiscounts());
		sale.setSalesExpenses(salesList.getTotalExpenditures());
		sale.setSalesCommission(salesList.getTotalCommission());
		sale.setCreatedAt(dayRepository.getCurrentBussinessDay().getId());
		sale.setCreatedBy(userService.getUserId(request));
		sale.setDay(dayRepository.getCurrentBussinessDay());
		sale.setReference("Sales list sales Ref# "+slsl.getNo());
		if(salesList.getSalesAgent() != null) {
			sale.setSalesAgent(salesList.getSalesAgent());
		}
		sale = saleRepository.saveAndFlush(sale);
		
		double totalAmountPacked = 0;
		double totalSales = 0;
		double totalReturns = 0;
		double totalOffered = 0;
		double totalDamages = 0;
		double totalDiscounts = slsl.getTotalDiscounts();
		double totalExpenditures = slsl.getTotalExpenditures();
		double totalCommission = slsl.getTotalCommission();
		double totalBank = slsl.getTotalBank();
		double totalCash = slsl.getTotalCash();
		double totalDeficit = slsl.getTotalDeficit();
		
		for(SalesListDetail d : details) {
			/**
			 * Grab stock qty and update stock
			 */
			Product product =productRepository.findById(d.getProduct().getId()).get();
			double stock = product.getStock();
			if(d.getQtyReturned() > 0) {
				stock = stock + (d.getQtyReturned());
				product.setStock(stock);				
			}
			productRepository.saveAndFlush(product);
			
			/**
			 * Create stock card
			 */
			if(d.getQtyReturned() > 0) {
				ProductStockCard stockCard = new ProductStockCard();
				stockCard.setQtyIn(d.getQtyReturned());
				stockCard.setProduct(product);
				stockCard.setBalance(stock);
				stockCard.setDay(dayRepository.getCurrentBussinessDay());
				stockCard.setReference("Returns. Ref #: "+slsl.getNo());
				productStockCardService.save(stockCard);
			}
			
			/**
			 * Register damages
			 */
			if(d.getQtyDamaged() > 0) {
				ProductDamage damage = new ProductDamage();
				damage.setQty(d.getQtyDamaged());
				damage.setCostPriceVatIncl(d.getCostPriceVatIncl());
				damage.setCostPriceVatExcl(d.getCostPriceVatExcl());
				damage.setSellingPriceVatIncl(d.getSellingPriceVatIncl());
				damage.setSellingPriceVatExcl(d.getCostPriceVatExcl());
				damage.setDay(dayRepository.getCurrentBussinessDay());
				damage.setProduct(d.getProduct());
				damage.setReference("Damaged in sales list. Ref# "+slsl.getNo());
				productDamageService.save(damage);
			}
			
			
			/**
			 * Register offers
			 */
			if(d.getQtyOffered() > 0) {
				ProductOffer offer = new ProductOffer();
				offer.setQty(d.getQtyOffered());
				offer.setCostPriceVatIncl(d.getCostPriceVatIncl());
				offer.setCostPriceVatExcl(d.getCostPriceVatExcl());
				offer.setSellingPriceVatIncl(d.getSellingPriceVatIncl());
				offer.setSellingPriceVatExcl(d.getCostPriceVatExcl());
				offer.setDay(dayRepository.getCurrentBussinessDay());
				offer.setProduct(d.getProduct());
				offer.setReference("Offered in sales list. Ref# "+slsl.getNo());
				productOfferService.save(offer);
			}
			
			/**
			 * Post to sales
			 * filter zero qtys
			 */
			if(d.getQtySold() > 0) {
				SaleDetail sd = new SaleDetail();
				sd.setProduct(product);
				sd.setSale(sale);
				sd.setQty(d.getQtySold());
				sd.setCostPriceVatIncl(d.getCostPriceVatIncl());
				sd.setCostPriceVatExcl(d.getCostPriceVatExcl());
				sd.setSellingPriceVatIncl(d.getSellingPriceVatIncl());
				sd.setSellingPriceVatExcl(d.getSellingPriceVatExcl());
				sd.setDiscount(0);
				sd.setTax(0);
				saleDetailRepository.saveAndFlush(sd);
			}
			
			
			totalAmountPacked = totalAmountPacked + d.getTotalPacked() * d.getSellingPriceVatIncl();
			totalSales = totalSales + d.getQtySold() * d.getSellingPriceVatIncl();
			totalReturns = totalReturns + d.getQtyReturned() * d.getSellingPriceVatIncl();
			totalOffered = totalOffered + d.getQtyOffered() * d.getSellingPriceVatIncl();
			totalDamages = totalDamages + d.getQtyDamaged() * d.getSellingPriceVatIncl();
		}
		
		/**
		 * Register Deficit
		 */
		if(totalDeficit > 0) {
			Debt debt = new Debt();
			debt.setNo("NA");
			debt.setStatus("PENDING");
			debt.setAmount(totalDeficit);
			debt.setBalance(totalDeficit);
			debt.setDay(dayRepository.getCurrentBussinessDay());
			debt.setSalesAgent(slsl.getSalesAgent());
			debt.setSalesList(slsl);
			debtService.create(debt, userRepository.findById(userService.getUserId(request)).get());			
		}
		
		if(totalDiscounts < 0) {
			throw new InvalidEntryException("Could not process, invalid discounts amount");
		}	
		if(totalExpenditures < 0) {
			throw new InvalidEntryException("Could not process, invalid expenses amount");
		}
		if(totalCommission < 0) {
			throw new InvalidEntryException("Could not process, invalid commission amount");
		}
		if(totalBank < 0) {
			throw new InvalidEntryException("Could not process, invalid bank amount");
		}
		if(totalCash < 0) {
			throw new InvalidEntryException("Could not process, invalid cash amount");
		}
		if(totalDeficit < 0) {
			throw new InvalidEntryException("Could not process, invalid deficit amount");
		}
		
		if(totalSales != totalDiscounts + totalExpenditures + totalCommission + totalBank + totalCash + totalDeficit) {
			throw new InvalidEntryException("Could not process, amounts do not tally ");
		}
		if(totalAmountPacked != totalSales +totalReturns + totalOffered + totalDamages) {
			throw new InvalidEntryException("Could not process, amounts do not tally");
		}
		
		SalesListModel model = new SalesListModel();
		model.setId(slsl.getId());
		model.setNo(slsl.getNo());
		model.setIssueDate(slsl.getIssueDate());
		model.setCustomer(slsl.getCustomer());
		model.setStatus(slsl.getStatus());
		model.setComments(slsl.getComments());
		model.setTotalAmountPacked(totalAmountPacked);
		model.setTotalSales(totalSales);
		model.setTotalOffered(totalOffered);
		model.setTotalReturns(totalReturns);
		model.setTotalDamages(totalDamages);
		if(slsl.getCreatedAt() != null && slsl.getCreatedBy() != null) {
			model.setCreated(dayRepository.findById(slsl.getCreatedAt()).get().getBussinessDate() +" "+ userRepository.getAlias(slsl.getCreatedBy()));
		}
		if(slsl.getApprovedAt() != null && slsl.getApprovedBy() != null) {
			model.setApproved(dayRepository.findById(slsl.getApprovedAt()).get().getBussinessDate() +" "+ userRepository.getAlias(slsl.getApprovedBy()));
		}	
		if(slsl.getPostedAt() != null && slsl.getPostedBy() != null) {
			model.setPosted(dayRepository.findById(slsl.getPostedAt()).get().getBussinessDate() +" "+ userRepository.getAlias(slsl.getPostedBy()));
		}	
		List<SalesListDetail> salesListDetails = slsl.getSalesListDetails();
		List<SalesListDetailModel> modelDetails = new ArrayList<SalesListDetailModel>();
		for(SalesListDetail d : salesListDetails) {
			SalesListDetailModel detail = new SalesListDetailModel();
			detail.setId(d.getId());
			detail.setProduct(d.getProduct());
			detail.setQtyDamaged(d.getQtyDamaged());
			detail.setTotalPacked(d.getTotalPacked());
			detail.setQtyOffered(d.getQtyOffered());
			detail.setQtyReturned(d.getQtyReturned());
			detail.setQtySold(d.getQtySold());
			detail.setSellingPriceVatIncl(d.getSellingPriceVatIncl());
			detail.setSellingPriceVatExcl(d.getSellingPriceVatExcl());
			modelDetails.add(detail);
		}
		model.setSalesListDetails(modelDetails);
		
		model.setTotalAmountPacked(totalAmountPacked);
		model.setTotalSales(totalSales);
		model.setTotalOffered(totalOffered);
		model.setTotalReturns(totalReturns);
		model.setTotalDamages(totalDamages);
		
		model.setTotalDiscounts(totalDiscounts);
		model.setTotalExpenditures(totalExpenditures);
		model.setTotalBank(totalBank);
		model.setTotalCash(totalCash);
		model.setTotalDeficit(totalDeficit);
		
		return model;
	}
	
	private boolean validate(SalesList salesList) {
		return true;
	}
	
	private boolean allowDelete(SalesList salesList) {
		return false;
	}
	
	private boolean validateDetail(SalesListDetail salesListDetail) {
		return true;
	}
	
	private boolean allowDeleteDetail(SalesListDetail salesListDetail) {
		return false;
	}
	
	
	@Override
	public SalesListDetailModel addDetail(SalesListDetail salesListDetail) {
		if(!validateDetail(salesListDetail)) {
			throw new InvalidEntryException("Could not save detail, Invalid entry");
		}
		SalesListDetailModel detail = new SalesListDetailModel();
		SalesListDetail d = salesListDetailRepository.save(salesListDetail);
		detail.setId(d.getId());
		detail.setProduct(d.getProduct());
		detail.setTotalPacked(d.getTotalPacked());
		detail.setCostPriceVatIncl(d.getCostPriceVatIncl());
		detail.setCostPriceVatExcl(d.getCostPriceVatExcl());
		detail.setSellingPriceVatIncl(d.getSellingPriceVatIncl());
		detail.setSellingPriceVatExcl(d.getSellingPriceVatExcl());
		return detail;
	}
	
	
	
	@Override
	public String generateSalesListNo(SalesList salesList) {
		Long number = salesList.getId();		
		String sNumber = number.toString();
		//return "SLR-"+Formater.formatNine(sNumber);
		return Formater.formatWithCurrentDate("SLR",sNumber);
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

	@Override
	public boolean downnloadFromSalesSheet(SalesList salesList) {
		Optional<SalesList> s = salesListRepository.findById(salesList.getId());
		if(!s.isPresent()) {
			throw new NotFoundException("SalesList not found");
		}
		if(!s.get().getStatus().equals("PENDING")) {
			throw new InvalidOperationException("Not a pending sales list");
		}
		Optional<SalesSheet> ss = salesSheetRepository.findBySalesList(s.get());
		if(!ss.isPresent()) {
			throw new NotFoundException("Correspoding sales sheet not found");
			
		}
		
		List<SalesListDetail> salesListDetails =  s.get().getSalesListDetails();
		double totalDiscount = 0;
		double totalExpenses = 0;
		double totalCommission = 0;
		List<SalesSheetSale> ssss = ss.get().getSalesSheetSales();
		for(SalesSheetSale salesSheetSale : ssss) {
			totalDiscount = totalDiscount + salesSheetSale.getTotalDiscount();
		}
		s.get().setTotalDiscounts(totalDiscount);
		List<SalesSheetExpense> expenses = ss.get().getSalesSheetExpenses();
		for(SalesSheetExpense salesSheetExpense : expenses) {
			totalExpenses = totalExpenses + salesSheetExpense.getAmount();
		}
		s.get().setTotalExpenditures(totalExpenses);
		salesListRepository.saveAndFlush(s.get());
		
		double qtySold = 0;
		for(SalesListDetail salesListDetail : salesListDetails) {
			List<SalesSheetSale> salesSheetSales = ss.get().getSalesSheetSales();
			
			
			for(SalesSheetSale salesSheetSale : salesSheetSales) {
				List<SalesSheetSaleDetail> slesSheetSaleDetails = salesSheetSale.getSalesSheetSaleDetails();
				for(SalesSheetSaleDetail salesSheetSaleDetail : slesSheetSaleDetails) {
					if(salesSheetSaleDetail.getProduct().getId() == salesListDetail.getProduct().getId()) {
						qtySold = qtySold + salesSheetSaleDetail.getQty();
					}
				}
			}
			SalesListDetail detail = salesListDetailRepository.findById(salesListDetail.getId()).get();
			detail.setQtySold(qtySold);
			detail.setQtyReturned(detail.getTotalPacked() - qtySold);
			detail.setQtyOffered(0);
			detail.setQtyDamaged(0);
			salesListDetailRepository.saveAndFlush(detail);
			qtySold = 0;
		}
		
		return false;
	}
	
	
	
}



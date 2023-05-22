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
import com.orbix.api.domain.Customer;
import com.orbix.api.domain.Product;
import com.orbix.api.domain.ProductStockCard;
import com.orbix.api.domain.Sale;
import com.orbix.api.domain.SaleDetail;
import com.orbix.api.domain.SalesInvoice;
import com.orbix.api.domain.SalesInvoiceDetail;
import com.orbix.api.exceptions.InvalidEntryException;
import com.orbix.api.exceptions.InvalidOperationException;
import com.orbix.api.exceptions.NotFoundException;
import com.orbix.api.models.RecordModel;
import com.orbix.api.models.SalesInvoiceDetailModel;
import com.orbix.api.models.SalesInvoiceModel;
import com.orbix.api.repositories.DayRepository;
import com.orbix.api.repositories.ProductRepository;
import com.orbix.api.repositories.SaleRepository;
import com.orbix.api.repositories.SaleDetailRepository;
import com.orbix.api.repositories.SalesInvoiceDetailRepository;
import com.orbix.api.repositories.SalesInvoiceRepository;
import com.orbix.api.repositories.UserRepository;

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
public class SalesInvoiceServiceImpl implements SalesInvoiceService {
	
	private final SalesInvoiceRepository salesInvoiceRepository;
	private final SalesInvoiceDetailRepository salesInvoiceDetailRepository;
	private final UserRepository userRepository;
	private final DayRepository dayRepository;
	private final ProductRepository productRepository;
	private final ProductStockCardService productStockCardService;
	private final SaleRepository saleRepository;
	private final SaleDetailRepository saleDetailRepository;
	private final UserService userService;
	
	@Override
	public SalesInvoiceModel save(SalesInvoice salesInvoice) {
		if(!validate(salesInvoice)) {
			throw new InvalidEntryException("Could not save, Sales Invoice invalid");
		}
		SalesInvoice inv = salesInvoiceRepository.save(salesInvoice);
		if(inv.getNo().equals("NA")) {
			inv.setNo(generateSalesInvoiceNo(inv));
			inv = salesInvoiceRepository.save(inv);
		}			
		SalesInvoiceModel model = new SalesInvoiceModel();
		model.setId(inv.getId());
		model.setNo(inv.getNo());
		model.setCustomer(inv.getCustomer());
		model.setSalesAgent(inv.getSalesAgent());
		model.setStatus(inv.getStatus());
		model.setBillingAddress(inv.getBillingAddress());
		model.setShippingAddress(inv.getShippingAddress());
		model.setTotalVat(inv.getTotalVat());
		model.setAmountVatExcl(inv.getAmountVatExcl());
		model.setAmountVatIncl(inv.getAmountVatIncl());
		model.setDiscount(inv.getDiscount());
		model.setOtherCharges(inv.getOtherCharges());
		model.setNetAmount(inv.getNetAmount());
		model.setComments(inv.getComments());
		model.setBalance(inv.getBalance());
		if(inv.getCreatedAt() != null && inv.getCreatedBy() != null) {
			model.setCreated(dayRepository.findById(inv.getCreatedAt()).get().getBussinessDate() +" "+ userRepository.getAlias(inv.getCreatedBy()));
		}
		if(inv.getApprovedAt() != null && inv.getApprovedBy() != null) {
			model.setApproved(dayRepository.findById(inv.getApprovedAt()).get().getBussinessDate() +" "+ userRepository.getAlias(inv.getApprovedBy()));
		}		
		return model;
	}
	
	@Override
	public SalesInvoiceModel get(Long id) {
		SalesInvoiceModel model = new SalesInvoiceModel();
		Optional<SalesInvoice> inv = salesInvoiceRepository.findById(id);
		if(!inv.isPresent()) {
			throw new NotFoundException("SalesInvoice not found");
		}
		model.setId(inv.get().getId());
		model.setNo(inv.get().getNo());
		model.setCustomer(inv.get().getCustomer());
		model.setSalesAgent(inv.get().getSalesAgent());
		model.setStatus(inv.get().getStatus());
		model.setBillingAddress(inv.get().getBillingAddress());
		model.setShippingAddress(inv.get().getShippingAddress());
		model.setTotalVat(inv.get().getTotalVat());
		model.setAmountVatExcl(inv.get().getAmountVatExcl());
		model.setAmountVatIncl(inv.get().getAmountVatIncl());
		model.setDiscount(inv.get().getDiscount());
		model.setOtherCharges(inv.get().getOtherCharges());
		model.setNetAmount(inv.get().getNetAmount());
		model.setComments(inv.get().getComments());
		model.setBalance(inv.get().getBalance());
		if(inv.get().getCreatedAt() != null && inv.get().getCreatedBy() != null) {
			model.setCreated(dayRepository.findById(inv.get().getCreatedAt()).get().getBussinessDate() +" "+ userRepository.getAlias(inv.get().getCreatedBy()));
		}
		if(inv.get().getApprovedAt() != null && inv.get().getApprovedBy() != null) {
			model.setApproved(dayRepository.findById(inv.get().getApprovedAt()).get().getBussinessDate() +" "+ userRepository.getAlias(inv.get().getApprovedBy()));
		}		
		List<SalesInvoiceDetail> salesInvoiceDetails = inv.get().getSalesInvoiceDetails();
		List<SalesInvoiceDetailModel> modelDetails = new ArrayList<SalesInvoiceDetailModel>();
		for(SalesInvoiceDetail d : salesInvoiceDetails) {
			SalesInvoiceDetailModel modelDetail = new SalesInvoiceDetailModel();
			modelDetail.setId(d.getId());
			modelDetail.setProduct(d.getProduct());
			modelDetail.setQty(d.getQty());
			modelDetail.setCostPriceVatIncl(d.getCostPriceVatIncl());
			modelDetail.setCostPriceVatExcl(d.getCostPriceVatExcl());
			modelDetail.setSellingPriceVatIncl(d.getSellingPriceVatIncl());
			modelDetail.setSellingPriceVatExcl(d.getSellingPriceVatExcl());
			modelDetail.setSalesInvoice(d.getSalesInvoice());
			modelDetails.add(modelDetail);
		}
		model.setSalesInvoiceDetails(modelDetails);
		return model;
	}
	
	@Override
	public SalesInvoiceModel getByNo(String no) {
		SalesInvoiceModel model = new SalesInvoiceModel();
		Optional<SalesInvoice> inv = salesInvoiceRepository.findByNo(no);
		if(!inv.isPresent()) {
			throw new NotFoundException("SalesInvoice not found");
		}
		model.setId(inv.get().getId());
		model.setNo(inv.get().getNo());
		model.setCustomer(inv.get().getCustomer());
		model.setSalesAgent(inv.get().getSalesAgent());
		model.setStatus(inv.get().getStatus());
		model.setBillingAddress(inv.get().getBillingAddress());
		model.setShippingAddress(inv.get().getShippingAddress());
		model.setTotalVat(inv.get().getTotalVat());
		model.setAmountVatExcl(inv.get().getAmountVatExcl());
		model.setAmountVatIncl(inv.get().getAmountVatIncl());
		model.setDiscount(inv.get().getDiscount());
		model.setOtherCharges(inv.get().getOtherCharges());
		model.setNetAmount(inv.get().getNetAmount());
		model.setComments(inv.get().getComments());
		model.setBalance(inv.get().getBalance());
		if(inv.get().getCreatedAt() != null && inv.get().getCreatedBy() != null) {
			model.setCreated(dayRepository.findById(inv.get().getCreatedAt()).get().getBussinessDate() +" "+ userRepository.getAlias(inv.get().getCreatedBy()));
		}
		if(inv.get().getApprovedAt() != null && inv.get().getApprovedBy() != null) {
			model.setApproved(dayRepository.findById(inv.get().getApprovedAt()).get().getBussinessDate() +" "+ userRepository.getAlias(inv.get().getApprovedBy()));
		}		
		List<SalesInvoiceDetail> salesInvoiceDetails = inv.get().getSalesInvoiceDetails();
		List<SalesInvoiceDetailModel> modelDetails = new ArrayList<SalesInvoiceDetailModel>();
		for(SalesInvoiceDetail d : salesInvoiceDetails) {
			SalesInvoiceDetailModel modelDetail = new SalesInvoiceDetailModel();
			modelDetail.setId(d.getId());
			modelDetail.setProduct(d.getProduct());
			modelDetail.setQty(d.getQty());
			modelDetail.setCostPriceVatIncl(d.getCostPriceVatIncl());
			modelDetail.setCostPriceVatExcl(d.getCostPriceVatExcl());
			modelDetail.setSellingPriceVatIncl(d.getSellingPriceVatIncl());
			modelDetail.setSellingPriceVatExcl(d.getSellingPriceVatExcl());
			modelDetail.setSalesInvoice(d.getSalesInvoice());
			modelDetails.add(modelDetail);
		}
		model.setSalesInvoiceDetails(modelDetails);
		return model;
	}
	
	@Override
	public boolean delete(SalesInvoice salesInvoice) {
		if(!allowDelete(salesInvoice)) {
			throw new InvalidOperationException("Deleting the selected Sales Invoice is not allowed");
		}
		salesInvoiceRepository.delete(salesInvoice);
		return true;
	}
	
	@Override
	public List<SalesInvoiceModel> getAllVisible() {
		List<String> statuses = new ArrayList<String>();
		statuses.add("BLANK");
		statuses.add("PENDING");
		statuses.add("APPROVED");
		statuses.add("PARTIAL");
		statuses.add("PAID");
		List<SalesInvoice> invoices = salesInvoiceRepository.findAllVissible(statuses);
		List<SalesInvoiceModel> models = new ArrayList<SalesInvoiceModel>();
		for(SalesInvoice inv : invoices) {
			SalesInvoiceModel model = new SalesInvoiceModel();
			model.setId(inv.getId());
			model.setNo(inv.getNo());
			model.setCustomer(inv.getCustomer());
			model.setSalesAgent(inv.getSalesAgent());
			model.setStatus(inv.getStatus());
			model.setBillingAddress(inv.getBillingAddress());
			model.setShippingAddress(inv.getShippingAddress());
			model.setTotalVat(inv.getTotalVat());
			model.setAmountVatExcl(inv.getAmountVatExcl());
			model.setAmountVatIncl(inv.getAmountVatIncl());
			model.setDiscount(inv.getDiscount());
			model.setOtherCharges(inv.getOtherCharges());
			model.setNetAmount(inv.getNetAmount());
			model.setComments(inv.getComments());
			model.setBalance(inv.getBalance());
			if(inv.getCreatedAt() != null && inv.getCreatedBy() != null) {
				model.setCreated(dayRepository.findById(inv.getCreatedAt()).get().getBussinessDate() +" "+ userRepository.getAlias(inv.getCreatedBy()));
			}
			if(inv.getApprovedAt() != null && inv.getApprovedBy() != null) {
				model.setApproved(dayRepository.findById(inv.getApprovedAt()).get().getBussinessDate() +" "+ userRepository.getAlias(inv.getApprovedBy()));
			}			
			models.add(model);
		}
		return models;
	}
	
	@Override
	public List<SalesInvoiceModel> getByCustomerAndApprovedOrPartial(Customer customer) {
		List<String> statuses = new ArrayList<String>();
		statuses.add("APPROVED");
		statuses.add("PARTIAL");
		List<SalesInvoice> invoices = salesInvoiceRepository.findByCustomerAndApprovedOrPosted(customer, statuses);
		List<SalesInvoiceModel> models = new ArrayList<SalesInvoiceModel>();
		for(SalesInvoice inv : invoices) {
			SalesInvoiceModel model = new SalesInvoiceModel();
			model.setId(inv.getId());
			model.setNo(inv.getNo());
			model.setCustomer(inv.getCustomer());
			model.setSalesAgent(inv.getSalesAgent());
			model.setStatus(inv.getStatus());
			model.setBillingAddress(inv.getBillingAddress());
			model.setShippingAddress(inv.getShippingAddress());
			model.setTotalVat(inv.getTotalVat());
			model.setAmountVatExcl(inv.getAmountVatExcl());
			model.setAmountVatIncl(inv.getAmountVatIncl());
			model.setDiscount(inv.getDiscount());
			model.setOtherCharges(inv.getOtherCharges());
			model.setNetAmount(inv.getNetAmount());
			model.setComments(inv.getComments());
			model.setBalance(inv.getBalance());
			if(inv.getCreatedAt() != null && inv.getCreatedBy() != null) {
				model.setCreated(dayRepository.findById(inv.getCreatedAt()).get().getBussinessDate() +" "+ userRepository.getAlias(inv.getCreatedBy()));
			}
			if(inv.getApprovedAt() != null && inv.getApprovedBy() != null) {
				model.setApproved(dayRepository.findById(inv.getApprovedAt()).get().getBussinessDate() +" "+ userRepository.getAlias(inv.getApprovedBy()));
			}			
			models.add(model);
		}
		return models;
	}
	
	@Override
	public SalesInvoiceDetailModel saveDetail(SalesInvoiceDetail salesInvoiceDetail) {
		if(!validateDetail(salesInvoiceDetail)) {
			throw new InvalidEntryException("Could not save detail, Invalid entry");
		}
		SalesInvoiceDetailModel model = new SalesInvoiceDetailModel();
		SalesInvoiceDetail d = salesInvoiceDetailRepository.save(salesInvoiceDetail);
		model.setId(d.getId());
		model.setProduct(d.getProduct());
		model.setQty(d.getQty());
		model.setCostPriceVatIncl(d.getCostPriceVatIncl());
		model.setCostPriceVatExcl(d.getCostPriceVatExcl());
		model.setSellingPriceVatIncl(d.getSellingPriceVatIncl());
		model.setSellingPriceVatExcl(d.getSellingPriceVatExcl());
		model.setSalesInvoice(d.getSalesInvoice());
		return model;
	}
	
	@Override
	public SalesInvoiceDetailModel getDetail(Long id) {
		SalesInvoiceDetailModel model = new SalesInvoiceDetailModel();
		Optional<SalesInvoiceDetail> d = salesInvoiceDetailRepository.findById(id);
		if(!d.isPresent()) {
			throw new NotFoundException("SalesInvoice detail not found");
		}
		model.setId(d.get().getId());
		model.setProduct(d.get().getProduct());
		model.setQty(d.get().getQty());
		model.setCostPriceVatIncl(d.get().getCostPriceVatIncl());
		model.setCostPriceVatExcl(d.get().getCostPriceVatExcl());
		model.setSellingPriceVatIncl(d.get().getSellingPriceVatIncl());
		model.setSellingPriceVatExcl(d.get().getSellingPriceVatExcl());
		model.setSalesInvoice(d.get().getSalesInvoice());
		return model;
	}
	
	@Override
	public boolean deleteDetail(SalesInvoiceDetail salesInvoiceDetail) {
		if(!allowDeleteDetail(salesInvoiceDetail)) {
			throw new InvalidOperationException("Deleting the selected Invoice detail is not allowed");
		}
		salesInvoiceDetailRepository.delete(salesInvoiceDetail);
		return true;
	}
	
	@Override
	public List<SalesInvoiceDetailModel> getAllDetails(SalesInvoice salesInvoice) {
		List<SalesInvoiceDetail> details = salesInvoiceDetailRepository.findBySalesInvoice(salesInvoice);
		List<SalesInvoiceDetailModel> models = new ArrayList<SalesInvoiceDetailModel>();
		for(SalesInvoiceDetail d : details) {
			SalesInvoiceDetailModel model = new SalesInvoiceDetailModel();
			model.setId(d.getId());
			model.setProduct(d.getProduct());
			model.setQty(d.getQty());
			model.setCostPriceVatIncl(d.getCostPriceVatIncl());
			model.setCostPriceVatExcl(d.getCostPriceVatExcl());
			model.setSellingPriceVatIncl(d.getSellingPriceVatIncl());
			model.setSellingPriceVatExcl(d.getSellingPriceVatExcl());
			model.setSalesInvoice(d.getSalesInvoice());
			models.add(model);
		}
		return models;	
	}
	
	@Override
	public boolean archive(SalesInvoice salesInvoice) {
		if(!(salesInvoice.getStatus().equals("APPROVED") && salesInvoice.getBalance() == 0)) {
			throw new InvalidOperationException("Could not process, only a fully PAID Invoice can be archived");
		}
		salesInvoice.setStatus("ARCHIVED");
		salesInvoiceRepository.saveAndFlush(salesInvoice);
		return true;
	}
	
	@Override
	public boolean archiveAll() {
		// TODO Auto-generated method stub
		return false;
	}	
	
	private boolean validate(SalesInvoice salesInvoice) {
		return true;
	}
	
	private boolean allowDelete(SalesInvoice salesInvoice) {
		return true;
	}
	
	private boolean validateDetail(SalesInvoiceDetail salesInvoiceDetail) {
		return true;
	}
	
	private boolean allowDeleteDetail(SalesInvoiceDetail salesInvoiceDetail) {
		return true;
	}
	
	private String generateSalesInvoiceNo(SalesInvoice salesInvoice) {
		Long number = salesInvoice.getId();		
		String sNumber = number.toString();
		//return "SNV-"+Formater.formatSix(sNumber);
		return Formater.formatWithCurrentDate("SNV",sNumber);
	}

	@Override
	public SalesInvoiceModel post(SalesInvoice salesInvoice, HttpServletRequest request) {
		/**
		 * Save invoice
		 * Deduct products from stock
		 * Update stock cards
		 */
		SalesInvoice inv = salesInvoiceRepository.saveAndFlush(salesInvoice);
		
		List<SalesInvoiceDetail> details = inv.getSalesInvoiceDetails();
		double amount = 0;
		
		Sale sale = new Sale();
		sale.setType("Credit");
		sale.setCreatedAt(dayRepository.getCurrentBussinessDay().getId());
		sale.setCreatedBy(userService.getUserId(request));
		sale.setDay(dayRepository.getCurrentBussinessDay());
		sale.setReference("Credit sales Ref# "+salesInvoice.getNo());
		//SalesInvoice sinc = inv;
		//sinc = salesInvoiceRepository.saveAndFlush(sinc);
		sale.setSalesInvoice(inv);
		sale.setSalesAgent(inv.getSalesAgent());
		sale = saleRepository.saveAndFlush(sale);
		
		for(SalesInvoiceDetail d : details) {
			
			
			/**
			 * Update stocks
			 * Create stock card
			 * First, take initial stock value
			 * Update stock
			 * Add qty to initial stock value to obtain final stock value
			 * Create stock card with the final stock value
			 */
			/**
			 * Here, update stock card
			 */
			Product product =productRepository.findById(d.getProduct().getId()).get();
			double stock = product.getStock() - d.getQty();
			product.setStock(stock);
			productRepository.saveAndFlush(product);
			
			/**
			 * Post to sales
			 */
			SaleDetail sd = new SaleDetail();
			sd.setProduct(product);
			sd.setSale(sale);
			sd.setQty(d.getQty());
			sd.setCostPriceVatIncl(d.getCostPriceVatIncl());
			sd.setCostPriceVatExcl(d.getCostPriceVatExcl());
			sd.setSellingPriceVatIncl(d.getSellingPriceVatIncl());
			sd.setSellingPriceVatExcl(d.getSellingPriceVatExcl());
			sd.setDiscount(0);
			sd.setTax(0);
			saleDetailRepository.saveAndFlush(sd);
			
			/**
			 * Now create stock card
			 */
			ProductStockCard stockCard = new ProductStockCard();
			stockCard.setQtyOut(d.getQty());
			stockCard.setProduct(product);
			stockCard.setBalance(stock);
			stockCard.setDay(dayRepository.getCurrentBussinessDay());
			stockCard.setReference("Credit Sales. Ref #: "+inv.getNo());
			productStockCardService.save(stockCard);
			
			amount = amount + (d.getQty() * d.getSellingPriceVatIncl());
		}
		inv.setBalance(amount);
		inv = salesInvoiceRepository.saveAndFlush(inv);
		SalesInvoiceModel model = new SalesInvoiceModel();
		model.setId(inv.getId());
		model.setNo(inv.getNo());
		model.setCustomer(inv.getCustomer());
		model.setSalesAgent(inv.getSalesAgent());
		model.setStatus(inv.getStatus());
		model.setBillingAddress(inv.getBillingAddress());
		model.setShippingAddress(inv.getShippingAddress());
		model.setTotalVat(inv.getTotalVat());
		model.setAmountVatExcl(inv.getAmountVatExcl());
		model.setAmountVatIncl(inv.getAmountVatIncl());
		model.setDiscount(inv.getDiscount());
		model.setOtherCharges(inv.getOtherCharges());
		model.setNetAmount(inv.getNetAmount());
		model.setComments(inv.getComments());
		model.setBalance(inv.getBalance());
		if(inv.getCreatedAt() != null && inv.getCreatedBy() != null) {
			model.setCreated(dayRepository.findById(inv.getCreatedAt()).get().getBussinessDate() +" "+ userRepository.getAlias(inv.getCreatedBy()));
		}
		if(inv.getApprovedAt() != null && inv.getApprovedBy() != null) {
			model.setApproved(dayRepository.findById(inv.getApprovedAt()).get().getBussinessDate() +" "+ userRepository.getAlias(inv.getApprovedBy()));
		}		
		return model;
	}
	
	@Override
	public RecordModel requestSalesInvoiceNo() {
		Long id = 1L;
		try {
			id = salesInvoiceRepository.getLastId() + 1;
		}catch(Exception e) {}
		RecordModel model = new RecordModel();
		model.setNo(Formater.formatWithCurrentDate("SNV",id.toString()));
		return model;
	}
}

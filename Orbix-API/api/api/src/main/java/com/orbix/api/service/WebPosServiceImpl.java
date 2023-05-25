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
import com.orbix.api.domain.SalesAgent;
import com.orbix.api.domain.SalesInvoice;
import com.orbix.api.domain.SalesInvoiceDetail;
import com.orbix.api.domain.WebPos;
import com.orbix.api.domain.WebPosApproveData;
import com.orbix.api.exceptions.InvalidEntryException;
import com.orbix.api.exceptions.InvalidOperationException;
import com.orbix.api.exceptions.NotFoundException;
import com.orbix.api.models.RecordModel;
import com.orbix.api.models.SalesInvoiceDetailModel;
import com.orbix.api.models.SalesInvoiceModel;
import com.orbix.api.models.WebPosModel;
import com.orbix.api.repositories.DayRepository;
import com.orbix.api.repositories.ProductRepository;
import com.orbix.api.repositories.SaleDetailRepository;
import com.orbix.api.repositories.SaleRepository;
import com.orbix.api.repositories.SalesAgentRepository;
import com.orbix.api.repositories.SalesInvoiceDetailRepository;
import com.orbix.api.repositories.SalesInvoiceRepository;
import com.orbix.api.repositories.UserRepository;
import com.orbix.api.repositories.WebPosRepository;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Godfrey
 *
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class WebPosServiceImpl implements WebPosService{
	private final WebPosRepository webPosRepository;
	private final SalesInvoiceDetailRepository salesInvoiceDetailRepository;
	private final UserRepository userRepository;
	private final DayRepository dayRepository;
	private final ProductRepository productRepository;
	private final ProductStockCardService productStockCardService;
	private final SaleRepository saleRepository;
	private final SaleDetailRepository saleDetailRepository;
	private final UserService userService;
	private final SalesAgentRepository salesAgentRepository;
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Override
	public WebPosModel save(WebPos webPos) {
		
		WebPosModel model = new WebPosModel();
		WebPos d = webPosRepository.save(webPos);
		model.setId(d.getId());
		model.setProduct(d.getProduct());
		model.setQty(d.getQty());
		model.setCostPriceVatIncl(d.getCostPriceVatIncl());
		model.setCostPriceVatExcl(d.getCostPriceVatExcl());
		model.setSellingPriceVatIncl(d.getSellingPriceVatIncl());
		model.setSellingPriceVatExcl(d.getSellingPriceVatExcl());
		
		if(d.getCreatedAt() != null && d.getCreatedBy() != null) {
			model.setCreated(dayRepository.findById(d.getCreatedAt()).get().getBussinessDate() +" "+ userRepository.getAlias(d.getCreatedBy()));
		}
		return model;
	}
	
	@Override
	public WebPosModel get(Long id) {
		WebPosModel model = new WebPosModel();
		Optional<WebPos> d = webPosRepository.findById(id);
		if(!d.isPresent()) {
			throw new NotFoundException("Detail not found");
		}
		model.setId(d.get().getId());
		model.setProduct(d.get().getProduct());
		model.setQty(d.get().getQty());
		model.setCostPriceVatIncl(d.get().getCostPriceVatIncl());
		model.setCostPriceVatExcl(d.get().getCostPriceVatExcl());
		model.setSellingPriceVatIncl(d.get().getSellingPriceVatIncl());
		model.setSellingPriceVatExcl(d.get().getSellingPriceVatExcl());
		return model;
	}
	
	@Override
	public boolean delete(WebPos webPos) {		
		webPosRepository.delete(webPos);
		return true;
	}
	
	@Override
	public List<WebPosModel> getAll() {
		List<WebPos> details = webPosRepository.findAll();
		List<WebPosModel> models = new ArrayList<WebPosModel>();
		for(WebPos d : details) {
			WebPosModel model = new WebPosModel();
			model.setId(d.getId());
			model.setProduct(d.getProduct());
			model.setQty(d.getQty());
			model.setCostPriceVatIncl(d.getCostPriceVatIncl());
			model.setCostPriceVatExcl(d.getCostPriceVatExcl());
			model.setSellingPriceVatIncl(d.getSellingPriceVatIncl());
			model.setSellingPriceVatExcl(d.getSellingPriceVatExcl());
			if(d.getCreatedAt() != null && d.getCreatedBy() != null) {
				model.setCreated(dayRepository.findById(d.getCreatedAt()).get().getBussinessDate() +" "+ userRepository.getAlias(d.getCreatedBy()));
			}
			models.add(model);
		}
		return models;	
	}
	

	@Override
	public boolean approve(WebPosApproveData data, HttpServletRequest request) {
		/**
		 * Save invoice
		 * Deduct products from stock
		 * Update stock cards
		 */
		
		List<WebPos> details = webPosRepository.findAll();
		double amount = 0;
		for(WebPos w : details) {
			amount = amount + w.getQty() * w.getSellingPriceVatIncl();
		}
		if(amount != data.getAmount()) {
			throw new InvalidOperationException("Input amount does not match with till amount");
		}
		
		Optional<SalesAgent> a = salesAgentRepository.findById(data.getSalesAgent().getId());
		if(!a.isPresent()) {
			throw new NotFoundException("Sales Agent not found");
		}
		if(!a.get().isWebPosEnabled()) {
			throw new InvalidOperationException("Web POS not enabled for the selected Sales agent.");
		}
			
		
		
		Sale sale = new Sale();
		sale.setType("Cash");
		sale.setCreatedAt(dayRepository.getCurrentBussinessDay().getId());
		sale.setCreatedBy(userService.getUserId(request));
		sale.setDay(dayRepository.getCurrentBussinessDay());
		sale.setSalesAgent(a.get());
		sale.setReference("Web POS");
		sale = saleRepository.saveAndFlush(sale);
		
		for(WebPos d : details) {
			
			
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
			stockCard.setReference("Web POS Sale");
			productStockCardService.save(stockCard);			
		}
		/*
		 * Clear Web POS
		 */
		webPosRepository.deleteAll();
				
		return true;
	}
	
	
}




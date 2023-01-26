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
import com.orbix.api.domain.Product;
import com.orbix.api.domain.ProductDamage;
import com.orbix.api.domain.ProductOffer;
import com.orbix.api.domain.ProductStockCard;
import com.orbix.api.domain.Sale;
import com.orbix.api.domain.SaleDetail;
import com.orbix.api.domain.SalesList;
import com.orbix.api.domain.SalesListDetail;
import com.orbix.api.domain.SalesSheet;
import com.orbix.api.domain.SalesSheetSale;
import com.orbix.api.domain.SalesSheetSaleDetail;
import com.orbix.api.exceptions.InvalidEntryException;
import com.orbix.api.exceptions.InvalidOperationException;
import com.orbix.api.exceptions.NotFoundException;
import com.orbix.api.models.SalesListDetailModel;
import com.orbix.api.models.SalesListModel;
import com.orbix.api.models.SalesSheetModel;
import com.orbix.api.models.SalesSheetSaleDetailModel;
import com.orbix.api.models.SalesSheetSaleModel;
import com.orbix.api.repositories.DayRepository;
import com.orbix.api.repositories.ProductRepository;
import com.orbix.api.repositories.SaleDetailRepository;
import com.orbix.api.repositories.SaleRepository;
import com.orbix.api.repositories.SalesListDetailRepository;
import com.orbix.api.repositories.SalesListRepository;
import com.orbix.api.repositories.SalesSheetRepository;
import com.orbix.api.repositories.UserRepository;

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
public class SalesSheetServiceImpl implements SalesSheetService{
	
	private final SalesListRepository salesListRepository;
	private final SalesListDetailRepository salesListDetailRepository;
	private final UserRepository userRepository;
	private final UserService userService;
	private final DayRepository dayRepository;
	private final ProductRepository productRepository;
	private final ProductStockCardService productStockCardService;
	private final SaleService saleService;
	private final SaleRepository saleRepository;
	private final SaleDetailRepository saleDetailRepository;
	private final ProductDamageService productDamageService;
	private final ProductOfferService productOfferService;
	private final DebtService debtService;
	private final SalesSheetRepository salesSheetRepository;
	
	@Override
	public String generateSalesSheetNo(SalesSheet salesSheet) {
		Long number = salesSheet.getId();		
		String sNumber = number.toString();
		return Formater.formatWithCurrentDate("SSH",sNumber);
	}
	
	
	
	

	@Override
	public SalesSheetModel get(Long id) {
		SalesSheetModel salesSheetModel = new SalesSheetModel();
		Optional<SalesSheet> pcl = salesSheetRepository.findById(id);
		if(!pcl.isPresent()) {
			throw new NotFoundException("Sales Sheet not found");
		}
		salesSheetModel.setId(pcl.get().getId());
		salesSheetModel.setNo(pcl.get().getNo());
		salesSheetModel.setSalesAgentName(pcl.get().getSalesList().getSalesAgent().getName());
		if(pcl.get().isConfirmed()) {
			salesSheetModel.setConfirmed("CONFIRMED");
		}else {
			salesSheetModel.setConfirmed("NOT CONFIRMED");
		}
		
		
		List<SalesSheetSale> salesSheetSales = pcl.get().getSalesSheetSales();
		List<SalesSheetSaleModel> salesSheetSaleModels = new ArrayList<SalesSheetSaleModel>();
		for(SalesSheetSale salesSheetSale : salesSheetSales) {
			SalesSheetSaleModel salesSheetSaleModel = new SalesSheetSaleModel();
			salesSheetSaleModel.setId(salesSheetSale.getId());
			salesSheetSaleModel.setNo(salesSheetSale.getNo());
			salesSheetSaleModel.setCustomerName(salesSheetSale.getCustomerName());
			salesSheetSaleModel.setCustomerMobile(salesSheetSale.getCustomerMobile());
			salesSheetSaleModel.setCustomerLocation(salesSheetSale.getCustomerLocation());
			salesSheetSaleModel.setTotalAmount(salesSheetSale.getTotalAmount());
			salesSheetSaleModel.setTotalPaid(salesSheetSale.getTotalPaid());
			salesSheetSaleModel.setTotalDiscount(salesSheetSale.getTotalDiscount());
			salesSheetSaleModel.setTotalCharges(salesSheetSale.getTotalCharges());
			salesSheetSaleModel.setTotalDue(salesSheetSale.getTotalDue());
			List<SalesSheetSaleDetailModel> salesSheetSaleDetailModels = new ArrayList<SalesSheetSaleDetailModel>();
			for(SalesSheetSaleDetail salesSheetSaleDetail : salesSheetSale.getSalesSheetSaleDetails()) {
				SalesSheetSaleDetailModel salesSheetSaleDetailModel = new SalesSheetSaleDetailModel();
				salesSheetSaleDetailModel.setBarcode(salesSheetSaleDetail.getProduct().getBarcode());
				salesSheetSaleDetailModel.setCode(salesSheetSaleDetail.getProduct().getCode());
				salesSheetSaleDetailModel.setDescription(salesSheetSaleDetail.getProduct().getDescription());
				salesSheetSaleDetailModel.setPrice(salesSheetSaleDetail.getSellingPriceVatIncl());
				salesSheetSaleDetailModel.setQty(salesSheetSaleDetail.getQty());
				salesSheetSaleDetailModels.add(salesSheetSaleDetailModel);	
			}
			salesSheetSaleModel.setSalesSheetSaleDetails(salesSheetSaleDetailModels);
			salesSheetSaleModels.add(salesSheetSaleModel);			
		}
		salesSheetModel.setSalesSheetSales(salesSheetSaleModels);
		
		return salesSheetModel;
	}

	@Override
	public SalesSheetModel getByNo(String no) {
		SalesSheetModel salesSheetModel = new SalesSheetModel();
		Optional<SalesSheet> pcl = salesSheetRepository.findByNo(no);
		if(!pcl.isPresent()) {
			throw new NotFoundException("Sales Sheet not found");
		}
		salesSheetModel.setId(pcl.get().getId());
		salesSheetModel.setNo(pcl.get().getNo());
		salesSheetModel.setSalesAgentName(pcl.get().getSalesList().getSalesAgent().getName());
		if(pcl.get().isConfirmed()) {
			salesSheetModel.setConfirmed("CONFIRMED");
		}else {
			salesSheetModel.setConfirmed("NOT CONFIRMED");
		}
		
		List<SalesSheetSale> salesSheetSales = pcl.get().getSalesSheetSales();
		List<SalesSheetSaleModel> salesSheetSaleModels = new ArrayList<SalesSheetSaleModel>();
		for(SalesSheetSale salesSheetSale : salesSheetSales) {
			SalesSheetSaleModel salesSheetSaleModel = new SalesSheetSaleModel();
			salesSheetSaleModel.setId(salesSheetSale.getId());
			salesSheetSaleModel.setNo(salesSheetSale.getNo());
			salesSheetSaleModel.setCustomerName(salesSheetSale.getCustomerName());
			salesSheetSaleModel.setCustomerMobile(salesSheetSale.getCustomerMobile());
			salesSheetSaleModel.setCustomerLocation(salesSheetSale.getCustomerLocation());
			salesSheetSaleModel.setTotalAmount(salesSheetSale.getTotalAmount());
			salesSheetSaleModel.setTotalPaid(salesSheetSale.getTotalPaid());
			salesSheetSaleModel.setTotalDiscount(salesSheetSale.getTotalDiscount());
			salesSheetSaleModel.setTotalCharges(salesSheetSale.getTotalCharges());
			salesSheetSaleModel.setTotalDue(salesSheetSale.getTotalDue());
			//if(salesSheetSale.getCompletedAt() !=  null) {
				//salesSheetSaleModel.setCompletedAt(salesSheetSale.getCompletedAt().toString());
			//}
			
			List<SalesSheetSaleDetailModel> salesSheetSaleDetailModels = new ArrayList<SalesSheetSaleDetailModel>();
			for(SalesSheetSaleDetail salesSheetSaleDetail : salesSheetSale.getSalesSheetSaleDetails()) {
				SalesSheetSaleDetailModel salesSheetSaleDetailModel = new SalesSheetSaleDetailModel();
				salesSheetSaleDetailModel.setBarcode(salesSheetSaleDetail.getProduct().getBarcode());
				salesSheetSaleDetailModel.setCode(salesSheetSaleDetail.getProduct().getCode());
				salesSheetSaleDetailModel.setDescription(salesSheetSaleDetail.getProduct().getDescription());
				salesSheetSaleDetailModel.setPrice(salesSheetSaleDetail.getSellingPriceVatIncl());
				salesSheetSaleDetailModel.setQty(salesSheetSaleDetail.getQty());
				salesSheetSaleDetailModels.add(salesSheetSaleDetailModel);	
			}
			salesSheetSaleModel.setSalesSheetSaleDetails(salesSheetSaleDetailModels);
			salesSheetSaleModels.add(salesSheetSaleModel);			
		}
		salesSheetModel.setSalesSheetSales(salesSheetSaleModels);
		
		return salesSheetModel;
	}
}

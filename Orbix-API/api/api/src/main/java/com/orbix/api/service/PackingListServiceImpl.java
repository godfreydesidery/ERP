/**
 * 
 */
package com.orbix.api.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.orbix.api.accessories.Formater;
import com.orbix.api.domain.PackingList;
import com.orbix.api.domain.PackingListDetail;
import com.orbix.api.domain.Product;
import com.orbix.api.domain.SalesList;
import com.orbix.api.domain.SalesListDetail;
import com.orbix.api.domain.SalesSheet;
import com.orbix.api.domain.ProductStockCard;
import com.orbix.api.exceptions.InvalidEntryException;
import com.orbix.api.exceptions.InvalidOperationException;
import com.orbix.api.exceptions.NotFoundException;
import com.orbix.api.models.PackingListDetailModel;
import com.orbix.api.models.PackingListModel;
import com.orbix.api.models.RecordModel;
import com.orbix.api.repositories.DayRepository;
import com.orbix.api.repositories.PackingListDetailRepository;
import com.orbix.api.repositories.PackingListRepository;
import com.orbix.api.repositories.ProductRepository;
import com.orbix.api.repositories.SalesListDetailRepository;
import com.orbix.api.repositories.SalesListRepository;
import com.orbix.api.repositories.SalesSheetRepository;
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
public class PackingListServiceImpl implements PackingListService {
	
	private final PackingListRepository packingListRepository;
	private final SalesListRepository salesListRepository;
	private final SalesSheetRepository salesSheetRepository;
	private final SalesListDetailRepository salesListDetailRepository;
	private final PackingListDetailRepository packingListDetailRepository;
	private final UserRepository userRepository;
	private final DayRepository dayRepository;
	private final ProductRepository productRepository;	
	private final ProductStockCardService productStockCardService;	
	private final SalesListService salesListService;
	private final SalesSheetService salesSheetService;

	@Override
	public PackingListModel save(PackingList packingList) {
		if(!validate(packingList)) {
			throw new InvalidEntryException("Could not save, Sales Issue invalid");
		}
		PackingList pcl = packingListRepository.saveAndFlush(packingList);
		if(pcl.getNo().equals("NA")) {
			pcl.setNo(generatePackingListNo(pcl));
			pcl = packingListRepository.save(pcl);
		}
		
		double totalPreviousReturns = 0;
		double totalAmountIssued = 0;
		double totalAmountPacked = 0;
		double totalSales = 0;
		double totalReturns = 0;
		double totalOffered = 0;
		double totalDamages = 0;
		
		PackingListModel model = new PackingListModel();
		model.setId(pcl.getId());
		model.setNo(pcl.getNo());
		model.setSalesListNo(pcl.getSalesListNo());
		model.setCustomer(pcl.getCustomer());
		model.setSalesAgent(pcl.getSalesAgent());
		model.setStatus(pcl.getStatus());
		model.setComments(pcl.getComments());
		if(pcl.getCreatedAt() != null && pcl.getCreatedBy() != null) {
			model.setCreated(dayRepository.findById(pcl.getCreatedAt()).get().getBussinessDate() +" "+ userRepository.getAlias(pcl.getCreatedBy()));
		}
		if(pcl.getApprovedAt() != null && pcl.getApprovedBy() != null) {
			model.setApproved(dayRepository.findById(pcl.getApprovedAt()).get().getBussinessDate() +" "+ userRepository.getAlias(pcl.getApprovedBy()));
		}
		if(pcl.getPostedAt() != null && pcl.getPostedBy() != null) {
			model.setPosted(dayRepository.findById(pcl.getPostedAt()).get().getBussinessDate() +" "+ userRepository.getAlias(pcl.getPostedBy()));
		}
		List<PackingListDetailModel> details = new ArrayList<PackingListDetailModel>();
		List<PackingListDetail> ds = packingListDetailRepository.findByPackingList(pcl);
		if(!ds.isEmpty()) {
			for(PackingListDetail d : ds) {
				PackingListDetailModel detail = new PackingListDetailModel();
				detail.setId(d.getId());
				detail.setPreviousReturns(d.getPreviousReturns());
				detail.setProduct(d.getProduct());
				detail.setQtyIssued(d.getQtyIssued());
				detail.setTotalPacked(d.getTotalPacked());
				detail.setSellingPriceVatIncl(d.getSellingPriceVatIncl());
				detail.setSellingPriceVatExcl(d.getSellingPriceVatExcl());
				details.add(detail);
				
				totalPreviousReturns = totalPreviousReturns + d.getPreviousReturns() * d.getSellingPriceVatIncl();
				totalAmountIssued = totalAmountIssued + d.getQtyIssued() * d.getSellingPriceVatIncl();
				totalAmountPacked = totalAmountPacked + d.getTotalPacked() * d.getSellingPriceVatIncl();
			}
			model.setPackingListDetails(details);
			
			model.setTotalPreviousReturns(totalPreviousReturns);
			model.setTotalAmountIssued(totalAmountIssued);
			model.setTotalAmountPacked(totalAmountPacked);
			model.setTotalSales(totalSales);
			model.setTotalOffered(totalOffered);
			model.setTotalReturns(totalReturns);
			model.setTotalDamages(totalDamages);
			
		}
		
		return model;
	}

	@Override
	public PackingListModel get(Long id) {
		PackingListModel model = new PackingListModel();
		Optional<PackingList> pcl = packingListRepository.findById(id);
		if(!pcl.isPresent()) {
			throw new NotFoundException("Packing List not found");
		}
		model.setId(pcl.get().getId());
		model.setNo(pcl.get().getNo());
		model.setSalesListNo(pcl.get().getSalesListNo());
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
		
		if(pcl.get().getCreatedAt() != null && pcl.get().getCreatedBy() != null) {
			model.setCreated(dayRepository.findById(pcl.get().getCreatedAt()).get().getBussinessDate() +" "+ userRepository.getAlias(pcl.get().getCreatedBy()));
		}
		if(pcl.get().getApprovedAt() != null && pcl.get().getApprovedBy() != null) {
			model.setApproved(dayRepository.findById(pcl.get().getApprovedAt()).get().getBussinessDate() +" "+ userRepository.getAlias(pcl.get().getApprovedBy()));
		}
		if(pcl.get().getPostedAt() != null && pcl.get().getPostedBy() != null) {
			model.setPosted(dayRepository.findById(pcl.get().getPostedAt()).get().getBussinessDate() +" "+ userRepository.getAlias(pcl.get().getPostedBy()));
		}
		List<PackingListDetail> packingListDetails = pcl.get().getPackingListDetails();
		List<PackingListDetailModel> details = new ArrayList<PackingListDetailModel>();
		for(PackingListDetail d : packingListDetails) {
			PackingListDetailModel detail = new PackingListDetailModel();
			detail.setId(d.getId());
			detail.setPreviousReturns(d.getPreviousReturns());
			detail.setProduct(d.getProduct());
			detail.setQtyIssued(d.getQtyIssued());
			detail.setTotalPacked(d.getTotalPacked());
			detail.setSellingPriceVatIncl(d.getSellingPriceVatIncl());
			detail.setSellingPriceVatExcl(d.getSellingPriceVatExcl());
			details.add(detail);
			
			totalPreviousReturns = totalPreviousReturns + d.getPreviousReturns() * d.getSellingPriceVatIncl();
			totalAmountIssued = totalAmountIssued + d.getQtyIssued() * d.getSellingPriceVatIncl();
			totalAmountPacked = totalAmountPacked + d.getTotalPacked() * d.getSellingPriceVatIncl();			
		}
		model.setPackingListDetails(details);
		
		model.setTotalPreviousReturns(totalPreviousReturns);
		model.setTotalAmountIssued(totalAmountIssued);
		model.setTotalAmountPacked(totalAmountPacked);
		model.setTotalSales(totalSales);
		model.setTotalOffered(totalOffered);
		model.setTotalReturns(totalReturns);
		model.setTotalDamages(totalDamages);
		
		return model;
	}

	@Override
	public PackingListModel getByNo(String no) {
		PackingListModel model = new PackingListModel();
		Optional<PackingList> pcl = packingListRepository.findByNo(no);
		if(!pcl.isPresent()) {
			throw new NotFoundException("Packing List not found");
		}
		model.setId(pcl.get().getId());
		model.setNo(pcl.get().getNo());
		model.setSalesListNo(pcl.get().getSalesListNo());
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
		
		if(pcl.get().getCreatedAt() != null && pcl.get().getCreatedBy() != null) {
			model.setCreated(dayRepository.findById(pcl.get().getCreatedAt()).get().getBussinessDate() +" "+ userRepository.getAlias(pcl.get().getCreatedBy()));
		}
		if(pcl.get().getApprovedAt() != null && pcl.get().getApprovedBy() != null) {
			model.setApproved(dayRepository.findById(pcl.get().getApprovedAt()).get().getBussinessDate() +" "+ userRepository.getAlias(pcl.get().getApprovedBy()));
		}	
		if(pcl.get().getPostedAt() != null && pcl.get().getPostedBy() != null) {
			model.setPosted(dayRepository.findById(pcl.get().getPostedAt()).get().getBussinessDate() +" "+ userRepository.getAlias(pcl.get().getPostedBy()));
		}	
		List<PackingListDetail> packingListDetails = pcl.get().getPackingListDetails();
		List<PackingListDetailModel> details = new ArrayList<PackingListDetailModel>();
		for(PackingListDetail d : packingListDetails) {
			PackingListDetailModel detail = new PackingListDetailModel();
			detail.setId(d.getId());
			detail.setPreviousReturns(d.getPreviousReturns());
			detail.setProduct(d.getProduct());
			detail.setQtyIssued(d.getQtyIssued());
			detail.setTotalPacked(d.getTotalPacked());
			detail.setSellingPriceVatIncl(d.getSellingPriceVatIncl());
			detail.setSellingPriceVatExcl(d.getSellingPriceVatExcl());
			details.add(detail);
			
			totalPreviousReturns = totalPreviousReturns + d.getPreviousReturns() * d.getSellingPriceVatIncl();
			totalAmountIssued = totalAmountIssued + d.getQtyIssued() * d.getSellingPriceVatIncl();
			totalAmountPacked = totalAmountPacked + d.getTotalPacked() * d.getSellingPriceVatIncl();			
		}
		model.setPackingListDetails(details);
		
		model.setTotalPreviousReturns(totalPreviousReturns);
		model.setTotalAmountIssued(totalAmountIssued);
		model.setTotalAmountPacked(totalAmountPacked);
		model.setTotalSales(totalSales);
		model.setTotalOffered(totalOffered);
		model.setTotalReturns(totalReturns);
		model.setTotalDamages(totalDamages);
		
		return model;
	}

	@Override
	public boolean delete(PackingList packingList) {
		if(!allowDelete(packingList)) {
			throw new InvalidOperationException("Deleting the selected Sales Issue is not allowed");
		}
		packingListRepository.delete(packingList);
		return true;
	}

	@Override
	public List<PackingListModel> getAllVisible() {
		List<String> statuses = new ArrayList<String>();
		statuses.add("BLANK");
		statuses.add("PENDING");
		statuses.add("APPROVED");
		statuses.add("POSTED");
		List<PackingList> packingLists = packingListRepository.findAllVissible(statuses);
		List<PackingListModel> models = new ArrayList<PackingListModel>();
		for(PackingList pcl : packingLists) {
			PackingListModel model = new PackingListModel();
			model.setId(pcl.getId());
			model.setNo(pcl.getNo());
			model.setSalesListNo(pcl.getSalesListNo());
			model.setCustomer(pcl.getCustomer());
			model.setSalesAgent(pcl.getSalesAgent());
			model.setStatus(pcl.getStatus());
			model.setComments(pcl.getComments());
						
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
	public PackingListDetailModel saveDetail(PackingListDetail packingListDetail) {
		if(!validateDetail(packingListDetail)) {
			throw new InvalidEntryException("Could not save detail, Invalid entry");
		}
		PackingListDetailModel detail = new PackingListDetailModel();
		PackingListDetail d = packingListDetailRepository.save(packingListDetail);
		detail.setId(d.getId());
		detail.setPreviousReturns(d.getPreviousReturns());
		detail.setProduct(d.getProduct());
		detail.setQtyIssued(d.getQtyIssued());
		detail.setTotalPacked(d.getTotalPacked());
		detail.setCostPriceVatIncl(d.getCostPriceVatIncl());
		detail.setCostPriceVatExcl(d.getCostPriceVatExcl());
		detail.setSellingPriceVatIncl(d.getSellingPriceVatIncl());
		detail.setSellingPriceVatExcl(d.getSellingPriceVatExcl());
		return detail;
	}

	@Override
	public PackingListDetailModel getDetail(Long id) {
		PackingListDetailModel detail = new PackingListDetailModel();
		Optional<PackingListDetail> d = packingListDetailRepository.findById(id);
		if(!d.isPresent()) {
			throw new NotFoundException("Packing List detail not found");
		}
		detail.setId(d.get().getId());
		detail.setPreviousReturns(d.get().getPreviousReturns());
		detail.setProduct(d.get().getProduct());
		detail.setQtyIssued(d.get().getQtyIssued());
		detail.setTotalPacked(d.get().getTotalPacked());
		detail.setSellingPriceVatIncl(d.get().getSellingPriceVatIncl());
		detail.setSellingPriceVatExcl(d.get().getSellingPriceVatExcl());
		return detail;
	}

	@Override
	public boolean deleteDetail(PackingListDetail packingListDetail) {
		if(!allowDeleteDetail(packingListDetail)) {
			throw new InvalidOperationException("Deleting the selected detail is not allowed");
		}
		packingListDetailRepository.delete(packingListDetail);
		return true;
	}

	@Override
	public List<PackingListDetailModel> getAllDetails(PackingList packingList) {
		List<PackingListDetail> details = packingListDetailRepository.findByPackingList(packingList);
		List<PackingListDetailModel> models = new ArrayList<PackingListDetailModel>();
		for(PackingListDetail detail : details) {
			PackingListDetailModel model = new PackingListDetailModel();
			model.setId(detail.getId());
			model.setPreviousReturns(detail.getPreviousReturns());
			model.setProduct(detail.getProduct());
			model.setQtyIssued(detail.getQtyIssued());
			model.setTotalPacked(detail.getTotalPacked());
			model.setSellingPriceVatIncl(detail.getSellingPriceVatIncl());
			model.setSellingPriceVatExcl(detail.getSellingPriceVatExcl());
			models.add(model);
		}
		return models;	
	}

	@Override
	public boolean archive(PackingList packingList) {
		if(!packingList.getStatus().equals("APPROVED")) {
			throw new InvalidOperationException("Could not process, only APPROVED packing list can be archived");
		}		
		packingList.setStatus("ARCHIVED");
		packingListRepository.saveAndFlush(packingList);
		return true;
	}

	@Override
	public boolean archiveAll() {
		List<PackingList> packingLists = packingListRepository.findAllPosted("APPROVED");
		if(packingLists.isEmpty()) {
			throw new NotFoundException("No Packing List to archive");
		}
		for(PackingList p : packingLists) {				
			p.setStatus("ARCHIVED");
			packingListRepository.saveAndFlush(p);
		}
		return true;
	}
	
	/**
	 * After approving, generate sales list, note: this should be performed in sales list service
	 */
	
	@Override
	public PackingListModel approve(PackingList packingList) {
		PackingList pcl = packingListRepository.saveAndFlush(packingList);
		List<PackingListDetail> details = pcl.getPackingListDetails();
		double totalPreviousReturns = 0;
		double totalAmountIssued = 0;
		double totalAmountPacked = 0;
		
		/**
		 * Create a new Sales list
		 */
		SalesList salesList = new SalesList();
		salesList.setNo("NA");
		salesList.setPackingList(pcl);
		salesList.setCustomer(pcl.getCustomer());
		salesList.setSalesAgent(pcl.getSalesAgent());
		salesList.setIssueDate(LocalDate.now());
		salesList.setStatus("PENDING");
		salesList.setCreatedBy(pcl.getApprovedBy());
		salesList.setCreatedAt(pcl.getApprovedAt());
		salesList = salesListRepository.saveAndFlush(salesList);
		
		if(salesList.getNo().equals("NA")) {
			salesList.setNo(salesListService.generateSalesListNo(salesList));
			salesList = salesListRepository.saveAndFlush(salesList);
		}
		
		pcl.setSalesListNo(salesList.getNo());
		packingListRepository.saveAndFlush(pcl);
		
		/**
		 * Create a new Sales sheet
		 */
		
		SalesSheet salesSheet = new SalesSheet();
		salesSheet.setNo("NA");
		salesSheet.setSalesList(salesList);
		salesSheet.setStatus("OPEN");
		salesSheet = salesSheetRepository.saveAndFlush(salesSheet);
		
		if(salesSheet.getNo().equals("NA")) {
			salesSheet.setNo(salesSheetService.generateSalesSheetNo(salesSheet));
			salesSheet = salesSheetRepository.saveAndFlush(salesSheet);
		}
		
		
		for(PackingListDetail d : details) {				
			/**
			 * Grab stock qty and update stock
			 */
			Product product =productRepository.findById(d.getProduct().getId()).get();
			double stock = product.getStock() - (d.getTotalPacked());
			product.setStock(stock);
			productRepository.saveAndFlush(product);
			/**
			 * Create stock card
			 */
			ProductStockCard stockCard = new ProductStockCard();
			stockCard.setQtyOut(d.getTotalPacked());
			stockCard.setProduct(product);
			stockCard.setBalance(stock);
			stockCard.setDay(dayRepository.getCurrentBussinessDay());
			stockCard.setReference("To Sales List. Ref #: "+salesList.getNo());
			productStockCardService.save(stockCard);
			
			/**
			 * Grab totals
			 */
			totalPreviousReturns = totalPreviousReturns + d.getPreviousReturns() * d.getSellingPriceVatIncl();
			totalAmountIssued = totalAmountIssued + d.getQtyIssued() * d.getSellingPriceVatIncl();
			totalAmountPacked = totalAmountPacked + d.getTotalPacked() * d.getSellingPriceVatIncl();
			
			/**
			 * Create a new sales detail
			 */
			SalesListDetail salesListDetail = new SalesListDetail();
			salesListDetail.setSalesList(salesList);			
			salesListDetail.setProduct(d.getProduct());
			salesListDetail.setTotalPacked(d.getTotalPacked());
			salesListDetail.setCostPriceVatIncl(d.getCostPriceVatIncl());
			salesListDetail.setCostPriceVatExcl(d.getCostPriceVatExcl());
			salesListDetail.setSellingPriceVatIncl(d.getSellingPriceVatIncl());
			salesListDetail.setSellingPriceVatExcl(d.getSellingPriceVatExcl());
			salesListDetailRepository.saveAndFlush(salesListDetail);
		}
		if(totalAmountPacked != totalPreviousReturns + totalAmountIssued) {
			throw new InvalidEntryException("Total packed must be equal to sum of previous returns and amount issued");
		}
		
		PackingListModel model = new PackingListModel();
		model.setId(pcl.getId());
		model.setNo(pcl.getNo());
		model.setCustomer(pcl.getCustomer());
		model.setStatus(pcl.getStatus());
		model.setComments(pcl.getComments());
		if(pcl.getCreatedAt() != null && pcl.getCreatedBy() != null) {
			model.setCreated(dayRepository.findById(pcl.getCreatedAt()).get().getBussinessDate() +" "+ userRepository.getAlias(pcl.getCreatedBy()));
		}
		if(pcl.getApprovedAt() != null && pcl.getApprovedBy() != null) {
			model.setApproved(dayRepository.findById(pcl.getApprovedAt()).get().getBussinessDate() +" "+ userRepository.getAlias(pcl.getApprovedBy()));
		}	
		if(pcl.getPostedAt() != null && pcl.getPostedBy() != null) {
			model.setPosted(dayRepository.findById(pcl.getPostedAt()).get().getBussinessDate() +" "+ userRepository.getAlias(pcl.getPostedBy()));
		}	
		List<PackingListDetail> packingListDetails = pcl.getPackingListDetails();
		List<PackingListDetailModel> modelDetails = new ArrayList<PackingListDetailModel>();
		for(PackingListDetail d : packingListDetails) {
			PackingListDetailModel detail = new PackingListDetailModel();
			detail.setId(d.getId());
			detail.setPreviousReturns(d.getPreviousReturns());
			detail.setProduct(d.getProduct());
			detail.setQtyIssued(d.getQtyIssued());
			detail.setTotalPacked(d.getTotalPacked());
			detail.setSellingPriceVatIncl(d.getSellingPriceVatIncl());
			detail.setSellingPriceVatExcl(d.getSellingPriceVatExcl());
			modelDetails.add(detail);
		}
		model.setPackingListDetails(modelDetails);
		
		model.setTotalPreviousReturns(totalPreviousReturns);
		model.setTotalAmountIssued(totalAmountIssued);
		model.setTotalAmountPacked(totalAmountPacked);
		
		return model;
	}

	private boolean validate(PackingList packingList) {
		return true;
	}
	
	private boolean allowDelete(PackingList packingList) {
		return false;
	}
	
	private boolean validateDetail(PackingListDetail packingListDetail) {
		return true;
	}
	
	private boolean allowDeleteDetail(PackingListDetail packingListDetail) {
		return true;
	}
	
	private String generatePackingListNo(PackingList packingList) {
		Long number = packingList.getId();		
		String sNumber = number.toString();
		//return "PCL-"+Formater.formatNine(sNumber);
		return Formater.formatWithCurrentDate("PCL",sNumber);
	}
	
	@Override
	public RecordModel requestPackingListNo() {
		Long id = 1L;
		try {
			id = packingListRepository.getLastId() + 1;
		}catch(Exception e) {}
		RecordModel model = new RecordModel();
		model.setNo(Formater.formatWithCurrentDate("PCL",id.toString()));
		return model;
	}
}

/**
 * 
 */
package com.orbix.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.orbix.api.accessories.Formater;
import com.orbix.api.domain.ClaimReplacementProduct;
import com.orbix.api.domain.ClaimedProduct;
import com.orbix.api.domain.CustomerClaim;
import com.orbix.api.domain.Product;
import com.orbix.api.domain.ProductStockCard;
import com.orbix.api.exceptions.InvalidEntryException;
import com.orbix.api.exceptions.InvalidOperationException;
import com.orbix.api.exceptions.NotFoundException;
import com.orbix.api.models.ClaimReplacementModel;
import com.orbix.api.models.ClaimedProductModel;
import com.orbix.api.models.CustomerClaimModel;
import com.orbix.api.models.RecordModel;
import com.orbix.api.repositories.ClaimReplacementProductRepository;
import com.orbix.api.repositories.ClaimedProductRepository;
import com.orbix.api.repositories.CustomerClaimRepository;
import com.orbix.api.repositories.DayRepository;
import com.orbix.api.repositories.ProductRepository;
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
public class CustomerClaimServiceImpl implements CustomerClaimService {
	private final CustomerClaimRepository customerClaimRepository;
	private final ClaimedProductRepository claimedProductRepository;
	private final ClaimReplacementProductRepository claimReplacementProductRepository;
	private final UserRepository userRepository;
	private final DayRepository dayRepository;
	private final ProductRepository productRepository;
	private final ProductStockCardService productStockCardService;
	
	
	@Override
	public CustomerClaimModel save(CustomerClaim customerClaim) {
		if(!validate(customerClaim)) {
			throw new InvalidEntryException("Could not save, Claim invalid");
		}
		CustomerClaim ptp = customerClaimRepository.save(customerClaim);
		if(ptp.getNo().equals("NA")) {
			ptp.setNo(generateCustomerClaimNo(ptp));
			ptp = customerClaimRepository.save(ptp);
		}			
		CustomerClaimModel model = new CustomerClaimModel();
		model.setId(ptp.getId());
		model.setNo(ptp.getNo());
		model.setStatus(ptp.getStatus());
		model.setComments(ptp.getComments());
		model.setCustomer(ptp.getCustomer());
		if(ptp.getCreatedAt() != null && ptp.getCreatedBy() != null) {
			model.setCreated(dayRepository.findById(ptp.getCreatedAt()).get().getBussinessDate() +" "+ userRepository.getAlias(ptp.getCreatedBy()));
		}
		if(ptp.getApprovedAt() != null && ptp.getApprovedBy() != null) {
			model.setApproved(dayRepository.findById(ptp.getApprovedAt()).get().getBussinessDate() +" "+ userRepository.getAlias(ptp.getApprovedBy()));
		}		
		return model;
	}
	
	@Override
	public CustomerClaimModel get(Long id) {
		CustomerClaimModel model = new CustomerClaimModel();
		Optional<CustomerClaim> ptp = customerClaimRepository.findById(id);
		if(!ptp.isPresent()) {
			throw new NotFoundException("Customer Claim not found");
		}
		model.setId(ptp.get().getId());
		model.setNo(ptp.get().getNo());
		model.setReason(ptp.get().getReason());
		model.setStatus(ptp.get().getStatus());
		model.setCustomer(ptp.get().getCustomer());
		model.setComments(ptp.get().getComments());
		if(ptp.get().getCreatedAt() != null && ptp.get().getCreatedBy() != null) {
			model.setCreated(dayRepository.findById(ptp.get().getCreatedAt()).get().getBussinessDate() +" "+ userRepository.getAlias(ptp.get().getCreatedBy()));
		}
		if(ptp.get().getApprovedAt() != null && ptp.get().getApprovedBy() != null) {
			model.setApproved(dayRepository.findById(ptp.get().getApprovedAt()).get().getBussinessDate() +" "+ userRepository.getAlias(ptp.get().getApprovedBy()));
		}		
		List<ClaimedProduct> claimedProducts = ptp.get().getClaimedProducts();
		List<ClaimReplacementProduct> claimReplacementProducts = ptp.get().getClaimReplacementProducts();
		
		List<ClaimedProductModel> initialModels = new ArrayList<ClaimedProductModel>();
		List<ClaimReplacementModel> finalModels = new ArrayList<ClaimReplacementModel>();
		
		for(ClaimedProduct in : claimedProducts) {
			ClaimedProductModel initialModel = new ClaimedProductModel();
			initialModel.setId(in.getId());
			initialModel.setCostPriceVatExcl(in.getCostPriceVatExcl());
			initialModel.setCostPriceVatIncl(in.getCostPriceVatIncl());
			initialModel.setSellingPriceVatExcl(in.getSellingPriceVatExcl());
			initialModel.setSellingPriceVatIncl(in.getSellingPriceVatIncl());
			initialModel.setProduct(in.getProduct());
			initialModel.setQty(in.getQty());
			initialModel.setReason(in.getReason());
			initialModel.setRemarks(in.getRemarks());			
			initialModels.add(initialModel);			
		}
		
		for(ClaimReplacementProduct fin : claimReplacementProducts) {
			ClaimReplacementModel finalModel = new ClaimReplacementModel();
			finalModel.setId(fin.getId());
			finalModel.setCostPriceVatExcl(fin.getCostPriceVatExcl());
			finalModel.setCostPriceVatIncl(fin.getCostPriceVatIncl());
			finalModel.setSellingPriceVatExcl(fin.getSellingPriceVatExcl());
			finalModel.setSellingPriceVatIncl(fin.getSellingPriceVatIncl());
			finalModel.setProduct(fin.getProduct());
			finalModel.setQty(fin.getQty());
			finalModel.setRemarks(fin.getRemarks());
			finalModels.add(finalModel);			
		}
		
		model.setClaimedProducts(initialModels);
		model.setReplacementProducts(finalModels);
		return model;
	}
	
	@Override
	public CustomerClaimModel getByNo(String no) {
		CustomerClaimModel model = new CustomerClaimModel();
		Optional<CustomerClaim> ptp = customerClaimRepository.findByNo(no);
		if(!ptp.isPresent()) {
			throw new NotFoundException("Product To Product not found");
		}
		model.setId(ptp.get().getId());
		model.setNo(ptp.get().getNo());
		model.setReason(ptp.get().getReason());
		model.setStatus(ptp.get().getStatus());
		model.setCustomer(ptp.get().getCustomer());
		model.setComments(ptp.get().getComments());
		if(ptp.get().getCreatedAt() != null && ptp.get().getCreatedBy() != null) {
			model.setCreated(dayRepository.findById(ptp.get().getCreatedAt()).get().getBussinessDate() +" "+ userRepository.getAlias(ptp.get().getCreatedBy()));
		}
		if(ptp.get().getApprovedAt() != null && ptp.get().getApprovedBy() != null) {
			model.setApproved(dayRepository.findById(ptp.get().getApprovedAt()).get().getBussinessDate() +" "+ userRepository.getAlias(ptp.get().getApprovedBy()));
		}		
		List<ClaimedProduct> claimedProducts = ptp.get().getClaimedProducts();
		List<ClaimReplacementProduct> claimReplacementProducts = ptp.get().getClaimReplacementProducts();
		
		List<ClaimedProductModel> initialModels = new ArrayList<ClaimedProductModel>();
		List<ClaimReplacementModel> finalModels = new ArrayList<ClaimReplacementModel>();
		
		for(ClaimedProduct in : claimedProducts) {
			ClaimedProductModel initialModel = new ClaimedProductModel();
			initialModel.setId(in.getId());
			initialModel.setCostPriceVatExcl(in.getCostPriceVatExcl());
			initialModel.setCostPriceVatIncl(in.getCostPriceVatIncl());
			initialModel.setSellingPriceVatExcl(in.getSellingPriceVatExcl());
			initialModel.setSellingPriceVatIncl(in.getSellingPriceVatIncl());
			initialModel.setProduct(in.getProduct());
			initialModel.setQty(in.getQty());
			initialModel.setReason(in.getReason());
			initialModel.setRemarks(in.getRemarks());			
			initialModels.add(initialModel);				
		}
		
		for(ClaimReplacementProduct fin : claimReplacementProducts) {
			ClaimReplacementModel finalModel = new ClaimReplacementModel();
			finalModel.setId(fin.getId());
			finalModel.setCostPriceVatExcl(fin.getCostPriceVatExcl());
			finalModel.setCostPriceVatIncl(fin.getCostPriceVatIncl());
			finalModel.setSellingPriceVatExcl(fin.getSellingPriceVatExcl());
			finalModel.setSellingPriceVatIncl(fin.getSellingPriceVatIncl());
			finalModel.setProduct(fin.getProduct());
			finalModel.setQty(fin.getQty());
			finalModel.setRemarks(fin.getRemarks());
			finalModels.add(finalModel);		
		}
		
		model.setClaimedProducts(initialModels);
		model.setReplacementProducts(finalModels);
		return model;
	}
	
	@Override
	public boolean delete(CustomerClaim customerClaim) {
		if(!allowDelete(customerClaim)) {
			throw new InvalidOperationException("Deleting the selected Conversion is not allowed");
		}
		customerClaimRepository.delete(customerClaim);
		return true;
	}
	
	@Override
	public List<CustomerClaimModel> getAllVisible() {
		List<String> statuses = new ArrayList<String>();
		statuses.add("BLANK");
		statuses.add("PENDING");
		statuses.add("APPROVED");
		List<CustomerClaim> ptms = customerClaimRepository.findAllVissible(statuses);
		List<CustomerClaimModel> models = new ArrayList<CustomerClaimModel>();
		for(CustomerClaim ptm : ptms) {
			CustomerClaimModel model = new CustomerClaimModel();
			model.setId(ptm.getId());
			model.setNo(ptm.getNo());
			model.setReason(ptm.getReason());
			model.setStatus(ptm.getStatus());
			model.setComments(ptm.getComments());
			if(ptm.getCreatedAt() != null && ptm.getCreatedBy() != null) {
				model.setCreated(dayRepository.findById(ptm.getCreatedAt()).get().getBussinessDate() +" "+ userRepository.getAlias(ptm.getCreatedBy()));
			}
			if(ptm.getApprovedAt() != null && ptm.getApprovedBy() != null) {
				model.setApproved(dayRepository.findById(ptm.getApprovedAt()).get().getBussinessDate() +" "+ userRepository.getAlias(ptm.getApprovedBy()));
			}			
			models.add(model);
		}
		return models;
	}
		
	@Override
	public ClaimedProductModel saveClaimedProduct(ClaimedProduct claimedProduct) {
		if(!validateInitials(claimedProduct)) {
			throw new InvalidEntryException("Could not save detail, Invalid entry");
		}
		ClaimedProductModel model = new ClaimedProductModel();
		ClaimedProduct d = claimedProductRepository.save(claimedProduct);
		
		model.setId(d.getId());
		model.setProduct(d.getProduct());
		model.setQty(d.getQty());
		model.setCostPriceVatExcl(d.getCostPriceVatExcl());
		model.setCostPriceVatIncl(d.getCostPriceVatIncl());
		model.setSellingPriceVatExcl(d.getSellingPriceVatExcl());
		model.setSellingPriceVatIncl(d.getSellingPriceVatIncl());
		model.setReason(d.getReason());
		model.setRemarks(d.getRemarks());
		model.setCustomerClaim(d.getCustomerClaim());
	
		return model;
	}
	
	@Override
	public ClaimReplacementModel saveReplacement(ClaimReplacementProduct claimReplacementProduct) {
		if(!validateFinals(claimReplacementProduct)) {
			throw new InvalidEntryException("Could not save detail, Invalid entry");
		}
		ClaimReplacementModel model = new ClaimReplacementModel();
		ClaimReplacementProduct d = claimReplacementProductRepository.save(claimReplacementProduct);
		
		model.setId(d.getId());
		model.setProduct(d.getProduct());
		model.setQty(d.getQty());
		model.setCostPriceVatExcl(d.getCostPriceVatExcl());
		model.setCostPriceVatIncl(d.getCostPriceVatIncl());
		model.setSellingPriceVatExcl(d.getSellingPriceVatExcl());
		model.setSellingPriceVatIncl(d.getSellingPriceVatIncl());
		model.setRemarks(d.getRemarks());		
		model.setCustomerClaim(d.getCustomerClaim());	
		return model;
	}
	
	@Override
	public ClaimedProductModel getClaimedProduct(Long id) {
		ClaimedProductModel model = new ClaimedProductModel();
		Optional<ClaimedProduct> d = claimedProductRepository.findById(id);
		if(!d.isPresent()) {
			throw new NotFoundException("Claimed Product not found");
		}
		
		model.setId(d.get().getId());
		model.setProduct(d.get().getProduct());
		model.setQty(d.get().getQty());
		model.setCostPriceVatExcl(d.get().getCostPriceVatExcl());
		model.setCostPriceVatIncl(d.get().getCostPriceVatIncl());
		model.setSellingPriceVatExcl(d.get().getSellingPriceVatExcl());
		model.setSellingPriceVatIncl(d.get().getSellingPriceVatIncl());
		model.setReason(d.get().getReason());
		model.setRemarks(d.get().getRemarks());		
		model.setCustomerClaim(d.get().getCustomerClaim());
		return model;
	}
	
	@Override
	public ClaimReplacementModel getReplacement(Long id) {
		ClaimReplacementModel model = new ClaimReplacementModel();
		Optional<ClaimReplacementProduct> d = claimReplacementProductRepository.findById(id);
		if(!d.isPresent()) {
			throw new NotFoundException("Replacement Product not found");
		}		
		model.setId(d.get().getId());
		model.setProduct(d.get().getProduct());
		model.setQty(d.get().getQty());
		model.setCostPriceVatExcl(d.get().getCostPriceVatExcl());
		model.setCostPriceVatIncl(d.get().getCostPriceVatIncl());
		model.setSellingPriceVatExcl(d.get().getSellingPriceVatExcl());
		model.setSellingPriceVatIncl(d.get().getSellingPriceVatIncl());
		model.setRemarks(d.get().getRemarks());		
		model.setCustomerClaim(d.get().getCustomerClaim());
		return model;
	}
	
	@Override
	public boolean deleteClaimedProduct(ClaimedProduct claimedProduct) {
		if(!allowDeleteInitial(claimedProduct)) {
			throw new InvalidOperationException("Deleting the selected Conversion detail is not allowed");
		}
		claimedProductRepository.delete(claimedProduct);
		return true;
	}
	
	@Override
	public boolean deleteReplacement(ClaimReplacementProduct claimReplacementProduct) {
		if(!allowDeleteFinal(claimReplacementProduct)) {
			throw new InvalidOperationException("Deleting the selected Conversion detail is not allowed");
		}
		claimReplacementProductRepository.delete(claimReplacementProduct);
		return true;
	}
	
	@Override
	public List<ClaimedProductModel> getAllClaimedProducts(CustomerClaim customerClaim) {
		List<ClaimedProduct> initials = claimedProductRepository.findByCustomerClaim(customerClaim);
		List<ClaimedProductModel> models = new ArrayList<ClaimedProductModel>();
		for(ClaimedProduct d : initials) {
			ClaimedProductModel model = new ClaimedProductModel();
			
			model.setId(d.getId());
			model.setProduct(d.getProduct());
			model.setQty(d.getQty());
			model.setCostPriceVatExcl(d.getCostPriceVatExcl());
			model.setCostPriceVatIncl(d.getCostPriceVatIncl());
			model.setSellingPriceVatExcl(d.getSellingPriceVatExcl());
			model.setSellingPriceVatIncl(d.getSellingPriceVatIncl());
			model.setReason(d.getReason());
			model.setRemarks(d.getRemarks());			
			model.setCustomerClaim(d.getCustomerClaim());	
			models.add(model);
		}
		return models;	
	}
	
	@Override
	public List<ClaimReplacementModel> getAllReplacementProducts(CustomerClaim customerClaim) {
		List<ClaimReplacementProduct> finals = claimReplacementProductRepository.findByCustomerClaim(customerClaim);
		List<ClaimReplacementModel> models = new ArrayList<ClaimReplacementModel>();
		for(ClaimReplacementProduct d : finals) {
			ClaimReplacementModel model = new ClaimReplacementModel();			
			model.setId(d.getId());
			model.setProduct(d.getProduct());
			model.setQty(d.getQty());
			model.setCostPriceVatExcl(d.getCostPriceVatExcl());
			model.setCostPriceVatIncl(d.getCostPriceVatIncl());
			model.setSellingPriceVatExcl(d.getSellingPriceVatExcl());
			model.setSellingPriceVatIncl(d.getSellingPriceVatIncl());
			model.setRemarks(d.getRemarks());			
			model.setCustomerClaim(d.getCustomerClaim());	
			models.add(model);
		}
		return models;	
	}
	
	@Override
	public boolean archive(CustomerClaim customerClaim) {
		if(!customerClaim.getStatus().equals("APPROVED")) {
			throw new InvalidOperationException("Could not process, only an approved Claim can be archived");
		}
		customerClaim.setStatus("ARCHIVED");
		customerClaimRepository.saveAndFlush(customerClaim);
		return true;
	}
	
	@Override
	public boolean archiveAll() {
		List<CustomerClaim> ptms = customerClaimRepository.findAllApproved("APPROVED");
		if(ptms.isEmpty()) {
			throw new NotFoundException("No Document to archive");
		}
		for(CustomerClaim p : ptms) {
			p.setStatus("ARCHIVED");
			customerClaimRepository.saveAndFlush(p);
		}
		return true;
	}	
	
	private boolean validate(CustomerClaim productToMaterial) {
		return true;
	}
	
	private boolean allowDelete(CustomerClaim customerClaim) {
		return true;
	}
	
	private boolean validateInitials(ClaimedProduct claimedProduct) {
		return true;
	}
	
	private boolean validateFinals(ClaimReplacementProduct claimReplacementProduct) {
		return true;
	}
	
	private boolean allowDeleteInitial(ClaimedProduct claimedProduct) {
		return true;
	}
	
	private boolean allowDeleteFinal(ClaimReplacementProduct claimReplacementProduct) {
		return true;
	}
	
	private String generateCustomerClaimNo(CustomerClaim customerClaim) {
		Long number = customerClaim.getId();		
		String sNumber = number.toString();
		//return "PTM-"+Formater.formatSix(sNumber);
		//return "PTM-"+sNumber;
		return Formater.formatWithCurrentDate("CCL",sNumber);
	}

	@Override
	public CustomerClaimModel approve(CustomerClaim customerClaim) {
		/**
		 * Save invoice
		 * Deduct products from stock
		 * Update stock cards
		 */
		CustomerClaim ptp = customerClaimRepository.saveAndFlush(customerClaim);
		//List<ClaimedProduct> initials = ptp.getClaimedProducts();
		//for(ClaimedProduct d : initials) {
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
			//Product product =productRepository.findById(d.getProduct().getId()).get();
			//double productStock = product.getStock() - d.getQty();
			//product.setStock(productStock);
			//productRepository.saveAndFlush(product);
			
			
			//ProductStockCard productStockCard = new ProductStockCard();
			//productStockCard.setQtyOut(d.getQty());
			//productStockCard.setProduct(product);
			//productStockCard.setBalance(productStock);
			//productStockCard.setDay(dayRepository.getCurrentBussinessDay());
			//productStockCard.setReference("Used in Product conversion. Ref #: "+ptp.getNo());
			//productStockCardService.save(productStockCard);
			
			
			
		//}
		
		
		ptp = customerClaimRepository.saveAndFlush(customerClaim);
		List<ClaimReplacementProduct> finals = ptp.getClaimReplacementProducts();
		for(ClaimReplacementProduct d : finals) {
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
			double productStock = product.getStock() - d.getQty();
			product.setStock(productStock);
			productRepository.saveAndFlush(product);
			
			
			ProductStockCard productStockCard = new ProductStockCard();
			productStockCard.setQtyOut(d.getQty());
			productStockCard.setProduct(product);
			productStockCard.setBalance(productStock);
			productStockCard.setDay(dayRepository.getCurrentBussinessDay());
			productStockCard.setReference("Issued in customer claim Ref #: "+ptp.getNo());
			productStockCardService.save(productStockCard);
			
		}
		
		ptp = customerClaimRepository.saveAndFlush(ptp);
		CustomerClaimModel model = new CustomerClaimModel();
		model.setId(ptp.getId());
		model.setNo(ptp.getNo());
		model.setReason(ptp.getReason());
		model.setStatus(ptp.getStatus());
		model.setComments(ptp.getComments());
		if(ptp.getCreatedAt() != null && ptp.getCreatedBy() != null) {
			model.setCreated(dayRepository.findById(ptp.getCreatedAt()).get().getBussinessDate() +" "+ userRepository.getAlias(ptp.getCreatedBy()));
		}
		if(ptp.getApprovedAt() != null && ptp.getApprovedBy() != null) {
			model.setApproved(dayRepository.findById(ptp.getApprovedAt()).get().getBussinessDate() +" "+ userRepository.getAlias(ptp.getApprovedBy()));
		}		
		return model;
	}

	@Override
	public RecordModel requestCCLNo() {
		Long id = 1L;
		try {
			id = customerClaimRepository.getLastId() + 1;
		}catch(Exception e) {}
		RecordModel model = new RecordModel();
		model.setNo(Formater.formatWithCurrentDate("CCL",id.toString()));
		return model;
	}	
}

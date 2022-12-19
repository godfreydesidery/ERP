/**
 * 
 */
package com.orbix.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.orbix.api.domain.Product;
import com.orbix.api.domain.ProductStockCard;
import com.orbix.api.domain.ProductToProduct;
import com.orbix.api.domain.ProductToProductFinal;
import com.orbix.api.domain.ProductToProductInitial;
import com.orbix.api.exceptions.InvalidEntryException;
import com.orbix.api.exceptions.InvalidOperationException;
import com.orbix.api.exceptions.NotFoundException;
import com.orbix.api.models.ProductToProductFinalModel;
import com.orbix.api.models.ProductToProductInitialModel;
import com.orbix.api.models.ProductToProductModel;
import com.orbix.api.repositories.DayRepository;
import com.orbix.api.repositories.ProductRepository;
import com.orbix.api.repositories.ProductToProductFinalRepository;
import com.orbix.api.repositories.ProductToProductInitialRepository;
import com.orbix.api.repositories.ProductToProductRepository;
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
public class ProductToProductServiceImpl implements ProductToProductService{
	private final ProductToProductRepository productToProductRepository;
	private final ProductToProductInitialRepository productToProductInitialRepository;
	private final ProductToProductFinalRepository productToProductFinalRepository;
	private final UserRepository userRepository;
	private final DayRepository dayRepository;
	private final ProductRepository productRepository;
	private final ProductStockCardService productStockCardService;
	
	
	@Override
	public ProductToProductModel save(ProductToProduct productToProduct) {
		if(!validate(productToProduct)) {
			throw new InvalidEntryException("Could not save, Conversion invalid");
		}
		ProductToProduct ptp = productToProductRepository.save(productToProduct);
		if(ptp.getNo().equals("NA")) {
			ptp.setNo(generateProductToProductNo(ptp));
			ptp = productToProductRepository.save(ptp);
		}			
		ProductToProductModel model = new ProductToProductModel();
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
	public ProductToProductModel get(Long id) {
		ProductToProductModel model = new ProductToProductModel();
		Optional<ProductToProduct> ptp = productToProductRepository.findById(id);
		if(!ptp.isPresent()) {
			throw new NotFoundException("Product To Product not found");
		}
		model.setId(ptp.get().getId());
		model.setNo(ptp.get().getNo());
		model.setReason(ptp.get().getReason());
		model.setStatus(ptp.get().getStatus());
		model.setComments(ptp.get().getComments());
		if(ptp.get().getCreatedAt() != null && ptp.get().getCreatedBy() != null) {
			model.setCreated(dayRepository.findById(ptp.get().getCreatedAt()).get().getBussinessDate() +" "+ userRepository.getAlias(ptp.get().getCreatedBy()));
		}
		if(ptp.get().getApprovedAt() != null && ptp.get().getApprovedBy() != null) {
			model.setApproved(dayRepository.findById(ptp.get().getApprovedAt()).get().getBussinessDate() +" "+ userRepository.getAlias(ptp.get().getApprovedBy()));
		}		
		List<ProductToProductInitial> productToProductInitials = ptp.get().getProductToProductInitials();
		List<ProductToProductFinal> productToProductFinals = ptp.get().getProductToProductFinals();
		
		List<ProductToProductInitialModel> initialModels = new ArrayList<ProductToProductInitialModel>();
		List<ProductToProductFinalModel> finalModels = new ArrayList<ProductToProductFinalModel>();
		
		for(ProductToProductInitial in : productToProductInitials) {
			ProductToProductInitialModel initialModel = new ProductToProductInitialModel();
			initialModel.setId(in.getId());
			initialModel.setProduct(in.getProduct());
			initialModel.setQty(in.getQty());
			initialModel.setCostPriceVatExcl(in.getCostPriceVatExcl());
			initialModel.setCostPriceVatIncl(in.getCostPriceVatIncl());
			initialModel.setSellingPriceVatExcl(in.getSellingPriceVatExcl());
			initialModel.setSellingPriceVatIncl(in.getSellingPriceVatIncl());
			initialModel.setProductToProduct(in.getProductToProduct());
			initialModels.add(initialModel);			
		}
		
		for(ProductToProductFinal fin : productToProductFinals) {
			ProductToProductFinalModel finalModel = new ProductToProductFinalModel();
			finalModel.setId(fin.getId());
			finalModel.setProduct(fin.getProduct());
			finalModel.setQty(fin.getQty());
			finalModel.setCostPriceVatExcl(fin.getCostPriceVatExcl());
			finalModel.setCostPriceVatIncl(fin.getCostPriceVatIncl());
			finalModel.setSellingPriceVatExcl(fin.getSellingPriceVatExcl());
			finalModel.setSellingPriceVatIncl(fin.getSellingPriceVatIncl());
			finalModel.setProductToProduct(fin.getProductToProduct());
			finalModels.add(finalModel);			
		}
		
		model.setProductToProductInitials(initialModels);
		model.setProductToProductFinals(finalModels);
		return model;
	}
	
	@Override
	public ProductToProductModel getByNo(String no) {
		ProductToProductModel model = new ProductToProductModel();
		Optional<ProductToProduct> ptp = productToProductRepository.findByNo(no);
		if(!ptp.isPresent()) {
			throw new NotFoundException("Product To Product not found");
		}
		model.setId(ptp.get().getId());
		model.setNo(ptp.get().getNo());
		model.setReason(ptp.get().getReason());
		model.setStatus(ptp.get().getStatus());
		model.setComments(ptp.get().getComments());
		if(ptp.get().getCreatedAt() != null && ptp.get().getCreatedBy() != null) {
			model.setCreated(dayRepository.findById(ptp.get().getCreatedAt()).get().getBussinessDate() +" "+ userRepository.getAlias(ptp.get().getCreatedBy()));
		}
		if(ptp.get().getApprovedAt() != null && ptp.get().getApprovedBy() != null) {
			model.setApproved(dayRepository.findById(ptp.get().getApprovedAt()).get().getBussinessDate() +" "+ userRepository.getAlias(ptp.get().getApprovedBy()));
		}		
		List<ProductToProductInitial> productToProductInitials = ptp.get().getProductToProductInitials();
		List<ProductToProductFinal> productToProductFinals = ptp.get().getProductToProductFinals();
		
		List<ProductToProductInitialModel> initialModels = new ArrayList<ProductToProductInitialModel>();
		List<ProductToProductFinalModel> finalModels = new ArrayList<ProductToProductFinalModel>();
		
		for(ProductToProductInitial in : productToProductInitials) {
			ProductToProductInitialModel initialModel = new ProductToProductInitialModel();
			initialModel.setId(in.getId());
			initialModel.setProduct(in.getProduct());
			initialModel.setQty(in.getQty());
			initialModel.setCostPriceVatExcl(in.getCostPriceVatExcl());
			initialModel.setCostPriceVatIncl(in.getCostPriceVatIncl());
			initialModel.setSellingPriceVatExcl(in.getSellingPriceVatExcl());
			initialModel.setSellingPriceVatIncl(in.getSellingPriceVatIncl());
			initialModel.setProductToProduct(in.getProductToProduct());
			initialModels.add(initialModel);			
		}
		
		for(ProductToProductFinal fin : productToProductFinals) {
			ProductToProductFinalModel finalModel = new ProductToProductFinalModel();
			finalModel.setId(fin.getId());
			finalModel.setProduct(fin.getProduct());
			finalModel.setQty(fin.getQty());
			finalModel.setCostPriceVatExcl(fin.getCostPriceVatExcl());
			finalModel.setCostPriceVatIncl(fin.getCostPriceVatIncl());
			finalModel.setSellingPriceVatExcl(fin.getSellingPriceVatExcl());
			finalModel.setSellingPriceVatIncl(fin.getSellingPriceVatIncl());
			finalModel.setProductToProduct(fin.getProductToProduct());
			finalModels.add(finalModel);			
		}
		
		model.setProductToProductInitials(initialModels);
		model.setProductToProductFinals(finalModels);
		return model;
	}
	
	@Override
	public boolean delete(ProductToProduct productToProduct) {
		if(!allowDelete(productToProduct)) {
			throw new InvalidOperationException("Deleting the selected Conversion is not allowed");
		}
		productToProductRepository.delete(productToProduct);
		return true;
	}
	
	@Override
	public List<ProductToProductModel> getAllVisible() {
		List<String> statuses = new ArrayList<String>();
		statuses.add("BLANK");
		statuses.add("PENDING");
		statuses.add("APPROVED");
		List<ProductToProduct> ptms = productToProductRepository.findAllVissible(statuses);
		List<ProductToProductModel> models = new ArrayList<ProductToProductModel>();
		for(ProductToProduct ptm : ptms) {
			ProductToProductModel model = new ProductToProductModel();
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
	public ProductToProductInitialModel saveInitial(ProductToProductInitial productToProductInitial) {
		if(!validateInitials(productToProductInitial)) {
			throw new InvalidEntryException("Could not save detail, Invalid entry");
		}
		ProductToProductInitialModel model = new ProductToProductInitialModel();
		ProductToProductInitial d = productToProductInitialRepository.save(productToProductInitial);
		
		model.setId(d.getId());
		model.setProduct(d.getProduct());
		model.setQty(d.getQty());
		model.setCostPriceVatExcl(d.getCostPriceVatExcl());
		model.setCostPriceVatIncl(d.getCostPriceVatIncl());
		model.setSellingPriceVatExcl(d.getSellingPriceVatExcl());
		model.setSellingPriceVatIncl(d.getSellingPriceVatIncl());
		model.setProductToProduct(d.getProductToProduct());
	
		return model;
	}
	
	@Override
	public ProductToProductFinalModel saveFinal(ProductToProductFinal productToProductFinal) {
		if(!validateFinals(productToProductFinal)) {
			throw new InvalidEntryException("Could not save detail, Invalid entry");
		}
		ProductToProductFinalModel model = new ProductToProductFinalModel();
		ProductToProductFinal d = productToProductFinalRepository.save(productToProductFinal);
		
		model.setId(d.getId());
		model.setProduct(d.getProduct());
		model.setQty(d.getQty());
		model.setCostPriceVatExcl(d.getCostPriceVatExcl());
		model.setCostPriceVatIncl(d.getCostPriceVatIncl());
		model.setSellingPriceVatExcl(d.getSellingPriceVatExcl());
		model.setSellingPriceVatIncl(d.getSellingPriceVatIncl());
		model.setProductToProduct(d.getProductToProduct());
	
		return model;
	}
	
	@Override
	public ProductToProductInitialModel getInitial(Long id) {
		ProductToProductInitialModel model = new ProductToProductInitialModel();
		Optional<ProductToProductInitial> d = productToProductInitialRepository.findById(id);
		if(!d.isPresent()) {
			throw new NotFoundException("Initial Product not found");
		}
		
		model.setId(d.get().getId());
		model.setProduct(d.get().getProduct());
		model.setQty(d.get().getQty());
		model.setCostPriceVatExcl(d.get().getCostPriceVatExcl());
		model.setCostPriceVatIncl(d.get().getCostPriceVatIncl());
		model.setSellingPriceVatExcl(d.get().getSellingPriceVatExcl());
		model.setSellingPriceVatIncl(d.get().getSellingPriceVatIncl());
		model.setProductToProduct(d.get().getProductToProduct());
		return model;
	}
	
	@Override
	public ProductToProductFinalModel getFinal(Long id) {
		ProductToProductFinalModel model = new ProductToProductFinalModel();
		Optional<ProductToProductFinal> d = productToProductFinalRepository.findById(id);
		if(!d.isPresent()) {
			throw new NotFoundException("Final Product not found");
		}
		
		model.setId(d.get().getId());
		model.setProduct(d.get().getProduct());
		model.setQty(d.get().getQty());
		model.setCostPriceVatExcl(d.get().getCostPriceVatExcl());
		model.setCostPriceVatIncl(d.get().getCostPriceVatIncl());
		model.setSellingPriceVatExcl(d.get().getSellingPriceVatExcl());
		model.setSellingPriceVatIncl(d.get().getSellingPriceVatIncl());
		model.setProductToProduct(d.get().getProductToProduct());
		return model;
	}
	
	@Override
	public boolean deleteInitial(ProductToProductInitial productToProductInitial) {
		if(!allowDeleteInitial(productToProductInitial)) {
			throw new InvalidOperationException("Deleting the selected Conversion detail is not allowed");
		}
		productToProductInitialRepository.delete(productToProductInitial);
		return true;
	}
	
	@Override
	public boolean deleteFinal(ProductToProductFinal productToProductFinal) {
		if(!allowDeleteFinal(productToProductFinal)) {
			throw new InvalidOperationException("Deleting the selected Conversion detail is not allowed");
		}
		productToProductFinalRepository.delete(productToProductFinal);
		return true;
	}
	
	@Override
	public List<ProductToProductInitialModel> getAllInitials(ProductToProduct productToProduct) {
		List<ProductToProductInitial> initials = productToProductInitialRepository.findByProductToProduct(productToProduct);
		List<ProductToProductInitialModel> models = new ArrayList<ProductToProductInitialModel>();
		for(ProductToProductInitial d : initials) {
			ProductToProductInitialModel model = new ProductToProductInitialModel();
			
			model.setId(d.getId());
			model.setProduct(d.getProduct());
			model.setQty(d.getQty());
			model.setCostPriceVatExcl(d.getCostPriceVatExcl());
			model.setCostPriceVatIncl(d.getCostPriceVatIncl());
			model.setSellingPriceVatExcl(d.getSellingPriceVatExcl());
			model.setSellingPriceVatIncl(d.getSellingPriceVatIncl());
			model.setProductToProduct(d.getProductToProduct());	
			models.add(model);
		}
		return models;	
	}
	
	@Override
	public List<ProductToProductFinalModel> getAllFinals(ProductToProduct productToProduct) {
		List<ProductToProductFinal> finals = productToProductFinalRepository.findByProductToProduct(productToProduct);
		List<ProductToProductFinalModel> models = new ArrayList<ProductToProductFinalModel>();
		for(ProductToProductFinal d : finals) {
			ProductToProductFinalModel model = new ProductToProductFinalModel();
			
			model.setId(d.getId());
			model.setProduct(d.getProduct());
			model.setQty(d.getQty());
			model.setCostPriceVatExcl(d.getCostPriceVatExcl());
			model.setCostPriceVatIncl(d.getCostPriceVatIncl());
			model.setSellingPriceVatExcl(d.getSellingPriceVatExcl());
			model.setSellingPriceVatIncl(d.getSellingPriceVatIncl());
			model.setProductToProduct(d.getProductToProduct());	
			models.add(model);
		}
		return models;	
	}
	
	@Override
	public boolean archive(ProductToProduct productToProduct) {
		if(!productToProduct.getStatus().equals("APPROVED")) {
			throw new InvalidOperationException("Could not process, only an approved Conversion can be archived");
		}
		productToProduct.setStatus("ARCHIVED");
		productToProductRepository.saveAndFlush(productToProduct);
		return true;
	}
	
	@Override
	public boolean archiveAll() {
		List<ProductToProduct> ptms = productToProductRepository.findAllApproved("APPROVED");
		if(ptms.isEmpty()) {
			throw new NotFoundException("No Document to archive");
		}
		for(ProductToProduct p : ptms) {
			p.setStatus("ARCHIVED");
			productToProductRepository.saveAndFlush(p);
		}
		return true;
	}	
	
	private boolean validate(ProductToProduct productToMaterial) {
		return true;
	}
	
	private boolean allowDelete(ProductToProduct productToProduct) {
		return true;
	}
	
	private boolean validateInitials(ProductToProductInitial productToProductInitial) {
		return true;
	}
	
	private boolean validateFinals(ProductToProductFinal productToProductFinal) {
		return true;
	}
	
	private boolean allowDeleteInitial(ProductToProductInitial productToProductInitial) {
		return true;
	}
	
	private boolean allowDeleteFinal(ProductToProductFinal productToProductFinal) {
		return true;
	}
	
	private String generateProductToProductNo(ProductToProduct productToProduct) {
		Long number = productToProduct.getId();		
		String sNumber = number.toString();
		//return "PTM-"+Formater.formatSix(sNumber);
		return "PTM-"+sNumber;
	}

	@Override
	public ProductToProductModel post(ProductToProduct productToProduct) {
		/**
		 * Save invoice
		 * Deduct products from stock
		 * Update stock cards
		 */
		ProductToProduct ptp = productToProductRepository.saveAndFlush(productToProduct);
		List<ProductToProductInitial> initials = ptp.getProductToProductInitials();
		for(ProductToProductInitial d : initials) {
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
			productStockCard.setReference("Used in Product conversion. Ref #: "+ptp.getNo());
			productStockCardService.save(productStockCard);
			
			
			
		}
		
		
		ptp = productToProductRepository.saveAndFlush(productToProduct);
		List<ProductToProductFinal> finals = ptp.getProductToProductFinals();
		for(ProductToProductFinal d : finals) {
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
			double productStock = product.getStock() + d.getQty();
			product.setStock(productStock);
			productRepository.saveAndFlush(product);
			
			
			ProductStockCard productStockCard = new ProductStockCard();
			productStockCard.setQtyIn(d.getQty());
			productStockCard.setProduct(product);
			productStockCard.setBalance(productStock);
			productStockCard.setDay(dayRepository.getCurrentBussinessDay());
			productStockCard.setReference("Produced in Product conversion. Ref #: "+ptp.getNo());
			productStockCardService.save(productStockCard);
			
			
			
		}
		
		
		
		
		ptp = productToProductRepository.saveAndFlush(ptp);
		ProductToProductModel model = new ProductToProductModel();
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

	
}

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
import com.orbix.api.domain.Drp;
import com.orbix.api.domain.DrpDetail;
import com.orbix.api.domain.Grn;
import com.orbix.api.domain.GrnDetail;
import com.orbix.api.domain.Lpo;
import com.orbix.api.domain.LpoDetail;
import com.orbix.api.domain.Product;
import com.orbix.api.domain.ProductStockCard;
import com.orbix.api.domain.Purchase;
import com.orbix.api.domain.PurchaseDetail;
import com.orbix.api.exceptions.InvalidEntryException;
import com.orbix.api.exceptions.InvalidOperationException;
import com.orbix.api.exceptions.NotFoundException;
import com.orbix.api.models.DrpDetailModel;
import com.orbix.api.models.DrpModel;
import com.orbix.api.models.GrnDetailModel;
import com.orbix.api.models.GrnModel;
import com.orbix.api.models.RecordModel;
import com.orbix.api.repositories.DayRepository;
import com.orbix.api.repositories.DrpDetailRepository;
import com.orbix.api.repositories.DrpRepository;
import com.orbix.api.repositories.GrnDetailRepository;
import com.orbix.api.repositories.GrnRepository;
import com.orbix.api.repositories.ProductRepository;
import com.orbix.api.repositories.PurchaseDetailRepository;
import com.orbix.api.repositories.PurchaseRepository;
import com.orbix.api.repositories.SupplierRepository;
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
public class DrpServiceImpl implements DrpService {
	
	private final DrpRepository drpRepository;
	private final DrpDetailRepository drpDetailRepository;
	private final UserRepository userRepository;
	private final DayRepository dayRepository;
	private final ProductRepository productRepository;
	private final GrnRepository grnRepository;
	private final GrnDetailRepository grnDetailRepository;
	private final PurchaseRepository purchaseRepository;
	private final GrnService grnService;
	private final ProductStockCardService stockCardService;
	private final PurchaseDetailRepository purchaseDetailRepository;
	
	@Override
	public DrpModel save(Drp drp) {
		if(!validate(drp)) {
			throw new InvalidEntryException("Could not save, DRP invalid");
		}
		Drp d = drpRepository.save(drp);
		if(d.getNo().equals("NA")) {
			d.setNo(generateDrpNo(d));
			d = drpRepository.save(d);
		}			
		DrpModel model = new DrpModel();
		model.setId(d.getId());
		model.setNo(d.getNo());
		model.setStatus(d.getStatus());
		model.setComments(d.getComments());
		if(d.getCreatedAt() != null && d.getCreatedBy() != null) {
			model.setCreated(dayRepository.findById(d.getCreatedAt()).get().getBussinessDate() +" "+ userRepository.getAlias(d.getCreatedBy()));
		}
		if(d.getApprovedAt() != null && d.getApprovedBy() != null) {
			model.setApproved(dayRepository.findById(d.getApprovedAt()).get().getBussinessDate() +" "+ userRepository.getAlias(d.getApprovedBy()));
		}		
		return model;
	}
	@Override
	public DrpModel get(Long id) {
		DrpModel model = new DrpModel();
		Optional<Drp> l = drpRepository.findById(id);
		if(!l.isPresent()) {
			throw new NotFoundException("DRP not found");
		}
		model.setId(l.get().getId());
		model.setNo(l.get().getNo());
		model.setStatus(l.get().getStatus());		
		model.setComments(l.get().getComments());
		if(l.get().getCreatedAt() != null && l.get().getCreatedBy() != null) {
			model.setCreated(dayRepository.findById(l.get().getCreatedAt()).get().getBussinessDate() +" "+ userRepository.getAlias(l.get().getCreatedBy()));
		}
		if(l.get().getApprovedAt() != null && l.get().getApprovedBy() != null) {
			model.setApproved(dayRepository.findById(l.get().getApprovedAt()).get().getBussinessDate() +" "+ userRepository.getAlias(l.get().getApprovedBy()));
		}		
		List<DrpDetail> drpDetails = l.get().getDrpDetails();
		List<DrpDetailModel> modelDetails = new ArrayList<DrpDetailModel>();
		for(DrpDetail d : drpDetails) {
			DrpDetailModel modelDetail = new DrpDetailModel();
			modelDetail.setId(d.getId());
			modelDetail.setProduct(d.getProduct());
			modelDetail.setQty(d.getQty());
			modelDetail.setCostPriceVatIncl(d.getCostPriceVatIncl());
			modelDetail.setCostPriceVatExcl(d.getCostPriceVatExcl());
			modelDetail.setDrp(d.getDrp());
			modelDetails.add(modelDetail);
		}
		model.setDrpDetails(modelDetails);
		return model;
	}
	@Override
	public DrpModel getByNo(String no) {
		DrpModel model = new DrpModel();
		Optional<Drp> l = drpRepository.findByNo(no);
		if(!l.isPresent()) {
			throw new NotFoundException("DRP not found");
		}
		model.setId(l.get().getId());
		model.setNo(l.get().getNo());
		model.setStatus(l.get().getStatus());		
		model.setComments(l.get().getComments());
		if(l.get().getCreatedAt() != null && l.get().getCreatedBy() != null) {
			model.setCreated(dayRepository.findById(l.get().getCreatedAt()).get().getBussinessDate() +" "+ userRepository.getAlias(l.get().getCreatedBy()));
		}
		if(l.get().getApprovedAt() != null && l.get().getApprovedBy() != null) {
			model.setApproved(dayRepository.findById(l.get().getApprovedAt()).get().getBussinessDate() +" "+ userRepository.getAlias(l.get().getApprovedBy()));
		}		
		List<DrpDetail> drpDetails = l.get().getDrpDetails();
		List<DrpDetailModel> modelDetails = new ArrayList<DrpDetailModel>();
		for(DrpDetail d : drpDetails) {
			DrpDetailModel modelDetail = new DrpDetailModel();
			modelDetail.setId(d.getId());
			modelDetail.setProduct(d.getProduct());
			modelDetail.setQty(d.getQty());
			modelDetail.setCostPriceVatIncl(d.getCostPriceVatIncl());
			modelDetail.setCostPriceVatExcl(d.getCostPriceVatExcl());
			modelDetail.setDrp(d.getDrp());
			modelDetails.add(modelDetail);
		}
		model.setDrpDetails(modelDetails);
		return model;
	}
	@Override
	public boolean delete(Drp drp) {
		if(!allowDelete(drp)) {
			throw new InvalidOperationException("Deleting the selected DRP is not allowed");
		}
		drpRepository.delete(drp);
		return true;
	}
	
	@Override
	public List<DrpModel> getAllVisible() {
		List<String> statuses = new ArrayList<String>();
		statuses.add("BLANK");
		statuses.add("PENDING");
		statuses.add("APPROVED");		
		List<Drp> drps = drpRepository.findAllVissible(statuses);
		List<DrpModel> models = new ArrayList<DrpModel>();
		for(Drp l : drps) {
			DrpModel model = new DrpModel();
			model.setId(l.getId());
			model.setNo(l.getNo());
			model.setStatus(l.getStatus());
			model.setComments(l.getComments());
			if(l.getCreatedAt() != null && l.getCreatedBy() != null) {
				model.setCreated(dayRepository.findById(l.getCreatedAt()).get().getBussinessDate() +" "+ userRepository.getAlias(l.getCreatedBy()));
			}
			if(l.getApprovedAt() != null && l.getApprovedBy() != null) {
				model.setApproved(dayRepository.findById(l.getApprovedAt()).get().getBussinessDate() +" "+ userRepository.getAlias(l.getApprovedBy()));
			}			
			models.add(model);
		}
		return models;		
	}
	@Override
	public DrpDetailModel saveDetail(DrpDetail drpDetail) {
		if(!validateDetail(drpDetail)) {
			throw new InvalidEntryException("Could not save detail, Invalid entry");
		}		
		DrpDetailModel model = new DrpDetailModel();
		DrpDetail l = drpDetailRepository.save(drpDetail);
		model.setId(l.getId());
		model.setProduct(l.getProduct());
		model.setQty(l.getQty());
		model.setCostPriceVatIncl(l.getCostPriceVatIncl());
		model.setCostPriceVatExcl(l.getCostPriceVatExcl());
		model.setDrp(l.getDrp());
		return model;
	}
	@Override
	public DrpDetailModel getDetail(Long id) {
		DrpDetailModel model = new DrpDetailModel();
		Optional<DrpDetail> l = drpDetailRepository.findById(id);
		if(!l.isPresent()) {
			throw new NotFoundException("DRP detail not found");
		}
		model.setId(l.get().getId());
		model.setProduct(l.get().getProduct());
		model.setQty(l.get().getQty());
		model.setCostPriceVatIncl(l.get().getCostPriceVatIncl());
		model.setCostPriceVatExcl(l.get().getCostPriceVatExcl());
		model.setDrp(l.get().getDrp());
		return model;
	}
	@Override
	public boolean deleteDetail(DrpDetail drpDetail) {
		if(!allowDeleteDetail(drpDetail)) {
			throw new InvalidOperationException("Deleting the selected DRP detail is not allowed");
		}
		drpDetailRepository.delete(drpDetail);
		return true;
	}
	@Override
	public List<DrpDetailModel> getAllDetails(Drp drp) {
		List<DrpDetail> details = drpDetailRepository.findByDrp(drp);
		List<DrpDetailModel> models = new ArrayList<DrpDetailModel>();
		for(DrpDetail l : details) {
			DrpDetailModel model = new DrpDetailModel();
			model.setId(l.getId());
			model.setProduct(l.getProduct());
			model.setQty(l.getQty());
			model.setCostPriceVatIncl(l.getCostPriceVatIncl());
			model.setCostPriceVatExcl(l.getCostPriceVatExcl());
			model.setDrp(l.getDrp());
			models.add(model);
		}
		return models;	
	}
	
	private boolean validate(Drp drp) {
		return true;
	}
	
	private boolean allowDelete(Drp drp) {
		return true;
	}
	
	private boolean validateDetail(DrpDetail drpDetail) {
		Optional<Drp> l = drpRepository.findById(drpDetail.getDrp().getId());
		Optional<Product> p = productRepository.findById(drpDetail.getProduct().getId());
		if(!l.isPresent()) {
			throw new NotFoundException("DRP not found");
		}
		if(!p.isPresent()) {
			throw new NotFoundException("Product not found");
		}		
		return true;
	}
	
	private boolean allowDeleteDetail(DrpDetail drpDetail) {
		return true;
	}
	
	private String generateDrpNo(Drp drp) {
		Long number = drp.getId();		
		String sNumber = number.toString();
		//return "DRP-"+Formater.formatSix(sNumber);
		return Formater.formatWithCurrentDate("DRP",sNumber);
	}
	
	@Override
	public boolean archive(Drp drp) {
		if(!drp.getStatus().equals("APPROVED")) {
			throw new InvalidOperationException("Could not process, only an APPROVED DRP can be archived");
		}
		drp.setStatus("ARCHIVED");
		drpRepository.saveAndFlush(drp);
		return true;
	}

	@Override
	public boolean archiveAll() {
		List<Drp> drps = drpRepository.findAllApproved("APPROVED");
		if(drps.isEmpty()) {
			throw new NotFoundException("No DRP to archive");
		}
		for(Drp l : drps) {
			l.setStatus("ARCHIVED");
			drpRepository.saveAndFlush(l);
		}
		return true;
	}
	@Override
	public RecordModel requestDrpNo() {
		Long id = 1L;
		try {
			id = drpRepository.getLastId() + 1;
		}catch(Exception e) {}
		RecordModel model = new RecordModel();
		model.setNo(Formater.formatWithCurrentDate("DRP",id.toString()));
		return model;
	}
	
	
	
	
	@Override
	public GrnModel approve(Drp drp) {
		/**
		 * First check if DRP is PENDING
		 * Validate DRP
		 * Check all details for validity
		 */
		if(!drp.getStatus().equals("PENDING")) {
			throw new InvalidOperationException("Could not process, only a pending DRP can be approved");
		}
		if(!validate(drp)) {
			throw new InvalidOperationException("Could not process, DRP invalid");
		}
		List<DrpDetail> _drpDetails = drp.getDrpDetails();
		/**
		 * Here, cross check every detail for validity, if invalid, reject all
		 */
		for(DrpDetail d : _drpDetails) {
			if(d.getCostPriceVatIncl() < 0) {
				throw new InvalidEntryException("Could not process, invalid cost price at ["+d.getProduct().getCode()+"] "+d.getProduct().getDescription()+". Cost price must be positive a value.");
			}
			if(d.getQty() < 0) {
				throw new InvalidEntryException("Could not process, invalid quantity at ["+d.getProduct().getCode()+"] "+d.getProduct().getDescription()+". Quantity must be positive a value.");
			}			
		}
		
		
		
		
		
		
		
		
		
		
		
		/**
		 * Create a new GRN space
		 */
		Grn grn = new Grn();
		grn.setNo("NA");
		
		Optional<Drp> l = drpRepository.findByNo(drp.getNo());
		if(!l.isPresent()) {
			/**
			 * Checks if the DRP is present
			 */
			throw new InvalidOperationException("Could not process DRP, DRP not found");
		}
		if(!l.get().getStatus().equals("PENDING")) {
			/**
			 * Checks if the DRP is valid for receiving.
			 * Only PENDING can be received
			 */
			throw new InvalidOperationException("Could not process DRP, only PENDING DRP can be received");
		}
		
//		Optional<Grn> grnOther = grnRepository.findByDrp(l.get());
//		if(grn.getId() == null && grnOther.isPresent()) {
//			throw new InvalidOperationException("Could not process GRN, DRP attached to another GRN");
//		}else if(grn.getId() != null && grnOther.get().getId() != grn.getId()) {
//			throw new InvalidOperationException("Could not process GRN, LPO attached to another GRN");
//		}		
		//if(!validate(grn)) {
			//throw new InvalidEntryException("Could not save, GRN invalid");
		//}
		if(l.isPresent()) {
			grn.setOrderNo(l.get().getNo());
			grn.setRefNo(l.get().getNo());
		}
		/**
		 * Create a GRN collection wrapper
		 */
		List<GrnDetail> grnDetails = new ArrayList<GrnDetail>();
		
		if(grn.getId() == null) {
			/**
			 * If it is a new GRN
			 * Grab the lpo details and add them to grn details
			 */
			List<DrpDetail> drpDetails = drpDetailRepository.findByDrp(l.get());
			for(DrpDetail d : drpDetails) {
				GrnDetail gd = new GrnDetail();
				gd.setClientPriceVatIncl(d.getCostPriceVatIncl());
				gd.setClientPriceVatExcl(d.getCostPriceVatExcl());
				gd.setSupplierPriceVatIncl(d.getCostPriceVatIncl());
				gd.setSupplierPriceVatExcl(d.getCostPriceVatExcl());
				gd.setProduct(d.getProduct());
				gd.setQtyOrdered(d.getQty());
				gd.setQtyReceived(d.getQty());
				grnDetails.add(gd);				
			}
		}
		/**
		 * Save transient property
		 */
		drpRepository.saveAndFlush(l.get());
		/**
		 * Save GRN
		 */
		grn.setDrp(l.get());		
		Grn g = grnRepository.saveAndFlush(grn);
		for(GrnDetail d : grnDetails) {
			d.setGrn(g);			
		}
		
		//if(grn.getId() == null) {
			//g.setGrnDetails(grnDetails);
		//}		
		if(g.getNo().equals("NA")) {
			
			g.setNo(grnService.requestGrnNo().getNo());
			g = grnRepository.saveAndFlush(g);
		}
		if(!grnDetails.isEmpty()) {
			for(GrnDetail d : grnDetails) {
				d.setGrn(g);
				grnDetailRepository.saveAndFlush(d);
			}
		}
		
		GrnModel model = new GrnModel();
		model.setId(g.getId());
		model.setNo(g.getNo());
		model.setOrderNo(g.getOrderNo());
		model.setInvoiceNo(g.getInvoiceNo());
		model.setInvoiceAmount(g.getInvoiceAmount());
		model.setStatus(g.getStatus());
		model.setComments(g.getComments());		
		if(g.getCreatedAt() != null && g.getCreatedBy() != null) {
			model.setCreated(dayRepository.findById(g.getCreatedAt()).get().getBussinessDate() +" "+ userRepository.getAlias(g.getCreatedBy()));
		}
		if(g.getApprovedAt() != null && g.getApprovedBy() != null) {
			model.setApproved(dayRepository.findById(g.getApprovedAt()).get().getBussinessDate() +" "+ userRepository.getAlias(g.getApprovedBy()));
		}
		List<GrnDetailModel> detailModels = new ArrayList<GrnDetailModel>();
		List<GrnDetail> _grnDetails = grnDetailRepository.findByGrn(g);
		for(GrnDetail d : _grnDetails) {
			GrnDetailModel m = new GrnDetailModel();
			m.setClientPriceVatIncl(d.getClientPriceVatIncl());
			m.setClientPriceVatExcl(d.getClientPriceVatExcl());
			m.setSupplierPriceVatIncl(d.getSupplierPriceVatIncl());
			m.setSupplierPriceVatExcl(d.getSupplierPriceVatExcl());
			m.setProduct(d.getProduct());
			m.setQtyOrdered(d.getQtyOrdered());
			m.setQtyReceived(d.getQtyReceived());
			detailModels.add(m);
		}
		model.setGrnDetails(detailModels);
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		/**
		 * Passed all validation checks, 
		 * now receive
		 */
		Optional<Drp> _d = drpRepository.findById(grn.getDrp().getId());
		if(_d.isPresent()) {
			_d.get().setStatus("APPROVED");
			drpRepository.saveAndFlush(_d.get());
		}		
		grn.setStatus("RECEIVED");
		grnRepository.saveAndFlush(grn);
		//Create Purchase
		Purchase purchase = new Purchase();
		purchase.setCreatedBy(grn.getCreatedBy());
		purchase.setCreatedAt(grn.getCreatedAt());
		purchase.setApprovedBy(grn.getApprovedBy());
		purchase.setApprovedAt(grn.getApprovedAt());
		purchase.setDay(dayRepository.getCurrentBussinessDay());
		purchase.setReference("Purchase on credit from GRN: "+grn.getNo());
		purchase.setType("Credit");
		purchase.setStatus("APPROVED");
		purchase = purchaseRepository.saveAndFlush(purchase);
		
		for(GrnDetail d : _grnDetails) {
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
			double stock = product.getStock() + d.getQtyReceived();
			product.setStock(stock);
			productRepository.saveAndFlush(product);
			/**
			 * Now create stock card
			 */
			ProductStockCard stockCard = new ProductStockCard();
			stockCard.setQtyIn(d.getQtyReceived());
			stockCard.setProduct(product);
			stockCard.setBalance(stock);
			stockCard.setDay(dayRepository.getCurrentBussinessDay());
			stockCard.setReference("Goods received. Ref #: "+grn.getNo());
			stockCardService.save(stockCard);
			
			//Create purchase detail
			if(d.getQtyReceived() > 0) {
				PurchaseDetail purchaseDetail = new PurchaseDetail();
				purchaseDetail.setPurchase(purchase);
				purchaseDetail.setProduct(product);
				purchaseDetail.setQty(d.getQtyReceived());
				purchaseDetail.setCostPriceVatExcl(d.getSupplierPriceVatExcl());
				purchaseDetail.setCostPriceVatIncl(d.getSupplierPriceVatIncl());
				purchaseDetail = purchaseDetailRepository.saveAndFlush(purchaseDetail);
			}			
		}
	return model;
	}
}

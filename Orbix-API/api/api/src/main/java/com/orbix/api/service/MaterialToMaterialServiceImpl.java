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
import com.orbix.api.domain.Material;
import com.orbix.api.domain.MaterialStockCard;
import com.orbix.api.domain.MaterialToMaterial;
import com.orbix.api.domain.MaterialToMaterialFinal;
import com.orbix.api.domain.MaterialToMaterialInitial;
import com.orbix.api.exceptions.InvalidEntryException;
import com.orbix.api.exceptions.InvalidOperationException;
import com.orbix.api.exceptions.NotFoundException;
import com.orbix.api.models.MaterialToMaterialFinalModel;
import com.orbix.api.models.MaterialToMaterialInitialModel;
import com.orbix.api.models.MaterialToMaterialModel;
import com.orbix.api.models.RecordModel;
import com.orbix.api.repositories.DayRepository;
import com.orbix.api.repositories.MaterialRepository;
import com.orbix.api.repositories.MaterialToMaterialFinalRepository;
import com.orbix.api.repositories.MaterialToMaterialInitialRepository;
import com.orbix.api.repositories.MaterialToMaterialRepository;
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
public class MaterialToMaterialServiceImpl implements MaterialToMaterialService {
	private final MaterialToMaterialRepository materialToMaterialRepository;
	private final MaterialToMaterialInitialRepository materialToMaterialInitialRepository;
	private final MaterialToMaterialFinalRepository materialToMaterialFinalRepository;
	private final UserRepository userRepository;
	private final DayRepository dayRepository;
	private final MaterialRepository materialRepository;
	private final MaterialStockCardService materialStockCardService;
	
	
	@Override
	public MaterialToMaterialModel save(MaterialToMaterial materialToMaterial) {
		if(!validate(materialToMaterial)) {
			throw new InvalidEntryException("Could not save, Conversion invalid");
		}
		MaterialToMaterial ptp = materialToMaterialRepository.save(materialToMaterial);
		if(ptp.getNo().equals("NA")) {
			ptp.setNo(generateMaterialToMaterialNo(ptp));
			ptp = materialToMaterialRepository.save(ptp);
		}			
		MaterialToMaterialModel model = new MaterialToMaterialModel();
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
	public MaterialToMaterialModel get(Long id) {
		MaterialToMaterialModel model = new MaterialToMaterialModel();
		Optional<MaterialToMaterial> ptp = materialToMaterialRepository.findById(id);
		if(!ptp.isPresent()) {
			throw new NotFoundException("Material To Material not found");
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
		List<MaterialToMaterialInitial> materialToMaterialInitials = ptp.get().getMaterialToMaterialInitials();
		List<MaterialToMaterialFinal> materialToMaterialFinals = ptp.get().getMaterialToMaterialFinals();
		
		List<MaterialToMaterialInitialModel> initialModels = new ArrayList<MaterialToMaterialInitialModel>();
		List<MaterialToMaterialFinalModel> finalModels = new ArrayList<MaterialToMaterialFinalModel>();
		
		for(MaterialToMaterialInitial in : materialToMaterialInitials) {
			MaterialToMaterialInitialModel initialModel = new MaterialToMaterialInitialModel();
			initialModel.setId(in.getId());
			initialModel.setMaterial(in.getMaterial());
			initialModel.setQty(in.getQty());
			initialModel.setCostPriceVatExcl(in.getCostPriceVatExcl());
			initialModel.setCostPriceVatIncl(in.getCostPriceVatIncl());
			initialModel.setMaterialToMaterial(in.getMaterialToMaterial());
			initialModels.add(initialModel);			
		}
		
		for(MaterialToMaterialFinal fin : materialToMaterialFinals) {
			MaterialToMaterialFinalModel finalModel = new MaterialToMaterialFinalModel();
			finalModel.setId(fin.getId());
			finalModel.setMaterial(fin.getMaterial());
			finalModel.setQty(fin.getQty());
			finalModel.setCostPriceVatExcl(fin.getCostPriceVatExcl());
			finalModel.setCostPriceVatIncl(fin.getCostPriceVatIncl());
			finalModel.setMaterialToMaterial(fin.getMaterialToMaterial());
			finalModels.add(finalModel);			
		}
		
		model.setMaterialToMaterialInitials(initialModels);
		model.setMaterialToMaterialFinals(finalModels);
		return model;
	}
	
	@Override
	public MaterialToMaterialModel getByNo(String no) {
		MaterialToMaterialModel model = new MaterialToMaterialModel();
		Optional<MaterialToMaterial> ptp = materialToMaterialRepository.findByNo(no);
		if(!ptp.isPresent()) {
			throw new NotFoundException("Material To Material not found");
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
		List<MaterialToMaterialInitial> materialToMaterialInitials = ptp.get().getMaterialToMaterialInitials();
		List<MaterialToMaterialFinal> materialToMaterialFinals = ptp.get().getMaterialToMaterialFinals();
		
		List<MaterialToMaterialInitialModel> initialModels = new ArrayList<MaterialToMaterialInitialModel>();
		List<MaterialToMaterialFinalModel> finalModels = new ArrayList<MaterialToMaterialFinalModel>();
		
		for(MaterialToMaterialInitial in : materialToMaterialInitials) {
			MaterialToMaterialInitialModel initialModel = new MaterialToMaterialInitialModel();
			initialModel.setId(in.getId());
			initialModel.setMaterial(in.getMaterial());
			initialModel.setQty(in.getQty());
			initialModel.setCostPriceVatExcl(in.getCostPriceVatExcl());
			initialModel.setCostPriceVatIncl(in.getCostPriceVatIncl());
			initialModel.setMaterialToMaterial(in.getMaterialToMaterial());
			initialModels.add(initialModel);			
		}
		
		for(MaterialToMaterialFinal fin : materialToMaterialFinals) {
			MaterialToMaterialFinalModel finalModel = new MaterialToMaterialFinalModel();
			finalModel.setId(fin.getId());
			finalModel.setMaterial(fin.getMaterial());
			finalModel.setQty(fin.getQty());
			finalModel.setCostPriceVatExcl(fin.getCostPriceVatExcl());
			finalModel.setCostPriceVatIncl(fin.getCostPriceVatIncl());
			finalModel.setMaterialToMaterial(fin.getMaterialToMaterial());
			finalModels.add(finalModel);			
		}
		
		model.setMaterialToMaterialInitials(initialModels);
		model.setMaterialToMaterialFinals(finalModels);
		return model;
	}
	
	@Override
	public boolean delete(MaterialToMaterial materialToMaterial) {
		if(!allowDelete(materialToMaterial)) {
			throw new InvalidOperationException("Deleting the selected Conversion is not allowed");
		}
		materialToMaterialRepository.delete(materialToMaterial);
		return true;
	}
	
	@Override
	public List<MaterialToMaterialModel> getAllVisible() {
		List<String> statuses = new ArrayList<String>();
		statuses.add("BLANK");
		statuses.add("PENDING");
		statuses.add("APPROVED");
		List<MaterialToMaterial> ptms = materialToMaterialRepository.findAllVissible(statuses);
		List<MaterialToMaterialModel> models = new ArrayList<MaterialToMaterialModel>();
		for(MaterialToMaterial ptm : ptms) {
			MaterialToMaterialModel model = new MaterialToMaterialModel();
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
	public MaterialToMaterialInitialModel saveInitial(MaterialToMaterialInitial materialToMaterialInitial) {
		if(!validateInitials(materialToMaterialInitial)) {
			throw new InvalidEntryException("Could not save detail, Invalid entry");
		}
		MaterialToMaterialInitialModel model = new MaterialToMaterialInitialModel();
		MaterialToMaterialInitial d = materialToMaterialInitialRepository.save(materialToMaterialInitial);
		
		model.setId(d.getId());
		model.setMaterial(d.getMaterial());
		model.setQty(d.getQty());
		model.setCostPriceVatExcl(d.getCostPriceVatExcl());
		model.setCostPriceVatIncl(d.getCostPriceVatIncl());
		model.setMaterialToMaterial(d.getMaterialToMaterial());
	
		return model;
	}
	
	@Override
	public MaterialToMaterialFinalModel saveFinal(MaterialToMaterialFinal materialToMaterialFinal) {
		if(!validateFinals(materialToMaterialFinal)) {
			throw new InvalidEntryException("Could not save detail, Invalid entry");
		}
		MaterialToMaterialFinalModel model = new MaterialToMaterialFinalModel();
		MaterialToMaterialFinal d = materialToMaterialFinalRepository.save(materialToMaterialFinal);
		
		model.setId(d.getId());
		model.setMaterial(d.getMaterial());
		model.setQty(d.getQty());
		model.setCostPriceVatExcl(d.getCostPriceVatExcl());
		model.setCostPriceVatIncl(d.getCostPriceVatIncl());
		model.setMaterialToMaterial(d.getMaterialToMaterial());
	
		return model;
	}
	
	@Override
	public MaterialToMaterialInitialModel getInitial(Long id) {
		MaterialToMaterialInitialModel model = new MaterialToMaterialInitialModel();
		Optional<MaterialToMaterialInitial> d = materialToMaterialInitialRepository.findById(id);
		if(!d.isPresent()) {
			throw new NotFoundException("Initial Material not found");
		}
		
		model.setId(d.get().getId());
		model.setMaterial(d.get().getMaterial());
		model.setQty(d.get().getQty());
		model.setCostPriceVatExcl(d.get().getCostPriceVatExcl());
		model.setCostPriceVatIncl(d.get().getCostPriceVatIncl());
		model.setMaterialToMaterial(d.get().getMaterialToMaterial());
		return model;
	}
	
	@Override
	public MaterialToMaterialFinalModel getFinal(Long id) {
		MaterialToMaterialFinalModel model = new MaterialToMaterialFinalModel();
		Optional<MaterialToMaterialFinal> d = materialToMaterialFinalRepository.findById(id);
		if(!d.isPresent()) {
			throw new NotFoundException("Final Material not found");
		}
		
		model.setId(d.get().getId());
		model.setMaterial(d.get().getMaterial());
		model.setQty(d.get().getQty());
		model.setCostPriceVatExcl(d.get().getCostPriceVatExcl());
		model.setCostPriceVatIncl(d.get().getCostPriceVatIncl());
		model.setMaterialToMaterial(d.get().getMaterialToMaterial());
		return model;
	}
	
	@Override
	public boolean deleteInitial(MaterialToMaterialInitial materialToMaterialInitial) {
		if(!allowDeleteInitial(materialToMaterialInitial)) {
			throw new InvalidOperationException("Deleting the selected Conversion detail is not allowed");
		}
		materialToMaterialInitialRepository.delete(materialToMaterialInitial);
		return true;
	}
	
	@Override
	public boolean deleteFinal(MaterialToMaterialFinal materialToMaterialFinal) {
		if(!allowDeleteFinal(materialToMaterialFinal)) {
			throw new InvalidOperationException("Deleting the selected Conversion detail is not allowed");
		}
		materialToMaterialFinalRepository.delete(materialToMaterialFinal);
		return true;
	}
	
	@Override
	public List<MaterialToMaterialInitialModel> getAllInitials(MaterialToMaterial materialToMaterial) {
		List<MaterialToMaterialInitial> initials = materialToMaterialInitialRepository.findByMaterialToMaterial(materialToMaterial);
		List<MaterialToMaterialInitialModel> models = new ArrayList<MaterialToMaterialInitialModel>();
		for(MaterialToMaterialInitial d : initials) {
			MaterialToMaterialInitialModel model = new MaterialToMaterialInitialModel();
			
			model.setId(d.getId());
			model.setMaterial(d.getMaterial());
			model.setQty(d.getQty());
			model.setCostPriceVatExcl(d.getCostPriceVatExcl());
			model.setCostPriceVatIncl(d.getCostPriceVatIncl());
			model.setMaterialToMaterial(d.getMaterialToMaterial());	
			models.add(model);
		}
		return models;	
	}
	
	@Override
	public List<MaterialToMaterialFinalModel> getAllFinals(MaterialToMaterial materialToMaterial) {
		List<MaterialToMaterialFinal> finals = materialToMaterialFinalRepository.findByMaterialToMaterial(materialToMaterial);
		List<MaterialToMaterialFinalModel> models = new ArrayList<MaterialToMaterialFinalModel>();
		for(MaterialToMaterialFinal d : finals) {
			MaterialToMaterialFinalModel model = new MaterialToMaterialFinalModel();
			
			model.setId(d.getId());
			model.setMaterial(d.getMaterial());
			model.setQty(d.getQty());
			model.setCostPriceVatExcl(d.getCostPriceVatExcl());
			model.setCostPriceVatIncl(d.getCostPriceVatIncl());
			model.setMaterialToMaterial(d.getMaterialToMaterial());	
			models.add(model);
		}
		return models;	
	}
	
	@Override
	public boolean archive(MaterialToMaterial materialToMaterial) {
		if(!materialToMaterial.getStatus().equals("APPROVED")) {
			throw new InvalidOperationException("Could not process, only an approved Conversion can be archived");
		}
		materialToMaterial.setStatus("ARCHIVED");
		materialToMaterialRepository.saveAndFlush(materialToMaterial);
		return true;
	}
	
	@Override
	public boolean archiveAll() {
		List<MaterialToMaterial> ptms = materialToMaterialRepository.findAllApproved("APPROVED");
		if(ptms.isEmpty()) {
			throw new NotFoundException("No Document to archive");
		}
		for(MaterialToMaterial p : ptms) {
			p.setStatus("ARCHIVED");
			materialToMaterialRepository.saveAndFlush(p);
		}
		return true;
	}	
	
	private boolean validate(MaterialToMaterial materialToMaterial) {
		return true;
	}
	
	private boolean allowDelete(MaterialToMaterial materialToMaterial) {
		return true;
	}
	
	private boolean validateInitials(MaterialToMaterialInitial materialToMaterialInitial) {
		return true;
	}
	
	private boolean validateFinals(MaterialToMaterialFinal materialToMaterialFinal) {
		return true;
	}
	
	private boolean allowDeleteInitial(MaterialToMaterialInitial materialToMaterialInitial) {
		return true;
	}
	
	private boolean allowDeleteFinal(MaterialToMaterialFinal materialToMaterialFinal) {
		return true;
	}
	
	private String generateMaterialToMaterialNo(MaterialToMaterial materialToMaterial) {
		Long number = materialToMaterial.getId();		
		String sNumber = number.toString();
		//return "PTM-"+Formater.formatSix(sNumber);
		//return "PTM-"+sNumber;
		return Formater.formatWithCurrentDate("MTM",sNumber);
	}

	@Override
	public MaterialToMaterialModel post(MaterialToMaterial materialToMaterial) {
		/**
		 * Save invoice
		 * Deduct materials from stock
		 * Update stock cards
		 */
		MaterialToMaterial ptp = materialToMaterialRepository.saveAndFlush(materialToMaterial);
		List<MaterialToMaterialInitial> initials = ptp.getMaterialToMaterialInitials();
		for(MaterialToMaterialInitial d : initials) {
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
			Material material =materialRepository.findById(d.getMaterial().getId()).get();
			double materialStock = material.getStock() - d.getQty();
			material.setStock(materialStock);
			materialRepository.saveAndFlush(material);
			
			
			MaterialStockCard materialStockCard = new MaterialStockCard();
			materialStockCard.setQtyOut(d.getQty());
			materialStockCard.setMaterial(material);
			materialStockCard.setBalance(materialStock);
			materialStockCard.setDay(dayRepository.getCurrentBussinessDay());
			materialStockCard.setReference("Used in Material conversion. Ref #: "+ptp.getNo());
			materialStockCardService.save(materialStockCard);
			
			
			
		}
		
		
		ptp = materialToMaterialRepository.saveAndFlush(materialToMaterial);
		List<MaterialToMaterialFinal> finals = ptp.getMaterialToMaterialFinals();
		for(MaterialToMaterialFinal d : finals) {
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
			Material material =materialRepository.findById(d.getMaterial().getId()).get();
			double materialStock = material.getStock() + d.getQty();
			material.setStock(materialStock);
			materialRepository.saveAndFlush(material);
			
			
			MaterialStockCard materialStockCard = new MaterialStockCard();
			materialStockCard.setQtyIn(d.getQty());
			materialStockCard.setMaterial(material);
			materialStockCard.setBalance(materialStock);
			materialStockCard.setDay(dayRepository.getCurrentBussinessDay());
			materialStockCard.setReference("Produced in Material conversion. Ref #: "+ptp.getNo());
			materialStockCardService.save(materialStockCard);
			
			
			
		}
		
		
		
		
		ptp = materialToMaterialRepository.saveAndFlush(ptp);
		MaterialToMaterialModel model = new MaterialToMaterialModel();
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
	public RecordModel requestMTMNo() {
		Long id = 1L;
		try {
			id = materialToMaterialRepository.getLastId() + 1;
		}catch(Exception e) {}
		RecordModel model = new RecordModel();
		model.setNo(Formater.formatWithCurrentDate("MTM",id.toString()));
		return model;
	}	
}

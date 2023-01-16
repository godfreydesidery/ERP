/**
 * 
 */
package com.orbix.api.api;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
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

import com.orbix.api.domain.Material;
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
import com.orbix.api.repositories.MaterialRepository;
import com.orbix.api.repositories.MaterialToMaterialFinalRepository;
import com.orbix.api.repositories.MaterialToMaterialInitialRepository;
import com.orbix.api.repositories.MaterialToMaterialRepository;
import com.orbix.api.service.DayService;
import com.orbix.api.service.MaterialToMaterialService;
import com.orbix.api.service.UserService;

import lombok.RequiredArgsConstructor;

/**
 * @author Godfrey
 *
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class MaterialToMaterialResource {
	private final UserService userService;
	private final DayService dayService;	
	private final MaterialToMaterialRepository materialToMaterialRepository;
	private final MaterialToMaterialInitialRepository materialToMaterialInitialRepository;
	private final MaterialToMaterialFinalRepository materialToMaterialFinalRepository;
	private final MaterialRepository materialRepository;
	private final MaterialToMaterialService materialToMaterialService;
	
	@GetMapping("/material_to_materials")
	public ResponseEntity<List<MaterialToMaterialModel>>getMaterialToMaterials(){
		return ResponseEntity.ok().body(materialToMaterialService.getAllVisible());
	}
	
	@GetMapping("/material_to_materials/request_no")
	public ResponseEntity<RecordModel> requestNo(){
		return ResponseEntity.ok().body(materialToMaterialService.requestMTMNo());
	}
	
	@GetMapping("/material_to_materials/get")
	public ResponseEntity<MaterialToMaterialModel> getMaterialToMaterial(
			@RequestParam(name = "id") Long id){
		return ResponseEntity.ok().body(materialToMaterialService.get(id));
	}
	
	@GetMapping("/material_to_materials/get_by_no")
	public ResponseEntity<MaterialToMaterialModel> getMaterialToMaterialByNo(
			@RequestParam(name = "no") String no){
		return ResponseEntity.ok().body(materialToMaterialService.getByNo(no));
	}
	
	@GetMapping("/material_to_material_initials/get_by_material_to_material")
	public ResponseEntity<List<MaterialToMaterialInitialModel>>getMaterialToMaterialInitials(
			@RequestParam(name = "id") Long id){		
		return ResponseEntity.ok().body(materialToMaterialService.getAllInitials(materialToMaterialRepository.findById(id).get()));
	}
	
	@GetMapping("/material_to_material_finals/get_by_material_to_material")
	public ResponseEntity<List<MaterialToMaterialFinalModel>>getMaterialToMaterialFinals(
			@RequestParam(name = "id") Long id){		
		return ResponseEntity.ok().body(materialToMaterialService.getAllFinals(materialToMaterialRepository.findById(id).get()));
	}
	
	@PostMapping("/material_to_materials/create")
	//@PreAuthorize("hasAnyAuthority('CONVERSION-CREATE')")
	public ResponseEntity<MaterialToMaterialModel>createMaterialToMaterial(
			@RequestBody MaterialToMaterial materialToMaterial,
			HttpServletRequest request){		
		MaterialToMaterial ptp = new MaterialToMaterial();
		ptp.setNo("NA");
		ptp.setStatus("PENDING");
		ptp.setReason(materialToMaterial.getReason());
		ptp.setComments(materialToMaterial.getComments());	
		ptp.setCreatedBy(userService.getUserId(request));
		ptp.setCreatedAt(dayService.getDayId());
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/material_to_materials/create").toUriString());
		return ResponseEntity.created(uri).body(materialToMaterialService.save(ptp));
	}
	
	@PutMapping("/material_to_materials/update")
	//@PreAuthorize("hasAnyAuthority('CONVERSION-UPDATE')")
	public ResponseEntity<MaterialToMaterialModel>updateMaterialToMaterial(
			@RequestBody MaterialToMaterial materialToMaterial,
			HttpServletRequest request){
		Optional<MaterialToMaterial> l = materialToMaterialRepository.findById(materialToMaterial.getId());
		if(!l.isPresent()) {
			throw new NotFoundException("CONVERSION not found");
		}
		if(!l.get().getStatus().equals("PENDING")) {
			throw new InvalidOperationException("Editing not allowed, only Pending Conversions can be edited");
		}
		//List<MaterialToMaterialDetail> dzz = materialToMaterialDetailRepository.findByMaterialToMaterial(l.get());
		l.get().setReason(materialToMaterial.getReason());
		l.get().setComments(materialToMaterial.getComments());
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/material_to_materials/update").toUriString());
		return ResponseEntity.created(uri).body(materialToMaterialService.save(l.get()));
	}
	
	@PutMapping("/material_to_materials/approve")
	//@PreAuthorize("hasAnyAuthority('CONVERSION-APPROVE')")
	public ResponseEntity<MaterialToMaterialModel>approveMaterialToMaterial(
			@RequestBody MaterialToMaterial materialToMaterial,
			HttpServletRequest request){		
		Optional<MaterialToMaterial> l = materialToMaterialRepository.findById(materialToMaterial.getId());
		if(!l.isPresent()) {
			throw new NotFoundException("CONVERSION not found");
		}
		if(l.get().getStatus().equals("PENDING")) {
			l.get().setApprovedBy(userService.getUserId(request));
			l.get().setApprovedAt(dayService.getDayId());
			l.get().setStatus("APPROVED");
		}else {
			throw new InvalidOperationException("Could not approve, not a PENDING CONVERSION");
		}
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/material_to_materials/approve").toUriString());
		return ResponseEntity.created(uri).body(materialToMaterialService.post(l.get()));
	}
	
	@PutMapping("/material_to_materials/cancel")
	//@PreAuthorize("hasAnyAuthority('CONVERSION-CANCEL')")
	public ResponseEntity<MaterialToMaterialModel>cancelMaterialToMaterial(
			@RequestBody MaterialToMaterial materialToMaterial,
			HttpServletRequest request){		
		Optional<MaterialToMaterial> l = materialToMaterialRepository.findById(materialToMaterial.getId());
		if(!l.isPresent()) {
			throw new NotFoundException("CONVERSION not found");
		}
		if(l.get().getStatus().equals("PENDING") || l.get().getStatus().equals("BLANK")) {
			l.get().setStatus("CANCELED");
		}else {
			throw new InvalidOperationException("Could not cancel, only Pending CONVERSIONs can be canceled");
		}
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/material_to_materials/cancel").toUriString());
		return ResponseEntity.created(uri).body(materialToMaterialService.save(l.get()));
	}
	
	@PutMapping("/material_to_materials/archive")
	//@PreAuthorize("hasAnyAuthority('CONVERSION-CREATE','CONVERSION-UPDATE','CONVERSION-ARCHIVE')")
	public ResponseEntity<Boolean>archiveMaterialToMaterial(
			@RequestBody MaterialToMaterial materialToMaterial,
			HttpServletRequest request){		
		Optional<MaterialToMaterial> l = materialToMaterialRepository.findById(materialToMaterial.getId());
		if(!l.isPresent()) {
			throw new NotFoundException("CONVERSION not found");
		}
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/material_to_materials/archive").toUriString());
		return ResponseEntity.created(uri).body(materialToMaterialService.archive(l.get()));
	}
	
	@PutMapping("/material_to_materials/archive_all")
	//@PreAuthorize("hasAnyAuthority('CONVERSION-CREATE','CONVERSION-UPDATE','CONVERSION-ARCHIVE')")
	public ResponseEntity<Boolean>archiveMaterialToMaterials(){			
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/material_to_materials/archive_all").toUriString());
		return ResponseEntity.created(uri).body(materialToMaterialService.archiveAll());
	}
	
	@PostMapping("/material_to_material_initials/save")
	//@PreAuthorize("hasAnyAuthority('CONVERSION-CREATE','CONVERSION-UPDATE')")
	public ResponseEntity<MaterialToMaterialInitialModel>createMaterialToMaterialInitial(
			@RequestBody MaterialToMaterialInitial materialToMaterialInitial){
		if(materialToMaterialInitial.getQty() <= 0) {
			throw new InvalidEntryException("Quantity value should be more than 0");
		}
		Optional<MaterialToMaterial> l = materialToMaterialRepository.findById(materialToMaterialInitial.getMaterialToMaterial().getId());
		if(!l.isPresent()) {
			throw new NotFoundException("CONVERSION not found");
		}
		if(l.get().getStatus().equals("BLANK")) {
			l.get().setStatus("PENDING");
			materialToMaterialRepository.saveAndFlush(l.get());
		}
		if(!(l.get().getStatus().equals("PENDING") || l.get().getStatus().equals("BLANK"))) {
			throw new InvalidOperationException("Editing is not allowed, only PENDING or BLANK CONVERSIONs can be edited.");
		}
		Optional<Material> p = materialRepository.findById(materialToMaterialInitial.getMaterial().getId());
		if(!p.isPresent()) {
			throw new NotFoundException("Material not found");
		}
		
		Optional<MaterialToMaterialInitial> d = materialToMaterialInitialRepository.findByMaterialAndMaterialToMaterial(p.get(), l.get());		
		
		MaterialToMaterialInitial detail = new MaterialToMaterialInitial();
		if(d.isPresent()) {
			/**
			 * Update existing detail
			 */
			detail = d.get();			
			detail.setQty(materialToMaterialInitial.getQty());
		}else {
			/**
			 * Create new detail
			 */
			Optional<MaterialToMaterialInitial> q = materialToMaterialInitialRepository.findByMaterialAndMaterialToMaterial(p.get(), l.get());
			if(q.isPresent()) {
				throw new InvalidOperationException("Material exist in list. Consider editing quantity");
			}
			
			detail.setMaterialToMaterial(l.get());
			detail.setMaterial(materialToMaterialInitial.getMaterial());
			detail.setQty(materialToMaterialInitial.getQty());
		}		
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/material_to_material_initials/save").toUriString());
		return ResponseEntity.created(uri).body(materialToMaterialService.saveInitial(detail));
	}
	
	
	@PostMapping("/material_to_material_finals/save")
	//@PreAuthorize("hasAnyAuthority('CONVERSION-CREATE','CONVERSION-UPDATE')")
	public ResponseEntity<MaterialToMaterialFinalModel>createMaterialToMaterialFinal(
			@RequestBody MaterialToMaterialFinal materialToMaterialFinal){
		if(materialToMaterialFinal.getQty() <= 0) {
			throw new InvalidEntryException("Quantity value should be more than 0");
		}
		Optional<MaterialToMaterial> l = materialToMaterialRepository.findById(materialToMaterialFinal.getMaterialToMaterial().getId());
		if(!l.isPresent()) {
			throw new NotFoundException("CONVERSION not found");
		}
		if(l.get().getStatus().equals("BLANK")) {
			l.get().setStatus("PENDING");
			materialToMaterialRepository.saveAndFlush(l.get());
		}
		if(!(l.get().getStatus().equals("PENDING") || l.get().getStatus().equals("BLANK"))) {
			throw new InvalidOperationException("Editing is not allowed, only PENDING or BLANK CONVERSIONs can be edited.");
		}
		Optional<Material> p = materialRepository.findById(materialToMaterialFinal.getMaterial().getId());
		if(!p.isPresent()) {
			throw new NotFoundException("Material not found");
		}
		
		Optional<MaterialToMaterialFinal> d = materialToMaterialFinalRepository.findByMaterialAndMaterialToMaterial(p.get(), l.get());
		
		MaterialToMaterialFinal detail = new MaterialToMaterialFinal();
		if(d.isPresent()) {
			/**
			 * Update existing detail
			 */
			detail = d.get();			
			detail.setQty(materialToMaterialFinal.getQty());
		}else {
			/**
			 * Create new detail
			 */
			Optional<MaterialToMaterialFinal> q = materialToMaterialFinalRepository.findByMaterialAndMaterialToMaterial(p.get(), l.get());
			if(q.isPresent()) {
				throw new InvalidOperationException("Material exist in list. Consider editing quantity");
			}
			detail.setMaterialToMaterial(l.get());
			detail.setMaterial(materialToMaterialFinal.getMaterial());
			detail.setQty(materialToMaterialFinal.getQty());
		}		
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/material_to_material_initials/save").toUriString());
		return ResponseEntity.created(uri).body(materialToMaterialService.saveFinal(detail));
	}
	
	@GetMapping("/material_to_material_initials/get")
	//@PreAuthorize("hasAnyAuthority('CONVERSION-CREATE','CONVERSION-UPDATE')")
	public ResponseEntity<MaterialToMaterialInitialModel>getInitial(
			@RequestParam(name = "id") Long id){		
		Optional<MaterialToMaterialInitial> d = materialToMaterialInitialRepository.findById(id);
		if(!d.isPresent()) {
			throw new NotFoundException("Detail not found");
		}		
		MaterialToMaterialInitialModel detail = new MaterialToMaterialInitialModel();
		detail.setId(d.get().getId());
		detail.setMaterial(d.get().getMaterial());
		detail.setQty(d.get().getQty());
		detail.setCostPriceVatExcl(d.get().getCostPriceVatExcl());
		detail.setCostPriceVatIncl(d.get().getCostPriceVatIncl());
		
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/material_to_material_initials/get").toUriString());
		return ResponseEntity.created(uri).body(detail);
	}
	
	@GetMapping("/material_to_material_finals/get")
	//@PreAuthorize("hasAnyAuthority('CONVERSION-CREATE','CONVERSION-UPDATE')")
	public ResponseEntity<MaterialToMaterialFinalModel>getFinal(
			@RequestParam(name = "id") Long id){		
		Optional<MaterialToMaterialFinal> d = materialToMaterialFinalRepository.findById(id);
		if(!d.isPresent()) {
			throw new NotFoundException("Detail not found");
		}		
		MaterialToMaterialFinalModel detail = new MaterialToMaterialFinalModel();
		detail.setId(d.get().getId());
		detail.setMaterial(d.get().getMaterial());
		detail.setQty(d.get().getQty());
		detail.setCostPriceVatExcl(d.get().getCostPriceVatExcl());
		detail.setCostPriceVatIncl(d.get().getCostPriceVatIncl());
		
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/material_to_material_finals/get").toUriString());
		return ResponseEntity.created(uri).body(detail);
	}
	
	@DeleteMapping("/material_to_material_initials/delete")
	//@PreAuthorize("hasAnyAuthority('CONVERSION-CREATE','CONVERSION-UPDATE')")
	public ResponseEntity<Boolean> deleteInitial(
			@RequestParam(name = "id") Long id){		
		Optional<MaterialToMaterialInitial> d = materialToMaterialInitialRepository.findById(id);
		if(!d.isPresent()) {
			throw new NotFoundException("Detail not found");
		}
		MaterialToMaterial materialToMaterial = d.get().getMaterialToMaterial();
		if(!materialToMaterial.getStatus().equals("PENDING")) {
			throw new InvalidOperationException("Editing not allowed, only pending CONVERSION can be edited");
		}		
		materialToMaterialInitialRepository.delete(d.get());		
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/material_to_material_initials/delete").toUriString());
		return ResponseEntity.created(uri).body(true);
	}
	
	@DeleteMapping("/material_to_material_finals/delete")
	//@PreAuthorize("hasAnyAuthority('CONVERSION-CREATE','CONVERSION-UPDATE')")
	public ResponseEntity<Boolean> deleteFinal(
			@RequestParam(name = "id") Long id){		
		Optional<MaterialToMaterialFinal> d = materialToMaterialFinalRepository.findById(id);
		if(!d.isPresent()) {
			throw new NotFoundException("Detail not found");
		}
		MaterialToMaterial materialToMaterial = d.get().getMaterialToMaterial();
		if(!materialToMaterial.getStatus().equals("PENDING")) {
			throw new InvalidOperationException("Editing not allowed, only pending CONVERSION can be edited");
		}		
		materialToMaterialFinalRepository.delete(d.get());		
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/material_to_material_finals/delete").toUriString());
		return ResponseEntity.created(uri).body(true);
	}
}

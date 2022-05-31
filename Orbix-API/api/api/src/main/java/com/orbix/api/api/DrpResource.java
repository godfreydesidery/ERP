/**
 * 
 */
package com.orbix.api.api;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

import com.orbix.api.domain.Drp;
import com.orbix.api.domain.DrpDetail;
import com.orbix.api.domain.Product;
import com.orbix.api.exceptions.InvalidEntryException;
import com.orbix.api.exceptions.InvalidOperationException;
import com.orbix.api.exceptions.NotFoundException;
import com.orbix.api.models.DrpDetailModel;
import com.orbix.api.models.DrpModel;
import com.orbix.api.models.GrnModel;
import com.orbix.api.models.RecordModel;
import com.orbix.api.repositories.DrpDetailRepository;
import com.orbix.api.repositories.DrpRepository;
import com.orbix.api.repositories.ProductRepository;
import com.orbix.api.service.DayService;
import com.orbix.api.service.DrpService;
import com.orbix.api.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Godfrey
 *
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class DrpResource {
	
	private final 	UserService userService;
	private final 	DayService dayService;
	private final 	DrpService drpService;
	private final 	DrpRepository drpRepository;
	private final 	DrpDetailRepository drpDetailRepository;
	private final 	ProductRepository productRepository;
	
	@GetMapping("/drps")
	public ResponseEntity<List<DrpModel>>getDrps(){
		return ResponseEntity.ok().body(drpService.getAllVisible());
	}
	
	@GetMapping("/drps/get")
	public ResponseEntity<DrpModel> getDrp(
			@RequestParam(name = "id") Long id){
		return ResponseEntity.ok().body(drpService.get(id));
	}
	
	@GetMapping("/drps/get_by_no")
	public ResponseEntity<DrpModel> getDrpByNo(
			@RequestParam(name = "no") String no){
		return ResponseEntity.ok().body(drpService.getByNo(no));
	}
	
	@GetMapping("/drps/request_no")
	@PreAuthorize("hasAnyAuthority('DRP-CREATE')")
	public ResponseEntity<RecordModel> requestNo(){
		return ResponseEntity.ok().body(drpService.requestDrpNo());
	}
	
	
	
	@GetMapping("/drp_details/get_by_drp")
	@PreAuthorize("hasAnyAuthority('DRP-READ')")
	public ResponseEntity<List<DrpDetailModel>>getDrpDetails(
			@RequestParam(name = "id") Long id){		
		return ResponseEntity.ok().body(drpService.getAllDetails(drpRepository.findById(id).get()));
	}
	
	@PostMapping("/drps/create")
	@PreAuthorize("hasAnyAuthority('DRP-CREATE')")
	public ResponseEntity<DrpModel>createDrp(
			@RequestBody Drp drp,
			HttpServletRequest request){
		Drp l = new Drp();
		l.setNo("NA");
		l.setStatus("BLANK");		
		l.setComments(drp.getComments());
		l.setCreatedBy(userService.getUserId(request));
		l.setCreatedAt(dayService.getDayId());
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/drps/create").toUriString());
		return ResponseEntity.created(uri).body(drpService.save(l));
	}
	
	@PutMapping("/drps/update")
	@PreAuthorize("hasAnyAuthority('DRP-UPDATE')")
	public ResponseEntity<DrpModel>updateDrp(
			@RequestBody Drp drp,
			HttpServletRequest request){		
		Optional<Drp> l = drpRepository.findById(drp.getId());
		if(!l.isPresent()) {
			throw new NotFoundException("DRP not found");
		}
		if(!l.get().getStatus().equals("PENDING")) {
			throw new InvalidOperationException("Editing not allowed, only Pending DRPs can be edited");
		}
		drpDetailRepository.findByDrp(l.get());						
		l.get().setComments(drp.getComments());
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/drps/update").toUriString());
		return ResponseEntity.created(uri).body(drpService.save(l.get()));
	}
	
	@PutMapping("/drps/approve")
	@PreAuthorize("hasAnyAuthority('DRP-APPROVE')")
	public ResponseEntity<GrnModel>approveDrp(
			@RequestBody Drp drp,
			HttpServletRequest request){		
		Optional<Drp> l = drpRepository.findById(drp.getId());
		if(!l.isPresent()) {
			throw new NotFoundException("DRP not found");
		}
		if(l.get().getStatus().equals("PENDING")) {
			l.get().setApprovedBy(userService.getUserId(request));
			l.get().setApprovedAt(dayService.getDayId());
		}else {
			throw new InvalidOperationException("Could not approve, not a PENDING DRP");
		}
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/drps/approve").toUriString());
		return ResponseEntity.created(uri).body(drpService.approve(l.get()));
	}
	
	@PutMapping("/drps/cancel")
	@PreAuthorize("hasAnyAuthority('DRP-CANCEL')")
	public ResponseEntity<DrpModel>cancelDrp(
			@RequestBody Drp drp,
			HttpServletRequest request){		
		Optional<Drp> l = drpRepository.findById(drp.getId());
		if(!l.isPresent()) {
			throw new NotFoundException("DRP not found");
		}
		if(l.get().getStatus().equals("PENDING")) {
			l.get().setStatus("CANCELED");
		}else {
			throw new InvalidOperationException("Could not cancel, only Pending or Approved DRPs can be canceled");
		}
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/drps/cancel").toUriString());
		return ResponseEntity.created(uri).body(drpService.save(l.get()));
	}
	
	@PutMapping("/drps/archive")
	@PreAuthorize("hasAnyAuthority('DRP-ARCHIVE')")
	public ResponseEntity<Boolean>archiveDrp(
			@RequestBody Drp drp,
			HttpServletRequest request){		
		Optional<Drp> l = drpRepository.findById(drp.getId());
		if(!l.isPresent()) {
			throw new NotFoundException("DRP not found");
		}
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/drps/archive").toUriString());
		return ResponseEntity.created(uri).body(drpService.archive(l.get()));
	}
	
	@PutMapping("/drps/archive_all")
	@PreAuthorize("hasAnyAuthority('DRP-ARCHIVE')")
	public ResponseEntity<Boolean>archiveDrps(){			
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/drps/archive_all").toUriString());
		return ResponseEntity.created(uri).body(drpService.archiveAll());
	}
	
	@PostMapping("/drp_details/save")
	@PreAuthorize("hasAnyAuthority('DRP-CREATE','DRP-UPDATE')")
	public ResponseEntity<DrpDetailModel>createDrpDetail(
			@RequestBody DrpDetail drpDetail){
		if(drpDetail.getQty() <= 0) {
			throw new InvalidEntryException("Quantity value should be more than 0");
		}
		Optional<Drp> l = drpRepository.findById(drpDetail.getDrp().getId());
		if(!l.isPresent()) {
			throw new NotFoundException("DRP not found");
		}
		if(l.get().getStatus().equals("BLANK")) {
			l.get().setStatus("PENDING");
			drpRepository.saveAndFlush(l.get());
		}
		if(!(l.get().getStatus().equals("PENDING") || l.get().getStatus().equals("BLANK"))) {
			throw new InvalidOperationException("Editing is not allowed, only PENDING or BLANK DRPs can be edited.");
		}
		Optional<Product> p = productRepository.findById(drpDetail.getProduct().getId());
		if(!p.isPresent()) {
			throw new NotFoundException("Product not found");
		}
		Optional<DrpDetail> d = drpDetailRepository.findByDrpAndProduct(l.get(), p.get());
		DrpDetail detail = new DrpDetail();
		if(d.isPresent()) {
			/**
			 * Update existing detail
			 */
			detail = d.get();
			detail.setQty(drpDetail.getQty());
			detail.setCostPriceVatIncl(drpDetail.getCostPriceVatIncl());
			detail.setCostPriceVatExcl(drpDetail.getCostPriceVatExcl());			
		}else {
			/**
			 * Create new detail
			 */
			detail.setDrp(l.get());
			detail.setProduct(drpDetail.getProduct());
			detail.setQty(drpDetail.getQty());
			detail.setCostPriceVatIncl(drpDetail.getCostPriceVatIncl());
			detail.setCostPriceVatExcl(drpDetail.getCostPriceVatExcl());
		}		
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/drp_details/save").toUriString());
		return ResponseEntity.created(uri).body(drpService.saveDetail(detail));
	}
	
	@GetMapping("/drp_details/get_by_product_id_and_drp_id")
	public ResponseEntity<DrpDetailModel>getDrpDetailByProductAndDrp(
			@RequestParam(name = "product_id") Long productId,
			@RequestParam(name = "drp_id") Long drpId){
		Optional<Drp> l = drpRepository.findById(drpId);
		if(!l.isPresent()) {
			throw new NotFoundException("DRP not found");
		}
		Optional<Product> p = productRepository.findById(drpId);
		if(!p.isPresent()) {
			throw new NotFoundException("Product not found");
		}
		Optional<DrpDetail> d = drpDetailRepository.findByDrpAndProduct(l.get(), p.get());
		if(!d.isPresent()) {
			throw new NotFoundException("Detail not found");
		}		
		DrpDetailModel detail = new DrpDetailModel();
		detail.setCostPriceVatIncl(d.get().getCostPriceVatIncl());
		detail.setCostPriceVatExcl(d.get().getCostPriceVatExcl());
		detail.setQty(d.get().getQty());
		
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/drp_details/get_by_product_id_and_drp_id").toUriString());
		return ResponseEntity.created(uri).body(detail);
	}
	
	@GetMapping("/drp_details/get")
	public ResponseEntity<DrpDetailModel>getDetail(
			@RequestParam(name = "id") Long id){		
		Optional<DrpDetail> d = drpDetailRepository.findById(id);
		if(!d.isPresent()) {
			throw new NotFoundException("Detail not found");
		}		
		DrpDetailModel detail = new DrpDetailModel();
		detail.setCostPriceVatIncl(d.get().getCostPriceVatIncl());
		detail.setCostPriceVatExcl(d.get().getCostPriceVatExcl());
		detail.setQty(d.get().getQty());
		
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/drp_details/get").toUriString());
		return ResponseEntity.created(uri).body(detail);
	}
	
	@DeleteMapping("/drp_details/delete")
	@PreAuthorize("hasAnyAuthority('DRP-CREATE','DRP-UPDATE')")
	public ResponseEntity<Boolean> deleteDetail(
			@RequestParam(name = "id") Long id){		
		Optional<DrpDetail> d = drpDetailRepository.findById(id);
		if(!d.isPresent()) {
			throw new NotFoundException("Detail not found");
		}
		Drp drp = d.get().getDrp();
		if(!drp.getStatus().equals("PENDING")) {
			throw new InvalidOperationException("Editing not allowed, only pending DRP can be edited");
		}		
		drpDetailRepository.delete(d.get());		
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/drp_details/delete").toUriString());
		return ResponseEntity.created(uri).body(true);
	}
}

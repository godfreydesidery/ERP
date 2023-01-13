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

import com.orbix.api.domain.Product;
import com.orbix.api.domain.CustomerClaim;
import com.orbix.api.domain.ClaimReplacementProduct;
import com.orbix.api.domain.ClaimedProduct;
import com.orbix.api.domain.Customer;
import com.orbix.api.exceptions.InvalidEntryException;
import com.orbix.api.exceptions.InvalidOperationException;
import com.orbix.api.exceptions.NotFoundException;
import com.orbix.api.models.ClaimReplacementModel;
import com.orbix.api.models.ClaimedProductModel;
import com.orbix.api.models.CustomerClaimModel;
import com.orbix.api.models.RecordModel;
import com.orbix.api.repositories.ProductRepository;
import com.orbix.api.repositories.ClaimReplacementProductRepository;
import com.orbix.api.repositories.ClaimedProductRepository;
import com.orbix.api.repositories.CustomerClaimRepository;
import com.orbix.api.repositories.CustomerRepository;
import com.orbix.api.service.DayService;
import com.orbix.api.service.CustomerClaimService;
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
public class CustomerClaimResource {
	private final UserService userService;
	private final DayService dayService;	
	private final CustomerClaimRepository customerClaimRepository;
	private final ClaimedProductRepository claimedProductRepository;
	private final ClaimReplacementProductRepository claimReplacementProductRepository;
	private final ProductRepository productRepository;
	private final CustomerClaimService customerClaimService;
	
	private final CustomerRepository customerRepository;
	
	@GetMapping("/customer_claims")
	public ResponseEntity<List<CustomerClaimModel>>getCustomerClaims(){
		return ResponseEntity.ok().body(customerClaimService.getAllVisible());
	}
	
	@GetMapping("/customer_claims/request_no")
	public ResponseEntity<RecordModel> requestNo(){
		return ResponseEntity.ok().body(customerClaimService.requestCCLNo());
	}
	
	@GetMapping("/customer_claims/get")
	public ResponseEntity<CustomerClaimModel> getCustomerClaim(
			@RequestParam(name = "id") Long id){
		return ResponseEntity.ok().body(customerClaimService.get(id));
	}
	
	@GetMapping("/customer_claims/get_by_no")
	public ResponseEntity<CustomerClaimModel> getCustomerClaimByNo(
			@RequestParam(name = "no") String no){
		return ResponseEntity.ok().body(customerClaimService.getByNo(no));
	}
	
	@GetMapping("/claimed_products/get_by_customer_claim")
	public ResponseEntity<List<ClaimedProductModel>>getClaimedProducts(
			@RequestParam(name = "id") Long id){		
		return ResponseEntity.ok().body(customerClaimService.getAllClaimedProducts(customerClaimRepository.findById(id).get()));
	}
	
	@GetMapping("/claim_replacement_products/get_by_customer_claim")
	public ResponseEntity<List<ClaimReplacementModel>>getClaimReplacements(
			@RequestParam(name = "id") Long id){		
		return ResponseEntity.ok().body(customerClaimService.getAllReplacementProducts(customerClaimRepository.findById(id).get()));
	}
	
	@PostMapping("/customer_claims/create")
	//@PreAuthorize("hasAnyAuthority('Claim-CREATE')")
	public ResponseEntity<CustomerClaimModel>createCustomerClaim(
			@RequestBody CustomerClaim customerClaim,
			HttpServletRequest request){		
		CustomerClaim ptp = new CustomerClaim();
		Optional<Customer> cus = customerRepository.findById(customerClaim.getCustomer().getId());
		if(!cus.isPresent()) {
			throw new NotFoundException("Customer not found");
		}
		ptp.setNo("NA");
		ptp.setStatus("PENDING");
		ptp.setCustomer(cus.get());
		ptp.setComments(customerClaim.getComments());	
		ptp.setCreatedBy(userService.getUserId(request));
		ptp.setCreatedAt(dayService.getDayId());
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/customer_claims/create").toUriString());
		return ResponseEntity.created(uri).body(customerClaimService.save(ptp));
	}
	
	@PutMapping("/customer_claims/update")
	//@PreAuthorize("hasAnyAuthority('Claim-UPDATE')")
	public ResponseEntity<CustomerClaimModel>updateProductToMaterial(
			@RequestBody CustomerClaim customerClaim,
			HttpServletRequest request){
		Optional<CustomerClaim> l = customerClaimRepository.findById(customerClaim.getId());
		if(!l.isPresent()) {
			throw new NotFoundException("Claim not found");
		}
		if(!l.get().getStatus().equals("PENDING")) {
			throw new InvalidOperationException("Editing not allowed, only Pending Claim can be edited");
		}
		Optional<Customer> cus = customerRepository.findById(customerClaim.getCustomer().getId());
		if(!cus.isPresent()) {
			throw new NotFoundException("Customer not found");
		}
		//List<ProductToMaterialDetail> dzz = productToMaterialDetailRepository.findByProductToMaterial(l.get());
		l.get().setComments(customerClaim.getComments());
		l.get().setCustomer(cus.get());
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/customer_claims/update").toUriString());
		return ResponseEntity.created(uri).body(customerClaimService.save(l.get()));
	}
	
	@PutMapping("/customer_claims/approve")
	//@PreAuthorize("hasAnyAuthority('Claim-APPROVE')")
	public ResponseEntity<CustomerClaimModel>approveProductToMaterial(
			@RequestBody CustomerClaim customerClaim,
			HttpServletRequest request){		
		Optional<CustomerClaim> l = customerClaimRepository.findById(customerClaim.getId());
		if(!l.isPresent()) {
			throw new NotFoundException("Claim not found");
		}
		if(l.get().getStatus().equals("PENDING")) {
			l.get().setApprovedBy(userService.getUserId(request));
			l.get().setApprovedAt(dayService.getDayId());
			l.get().setStatus("APPROVED");
		}else {
			throw new InvalidOperationException("Could not approve, not a PENDING Claim");
		}
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/customer_claims/approve").toUriString());
		return ResponseEntity.created(uri).body(customerClaimService.approve(l.get()));
	}
	
	@PutMapping("/customer_claims/cancel")
	//@PreAuthorize("hasAnyAuthority('Claim-CANCEL')")
	public ResponseEntity<CustomerClaimModel>cancelCustomerClaim(
			@RequestBody CustomerClaim customerClaim,
			HttpServletRequest request){		
		Optional<CustomerClaim> l = customerClaimRepository.findById(customerClaim.getId());
		if(!l.isPresent()) {
			throw new NotFoundException("Claim not found");
		}
		if(l.get().getStatus().equals("PENDING") || l.get().getStatus().equals("BLANK")) {
			l.get().setStatus("CANCELED");
		}else {
			throw new InvalidOperationException("Could not cancel, only Pending Claims can be canceled");
		}
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/customer_claims/cancel").toUriString());
		return ResponseEntity.created(uri).body(customerClaimService.save(l.get()));
	}
	
	@PutMapping("/customer_claims/archive")
	//@PreAuthorize("hasAnyAuthority('Claim-CREATE','Claim-UPDATE','Claim-ARCHIVE')")
	public ResponseEntity<Boolean>archiveCustomerClaim(
			@RequestBody CustomerClaim customerClaim,
			HttpServletRequest request){		
		Optional<CustomerClaim> l = customerClaimRepository.findById(customerClaim.getId());
		if(!l.isPresent()) {
			throw new NotFoundException("Claim not found");
		}
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/customer_claims/archive").toUriString());
		return ResponseEntity.created(uri).body(customerClaimService.archive(l.get()));
	}
	
	@PutMapping("/customer_claims/archive_all")
	//@PreAuthorize("hasAnyAuthority('Claim-CREATE','Claim-UPDATE','Claim-ARCHIVE')")
	public ResponseEntity<Boolean>archiveCustomerClaims(){			
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/customer_claims/archive_all").toUriString());
		return ResponseEntity.created(uri).body(customerClaimService.archiveAll());
	}
	
	@PostMapping("/claimed_products/save")
	//@PreAuthorize("hasAnyAuthority('Claim-CREATE','Claim-UPDATE')")
	public ResponseEntity<ClaimedProductModel>createClaimedProduct(
			@RequestBody ClaimedProduct claimedProduct){
		
		if(claimedProduct.getQty() <= 0) {
			throw new InvalidEntryException("Quantity value should be more than 0");
		}
		Optional<CustomerClaim> l = customerClaimRepository.findById(claimedProduct.getCustomerClaim().getId());
		if(!l.isPresent()) {
			throw new NotFoundException("Claim not found");
		}
		if(l.get().getStatus().equals("BLANK")) {
			l.get().setStatus("PENDING");
			customerClaimRepository.saveAndFlush(l.get());
		}
		if(!(l.get().getStatus().equals("PENDING") || l.get().getStatus().equals("BLANK"))) {
			throw new InvalidOperationException("Editing is not allowed, only PENDING or BLANK Claims can be edited.");
		}
		Optional<Product> p = productRepository.findById(claimedProduct.getProduct().getId());
		if(!p.isPresent()) {
			throw new NotFoundException("Product not found");
		}
		
		//Optional<ClaimedProduct> d = claimReplacementProductRepository.findByProductAndCustomerClaim(p.get(), l.get());
		if(claimedProduct.getId() == null) {
			
		}
		//Optional<ClaimedProduct> d = claimedProductRepository.findById(claimedProduct.getId());
		
		ClaimedProduct detail = new ClaimedProduct();
		//if(d.isPresent()) {
		if(claimedProduct.getId() != null) {
			Optional<ClaimedProduct> d = claimedProductRepository.findById(claimedProduct.getId());
			/**
			 * Update existing detail
			 */
			detail = d.get();			
			detail.setQty(claimedProduct.getQty());
			detail.setReason(claimedProduct.getReason());
			detail.setRemarks(claimedProduct.getRemarks());
		}else {
			/**
			 * Create new detail
			 */
			//Optional<ClaimedProduct> q = claimReplacementProductRepository.findByProductAndCustomerClaim(p.get(), l.get());
			//if(q.isPresent()) {
				//throw new InvalidOperationException("Product exist in list. Consider editing quantity"); delete this snipet
			//}
			
			detail.setCustomerClaim(l.get());
			detail.setProduct(claimedProduct.getProduct());
			detail.setQty(claimedProduct.getQty());
			detail.setReason(claimedProduct.getReason());
			detail.setRemarks(claimedProduct.getRemarks());
		}		
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/claimed_products/save").toUriString());
		return ResponseEntity.created(uri).body(customerClaimService.saveClaimedProduct(detail));
	}
	
	
	@PostMapping("/claim_replacement_products/save")
	//@PreAuthorize("hasAnyAuthority('Claim-CREATE','Claim-UPDATE')")
	public ResponseEntity<ClaimReplacementModel>createClaimReplacement(
			@RequestBody ClaimReplacementProduct claimReplacementProduct){
		if(claimReplacementProduct.getQty() <= 0) {
			throw new InvalidEntryException("Quantity value should be more than 0");
		}
		Optional<CustomerClaim> l = customerClaimRepository.findById(claimReplacementProduct.getCustomerClaim().getId());
		if(!l.isPresent()) {
			throw new NotFoundException("Claim not found");
		}
		if(l.get().getStatus().equals("BLANK")) {
			l.get().setStatus("PENDING");
			customerClaimRepository.saveAndFlush(l.get());
		}
		if(!(l.get().getStatus().equals("PENDING") || l.get().getStatus().equals("BLANK"))) {
			throw new InvalidOperationException("Editing is not allowed, only PENDING or BLANK Claims can be edited.");
		}
		Optional<Product> p = productRepository.findById(claimReplacementProduct.getProduct().getId());
		if(!p.isPresent()) {
			throw new NotFoundException("Product not found");
		}
		
		ClaimReplacementProduct detail = new ClaimReplacementProduct();
		if(claimReplacementProduct.getId() != null) {
			Optional<ClaimReplacementProduct> d = claimReplacementProductRepository.findById(claimReplacementProduct.getId());
			/**
			 * Update existing detail
			 */
			detail = d.get();			
			detail.setQty(claimReplacementProduct.getQty());
			detail.setRemarks(claimReplacementProduct.getRemarks());
		}else {
			/**
			 * Create new detail
			 */
			Optional<ClaimReplacementProduct> q = claimReplacementProductRepository.findByProductAndCustomerClaim(p.get(), l.get());
			if(q.isPresent()) {
				throw new InvalidOperationException("Product exist in list. Consider editing quantity");
			}
			detail.setCustomerClaim(l.get());
			detail.setProduct(claimReplacementProduct.getProduct());
			detail.setQty(claimReplacementProduct.getQty());
			detail.setRemarks(claimReplacementProduct.getRemarks());
		}		
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/claim_replacement_products/save").toUriString());
		return ResponseEntity.created(uri).body(customerClaimService.saveReplacement(detail));
	}
	
	@GetMapping("/claimed_products/get")
	//@PreAuthorize("hasAnyAuthority('Claim-CREATE','Claim-UPDATE')")
	public ResponseEntity<ClaimedProductModel>getInitial(
			@RequestParam(name = "id") Long id){		
		Optional<ClaimedProduct> d = claimedProductRepository.findById(id);
		if(!d.isPresent()) {
			throw new NotFoundException("Detail not found");
		}		
		ClaimedProductModel detail = new ClaimedProductModel();
		detail.setId(d.get().getId());
		detail.setProduct(d.get().getProduct());
		detail.setQty(d.get().getQty());
		detail.setReason(d.get().getReason());
		detail.setRemarks(d.get().getRemarks());
		
		
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/claimed_products/get").toUriString());
		return ResponseEntity.created(uri).body(detail);
	}
	
	@GetMapping("/claim_replacement_products/get")
	//@PreAuthorize("hasAnyAuthority('Claim-CREATE','Claim-UPDATE')")
	public ResponseEntity<ClaimReplacementModel>getFinal(
			@RequestParam(name = "id") Long id){		
		Optional<ClaimReplacementProduct> d = claimReplacementProductRepository.findById(id);
		if(!d.isPresent()) {
			throw new NotFoundException("Detail not found");
		}		
		ClaimReplacementModel detail = new ClaimReplacementModel();
		detail.setId(d.get().getId());
		detail.setProduct(d.get().getProduct());
		detail.setQty(d.get().getQty());
		detail.setRemarks(d.get().getRemarks());
				
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/claim_replacement_products/get").toUriString());
		return ResponseEntity.created(uri).body(detail);
	}
	
	@DeleteMapping("/claimed_products/delete")
	//@PreAuthorize("hasAnyAuthority('Claim-CREATE','Claim-UPDATE')")
	public ResponseEntity<Boolean> deleteInitial(
			@RequestParam(name = "id") Long id){		
		Optional<ClaimedProduct> d = claimedProductRepository.findById(id);
		if(!d.isPresent()) {
			throw new NotFoundException("Detail not found");
		}
		CustomerClaim customerClaim = d.get().getCustomerClaim();
		if(!customerClaim.getStatus().equals("PENDING")) {
			throw new InvalidOperationException("Editing not allowed, only pending Claim can be edited");
		}		
		claimedProductRepository.delete(d.get());		
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/claimed_products/delete").toUriString());
		return ResponseEntity.created(uri).body(true);
	}
	
	@DeleteMapping("/claim_replacement_products/delete")
	//@PreAuthorize("hasAnyAuthority('Claim-CREATE','Claim-UPDATE')")
	public ResponseEntity<Boolean> deleteFinal(
			@RequestParam(name = "id") Long id){		
		Optional<ClaimReplacementProduct> d = claimReplacementProductRepository.findById(id);
		if(!d.isPresent()) {
			throw new NotFoundException("Detail not found");
		}
		CustomerClaim customerClaim = d.get().getCustomerClaim();
		if(!customerClaim.getStatus().equals("PENDING")) {
			throw new InvalidOperationException("Editing not allowed, only pending Claim can be edited");
		}		
		claimReplacementProductRepository.delete(d.get());		
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/claim_replacement_products/delete").toUriString());
		return ResponseEntity.created(uri).body(true);
	}
}

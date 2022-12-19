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
import com.orbix.api.domain.ProductToProduct;
import com.orbix.api.domain.ProductToProductFinal;
import com.orbix.api.domain.ProductToProductInitial;
import com.orbix.api.exceptions.InvalidEntryException;
import com.orbix.api.exceptions.InvalidOperationException;
import com.orbix.api.exceptions.NotFoundException;
import com.orbix.api.models.ProductToProductFinalModel;
import com.orbix.api.models.ProductToProductInitialModel;
import com.orbix.api.models.ProductToProductModel;
import com.orbix.api.repositories.ProductRepository;
import com.orbix.api.repositories.ProductToProductFinalRepository;
import com.orbix.api.repositories.ProductToProductInitialRepository;
import com.orbix.api.repositories.ProductToProductRepository;
import com.orbix.api.service.DayService;
import com.orbix.api.service.ProductToProductService;
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
public class ProductToProductResource {
	private final UserService userService;
	private final DayService dayService;	
	private final ProductToProductRepository productToProductRepository;
	private final ProductToProductInitialRepository productToProductInitialRepository;
	private final ProductToProductFinalRepository productToProductFinalRepository;
	private final ProductRepository productRepository;
	private final ProductToProductService productToProductService;
	
	@GetMapping("/product_to_products")
	public ResponseEntity<List<ProductToProductModel>>getProductToProducts(){
		return ResponseEntity.ok().body(productToProductService.getAllVisible());
	}
	
	@GetMapping("/product_to_products/get")
	public ResponseEntity<ProductToProductModel> getProductToProduct(
			@RequestParam(name = "id") Long id){
		return ResponseEntity.ok().body(productToProductService.get(id));
	}
	
	@GetMapping("/product_to_products/get_by_no")
	public ResponseEntity<ProductToProductModel> getProductToProductByNo(
			@RequestParam(name = "no") String no){
		return ResponseEntity.ok().body(productToProductService.getByNo(no));
	}
	
	@GetMapping("/product_to_product_initials/get_by_product_to_product")
	public ResponseEntity<List<ProductToProductInitialModel>>getProductToProductInitials(
			@RequestParam(name = "id") Long id){		
		return ResponseEntity.ok().body(productToProductService.getAllInitials(productToProductRepository.findById(id).get()));
	}
	
	@GetMapping("/product_to_product_finals/get_by_product_to_product")
	public ResponseEntity<List<ProductToProductFinalModel>>getProductToProductFinals(
			@RequestParam(name = "id") Long id){		
		return ResponseEntity.ok().body(productToProductService.getAllFinals(productToProductRepository.findById(id).get()));
	}
	
	@PostMapping("/product_to_products/create")
	//@PreAuthorize("hasAnyAuthority('CONVERSION-CREATE')")
	public ResponseEntity<ProductToProductModel>createProductToProduct(
			@RequestBody ProductToProduct productToProduct,
			HttpServletRequest request){		
		ProductToProduct ptp = new ProductToProduct();
		ptp.setNo("NA");
		ptp.setStatus("PENDING");
		ptp.setReason(productToProduct.getReason());
		ptp.setComments(productToProduct.getComments());	
		ptp.setCreatedBy(userService.getUserId(request));
		ptp.setCreatedAt(dayService.getDayId());
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/product_to_products/create").toUriString());
		return ResponseEntity.created(uri).body(productToProductService.save(ptp));
	}
	
	@PutMapping("/product_to_products/update")
	//@PreAuthorize("hasAnyAuthority('CONVERSION-UPDATE')")
	public ResponseEntity<ProductToProductModel>updateProductToMaterial(
			@RequestBody ProductToProduct productToProduct,
			HttpServletRequest request){
		Optional<ProductToProduct> l = productToProductRepository.findById(productToProduct.getId());
		if(!l.isPresent()) {
			throw new NotFoundException("CONVERSION not found");
		}
		if(!l.get().getStatus().equals("PENDING")) {
			throw new InvalidOperationException("Editing not allowed, only Pending Conversions can be edited");
		}
		//List<ProductToMaterialDetail> dzz = productToMaterialDetailRepository.findByProductToMaterial(l.get());
		l.get().setReason(productToProduct.getReason());
		l.get().setComments(productToProduct.getComments());
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/product_to_products/update").toUriString());
		return ResponseEntity.created(uri).body(productToProductService.save(l.get()));
	}
	
	@PutMapping("/product_to_products/approve")
	//@PreAuthorize("hasAnyAuthority('CONVERSION-APPROVE')")
	public ResponseEntity<ProductToProductModel>approveProductToMaterial(
			@RequestBody ProductToProduct productToProduct,
			HttpServletRequest request){		
		Optional<ProductToProduct> l = productToProductRepository.findById(productToProduct.getId());
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
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/product_to_products/approve").toUriString());
		return ResponseEntity.created(uri).body(productToProductService.post(l.get()));
	}
	
	@PutMapping("/product_to_products/cancel")
	//@PreAuthorize("hasAnyAuthority('CONVERSION-CANCEL')")
	public ResponseEntity<ProductToProductModel>cancelProductToProduct(
			@RequestBody ProductToProduct productToProduct,
			HttpServletRequest request){		
		Optional<ProductToProduct> l = productToProductRepository.findById(productToProduct.getId());
		if(!l.isPresent()) {
			throw new NotFoundException("CONVERSION not found");
		}
		if(l.get().getStatus().equals("PENDING") || l.get().getStatus().equals("BLANK")) {
			l.get().setStatus("CANCELED");
		}else {
			throw new InvalidOperationException("Could not cancel, only Pending CONVERSIONs can be canceled");
		}
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/product_to_products/cancel").toUriString());
		return ResponseEntity.created(uri).body(productToProductService.save(l.get()));
	}
	
	@PutMapping("/product_to_products/archive")
	//@PreAuthorize("hasAnyAuthority('CONVERSION-CREATE','CONVERSION-UPDATE','CONVERSION-ARCHIVE')")
	public ResponseEntity<Boolean>archiveProductToProduct(
			@RequestBody ProductToProduct productToProduct,
			HttpServletRequest request){		
		Optional<ProductToProduct> l = productToProductRepository.findById(productToProduct.getId());
		if(!l.isPresent()) {
			throw new NotFoundException("CONVERSION not found");
		}
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/product_to_products/archive").toUriString());
		return ResponseEntity.created(uri).body(productToProductService.archive(l.get()));
	}
	
	@PutMapping("/product_to_products/archive_all")
	//@PreAuthorize("hasAnyAuthority('CONVERSION-CREATE','CONVERSION-UPDATE','CONVERSION-ARCHIVE')")
	public ResponseEntity<Boolean>archiveProductToProducts(){			
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/product_to_products/archive_all").toUriString());
		return ResponseEntity.created(uri).body(productToProductService.archiveAll());
	}
	
	@PostMapping("/product_to_product_initials/save")
	//@PreAuthorize("hasAnyAuthority('CONVERSION-CREATE','CONVERSION-UPDATE')")
	public ResponseEntity<ProductToProductInitialModel>createProductToProductInitial(
			@RequestBody ProductToProductInitial productToProductInitial){
		if(productToProductInitial.getQty() <= 0) {
			throw new InvalidEntryException("Quantity value should be more than 0");
		}
		Optional<ProductToProduct> l = productToProductRepository.findById(productToProductInitial.getProductToProduct().getId());
		if(!l.isPresent()) {
			throw new NotFoundException("CONVERSION not found");
		}
		if(l.get().getStatus().equals("BLANK")) {
			l.get().setStatus("PENDING");
			productToProductRepository.saveAndFlush(l.get());
		}
		if(!(l.get().getStatus().equals("PENDING") || l.get().getStatus().equals("BLANK"))) {
			throw new InvalidOperationException("Editing is not allowed, only PENDING or BLANK CONVERSIONs can be edited.");
		}
		Optional<Product> p = productRepository.findById(productToProductInitial.getProduct().getId());
		if(!p.isPresent()) {
			throw new NotFoundException("Product not found");
		}
		
		//Optional<ProductToProductInitial> d = productToProductInitialRepository.findByProductAndProductToProduct(p.get(), l.get());
		Optional<ProductToProductInitial> d = productToProductInitialRepository.findById(productToProductInitial.getId());
		
		ProductToProductInitial detail = new ProductToProductInitial();
		if(d.isPresent()) {
			/**
			 * Update existing detail
			 */
			detail = d.get();			
			detail.setQty(productToProductInitial.getQty());
		}else {
			/**
			 * Create new detail
			 */
			Optional<ProductToProductInitial> q = productToProductInitialRepository.findByProductAndProductToProduct(p.get(), l.get());
			if(q.isPresent()) {
				throw new InvalidOperationException("Product exist in list. Consider editing quantity");
			}
			
			detail.setProductToProduct(l.get());
			detail.setProduct(productToProductInitial.getProduct());
			detail.setQty(productToProductInitial.getQty());
		}		
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/product_to_product_initials/save").toUriString());
		return ResponseEntity.created(uri).body(productToProductService.saveInitial(detail));
	}
	
	
	@PostMapping("/product_to_product_finals/save")
	//@PreAuthorize("hasAnyAuthority('CONVERSION-CREATE','CONVERSION-UPDATE')")
	public ResponseEntity<ProductToProductFinalModel>createProductToProductFinal(
			@RequestBody ProductToProductFinal productToProductFinal){
		if(productToProductFinal.getQty() <= 0) {
			throw new InvalidEntryException("Quantity value should be more than 0");
		}
		Optional<ProductToProduct> l = productToProductRepository.findById(productToProductFinal.getProductToProduct().getId());
		if(!l.isPresent()) {
			throw new NotFoundException("CONVERSION not found");
		}
		if(l.get().getStatus().equals("BLANK")) {
			l.get().setStatus("PENDING");
			productToProductRepository.saveAndFlush(l.get());
		}
		if(!(l.get().getStatus().equals("PENDING") || l.get().getStatus().equals("BLANK"))) {
			throw new InvalidOperationException("Editing is not allowed, only PENDING or BLANK CONVERSIONs can be edited.");
		}
		Optional<Product> p = productRepository.findById(productToProductFinal.getProduct().getId());
		if(!p.isPresent()) {
			throw new NotFoundException("Product not found");
		}
		
		//Optional<ProductToProductFinal> d = productToProductFinalRepository.findByProductAndProductToProduct(p.get(), l.get());
		Optional<ProductToProductFinal> d = productToProductFinalRepository.findById(productToProductFinal.getId());
		
		ProductToProductFinal detail = new ProductToProductFinal();
		if(d.isPresent()) {
			/**
			 * Update existing detail
			 */
			detail = d.get();			
			detail.setQty(productToProductFinal.getQty());
		}else {
			/**
			 * Create new detail
			 */
			Optional<ProductToProductFinal> q = productToProductFinalRepository.findByProductAndProductToProduct(p.get(), l.get());
			if(q.isPresent()) {
				throw new InvalidOperationException("Product exist in list. Consider editing quantity");
			}
			detail.setProductToProduct(l.get());
			detail.setProduct(productToProductFinal.getProduct());
			detail.setQty(productToProductFinal.getQty());
		}		
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/product_to_product_initials/save").toUriString());
		return ResponseEntity.created(uri).body(productToProductService.saveFinal(detail));
	}
	
	@GetMapping("/product_to_product_initials/get")
	//@PreAuthorize("hasAnyAuthority('CONVERSION-CREATE','CONVERSION-UPDATE')")
	public ResponseEntity<ProductToProductInitialModel>getInitial(
			@RequestParam(name = "id") Long id){		
		Optional<ProductToProductInitial> d = productToProductInitialRepository.findById(id);
		if(!d.isPresent()) {
			throw new NotFoundException("Detail not found");
		}		
		ProductToProductInitialModel detail = new ProductToProductInitialModel();
		detail.setId(d.get().getId());
		detail.setProduct(d.get().getProduct());
		detail.setQty(d.get().getQty());
		detail.setCostPriceVatExcl(d.get().getCostPriceVatExcl());
		detail.setCostPriceVatIncl(d.get().getCostPriceVatIncl());
		detail.setSellingPriceVatExcl(d.get().getSellingPriceVatExcl());
		detail.setSellingPriceVatIncl(d.get().getSellingPriceVatIncl());
		
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/product_to_product_initials/get").toUriString());
		return ResponseEntity.created(uri).body(detail);
	}
	
	@GetMapping("/product_to_product_finals/get")
	//@PreAuthorize("hasAnyAuthority('CONVERSION-CREATE','CONVERSION-UPDATE')")
	public ResponseEntity<ProductToProductFinalModel>getFinal(
			@RequestParam(name = "id") Long id){		
		Optional<ProductToProductFinal> d = productToProductFinalRepository.findById(id);
		if(!d.isPresent()) {
			throw new NotFoundException("Detail not found");
		}		
		ProductToProductFinalModel detail = new ProductToProductFinalModel();
		detail.setId(d.get().getId());
		detail.setProduct(d.get().getProduct());
		detail.setQty(d.get().getQty());
		detail.setCostPriceVatExcl(d.get().getCostPriceVatExcl());
		detail.setCostPriceVatIncl(d.get().getCostPriceVatIncl());
		detail.setSellingPriceVatExcl(d.get().getSellingPriceVatExcl());
		detail.setSellingPriceVatIncl(d.get().getSellingPriceVatIncl());
		
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/product_to_product_finals/get").toUriString());
		return ResponseEntity.created(uri).body(detail);
	}
	
	@DeleteMapping("/product_to_product_initials/delete")
	//@PreAuthorize("hasAnyAuthority('CONVERSION-CREATE','CONVERSION-UPDATE')")
	public ResponseEntity<Boolean> deleteInitial(
			@RequestParam(name = "id") Long id){		
		Optional<ProductToProductInitial> d = productToProductInitialRepository.findById(id);
		if(!d.isPresent()) {
			throw new NotFoundException("Detail not found");
		}
		ProductToProduct productToProduct = d.get().getProductToProduct();
		if(!productToProduct.getStatus().equals("PENDING")) {
			throw new InvalidOperationException("Editing not allowed, only pending CONVERSION can be edited");
		}		
		productToProductInitialRepository.delete(d.get());		
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/product_to_product_initials/delete").toUriString());
		return ResponseEntity.created(uri).body(true);
	}
	
	@DeleteMapping("/product_to_product_finals/delete")
	//@PreAuthorize("hasAnyAuthority('CONVERSION-CREATE','CONVERSION-UPDATE')")
	public ResponseEntity<Boolean> deleteFinal(
			@RequestParam(name = "id") Long id){		
		Optional<ProductToProductFinal> d = productToProductFinalRepository.findById(id);
		if(!d.isPresent()) {
			throw new NotFoundException("Detail not found");
		}
		ProductToProduct productToProduct = d.get().getProductToProduct();
		if(!productToProduct.getStatus().equals("PENDING")) {
			throw new InvalidOperationException("Editing not allowed, only pending CONVERSION can be edited");
		}		
		productToProductFinalRepository.delete(d.get());		
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/product_to_product_finals/delete").toUriString());
		return ResponseEntity.created(uri).body(true);
	}
}

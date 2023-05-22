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

import com.orbix.api.domain.Product;
import com.orbix.api.domain.WebPos;
import com.orbix.api.domain.WebPosApproveData;
import com.orbix.api.exceptions.InvalidEntryException;
import com.orbix.api.exceptions.NotFoundException;
import com.orbix.api.models.WebPosModel;
import com.orbix.api.repositories.ProductRepository;
import com.orbix.api.repositories.WebPosRepository;
import com.orbix.api.service.DayService;
import com.orbix.api.service.UserService;
import com.orbix.api.service.WebPosService;

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
public class WebPosResource {
	private final 	UserService userService;
	private final 	DayService dayService;
	private final 	WebPosService webPosService;
	private final 	WebPosRepository webPosRepository;
	private final 	ProductRepository productRepository;
	
	
	
	@GetMapping("/web_poses")
	public ResponseEntity<List<WebPosModel>>getWebPoses(
			@RequestParam(name = "id") Long id){		
		return ResponseEntity.ok().body(webPosService.getAll());
	}
	
	
	@PutMapping("/web_pos/approve")
	@PreAuthorize("hasAnyAuthority('WEB_POS-APPROVE')")
	public ResponseEntity<Boolean>approve(
			@RequestBody WebPosApproveData data,
			HttpServletRequest request){				
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/web_pos/approve").toUriString());
		return ResponseEntity.created(uri).body(webPosService.approve(data, request));
	}
	
	
	@PostMapping("/web_pos/save")
	@PreAuthorize("hasAnyAuthority('WEB_POS-CREATE','WEB_POS-UPDATE')")
	public ResponseEntity<WebPosModel>createWebPos(
			@RequestBody WebPos webPos,
			HttpServletRequest request){
		if(webPos.getQty() <= 0) {
			throw new InvalidEntryException("Quantity value should be more than 0");
		}
		
		Optional<Product> p = productRepository.findById(webPos.getProduct().getId());
		if(!p.isPresent()) {
			throw new NotFoundException("Product not found");
		}
		WebPos detail = new WebPos();
		
		
			/**
			 * Create new detail
			 */
			detail.setProduct(webPos.getProduct());
			detail.setQty(webPos.getQty());
			detail.setCostPriceVatIncl(webPos.getCostPriceVatIncl());
			detail.setCostPriceVatExcl(webPos.getCostPriceVatExcl());	
			detail.setSellingPriceVatIncl(webPos.getSellingPriceVatIncl());
			detail.setSellingPriceVatExcl(Math.round((webPos.getSellingPriceVatIncl() * 100) / (100 + p.get().getVat()) * 100.0) / 100.0);
			detail.setCreatedBy(userService.getUserId(request));
			detail.setCreatedAt(dayService.getDayId());
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/web_pos/save").toUriString());
		return ResponseEntity.created(uri).body(webPosService.save(detail));
	}
	
	@GetMapping("/web_pos/get")
	public ResponseEntity<WebPosModel>get(
			@RequestParam(name = "id") Long id){		
		Optional<WebPos> d = webPosRepository.findById(id);
		if(!d.isPresent()) {
			throw new NotFoundException("Detail not found");
		}		
		WebPosModel detail = new WebPosModel();
		detail.setCostPriceVatIncl(d.get().getCostPriceVatIncl());
		detail.setCostPriceVatExcl(d.get().getCostPriceVatExcl());	
		detail.setSellingPriceVatIncl(d.get().getSellingPriceVatIncl());
		detail.setSellingPriceVatExcl(d.get().getSellingPriceVatExcl());
		detail.setQty(d.get().getQty());		
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/web_pos/get").toUriString());
		return ResponseEntity.created(uri).body(detail);
	}
	
	@DeleteMapping("/web_pos/delete")
	@PreAuthorize("hasAnyAuthority('WEB_POS-CREATE','WEB_POS-UPDATE')")
	public ResponseEntity<Boolean> delete(
			@RequestParam(name = "id") Long id){		
		Optional<WebPos> d = webPosRepository.findById(id);
		if(!d.isPresent()) {
			throw new NotFoundException("Detail not found");
		}
				
		webPosRepository.delete(d.get());		
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/web_pos/delete").toUriString());
		return ResponseEntity.created(uri).body(true);
	}
}

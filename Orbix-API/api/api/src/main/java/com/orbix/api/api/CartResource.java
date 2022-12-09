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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.orbix.api.domain.Cart;
import com.orbix.api.domain.CartDetail;
import com.orbix.api.domain.CartHeld;
import com.orbix.api.domain.Payment;
import com.orbix.api.domain.Receipt;
import com.orbix.api.domain.Till;
import com.orbix.api.exceptions.NotFoundException;
import com.orbix.api.repositories.CartDetailRepository;
import com.orbix.api.repositories.CartRepository;
import com.orbix.api.repositories.ReceiptRepository;
import com.orbix.api.repositories.TillRepository;
import com.orbix.api.service.CartService;
import lombok.RequiredArgsConstructor;

/**
 * @author GODFREY
 *
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class CartResource {
	private final TillRepository tillRepository;
	private final CartRepository cartRepository;
	private final ReceiptRepository receiptRepository;
	private final CartDetailRepository cartDetailRepository;
	private final 	CartService cartService;
	
	@GetMapping("/carts/load")
	public ResponseEntity<Cart> loadCart(
			@RequestParam(name = "till_no") String no){
		Optional<Till> t = tillRepository.findByNo(no);
		if(!t.isPresent()) {
			throw new NotFoundException("Till not found");
		}
		return ResponseEntity.ok().body(cartService.loadCart(t.get()));
	}
	
	@GetMapping("/carts/create")
	public ResponseEntity<Cart> createCart(
			@RequestParam(name = "till_no") String no){
		Optional<Till> t = tillRepository.findByNo(no);
		if(!t.isPresent()) {
			throw new NotFoundException("Till not found");
		}
		
		return ResponseEntity.ok().body(cartService.createCart(t.get()));
	}
	
	@GetMapping("/carts/hold")
	public ResponseEntity<Cart> holdCart(
			@RequestParam(name = "till_no") String no){
		Optional<Till> t = tillRepository.findByNo(no);
		if(!t.isPresent()) {
			throw new NotFoundException("Till not found");
		}
		
		return ResponseEntity.ok().body(cartService.holdCart(t.get()));
	}
	
	@GetMapping("/carts/unhold")
	public ResponseEntity<Cart> unholdCart(
			@RequestParam(name = "till_no") String no,
			@RequestParam(name = "id") Long id){
		Optional<Till> t = tillRepository.findByNo(no);
		if(!t.isPresent()) {
			throw new NotFoundException("Till not found");
		}
		
		return ResponseEntity.ok().body(cartService.unholdCartHeld(t.get(), id));
	}
	
	@GetMapping("/carts/show_held")
	public ResponseEntity<List<CartHeld>> showCartsHeld(
			@RequestParam(name = "till_no") String no){
		Optional<Till> t = tillRepository.findByNo(no);
		if(!t.isPresent()) {
			throw new NotFoundException("Till not found");
		}
		
		return ResponseEntity.ok().body(cartService.showCartsHeld(t.get()));
	}
	
	@PostMapping("/carts/add_detail")
	public ResponseEntity<Boolean>addDetail(
			@RequestBody CartDetail cartDetail){		
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/carts/add_detail").toUriString());
		return ResponseEntity.created(uri).body(cartService.addCartDetail(cartDetail));
	}
	
	@PostMapping("/carts/update_qty")
	public ResponseEntity<Boolean>updateQty(
			@RequestBody CartDetail cartDetail){		
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/carts/update_qty").toUriString());
		return ResponseEntity.created(uri).body(cartService.updateQty(cartDetail));
	}
	
	@PostMapping("/carts/update_discount")
	public ResponseEntity<Boolean>updateDiscount(
			@RequestBody CartDetail cartDetail){		
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/carts/update_discount").toUriString());
		return ResponseEntity.created(uri).body(cartService.updateDiscount(cartDetail));
	}
	
	@PostMapping("/carts/void")
	public ResponseEntity<Boolean>voidd(
			@RequestBody CartDetail cartDetail,
			HttpServletRequest request){		
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/carts/void").toUriString());
		return ResponseEntity.created(uri).body(cartService.voidd(cartDetail, request));
	}
	
	@PostMapping("/carts/unvoid")
	public ResponseEntity<Boolean>unvoid(
			@RequestBody CartDetail cartDetail){
		Optional<CartDetail> d = cartDetailRepository.findById(cartDetail.getId());
		if(!d.isPresent()) {
			throw new NotFoundException("Detail not found");
		}
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/carts/unvoid").toUriString());
		return ResponseEntity.created(uri).body(cartService.unvoid(d.get()));
	}
	
	@GetMapping("/carts/get_qty")
	public CartDetail getQty(
			@RequestParam(name = "id") Long id){
		CartDetail detail = cartDetailRepository.findById(id).get();
		return detail;
	}
	
	@PostMapping("/carts/pay")
	public ResponseEntity<Receipt>pay(
			@RequestBody Payment payment,
			@RequestParam(name = "till_no") String tillNo,
			@RequestParam(name = "cart_no") String cartNo,
			HttpServletRequest request){
		Optional<Cart> c = cartRepository.findByNo(cartNo);
		if(!c.isPresent()) {
			throw new NotFoundException("Workspace not found in database");
		}		
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/carts/pay").toUriString());
		return ResponseEntity.created(uri).body(cartService.pay(payment, c.get(), request));
	}
	
	@PostMapping("/receipt/get_receipt")
	public ResponseEntity<Receipt>pay(
			@RequestParam(name = "receipt_no") String receiptNo,
			HttpServletRequest request){
		Optional<Receipt> r = receiptRepository.findByNo(receiptNo);
		if(!r.isPresent()) {
			throw new NotFoundException("Receipt not found in database");
		}		
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/receipt/get_receipt").toUriString());
		return ResponseEntity.created(uri).body(r.get());
	}
}

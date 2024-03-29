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

import com.orbix.api.domain.CashPickUp;
import com.orbix.api.domain.Floatt;
import com.orbix.api.domain.PettyCash;
import com.orbix.api.domain.Till;
import com.orbix.api.domain.User;
import com.orbix.api.exceptions.InvalidEntryException;
import com.orbix.api.exceptions.InvalidOperationException;
import com.orbix.api.exceptions.NotFoundException;
import com.orbix.api.repositories.TillRepository;
import com.orbix.api.service.CashPickUpService;
import com.orbix.api.service.FloatService;
import com.orbix.api.service.PettyCashService;
import com.orbix.api.service.TillService;
import lombok.RequiredArgsConstructor;

/**
 * @author GODFREY
 *
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class TillResource {
	
	private final TillRepository tillRepository;
	private final TillService tillService;
	private final CashPickUpService cashPickUpService;
	private final PettyCashService pettyCashService;
	private final FloatService floatService;
	
	@GetMapping("/tills")
	public ResponseEntity<List<Till>>getTills(){
		return ResponseEntity.ok().body(tillService.getTills());
	}
	
	@GetMapping("/tills/get")
	public ResponseEntity<Till> getTill(
			@RequestParam(name = "id") Long id){
		return ResponseEntity.ok().body(tillService.getTill(id));
	}
	
	@GetMapping("/tills/get_by_till_no")
	public ResponseEntity<Till> getTillByTillNo(
			@RequestParam(name = "till_no") String no){
		return ResponseEntity.ok().body(tillService.getTillByNo(no));
	}
	
	@GetMapping("/tills/get_by_computer_name")
	public ResponseEntity<Till> getTillByComputerName(
			@RequestParam(name = "computer_name") String computerName){
		return ResponseEntity.ok().body(tillService.getTillByComputerName(computerName));
	}
	
	@PostMapping("/tills/create")
	@PreAuthorize("hasAnyAuthority('TILL-CREATE')")
	public ResponseEntity<Till>createTill(
			@RequestBody Till till){
		till.setActive(true);
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/tills/create").toUriString());
		return ResponseEntity.created(uri).body(tillService.saveTill(till));
	}
		
	@PutMapping("/tills/update")
	@PreAuthorize("hasAnyAuthority('TILL-CREATE','TILL-UPDATE')")
	public ResponseEntity<Till>updateTill(
			@RequestBody Till till, 
			HttpServletRequest request){
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/tills/update").toUriString());
		return ResponseEntity.created(uri).body(tillService.saveTill(till));
	}
	
	@DeleteMapping("/tills/delete")
	@PreAuthorize("hasAnyAuthority('TILL-DELETE')")
	public ResponseEntity<Boolean> deleteTill(
			@RequestParam(name = "id") Long id){
		Till till = tillService.getTill(id);
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/tills/delete").toUriString());
		return ResponseEntity.created(uri).body(tillService.deleteTill(till));
	}
	
	@PostMapping("/tills/activate")
	@PreAuthorize("hasAnyAuthority('TILL-CREATE','TILL-UPDATE')")
	public ResponseEntity<Till> activateTill(
			@RequestParam(name = "id") Long id){
		Till till = tillService.getTill(id);
		till.setActive(true);
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/tills/activate").toUriString());
		return ResponseEntity.created(uri).body(tillService.saveTill(till));
	}
	
	@PostMapping("/tills/deactivate")
	@PreAuthorize("hasAnyAuthority('TILL-CREATE','TILL-UPDATE')")
	public ResponseEntity<Till> deactivateTill(
			@RequestParam(name = "id") Long id){
		Till till = tillService.getTill(id);
		till.setActive(false);
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/tills/deactivate").toUriString());
		return ResponseEntity.created(uri).body(tillService.saveTill(till));
	}
	
	@PostMapping("/tills/activate_pos_printer")
	@PreAuthorize("hasAnyAuthority('TILL-CREATE','TILL-UPDATE')")
	public ResponseEntity<Till> activatePosPrinter(
			@RequestParam(name = "id") Long id){
		Till till = tillService.getTill(id);
		till.setPosPrinterEnabled(true);
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/tills/activate_pos_printer").toUriString());
		return ResponseEntity.created(uri).body(tillService.saveTill(till));
	}
	
	@PostMapping("/tills/deactivate_pos_printer")
	@PreAuthorize("hasAnyAuthority('TILL-CREATE','TILL-UPDATE')")
	public ResponseEntity<Till> deactivatePosPrinter(
			@RequestParam(name = "id") Long id){
		Till till = tillService.getTill(id);
		till.setPosPrinterEnabled(false);
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/tills/activate_pos_printer").toUriString());
		return ResponseEntity.created(uri).body(tillService.saveTill(till));
	}
	
	@PostMapping("/tills/activate_fiscal_printer")
	@PreAuthorize("hasAnyAuthority('TILL-CREATE','TILL-UPDATE')")
	public ResponseEntity<Till> activateFiscalPrinter(
			@RequestParam(name = "id") Long id){
		Till till = tillService.getTill(id);
		till.setFiscalPrinterEnabled(true);
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/tills/activate_fiscal_printer").toUriString());
		return ResponseEntity.created(uri).body(tillService.saveTill(till));
	}
	
	@PostMapping("/tills/deactivate_fiscal_printer")
	@PreAuthorize("hasAnyAuthority('TILL-CREATE','TILL-UPDATE')")
	public ResponseEntity<Till> deactivateFiscalPrinter(
			@RequestParam(name = "id") Long id){
		Till till = tillService.getTill(id);
		till.setFiscalPrinterEnabled(false);
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/tills/deactivate_fiscal_printer").toUriString());
		return ResponseEntity.created(uri).body(tillService.saveTill(till));
	}
	
	@PostMapping("/tills/enable_negative_sales")
	@PreAuthorize("hasAnyAuthority('TILL-UPDATE')")
	public ResponseEntity<Till> enableNegativeSales(
			@RequestParam(name = "id") Long id){
		Till till = tillService.getTill(id);
		till.setNegativeSalesEnabled(true);
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/tills/enable_negative_sales").toUriString());
		return ResponseEntity.created(uri).body(tillService.saveTill(till));
	}
	
	@PostMapping("/tills/disable_negative_sales")
	@PreAuthorize("hasAnyAuthority('TILL-UPDATE')")
	public ResponseEntity<Till> disableNegativeSales(
			@RequestParam(name = "id") Long id){
		Till till = tillService.getTill(id);
		till.setNegativeSalesEnabled(false);
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/tills/enable_negative_sales").toUriString());
		return ResponseEntity.created(uri).body(tillService.saveTill(till));
	}
	
	@PostMapping("/tills/cash_pick_up")
	@PreAuthorize("hasAnyAuthority('TILL-UPDATE')")
	public ResponseEntity<Void>cashPickUp(
			@RequestBody CashPickUp cashPickUp,
			HttpServletRequest request){
		double amount = cashPickUp.getAmount();
		Optional<Till> t = tillRepository.findByNo(cashPickUp.getTill().getNo());
		if(!t.isPresent()) {
			throw new NotFoundException("Till not found");
		}		
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/tills/cash_pick_up").toUriString());
		cashPickUpService.pickUp(amount, t.get(), request);
		return null;		
	}
	
	@GetMapping("/tills/get_float")
	public double getFloatByTill(
			@RequestParam(name = "till_no") String tillNo){
		Optional<Till> t = tillRepository.findByNo(tillNo);
		if(t.isPresent()) {
			return t.get().getFloatBalance();
		}else {
			return 0;
		}
	}
	
	@GetMapping("/tills/get_cash")
	public double getCashByTill(
			@RequestParam(name = "till_no") String tillNo){
		Optional<Till> t = tillRepository.findByNo(tillNo);
		if(t.isPresent()) {
			return t.get().getCash();
		}else {
			return 0;
		}
	}
	
	@PostMapping("/tills/update_float")
	@PreAuthorize("hasAnyAuthority('TILL-UPDATE')")
	public ResponseEntity<Void>float_(
			@RequestBody Floatt float_,
			HttpServletRequest request){
		double add = float_.getAddition();
		double deduct = float_.getDeduction();
		Optional<Till> t = tillRepository.findByNo(float_.getTill().getNo());
		if(!t.isPresent()) {
			throw new NotFoundException("Till not found");
		}		
		if(add > 0 && deduct > 0) {
			throw new InvalidEntryException("Simultaneous addition and deduction is not allowed");
		}else if(add < 0 || deduct < 0) {
			throw new InvalidOperationException("Negative values are not allowed");
		}else if(add == 0 && deduct == 0) {
			throw new InvalidOperationException("One of the values must be more than zero");
		}
		if(add > 0) {
			floatService.addFloat(add, t.get(), request);
		}else if(deduct > 0) {
			floatService.deductFloat(deduct, t.get(), request);
		}
		return null;		
	}
	
	@PostMapping("/tills/petty_cash")
	//@PreAuthorize("hasAnyAuthority('TILL-CREATE')")
	public ResponseEntity<Void>pettyCash(
			@RequestBody PettyCash pettyCash,
			HttpServletRequest request){
		double amount = pettyCash.getAmount();
		String details = pettyCash.getDetails();
		Optional<Till> t = tillRepository.findByNo(pettyCash.getTill().getNo());
		if(!t.isPresent()) {
			throw new NotFoundException("Till not found");
		}		
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/tills/cash_pick_up").toUriString());
		pettyCashService.pettyCash(amount, details, t.get(), request);
		return null;		
	}
}

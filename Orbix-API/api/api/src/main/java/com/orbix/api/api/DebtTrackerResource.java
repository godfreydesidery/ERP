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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.orbix.api.domain.Customer;
import com.orbix.api.domain.Debt;
import com.orbix.api.domain.DebtHistory;
import com.orbix.api.domain.DebtTracker;
import com.orbix.api.domain.Lpo;
import com.orbix.api.domain.SalesAgent;
import com.orbix.api.domain.SalesInvoice;
import com.orbix.api.domain.Supplier;
import com.orbix.api.exceptions.InvalidOperationException;
import com.orbix.api.exceptions.NotFoundException;
import com.orbix.api.models.DebtModel;
import com.orbix.api.models.DebtTrackerModel;
import com.orbix.api.models.LpoModel;
import com.orbix.api.repositories.CustomerRepository;
import com.orbix.api.repositories.DayRepository;
import com.orbix.api.repositories.DebtHistoryRepository;
import com.orbix.api.repositories.DebtRepository;
import com.orbix.api.repositories.DebtTrackerRepository;
import com.orbix.api.repositories.SalesAgentRepository;
import com.orbix.api.repositories.SalesInvoiceRepository;
import com.orbix.api.repositories.UserRepository;
import com.orbix.api.service.DayService;
import com.orbix.api.service.DebtService;
import com.orbix.api.service.DebtTrackerService;
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
public class DebtTrackerResource {
	
	private final SalesAgentRepository salesAgentRepository;
	private final CustomerRepository customerRepository;
	private final DebtRepository debtRepository;
	private final SalesInvoiceRepository salesInvoiceRepository;
	private final DebtTrackerRepository debtTrackerRepository;
	private final UserService userService;
	private final DayService dayService;
	private final DebtTrackerService debtTrackerService;
	private final DayRepository dayRepository;
	private final UserRepository userRepository;
	private final DebtHistoryRepository debtHistoryRepository;
	
	
	
	@GetMapping("/debt_trackers")
	public ResponseEntity<List<DebtTracker>>getDebtTrackers(){
		return ResponseEntity.ok().body(debtTrackerService.getAllVisible());
	}
		
	@GetMapping("/debt_trackers/history")
	public ResponseEntity<List<DebtHistory>>getDebtHistory(@RequestParam(name = "id") Long id){
		Optional<DebtTracker> dt = debtTrackerRepository.findById(id);
		if(!dt.isPresent()) {
			throw new NotFoundException("Debt not found");
		}		
		return ResponseEntity.ok().body(debtHistoryRepository.findAllByDebtTracker(dt.get()));
	}
	
	@PostMapping("/debt_trackers/create")
	//@PreAuthorize("hasAnyAuthority('LPO-CREATE')")
	public ResponseEntity<Boolean>createDebtTracker(
			@RequestBody DebtTracker debtTracker,
			HttpServletRequest request){
		Optional<SalesAgent> s = salesAgentRepository.findById(debtTracker.getOfficerIncharge().getId());
		if(!s.isPresent()) {
			throw new NotFoundException("Officer in charge not found");
		}
		Optional<Customer> c = customerRepository.findById(debtTracker.getCustomer().getId());
		if(!c.isPresent()) {
			throw new NotFoundException("Customer not found");
		}
		Optional<Debt> d = debtRepository.findById(debtTracker.getDebt().getId());
		if(!d.isPresent()) {
			throw new NotFoundException("Debt not present");
		}
		DebtTracker dt = new DebtTracker();
		dt.setNo("NA");
		dt.setOfficerIncharge(s.get());
		dt.setCustomer(c.get());
		dt.setDebt(d.get());
		dt.setSalesInvoice(null);
		dt.setAmount(debtTracker.getAmount());
		dt.setPaid(0);
		dt.setBalance(debtTracker.getAmount());
		dt.setStatus("UNPAID");		
		dt.setComments(debtTracker.getComments());
		dt.setCreatedBy(userService.getUserId(request));
		dt.setCreatedAt(dayService.getDayId());
		dt.setInceptionDay(dayRepository.getCurrentBussinessDay());
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/debt_trackers/create").toUriString());
		return ResponseEntity.created(uri).body(debtTrackerService.createFromSalesList(dt, userRepository.findById(userService.getUserId(request)).get()));
	}
	
	@PostMapping("/debt_trackers/create_from_sales_invoice")
	//@PreAuthorize("hasAnyAuthority('LPO-CREATE')")
	public ResponseEntity<Boolean>createDebtTrackerFromSalesInvoice(
			@RequestBody DebtTracker debtTracker,
			HttpServletRequest request){
		Optional<SalesAgent> s = salesAgentRepository.findById(debtTracker.getOfficerIncharge().getId());
		if(!s.isPresent()) {
			throw new NotFoundException("Officer in charge not found");
		}
		Optional<Customer> c = customerRepository.findById(debtTracker.getCustomer().getId());
		if(!c.isPresent()) {
			throw new NotFoundException("Customer not found");
		}
		Optional<SalesInvoice> d = salesInvoiceRepository.findById(debtTracker.getSalesInvoice().getId());
		if(!d.isPresent()) {
			throw new NotFoundException("Sales iinvoice not present");
		}
		DebtTracker dt = new DebtTracker();
		dt.setNo("NA");
		dt.setOfficerIncharge(s.get());
		dt.setCustomer(c.get());
		dt.setDebt(null);
		dt.setSalesInvoice(d.get());
		dt.setAmount(debtTracker.getAmount());
		dt.setPaid(0);
		dt.setBalance(debtTracker.getAmount());
		dt.setStatus("UNPAID");		
		dt.setComments(debtTracker.getComments());
		dt.setCreatedBy(userService.getUserId(request));
		dt.setCreatedAt(dayService.getDayId());
		dt.setInceptionDay(dayRepository.getCurrentBussinessDay());
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/debt_trackers/create_from_sales_invoice").toUriString());
		return ResponseEntity.created(uri).body(debtTrackerService.createFromSalesInvoice(dt, userRepository.findById(userService.getUserId(request)).get()));
	}
	
	@GetMapping("/debt_trackers/get")
	public ResponseEntity<DebtTrackerModel>getDebtTracker(
			@RequestParam(name = "id") Long id){
		Optional<DebtTracker> d = debtTrackerRepository.findById(id);
		if(!d.isPresent()) {
			throw new NotFoundException("Debt not found");
		}
		DebtTrackerModel m = new DebtTrackerModel();
		m.setId(d.get().getId());
		m.setNo(d.get().getNo());
		m.setStatus(d.get().getStatus());
		m.setAmount(d.get().getAmount());
		m.setPaid(d.get().getPaid());
		m.setBalance(d.get().getBalance());
		
		return ResponseEntity.ok().body(m);
	}
	
	@PostMapping("/debt_trackers/pay")
	//@PreAuthorize("hasAnyAuthority('LPO-CREATE')")
	public ResponseEntity<DebtTracker>payDebtTracker(
			@RequestBody DebtTracker debtTracker,
			HttpServletRequest request){
		
		Optional<DebtTracker> d = debtTrackerRepository.findById(debtTracker.getId());
		if(!d.isPresent()) {
			throw new NotFoundException("Debt not present");
		}
		
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/debt_trackers/pay").toUriString());
		return ResponseEntity.created(uri).body(debtTrackerService.pay(d.get(), debtTracker.getAmount(), userRepository.findById(userService.getUserId(request)).get()));
	}
	
	@PutMapping("/debt_trackers/archive_all")
	@PreAuthorize("hasAnyAuthority('DEBT_TRACKER-ARCHIVE')")
	public ResponseEntity<Boolean>archiveDebtTrackers(){			
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/debt_trackers/archive_all").toUriString());
		return ResponseEntity.created(uri).body(debtTrackerService.archiveAll());
	}
}

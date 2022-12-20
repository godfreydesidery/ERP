/**
 * 
 */
package com.orbix.api.api;

import java.net.URI;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.orbix.api.domain.Customer;
import com.orbix.api.domain.Debt;
import com.orbix.api.domain.DebtTracker;
import com.orbix.api.domain.Lpo;
import com.orbix.api.domain.SalesAgent;
import com.orbix.api.domain.Supplier;
import com.orbix.api.exceptions.NotFoundException;
import com.orbix.api.models.DebtTrackerModel;
import com.orbix.api.models.LpoModel;
import com.orbix.api.repositories.CustomerRepository;
import com.orbix.api.repositories.DayRepository;
import com.orbix.api.repositories.DebtRepository;
import com.orbix.api.repositories.SalesAgentRepository;
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
	private final UserService userService;
	private final DayService dayService;
	private final DebtTrackerService debtTrackerService;
	private final DayRepository dayRepository;
	
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
		dt.setAmount(debtTracker.getAmount());
		dt.setPaid(0);
		dt.setBalance(debtTracker.getAmount());
		dt.setStatus("UNPAID");		
		dt.setComments(debtTracker.getComments());
		dt.setCreatedBy(userService.getUserId(request));
		dt.setCreatedAt(dayService.getDayId());
		dt.setInceptionDay(dayRepository.getCurrentBussinessDay());
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/debt_trackers/create").toUriString());
		return ResponseEntity.created(uri).body(debtTrackerService.create(dt));
	}
}

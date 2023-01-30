/**
 * 
 */
package com.orbix.api.api;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.orbix.api.domain.Product;
import com.orbix.api.domain.SalesAgent;
import com.orbix.api.domain.SalesList;
import com.orbix.api.domain.SalesSheet;
import com.orbix.api.exceptions.InvalidOperationException;
import com.orbix.api.exceptions.NotFoundException;
import com.orbix.api.models.LAgentModel;
import com.orbix.api.models.LCustomerModel;
import com.orbix.api.models.LProductModel;
import com.orbix.api.models.LSalesListObjectModel;
import com.orbix.api.models.SalesListModel;
import com.orbix.api.models.SalesSheetModel;
import com.orbix.api.models.WMSSalesModel;
import com.orbix.api.repositories.SalesAgentRepository;
import com.orbix.api.repositories.SalesListRepository;
import com.orbix.api.repositories.SalesSheetRepository;
import com.orbix.api.repositories.VatGroupRepository;
import com.orbix.api.service.SalesAgentService;
import com.orbix.api.service.VatGroupService;

import lombok.RequiredArgsConstructor;

/**
 * @author Godfrey
 *
 */
@RestController
@RequestMapping("/wms")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class WMSResource {
	
	private final SalesAgentService salesAgentService;
	private final SalesSheetRepository salesSheetRepository;
	private final SalesListRepository salesListRepository;
	private final SalesAgentRepository salesAgentRepository;
	
	
	@GetMapping("/wms_load_customers")	
	public ResponseEntity<List<LCustomerModel>>getCustomers(){
		return ResponseEntity.ok().body(salesAgentService.loadCustomers());
	}
	
	@GetMapping("/wms_get_profile")	
	public SalesAgent getProfile(
			@RequestParam(name = "sales_agent_name") String salesAgentName){
		return salesAgentRepository.findByName(salesAgentName).get();
	}
	
	@GetMapping("/wms_load_sales_sheet")	
	public ResponseEntity<SalesSheetModel> getSalesSheet(
			@RequestParam(name = "sales_list_no") String salesListNo){
		Optional<SalesList> sl = salesListRepository.findByNo(salesListNo);
		if(!sl.isPresent()) {
			throw new NotFoundException("Corresponding sales list not found");
		}
		Optional<SalesSheet> ss = salesSheetRepository.findBySalesList(sl.get());
		if(!ss.isPresent()) {
			throw new NotFoundException("Sales Sheet not found");
		}
		return ResponseEntity.ok().body(salesAgentService.getSalesSheet(ss.get().getId()));
	}
	
	@GetMapping("/wms_load_sales_list")	
	public ResponseEntity<SalesListModel> getSalesList(
			@RequestParam(name = "sales_list_no") String salesListNo){
		Optional<SalesList> sl = salesListRepository.findByNo(salesListNo);
		if(!sl.isPresent()) {
			throw new NotFoundException("Sales list not found");
		}
		
		return ResponseEntity.ok().body(salesAgentService.getSalesList(sl.get().getId()));
	}
	
	@GetMapping("/wms_load_available_products")	
	public ResponseEntity<List<LProductModel>>getAvailableProducts(
			@RequestParam(name = "sales_list_no") String salesListNo, @RequestParam(name = "sales_agent_id") Long salesAgentId){
		return ResponseEntity.ok().body(salesAgentService.loadAvailableProducts(salesListNo, salesAgentId));
	}
	
	@PostMapping("/wms_pass_in")
	public ResponseEntity<LSalesListObjectModel>createProduct(
			@RequestBody LAgentModel agent){
		if(agent.getPassName().equals("") || agent.getPassCode().equals("")) {
			throw new InvalidOperationException("Invalid pass in credentials");
		}
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/wms/wms_load_available_products").toUriString());
		return ResponseEntity.created(uri).body(salesAgentService.passIn(agent.getPassName(), agent.getPassCode()));
	}
	
	@PostMapping("/wms_sale/confirm")
	public Object confirmSale(
			@RequestBody WMSSalesModel sale){
		
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/wms/wms_sale/confirm").toUriString());
		return salesAgentService.confirmSale(sale);
	}
}

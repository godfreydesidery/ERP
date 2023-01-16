/**
 * 
 */
package com.orbix.api.api;

import java.net.URI;
import java.util.List;

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
import com.orbix.api.exceptions.InvalidOperationException;
import com.orbix.api.models.LAgentModel;
import com.orbix.api.models.LCustomerModel;
import com.orbix.api.models.LProductModel;
import com.orbix.api.models.LSalesListObjectModel;
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
	
	
	@GetMapping("/wms_load_customers")	
	public ResponseEntity<List<LCustomerModel>>getCustomers(){
		return ResponseEntity.ok().body(salesAgentService.loadCustomers());
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
}

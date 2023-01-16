/**
 * 
 */
package com.orbix.api.api;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

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

import com.orbix.api.domain.SalesAgent;
import com.orbix.api.models.RecordModel;
import com.orbix.api.service.SalesAgentService;

import lombok.RequiredArgsConstructor;

/**
 * @author Godfrey
 *
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class SalesAgentResource {

	private final 	SalesAgentService salesAgentService;
	
	@GetMapping("/sales_agents")	
	public ResponseEntity<List<SalesAgent>>getSalesAgents(){
		return ResponseEntity.ok().body(salesAgentService.getAll());
	}
	
	@GetMapping("/sales_agents/get")
	public ResponseEntity<SalesAgent> getSalesAgent(
			@RequestParam(name = "id") Long id){
		return ResponseEntity.ok().body(salesAgentService.get(id));
	}
	
	@GetMapping("/sales_agents/get_by_no")
	public ResponseEntity<SalesAgent> getSalesAgentByNo(
			@RequestParam(name = "no") String no){
		return ResponseEntity.ok().body(salesAgentService.getByNo(no));
	}
	
	@GetMapping("/sales_agents/get_by_name")
	public ResponseEntity<SalesAgent> getSalesAgentByName(
			@RequestParam(name = "name") String name){
		return ResponseEntity.ok().body(salesAgentService.getByName(name));
	}
	
	@GetMapping("/sales_agents/get_names")
	public ResponseEntity<List<String>> getSalesAgentsNames(){
		List<String> names = new ArrayList<String>();
		names = salesAgentService.getNames();
		return ResponseEntity.ok().body(names);
	}
	
	@GetMapping("/sales_agents/request_no")
	public ResponseEntity<RecordModel> requestNo(){
		return ResponseEntity.ok().body(salesAgentService.requestSalesAgentNo());
	}
	
	@PostMapping("/sales_agents/create")
	@PreAuthorize("hasAnyAuthority('EMPLOYEE-CREATE')")
	public ResponseEntity<SalesAgent>createSalesAgent(
			@RequestBody SalesAgent salesAgent){
		salesAgent.setNo("NA");
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/sales_agents/create").toUriString());
		return ResponseEntity.created(uri).body(salesAgentService.save(salesAgent));
	}
		
	@PutMapping("/sales_agents/update")
	@PreAuthorize("hasAnyAuthority('EMPLOYEE-CREATE','EMPLOYEE-UPDATE')")
	public ResponseEntity<SalesAgent>updateSalesAgent(
			@RequestBody SalesAgent salesAgent, 
			HttpServletRequest request){
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/sales_agents/update").toUriString());
		return ResponseEntity.created(uri).body(salesAgentService.save(salesAgent));
	}
	
	@DeleteMapping("/sales_agents/delete")
	@PreAuthorize("hasAnyAuthority('EMPLOYEE-DELETE')")
	public ResponseEntity<Boolean> deleteSalesAgent(
			@RequestParam(name = "id") Long id){
		SalesAgent salesAgent = salesAgentService.get(id);
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/sales_agents/delete").toUriString());
		return ResponseEntity.created(uri).body(salesAgentService.delete(salesAgent));
	}
	
	
	
	
	
}

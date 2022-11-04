/**
 * 
 */
package com.orbix.api.api;

import java.net.URI;
import java.util.ArrayList;
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

import com.orbix.api.domain.VatGroup;
import com.orbix.api.exceptions.NotFoundException;
import com.orbix.api.models.RecordModel;
import com.orbix.api.repositories.VatGroupRepository;
import com.orbix.api.service.VatGroupService;

import lombok.RequiredArgsConstructor;

/**
 * @author Godfrey
 *
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class VatGroupResource {
	private final 	VatGroupService vatGroupService;
	private final VatGroupRepository vatGroupRepository;
	
	@GetMapping("/vat_groups")
	public ResponseEntity<List<VatGroup>>getVatGroups(){
		return ResponseEntity.ok().body(vatGroupService.getAll());
	}
	
	@GetMapping("/vat_groups/get_codes")
	public ResponseEntity<List<String>> getVatGroupCodes(){
		List<String> codes = new ArrayList<String>();
		codes = vatGroupService.getCodes();
		return ResponseEntity.ok().body(codes);
	}
	
	@GetMapping("/vat_groups/get")
	public ResponseEntity<VatGroup> getVatGroup(
			@RequestParam(name = "id") Long id){
		return ResponseEntity.ok().body(vatGroupService.get(id));
	}
	
	@GetMapping("/vat_groups/get_by_code")
	public ResponseEntity<VatGroup> getVatGroupByCode(
			@RequestParam(name = "code") String code){
		return ResponseEntity.ok().body(vatGroupService.getByCode(code));
	}
	
	@PostMapping("/vat_groups/create")
	@PreAuthorize("hasAnyAuthority('ADMIN-CREATE')")
	public ResponseEntity<VatGroup>createVatGroup(
			@RequestBody VatGroup vatGroup){		
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/vat_groups/create").toUriString());
		return ResponseEntity.created(uri).body(vatGroupService.save(vatGroup));
	}
		
	@PutMapping("/vat_groups/update")
	@PreAuthorize("hasAnyAuthority('ADMIN-CREATE','ADMIN-UPDATE')")
	public ResponseEntity<VatGroup>updateVatGroup(
			@RequestBody VatGroup vatGroup, 
			HttpServletRequest request){
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/vat_groups/update").toUriString());
		return ResponseEntity.created(uri).body(vatGroupService.save(vatGroup));
	}
	
	@PutMapping("/vat_groups/update_by_code")
	@PreAuthorize("hasAnyAuthority('ADMIN-UPDATE')")
	public ResponseEntity<VatGroup>updateVatGroupByCode(
			@RequestBody VatGroup vatGroup, 
			HttpServletRequest request){
		Optional<VatGroup> vg = vatGroupRepository.findByCode(vatGroup.getCode());
		if(vg.isPresent()) {
			vatGroup.setId(vg.get().getId());
		}else {
			throw new NotFoundException("VAT Group not found");
		}
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/vat_groups/update").toUriString());
		return ResponseEntity.created(uri).body(vatGroupService.save(vatGroup));
	}
	
	@DeleteMapping("/vat_groups/delete")
	@PreAuthorize("hasAnyAuthority('ADMIN-DELETE')")
	public ResponseEntity<Boolean> deleteVatGroup(
			@RequestParam(name = "id") Long id){
		VatGroup vatGroup = vatGroupService.get(id);
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/vat_groups/delete").toUriString());
		return ResponseEntity.created(uri).body(vatGroupService.delete(vatGroup));
	}

}

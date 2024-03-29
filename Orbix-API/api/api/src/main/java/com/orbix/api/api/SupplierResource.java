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

import com.orbix.api.domain.Customer;
import com.orbix.api.domain.Product;
import com.orbix.api.domain.Supplier;
import com.orbix.api.exceptions.NotFoundException;
import com.orbix.api.models.RecordModel;
import com.orbix.api.repositories.SupplierRepository;
import com.orbix.api.service.SupplierService;

import lombok.RequiredArgsConstructor;

/**
 * @author GODFREY
 *
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class SupplierResource {
	
	private final 	SupplierService supplierService;
	private final SupplierRepository supplierRepository;
	
	@GetMapping("/suppliers")
	public ResponseEntity<List<Supplier>>getSuppliers(){
		return ResponseEntity.ok().body(supplierService.getAll());
	}
	
	@GetMapping("/suppliers/request_code")
	public ResponseEntity<RecordModel> requestCode(){
		return ResponseEntity.ok().body(supplierService.requestSupplierCode());
	}
	
	@GetMapping("/suppliers/get_names")
	public ResponseEntity<List<String>> getSupplierNames(){
		List<String> names = new ArrayList<String>();
		names = supplierService.getNames();
		return ResponseEntity.ok().body(names);
	}
	
	@GetMapping("/suppliers/get")
	public ResponseEntity<Supplier> getSupplier(
			@RequestParam(name = "id") Long id){
		return ResponseEntity.ok().body(supplierService.get(id));
	}
	
	@GetMapping("/suppliers/get_by_code")
	public ResponseEntity<Supplier> getSupplierByCode(
			@RequestParam(name = "code") String code){
		return ResponseEntity.ok().body(supplierService.getByCode(code));
	}
	
	@GetMapping("/suppliers/get_by_name")
	public ResponseEntity<Supplier> getSupplierByName(
			@RequestParam(name = "name") String name){
		return ResponseEntity.ok().body(supplierService.getByName(name));
	}
	
	
	
	@PostMapping("/suppliers/create")
	@PreAuthorize("hasAnyAuthority('SUPPLIER-CREATE')")
	public ResponseEntity<Supplier>createSupplier(
			@RequestBody Supplier supplier){
		if(supplier.getCode().equals("")) {
			supplier.setCode("NA");
		}
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/suppliers/create").toUriString());
		return ResponseEntity.created(uri).body(supplierService.save(supplier));
	}
		
	@PutMapping("/suppliers/update")
	@PreAuthorize("hasAnyAuthority('SUPPLIER-CREATE','SUPPLIER-UPDATE')")
	public ResponseEntity<Supplier>updateSupplier(
			@RequestBody Supplier supplier, 
			HttpServletRequest request){
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/suppliers/update").toUriString());
		return ResponseEntity.created(uri).body(supplierService.save(supplier));
	}
	
	@PutMapping("/suppliers/update_by_code")
	@PreAuthorize("hasAnyAuthority('SUPPLIER-UPDATE')")
	public ResponseEntity<Supplier>updateSupplierByCode(
			@RequestBody Supplier supplier, 
			HttpServletRequest request){
		Optional<Supplier> sup = supplierRepository.findByCode(supplier.getCode());
		if(sup.isPresent()) {
			supplier.setId(sup.get().getId());
		}else {
			throw new NotFoundException("Supplier not found");
		}
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/suppliers/update").toUriString());
		return ResponseEntity.created(uri).body(supplierService.save(supplier));
	}
	
	@DeleteMapping("/suppliers/delete")
	@PreAuthorize("hasAnyAuthority('SUPPLIER-DELETE')")
	public ResponseEntity<Boolean> deleteSupplier(
			@RequestParam(name = "id") Long id){
		Supplier supplier = supplierService.get(id);
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/suppliers/delete").toUriString());
		return ResponseEntity.created(uri).body(supplierService.delete(supplier));
	}

}

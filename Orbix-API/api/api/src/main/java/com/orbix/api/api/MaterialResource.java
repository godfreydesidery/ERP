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

import com.orbix.api.domain.Material;
import com.orbix.api.exceptions.NotFoundException;
import com.orbix.api.models.RecordModel;
import com.orbix.api.repositories.MaterialRepository;
import com.orbix.api.service.MaterialService;
import lombok.RequiredArgsConstructor;

/**
 * @author GODFREY
 *
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class MaterialResource {
	
private final MaterialService materialService;
private final MaterialRepository materialRepository;
	
	@GetMapping("/materials")
	public ResponseEntity<List<Material>>getMaterials(){
		return ResponseEntity.ok().body(materialService.getAll());
	}
	
	@GetMapping("/materials/get")
	public ResponseEntity<Material> getMaterial(
			@RequestParam(name = "id") Long id){
		return ResponseEntity.ok().body(materialService.get(id));
	}
	
	@GetMapping("/materials/request_code")
	public ResponseEntity<RecordModel> requestCode(){
		return ResponseEntity.ok().body(materialService.requestMaterialCode());
	}
	
	@GetMapping("/materials/get_descriptions")
	public ResponseEntity<List<String>> getActiveDescriptions(){
		List<String> descriptions = new ArrayList<String>();
		descriptions = materialService.getActiveDescriptions();
		return ResponseEntity.ok().body(descriptions);
	}
	
	@GetMapping("/materials/get_by_code")
	@PreAuthorize("hasAnyAuthority('MATERIAL-READ')")
	public ResponseEntity<Material> getMaterialByCode(
			@RequestParam(name = "code") String code){
		return ResponseEntity.ok().body(materialService.getByCode(code));
	}
	
	@GetMapping("/materials/get_by_description")
	@PreAuthorize("hasAnyAuthority('MATERIAL-READ')")
	public ResponseEntity<Material> getMaterialByDescription(
			@RequestParam(name = "description") String description){
		return ResponseEntity.ok().body(materialService.getByDescription(description));
	}
	
	@PostMapping("/materials/create")
	@PreAuthorize("hasAnyAuthority('MATERIAL-CREATE')")
	public ResponseEntity<Material>createMaterial(
			@RequestBody Material material){
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/materials/create").toUriString());
		return ResponseEntity.created(uri).body(materialService.save(material));
	}
		
	@PutMapping("/materials/update")
	@PreAuthorize("hasAnyAuthority('MATERIAL-UPDATE')")
	public ResponseEntity<Material>updateMaterial(
			@RequestBody Material material, 
			HttpServletRequest request){
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/materials/update").toUriString());
		return ResponseEntity.created(uri).body(materialService.save(material));
	}
	
	@PutMapping("/materials/update_by_code")
	@PreAuthorize("hasAnyAuthority('MATERIAL-UPDATE')")
	public ResponseEntity<Material>updateMaterialByCode(
			@RequestBody Material material, 
			HttpServletRequest request){
		Optional<Material> mat = materialRepository.findByCode(material.getCode());
		if(mat.isPresent()) {
			material.setId(mat.get().getId());
		}else {
			throw new NotFoundException("Material not found");
		}
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/materials/update").toUriString());
		return ResponseEntity.created(uri).body(materialService.save(material));
	}
	
	@DeleteMapping("/materials/delete")
	@PreAuthorize("hasAnyAuthority('MATERIAL-DELETE')")
	public ResponseEntity<Boolean> deleteMaterial(
			@RequestParam(name = "id") Long id){
		Material material = materialService.get(id);
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/materials/delete").toUriString());
		return ResponseEntity.created(uri).body(materialService.delete(material));
	}
}

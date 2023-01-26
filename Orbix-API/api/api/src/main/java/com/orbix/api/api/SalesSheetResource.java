/**
 * 
 */
package com.orbix.api.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.orbix.api.domain.SalesList;
import com.orbix.api.domain.SalesSheet;
import com.orbix.api.models.SalesSheetModel;
import com.orbix.api.repositories.SalesListRepository;
import com.orbix.api.repositories.SalesSheetRepository;
import com.orbix.api.service.SalesSheetService;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * @author Godfrey
 *
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class SalesSheetResource {
	private final SalesSheetService salesSheetService;
	private final SalesListRepository salesListRepository;
	private final SalesSheetRepository salesSheetRepository;
	
	
	@GetMapping("/sales_sheets")
	public List<LSalesSheetModel> getSalesSheets(){
		List<SalesList> sl = salesListRepository.findByStatus("PENDING");
		List<SalesSheet> ss = new ArrayList<>();
		for(SalesList salesList : sl) {
			Optional<SalesSheet> s = salesSheetRepository.findBySalesList(salesList);
			if(s.isPresent()) {
				ss.add(s.get());
			}
		}
		List<LSalesSheetModel> models = new ArrayList<>();
		for(SalesSheet sheet : ss) {
			LSalesSheetModel model = new LSalesSheetModel();
			model.setId(sheet.getId());
			model.setNo(sheet.getNo());
			model.setSalesListNo(sheet.getSalesList().getNo());
			try {
				model.setSalesAgentName(sheet.getSalesList().getSalesAgent().getName());
			}catch(Exception e) {}
			
			if(sheet.isConfirmed() == true) {
				model.setConfirmed("CONFIRMED");
			}else {
				model.setConfirmed("NOT CONFIRMED");
			}
			models.add(model);
		}		
		return models;
	}
	
	@GetMapping("/sales_sheets/get_deactivateddueetoerror")
	public ResponseEntity<SalesSheetModel> getSalesList(
			@RequestParam(name = "id") Long id){
		return ResponseEntity.ok().body(salesSheetService.get(id));
	}
	
	@GetMapping("/sales_sheets/get_by_no")
	public ResponseEntity<SalesSheetModel> getSalesListByNo(
			@RequestParam(name = "no") String no){
		return ResponseEntity.ok().body(salesSheetService.getByNo(no));
	}
	
}

@Data
class LSalesSheetModel{
	Long id;
	String no;
	String salesListNo;
	String salesAgentName;
	String confirmed;
}
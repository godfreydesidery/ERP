/**
 * 
 */
package com.orbix.api.reports.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.orbix.api.domain.Product;
import com.orbix.api.domain.Supplier;
import com.orbix.api.exceptions.NotFoundException;
import com.orbix.api.reports.models.GrnReport;
import com.orbix.api.reports.models.LpoReport;
import com.orbix.api.repositories.GrnRepository;
import com.orbix.api.repositories.LpoRepository;
import com.orbix.api.repositories.SupplierRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Godfrey
 *
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class GrnReportServiceImpl implements GrnReportService {

	private final GrnRepository grnRepository;
	private final SupplierRepository supplierRepository;

	@Override
	public List<GrnReport> getGrnReport(
			LocalDate from,
			LocalDate to,
			Supplier supplier,
			List<Product> products) {		
		if(!supplier.getName().equals("")) {
			Optional<Supplier> s = supplierRepository.findByName(supplier.getName());
			if(!s.isPresent()) {
				throw new NotFoundException("Supplier not found");
			}
			//return lpoRepository.getSupplySalesReport(from, to, supplier.getName());
			return grnRepository.getGrnReportAll(from, to);
		}else if(!products.isEmpty()) {
			List<String> codes = new ArrayList<>();
			for(Product product : products) {
				codes.add(product.getCode());
			}
			return grnRepository.getGrnReportByProducts(from, to, codes);
		}else {
			return grnRepository.getGrnReportAll(from, to);
		}			
	}
}

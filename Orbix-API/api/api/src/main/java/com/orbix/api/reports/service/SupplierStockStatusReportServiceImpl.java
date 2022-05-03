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
import com.orbix.api.reports.models.ProductStockCardReport;
import com.orbix.api.reports.models.SupplierStockStatusReport;
import com.orbix.api.repositories.CategoryRepository;
import com.orbix.api.repositories.ClassRepository;
import com.orbix.api.repositories.DayRepository;
import com.orbix.api.repositories.DepartmentRepository;
import com.orbix.api.repositories.LevelFourRepository;
import com.orbix.api.repositories.LevelOneRepository;
import com.orbix.api.repositories.LevelThreeRepository;
import com.orbix.api.repositories.LevelTwoRepository;
import com.orbix.api.repositories.ProductRepository;
import com.orbix.api.repositories.SaleDetailRepository;
import com.orbix.api.repositories.SaleRepository;
import com.orbix.api.repositories.SubClassRepository;
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
public class SupplierStockStatusReportServiceImpl implements SupplierStockStatusReportService{
	
	private final ProductRepository productRepository;
	private final SupplierRepository supplierRepository;
	
	@Override
	public List<SupplierStockStatusReport> getSupplierStockStatusReport(Supplier supplier, List<Product> products) {
		
		if(!supplier.getName().equals("")) {
			Optional<Supplier> s = supplierRepository.findByName(supplier.getName());
			if(!s.isPresent()) {
				throw new NotFoundException("Supplier not found");
			}			
			return productRepository.getSupplierStockStatusReportBySupplier(supplier.getName());
		}else if(!products.isEmpty()) {
			List<String> codes = new ArrayList<>();
			for(Product product : products) {
				codes.add(product.getCode());
			}
			return productRepository.getSupplierStockStatusReportByProducts(codes);
		}else {
			return productRepository.getSupplierStockStatusReportAll();
		}
	}
	
	
	
	
	
//	@Override
//	public List<SupplierStockStatusReport> getSupplierStockStatusReport(String name) {
//		return productRepository.getSupplierStockStatusReportBySupplier(name);
//	}
//	
//	@Override
//	public List<SupplierStockStatusReport> getSupplierStockStatusReportAll() {
//		return productRepository.getSupplierStockStatusReportAll();
//	}

}

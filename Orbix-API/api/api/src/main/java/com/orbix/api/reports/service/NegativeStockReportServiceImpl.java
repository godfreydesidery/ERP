/**
 * 
 */
package com.orbix.api.reports.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.orbix.api.reports.models.NegativeStockReport;
import com.orbix.api.repositories.ProductRepository;
import com.orbix.api.repositories.ProductionRepository;

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
public class NegativeStockReportServiceImpl implements NegativeStockReportService {
	
	private final ProductRepository productRepository;

	@Override
	public List<NegativeStockReport> getNegativeStockReport() {
		return productRepository.getNegativeStockReport();
	}

}

/**
 * 
 */
package com.orbix.api.api;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.orbix.api.domain.Cart;
import com.orbix.api.domain.Till;
import com.orbix.api.exceptions.NotFoundException;
import com.orbix.api.repositories.CustomerRepository;
import com.orbix.api.repositories.SalesInvoiceRepository;
import com.orbix.api.service.AllocationService;
import com.orbix.api.service.CustomerService;

import lombok.RequiredArgsConstructor;

/**
 * @author Godfrey
 *
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ConnectionResource {
	@GetMapping("/ping")
	public boolean ping(){
		return true;
	}
}

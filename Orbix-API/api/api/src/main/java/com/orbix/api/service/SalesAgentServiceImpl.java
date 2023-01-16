/**
 * 
 */
package com.orbix.api.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.orbix.api.accessories.Formater;
import com.orbix.api.domain.Customer;
import com.orbix.api.domain.SalesAgent;
import com.orbix.api.domain.SalesList;
import com.orbix.api.domain.SalesListDetail;
import com.orbix.api.domain.SalesSheet;
import com.orbix.api.domain.SalesSheetSale;
import com.orbix.api.domain.SalesSheetSaleDetail;
import com.orbix.api.domain.SalesAgent;
import com.orbix.api.exceptions.InvalidOperationException;
import com.orbix.api.exceptions.NotFoundException;
import com.orbix.api.models.LCustomerModel;
import com.orbix.api.models.LProductModel;
import com.orbix.api.models.LSalesListObjectModel;
import com.orbix.api.models.RecordModel;
import com.orbix.api.repositories.CustomerRepository;
import com.orbix.api.repositories.EmployeeRepository;
import com.orbix.api.repositories.SalesAgentRepository;
import com.orbix.api.repositories.SalesListRepository;
import com.orbix.api.repositories.SalesSheetRepository;

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
public class SalesAgentServiceImpl implements SalesAgentService{
	private final SalesAgentRepository salesAgentRepository;
	private final CustomerRepository customerRepository;
	private final SalesListRepository salesListRepository;
	private final SalesSheetRepository salesSheetRepository;

	@Override
	public SalesAgent save(SalesAgent salesAgent) {
		validateAgent(salesAgent);
		log.info("Saving sale agent to the database");
		SalesAgent c = salesAgentRepository.saveAndFlush(salesAgent);		
		if(c.getNo().equals("NA")) {
			c.setNo(generateAgentNo(c));
			c = salesAgentRepository.saveAndFlush(c);
		}
		return salesAgentRepository.save(c);
	}

	@Override
	public SalesAgent get(Long id) {
		return salesAgentRepository.findById(id).get();
	}
	
	@Override
	public SalesAgent getByNo(String no) {
		Optional<SalesAgent> salesAgent = salesAgentRepository.findByNo(no);
		if(!salesAgent.isPresent()) {
			throw new NotFoundException("SalesAgent not found");
		}
		return salesAgent.get();
	}

	@Override
	public boolean delete(SalesAgent salesAgent) {
		if(allowDelete(salesAgent)) {
			//salesAgentRepository.delete(salesAgent);
		}else {
			return false;
		}
		return true;
	}

	@Override
	public List<SalesAgent> getAll() {
		log.info("Fetching all sales agents");
		return salesAgentRepository.findAll();
	}
	
	private boolean validateAgent(SalesAgent salesAgent) {
		/**
		 * Put validation logic, throw Invalid exception if not valid
		 */
		
		
		return true;
	}
	
	private boolean allowDelete(SalesAgent salesAgent) {
		/**
		 * Put logic to allow till deletion, return false if not allowed, else return true
		 */
		
		throw new InvalidOperationException("Deleting the selected salesAgent is not allowed");
		
		//return true;
	}

	

	@Override
	public SalesAgent getByName(String name) {
		Optional<SalesAgent> salesAgent = salesAgentRepository.findByName(name);
		if(!salesAgent.isPresent()) {
			throw new NotFoundException("Sales Agent not found");
		}
		return salesAgent.get();
	}
	
	@Override
	public List<String> getNames() {
		return salesAgentRepository.getActiveNames();
	}
	
	private String generateAgentNo(SalesAgent salesAgent) {
		Long number = salesAgent.getId();		
		String sNumber = number.toString();
		return "SA"+sNumber;
	}
	
	@Override
	public RecordModel requestSalesAgentNo() {
		Long id = 1L;
		try {
			id = salesAgentRepository.getLastId() + 1;
		}catch(Exception e) {}
		RecordModel model = new RecordModel();
		model.setNo("SA"+id.toString());		
		return model;
	}

	@Override
	public LSalesListObjectModel passIn(String passName, String passCode) {
		LSalesListObjectModel obj = new LSalesListObjectModel();
		
		Optional<SalesAgent> s = salesAgentRepository.findByPassName(passName);
		if(!s.isPresent()) {
			throw new InvalidOperationException("Invalid pass in credentials");
		}
		if(!s.get().getPassCode().equals(passCode)) {
			throw new InvalidOperationException("Invalid pass in credentials");
		}
		
		List<SalesList> sl = salesListRepository.findBySalesAgentAndStatus(s.get(), "PENDING");
		String[] agentNos = new String[sl.size()];
		int i = 0;
		for(SalesList q : sl) {
			agentNos[i] = q.getNo();
			i++;
		}
		
		
		
		Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
		String access_token = JWT.create()
				.withSubject(passName)
				.withExpiresAt(new Date(System.currentTimeMillis()+8*60*60*1000))
				//.withIssuer(request.getRequestURI().toString())
				//.withClaim("privileges", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
				.sign(algorithm);
		
		obj.setSalesAgentId(s.get().getId());
		obj.setSalesListNo(agentNos);
		obj.setAccessToken(access_token);
		
		return obj;
	}

	@Override
	public List<LCustomerModel> loadCustomers() {
		List<LCustomerModel> l = new ArrayList<LCustomerModel>();
		List<Customer> lc = customerRepository.findAll();
		for(Customer c : lc) {
			LCustomerModel cm = new LCustomerModel();
			cm.setId(c.getId());
			cm.setName(c.getName());
			l.add(cm);
		}
		return l;
	}

	@Override
	public List<LProductModel> loadAvailableProducts(String salesListNo, Long salesAgentId) {
		Optional<SalesAgent> sa =salesAgentRepository.findById(salesAgentId);
		if(!sa.isPresent()) {
			throw new NotFoundException("Sales Agent not found");
		}
		Optional<SalesList> sl = salesListRepository.findByNoAndSalesAgent(salesListNo, sa.get());
		if(!sl.isPresent()) {
			throw new NotFoundException("Could not find a matching sales list");
		}
		if(!sl.get().getStatus().equals("PENDING")) {
			throw new InvalidOperationException("Operation is only available in a pending list");
		}
		
		Optional<SalesSheet> ss = salesSheetRepository.findBySalesList(sl.get());
		if(!ss.isPresent()) {
			throw new NotFoundException("Coresponding Sales sheet not found");
		}
		
		List<SalesSheetSale> sss = ss.get().getSalesSheetSales();
		List<SalesSheetSaleDetail> sssd = new ArrayList<SalesSheetSaleDetail>();
		for(SalesSheetSale m : sss) {
			for(SalesSheetSaleDetail dd : m.getSalesSheetSaleDetails()) {
				sssd.add(dd);
			}
			
		}
		
		List<LProductModel> l = new ArrayList<LProductModel>();
		
		List<SalesListDetail> slds = sl.get().getSalesListDetails();
		for(SalesListDetail x : slds) {
			LProductModel pm = new LProductModel();
			pm.setId(x.getProduct().getId());
			pm.setBarcode(x.getProduct().getBarcode());
			pm.setCode(x.getProduct().getCode());
			pm.setDescription(x.getProduct().getDescription());
			pm.setPrice(x.getSellingPriceVatIncl());
			pm.setAvailable(x.getTotalPacked());
			for(SalesSheetSaleDetail y : sssd) {
				if(y.getProduct() == x.getProduct()) {
					pm.setAvailable(pm.getAvailable() - y.getQty());
				}
			}
			if(pm.getAvailable() > 0) {
				l.add(pm);
			}
		}
		
		return l;
	}	
}

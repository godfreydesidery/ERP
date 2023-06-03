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
import com.orbix.api.domain.Product;
import com.orbix.api.domain.SalesAgent;
import com.orbix.api.domain.SalesAgentCustomer;
import com.orbix.api.domain.SalesList;
import com.orbix.api.domain.SalesListDetail;
import com.orbix.api.domain.SalesSheet;
import com.orbix.api.domain.SalesSheetExpense;
import com.orbix.api.domain.SalesSheetSale;
import com.orbix.api.domain.SalesSheetSaleDetail;
import com.orbix.api.domain.SalesAgent;
import com.orbix.api.exceptions.InvalidOperationException;
import com.orbix.api.exceptions.NotFoundException;
import com.orbix.api.models.LCustomerModel;
import com.orbix.api.models.LProductModel;
import com.orbix.api.models.LSalesListObjectModel;
import com.orbix.api.models.RecordModel;
import com.orbix.api.models.SalesAgentCustomerModel;
import com.orbix.api.models.SalesExpenseModel;
import com.orbix.api.models.SalesListDetailModel;
import com.orbix.api.models.SalesListModel;
import com.orbix.api.models.SalesSheetModel;
import com.orbix.api.models.SalesSheetSaleDetailModel;
import com.orbix.api.models.SalesSheetSaleModel;
import com.orbix.api.models.WMSExpenseModel;
import com.orbix.api.models.WMSProductModel;
import com.orbix.api.models.WMSSalesModel;
import com.orbix.api.repositories.CustomerRepository;
import com.orbix.api.repositories.EmployeeRepository;
import com.orbix.api.repositories.ProductRepository;
import com.orbix.api.repositories.SalesAgentCustomerRepository;
import com.orbix.api.repositories.SalesAgentRepository;
import com.orbix.api.repositories.SalesListRepository;
import com.orbix.api.repositories.SalesSheetExpenseRepository;
import com.orbix.api.repositories.SalesSheetRepository;
import com.orbix.api.repositories.SalesSheetSaleDetailRepository;
import com.orbix.api.repositories.SalesSheetSaleRepository;

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
	private final SalesSheetSaleRepository salesSheetSaleRepository;
	private final SalesSheetSaleDetailRepository salesSheetSaleDetailRepository;
	private final ProductRepository productRepository;
	private final SalesSheetExpenseRepository salesSheetExpenseRepository;
	private final SalesAgentCustomerRepository salesAgentCustomerRepository;

	@Override
	public SalesAgent save(SalesAgent salesAgent) {
		salesAgent.setName(salesAgent.getName().trim().replaceAll("\\s+", " "));
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
		obj.setSalesAgentName(s.get().getName());
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
			pm.setDescription(x.getProduct().getDescription().trim());
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

	@Override
	public Object confirmSale(WMSSalesModel saleModel) {
		Optional<SalesList> l = salesListRepository.findByNo(saleModel.getSalesListNo());
		if(!l.isPresent()) {
			throw new NotFoundException("Operation failed, corresponding sales list not found");
		}
		if(!l.get().getStatus().equals("PENDING")) {
			throw new InvalidOperationException("Corresponding sales list not pending, please login afresh to view pending sales list");
		}
		Optional<SalesSheet> s = salesSheetRepository.findBySalesList(l.get());
		if(!s.isPresent()) {
			throw new NotFoundException("Operation failed, sales sheet not found");
		}
		SalesSheet sheet = salesSheetRepository.saveAndFlush(s.get());
		
		SalesSheetSale sale = new SalesSheetSale();
		sale.setNo(saleModel.getNo());
		sale.setCustomerName(saleModel.getCustomerName());
		sale.setCustomerMobile(saleModel.getCustomerMobile());
		sale.setCustomerLocation(saleModel.getCustomerLocation());
		sale.setTotalAmount(saleModel.getTotalAmount());
		sale.setTotalDiscount(saleModel.getTotalDiscount());
		sale.setTotalCharges(saleModel.getTotalCharges());
		sale.setTotalPaid(saleModel.getTotalPaid());
		sale.setTotalDue(saleModel.getTotalDue());
		sale.setSalesSheet(sheet);
		sale = salesSheetSaleRepository.saveAndFlush(sale);
		
		List<WMSProductModel> products = saleModel.getProducts();
		for(WMSProductModel p : products) {
			Optional<Product> pr = productRepository.findById(p.getId());
			if(!pr.isPresent()) {
				continue;
			}
			SalesSheetSaleDetail sd = new SalesSheetSaleDetail();
			sd.setProduct(pr.get());
			sd.setQty(p.getQty());
			sd.setSellingPriceVatIncl(p.getSellingPriceVatIncl());
			sd.setSalesSheetSale(sale);
			salesSheetSaleDetailRepository.saveAndFlush(sd);
		}
		return products;
		
	}

	@Override
	public SalesSheetModel getSalesSheet(Long id) {
		Optional<SalesSheet> ss = salesSheetRepository.findById(id);
		if(!ss.isPresent()) {
			throw new NotFoundException("Sales sheet not found");
		}
		SalesSheet salesSheet = ss.get();
		SalesSheetModel salesSheetModel = new SalesSheetModel();
		
		salesSheetModel.setNo(salesSheet.getNo());
		List<SalesSheetSaleModel> saleSheetSaleModels = new ArrayList<>();
		for(SalesSheetSale salesSheetSale : salesSheet.getSalesSheetSales()) {
			SalesSheetSaleModel salesSheetSaleModel = new SalesSheetSaleModel();
			salesSheetSaleModel.setNo(salesSheetSale.getNo());
			salesSheetSaleModel.setCustomerName(salesSheetSale.getCustomerName());
			salesSheetSaleModel.setCustomerMobile(salesSheetSale.getCustomerMobile());
			salesSheetSaleModel.setCustomerLocation(salesSheetSale.getCustomerLocation());
			salesSheetSaleModel.setTotalAmount(salesSheetSale.getTotalAmount());
			salesSheetSaleModel.setTotalPaid(salesSheetSale.getTotalPaid());
			salesSheetSaleModel.setTotalDiscount(salesSheetSale.getTotalDiscount());
			salesSheetSaleModel.setTotalCharges(salesSheetSale.getTotalCharges());
			salesSheetSaleModel.setTotalDue(salesSheetSale.getTotalDue());
			List<SalesSheetSaleDetailModel> salesSheetSaleDetailModels = new ArrayList<>();
			for(SalesSheetSaleDetail  salesSheetSaleDetail : salesSheetSale.getSalesSheetSaleDetails()) {
				SalesSheetSaleDetailModel salesSheetSaleDetailModel = new SalesSheetSaleDetailModel();
				salesSheetSaleDetailModel.setBarcode(salesSheetSaleDetail.getProduct().getBarcode());
				salesSheetSaleDetailModel.setCode(salesSheetSaleDetail.getProduct().getCode());
				salesSheetSaleDetailModel.setDescription(salesSheetSaleDetail.getProduct().getDescription());
				salesSheetSaleDetailModel.setQty(salesSheetSaleDetail.getQty());
				salesSheetSaleDetailModel.setPrice(salesSheetSaleDetail.getSellingPriceVatIncl());
				salesSheetSaleDetailModels.add(salesSheetSaleDetailModel);				
			}
			salesSheetSaleModel.setSalesSheetSaleDetails(salesSheetSaleDetailModels);
			saleSheetSaleModels.add(salesSheetSaleModel);
			
		}
		salesSheetModel.setSalesSheetSales(saleSheetSaleModels);
		return salesSheetModel;
		
	}
	
	@Override
	public SalesListModel getSalesList(Long id) {
		Optional<SalesList> sl = salesListRepository.findById(id);
		if(!sl.isPresent()) {
			throw new NotFoundException("Sales list not found");
		}
		if(!sl.get().getStatus().equals("PENDING")) {
			throw new InvalidOperationException("Could not display list, not a pending list");
		}
		SalesList salesList = sl.get();
		SalesListModel salesListModel = new SalesListModel();
		
		salesListModel.setNo(salesList.getNo());
		List<SalesListDetailModel> saleListDetailModels = new ArrayList<>();
		for(SalesListDetail salesListDetail : salesList.getSalesListDetails()) {
			SalesListDetailModel salesListDetailModel = new SalesListDetailModel();
			salesListDetailModel.setBarcode(salesListDetail.getProduct().getBarcode());
			salesListDetailModel.setCode(salesListDetail.getProduct().getCode());
			salesListDetailModel.setDescription(salesListDetail.getProduct().getDescription());
			salesListDetailModel.setTotalPacked(salesListDetail.getTotalPacked());
			salesListDetailModel.setSellingPriceVatIncl(salesListDetail.getSellingPriceVatIncl());
			saleListDetailModels.add(salesListDetailModel);			
		}
		salesListModel.setSalesListDetails(saleListDetailModels);
		return salesListModel;
		
	}
	
	@Override
	public Object saveExpense(WMSExpenseModel expenseModel) {
		Optional<SalesList> l = salesListRepository.findByNo(expenseModel.getSalesListNo());
		if(!l.isPresent()) {
			throw new NotFoundException("Operation failed, corresponding sales list not found");
		}
		if(!l.get().getStatus().equals("PENDING")) {
			throw new InvalidOperationException("Corresponding sales list not pending, please login afresh to view pending sales list");
		}
		Optional<SalesSheet> s = salesSheetRepository.findBySalesList(l.get());
		if(!s.isPresent()) {
			throw new NotFoundException("Operation failed, sales sheet not found");
		}
		SalesSheet sheet = salesSheetRepository.saveAndFlush(s.get());
		
		SalesSheetExpense expense = new SalesSheetExpense();
		expense.setDescription(expenseModel.getDescription());
		expense.setAmount(expenseModel.getAmount());
		expense.setSalesSheet(sheet);
		expense = salesSheetExpenseRepository.saveAndFlush(expense);
		
		List<WMSExpenseModel> expenses = new ArrayList<WMSExpenseModel>();
		List<SalesSheetExpense> es = salesSheetExpenseRepository.findAllBySalesSheet(sheet);
		
		for(SalesSheetExpense e : es) {
			WMSExpenseModel mod = new WMSExpenseModel();
			mod.setId(e.getId());
			mod.setDescription(e.getDescription());
			mod.setAmount(e.getAmount());
			mod.setSalesListNo("");
			expenses.add(mod);
		}
		
		return expenses;
		
	}
	
	@Override
	public Object deleteExpense(WMSExpenseModel expenseModel) {
		Optional<SalesList> l = salesListRepository.findByNo(expenseModel.getSalesListNo());
		if(!l.isPresent()) {
			throw new NotFoundException("Operation failed, corresponding sales list not found");
		}
		if(!l.get().getStatus().equals("PENDING")) {
			throw new InvalidOperationException("Corresponding sales list not pending, please login afresh to view pending sales list");
		}
		Optional<SalesSheet> s = salesSheetRepository.findBySalesList(l.get());
		if(!s.isPresent()) {
			throw new NotFoundException("Operation failed, sales sheet not found");
		}
		SalesSheet sheet = salesSheetRepository.saveAndFlush(s.get());
		
		Optional<SalesSheetExpense> se = salesSheetExpenseRepository.findById(expenseModel.getId());
		if(!se.isPresent()) {
			throw new NotFoundException("Expense not found");
		}
		salesSheetExpenseRepository.delete(se.get());
		
		List<WMSExpenseModel> expenses = new ArrayList<WMSExpenseModel>();
		List<SalesSheetExpense> es = salesSheetExpenseRepository.findAllBySalesSheet(sheet);
		
		for(SalesSheetExpense e : es) {
			WMSExpenseModel mod = new WMSExpenseModel();
			mod.setId(e.getId());
			mod.setDescription(e.getDescription());
			mod.setAmount(e.getAmount());
			mod.setSalesListNo("");
			expenses.add(mod);
		}
		
		return expenses;
		
	}
	
	@Override
	public List<SalesExpenseModel> loadSalesExpenses(String salesListNo) {
		Optional<SalesList> sl = salesListRepository.findByNo(salesListNo);
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
		
		List<SalesSheetExpense> sss = ss.get().getSalesSheetExpenses();
		List<SalesExpenseModel> sm = new ArrayList<SalesExpenseModel>();
		for(SalesSheetExpense m : sss) {
			SalesExpenseModel model = new SalesExpenseModel();
			model.setId(m.getId());
			model.setDescription(m.getDescription());
			model.setAmount(m.getAmount());
			sm.add(model);
		}
		return sm;
	}
	
	
	
	@Override
	public Object saveCustomer(SalesAgentCustomerModel c) {
		Optional<SalesAgent> a = salesAgentRepository.findByName(c.getSalesAgent().getName());
		if(!a.isPresent()) {
			throw new NotFoundException("Operation failed, corresponding sales agent not found");
		}
		
		c.setName(c.getName().trim().replaceAll("\\s+", " "));
		Optional<SalesAgentCustomer> sa = salesAgentCustomerRepository.findByNameAndSalesAgent(c.getName(), a.get()); 
		if(sa.isPresent()) {
			throw new NotFoundException("Operation failed, customer already exist for the particular agent.");
		}
		SalesAgentCustomer customer = new SalesAgentCustomer();
		customer.setName(c.getName().trim().replaceAll("\\s+", " "));
		customer.setLocation(c.getLocation());
		customer.setMobile(c.getMobile());
		customer.setSalesAgent(a.get());
		
		customer = salesAgentCustomerRepository.saveAndFlush(customer);
		
		List<SalesAgentCustomerModel> customers = new ArrayList<SalesAgentCustomerModel>();
		List<SalesAgentCustomer> cs = salesAgentCustomerRepository.findAllBySalesAgent(a.get());
		
		for(SalesAgentCustomer e : cs) {
			SalesAgentCustomerModel mod = new SalesAgentCustomerModel();
			mod.setId(e.getId());
			mod.setName(e.getName());
			mod.setLocation(e.getLocation());
			mod.setMobile(e.getMobile());
			mod.setSalesAgent(null);
			customers.add(mod);
		}
		
		return customers;
		
	}
	
	@Override
	public Object deleteCustomer(SalesAgentCustomerModel c) {
		Optional<SalesAgent> a = salesAgentRepository.findByName(c.getSalesAgent().getName());
		if(!a.isPresent()) {
			throw new NotFoundException("Operation failed, corresponding sales agent not found");
		}
		
		
		Optional<SalesAgentCustomer> se = salesAgentCustomerRepository.findById(c.getId());
		if(!se.isPresent()) {
			throw new NotFoundException("Customer not found");
		}
		salesAgentCustomerRepository.delete(se.get());
		
		List<SalesAgentCustomerModel> customers = new ArrayList<SalesAgentCustomerModel>();
		List<SalesAgentCustomer> cs = salesAgentCustomerRepository.findAllBySalesAgent(a.get());
		
		for(SalesAgentCustomer e : cs) {
			SalesAgentCustomerModel mod = new SalesAgentCustomerModel();
			mod.setId(e.getId());
			mod.setName(e.getName());
			mod.setLocation(e.getLocation());
			mod.setMobile(e.getMobile());
			mod.setSalesAgent(null);
			customers.add(mod);
		}
		
		return customers;
		
	}
	
	@Override
	public List<SalesAgentCustomerModel> loadCustomers(SalesAgent salesAgent) {
		
		List<SalesAgentCustomer> ss = salesAgentCustomerRepository.findAllBySalesAgent(salesAgent);
		
		List<SalesAgentCustomerModel> sm = new ArrayList<SalesAgentCustomerModel>();
		for(SalesAgentCustomer m : ss) {
			SalesAgentCustomerModel model = new SalesAgentCustomerModel();
			model.setId(m.getId());
			model.setName(m.getName());
			model.setLocation(m.getLocation());
			model.setMobile(m.getMobile());
			sm.add(model);
		}
		return sm;
	}
}

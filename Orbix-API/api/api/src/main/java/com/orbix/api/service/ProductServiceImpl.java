/**
 * 
 */
package com.orbix.api.service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.orbix.api.domain.Department;
import com.orbix.api.domain.LevelFour;
import com.orbix.api.domain.LevelOne;
import com.orbix.api.domain.LevelThree;
import com.orbix.api.domain.LevelTwo;
import com.orbix.api.accessories.Formater;
import com.orbix.api.domain.Category;
import com.orbix.api.domain.Class;
import com.orbix.api.domain.Product;
import com.orbix.api.domain.ProductPriceChange;
import com.orbix.api.domain.ProductStockCard;
import com.orbix.api.domain.SubCategory;
import com.orbix.api.domain.SubClass;
import com.orbix.api.domain.Supplier;
import com.orbix.api.exceptions.InvalidEntryException;
import com.orbix.api.exceptions.InvalidOperationException;
import com.orbix.api.exceptions.MissingInformationException;
import com.orbix.api.exceptions.NotFoundException;
import com.orbix.api.models.RecordModel;
import com.orbix.api.repositories.CategoryRepository;
import com.orbix.api.repositories.ClassRepository;
import com.orbix.api.repositories.DayRepository;
import com.orbix.api.repositories.DepartmentRepository;
import com.orbix.api.repositories.LevelFourRepository;
import com.orbix.api.repositories.LevelOneRepository;
import com.orbix.api.repositories.LevelThreeRepository;
import com.orbix.api.repositories.LevelTwoRepository;
import com.orbix.api.repositories.ProductRepository;
import com.orbix.api.repositories.SubCategoryRepository;
import com.orbix.api.repositories.SubClassRepository;
import com.orbix.api.repositories.SupplierRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author GODFREY
 *
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProductServiceImpl implements ProductService {
	
	private final ProductRepository productRepository;
	private final SupplierRepository supplierRepository;
	private final DepartmentRepository departmentRepository;
	private final ClassRepository classRepository;
	private final SubClassRepository subClassRepository;
	private final CategoryRepository categoryRepository;
	private final SubCategoryRepository subCategoryRepository;
	private final LevelOneRepository levelOneRepository;
	private final LevelTwoRepository levelTwoRepository;
	private final LevelThreeRepository levelThreeRepository;
	private final LevelFourRepository levelFourRepository;
	private final DayRepository dayRepository;
	private final ProductStockCardService productStockCardService;
	private final ProductPriceChangeService productPriceChangeService;

	@Override
	public Product save(Product p) {
		Product product;
		boolean isNew = false;
		if(p.getId() == null) {
			isNew = true;
			//Add a new Product
			product = p;
			product.setDescription(product.getDescription().replace("+", " "));
			product.setCostPriceVatExcl(Math.round(product.getCostPriceVatExcl() *100.0)/100.0);
			product.setCostPriceVatIncl(Math.round(product.getCostPriceVatIncl() *100.0)/100.0);
			product.setSellingPriceVatExcl(Math.round(product.getSellingPriceVatExcl() *100.0)/100.0);
			product.setSellingPriceVatIncl(Math.round(product.getSellingPriceVatIncl() *100.0)/100.0);			
		}else {
			//Update an existing product
			product = productRepository.findById(p.getId()).get();
			product.setId(p.getId());
			product.setBarcode(p.getBarcode());
			product.setCode(p.getCode());
			product.setDescription(p.getDescription().replace("+", " "));
			product.setShortDescription(p.getShortDescription());
			product.setCommonName(p.getCommonName());
			product.setSellable(p.isSellable());
			product.setActive(p.isActive());
		}
		Optional<Supplier> s = supplierRepository.findByName(p.getSupplier().getName());
		if(!s.isPresent()) {
			throw new MissingInformationException("Could not save product, supplier missing");
		}else {
			supplierRepository.save(s.get());
			product.setSupplier(s.get());
		}
		Optional<Department> d = departmentRepository.findByName(p.getDepartment().getName());
		if(d.isPresent()) {
			departmentRepository.save(d.get());
			product.setDepartment(d.get());
			Optional<Class> c = classRepository.findByName(p.getClass_().getName());
			if(c.isPresent()) {
				classRepository.save(c.get());
				product.setClass_(c.get());
				Optional<SubClass> sb = subClassRepository.findByName(p.getSubClass().getName());
				if(sb.isPresent()) {
					subClassRepository.save(sb.get());
					product.setSubClass(sb.get());
				}else {
					product.setSubClass(null);
				}
			}else {
				product.setClass_(null);
				product.setSubClass(null);
			}
		}else {
			product.setDepartment(null);
			product.setClass_(null);
			product.setSubClass(null);
		}
		
		Optional<Category> c = categoryRepository.findByName(p.getCategory().getName());
		if(c.isPresent()) {
			categoryRepository.save(c.get());
			product.setCategory(c.get());;
			Optional<SubCategory> sc = subCategoryRepository.findByName(p.getSubCategory().getName());
			if(sc.isPresent()) {
				subCategoryRepository.save(sc.get());
				product.setSubCategory(sc.get());
			}else {
				product.setSubCategory(null);
			}
		}else {
			product.setCategory(null);
			product.setSubCategory(null);
		}
		
		Optional<LevelOne> one = levelOneRepository.findByName(p.getLevelOne().getName());
		if(one.isPresent()) {
			levelOneRepository.save(one.get());
			product.setLevelOne(one.get());
		}else {
			product.setLevelOne(null);
		}
		
		Optional<LevelTwo> two = levelTwoRepository.findByName(p.getLevelTwo().getName());
		if(two.isPresent()) {
			levelTwoRepository.save(two.get());
			product.setLevelTwo(two.get());
		}else {
			product.setLevelTwo(null);
		}
		
		Optional<LevelThree> three = levelThreeRepository.findByName(p.getLevelThree().getName());
		if(three.isPresent()) {
			levelThreeRepository.save(three.get());
			product.setLevelThree(three.get());
		}else {
			product.setLevelThree(null);
		}
		
		Optional<LevelFour> four = levelFourRepository.findByName(p.getLevelFour().getName());
		if(four.isPresent()) {
			levelFourRepository.save(four.get());
			product.setLevelFour(four.get());
		}else {
			product.setLevelFour(null);
		}
		
		if(validate(product)) {
			//Continue, else throw validation error
		}		
		product = productRepository.saveAndFlush(product);
		if(product.getCode().equals("NA")) {
			product.setCode(Formater.formatSix(product.getId().toString()));
			product = productRepository.saveAndFlush(product);
		}
		
		if(isNew == true) {
			ProductStockCard stockCard = new ProductStockCard();
			stockCard.setQtyIn(product.getStock());
			stockCard.setProduct(product);
			stockCard.setBalance(product.getStock());
			stockCard.setDay(dayRepository.getCurrentBussinessDay());
			stockCard.setReference("Opening balance");
			productStockCardService.save(stockCard);	
		}		
		return product;		
	}
	
	@Override
	public Product updateInventory(Product p) {
		Product product;
		Optional<Product> prod = productRepository.findById(p.getId());
		double initialStock = prod.get().getStock();
		prod.get().setUom(p.getUom());
		prod.get().setPackSize(p.getPackSize());
		prod.get().setStock(p.getStock());
		prod.get().setMinimumInventory(p.getMinimumInventory());
		prod.get().setMaximumInventory(p.getMaximumInventory());
		prod.get().setDefaultReorderLevel(p.getDefaultReorderLevel());
		prod.get().setDefaultReorderQty(p.getDefaultReorderQty());
		
		product = productRepository.saveAndFlush(prod.get());
		
		if(p.getStock() != initialStock) {
			ProductStockCard stockCard = new ProductStockCard();
			stockCard.setQtyIn(product.getStock());
			stockCard.setProduct(product);
			stockCard.setBalance(product.getStock());
			stockCard.setDay(dayRepository.getCurrentBussinessDay());
			stockCard.setReference("Stock Update");
			productStockCardService.save(stockCard);	
		}		
		return product;		
	}
	
	
	
	@Override
	public Product updatePrices(Product p) {		
		Product product;
		double originalDiscount = 0;
		double originalVat = 0;
		double originalProfitMargin = 0;
		double originalCostPriceVatIncl = 0;
		double originalCostPriceVatExcl = 0;
		double originalSellingPriceVatIncl = 0;
		double originalSellingPriceVatExcl = 0;
		double finalDiscount = 0;
		double finalVat = 0;
		double finalProfitMargin = 0;
		double finalCostPriceVatIncl = 0;
		double finalCostPriceVatExcl = 0;
		double finalSellingPriceVatIncl = 0;
		double finalSellingPriceVatExcl = 0;
		
		Product pr = productRepository.findById(p.getId()).get();
		originalDiscount = pr.getDiscount();
		originalVat = pr.getVat();
		originalProfitMargin = pr.getProfitMargin();
		originalCostPriceVatIncl = Math.round(pr.getCostPriceVatIncl() *100.0)/100.0;
		originalCostPriceVatExcl = Math.round(pr.getCostPriceVatExcl() *100.0)/100.0;
		originalSellingPriceVatIncl = Math.round(pr.getSellingPriceVatIncl() *100.0)/100.0;
		originalSellingPriceVatExcl = Math.round(pr.getSellingPriceVatExcl() *100.0)/100.0;
		
		//Math.round(a * 100.0) / 100.0;
		
		finalDiscount = p.getDiscount();
		finalVat = p.getVat();
		finalProfitMargin = p.getProfitMargin();
		finalCostPriceVatIncl = Math.round(p.getCostPriceVatIncl() *100.0)/100.0;
		finalCostPriceVatExcl = Math.round(p.getCostPriceVatExcl() *100.0)/100.0;
		finalSellingPriceVatIncl = Math.round(p.getSellingPriceVatIncl() *100.0)/100.0;
		finalSellingPriceVatExcl = Math.round(p.getSellingPriceVatExcl() *100.0)/100.0;
		
		pr.setDiscount(finalDiscount);
		pr.setVat(finalVat);
		pr.setProfitMargin(finalProfitMargin);
		pr.setCostPriceVatIncl(finalCostPriceVatIncl);
		pr.setCostPriceVatExcl(finalCostPriceVatExcl);
		pr.setSellingPriceVatIncl(finalSellingPriceVatIncl);
		pr.setSellingPriceVatExcl(finalSellingPriceVatExcl);
		
		product = productRepository.saveAndFlush(pr);
		
		if(originalCostPriceVatIncl != finalCostPriceVatIncl || originalCostPriceVatExcl != finalCostPriceVatExcl || originalSellingPriceVatIncl != finalSellingPriceVatIncl || originalSellingPriceVatExcl != finalSellingPriceVatExcl) {
			ProductPriceChange productPriceChange = new ProductPriceChange();
			productPriceChange.setOriginalCostPriceVatIncl(originalCostPriceVatIncl);
			productPriceChange.setOriginalCostPriceVatExcl(originalCostPriceVatExcl);
			productPriceChange.setOriginalSellingPriceVatIncl(originalSellingPriceVatIncl);
			productPriceChange.setOriginalSellingPriceVatExcl(originalSellingPriceVatExcl);
			
			productPriceChange.setFinalCostPriceVatIncl(finalCostPriceVatIncl);
			productPriceChange.setFinalCostPriceVatExcl(finalCostPriceVatExcl);
			productPriceChange.setFinalSellingPriceVatIncl(finalSellingPriceVatIncl);
			productPriceChange.setFinalSellingPriceVatExcl(finalSellingPriceVatExcl);
			
			productPriceChange.setProduct(product);
			productPriceChange.setDay(dayRepository.getCurrentBussinessDay());
			productPriceChange.setReference("Price changed in product update");
			
			productPriceChangeService.save(productPriceChange);
		}		
		return product;		
	}
	
	
	
	

	@Override
	public Product get(Long id) {
		return productRepository.findById(id).get();
	}

	@Override
	public Product getByBarcode(String barcode) {
		Optional<Product> p = productRepository.findByBarcode(barcode);
		if(!p.isPresent()) {
			throw new NotFoundException("Product not found");
		}
		return p.get();
	}

	@Override
	public Product getByCode(String code) {
		Optional<Product> p = productRepository.findByCode(code);
		if(!p.isPresent()) {
			throw new NotFoundException("Product not found");
		}
		return p.get();
	}

	@Override
	public Product getByDescription(String description) {
		Optional<Product> p = productRepository.findByDescription(description);
		if(!p.isPresent()) {
			throw new NotFoundException("Product not found");
		}
		return p.get();
	}

	@Override
	public Product getByCommonName(String commonName) {
		Optional<Product> p = productRepository.findByCommonName(commonName);
		if(!p.isPresent()) {
			throw new NotFoundException("Product not found");
		}
		return p.get();
	}

	@Override
	public boolean delete(Product product) {
		if(!allowDelete(product)) {
			throw new InvalidOperationException("Deleting this product is not allowed");
		}
		productRepository.delete(product);
		return true;
	}

	@Override
	public List<Product> getAll() {
		return productRepository.findAll();
	}
	
	private boolean validate(Product product) {
		if(product.getCode().equals("")) {
			throw new MissingInformationException("Product Code required");
		}
		if(product.getDescription().equals("")) {
			throw new MissingInformationException("Product Description required");
		}
		if(product.getSupplier().getName().equals("")) {
			throw new MissingInformationException("Supplier information required");
		}
		if(product.getDiscount() < 0 || product.getDiscount() > 100) {
			throw new InvalidEntryException("Discount ratio should be between 0 and 100");
		}
		if(product.getVat() < 0 || product.getVat() > 100) {
			throw new InvalidEntryException("VAT ratio should be between 0 and 100");
		}
		if(product.getProfitMargin() < 0 ) {
			throw new InvalidEntryException("Profit margin should be more than 0");
		}
		return true;
	}
	
	private boolean allowDelete(Product product) {
		/**
		 * Check product if it can be deleted
		 */
		return false;
	}

	@Override
	public List<String> getSellableDescriptions() {
		return productRepository.getSellableProductDescriptions();	
	}
	
	@Override
	public RecordModel requestProductCode() {
		Long id = 1L;
		try {
			id = productRepository.getLastId() + 1;
		}catch(Exception e) {}
		RecordModel model = new RecordModel();
		model.setCode("P"+id.toString());		
		return model;
	}	
}

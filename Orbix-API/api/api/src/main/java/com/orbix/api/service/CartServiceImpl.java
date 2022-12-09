/**
 * 
 */
package com.orbix.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.orbix.api.accessories.Formater;
import com.orbix.api.domain.Cart;
import com.orbix.api.domain.CartDetail;
import com.orbix.api.domain.CartHeld;
import com.orbix.api.domain.CartHeldDetail;
import com.orbix.api.domain.Payment;
import com.orbix.api.domain.Product;
import com.orbix.api.domain.ProductStockCard;
import com.orbix.api.domain.Receipt;
import com.orbix.api.domain.ReceiptDetail;
import com.orbix.api.domain.Sale;
import com.orbix.api.domain.SaleDetail;
import com.orbix.api.domain.Till;
import com.orbix.api.exceptions.InvalidEntryException;
import com.orbix.api.exceptions.InvalidOperationException;
import com.orbix.api.exceptions.NotFoundException;
import com.orbix.api.repositories.CartDetailRepository;
import com.orbix.api.repositories.CartHeldDetailRepository;
import com.orbix.api.repositories.CartHeldRepository;
import com.orbix.api.repositories.CartRepository;
import com.orbix.api.repositories.DayRepository;
import com.orbix.api.repositories.PackingListDetailRepository;
import com.orbix.api.repositories.PackingListRepository;
import com.orbix.api.repositories.ProductRepository;
import com.orbix.api.repositories.ReceiptDetailRepository;
import com.orbix.api.repositories.ReceiptRepository;
import com.orbix.api.repositories.SaleDetailRepository;
import com.orbix.api.repositories.SaleRepository;
import com.orbix.api.repositories.TillRepository;
import com.orbix.api.repositories.UserRepository;

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
public class CartServiceImpl implements CartService {
	
	private final CartRepository cartRepository;
	private final CartDetailRepository cartDetailRepository;
	private final CartHeldRepository cartHeldRepository;
	private final CartHeldDetailRepository cartHeldDetailRepository;
	private final ReceiptRepository receiptRepository;
	private final ReceiptDetailRepository receiptDetailRepository;
	private final TillRepository tillRepository;
	
	private final UserService userService;
	private final DayRepository dayRepository;
	private final ProductRepository productRepository;
	private final ProductStockCardService productStockCardService;
	private final SaleRepository saleRepository;
	private final SaleDetailRepository saleDetailRepository;
	private final VoidedService voidedService;
	
	
	@Override
	public Cart createCart(Till till) {
		Optional<Cart> c = cartRepository.findByTill(till);
		if(c.isPresent()) {
			throw new InvalidOperationException("Could not create a new work space. An active work space already exists");
		}
		Cart newCart = new Cart();
		newCart.setNo("NA");
		newCart.setTill(till);
		newCart.setActive(true);
		newCart = cartRepository.save(newCart);
		if(newCart.getNo().equals("NA")) {
			newCart.setNo(generateCartNo(newCart));
			newCart = cartRepository.save(newCart);
		}	
		return newCart;
	}
	@Override
	public Cart loadCart(Till till) {
		Optional<Cart> c = cartRepository.findByTillAndActive(till, true);
		if(!c.isPresent()) {
			throw new NotFoundException("No active work space exist");
		}
		return c.get();
	}
	
	
	@Override
	public Cart holdCart(Till till) {
		Optional<Cart> c = cartRepository.findByTillAndActive(till, true);
		if(!c.isPresent()) {
			throw new NotFoundException("No active work space exist");
		}
		int count = 0;
		double amount = 0;
		for(CartDetail cartDetail : c.get().getCartDetails()) {
			count = count + 1;
			if(cartDetail.isVoided() == false) {
				amount = amount + cartDetail.getSellingPriceVatIncl()*cartDetail.getQty()*(1 - (cartDetail.getDiscount()/100));
			}		
		}
		if(count == 0) {
			throw new InvalidOperationException("Can not hold an empty cart");
		}
		
		CartHeld cartHeld = new CartHeld();
		cartHeld.setNo(c.get().getNo());
		cartHeld.setActive(c.get().isActive());
		cartHeld.setTill(c.get().getTill());
		cartHeld.setAmount(amount);
		cartHeldRepository.saveAndFlush(cartHeld);
		
		for(CartDetail cartDetail : c.get().getCartDetails()) {
			CartHeldDetail cartHeldDetail = new CartHeldDetail();
			cartHeldDetail.setBarcode(cartDetail.getBarcode());
			
			cartHeldDetail.setCode(cartDetail.getCode());
			cartHeldDetail.setDescription(cartDetail.getDescription());
			cartHeldDetail.setCostPriceVatExcl(cartDetail.getCostPriceVatExcl());
			cartHeldDetail.setCostPriceVatIncl(cartDetail.getCostPriceVatIncl());
			cartHeldDetail.setSellingPriceVatExcl(cartDetail.getSellingPriceVatExcl());
			cartHeldDetail.setSellingPriceVatIncl(cartDetail.getSellingPriceVatIncl());
			cartHeldDetail.setDiscount(cartDetail.getDiscount());
			cartHeldDetail.setQty(cartDetail.getQty());			
			cartHeldDetail.setVat(cartDetail.getVat());
			cartHeldDetail.setVatGroup(cartDetail.getVatGroup());
			cartHeldDetail.setVoided(cartDetail.isVoided());
			cartHeldDetail.setCartHeld(cartHeld);
			cartHeldDetailRepository.saveAndFlush(cartHeldDetail);
		}
		cartRepository.delete(c.get());
		Cart newCart = new Cart();
		newCart.setNo("NA");
		newCart.setTill(till);
		newCart.setActive(true);
		newCart = cartRepository.save(newCart);
		if(newCart.getNo().equals("NA")) {
			newCart.setNo(generateCartNo(newCart));
			newCart = cartRepository.save(newCart);
		}	
		return newCart;
	}
	@Override
	public Cart unholdCartHeld(Till till, Long id) {
		Optional<Cart> c = cartRepository.findByTillAndActive(till, true);
		if(!c.isPresent()) {
			throw new NotFoundException("No active work space exist");
		}
		int count = 0;
		for(CartDetail cartDetail : c.get().getCartDetails()) {
			count = count + 1;
		}
		if(count > 0) {
			throw new InvalidOperationException("Can not unhold to the current cart. Please empty the current cart");
		}
		Optional<CartHeld> ch = cartHeldRepository.findById(id);
		if(!ch.isPresent()) {
			throw new NotFoundException("Can not unhold, cart does not exist");
		}
		
		for(CartHeldDetail cartHeldDetail : ch.get().getCartHeldDetails()) {
			CartDetail cartDetail = new CartDetail();
			cartDetail.setBarcode(cartHeldDetail.getBarcode());			
			cartDetail.setCode(cartHeldDetail.getCode());
			cartDetail.setDescription(cartHeldDetail.getDescription());
			cartDetail.setCostPriceVatExcl(cartHeldDetail.getCostPriceVatExcl());
			cartDetail.setCostPriceVatIncl(cartHeldDetail.getCostPriceVatIncl());
			cartDetail.setSellingPriceVatExcl(cartHeldDetail.getSellingPriceVatExcl());
			cartDetail.setSellingPriceVatIncl(cartHeldDetail.getSellingPriceVatIncl());
			cartDetail.setDiscount(cartHeldDetail.getDiscount());
			cartDetail.setQty(cartHeldDetail.getQty());			
			cartDetail.setVat(cartHeldDetail.getVat());
			cartDetail.setVatGroup(cartHeldDetail.getVatGroup());
			cartDetail.setVoided(cartHeldDetail.isVoided());
			cartDetail.setCart(c.get());
			cartDetailRepository.saveAndFlush(cartDetail);
		}
		cartHeldRepository.delete(ch.get());
		return c.get();
	}
	
	
	@Override
	public List<CartHeld> showCartsHeld(Till till) {
		Optional<Till> t = tillRepository.findByNo(till.getNo());
		if(!t.isPresent()) {
			throw new NotFoundException("Till not found");
		}	
		return cartHeldRepository.findByTill(t.get());
	}
	
	private String generateCartNo(Cart cart) {
		Long number = cart.getId();		
		return number.toString();
	}
	@Override
	public boolean deactivateCart(Cart cart) {
		cart.setActive(false);
		cartRepository.save(cart);
		return true;
	}
	@Override
	public Cart activateCart(Cart cart, Till till) {
		Optional<Cart> c = cartRepository.findByTillAndActive(till, true);
		c.get().setActive(false);
		cartRepository.saveAndFlush(cart);	
		Optional<Cart> c2 = cartRepository.findByTillAndActive(till, true);
		c2.get().setActive(true);
		return cartRepository.saveAndFlush(c2.get());
	}
	@Override
	public boolean addCartDetail(CartDetail cartDetail) {
		Optional<Cart> c = cartRepository.findByNo(cartDetail.getCart().getNo());
		if(!c.isPresent()) {
			throw new NotFoundException("Could not find work space");
		}
		Optional<CartDetail> d = cartDetailRepository.findByCodeAndCartAndVoided(cartDetail.getCode(), c.get(), false);		
		if(d.isPresent()) {			
			d.get().setQty(d.get().getQty() + cartDetail.getQty());
			cartDetailRepository.saveAndFlush(d.get());
			return true;
		}else {	
			double cpIncl = 0;
			double cpExcl = 0;			
			double spExcl = 0;
			Optional<Product> p = productRepository.findByCode(cartDetail.getCode());
			if(p.isPresent()) {
				/**
				 * This values are fetched from product file since they are not provided in the cart
				 * It can be removed once that option is included
				 */
				cpIncl = p.get().getCostPriceVatIncl();
				cpExcl = p.get().getCostPriceVatExcl();
				spExcl = p.get().getSellingPriceVatExcl();
			}		
			CartDetail detail = new CartDetail();
			detail.setCart(c.get());
			detail.setBarcode(cartDetail.getBarcode());
			detail.setCode(cartDetail.getCode());
			detail.setDescription(cartDetail.getDescription());
			detail.setCostPriceVatExcl(cpExcl);
			detail.setCostPriceVatIncl(cpIncl);
			detail.setSellingPriceVatExcl(spExcl);
			detail.setSellingPriceVatIncl(cartDetail.getSellingPriceVatIncl());
			detail.setQty(cartDetail.getQty());
			detail.setDiscount(cartDetail.getDiscount());
			detail.setVat(cartDetail.getVat());
			detail.setVatGroup(p.get().getVatGroup());
			detail.setVoided(false);
			cartDetailRepository.saveAndFlush(detail);
			return true;
		}
	}
	@Override
	public boolean updateQty(CartDetail cartDetail) {
		Optional<CartDetail> d = cartDetailRepository.findById(cartDetail.getId());
		if(!d.isPresent()) {
			throw new NotFoundException("Detail not found");
		}
		if(cartDetail.getQty() <= 0) {
			throw new InvalidEntryException("Invalid entry");
		}
		d.get().setQty(cartDetail.getQty());
		cartDetailRepository.saveAndFlush(d.get());
		return true;
	}
	@Override
	public boolean updateDiscount(CartDetail cartDetail) {
		Optional<CartDetail> d = cartDetailRepository.findById(cartDetail.getId());
		if(!d.isPresent()) {
			throw new NotFoundException("Detail not found");
		}
		if(cartDetail.getDiscount() < 0) {
			throw new InvalidEntryException("Invalid entry");
		}
		d.get().setDiscount(cartDetail.getDiscount());
		cartDetailRepository.saveAndFlush(d.get());
		return true;
	}
	@Override
	public boolean voidd(CartDetail cartDetail, HttpServletRequest request) {
		Optional<CartDetail> d = cartDetailRepository.findById(cartDetail.getId());
		if(!d.isPresent()) {
			throw new NotFoundException("Detail not found");
		}		
		d.get().setVoided(true);
		cartDetailRepository.save(d.get());
		voidedService.createVoid(d.get().getCart().getTill(), d.get().getBarcode(), d.get().getCode(), d.get().getDescription(), d.get().getQty(), d.get().getSellingPriceVatIncl(), request, dayRepository.getCurrentBussinessDay().getId(), d.get().getId());
		return true;
	}
	
	@Override
	public boolean unvoid(CartDetail cartDetail) {
		Optional<CartDetail> d = cartDetailRepository.findById(cartDetail.getId());
		if(!d.isPresent()) {
			throw new NotFoundException("Detail not found");
		}
		Long dId = d.get().getId();
		Optional<CartDetail> dv = cartDetailRepository.findByCodeAndCartAndVoided(cartDetail.getCode(), d.get().getCart(), false);
		if(dv.isPresent()) {
			dv.get().setQty(dv.get().getQty() + d.get().getQty());
			cartDetailRepository.saveAndFlush(dv.get());
			cartDetailRepository.delete(d.get());
		}else {
			d.get().setVoided(false);
			cartDetailRepository.saveAndFlush(d.get());
		}		
		voidedService.deleteVoid(dId);		
		return true;
	}
	
	@Override
	public Receipt pay(Payment payment, Cart cart, HttpServletRequest request) {
		//First validate values, will be skipped for the time being
		
		//define a sale, define a receipt
		
		//To add: deduct from stocks, and update stock cards
		//Register till position
		Till till = cart.getTill();
		till.setCash(till.getCash() + payment.getCash());
		till.setCap(till.getCash() + payment.getCap());
		till.setCheque(till.getCheque() + payment.getCheque());
		till.setCrCard(till.getCrCard() + payment.getCrCard());
		till.setCrNote(till.getCrNote() + payment.getCrNote());
		till.setDeposit(till.getDeposit() + payment.getDeposit());
		till.setLoyalty(till.getLoyalty() + payment.getLoyalty());
		till.setMobile(till.getMobile() + payment.getMobile());
		till.setOther(till.getOther() + payment.getOther());
		tillRepository.saveAndFlush(till);
		
		List<CartDetail> cartDetails = cart.getCartDetails();		
		Receipt receipt = new Receipt();
		Receipt returnReceipt = new Receipt();
		receipt.setNo("NA");
		receipt.setCreatedAt(dayRepository.getCurrentBussinessDay().getId());
		receipt.setCreatedBy(userService.getUserId(request));
		receipt.setTill(till);
		receipt.setCash(payment.getCash());
		receipt.setCap(payment.getCap());
		receipt.setCheque(payment.getCheque());
		receipt.setCrCard(payment.getCrCard());
		receipt.setCrNote(payment.getCrNote());
		receipt.setDeposit(payment.getDeposit());
		receipt.setLoyalty(payment.getLoyalty());
		receipt.setMobile(payment.getMobile());
		receipt.setOther(payment.getOther());		
		receipt = receiptRepository.saveAndFlush(receipt);
		if(receipt.getNo().equals("NA")) {
			receipt.setNo(generateReceiptNo(receipt));
			receipt = receiptRepository.save(receipt);
		}
		returnReceipt = receipt;
		
		List<ReceiptDetail> rds = new ArrayList<ReceiptDetail>();
		for(CartDetail cartDetail : cartDetails) {
			if(cartDetail.isVoided() == false) {
				ReceiptDetail rd = new ReceiptDetail();				
				rd.setReceipt(receipt);
				rd.setDescription(cartDetail.getDescription());				
				rd.setQty(cartDetail.getQty());
				rd.setCostPriceVatIncl(cartDetail.getCostPriceVatIncl());
				rd.setCostPriceVatExcl(cartDetail.getCostPriceVatExcl());
				rd.setSellingPriceVatIncl(cartDetail.getSellingPriceVatIncl());
				rd.setSellingPriceVatExcl(cartDetail.getSellingPriceVatExcl());
				rd.setDiscount((cartDetail.getDiscount()/100) * cartDetail.getSellingPriceVatIncl());
				rd.setTax((cartDetail.getVat()/100) * cartDetail.getSellingPriceVatIncl());
				rd.setVatGroup(cartDetail.getVatGroup());
				rds.add(rd);
				receiptDetailRepository.saveAndFlush(rd);				
			}			
		}
		returnReceipt.setReceiptDetails(rds);
		
		Sale sale = new Sale();
		
		sale.setCreatedAt(dayRepository.getCurrentBussinessDay().getId());
		sale.setCreatedBy(userService.getUserId(request));
		sale.setDay(dayRepository.getCurrentBussinessDay());
		sale.setReference("POS sales Receipt# "+receipt.getNo());
		sale.setTill(till);
		receiptRepository.saveAndFlush(returnReceipt);
		sale.setReceipt(returnReceipt);
		sale = saleRepository.saveAndFlush(sale);
		for(CartDetail cartDetail : cartDetails) {
			if(cartDetail.isVoided() == false) {
				SaleDetail sd = new SaleDetail();
				Optional<Product> p = productRepository.findByCode(cartDetail.getCode());
				if(!p.isPresent()) {
					continue;
				}
				sd.setProduct(p.get());
				sd.setSale(sale);
				sd.setQty(cartDetail.getQty());
				sd.setCostPriceVatIncl(cartDetail.getCostPriceVatIncl());
				sd.setCostPriceVatExcl(cartDetail.getCostPriceVatExcl());
				sd.setSellingPriceVatIncl(cartDetail.getSellingPriceVatIncl());
				sd.setSellingPriceVatExcl(cartDetail.getSellingPriceVatExcl());
				sd.setDiscount((cartDetail.getDiscount()/100) * cartDetail.getSellingPriceVatIncl());
				sd.setTax((cartDetail.getVat()/100) * cartDetail.getSellingPriceVatIncl());
				saleDetailRepository.saveAndFlush(sd);
				
				/**
				 * Grab stock qty and update stock
				 */
				Product product = p.get();
				double stock = product.getStock() - (cartDetail.getQty());
				product.setStock(stock);
				productRepository.saveAndFlush(product);
				/**
				 * Create stock card
				 */
				ProductStockCard stockCard = new ProductStockCard();
				stockCard.setQtyOut(cartDetail.getQty());
				stockCard.setProduct(product);
				stockCard.setBalance(stock);
				stockCard.setDay(dayRepository.getCurrentBussinessDay());
				stockCard.setReference("POS Sales. Ref #: "+ receipt.getNo());
				productStockCardService.save(stockCard);
			}else {
				voidedService.checkVoid(cartDetail.getId());
			}
		}
		for(CartDetail cartDetail : cartDetails) {				
			cartDetailRepository.delete(cartDetail);
		}
		cartRepository.delete(cart);
		return returnReceipt;
	}
	
	private String generateReceiptNo(Receipt receipt) {
		Long number = receipt.getId();		
		String sNumber = number.toString();
		return "R"+sNumber;
		//return "RCPT-"+Formater.formatNine(sNumber);
	}
	
	
}

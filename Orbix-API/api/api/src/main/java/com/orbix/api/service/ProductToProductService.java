/**
 * 
 */
package com.orbix.api.service;

import java.util.List;

import com.orbix.api.domain.ProductToProduct;
import com.orbix.api.domain.ProductToProductFinal;
import com.orbix.api.domain.ProductToProductInitial;
import com.orbix.api.models.ProductToProductFinalModel;
import com.orbix.api.models.ProductToProductInitialModel;
import com.orbix.api.models.ProductToProductModel;
import com.orbix.api.models.RecordModel;

/**
 * @author Godfrey
 *
 */
public interface ProductToProductService {
	ProductToProductModel save(ProductToProduct productToProduct);
	ProductToProductModel get(Long id);
	ProductToProductModel getByNo(String no);
	boolean delete(ProductToProduct productToProduct);
	List<ProductToProductModel>getAllVisible();
	ProductToProductInitialModel saveInitial(ProductToProductInitial productToProductInitial);
	ProductToProductFinalModel saveFinal(ProductToProductFinal productToProductFinal);
	ProductToProductInitialModel getInitial(Long id);
	ProductToProductFinalModel getFinal(Long id);
	boolean deleteInitial(ProductToProductInitial productToProductInitial);
	boolean deleteFinal(ProductToProductFinal productToProductFinal);
	List<ProductToProductInitialModel>getAllInitials(ProductToProduct productToProduct);
	List<ProductToProductFinalModel>getAllFinals(ProductToProduct productToProduct);
	boolean archive(ProductToProduct productToProduct);
	boolean archiveAll();
	ProductToProductModel post(ProductToProduct productToProduct);
	RecordModel requestPTPNo();
}

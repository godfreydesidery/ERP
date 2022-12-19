/**
 * 
 */
package com.orbix.api.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orbix.api.domain.Product;
import com.orbix.api.domain.ProductToProduct;
import com.orbix.api.domain.ProductToProductInitial;

/**
 * @author Godfrey
 *
 */
public interface ProductToProductInitialRepository extends JpaRepository<ProductToProductInitial, Long> {

	/**
	 * @param productToProduct
	 * @return
	 */
	List<ProductToProductInitial> findByProductToProduct(ProductToProduct productToProduct);

	/**
	 * @param product
	 * @param productToProduct
	 * @return
	 */
	Optional<ProductToProductInitial> findByProductAndProductToProduct(Product product,
			ProductToProduct productToProduct);

}

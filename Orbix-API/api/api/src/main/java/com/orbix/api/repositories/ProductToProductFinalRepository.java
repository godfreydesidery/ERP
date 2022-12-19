/**
 * 
 */
package com.orbix.api.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orbix.api.domain.Product;
import com.orbix.api.domain.ProductToProduct;
import com.orbix.api.domain.ProductToProductFinal;

/**
 * @author Godfrey
 *
 */
public interface ProductToProductFinalRepository extends JpaRepository<ProductToProductFinal, Long> {

	/**
	 * @param productToProduct
	 * @return
	 */
	List<ProductToProductFinal> findByProductToProduct(ProductToProduct productToProduct);

	/**
	 * @param product
	 * @param productToProduct
	 * @return
	 */
	Optional<ProductToProductFinal> findByProductAndProductToProduct(Product product,
			ProductToProduct productToProduct);

}


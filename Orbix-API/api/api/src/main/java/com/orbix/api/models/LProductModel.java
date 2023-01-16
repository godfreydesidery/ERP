/**
 * 
 */
package com.orbix.api.models;

import lombok.Data;

/**
 * @author Godfrey
 *
 */
@Data
public class LProductModel {
	Long id;
	String barcode;
	String code;
	String description;
	double price;
	double available;
}

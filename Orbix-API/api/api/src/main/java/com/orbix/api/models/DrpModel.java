/**
 * 
 */
package com.orbix.api.models;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

/**
 * @author Godfrey
 *
 */
@Data
public class DrpModel {
	Long id = null;
	String no = "";
	String status = "";
	LocalDate purchaseDate = null;
	String created = "";
	String approved = "";
	String comments = "";
	List<DrpDetailModel> drpDetails;
}

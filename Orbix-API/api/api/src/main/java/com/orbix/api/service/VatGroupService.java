/**
 * 
 */
package com.orbix.api.service;

import java.util.List;

import com.orbix.api.domain.Supplier;
import com.orbix.api.domain.VatGroup;
import com.orbix.api.models.RecordModel;

/**
 * @author Godfrey
 *
 */
public interface VatGroupService {
	VatGroup save(VatGroup vatGroup);
	VatGroup get(Long id);
	VatGroup getByCode(String code);
	boolean delete(VatGroup vatGroup);
	List<VatGroup>getAll(); //edit this to limit the number, for perfomance.
	List<String> getCodes();
}

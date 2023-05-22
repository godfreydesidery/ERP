/**
 * 
 */
package com.orbix.api.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.orbix.api.domain.WebPos;
import com.orbix.api.domain.WebPosApproveData;
import com.orbix.api.models.WebPosModel;

/**
 * @author Godfrey
 *
 */
public interface WebPosService {
	WebPosModel save(WebPos webPos);
	WebPosModel get(Long id);
	boolean delete(WebPos webPos);
	List<WebPosModel>getAll();	
	boolean approve(WebPosApproveData approveData, HttpServletRequest request);
}

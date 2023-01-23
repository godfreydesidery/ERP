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
public class LSalesListObjectModel {
	Long salesAgentId;
	String salesAgentName;
	String[] salesListNo;
	String accessToken;
}

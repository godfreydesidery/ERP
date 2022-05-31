/**
 * 
 */
package com.orbix.api.security;

/**
 * @author GODFREY
 *
 */
public class Object_ {
	
	/**
	 * List of authorities not allowed
	 * CREATE READ UPDATE DELETE ACTIVATE APPROVE PRINT CANCEL
	 * 
	 * Format: OBJECT-LIST OF NOT ALLOWED AUTHORITIES
	 */
	
	public static String ADMIN = "ADMIN";
	public static String USER = "USER-APPROVE PRINT CANCEL";
	public static String ROLE = "ROLE-ACTIVATE APPROVE PRINT CANCEL";
	public static String TILL = "TILL-APPROVE PRINT CANCEL";
	public static String COMPANY_PROFILE = "COMPANY_PROFILE-DELETE ACTIVATE APPROVE PRINT CANCEL";
	public static String CUSTOMER = "CUSTOMER-APPROVE PRINT CANCEL";
	public static String EMPLOYEE = "EMPLOYEE-APPROVE PRINT CANCEL";
	public static String SUPPLIER = "SUPPLIER-APPROVE PRINT CANCEL";
	public static String PRODUCT = "PRODUCT-APPROVE PRINT CANCEL";
	public static String LPO = "LPO-ACTIVATE";
	public static String GRN = "GRN-ACTIVATE";
	public static String SALES_INVOICE = "SALES_INVOICE-ACTIVATE";
	public static String QUOTATION = "QUOTATION-ACTIVATE";
	public static String PACKING_LIST = "PACKING_LIST-ACTIVATE";
	public static String SALES_LIST = "SALES_LIST-ACTIVATE";
	public static String SALES_RECEIPT = "SALES_RECEIPT-ACTIVATE";
	public static String ALLOCATION = "ALLOCATION-UPDATE DELETE ACTIVATE APPROVE PRINT CANCEL";
	public static String DEBT_RECEIPT = "DEBT_RECEIPT-ACTIVATE";
	public static String MATERIAL = "MATERIAL-APPROVE PRINT CANCEL";
	public static String DRP = "DRP";
	public static String DEPARTMENT = "DEPARTMENT";
	public static String CLASS = "CLASS";
	public static String SUB_CLASS = "SUB_CLASS";
	public static String CATEGORY = "CATEGORY";
	public static String SUB_CATEGORY = "SUB_CATEGORY";
	public static String GROUP = "GROUP";
	public static String SALES_LEDGE = "SALES_LEDGE";
	public static String SALES_JOURNAL = "SALES_JOURNAL";
	public static String BILL_REPRINT = "BILL_REPRINT";
	public static String CUSTOMER_RETURN = "CUSTOMER_RETURN";
	public static String CUSTOMER_CLAIM = "CUSTOMER_CLAIM";
	public static String RETURN_TO_VENDOR = "RETURN_TO_VENDOR";
	public static String VENDOR_CREDIT_NOTE = "VENDOR_CREDIT_NOTE";
	public static String CUSTOMER_CREDIT_NOTE = "CUSTOMER_CREDIT_NOTE";
	public static String PRODUCTION = "PRODUCTION";
}

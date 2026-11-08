package org.iottree.driver.zigbee.zcl.defs;

import java.util.HashMap;

public class ManufacturerSpec
{
	public static HashMap<String,Integer> SPEC_MANUF_IEEE_PREFIX_TO_CODE = new HashMap<>() ;
	
	static
	{
		SPEC_MANUF_IEEE_PREFIX_TO_CODE.put("54EF44",ManufacturerCode.LUMI_UNITED_TECHOLOGY_LTD_SHENZHEN) ;
		SPEC_MANUF_IEEE_PREFIX_TO_CODE.put("04CF8C",ManufacturerCode.LUMI_UNITED_TECHOLOGY_LTD_SHENZHEN) ;
	}
}

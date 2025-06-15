package org.iottree.core.util.cer;

import java.io.File;
import java.io.IOException;
import java.security.KeyPair;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.milo.opcua.stack.core.util.SelfSignedCertificateBuilder;
import org.eclipse.milo.opcua.stack.core.util.SelfSignedCertificateGenerator;
import org.iottree.core.Config;
import org.iottree.core.util.Convert;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 统一证书管理
 * 
 * @author jason.zhu
 *
 */
public class CerManager
{
	private static CerManager instance = null ;
	
	public static CerManager getInstance()
	{
		if(instance!=null)
			return instance ;
		
		synchronized(CerManager.class)
		{
			if(instance!=null)
				return instance ;
			
			instance = new CerManager() ;
			return instance ;
		}
	}
	
	static File calCatDir(String cat_name)
	{
		return new File(Config.getDataDirBase()+"/auth/cer/"+cat_name+"/") ;
	}
	
	
	ArrayList<CerCat> cats = new ArrayList<>() ;
	
	CerCat def_cat = null ;
	
	private CerManager()
	{
		def_cat = new CerCat("opc_ua_client","OPC UA Client") ;
		cats.add(def_cat) ;
	}
	
	public CerCat getDefaultCerCat()
	{
		return this.def_cat ;
	}
	
	public List<CerCat> listCerCat()
	{
		return this.cats;
	}
	
	public CerCat getCat(String cat_name)
	{
		for(CerCat cc:this.cats)
		{
			if(cc.name.equals(cat_name))
				return cc ;
		}
		return null ;
	}
	
	public CerItem getItem(String cat,String id)
	{
		CerCat cc = this.getCat(cat) ;
		if(cc==null)
			return null ;
		return cc.getCerItemById(id) ;
	}
	
//	public List<CerItem> listCerItems(String cat_name)
//	{
//		CerCat cc = this.getCat(cat_name) ;
//		if(cc==null)
//			return null ;
//		
//	}
}

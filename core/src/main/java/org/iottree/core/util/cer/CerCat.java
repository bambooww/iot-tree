package org.iottree.core.util.cer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.time.Period;
import java.util.ArrayList;

import org.eclipse.milo.opcua.stack.core.util.SelfSignedCertificateBuilder;
import org.eclipse.milo.opcua.stack.core.util.SelfSignedCertificateGenerator;
import org.iottree.core.util.Convert;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 证书分类
 * 
 * @author jason.zhu
 *
 */
public class CerCat
{
	String name;

	String title;

	ArrayList<CerItem> cerItems = null;

	public CerCat(String name, String title)
	{
		this.name = name;
		this.title = title;
	}

	public String getName()
	{
		return this.name;
	}

	public String getTitle()
	{
		return this.title;
	}

	public ArrayList<CerItem> getCerItems()
	{
		if (this.cerItems != null)
			return this.cerItems;

		try
		{
			return this.cerItems = loadItems(this.name);
		}
		catch ( Exception ee)
		{
			ee.printStackTrace();
			return null;
		}
	}

	public CerItem getCerItemById(String id)
	{
		for (CerItem ci : this.getCerItems())
		{
			if (id.equals(ci.autoId))
				return ci;
		}
		return null;
	}
	
//	public CerItem addCerItem(String title, JSONObject cer_pm)
//	{
//		CerItem ci = new CerItem(title);
//
//		return ci;
//	}

	public CerItem addCerItem(String title, String org, String org_depart) throws Exception
	{
		CerItem ci = new CerItem(title,org,org_depart);

		// X509Certificate cert = createOpcUaClientCer(title,org,org_depart) ;
		// 保存到密钥库
		File dir = CerManager.calCatDir(this.name);
		if(!dir.exists())
			dir.mkdirs();
		File key_file = new File(dir, ci.getAutoId() + ".pfx");

		KeyPair clientKeyPair = SelfSignedCertificateGenerator.generateRsaKeyPair(2048);
		SelfSignedCertificateBuilder clientCertBuilder = new SelfSignedCertificateBuilder(clientKeyPair)
				.setCommonName(name).setOrganization(org).setOrganizationalUnit(org_depart)
				.setValidityPeriod(Period.ofYears(3)).setApplicationUri("urn:iottree:opcua:client");

		X509Certificate cert = clientCertBuilder.build();
		KeyStore keyStore = KeyStore.getInstance("PKCS12");
		keyStore.load(null, null);
		keyStore.setKeyEntry("client-cert", clientKeyPair.getPrivate(), "password".toCharArray(),
				new X509Certificate[] { cert });

		try (OutputStream out = new FileOutputStream(key_file))
		{
			keyStore.store(out, "password".toCharArray());
		}

		this.cerItems.add(ci) ;
		saveIdx() ;
		return ci;
	}
	
	public CerItem delCerItem(String item_id) throws Exception
	{
		CerItem ci = this.getCerItemById(item_id) ;
		if(ci==null)
			return null ;
		File dir = CerManager.calCatDir(this.name);
		File key_file = new File(dir, ci.getAutoId() + ".pfx");
		if(!this.cerItems.remove(ci))
			return null ;
		
		this.saveIdx();
		key_file.delete() ;
		return ci ;
	}

	ArrayList<CerItem> loadItems(String cat_name) throws IOException
	{
		ArrayList<CerItem> rets = new ArrayList<>() ;
		
		File dir = CerManager.calCatDir(cat_name) ;
		if(!dir.exists())
			return rets ;
		
		File idxf = new File(dir,"_idx.json") ;
		if(!idxf.exists())
			return rets ;
		
		String idxstr = Convert.readFileTxt(idxf) ;
		//JSONObject mjo = new JSONObject(idxstr) ;
		//this.name = mjo.getString("n") ;
		//this.title = mjo.getString("t") ;
		JSONArray jarr = new JSONArray(idxstr) ;
		int n = 0 ;
		if(jarr!=null)
			n = jarr.length() ;
		
		for(int i = 0 ; i <  n ; i ++)
		{
			JSONObject jo = jarr.getJSONObject(i) ;
			CerItem ci = new CerItem() ;
			if(!ci.fromIdxJO(jo))
				continue ;
			rets.add(ci) ;
		}
		return rets ;
	}
	
	private JSONArray toItemsJArr()
	{
		JSONArray jarr = new JSONArray() ;
		for(CerItem ci:this.cerItems)
		{
			JSONObject tmpjo = ci.toIdxJO() ;
			jarr.put(tmpjo) ;
		}
		return jarr ;
	}

	private void saveIdx() throws Exception
	{
		File dir = CerManager.calCatDir(this.name);
		if(!dir.exists())
			dir.mkdirs();
		File idxf = new File(dir,"_idx.json") ;
		JSONArray jarr = toItemsJArr() ;
		try(FileOutputStream fos = new FileOutputStream(idxf);
				OutputStreamWriter osw = new OutputStreamWriter(fos,"UTF-8");)
		{
			jarr.write(osw) ;
		}
	}
	/**
	 * 
	 * @param common_name
	 *            OPC UA Client
	 * @param org
	 *            My Company
	 * @param org_unit
	 *            My Company Depart
	 * @param app_url
	 *            urn:mycompany:opcua:client
	 * @return
	 * @throws Exception
	 */
	static X509Certificate createOpcUaClientCer(String name, String org, String org_unit) throws Exception
	{
		// 生成客户端证书

		return null;
	}

}

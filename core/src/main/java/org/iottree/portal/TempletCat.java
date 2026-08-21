package org.iottree.portal;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.iottree.core.Config;
import org.iottree.core.util.Convert;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 模板分类 //，对应具体的页面中，可以放置1个或多个PageBlk
 * 
 * @author zzj
 */
public class TempletCat
{
	public static File getTempletBaseDir()
	{
		return new File(Config.getWebappBase() + "/_templet/");
	}

	private static LinkedHashMap<String, TempletCat> templetCats = null;

	// templet

	public static LinkedHashMap<String, TempletCat> listTempletCats()
	{
		if (templetCats != null)
			return templetCats;

		synchronized (TempletCat.class)
		{
			if (templetCats != null)
				return templetCats;

			try
			{
				List<TempletCat> pcs = loadTempletCats();
				LinkedHashMap<String, TempletCat> ret = new LinkedHashMap<>();
				for (TempletCat pc : pcs)
				{
					ret.put(pc.getName(), pc);
				}
				return templetCats = ret;
			}
			catch ( Exception ee)
			{
				ee.printStackTrace();
				return null;
			}
		}
	}

	private static List<TempletCat> loadTempletCats() throws IOException
	{
		ArrayList<TempletCat> rets = new ArrayList<>();

		File dir = TempletCat.getTempletBaseDir();
		if (!dir.exists())
			return rets;

		File[] subds = dir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File f)
			{
				return f.isDirectory();
			}
		});

		for (File subd : subds)
		{
			String catn = subd.getName();
			File tf = new File(subd, "_templets.json");
			JSONObject jo = Convert.readFileJO(tf);
			String catt = catn;
			if (jo == null)
			{
				TempletCat tc = new TempletCat(catn, catt);
				rets.add(tc);
				continue;
			}

			catt = jo.optString("t", catn);
			TempletCat tc = new TempletCat(catn, catt);
			rets.add(tc);

			JSONArray jarr = jo.optJSONArray("templets");
			if (jarr != null)
			{
				int ii = jarr.length();
				for (int i = 0; i < ii; i++)
				{
					JSONObject tmpjo = jarr.getJSONObject(i);
					// {"n":"dp1","t":"大屏模板1","page":"dp1.html"}
					String n = tmpjo.optString("n");
					if (Convert.isNullOrEmpty(n))
						continue;
					String t = tmpjo.optString("t");
					String p = tmpjo.optString("page");
					Templet tmp = new Templet(tc, n, t, p);
					tc.name2tmp.put(n, tmp);
				}
			}
		}

		return rets;
	}

	public static List<Templet> listTempletsAll()
	{
		ArrayList<Templet> rets = new ArrayList<>();
		for (TempletCat tc : listTempletCats().values())
		{
			rets.addAll(tc.listTempletAll().values());
		}
		return rets;
	}

	public static TempletCat getTempletCat(String name)
	{
		return listTempletCats().get(name);
	}

	public static Templet getTemplet(String cat_name, String name)
	{
		TempletCat tc = listTempletCats().get(cat_name);
		if (tc == null)
			return null;
		return tc.getTemplet(name);
	}

	public static Templet getTempletByUID(String uid)
	{
		int k = uid.indexOf('.');
		if (k <= 0)
			return null;
		return getTemplet(uid.substring(0, k), uid.substring(k + 1));
	}

	String name;

	String title;

	LinkedHashMap<String, Templet> name2tmp = new LinkedHashMap<>();

	public TempletCat(String name, String title)
	{
		this.name = name;
		this.title = title;
	}

	public String getName()
	{
		return name;
	}

	public String getTitle()
	{
		return title;
	}

	public File getCatDir()
	{
		return new File(getTempletBaseDir(), this.name + "/");
	}

	public LinkedHashMap<String, Templet> listTempletAll()
	{
		return this.name2tmp;
	}

	public Templet getTemplet(String name)
	{
		return name2tmp.get(name);
	}
}

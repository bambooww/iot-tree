package org.iottree.core.conn.html;

import java.util.ArrayList;
import java.util.List;

import org.iottree.core.util.Convert;

/**
 * support xpath format below
 * 
 * /xx[1]/yy[3]/a /xx[1]/yy[3]/a[3] /xx[1]/yy[3]/a[3]@p1
 * /xx[1]/yy[3]/a[3]@p1[substring(0,1)] /xx[1]/yy[3]/a[3]@p1[split(2)]
 * 
 * @author jason.zhu
 *
 */
public class XPath
{
	List<XPathItem> pathItems = null;

	List<XPathExt> pathExts = null;
	
	public List<XPathItem> getPathItems()
	{
		return pathItems ;
	}
	
	public List<XPathExt> getPathExts()
	{
		return pathExts ;
	}

	public static XPath parseFromStr(String xp, StringBuilder failedr)
	{
		if (Convert.isNullOrEmpty(xp))
		{
			failedr.append("xpath cannot be null or empty");
			return null;
		}

		List<String> ss = Convert.splitStrWith(xp, "/");
		int s = ss.size();
		ArrayList<XPathItem> pitems = new ArrayList<>();

		for (int i = 0; i < s - 1; i++)
		{
			XPathItem xpi = XPathItem.parseStr(ss.get(i), failedr);
			if (xpi == null)
				return null;
			pitems.add(xpi);
		}

		String lasts = ss.get(s - 1);

		int k = lasts.indexOf("]");
		String extstr = null;
		StringBuilder sb = new StringBuilder();
		if (k > 0)
		{
			String tmps = lasts.substring(0, k + 1);
			XPathItem xpi = XPathItem.parseStr(tmps, sb);
			if (xpi != null)
			{
				pitems.add(xpi);
				extstr = lasts.substring(k + 1);
			}
		}

		if (extstr == null)
		{
			k = lasts.indexOf('@');
			if (k > 0)
			{
				String tmps = lasts.substring(0, k);
				XPathItem xpi = XPathItem.parseStr(tmps, failedr);
				if (xpi == null)
					return null;
				pitems.add(xpi);
				extstr = lasts.substring(k);
			}
		}

		XPath r = new XPath();
		r.pathItems = pitems;
		if (extstr == null)
		{
			XPathItem xpi = XPathItem.parseStr(lasts, failedr);
			if (xpi == null)
				return null;
			pitems.add(xpi);

			return r;
		}

		r.pathExts = parseExts(extstr);
		return r;
	}

	/**
	 * @xxx[f1()][f2()] [f1()] @param extstr
	 * @return
	 */
	private static List<XPathExt> parseExts(String extstr)
	{
		if(Convert.isNullOrEmpty(extstr))
			return null ;
		List<String> ss = Convert.splitStrWith(extstr, "@[]");
		int s = ss.size();
		ArrayList<XPathExt> exts = new ArrayList<>(s);
		int i = 0;
		if (extstr.startsWith("@"))
		{//
			exts.add(new XPathAttr(ss.get(0)));
			i = 1;
		}

		for (; i < s; i++)
		{
			XPathFunc xpf = parseFunc(ss.get(i)) ;
			exts.add(xpf) ;
		}
		return exts;
	}
	
	private static XPathFunc parseFunc(String fs)
	{
		List<String> tmps = Convert.splitStrWith(fs, "(,)") ;
		String fn = tmps.remove(0) ;
		return new XPathFunc(fn,tmps) ;
	}
}

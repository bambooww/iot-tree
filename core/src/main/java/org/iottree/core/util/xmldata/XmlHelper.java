package org.iottree.core.util.xmldata;

import java.io.StringReader;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.iottree.core.util.Convert;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;

public class XmlHelper
{
	static HashMap<String,char[]> s2charMap = new HashMap<>();
	static
	{
		s2charMap.put("lt", new char[] { '<' });
		s2charMap.put("gt", new char[] { '>' });
		s2charMap.put("apos", new char[] { '\'' });
		s2charMap.put("amp", new char[] { '&' });
		s2charMap.put("quot", new char[] { '\"' });
	}

	private static char getDecodeChar(String s)
	{
		char[] cs = (char[]) s2charMap.get(s);
		if (cs != null)
			return cs[0];

		if (!s.startsWith("#"))
			return (char) -1;

		try
		{
			return (char) Integer.parseInt(s.substring(1));
		}
		catch (Exception ee)
		{
			return (char) -1;
		}
	}

	public static String getEncodeStr(char c)
	{
		switch (c)
		{
		case '<':
			return "lt";
		case '>':
			return "gt";
		case '&':
			return "amp";
		case '\'':
			return "apos";
		case '\"':
			return "quot";
		case '\n':
			return "#10;";
		case '\r':
			return "#13;";
		default:
			return "#" + c;
		}
	}

	static String entitystr = "><&\'\"\r\n\t";

	// static String entitystr = "><&\'\"" ;

	public static String xmlEncoding(String input)
	{
		return xmlEncoding(input, entitystr);
	}

	/**
	 * 把结果中的涉及实体引用的字符转换为实体
	 */
	public static String xmlEncoding(String input, String delimiter)
	{
		if (input == null || input.equals(""))
			return input;

		delimiter += '&';

		StringTokenizer tmpst = new StringTokenizer(input, delimiter, true);
		StringBuffer tmpsb = new StringBuffer(input.length() + 100);
		String tmps = null;
		while (tmpst.hasMoreTokens())
		{
			tmps = tmpst.nextToken();
			if (tmps.length() == 1 && delimiter.indexOf(tmps) >= 0)
			{
				// tmpsb.append('&').append(getEncodeStr(tmps.charAt(0))).append(';');

				switch (tmps.charAt(0))
				{
				case '<':
					tmpsb.append("&lt;");
					break;
				case '>':
					tmpsb.append("&gt;");
					break;
				case '&':
					tmpsb.append("&amp;");
					break;
				case '\'':
					tmpsb.append("&apos;");
					break;
				case '\"':
					tmpsb.append("&quot;");
					break;
				case '\n':
					tmpsb.append("&#10;");
					break;
				case '\r':
					tmpsb.append("&#13;");
					break;
				case '\t':
					tmpsb.append("&#9;");
					break;
				}
			}
			else
			{
				tmpsb.append(tmps);
			}
		}

		return tmpsb.toString();
	}

	public static String xmlDecoding(String input)
	{
		return xmlDecoding(input, entitystr);
	}

	public static String xmlDecoding(String input, String delimiter)
	{
		if (input == null || input.equals(""))
			return input;

		delimiter += '&';

		StringBuffer sb = new StringBuffer(input.length());
		int p = 0;
		while (true)
		{
			int a = input.indexOf('&', p);
			if (a < 0)
			{
				sb.append(input.substring(p));
				break;
			}
			else
			{
				sb.append(input.substring(p, a));
				p = a;
				int b = input.indexOf(';', p);
				if (b < 0)
				{
					sb.append(input.substring(p));
					break;
				}
				else
				{
					String s = input.substring(p + 1, b);
					char cc = getDecodeChar(s);
					// System.out.println(s+">-->"+cc);
					if (delimiter.indexOf(cc) < 0)
					{
						sb.append('&').append(s).append(';');
						p = b + 1;
						continue;
					}
					else
					{
						sb.append(cc);
						p = b + 1;
						continue;
					}
				}
			}
		}
		return sb.toString();
	}

	public static String getElementFirstText(Element ele)
	{
		Node n = ele.getFirstChild();
		if (n instanceof Text)
		{
			return ((Text) n).getNodeValue();
		}
		else
		{
			return null;
		}
	}

	public static List<Element> getSubChildElementList(Element ele,
			String tagname)
	{
		int p0 = tagname.indexOf(':');
		if (p0 >= 0)
			tagname = tagname.substring(p0 + 1);
		return getSubChildElementList(ele, new String[] { tagname });
	}

	public static List<Element> getSubChildElementList(Element ele,
			String[] tagnames)
	{
		if (ele == null)
		{
			return null;
		}

		List<Element> v = new ArrayList<Element>();

		// boolean isall = false;

		NodeList tmpnl = ele.getChildNodes();

		Node tmpn = null;

		int k;
		for (k = 0; k < tmpnl.getLength(); k++)
		{
			tmpn = tmpnl.item(k);

			if (tmpn.getNodeType() != Node.ELEMENT_NODE)
			{
				continue;
			}

			Element eee = (Element) tmpn;
			String noden = eee.getNodeName();
			int p = noden.indexOf(':');
			if (p >= 0)
				noden = noden.substring(p + 1);

			for (String tagname : tagnames)
			{
				if (tagname.equals(noden) || tagname.equals("*"))
				{
					v.add(eee);
					break;
				}
			}
		}

		return v;
	}

	public static Element[] getSubChildElement(Element ele, String tagname)
	{
		int p0 = tagname.indexOf(':');
		if (p0 >= 0)
			tagname = tagname.substring(p0 + 1);

		return getSubChildElement(ele, new String[] { tagname });
	}

	public static Element[] getSubChildElement(Element ele, String[] tagnames)
	{
		if (ele == null)
		{
			return null;
		}

		List<Element> v = new ArrayList<Element>();

		NodeList tmpnl = ele.getChildNodes();

		Node tmpn = null;

		int k;
		for (k = 0; k < tmpnl.getLength(); k++)
		{
			tmpn = tmpnl.item(k);

			if (tmpn.getNodeType() != Node.ELEMENT_NODE)
			{
				continue;
			}

			Element eee = (Element) tmpn;
			String noden = eee.getNodeName();
			int p = noden.indexOf(':');
			if (p >= 0)
				noden = noden.substring(p + 1);

			for (int i = 0; i < tagnames.length; i++)
			{
				if (tagnames[i].equals(noden) || tagnames[i].equals("*"))
				{
					v.add(eee);
					break;
				}
			}
		}

		Element[] rets = new Element[v.size()];
		v.toArray(rets);
		return rets;
	}

	/**
	 * 获取元素的所有属性和值
	 * 
	 * @param ele
	 *            元素对象
	 * @return Hashtable 存放属性和值的Hash表，其key为属性名对应的值为属性值
	 */
	public static Properties getElementAttributes(Element ele)
	{
		Properties ht = new Properties();
		NamedNodeMap nnm = ele.getAttributes();
		int len = nnm.getLength();
		Node tmpn = null;
		for (int k = 0; k < len; k++)
		{
			tmpn = nnm.item(k);
			String tmps = tmpn.getNodeValue();
			if (tmps == null)
			{
				tmps = "";
			}
			ht.put(tmpn.getNodeName(), tmps);
		}
		return ht;
	}

	public static HashMap<String, String> getElementAttrMap(Element ele)
	{
		HashMap<String, String> ht = new HashMap<String, String>();
		NamedNodeMap nnm = ele.getAttributes();
		int len = nnm.getLength();
		Node tmpn = null;
		for (int k = 0; k < len; k++)
		{
			tmpn = nnm.item(k);
			String tmps = tmpn.getNodeValue();
			if (tmps == null)
			{
				tmps = "";
			}
			ht.put(tmpn.getNodeName(), tmps);
		}
		return ht;
	}

	public static HashMap<String, String> getEleAttrNameValueMap(Element ele)
	{
		HashMap<String, String> ht = new HashMap<String, String>();
		NamedNodeMap nnm = ele.getAttributes();
		int len = nnm.getLength();
		Node tmpn = null;
		for (int k = 0; k < len; k++)
		{
			tmpn = nnm.item(k);
			String tmps = tmpn.getNodeValue();
			if (tmps == null)
			{
				tmps = "";
			}
			ht.put(tmpn.getNodeName(), tmps);
		}
		return ht;
	}

	/**
	 * 得到当前元素的所有的子元素，且子元素都有指定的标签名
	 * 
	 * @param ele
	 *            当前元素
	 * @param tagname
	 *            标签名（"*" 代表所有的标签）
	 * @return Element[] 元素数组
	 */
	// public static Element[] getSubChildElement(Element ele, String tagname)
	// {
	// if (ele == null)
	// {
	// return null;
	// }
	//
	// boolean isall = false;
	// if (tagname.equals("*"))
	// {
	// isall = true;
	// }
	//		
	// int p0 = tagname.indexOf(':');
	// if(p0>=0)
	// tagname = tagname.substring(p0+1);
	//		
	// NodeList tmpnl = ele.getChildNodes();
	//
	// Node tmpn = null;
	//
	// Vector v = new Vector();
	// int k;
	// for (k = 0; k < tmpnl.getLength(); k++)
	// {
	// tmpn = tmpnl.item(k);
	//
	// if (tmpn.getNodeType() != Node.ELEMENT_NODE)
	// {
	// continue;
	// }
	//			
	// Element eee = (Element) tmpn;
	// String noden = eee.getNodeName();
	// int p = noden.indexOf(':') ;
	// if(p>=0)
	// noden = noden.substring(p+1);
	//			
	// if (isall || tagname.equals(noden))
	// {
	// v.add(eee);
	// }
	// }
	//
	// int s = v.size();
	// Element[] tmpe = new Element[s];
	// for (k = 0; k < s; k++)
	// {
	// tmpe[k] = (Element) v.elementAt(k);
	//
	// }
	// return tmpe;
	// }
	// /**
	// * 把结果中的涉及实体引用的字符转换为实体
	// */
	// public static String xmlEncoding(String input)
	// {
	// if (input == null)
	// {
	// return null;
	// }
	// String entitystr = "><&\'\"\r\n";
	// StringTokenizer tmpst = new StringTokenizer(input, entitystr, true);
	// StringBuffer tmpsb = new StringBuffer(input.length() + 100);
	// String tmps = null;
	// while (tmpst.hasMoreTokens())
	// {
	// tmps = tmpst.nextToken();
	// if (tmps.length() == 1 && entitystr.indexOf(tmps) >= 0)
	// {
	// switch (tmps.charAt(0))
	// {
	// case '<':
	// tmpsb.append("&lt;");
	// break;
	// case '>':
	// tmpsb.append("&gt;");
	// break;
	// case '&':
	// tmpsb.append("&amp;");
	// break;
	// case '\'':
	// tmpsb.append("&apos;");
	// break;
	// case '\"':
	// tmpsb.append("&quot;");
	// break;
	// case '\n':
	// tmpsb.append("&#10;");
	// break;
	// case '\r':
	// tmpsb.append("&#13;");
	// break;
	// }
	// }
	// else
	// {
	// tmpsb.append(tmps);
	// }
	// }
	//
	// return tmpsb.toString();
	// }
	/**
	 * @param ls
	 * @return String
	 * @roseuid 3E6F3C4302CC
	 */
	public static String arrayOfLongToStr(long[] ls)
	{
		if (ls == null || ls.length == 0)
		{
			return "";
		}

		StringBuffer tmpsb = new StringBuffer();

		tmpsb.append('|');
		for (int i = 0; i < ls.length; i++)
		{
			tmpsb.append(ls[i]).append('|');
		}

		return tmpsb.toString();
	}

	/**
	 * @param str
	 * @return long[]
	 * @roseuid 3E6F3C680149
	 */
	public static long[] strToArrayOfLong(String str)
	{
		StringTokenizer tmpst = new StringTokenizer(str, "|", false);
		int len = tmpst.countTokens();
		long[] retl = new long[len];

		for (int i = 0; i < len; i++)
		{
			retl[i] = Long.parseLong(tmpst.nextToken());
		}

		return retl;
	}

	/**
	 * @param str
	 * @return long[]
	 * @roseuid 3E6F3C680149
	 */
	public static String[] strToArrayOfString(String str)
	{
		if (str == null)
		{
			return new String[0];
		}
		StringTokenizer tmpst = new StringTokenizer(str, "|", false);
		int len = tmpst.countTokens();
		String[] retl = new String[len];

		for (int i = 0; i < len; i++)
		{
			retl[i] = tmpst.nextToken();
		}

		return retl;
	}

	/**
	 * @param ls
	 * @return String
	 * @roseuid 3E6F3C4302CC
	 */
	public static String arrayOfStringToStr(String[] ls)
	{
		if (ls == null || ls.length == 0)
		{
			return "";
		}

		StringBuffer tmpsb = new StringBuffer();

		tmpsb.append('|');
		for (int i = 0; i < ls.length; i++)
		{
			tmpsb.append(ls[i]).append('|');
		}

		return tmpsb.toString();
	}

	public static String elementToString(Element ele)
	{
		try
		{
			StringWriter sw = new StringWriter();

			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer();

			DOMSource source = new DOMSource(ele);
			StreamResult result = new StreamResult(sw);
			transformer.setOutputProperty("indent", "yes");
			transformer.transform(source, result);

			return sw.toString();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public static Element stringToElement(String str)
	{
		StringReader sr = new StringReader(str);

		try
		{
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
					.newInstance();
			docBuilderFactory.setNamespaceAware(false);
			docBuilderFactory.setValidating(false);
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

			InputSource is = new InputSource(sr);
			// is.setEncoding("gb2312");
			Document doc = docBuilder.parse(is);
			return doc.getDocumentElement();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public static Element byteArrayToElement(byte[] cont)
	{
		if (cont == null || cont.length <= 0)
			return null;

		try
		{
			ByteArrayInputStream bais = new ByteArrayInputStream(cont);
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
					.newInstance();
			docBuilderFactory.setNamespaceAware(false);
			docBuilderFactory.setValidating(false);
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

			InputSource is = new InputSource(bais);
			// is.setEncoding("gb2312");
			Document doc = docBuilder.parse(is);
			return doc.getDocumentElement();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public static Element loadFileToElemenet(File f) throws Exception
	{
		if (!f.exists())
			return null;
		if (f.isDirectory())
			return null;

		try(FileInputStream fis = new FileInputStream(f); )
		{
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
					.newInstance();
			docBuilderFactory.setNamespaceAware(false);
			docBuilderFactory.setValidating(false);
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

			InputSource is = new InputSource(fis);
			// is.setEncoding("gb2312");
			Document doc = docBuilder.parse(is);
			return doc.getDocumentElement();
		}
	}
	
	/**
	 * transfer xml element to json str
	 * tag will be  ”_":"tagname"
	 * @param ele
	 * @return
	 */
	public static String transElement2JSONStr(Element ele,boolean bformat,boolean ignore_whitesp,boolean element_only)
	{
		StringWriter sw = new StringWriter() ;
		String indent = null ;
		if(bformat)
			indent = "  " ;
		try
		{
			transElement2JSONStr(ele,sw,indent,ignore_whitesp,element_only);
			return sw.toString() ;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null ;
		}
	}
	
	private static void transElement2JSONStr(Element ele,Writer w,String indent,boolean ignore_whitesp,boolean element_only)
		throws IOException
	{
		boolean bformat = Convert.isNotNullEmpty(indent) ;
		w.write(indent+"{");
		if(bformat)
			w.write("\r\n"+indent+"\"$tag\":\""+ele.getTagName()+"\"");
		else
			w.write("\"$tag\":\""+ele.getTagName()+"\"");
		
		NamedNodeMap nnm = ele.getAttributes() ;
		int s ;
		if(nnm!=null&&(s=nnm.getLength())>0)
		{
			for(int i = 0 ; i < s; i ++)
			{
				Node tmpn = nnm.item(i) ;
				String tmpv = tmpn.getNodeValue();
				if (tmpv == null)
				{
					tmpv = "";
				}
				w.write(" \""+tmpn.getNodeName()+"\":\""+Convert.plainToJsStr(tmpv)+"\"");
			}
		}
		
		NodeList nls = ele.getChildNodes() ;
		if(nls!=null&&(s=nls.getLength())>0)
		{
			if(bformat)
				w.write("\r\n"+indent+"\"$sub\":[");
			else
				w.write("\"$sub\":[");
			
			String subind = "";
			if(bformat)
				subind = indent+"  " ;
			boolean bf = true;
			for(int i = 0 ; i < s; i ++)
			{
				Node tmpn = nls.item(i) ;
				if(tmpn instanceof Element)
				{
					if(bf) bf=false;
					else w.write(",");
					transElement2JSONStr((Element)tmpn,w,subind,ignore_whitesp,element_only);
				}
				else if(tmpn instanceof org.w3c.dom.Text)
				{
					if(element_only)
						continue ;
					String str = ((org.w3c.dom.Text)tmpn).getData() ;
					if(ignore_whitesp)
					{
						str = str.trim();
						if(Convert.isNullOrEmpty(str))
							continue ;
					}
					if(bf) bf=false;
					else w.write(",");
					w.write("\""+Convert.plainToJsStr(str)+"\"");
				}
			}
		}
		
		if(bformat)
			w.write("\r\n"+indent+"}");
		else
			w.write("}");
	}
}

class BinHexTransfer
{
//	private static final int CharsChunkSize = 0x80;
//
//	private static final String s_hexDigits = "0123456789ABCDEF";

	public static void TransBinToHexStr(byte[] buffer, int index, int count,
			StringBuffer writer)
	{
		if (buffer == null)
		{
			throw new IllegalArgumentException("buffer");
		}
		if (index < 0)
		{
			throw new IllegalArgumentException("index");
		}
		if (count < 0)
		{
			throw new IllegalArgumentException("count");
		}
		if (count > (buffer.length - index))
		{
			throw new IllegalArgumentException("count");
		}
		char[] chArray1 = new char[((count * 2) < 0x80) ? (count * 2) : 0x80];
		int num1 = index + count;
		while (index < num1)
		{
			int num2 = (count < 0x40) ? count : 0x40;
			int num3 = BinHexTransfer.Encode(buffer, index, num2, chArray1);
			writer.append(chArray1, 0, num3);
			index += num2;
			count -= num2;
		}
	}

	public static String TransBinToHexStr(byte[] inArray)
	{
		return TransBinToHexStr(inArray, 0, inArray.length);
	}

	public static String TransBinToHexStr(byte[] inArray, int offsetIn,
			int count)
	{
		if (inArray == null)
		{
			throw new IllegalArgumentException("inArray");
		}
		if (0 > offsetIn)
		{
			throw new IllegalArgumentException("offsetIn");
		}
		if (0 > count)
		{
			throw new IllegalArgumentException("count");
		}
		if (count > (inArray.length - offsetIn))
		{
			throw new IllegalArgumentException("count");
		}
		char[] chArray1 = new char[2 * count];
		int num1 = Encode(inArray, offsetIn, count, chArray1);
		return new String(chArray1, 0, num1);
	}

	public static byte[] TransHexStrToBin(String hexstr)
	{
		int c = hexstr.length();
		byte[] rets = new byte[c / 2];
		for (int i = 0; i < c; i += 2)
		{
			byte tmph = 0;
			char ch = hexstr.charAt(i);
			char cl = hexstr.charAt(i + 1);
			if (ch >= '0' && ch <= '9')
				tmph = (byte) (ch - '0');
			else if (ch >= 'A' && ch <= 'F')
				tmph = (byte) (ch - 'A' + 10);
			else
				throw new RuntimeException("Illegal hex str unknow char=" + ch);

			byte tmpl = 0;
			if (cl >= '0' && cl <= '9')
				tmpl = (byte) (cl - '0');
			else if (cl >= 'A' && cl <= 'F')
				tmpl = (byte) (cl - 'A' + 10);
			else
				throw new RuntimeException("Illegal hex str unknow char=" + cl);

			rets[i / 2] = (byte) ((tmph << 4) + tmpl);
		}
		return rets;
	}

	private static int Encode(byte[] inArray, int offsetIn, int count,
			char[] outArray)
	{
		int outi = 0;
		int j = 0;
		int outlen = outArray.length;
		for (int i = 0; i < count; i++)
		{
			byte tmpi = inArray[offsetIn++];
			outArray[outi++] = "0123456789ABCDEF".charAt((tmpi >> 4) & 0x0F);
			if (outi == outlen)
			{
				break;
			}
			outArray[outi++] = "0123456789ABCDEF".charAt(tmpi & 0x0F);
			if (outi == outlen)
			{
				break;
			}
		}
		return (outi - j);
	}
}

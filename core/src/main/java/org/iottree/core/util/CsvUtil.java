package org.iottree.core.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class CsvUtil
{

	public static List<Object> parseCSVLine(String line,boolean parse_num)
	{
		List<Object> result = new ArrayList<>();
		if (line == null || line.isEmpty())
		{
			return result;
		}

		StringBuilder currentField = new StringBuilder();
		boolean inQuotes = false;
		boolean lastInQuo = false;
		for (int i = 0; i < line.length(); i++)
		{
			char c = line.charAt(i);
			if (inQuotes)
			{
				if (c == '"')
				{
					if (i + 1 < line.length() && line.charAt(i + 1) == '"')
					{
						// Double quote inside quoted field
						currentField.append('"');
						i++;
					}
					else
					{
						// End of quoted field
						inQuotes = false;
						lastInQuo = true ;
					}
				}
				else
				{
					currentField.append(c);
				}
			}
			else
			{
				if (c == '"')
				{
					// Beginning of quoted field
					inQuotes = true;
				}
				else if (c == ',')
				{
					// End of field
					Object objv = parseCurStr(parse_num,lastInQuo,currentField.toString()) ;
					result.add(objv);
						
					currentField.setLength(0);
				}
				else
				{
					currentField.append(c);
				}
			}
		}
		// Add the last field
		result.add(parseCurStr(parse_num,lastInQuo,currentField.toString()));

		return result;
	}

	
	private static Object parseCurStr(boolean parsenum,boolean lastInQuo,String tmps)
	{
		if(lastInQuo)
		{
			lastInQuo = false;
			return tmps ;
		}

			tmps = tmps.trim() ;
			if(parsenum)
			{
				int k = tmps.indexOf('.') ;
				try
				{
					if(k>=0)
					{
						double dval = Double.parseDouble(tmps) ;
						return dval ;
					}
					else
					{
						long ival = Long.parseLong(tmps) ;
						return ival ;
					}
				}
				catch(Exception e)
				{}
			}
		return tmps ;
	}
	
	public static String transToCsvLine(List<String> ss)// throws IOException
	{
		return transToCsvLine(ss, ',');
	}
	
	public static String transToCsvLine(List<String> ss, char delimi)// throws IOException
	{
		if (ss == null||ss.size()<=0)
			return null;

		StringBuilder sb = new StringBuilder();
		for (String s : ss)
		{
			s= transValOtsCrLf(s) ;
			s = transToCsvVal(s) ;
			sb.append(delimi).append(s);
		}
		if (ss.size() > 0)
			return sb.substring(1);
		else
			return "";
	}
	
	private static String transToCsvVal(String str)
	{
		if(Convert.isNullOrEmpty(str))
			return "" ;
		boolean has_d = str.indexOf(',')>=0 ;
		boolean has_quot = str.indexOf('\"')>=0 ;
		boolean has_cr = str.indexOf('\r')>=0 ;
		boolean has_lf = str.indexOf('\n')>=0 ;
		if(!has_quot && !has_cr && ! has_lf && !has_d)
			return str ;
		
		String ret = "\"" ;
		int s = str.length() ;
		for(int i = 0 ; i < s ; i ++)
		{
			char c = str.charAt(i);
			if(c=='\"')
				ret += "\"\"";
			else
				ret += c ; 
		}
		ret += "\"" ;
		return ret ;
	}
	
	/**
	 * 更加ots文件格式的\r\n要求进行整理
	 * @param val
	 * @return
	 * @throws IOException 
	 */
	public static String transValOtsCrLf(String str) //throws IOException
	{
		if(Convert.isNullOrEmpty(str))
			return "" ;
		
		boolean has_cr = str.indexOf('\r')>=0 ;
		boolean has_lf = str.indexOf('\n')>=0 ;
		if(!has_cr && ! has_lf)
			return str ;
		BufferedReader br = new BufferedReader(new StringReader(str)) ;
		ArrayList<String> lns = new ArrayList<>() ;
		String ln = null ;
		
		try
		{
			while((ln=br.readLine())!=null)
			{
				if(ln.equals(""))
					continue ;
				lns.add(ln) ;
			}
		}
		catch(IOException ioe)
		{
		}
		
		int s = lns.size() ;
		if(s<=0)
			return "" ;
		
		String ret = lns.get(0) ;
		for(int i = 1 ; i < s ; i ++)
		{
			//ret += "\r\n"+lns.get(i) ;
			
			ret += " "+lns.get(i) ;
		}
		return ret ;
	}
}

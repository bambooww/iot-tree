package org.iottree.core.util;

import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.net.*;

import javax.servlet.http.HttpServletRequest;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Convert
{
	static SimpleDateFormat FshortYMD = new SimpleDateFormat("yyyy-MM-dd");

	public static String toShortYMD(Date d)
	{
		if (d == null)
			return "";
		return FshortYMD.format(d);
	}

	static SimpleDateFormat YM = new SimpleDateFormat("yyyy-MM");

	public static String toYM(Date d)
	{
		if (d == null)
			return "";
		// return YM.format(d);
		return new SimpleDateFormat("yyyy-MM").format(d);
	}

	static SimpleDateFormat FfulYMD = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static String toFullYMDHMS(Date d)
	{
		if (d == null)
			return "";
		// return FfulYMD.format(d);
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(d);
	}

	static SimpleDateFormat XmlDateStr = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

	public static String toXmlValDateStr(Date d)
	{
		if (d == null)
			return "";
		// return XmlDateStr.format(d);
		return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(d);
	}

	static SimpleDateFormat FYMDHM = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	public static String toYMDHM(Date d)
	{
		if (d == null)
			return "";
		// return FYMDHM.format(d);
		return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(d);
	}

	static SimpleDateFormat HM = new SimpleDateFormat("HH:mm");

	public static String toHM(Date d)
	{
		if (d == null)
			return "";
		// return HM.format(d);
		return new SimpleDateFormat("HH:mm").format(d);
	}

	static SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

	/**
	 * 把字符串表示的时间信息,转换为时间对象 字符串格式支持如下 yyyy-MM-ddThh:mi:ss.SSSZ
	 * yyyy-MM-ddThh:mi:ss.SSS yyyy-MM-dd hh:mi:ss yyyy-MM-dd hh:mi yyyy-MM-dd
	 * hh yyyy-MM-dd yyyy-MM yyyy
	 * 
	 * @param datastr
	 * @return
	 */
	public static Calendar toCalendar(String datastr)
	{
		if (datastr == null || datastr.equals(""))
			return null;

		int y = 0, m = 0, d = 0, h = 0, mi = 0, s = 0, n = 0;
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, y);
		cal.set(Calendar.MONTH, m);
		cal.set(Calendar.DAY_OF_MONTH, d);
		cal.set(Calendar.HOUR_OF_DAY, h);
		cal.set(Calendar.MINUTE, mi);
		cal.set(Calendar.SECOND, s);
		cal.set(Calendar.MILLISECOND, n);

		datastr = datastr.trim();

		int p = 0, q;
		// yyyy-...
		q = datastr.indexOf('-', p);
		if (q <= 0)
		{
			y = Integer.parseInt(datastr);
			cal.set(Calendar.YEAR, y);
			return cal;
		}

		y = Integer.parseInt(datastr.substring(p, q));
		cal.set(Calendar.YEAR, y);

		p = q + 1;
		// yyyy-mm-...
		q = datastr.indexOf('-', p);
		if (q < 0)
		{
			m = Integer.parseInt(datastr.substring(p));
			cal.set(Calendar.MONTH, m - 1);
			return cal;
		}

		m = Integer.parseInt(datastr.substring(p, q));
		cal.set(Calendar.MONTH, m - 1);

		p = q + 1;
		// yyyy-mm-dd ...
		q = datastr.indexOf('T', p);
		if (q < 0)
			q = datastr.indexOf(' ', p);

		if (q < 0)
		{
			d = Integer.parseInt(datastr.substring(p));
			cal.set(Calendar.DAY_OF_MONTH, d);
			return cal;
		}

		d = Integer.parseInt(datastr.substring(p, q));
		cal.set(Calendar.DAY_OF_MONTH, d);

		p = q + 1;
		// yyyy-mm-dd hh:...
		q = datastr.indexOf(':', p);
		if (q < 0)
		{
			h = Integer.parseInt(datastr.substring(p));
			cal.set(Calendar.HOUR_OF_DAY, h);
			return cal;
		}

		h = Integer.parseInt(datastr.substring(p, q).trim());
		cal.set(Calendar.HOUR_OF_DAY, h);

		p = q + 1;
		// yyyy-mm-dd hh:mi:...
		q = datastr.indexOf(':', p);
		if (q < 0)
		{
			mi = Integer.parseInt(datastr.substring(p));
			cal.set(Calendar.MINUTE, mi);
			return cal;
		}

		mi = Integer.parseInt(datastr.substring(p, q));
		cal.set(Calendar.MINUTE, mi);

		p = q + 1;
		// yyyy-mm-dd hh:mi:ss...
		q = datastr.indexOf('.', p);
		if (q < 0)
		{
			s = Integer.parseInt(datastr.substring(p).trim());
			cal.set(Calendar.SECOND, s);
			return cal;
		}

		s = Integer.parseInt(datastr.substring(p, q));
		cal.set(Calendar.SECOND, s);

		p = q + 1;
		// yyyy-mm-dd hh:mi:ss.ns
		String tmps = datastr.substring(p);
		if (tmps.length() >= 3)
		{
			n = Integer.parseInt(datastr.substring(p, p + 3).trim());
			cal.set(Calendar.MILLISECOND, n);
		}

		// ss.+0800
		if (tmps.length() == 8)
		{// zone
			int zmillis = Integer.parseInt(tmps.substring(4, 6)) * 3600000
					+ Integer.parseInt(tmps.substring(6, 8)) * 60000;

			if (tmps.charAt(3) == '-')
				zmillis = -zmillis;

			cal.set(Calendar.ZONE_OFFSET, zmillis);
		}

		// System.out.println("input="+datastr+"
		// output="+sdf.format(cal.getTime()));
		return cal;
	}

	/**
	 * 对输入的Plain文本转换成html文本,以适合页面输出展示的需要
	 * 
	 * @param input
	 * @return
	 */
	public static String plainToHtml(String input)
	{
		return plainToHtml(input, true);
	}

	public static String plainToHtml(String input, boolean trans_br)
	{
		return plainToHtml(input, trans_br, false);
	}

	public static String plainToHtml(String input, boolean trans_br, boolean trans_space)
	{
		if (input == null)
		{
			return "";
		}

		char[] array = input.toCharArray();

		StringBuffer buf = new StringBuffer(array.length + array.length / 2);

		for (int i = 0; i < array.length; i++)
		{
			// if ( (i != 0) && (i % 60 == 0))
			// {
			// buf.append("<br/>");
			// }

			switch (array[i])
			{
			case '<':
				buf.append("&lt;");
				break;
			case '>':
				buf.append("&gt;");
				break;
			case '&':
				buf.append("&amp;");
				break;
			case '\n':
				if (trans_br)
					buf.append("<br/>");
				else
					buf.append('\n');
				break;
			case ' ':
				if (trans_space)
					buf.append("&nbsp;");
				else
					buf.append(array[i]);
				break;
			case '\"':
				buf.append("&#34;");
				break;
			case '\'':
				buf.append("&#39;");
				break;
			default:
				buf.append(array[i]);
				break;
			}
		}
		return buf.toString();
	}

	/**
	 * 
	 * @param txt
	 * @return
	 */
	public static String plainToJsStr(String txt)
	{
		if (txt == null)
			return null;

		if (txt.equals(""))
			return "";

		StringBuilder sb = new StringBuilder();
		int len = txt.length();
		for (int i = 0; i < len; i++)
		{
			char c = txt.charAt(i);
			switch (c)
			{
			case '\'':
				sb.append("\\\'");
				break;
			case '\"':
				sb.append("\\\"");
				break;
			case '\t':
				sb.append("\\t");
				break;
			case '\r':
				sb.append("\\r");
				break;
			case '\n':
				sb.append("\\n");
				break;
			case '\\':
				sb.append("\\\\");
				break;
			default:
				sb.append(c);
				break;
			}
		}
		return sb.toString();
	}

	/**
	 * 判断字符串是否为空
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isNullOrEmpty(String s)
	{
		if (s == null)
			return true;

		return s.equals("");
	}

	public static boolean isEmptyOrWhitespace(String s)
	{
		if (s == null)
			return true;
		if ("".equals(s.trim()))
			return true;
		return false;
	}

	public static boolean isNotNullEmpty(String s)
	{
		if (s == null)
			return false;

		return !s.equals("");
	}

	/**
	 * 判定字符串是否为null,或只有空白字符空
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isNullOrTrimEmpty(String s)
	{
		if (s == null)
			return true;

		return s.trim().equals("");
	}

	public static boolean isNotNullTrimEmpty(String s)
	{
		if (s == null)
			return false;

		return !s.trim().equals("");
	}

	public static long parseToInt64(String s, long defv)
	{
		if (isNullOrTrimEmpty(s))
			return defv;

		return Long.parseLong(s);
	}

	public static int parseToInt32(String s, int defv)
	{
		if (isNullOrTrimEmpty(s))
			return defv;

		return Integer.parseInt(s);
	}

	public static short parseToInt16(String s, short defv)
	{
		if (isNullOrTrimEmpty(s))
			return defv;

		return Short.parseShort(s);
	}

	public static double parseToDouble(String s, double defv)
	{
		if (isNullOrTrimEmpty(s))
			return defv;

		return Double.parseDouble(s);
	}

	public static float parseToFloat(String s, float defv)
	{
		if (isNullOrTrimEmpty(s))
			return defv;

		return Float.parseFloat(s);
	}

	public static String toDecimalDigitsStr(double d, int dec_digit_num, boolean five_in)
	{
		String rStr = "";

		if (five_in)
		{
			BigDecimal tmpv = new BigDecimal(d);
			tmpv = tmpv.setScale(dec_digit_num, BigDecimal.ROUND_HALF_UP);
			return tmpv.toString();
		}

		for (int i = 0 ; i  < dec_digit_num ; i ++)
			rStr += "0";
		java.text.DecimalFormat df = new java.text.DecimalFormat("0." + rStr);
		return df.format(d);
	}

	/**
	 * 转换浮点数,控制小数点位数的字符串
	 * 
	 * @param d
	 *            原始数据
	 * @param dec_digit_num
	 *            小数位数
	 * @param five_in
	 *            是否四舍五入
	 * @return
	 */
	public static String toDecimalDigitsStr0(double d, int dec_digit_num, boolean five_in)
	{
		String s = "" + d;
		int p = s.indexOf('.');
		if (p < 0)
			return s;

		if (s.length() - p - 1 <= dec_digit_num)
			return s;

		if (five_in)
		{
			// return (new
			// BigDecimal(d)).setScale(dec_digit_num,BigDecimal.ROUND_UP).toString();

			double tenm = Math.pow(10, dec_digit_num);
			double dv = d * tenm;
			String tmps = "" + dv;
			int k = tmps.indexOf('.');
			if (k < 0)
				return s;

			long lv = Long.parseLong(tmps.substring(0, k));
			if (dv - lv > 0.5)
			{
				lv++;
			}

			tmps = "" + lv;

			if (d < 1)
				return "0." + tmps;
			else
				return tmps.substring(0, p) + "." + tmps.substring(p);
		}
		else
		{
			return s.substring(0, p + dec_digit_num + 1);
		}
	}

	public static String toDecimalDigitsStr(double d, int dec_digit_num)
	{
		return toDecimalDigitsStr(d, dec_digit_num, true);
	}

	/**
	 * 判断变量名称是否有效 该方法用来为输入有效的变量名称做验证
	 * 
	 * @param n
	 */
	public static void checkVarName(String n)
	{
		StringBuilder sb = new StringBuilder();
		if (!checkVarName(n, sb))
			throw new IllegalArgumentException(sb.toString());

		return;
	}

	public static boolean checkVarName(String n, StringBuilder invalidreson)
	{
		return checkVarName(n, true, invalidreson);
	}

	public static void checkVarName(String n, boolean letterfirst)
	{
		StringBuilder sb = new StringBuilder();
		if (!checkVarName(n, letterfirst, sb))
			throw new IllegalArgumentException(sb.toString());

		return;
	}

	public static boolean checkVarName(String n, boolean letterfirst, StringBuilder invalidreson)
	{
		if (n == null || n.equals(""))
		{
			invalidreson.append("name cannot be null or empty!");
			return false;
		}

		char c1 = n.charAt(0);
		if (letterfirst)
		{
			boolean bc1 = (c1 >= 'a' && c1 <= 'z') || (c1 >= 'A' && c1 <= 'Z');
			if (!bc1)
			{
				invalidreson.append("name first char must be a-z A-Z");
				return false;
			}
		}
		else
		{
			boolean bc1 = (c1 >= 'a' && c1 <= 'z') || (c1 >= 'A' && c1 <= 'Z') || c1 == '_';
			if (!bc1)
			{
				invalidreson.append("name first char must be a-z A-Z _");
				return false;
			}
		}

		int s = n.length();
		for (int i = 1; i < s; i++)
		{
			char c = n.charAt(i);
			boolean bc = (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || c == '_';
			if (!bc)
			{
				invalidreson.append("invalid char =" + c);
				return false;
			}
		}
		return true;
	}

	/**
	 * chk string cannot contains '"\/<>&
	 * 
	 * @param title
	 * @return
	 */
	public static boolean checkVarTitle(String title, StringBuilder failedr)
	{
		final String INVALID_CS = "\'\"\\/<>&";
		final int len = INVALID_CS.length();
		for (int i = 0; i < len; i++)
		{
			char c = INVALID_CS.charAt(i);
			if (title.indexOf(c) >= 0)
			{
				failedr.append("cannot contains ").append(INVALID_CS);
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断两个字符串在忽略null或Empty情况下是否相同 其中null和Empty认为相同
	 * 
	 * @param str1
	 * @param str2
	 * @return
	 */
	public static boolean checkStrEqualsIgnoreNullEmpty(String str1, String str2)
	{
		if (isNullOrEmpty(str1))
		{
			return isNullOrEmpty(str2);
		}

		return str1.equals(str2);
	}

	public static int getBytesLen(String s, String encoding)
	{
		if (s == null)
			return 0;

		if ("utf-8".equalsIgnoreCase(encoding) || "utf8".equalsIgnoreCase(encoding))
		{
			int c = 0;
			int l = s.length();
			for (int i = 0; i < l; i++)
			{
				if (s.charAt(i) > 255)
					c += 3;
				else
					c++;
			}
			return c;
		}

		if ("utf-16".equalsIgnoreCase(encoding) || "utf16".equalsIgnoreCase(encoding))
		{
			return s.length() * 2;
		}

		if (Convert.isNullOrEmpty(encoding))
			return s.length();
		else
			return s.length() * 2;
	}

	// /**
	// * 判断是否是全角字符
	// *
	// * @param c
	// * @return
	// */
	// public static boolean isQjChar(char c)
	// {
	// int i = (int) c;
	// int f1 = i & 0xF0;
	// int f2 = i & 0x0F;
	// f1 = f1 >> 8;
	// return f1 == 0xA3;
	// }
	//
	// public static char transQjToBj(char c)
	// {
	// int i = (int) c;
	//
	// int f1 = i & 65280;// 0xFF00 ;
	// int f2 = i & 255;// 0x00FF ;
	//
	// f1 = f1 >>> 8;
	// System.out.println("c=" + c + " i=" + i + " f1=" + f1 + " f2=" + f2);
	// if (f1 == 0xA3)
	// {
	// if (f2 > 128)
	// return (char) (f2 - 128);
	// return c;
	// }
	// return c;
	// }
	//
	// /**
	// * 判断是否是半角字符
	// *
	// * @param c
	// * @return
	// */
	// public static boolean isBjChar(char c)
	// {
	// int i = (int) c;
	// return i >= 32 && i <= 126;
	// }

	static String QJCS = "｀～！＠＃＄％︿＆×（）＿＋－＝｛｝［］｜＼＂＇：；＜＞，．？／";
	static String BJCS = "`~!@#$%^&*()_+-={}[]|\\\"\':;<>,.?/";

	static HashMap<Character, Character> QJ2BJ = new HashMap<Character, Character>();
	static
	{
		for (int i = 0; i < 26; i++)
		{
			QJ2BJ.put((char) ('ａ' + i), (char) ('a' + i));
			QJ2BJ.put((char) ('Ａ' + i), (char) ('A' + i));
		}

		for (int i = 0; i < 10; i++)
			QJ2BJ.put((char) ('０' + i), (char) ('0' + i));

		int len = QJCS.length();
		for (int i = 0; i < len; i++)
			QJ2BJ.put(QJCS.charAt(i), BJCS.charAt(i));
	}

	/**
	 * 把字符串转换为半角字符 例如 ａｂｃｄｅｆｇ -> abcdef
	 * 
	 * @param s
	 * @return
	 */
	public static String toBj(String s)
	{
		if (Convert.isNullOrTrimEmpty(s))
			return s;

		try
		{
			int l = s.length();
			StringBuilder sb = new StringBuilder(l);
			// String tmps = null;
			// byte[] b = null;

			for (int i = 0; i < l; i++)
			{
				char tmpc = s.charAt(i);
				Character tmpbc = QJ2BJ.get(tmpc);
				if (tmpbc == null)
					sb.append(tmpc);
				else
					sb.append(tmpbc);
			}
			return sb.toString();
		} catch (Exception e)
		{
			return s;
		}
	}

	// public static String toBj(String s)
	// {
	// if (Convert.isNullOrTrimEmpty(s))
	// return s;
	//
	// try
	// {
	// int l = s.length();
	// StringBuilder sb = new StringBuilder(l);
	// String tmps = null;
	// byte[] b = null;
	//
	// for (int i = 0; i < l; i++)
	// {
	// tmps = "" + s.charAt(i);
	// b = tmps.getBytes("unicode");
	//
	// if (b[3] == -1)
	// {
	// b[2] = (byte) (b[2] + 32);
	// b[3] = 0;
	//
	// sb.append(new String(b, "unicode"));
	// }
	// else
	// {
	// sb.append(tmps);
	// }
	// }
	// return sb.toString();
	// }
	// catch (Exception e)
	// {
	// return s;
	// }
	// }

	/**
	 * 得到某一年的起始时间和结束时间
	 * 
	 * @param year
	 * @return
	 */
	public static long[] getYearStartEnd(int year)
	{
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, 0);
		c.set(Calendar.DAY_OF_MONTH, 1);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);

		long st = c.getTimeInMillis();

		c.set(Calendar.MONTH, 11);
		c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
		c.set(Calendar.MILLISECOND, 999);

		long et = c.getTimeInMillis();
		return new long[] { st, et };
	}

	/**
	 * 得到某一月份的起始结束时间
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	public static long[] getMonthStartEnd(int year, int month)
	{
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month - 1);
		c.set(Calendar.DAY_OF_MONTH, 1);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);

		long st = c.getTimeInMillis();

		c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
		c.set(Calendar.MILLISECOND, 999);

		long et = c.getTimeInMillis();
		return new long[] { st, et };
	}

	public static List<long[]> getWeekStartEndsInMonth(int year, int mon)
	{
		return getWeekStartEndsInMonth(year, mon, false);
	}

	/**
	 * 获得一个月之内的所有周的起始结束时间 第一周和最后一周如果有天不在本月之内,则排除
	 * 
	 * @param year
	 * @param mon
	 * @param b_week_start_monday
	 *            是否用星期一作为周的开始,缺省是星期天作为周开始
	 * @return
	 */
	public static List<long[]> getWeekStartEndsInMonth(int year, int mon, boolean b_week_start_monday)
	{
		List<long[]> rets = new ArrayList<long[]>();
		Calendar sc = Calendar.getInstance();
		sc.set(Calendar.YEAR, year);
		sc.set(Calendar.MONTH, mon - 1);
		sc.set(Calendar.DAY_OF_MONTH, 1);

		sc.set(Calendar.HOUR_OF_DAY, 0);
		sc.set(Calendar.MINUTE, 0);
		sc.set(Calendar.SECOND, 0);
		sc.set(Calendar.MILLISECOND, 0);
		// 第一周
		long s1 = sc.getTimeInMillis();

		Calendar ec = Calendar.getInstance();
		ec.setTimeInMillis(s1);

		int df = ec.get(Calendar.DAY_OF_WEEK);
		if (b_week_start_monday)
		{
			if (df != Calendar.SUNDAY)
				ec.add(Calendar.DAY_OF_MONTH, Calendar.SATURDAY + 1 - df);
		}
		else
		{
			ec.add(Calendar.DAY_OF_MONTH, Calendar.SATURDAY - df);
		}
		ec.set(Calendar.HOUR_OF_DAY, 23);
		ec.set(Calendar.MINUTE, 59);
		ec.set(Calendar.SECOND, 59);
		ec.set(Calendar.MILLISECOND, 999);
		long e1 = ec.getTimeInMillis();
		rets.add(new long[] { s1, e1 });

		sc.set(Calendar.DAY_OF_MONTH, ec.get(Calendar.DAY_OF_MONTH) - 6);

		for (int i = 0; i < 3; i++)
		{
			sc.add(Calendar.DAY_OF_MONTH, 7);
			ec.add(Calendar.DAY_OF_MONTH, 7);
			rets.add(new long[] { sc.getTimeInMillis(), ec.getTimeInMillis() });
		}

		for (int i = 0; i < 2; i++)
		{
			sc.add(Calendar.DAY_OF_MONTH, 7);
			if (sc.get(Calendar.MONTH) + 1 != mon)
				return rets;

			ec.add(Calendar.DAY_OF_MONTH, 7);
			if (ec.get(Calendar.MONTH) + 1 == mon)
			{
				rets.add(new long[] { sc.getTimeInMillis(), ec.getTimeInMillis() });
			}
			else
			{// read end of month day
				ec.add(Calendar.MONTH, -1);
				ec.set(Calendar.DAY_OF_MONTH, ec.getActualMaximum(Calendar.DAY_OF_MONTH));
				rets.add(new long[] { sc.getTimeInMillis(), ec.getTimeInMillis() });
				break;
			}
		}

		return rets;
	}

	static class LineReader
	{
		String strs = null;
		int idx = 0;

		public LineReader(String s)
		{
			strs = s;
		}

		public String readLine()
		{
			if (this.idx == this.strs.length())
				return null;
			int i;
			for (i = this.idx; i < this.strs.length(); i++)
			{
				if (this.strs.charAt(i) == '\n')
				{// find line
					String tmps = this.strs.substring(this.idx, i + 1).trim();
					// alert(tmps) ;
					this.idx = i + 1;
					return tmps;
				}
			}
			if (i == this.strs.length())
			{
				String tmps = this.strs.substring(this.idx).trim();
				this.idx = this.strs.length();
				return tmps;
			}
			else
			{
				return null;
			}
		}
	}

	/**
	 * 对输入形如 xx=yy 的字符串进行映射转换
	 * 
	 * @param inputs
	 * @return
	 */
	public static HashMap<String, String> transPropStrToMap(String inputs)
	{
		HashMap<String, String> ret = new HashMap<String, String>();
		if (inputs == null)
			return ret;

		BufferedReader br = new BufferedReader(new StringReader(inputs));
		String line = null;

		try
		{
			while ((line = br.readLine()) != null)
			{
				line = line.trim();
				if ("".equals(line))
					continue;

				int p = line.indexOf('=');
				if (p < 0)
				{
					ret.put(line, "");
				}
				else
				{
					ret.put(line.substring(0, p).trim(), line.substring(p + 1).trim());
				}
			}
			return ret;
		} catch (IOException ioe)
		{
			return ret;
		}
	}

	public static String transMapToPropStr(HashMap<String, String> mp)
	{
		StringBuilder ret = new StringBuilder();
		for (Map.Entry<String, String> n2v : mp.entrySet())
		{
			ret.append(n2v.getKey() + "=" + n2v.getValue() + "\r\n");
		}

		return ret.toString();
	}

	/**
	 * 从流中读取字典映射
	 * 
	 * @param ins
	 * @param charset
	 * @return
	 * @throws Exception
	 */
	public static HashMap<String, String> readStringMapFromStream(InputStream ins, String charset) throws Exception
	{
		HashMap<String, String> rets = new HashMap<String, String>();

		BufferedReader br = new BufferedReader(new InputStreamReader(ins, charset));
		String line;
		while ((line = br.readLine()) != null)
		{
			line = line.trim();

			if ("".equals(line))
				continue;

			if (line.startsWith("#"))
				continue;

			int p = line.indexOf('=');
			if (p > 0)
			{
				rets.put(line.substring(0, p), line.substring(p + 1));
			}
			else
			{
				rets.put(line, "");
			}
		}

		return rets;
	}

	//////////////
	public static String byteArray2HexStr(byte[] bs)
	{
		return byteArray2HexStr(bs, 0, bs.length);
	}

	public static String byteArray2HexStr(byte[] bs, int offset, int len)
	{
		return byteArray2HexStr(bs, offset, len, null);
	}

	public static String byteArray2HexStr(byte[] bs, int offset, int len, String delim)
	{
		if (bs == null)
			return null;

		if (bs.length == 0 || len <= 0)
			return "";

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < len; i++)
		{
			int tmpi = 255;
			tmpi = tmpi & bs[i + offset];
			String s = Integer.toHexString(tmpi);
			if (s.length() == 1)
				s = "0" + s;
			sb.append(s);
			if (delim != null)
				sb.append(delim);
		}
		return sb.toString().toUpperCase();
	}

	public static byte[] hexStr2ByteArray(String hexstr)
	{
		if (hexstr == null)
			return null;

		if (hexstr.equals(""))
			return new byte[0];

		int s = hexstr.length() / 2;
		byte[] ret = new byte[s];
		for (int i = 0; i < s; i++)
		{
			ret[i] = (byte) Short.parseShort(hexstr.substring(i * 2, i * 2 + 2), 16);
		}
		return ret;
	}

	// public static void sort(List<?> ll)
	// {
	// if (ll == null)
	// return;
	//
	// if (ll.size() <= 0)
	// return;
	//
	// Comparable<?>[] ss = new Comparable[ll.size()];
	//
	// ll.toArray(ss);
	//
	// Arrays.sort(ss);
	//
	// ll.clear();
	// for (Comparable<?> c : ss)
	// {
	// ll.add(c);
	// }
	// }
	//
	// public static void sort(List ll, Comparator cp)
	// {
	// if (ll == null)
	// return;
	//
	// if (ll.size() <= 0)
	// return;
	//
	// Object[] ss = new Object[ll.size()];
	//
	// ll.toArray(ss);
	//
	// Arrays.sort(ss, cp);
	//
	// ll.clear();
	// for (Object c : ss)
	// {
	// ll.add(c);
	// }
	// }

	/**
	 * 根据jsp页面中的http请求获得页面对应的相关模块（构件）名称
	 * 
	 * @param req
	 * @return
	 */
	public static String getModuleNameByHttpReq(HttpServletRequest req)
	{
		String cp = req.getContextPath();
		if (Convert.isNullOrEmpty(cp))
			return null;

		if (cp.equals("/"))
			return null;

		if (cp.startsWith("/"))
			return cp.substring(1);
		else
			return cp;
	}

	public static String UIClient_getLocAppDir(HttpServletRequest req)
	{
		final String UI_CLIENT_LOC_APP_DIR = "_ui_client_loc_app_dir_";
		String r = req.getHeader(UI_CLIENT_LOC_APP_DIR);
		if (r == null)
			return "";
		return r;
	}

	public static String encodeHexUrl(String pv)
	{
		if (pv == null)
			return null;

		if (pv.equals(""))
			return "";

		try
		{
			byte[] buf = pv.getBytes("UTF-8");
			return "=h=" + Convert.byteArray2HexStr(buf);
		} catch (UnsupportedEncodingException ee)
		{
			throw new RuntimeException(ee.getMessage());
		}
	}

	public static String decodeHexUrl(String hexu)
	{
		if (hexu == null)
			return null;

		if (hexu.startsWith("=h="))
		{
			hexu = hexu.substring(3);
			byte[] buf = Convert.hexStr2ByteArray(hexu);
			if (buf == null)
				return null;
			try
			{
				return new String(buf, "UTF-8");
			} catch (UnsupportedEncodingException ee)
			{
				throw new RuntimeException(ee.getMessage());
			}
		}
		else if (hexu.startsWith("=u="))
		{
			hexu = hexu.substring(3);

			try
			{
				// return URLDecoder.decode(hexu, "UTF-8");
				String s = URLDecoder.decode(hexu, "UTF-8");
				if (s.indexOf('%') < 0)
					return s;

				return URLDecoder.decode(URLDecoder.decode(hexu, "US-ASCII"), "UTF-8");
			} catch (Exception e)
			{
				throw new RuntimeException(e.getMessage());
			}
		}
		else
		{
			return hexu;
		}
	}

	public static String decodeSmartUrl(String u)
	{
		if (u == null)
			return null;
		if ("".equals(u))
			return u;

		if (u.startsWith("=h="))
		{
			return decodeHexUrl(u);
		}
		else if (u.startsWith("=u="))
		{// 客户端脚本提供的utf8urlencode做的编码--并且该编码没有通过ie标准提交
			// 该url通过ajax方式直接提交的
			u = u.substring(3);

			String s = null;
			try
			{
				// return URLDecoder.decode(URLDecoder.decode(u,
				// "US-ASCII"),"UTF-8") ;
				s = URLDecoder.decode(u, "UTF-8");
				if (s.indexOf('%') < 0)
					return s;

				return URLDecoder.decode(URLDecoder.decode(u, "US-ASCII"), "UTF-8");
			} catch (Exception e)
			{
				// throw new RuntimeException(e.getMessage());
				return s;
			}
		}
		else
		{
			return u;
		}
	}

	private static final int BYTE_MASK = 0x0f;
	private static final char[] HEX_DIGITS = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B',
			'C', 'D', 'E', 'F' };

	/**
	 * 根据某种键值转换成十六进制表示的文件名
	 * 
	 * 该方法被James项目中，存储收到的邮件body用到
	 * 
	 * 同时，会被WebMail的支持用到
	 * 
	 * @param key
	 * @return
	 */
	public static String encodeKeyToFileName(String key)
	{
		final byte[] bytes = key.getBytes();
		final char[] buffer = new char[bytes.length << 1];

		for (int i = 0, j = 0; i < bytes.length; i++)
		{
			final int k = bytes[i];
			buffer[j++] = HEX_DIGITS[(k >>> 4) & BYTE_MASK];
			buffer[j++] = HEX_DIGITS[k & BYTE_MASK];
		}

		return new String(buffer);
	}

	/**
	 * 文件名反转回键值
	 * 
	 * 该方法被James项目中，存储收到的邮件body用到
	 * 
	 * @param filename
	 * @return
	 */
	public static String decodeKeyFromFileName(String filename)
	{
		final int size = filename.length();
		final byte[] bytes = new byte[size >>> 1];

		for (int i = 0, j = 0; i < size; j++)
		{
			bytes[j] = Byte.parseByte(filename.substring(i, i + 2), 16);
			i += 2;
		}

		return new String(bytes);
	}

	public static Date calHourStart(Date one_day)
	{
		if (one_day == null)
			return null;

		Calendar cal = Calendar.getInstance();
		cal.setTime(one_day);
		// cal.set(Calendar.HOUR_OF_DAY, 0) ;
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		return cal.getTime();
	}

	public static Date calHourEnd(Date one_day)
	{
		if (one_day == null)
			return null;

		Calendar cal = Calendar.getInstance();
		cal.setTime(one_day);
		// cal.set(Calendar.HOUR_OF_DAY, 0) ;
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 999);

		return cal.getTime();
	}

	/**
	 * 根据输入的时间对象，计算该时间对于的某一天的起始时间 也就是 yyyy-MM-dd 00:00:00:000
	 * 
	 * @param one_day
	 * @return
	 */
	public static Date calDayStart(Date one_day)
	{
		if (one_day == null)
			return null;

		Calendar cal = Calendar.getInstance();
		cal.setTime(one_day);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		return cal.getTime();
	}

	public static Date calDayStart(Calendar one_day)
	{
		if (one_day == null)
			return null;

		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(one_day.getTimeInMillis());

		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		return cal.getTime();
	}

	/**
	 * 根据输入的时间对象，计算该时间对于的某一天的结束时间 也就是 yyyy-MM-dd 23:59:59:999
	 * 
	 * @param one_day
	 * @return
	 */
	public static Date calDayEnd(Date one_day)
	{
		if (one_day == null)
			return null;

		Calendar cal = Calendar.getInstance();
		cal.setTime(one_day);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 999);

		return cal.getTime();
	}

	public static Date calDayEnd(Calendar one_day)
	{
		if (one_day == null)
			return null;

		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(one_day.getTimeInMillis());
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 999);

		return cal.getTime();
	}

	public static Date calWeekStart(Date one_day)
	{
		if (one_day == null)
			return null;

		Calendar cal = Calendar.getInstance();
		cal.setTime(one_day);
		cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		return cal.getTime();
	}

	public static Date calWeekEnd(Date one_day)
	{
		if (one_day == null)
			return null;

		Calendar cal = Calendar.getInstance();
		cal.setTime(one_day);
		cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 999);

		return cal.getTime();
	}

	public static Date calMonthStart(Date one_day)
	{
		if (one_day == null)
			return null;

		Calendar cal = Calendar.getInstance();
		cal.setTime(one_day);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		return cal.getTime();
	}

	public static Date calMonthEnd(Date one_day)
	{
		if (one_day == null)
			return null;

		Calendar cal = Calendar.getInstance();
		cal.setTime(one_day);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 999);

		return cal.getTime();
	}

	/**
	 * 计算绝对路径
	 * 
	 * @param parent_p
	 *            父路径
	 * @param p
	 *            被计算路径 /aa/bb表示已经是绝对路径 ./aa/bb aa/bb 当前路径 ../上一级路径
	 * @return
	 */
	public static String calAbsPath(String parent_p, String p)
	{
		if (p.startsWith("/"))
			return p;

		if (!parent_p.endsWith("/"))
			parent_p += "/";

		if (p.startsWith("./"))
		{
			return parent_p + p.substring(2);
		}

		StringTokenizer st = new StringTokenizer(parent_p, "/\\");
		ArrayList<String> pps = new ArrayList<String>();
		while (st.hasMoreTokens())
			pps.add(st.nextToken());

		if (p.startsWith("../"))
		{
			while (p.startsWith("../"))
			{
				p = p.substring(3);
				if (pps.size() > 0)
					pps.remove(pps.size() - 1);
			}

			StringBuilder sb = new StringBuilder();
			sb.append("/");
			for (String s : pps)
			{
				sb.append(s).append("/");
			}

			sb.append(p);

			return sb.toString();
		}

		return parent_p + p;
	}

	/**
	 * 根据当前url，计算第二个参数的路径
	 * 
	 * @param parent_p
	 * @param p
	 * @return
	 */
	public static String calAbsHttpUrl(String cur_url, String p)
	{
		if (p.toLowerCase().startsWith("http"))
			return p;

		// StringBuilder sb = new StringBuilder() ;

		if (!cur_url.toLowerCase().startsWith("http"))
		{
			cur_url = "http://" + cur_url;
		}

		String url_prefix = null;
		String parent_p = "/";
		int k = cur_url.indexOf('/', 8);
		if (k <= 0)
		{
			url_prefix = cur_url;
		}
		else
		{
			url_prefix = cur_url.substring(0, k);
			parent_p = cur_url.substring(k);
			if (!parent_p.endsWith("/"))
			{
				int s = parent_p.lastIndexOf('/');
				if (s > 0)
					parent_p = parent_p.substring(0, s);
			}
		}

		if (p.startsWith("./"))
		{
			return parent_p + p.substring(2);
		}

		StringTokenizer st = new StringTokenizer(parent_p, "/\\");
		ArrayList<String> pps = new ArrayList<String>();
		while (st.hasMoreTokens())
			pps.add(st.nextToken());

		if (p.startsWith("../"))
		{
			while (p.startsWith("../"))
			{
				p = p.substring(3);
				if (pps.size() > 0)
					pps.remove(pps.size() - 1);
			}

			StringBuilder sb = new StringBuilder();
			sb.append("/");
			for (String s : pps)
			{
				sb.append(s).append("/");
			}

			sb.append(p);

			return sb.toString();
		}

		return url_prefix + parent_p + p;
	}

	/**
	 * 根据一定的分割符切分支付串
	 * 
	 * @param str
	 * @param delimi
	 * @return
	 */
	public static List<String> splitStrWith(String str, String delimi)
	{
		if (isNullOrEmpty(str))
			return null;

		ArrayList<String> rets = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(str, delimi);
		while (st.hasMoreTokens())
			rets.add(st.nextToken());

		return rets;
	}

	public static LinkedList<String> splitStrWithLinkedList(String str, String delimi)
	{
		if (isNullOrEmpty(str))
			return null;

		LinkedList<String> rets = new LinkedList<String>();
		StringTokenizer st = new StringTokenizer(str, delimi);
		while (st.hasMoreTokens())
			rets.add(st.nextToken());

		return rets;
	}

	/**
	 * 支持空字符串输出，如果切割符合前端或中间为空字符串，也输出
	 * 
	 * @param str
	 * @param delimi
	 * @param has_empty
	 * @return
	 */
	public static List<String> splitStrWith(String str, String delimi, boolean has_empty)
	{
		if (isNullOrEmpty(str))
			return null;

		ArrayList<String> rets = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(str, delimi, true);
		boolean last_t = false;
		while (st.hasMoreTokens())
		{
			String nt = st.nextToken();
			if (nt.length() == 1 && delimi.contains(nt))
			{// 本次输出为分割符
				if (last_t)
				{// 上一次已经是切分串，不做任何处理
					last_t = false;
				}
				else
				{// 上一次是分隔符，此次还是，则加入一个空字符串
					rets.add("");
				}
			}
			else
			{
				rets.add(nt);
				last_t = true;
			}
		}

		return rets;
	}

	public static String combineStrWith(List<String> ss, char delimi)
	{
		if (ss == null)
			return null;

		StringBuilder sb = new StringBuilder();
		for (String s : ss)
		{
			sb.append(delimi).append(s);
		}
		if (ss.size() > 0)
			return sb.substring(1);
		else
			return "";
	}

	public static String combineWith(List<?> ss, char delimi)
	{
		if (ss == null)
			return null;

		StringBuilder sb = new StringBuilder();
		for (Object s : ss)
		{
			sb.append(delimi).append(s);
		}
		if (ss.size() > 0)
			return sb.substring(1);
		else
			return "";
	}

	public static String combineWith(Object[] ss, char delimi)
	{
		if (ss == null)
			return null;

		StringBuilder sb = new StringBuilder();
		for (Object s : ss)
		{
			sb.append(delimi).append(s);
		}
		if (ss.length > 0)
			return sb.substring(1);
		else
			return "";
	}

	/**
	 * 解析url参数字符串 形如：aa=bb&xx=1
	 * 
	 * @param urlpstr
	 * @return
	 */
	public static HashMap<String, String> parseUrlParamStr(String urlpstr)
	{
		if (urlpstr == null)
			return null;
		HashMap<String, String> ret = new HashMap<String, String>();
		StringTokenizer st = new StringTokenizer(urlpstr, "&");
		while (st.hasMoreTokens())
		{
			String tmps = st.nextToken();
			int i = tmps.indexOf('=');
			if (i <= 0)
				ret.put(tmps, "");
			else
				ret.put(tmps.substring(0, i), tmps.substring(i + 1));
		}
		return ret;
	}
	
	public static HashMap<String,String> parseFromRequest(HttpServletRequest req,String prefix)
	{
		HashMap<String,String> rets = new HashMap<>() ;
		for(Enumeration<String> ens = req.getParameterNames();ens.hasMoreElements();)
		{
			String n = ens.nextElement() ;
			String v = req.getParameter(n) ;
			if(Convert.isNotNullEmpty(prefix))
			{
				if(!n.startsWith(prefix))
					continue ;
				n = n.substring(prefix.length()) ;
			}
			rets.put(n,v) ;
		}
		return rets;
	}

	/**
	 * 转换字符串到适合的文件名称
	 * 
	 * @param fn
	 * @return
	 */
	public static String fitFileName(String fn)
	{
		if (fn == null)
			return null;

		fn = fn.replace("/", "");
		fn = fn.replace("\\", "");
		fn = fn.replace(":", "");
		fn = fn.replace("*", "");
		fn = fn.replace("?", "");
		fn = fn.replace("\"", "");
		fn = fn.replace("<", "");
		fn = fn.replace(">", "");
		fn = fn.replace("|", "");

		return fn;
	}

	/**
	 * 把字符串中的换行进行\r \n替换，使得变成一行内容
	 * 
	 * @param txt
	 * @return
	 */
	public static String transStrToLine(String txt)
	{
		if (txt == null)
			return null;

		if (txt.equals(""))
			return "";

		StringBuilder sb = new StringBuilder();
		int len = txt.length();
		for (int i = 0; i < len; i++)
		{
			char c = txt.charAt(i);
			switch (c)
			{
			case '\r':
				sb.append("\\r");
				break;
			case '\n':
				sb.append("\\n");
				break;
			default:
				sb.append(c);
				break;
			}
		}
		return sb.toString();
	}

	/**
	 * 把通过transStrToLine转换的单行字符串，进行恢复
	 * 
	 * @param linestr
	 * @return
	 */
	public static String transLineToStr(String linestr)
	{
		if (linestr == null)
			return null;

		if (linestr.equals(""))
			return "";

		StringBuilder sb = new StringBuilder();
		int len = linestr.length();
		for (int i = 0; i < len; i++)
		{
			char c = linestr.charAt(i);
			if (c == '\\' && i + 1 < len)
			{
				char nc = linestr.charAt(i + 1);
				if (nc == 'r')
				{
					sb.append('\r');
					i++;
					continue;
				}
				else if (nc == 'n')
				{
					sb.append('\n');
					i++;
					continue;
				}
			}
			sb.append(c);
		}
		return sb.toString();
	}

	/**
	 * 平均数过滤器
	 * 
	 * @author zzj
	 */
	public static class AveDataFilter
	{
		double[] dataArr = null;

		int pos = 0;

		boolean bHasData = false;

		double sum = 0;

		public AveDataFilter(int buflen)
		{
			dataArr = new double[buflen];
		}

		public boolean hasData()
		{
			return bHasData;
		}

		public double getLastData()
		{
			int p = pos - 1;
			if (p < 0)
				p = dataArr.length - 1;
			return dataArr[p];
		}

		public synchronized double insertData(double d)
		{
			if (!bHasData)
			{
				bHasData = true;
				for (int i = 0; i < dataArr.length; i++)
					dataArr[i] = d;
				sum = d * dataArr.length;
				return d;
			}

			sum -= dataArr[pos];
			dataArr[pos] = d;
			sum += d;
			pos++;
			if (pos >= dataArr.length)
				pos = 0;

			return sum / dataArr.length;
		}
	}

	public static void BS_transLong2Bytes(long data, byte[] bytes, int offset)
	{
		// byte[] bytes = new byte[8];
		bytes[offset] = (byte) (data & 0xff);
		bytes[offset + 1] = (byte) ((data >> 8) & 0xff);
		bytes[offset + 2] = (byte) ((data >> 16) & 0xff);
		bytes[offset + 3] = (byte) ((data >> 24) & 0xff);
		bytes[offset + 4] = (byte) ((data >> 32) & 0xff);
		bytes[offset + 5] = (byte) ((data >> 40) & 0xff);
		bytes[offset + 6] = (byte) ((data >> 48) & 0xff);
		bytes[offset + 7] = (byte) ((data >> 56) & 0xff);
	}

	public static long BS_transBytes2Long(byte[] bytes, int offset)
	{
		return (0xffL & (long) bytes[offset]) | (0xff00L & ((long) bytes[offset + 1] << 8))
				| (0xff0000L & ((long) bytes[offset + 2] << 16)) | (0xff000000L & ((long) bytes[offset + 3] << 24))
				| (0xff00000000L & ((long) bytes[offset + 4] << 32))
				| (0xff0000000000L & ((long) bytes[offset + 5] << 40))
				| (0xff000000000000L & ((long) bytes[offset + 6] << 48))
				| (0xff00000000000000L & ((long) bytes[offset + 7] << 56));
	}

	public static void BS_transDouble2Bytes(double d, byte[] bytes, int offset)
	{
		long longb = Double.doubleToLongBits(d);
		BS_transLong2Bytes(longb, bytes, offset);
	}

	public static double BS_transBytes2Double(byte[] bytes, int offset)
	{
		long l = BS_transBytes2Long(bytes, offset);
		return Double.longBitsToDouble(l);
	}

	//
	//
	/**
	 * World Geodetic System ==> Mars Geodetic System
	 * 对国家经纬度做火星坐标的转换（使之适合国内的地图显示）
	 */
	public static double[] POS_transGPS2MarsGeo(double wg_lng, double wg_lat)
	{
		double mglat, mglng;
		if (POS_outOfChina(wg_lat, wg_lng))
		{
			mglat = wg_lat;
			mglng = wg_lng;
			return new double[] { mglat, mglng };
		}
		double dLat = POS_transformLat(wg_lng - 105.0, wg_lat - 35.0);
		double dLon = POS_transformLon(wg_lng - 105.0, wg_lat - 35.0);
		double radLat = wg_lat / 180.0 * Math.PI;
		double magic = Math.sin(radLat);
		final double ee = 0.00669342162296594323;
		final double a = 6378245.0;
		magic = 1 - ee * magic * magic;
		double sqrtMagic = Math.sqrt(magic);
		dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * Math.PI);
		dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * Math.PI);
		mglat = wg_lat + dLat;
		mglng = wg_lng + dLon;

		// x_add = x_add + h_add * 0.001 + yj_sin2(wg_time * 0.0174532925199433)
		// + random_yj();
		// y_add = y_add + h_add * 0.001 + yj_sin2(wg_time * 0.0174532925199433)
		// + random_yj();

		return new double[] { mglng, mglat };
	}

	private static boolean POS_outOfChina(double lat, double lon)
	{
		if (lon < 72.004 || lon > 137.8347)
			return true;
		if (lat < 0.8293 || lat > 55.8271)
			return true;
		return false;
	}

	private static double POS_transformLat(double x, double y)
	{
		double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * Math.sqrt(Math.abs(x));
		ret += (20.0 * Math.sin(6.0 * x * Math.PI) + 20.0 * Math.sin(2.0 * x * Math.PI)) * 2.0 / 3.0;
		ret += (20.0 * Math.sin(y * Math.PI) + 40.0 * Math.sin(y / 3.0 * Math.PI)) * 2.0 / 3.0;
		ret += (160.0 * Math.sin(y / 12.0 * Math.PI) + 320 * Math.sin(y * Math.PI / 30.0)) * 2.0 / 3.0;
		return ret;
	}

	private static double POS_transformLon(double x, double y)
	{
		double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * Math.sqrt(Math.abs(x));
		ret += (20.0 * Math.sin(6.0 * x * Math.PI) + 20.0 * Math.sin(2.0 * x * Math.PI)) * 2.0 / 3.0;
		ret += (20.0 * Math.sin(x * Math.PI) + 40.0 * Math.sin(x / 3.0 * Math.PI)) * 2.0 / 3.0;
		ret += (150.0 * Math.sin(x / 12.0 * Math.PI) + 300.0 * Math.sin(x / 30.0 * Math.PI)) * 2.0 / 3.0;
		return ret;
	}

	public static HashMap<String, String> loadPropFile(File f)
	{
		if (!f.exists() || f.isDirectory())
			return null;

		HashMap<String, String> ret = new HashMap<String, String>();
		try (FileInputStream fis = new FileInputStream(f);
				InputStreamReader isr = new InputStreamReader(fis);
				BufferedReader br = new BufferedReader(isr);)
		{

			String ln = null;
			while ((ln = br.readLine()) != null)
			{
				ln = ln.trim();
				if ("".equals(ln) || ln.startsWith("#"))
					continue;

				int i = ln.indexOf('=');
				if (i > 0)
					ret.put(ln.substring(0, i).trim(), ln.substring(i + 1).trim());
				else
					ret.put(ln, "");
			}
		} catch (Exception e)
		{
			return null;
		}

		return ret;
	}

	public static void savePropFile(File f, HashMap<String, String> pnv) throws Exception
	{
		if (!f.getParentFile().exists())
			f.getParentFile().mkdirs();

		try (FileOutputStream fos = new FileOutputStream(f);
				OutputStreamWriter w = new OutputStreamWriter(fos, "UTF-8");)
		{

			if (pnv != null)
			{
				for (Map.Entry<String, String> n2v : pnv.entrySet())
				{
					w.write(n2v.getKey() + "=" + n2v.getValue() + "\r\n");
				}
				w.flush();
			}

		}

	}

	public static List<Element> getElementSubChildList(Element ele, String tagname)
	{
		int p0 = tagname.indexOf(':');
		if (p0 >= 0)
			tagname = tagname.substring(p0 + 1);
		return getElementSubChildList(ele, new String[] { tagname });
	}

	public static List<Element> getElementSubChildList(Element ele, String[] tagnames)
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

	public static Element[] getElementSubChild(Element ele, String tagname)
	{
		int p0 = tagname.indexOf(':');
		if (p0 >= 0)
			tagname = tagname.substring(p0 + 1);

		return getElementSubChild(ele, new String[] { tagname });
	}

	public static Element[] getElementSubChild(Element ele, String[] tagnames)
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

	public static Element[] getSubChildElement(Element ele, String tagname)
	{
		return getElementSubChild(ele, tagname);
	}

	/**
	 * 读取文件到字符串
	 * 
	 * @param f
	 * @param encod
	 * @return
	 */
	public static String readFileTxt(File f, String encod) throws IOException
	{
		byte[] bs = readFileBuf(f);
		if (isNullOrEmpty(encod))
			return new String(bs);
		else
			return new String(bs, encod);
	}

	public static byte[] readFileBuf(File f) throws IOException
	{
		if (f.length() > 10485760)
			throw new RuntimeException("file is too long");

		try (FileInputStream fis = new FileInputStream(f);)
		{
			int size = fis.available();
			byte[] buffer = new byte[size];
			fis.read(buffer);
			return buffer;
		}
	}

	public static List<String> readFileTxtLines(File f, String encod) throws IOException
	{
		if (f.length() > 10485760)
			throw new RuntimeException("file is too long");

		ArrayList<String> ss = new ArrayList<>();
		try (FileInputStream fis = new FileInputStream(f);)
		{
			InputStreamReader isr = null;
			if (Convert.isNullOrEmpty(encod))
				isr = new InputStreamReader(fis);
			else
				isr = new InputStreamReader(fis, encod);

			BufferedReader br = new BufferedReader(isr);
			String ln;
			while ((ln = br.readLine()) != null)
			{
				ln = ln.trim();
				if (ln.contentEquals("") || ln.startsWith("#"))
					continue;
				ss.add(ln);
			}
		}
		return ss;
	}

	public static void validateNotNull(final Object object, final String message)
	{
		if (object == null)
		{
			throw new IllegalArgumentException(message);
		}
	}

	public static void validateNotEmpty(final String object, final String message)
	{
		if (isEmptyOrWhitespace(object))
		{
			throw new IllegalArgumentException(message);
		}
	}

	public static void validateNotEmpty(final Collection<?> object, final String message)
	{
		if (object == null || object.size() == 0)
		{
			throw new IllegalArgumentException(message);
		}
	}

	public static void validateNotEmpty(final Object[] object, final String message)
	{
		if (object == null || object.length == 0)
		{
			throw new IllegalArgumentException(message);
		}
	}

	public static void validateContainsNoNulls(final Iterable<?> collection, final String message)
	{
		for (final Object object : collection)
		{
			validateNotNull(object, message);
		}
	}

	public static void validateContainsNoEmpties(final Iterable<String> collection, final String message)
	{
		for (final String object : collection)
		{
			validateNotEmpty(object, message);
		}
	}

	public static void validateContainsNoNulls(final Object[] array, final String message)
	{
		for (final Object object : array)
		{
			validateNotNull(object, message);
		}
	}

	public static void validateIsTrue(final boolean condition, final String message)
	{
		if (!condition)
		{
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * jsp中用来支持判断request参数是否为空的判断支持
	 * 
	 * @param req
	 * @param out
	 * @param pns
	 * @return
	 */
	public static boolean checkReqEmpty(HttpServletRequest req, Writer out, String... pns) throws IOException
	{
		for (String pn : pns)
		{
			String pv = req.getParameter(pn);
			if (isNullOrEmpty(pv))
			{
				out.write("input " + pn + " is empty");
				return false;
			}
		}
		return true;
	}

	public static boolean checkReqEmpty(HttpServletRequest req, String errinf, Writer out, String... pns)
			throws IOException
	{
		for (String pn : pns)
		{
			String pv = req.getParameter(pn);
			if (isNullOrEmpty(pv))
			{
				out.write(errinf);
				return false;
			}
		}
		return true;
	}

	public static boolean deleteDir(File dir)
	{
		if (dir.isDirectory())
		{
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++)
			{
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success)
				{
					return false;
				}
			}
		}
		return dir.delete();
	}

	public static void main(String[] args) throws Exception
	{
		// String tmps =
		// "http://babelfish.altavista.com/babelfish/trurl_pagecontent?lp=zh_en&url=http%3A%2F%2Fwww.souln.cn"
		// ;
		// System.out.println(URLEncoder.encode(tmps,"UTF-8")) ;
		try
		{
			System.out.println(">>" + encodeKeyToFileName("Mail1218364208991-2"));
			System.out.println("res=" + Convert.toDecimalDigitsStr(0.34543333, 2));
			System.out.println("res=" + Convert.toDecimalDigitsStr(324.2321, 3));
			System.out.println("res=" + Convert.toDecimalDigitsStr(234658.34543333, 4));
			System.out.println("res=" + Convert.toDecimalDigitsStr(234658.34543778E5, 2));
		} catch (Exception eee)
		{
			eee.printStackTrace();
		}
	}
}

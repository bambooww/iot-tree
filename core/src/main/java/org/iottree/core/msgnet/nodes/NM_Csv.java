package org.iottree.core.msgnet.nodes;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeMid;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.util.Convert;
import org.iottree.core.util.Lan;
import org.json.JSONArray;
import org.json.JSONObject;

public class NM_Csv extends MNNodeMid
{
	static Lan lan = Lan.getLangInPk(NM_FileReader.class) ;
	
	public static enum OutTP
	{
		msg_per_row_jo,
		msg_per_row_jarr,
		single_msg;
		
		public String getTitle()
		{
			return lan.g("csv_"+this.name()) ;
		}
	}
	
	int skipFirstLines = 0 ;
	
	String colNames = null ;
	
	OutTP outTP = OutTP.msg_per_row_jo ;
	
	boolean parseNum = false;
	
	boolean parseBool = false; //parse true false
	
	private List<String> colNameList = null ;
	
	@Override
	public int getOutNum()
	{
		return 1;
	}

	@Override
	public String getTP()
	{
		return "csv";
	}

	@Override
	public String getTPTitle()
	{
		return "CSV";
	}

	@Override
	public String getColor()
	{
		return "#ddc069";
	}

	@Override
	public String getIcon()
	{
		return "\\uf0f6";
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.put("parse_num", parseNum) ;
		jo.put("out_tp", outTP.name()) ;
		jo.put("skip_first_lns", this.skipFirstLines) ;
		jo.putOpt("col_names", colNames) ;
		return jo;
	}
	
	

	@Override
	protected void setParamJO(JSONObject jo)
	{
		this.parseNum = jo.optBoolean("parse_num", false) ;
		this.outTP = OutTP.valueOf(jo.optString("out_tp","msg_per_row_jo")) ;
		if(this.outTP==null)
			this.outTP = OutTP.msg_per_row_jo ;
		this.skipFirstLines = jo.optInt("skip_first_lns", 0) ;
		this.colNames = jo.optString("col_names") ;
		colNameList = Convert.splitStrWith(this.colNames, ",|") ;
	}
	
	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg) throws Exception
	{
		String txt = msg.getPayloadStr() ;
		if(Convert.isNullOrEmpty(txt))
		{
			return null ;
		}
		StringReader sr = new StringReader(txt) ;
		BufferedReader br = new BufferedReader(sr) ;
		String ln = null;
		int cc = 0 ;
		if(this.outTP==OutTP.msg_per_row_jo || this.outTP==OutTP.msg_per_row_jarr )
		{
			while((ln=br.readLine())!=null)
			{
				cc ++ ;
				if(this.skipFirstLines>0 && cc<=this.skipFirstLines)
					continue ;
				
				List<Object> ss = parseCSVLine(ln) ;
				MNMsg outm = new MNMsg() ;
				if(this.outTP==OutTP.msg_per_row_jo)
				{
					JSONObject tmpjo = new JSONObject() ;
					int n = ss.size() ;
					for(int i = 0 ; i < n ; i ++)
					{
						String coln = ""+i ;
						if(colNameList!=null&&colNameList.size()>i)
							coln = colNameList.get(i) ;
						tmpjo.put(coln,ss.get(i)) ;
					}
					outm.asPayload(tmpjo);
				}
				else
				{
					JSONArray jarr = new JSONArray(ss) ;
					outm.asPayload(jarr);
				}
				this.RT_sendMsgOut(RTOut.createOutAll(outm));
			}
			return null ;
		}
	
		JSONArray pld = new JSONArray() ;
		while((ln=br.readLine())!=null)
		{
			cc ++ ;
			if(this.skipFirstLines>0 && cc<=this.skipFirstLines)
				continue ;
			
			List<Object> ss = parseCSVLine(ln) ;
			JSONArray jarr = new JSONArray(ss) ;
			pld.put(jarr) ;
		}
		MNMsg outm = new MNMsg() ;
		outm.asPayload(pld);
		this.RT_sendMsgOut(RTOut.createOutAll(outm));
		return null;
	}


	private List<Object> parseCSVLine(String line)
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
					Object objv = parseCurStr(lastInQuo,currentField.toString()) ;
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
		result.add(parseCurStr(lastInQuo,currentField.toString()));

		return result;
	}

	
	private Object parseCurStr(boolean lastInQuo,String tmps)
	{
		if(lastInQuo)
		{
			lastInQuo = false;
			return tmps ;
		}

			tmps = tmps.trim() ;
			if(parseNum)
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
}

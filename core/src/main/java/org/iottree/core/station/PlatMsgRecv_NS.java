package org.iottree.core.station;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.iottree.core.UAPrj;
import org.iottree.core.msgnet.IMNContainer;
import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeMid;
import org.iottree.core.msgnet.MNNodeStart;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.msgnet.util.ValPack;
import org.iottree.core.store.SourceJDBC;
import org.iottree.core.store.StoreManager;
import org.iottree.core.util.Convert;
import org.json.JSONObject;

public class PlatMsgRecv_NS extends MNNodeStart
{
		public static final String TP = "plat_msg_recv" ;
		//String dbName = null;

		//ArrayList<String> station_prjs = new ArrayList<>();
		
		private String matchTopics = null ;
		
		
		public PlatMsgRecv_NS()
		{
		}

		@Override
		public String getTP()
		{
			return TP;
		}

		@Override
		public String getTPTitle()
		{
			return "Platform recv from Station";
		}

		@Override
		public int getOutNum()
		{
			return 2;
		}

		@Override
		public String getColor()
		{
			return "#1d90ad";
		}

		@Override
		public String getIcon()
		{
			return "\\uf148";
		}
		
		@Override
		public boolean isFitForPrj(UAPrj prj)
		{
			if(prj==null)
				return false;
			return prj.isPrjPStationIns() ;
		}
		
		

		private UAPrj getPrj()
		{
			IMNContainer mnc = this.getBelongTo().getBelongTo().getBelongTo();
			if (mnc == null || !(mnc instanceof UAPrj))
				return null;

			return (UAPrj) mnc;
		}
		
		@Override
		public boolean isParamReady(StringBuilder failedr)
		{
			UAPrj prj = getPrj() ;
			if(prj==null)
			{
				failedr.append("no prj found") ;
				return false;
			}
			PStation ps = prj.getPrjPStationInsDef() ;
			if(ps==null)
			{
				failedr.append("prj is not run as remote station instance") ;
				return false;
			}
			
			if(Convert.isNullOrEmpty(this.matchTopics))
			{
				failedr.append("no match topic set") ;
				return false;
			}
			
			return true ;
		}

		@Override
		public JSONObject getParamJO()
		{
			JSONObject jo = new JSONObject() ;
			jo.putOpt("match_topics", this.matchTopics) ;
			return jo ;
		}

		@Override
		protected void setParamJO(JSONObject jo)
		{
			this.matchTopics = jo.optString("match_topics") ;
			parseMatchTopic();
		}

		@Override
		public String RT_getOutColor(int idx)
		{
			if(idx==0)
				return "#9cd037";
			else if(idx==1)
				return "yellow" ;
			return null ;
		}
		
		@Override
		public String RT_getOutTitle(int idx)
		{
			if(idx==0)
				return "realtime messsage";
			else if(idx==1)
				return "history messsage" ;
			return null ;
		}
		
		private transient HashSet<String> eqTopicSet = null ;
		
		private transient ArrayList<String> prefixTopics = null ;
		
		private transient boolean bAllTopics = false;

		private void parseMatchTopic()
		{
			HashSet<String> eq_set = null ;
			
			ArrayList<String> prefixs = null ;
			
			boolean b_all = false;
			
			if(Convert.isNotNullEmpty(this.matchTopics))
			{
				List<String> ss = Convert.splitStrWith(this.matchTopics, ",|") ;
				for(String s:ss)
				{
					s = s.trim() ;
					if(s.endsWith("*"))
					{
						if(s.equals("*"))
						{
							b_all = true ;
							break ;
						}
						
						if(prefixs==null)
							prefixs = new ArrayList<>() ;
						prefixs.add(s.substring(0,s.length()-1)) ;
					}
					else
					{
						if(eq_set==null)
							eq_set = new HashSet<>() ;
						eq_set.add(s) ;
					}
				}
			}
			
			this.bAllTopics = b_all ;
			this.eqTopicSet = eq_set ;
			this.prefixTopics = prefixs ;
		}
		
		private boolean checkMatchTopic(String topic)
		{
			if(Convert.isNullOrEmpty(topic))
				return false;
			if(this.bAllTopics)
				return true ;
			
			if(this.eqTopicSet!=null && this.eqTopicSet.contains(topic))
				return true ;
			if(this.prefixTopics!=null)
			{
				for(String p:this.prefixTopics)
				{
					if(topic.startsWith(p))
						return true ;
				}
			}
			return false;
		}
		/**
		 * call by PlatInsManager.onRecvedRTMsg
		 * @param keyid
		 * @param topic
		 * @param vp
		 */
		void RT_onRecvedMsg(String keyid,String topic,ValPack vp,boolean b_his)
		{
			if(!checkMatchTopic(topic))
				return ;
			//System.out.println(" topic="+topic);
			MNMsg msg = new MNMsg().asPayload(vp.getPayload()) ;
			RTOut rto = RTOut.createOutIdx().asIdxMsg(b_his?1:0, msg) ;
			RT_sendMsgOut(rto);
		}
}

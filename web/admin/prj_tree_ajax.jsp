<%@ page contentType="text/json;charset=UTF-8" isELIgnored="false"%><%@ page import="java.util.*,
				java.io.*,
				java.net.*,
				java.util.*,
				org.iottree.core.*,
				org.iottree.core.util.*,
				java.net.*"%><%!
				public static void renderTagGroup(Writer out,UATagG tg) throws Exception
				{
					out.write("{\"text\": \""+tg.getName()+"\"") ;
					out.write(",\"id\": \""+tg.getId()+"\",\"type\":\"tagg\" ,\"path\":\""+tg.getNodePath()+"\"") ;
					out.write(",\"icon\":\"icon_tagg\",\"state\": {\"opened\": false}") ;
					out.write(",\"in_dev\":"+tg.isInDev()) ;
					out.write(",\"children\": [") ;
					List<UATagG> tgs = tg.getSubTagGs() ;
					boolean bfirst = true ;
					if(tgs!=null)
					{
						
						for(UATagG subtg:tgs)
						{
							if(bfirst)
								bfirst = false;
							else
								out.write(',') ;
							renderTagGroup(out,subtg);
						}
					}
					renderHmis(bfirst,out,tg);
					out.write("]}") ;
				}
	
				public static void renderTagGroupInCxt(boolean bfirst,Writer out,UANodeOCTagsGCxt dev) throws Exception
				{
					//DevDef dd = dev.getDevDef();
					List<UATagG> tgs = dev.getSubTagGs();
					//boolean bfirst = true ;
					if(tgs!=null)
					{
						for(UATagG tg:tgs)
						{
							if(bfirst)
								bfirst = false;
							else
								out.write(',') ;
							renderTagGroup(out,tg);
						}
					}
					
					renderHmis(bfirst,out,dev) ;
				}
				
				public static void renderHmis(boolean bfirst,Writer out,UANodeOCTagsCxt tagcxt) throws Exception
				{
					List<UAHmi> hmis = tagcxt.getHmis() ;
					if(hmis==null||hmis.size()<=0)
						return ;
					
					for(UAHmi hmi:hmis)
					{
						
						if(bfirst) bfirst=false;
						else out.write(",") ;
						
						boolean ref = hmi.isRefedNode();
						out.write("{\"text\": \""+hmi.getName()+"\",\"title\":\""+hmi.getTitle()+"\",\"ref\":"+ref) ;
						out.write(",\"id\": \""+hmi.getId()+"\",\"type\":\"hmi\" ,\"path\":\""+hmi.getNodePath()+"\",\"main_ui\":"+hmi.isMainInPrj()) ;
						out.write(",\"tp\":\"hmi\",\"icon\":\"fa fa-puzzle-piece fa-lg\",\"state\": {\"opened\": true}}") ;
					}
				}
%><%if(!Convert.checkReqEmpty(request, out, "id"))
		return;
	String id = request.getParameter("id");
	UAPrj rep = UAManager.getInstance().getPrjById(id);
	if(rep==null)
	{
		out.print("no repository found!");
		return;
	}%>[
	
	{
	"text":"<%=rep.getName() %>&nbsp;&nbsp;<span id='share_run' ><i id='' class='fa fa fa-share-alt-square fa-lg'></i></span>"
	,"id":"<%=rep.getId() %>"
	,"type":"prj"
	,"path":"<%=rep.getNodePath()%>"
	,"icon": "fa fa-sitemap fa-lg"
	,"state": {"opened": true}
	,"children": [

		<%
	boolean bf1=true;
	for(UACh ch:rep.getChs())
	{
		if(bf1)
			bf1=false;
		else
			out.print(",") ;
		String drvfit = "";
		DevDriver drv = ch.getDriver();
		String drvt = "none";
		if(drv!=null)
			drvt = drv.getTitle() ;
		boolean hasdrv = ch.hasDriver();
		if(hasdrv)
		{
			if(!ch.isDriverFit())
				drvfit = "<span class=tn_warn title='"+drvt+" is not fit'>drv?</span>" ;
			else
				drvfit = "<span class=tn_ok title='"+drvt+"'>drv</span>" ;
		}
%>
		{
		  "text":"<img id='ch_<%=ch.getId()%>' src='/admin/inc/sm_icon_ch.png'/><%if(hasdrv){%><i id='ch_run_<%=ch.getId()%>' class='fa fa-cog fa-lg'></i><%}%>&nbsp;<span title='<%=ch.getTitle()%>'><%=ch.getName() %></span><%=drvfit%>"
		  ,"id":"<%=ch.getId() %>"
		  ,"type":"ch"
		  ,"path":"<%=ch.getNodePath()%>"
		  ,"state": {"opened": true}
		  ,"icon":"icon_ch"
		 ,"children": [
<%
	boolean bf2=true;
		for(UADev dev:ch.getDevs())
		{
			if(bf2)
				bf2=false;
			else
				out.print(",") ;
			String devok = "" ;
			DevDef devdef = dev.getDevDef() ;
			String deft = "" ;
			if(devdef==null)
			{
				devok = "<span class=tn_warn title='"+dev.getTitle()+" is not def'>dev?</span>" ;
			}
			else
			{
				deft = devdef.getTitle() ;
			}
%>
			{
				"text":"<span title='<%=dev.getTitle()%>'><%=dev.getName() %></span>[<%=deft%>] <%=devok%>"
			  ,"id":"<%=dev.getId() %>"
			  ,"type":"dev"
			   ,"path":"<%=dev.getNodePath()%>"
			  ,"state": {"opened": false}
			   ,"icon":"icon_dev"
			  ,"children": [
<%
renderTagGroupInCxt(true,out,dev) ;
%>
			   ]
			}
	<%
		}
		renderTagGroupInCxt(bf2,out, ch);
		//renderHmis(bf2,out,ch) ;
%>
		  ]
		}
<%
	}
	renderHmis(bf1,out,rep) ;
%>

	]}

]

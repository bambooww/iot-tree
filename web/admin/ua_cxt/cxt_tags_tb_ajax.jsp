<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,
	org.iottree.core.*,
	org.iottree.core.basic.*,
	org.iottree.core.util.*,
	org.iottree.core.store.*,
	org.iottree.core.store.record.*,
	org.iottree.core.comp.*
	"%><%@ taglib uri="wb_tag" prefix="wbt"%><%!
	private static class TagComp implements Comparator<UATag>
	{
		String sortBy = null ;
		
		public TagComp(String sortby)
		{
			this.sortBy = sortby ;
			if(this.sortBy==null)
				this.sortBy="" ;
		}
		
		public int compare(UATag t1,UATag t2)
		{
			switch(sortBy)
			{
			case "addr":
				String addr1 = t1.getAddress() ;
				String addr2 = t2.getAddress() ;
				if(addr1==null)
					addr1="" ;
				if(addr2==null)
					addr2="" ;
				return addr1.compareTo(addr2) ;
			case "title":
				String title1 = t1.getTitle() ;
				String title2 = t2.getTitle() ;
				return title1.compareTo(title2) ;
			case "valtp":
				String vtstr1 = t1.getValTp().getStr();//.getTitle() ;
				String vtstr2 = t2.getValTp().getStr();
				return vtstr1.compareTo(vtstr2) ;
			default:
				return t1.getName().compareTo(t2.getName());
			}
			
		}
	}
%><%
if(!Convert.checkReqEmpty(request, out, "path"))
	return ;
//boolean bdev = "true".equals(request.getParameter("bdev")) ;
//boolean bmgr ="true".equals(request.getParameter("mgr")) ;
String sortby = request.getParameter("sortby") ;
if(sortby==null)
	sortby="" ;
boolean bsys = "true".equals(request.getParameter("sys")) ;
boolean bsub = "true".equals(request.getParameter("sub")) ;
String path = request.getParameter("path") ;
UANode node = UAUtil.findNodeByPath(path) ;
if(node==null)
{
	out.print("node not found"); 
	return ;
}
UANode topn = node.getTopNode() ;
UAPrj prj = null ;
StoreManager storem = null;
RecManager recmgr = null ;
if(topn instanceof UAPrj)
{
	prj = (UAPrj)topn ;
	storem = StoreManager.getInstance(prj.getId()) ;
	recmgr = RecManager.getInstance(prj) ;
}

UAHmi hmi = null ;
boolean bhmi = false;
if(node instanceof UAHmi)
{
	bhmi = true ;
	hmi = (UAHmi)node ;
	node = node.getParentNode() ;
}

boolean b_tags_g = false;
if(!(node instanceof UANodeOCTags))
{
	out.print("node has no tags") ;
	return ;
}

b_tags_g = node instanceof UANodeOCTagsGCxt ;

boolean ref_locked = false;
if(b_tags_g)
{
	ref_locked = ((UANodeOCTagsGCxt)node).isRefLocked();
}

String node_cxtpath = node.getNodePath();
UANodeOCTags node_tags = (UANodeOCTags)node;
//UATagList taglist  = node_tags.getTagList() ;

boolean bdevdef = UAUtil.isDevDefPath(path) ;
List<UATag> cur_tags = node_tags.getNorTags() ;
List<UANodeOCTags>  tns = null;
if(bsub)
	tns = node_tags.listSelfAndSubTagsNode() ;
else
	tns = Arrays.asList(node_tags) ;
boolean brefowner = node_tags.isRefOwner();
boolean brefed = node_tags.isRefedNode() ;

TagComp tag_comp = new TagComp(sortby);
Collections.sort(cur_tags, tag_comp) ;
String hmitt = "" ;
if(bhmi)
{
	hmitt ="UI ["+hmi.getNodePath()+"]";
}


int tags_num = 0 ;
for(UANodeOCTags tn:tns)
{
	//if(tn.getRefBranchNode()!=null)
	//	continue ;
	List<UATag> tags = null;
	if(bsys)
		tags = tn.listTags() ;
	else
		tags = tn.getNorTags() ;
	
	Collections.sort(tags, tag_comp) ;
	String tn_id = tn.getId() ;
	String tn_path = tn.getNodePath() ;
	for(UATag tag:tags)
	{
		tags_num ++ ;
		String tagpath = tag.getNodePath();//.getNodePathCxt() ;
		String cxtpath=  tag.getNodeCxtPathIn(node_tags) ;
		String tagp = tag.getNodePathCxt();
		boolean bloc = tag.getParentNode()==node_tags;
		String cssstr="" ;
		if(tag.isSysTag())
			cssstr="color:grey";
		else if(bloc)
			cssstr="color:blue;cursor:hand";
	
		String tt = "" ;
		if(tag.getDesc()!=null)
			tt += tag.getDesc() ;
		if(tag.getNameSor()!=null)
			tt += "&#10;sor name:"+tag.getNameSor();
		if(tag.getTitleSor()!=null)
			tt += "&#10;sor title:"+tag.getTitleSor();
		if(tag.getDescSor()!=null)
			tt += "&#10;sor desc:"+tag.getDescSor();
		
		String addr = tag.getAddress() ;
		if(addr.length()>10)
			addr = addr.substring(0,10)+"..." ;
		
		String valtp_str = tag.getValTp().getStr();
		if(tag.getValTranserObj()!=null)
			valtp_str = tag.getValTpRaw().getStr()+"-"+valtp_str;
		
		String alert_str = "" ;
		List<ValAlert> alerts = tag.getValAlerts() ;
		if(alerts!=null&&alerts.size()>0)
			alert_str = "<i class='fa-solid fa-bell'></i>" ;
%>
   <tr id="ctag_<%=tag.getId() %>" tag_loc="<%=bloc %>"  tag_sys="<%=tag.isSysTag() %>" 
   	tag_path="<%=tn_path %>" tag_id="<%=tag.getId()%>" cxt_path="<%=cxtpath%>"
   	title="<%=tt%>" tag_num="<%=tags_num %>" class="tag_row"
<%
if(bloc&&!tag.isSysTag())
{
%>
   	 ondblclick="add_or_modify_tag('<%=tag.getId()%>')"
   	
<%
}
%>
   	 >
   <td >
   <%
	if(bloc)
	{
		//String ss = "✔";
		
%>
        <input type="checkbox" lay-skin="primary"  id="chk_<%=tag.getId()%>"/>
<%
	}
   
   String t = "" ;
   if(tag.isLocalTag())
	   t = "L" ;
   else if(tag.isMidExpress())
	   t = "M" ;
   boolean anti = tag.isValFilter() ;
%></td>
        <td style="text-align: center;"><%=t %></td>
<td title="<%=tag.getNodeCxtPathTitleIn(node_tags)%>"><span style="<%=cssstr%>" 
<%
if(bloc&&!tag.isSysTag())
{
%>
	onclick="add_or_modify_tag('<%=tag.getId()%>')"
<%
}
%>
><%=cxtpath%></span></td>
		<td><%=tag.getTitle() %></td>
        <td><%=addr%></td>
        <td><%=valtp_str %></td>
        <td><span id="ctag_alert_<%=cxtpath%>"><%=alert_str %></span></td>
        <td style="width:80px" id="ctag_v_<%=cxtpath%>" filter="<%=anti%>"></td>
        <td id="ctag_dt_<%=cxtpath%>"></td>
        <td id="ctag_chgdt_<%=cxtpath%>"></td>
        <td id="ctag_q_<%=cxtpath%>"></td>
        <td style="white-space: nowrap;">
<%
	if(tag.isCanWrite())
	{
%>
        	<input type="text" id="ctag_w_<%=tag.getId()%>" value="" size="8" style="color:#999999"/><a href="javascript:w_tag('<%=tag.getId()%>')"><i class="fa fa-pencil-square" aria-hidden="true"></i></a>
<%
	}
%>
        </td>
                <td style="white-space: nowrap;">
<%
if(bloc&&!tag.isSysTag())
{
%>
        <a href="javascript:del_tag('<%=tag.getId()%>')"><i class="fa-solid fa-times" aria-hidden="true"></i></a>&nbsp;
        <a href="javascript:add_or_modify_tag('<%=tag.getId()%>')"><i class="fa fa-pencil " aria-hidden="true"></i></a>
<%
}

String ext_str = tag.getExtAttrStr() ;
String ext_color = "" ;
String tagt = "set extended properties" ;
if(Convert.isNotNullEmpty(ext_str))
{
	ext_color="color:#17c680" ;
	tagt = ext_str.replaceAll("\\r", "&#10;").replaceAll("\\n", "&#13;").replaceAll("\"","&#34;") ;
}
%>&nbsp;<a href="javascript:bind_ext('<%=tagpath%>')" id="node_ext_<%=tag.getId() %>" title="<%=tagt %>" style="<%=ext_color%>"><i class="fa-solid fa-paperclip" aria-hidden="true"></i></a>
&nbsp;<a href="javascript:node_access('<%=tagpath%>')"  title="<wbt:g>access</wbt:g>"><i class="fa fa-paper-plane" aria-hidden="true"></i></a>
        </td>
<%
if(recmgr!=null)
{
%><td><%
	if(recmgr.checkTagCanRecord(tag))
	{
		String tmppath = tag.getNodeCxtPathInPrj() ;
		RecTagParam rtp = recmgr.getRecTagParam(tag) ;
		boolean bset =  rtp!=null;
		boolean ben = rtp!=null && rtp.isEnable();
		String color = "#d2d2d2" ;
		if(bset)
		{
			color = ben?"green":"#b46c24" ;
		}
		String dis_show = bset?"inline":"none" ;
		
%><button onclick="rec_tag_set(this,'<%=tmppath %>','<%=tag.getTitle() %> [<%=tmppath %>]')" title="<wbt:g>set,tag,recorder</wbt:g>" style="color:<%=color%>">&nbsp;<i class="fa fa-edit" /></i>&nbsp;</button>
  <span id="rec_tag_show_<%=tmppath %>" style="display:<%=dis_show%>;">
<a href="javascript:rec_tag_show('<%=tmppath %>','<%=tag.getTitle() %> [<%=tmppath %>]')" title="<wbt:g>show,recorded,history</wbt:g>" >&nbsp;<i class="fa fa-line-chart" ></i></a>

</span>
<%
	}
%></td><%
}


if(storem!=null)
{
	List<StoreOut> storeos = storem.findStoreOutsByTag(tag, true, true);
	boolean b_his = (storeos!=null && storeos.size()>0) ;
%><td>
<%
if(b_his)
{
	for(StoreOut so:storeos)
	{
		String outtp = so.getOutTp() ;
		String outtpt = so.getOutTpTitle() ;
		String outid = so.getId() ;
%><a href="javascript:show_data_his('<%=outtp %>','<%=outid %>','<%=tagp%>','<%=tt %>')" title="<wbt:g>show,history</wbt:g> - <%=outtpt%>">&nbsp;<i class="fa fa-line-chart" /></i>&nbsp;</a>
<%
	}
}
%>
        </td>
<%
}
%>
      </tr>
<%
	}
}
%>
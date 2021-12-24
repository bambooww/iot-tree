<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,
	org.iottree.core.*,
	org.iottree.core.basic.*,
	org.iottree.core.util.*,
	org.iottree.core.comp.*
	"%><%!
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
String path = request.getParameter("path") ;
UANode node = UAUtil.findNodeByPath(path) ;
if(node==null)
{
	out.print("node not found"); 
	return ;
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
List<UANodeOCTags>  tns = node_tags.listSelfAndSubTagsNode() ;
boolean brefowner = node_tags.isRefOwner();
boolean brefed = node_tags.isRefedNode() ;

TagComp tag_comp = new TagComp(sortby);
Collections.sort(cur_tags, tag_comp) ;
String hmitt = "" ;
if(bhmi)
{
	hmitt ="UI ["+hmi.getNodePath()+"]";
}%>

<%
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
		
		String cxtpath=  tag.getNodeCxtPathIn(node_tags) ;
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
%>
   <tr id="ctag_<%=tag.getId() %>" tag_loc="<%=bloc %>"  tag_sys="<%=tag.isSysTag() %>" 
   	tag_path="<%=tn_path %>" tag_id="<%=tag.getId()%>" cxt_path="<%=cxtpath%>"
   	title="<%=tt%>" tag_num="<%=tags_num %>"
<%
if(bloc&&!tag.isSysTag())
{
%>
   	 ondblclick="add_or_modify_tag('<%=tag.getId()%>')"
   	
<%
}
%>
   	 >
   <td style="text-align: center;">
   <%
	if(bloc)
	{
%>
        <input type="checkbox" lay-skin="primary"  id="chk_<%=tag.getId()%>"/>
<%
	}
%></td>
        <td style="text-align: center;"><%=(tag.isMidExpress()?"✔":"") %></td>
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
        <td style="width:100px" id="ctag_v_<%=cxtpath%>"></td>
        <td id="ctag_dt_<%=cxtpath%>"></td>
        <td id="ctag_chgdt_<%=cxtpath%>"></td>
        <td id="ctag_q_<%=cxtpath%>"></td>
        <td>
<%
	if(tag.isCanWrite())
	{
%>
        	<input type="text" id="ctag_w_<%=tag.getId()%>" value="" size="8"/><a href="javascript:w_tag('<%=tag.getId()%>')"><i class="fa fa-pencil-square" aria-hidden="true"></i></a>
<%
	}
%>
        </td>
                <td>
<%
if(bloc&&!tag.isSysTag())
{
%>
        <a href="javascript:del_tag('<%=tag.getId()%>')"><i class="fa fa-times" aria-hidden="true"></i></a>&nbsp;&nbsp;
        <a href="javascript:add_or_modify_tag('<%=tag.getId()%>')"><i class="fa fa-pencil " aria-hidden="true"></i></a>
<%
}
%>&nbsp;	
        </td>
      </tr>
<%
	}
}
%>
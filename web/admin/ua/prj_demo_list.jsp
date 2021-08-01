<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%><%@ page import="
	org.iottree.core.util.*,
	org.iottree.core.*,
	org.json.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*,
	org.iottree.core.util.xmldata.*
"%><%@ taglib uri="wb_tag" prefix="wbt"%><%!
static class FileItem
{
	String name = null ;
	
	String title = null ;
	
	String desc = null ;
	
	File file = null ;
	
	
}

private static FileItem transJO2FileItem(JSONObject jo)
{
	FileItem r = new FileItem() ;
	r.name = jo.getString("name") ;
	r.title = jo.optString("title") ;
	if(Convert.isNullOrEmpty(r.title))
		r.title = r.name ;
	r.desc = jo.optString("desc") ;
	if(r.desc==null)
		r.desc="" ;
	String fn = jo.optString("file") ;
	if(Convert.isNullOrEmpty(fn))
		fn = r.name+".zip" ;
	r.file = new File(Config.getDataDirBase()+"/demo/"+fn) ;
	if(!r.file.exists())
		return null ;
	return r ;
}



private static List<FileItem> loadFileItems() throws Exception
{
	File listf = new File(Config.getDataDirBase()+"/demo/list.json");
	if(!listf.exists())
		return null;

	ArrayList<FileItem> rets=  new ArrayList<>() ;
	String txt = Convert.readFileTxt(listf, "utf-8");
	JSONArray jsarr = new JSONArray(txt);
	int n = jsarr.length() ;
	for(int i = 0 ; i < n ; i ++)
	{
		JSONObject jo = jsarr.getJSONObject(i);
		FileItem fi = transJO2FileItem(jo);
		if(fi==null)
			continue ;
		rets.add(fi);
	}
	return rets;
}


static List<FileItem> fileitems = null ;

static List<FileItem> getOrLoadFileItems() throws Exception
{
	if(fileitems!=null)
		return fileitems ;
	fileitems = loadFileItems();
	return fileitems;
}
%><%

%><!DOCTYPE html>
<html class="">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width,minimum-scale=1,initial-scale=1">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,Chrome=1">
    <title>IOT-Tree</title>
    <link href="/favicon.ico" rel="shortcut icon" type="image/x-icon">
<jsp:include page="../head.jsp"/>

    </head>
<body aria-hidden="false">
	

						<div class="mod-body">
							<div class="content markitup-box" style="height:100%">
<%
	for(FileItem fi:getOrLoadFileItems())
{
%>
	<div class="aw-item" style="margin: 10px;border:1px solid">
       <input type="radio" name="rd_imp_item"  value="<%=fi.file.getName()%>"/>
      <%=fi.title %>
       <div class="inline-block pull-right text-left">
           
           
       </div>

       <div class="text-color-999">
           <span class="text-color-666">&nbsp;&nbsp;&nbsp;</span>
           <%=fi.desc %>
       </div>
   </div>
<%
}
%>
		</div>
	</div>
</body>
<script type="text/javascript">
function do_submit(cb)
{
	var v = $("input[name='rd_imp_item']:checked").val();
	if(v==null||v==undefined)
	{
		cb(false,"please select one demo to import");
		return ;
	}
	
	cb(true,v) ;
}
</script>
</html>

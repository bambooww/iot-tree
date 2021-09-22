<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="wbt"%>
<%
if(!Convert.checkReqEmpty(request, out, "tmpfn"))
	return;
	String tmpfn = request.getParameter("tmpfn");
	boolean bdemo = "true".equals(request.getParameter("demo")) ;
	File tmpf = null;
	if(bdemo)
	{
		tmpf = new File(Config.getDataDirBase()+"/demo/",tmpfn) ;
	}
	else
	{
		tmpf = new File(Config.getDataTmpDir(),tmpfn) ;
	}
	if(!tmpf.exists())
	{
		out.print("no upload file found") ;
		return ;
	}
	
	List<IdName> idnames = UAManager.getInstance().parsePrjZipFile(tmpf) ;
	if(idnames==null||idnames.size()<=0)
	{
		out.print("no project found") ;
		return ;
	}
	
	String idstr = "" ;
	String namestr="" ;
%>
<html>
<head>
<title>project importer</title>
<script src="/_js/jquery-1.12.0.min.js"></script>
<script type="text/javascript" src="/_js/ajax.js"></script>
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<style type="text/css">
.imp_item
{
 position:relative;
 width:90%;
 margin:10px;
 
 height:85px;
}
</style>
<script>
dlg.resize_to(600,400);
</script>
</head>
<body>
<%
for(IdName idn:idnames)
{
	String id = idn.getId() ;
	String name = idn.getName() ;
	idstr += ",\""+id+"\"" ;
	namestr +=",\""+name+"\"" ;
	UAPrj oldp = UAManager.getInstance().getPrjById(idn.getId()) ;
	UAPrj namep = UAManager.getInstance().getPrjByName(idn.getName()) ;
	String bgcolor = "#8dd35f" ;
	String prompt="New project will import" ;
	boolean b_can_imp=true;
	if(oldp!=null)
	{
		bgcolor = "#ff8080" ;
		prompt = "Project is already existed " ;
		if(oldp.RT_isRunning())
		{
			prompt+=",it's in running.";
			b_can_imp = false;
		}
	}
	else if(namep!=null)
	{
		prompt = "Project with this name already existed " ;
	}
%>
<div class="imp_item" style="background-color: <%=bgcolor%>">
  <table style="height:100%;width:100%">
    <tr>
      <td colspan="3" align="center"><h3><%=prompt %></h3></td>
    </tr>
    <tr>
      <td valign="middle" >

      </td>
      <td>Name:<input type="text" id="inputn_<%=id %>" value="<%=idn.getName() %>" /><br>
      Title:<input type="text" id="inputt_<%=id %>" value="<%=idn.getTitle() %>  " />
      </td>
      <td></td>
    </tr>
    <tr>
      <td colspan="3" align="center">
      <input type="radio" id="" name="radio_<%=id %>" value="ignore">Do not import
<%
	if(oldp!=null)
	{
%>
      <input type="radio" id="" name="radio_<%=id %>" value="replace" checked="checked">Replace
<%
	}
	
    if(namep!=null)
	{
%><input type="radio" id="" name="radio_<%=id %>" value="rename" checked="checked">Rename to import<%
	}
	else
	{
%><input type="radio" id="" name="radio_<%=id %>" value="new" checked="checked">Create New One<%
	}
%>
	
     
      </td>
    </tr>
    <tr>
  </table>
	 
</div>
<%
}

idstr=idstr.substring(1) ;
namestr=namestr.substring(1) ;
%>
</body>
<script type="text/javascript">
var tmpfn = "<%=tmpfn%>" ;
var demo = <%=bdemo%>
var ids=[<%=idstr%>];
var names = [<%=namestr%>] ;
function win_close()
{
	dlg.close(0);
}


function do_submit(cb)
{
	var pms = {tmpfn:tmpfn,demo:demo} ;
	var b = false;
	for(var i=0 ;i < ids.length ; i ++)
	{
		var id = ids[i] ;
		var name = names[i] ;
		var v = $("input[name='radio_"+id+"']:checked").val();
		if("ignore"==v)
			continue ;
		if("rename"==v)
		{
			var newname = $("#inputn_"+id).val() ;
			if(newname==name)
			{
				dlg.msg("name ["+newname+"] must be changed") ;
				return ;
			}
			var tt=$("#inputt_"+id).val();
			pms["id_"+id+"_title"]=tt;
			pms["id_"+id]="rename_"+newname ;
		}
		else
			pms["id_"+id]=v ;
		b=true ;
	}
	
	if(!b)
	{
		dlg.msg("please select to import!") ;
		return ;
	}
	
	console.log(pms);
	
	send_ajax('prj_imp_ajax.jsp',pms,function(bsucc,ret)
	{
		if(!bsucc || ret.indexOf('succ')<0)
		{
			cb(false,ret) ;
			return ;
		}
		cb(true,ret);
	},false);
	
	//document.getElementById('form1').submit() ;
}

</script>
</html>
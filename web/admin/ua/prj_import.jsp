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
<jsp:include page="../head.jsp"></jsp:include>
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
	String prompt="new_prj_will_imp";//New project will import" ;
	boolean b_can_imp=true;
	if(oldp!=null)
	{
		bgcolor = "#ff8080" ;
		prompt = "prj_existed";//Project is already existed" ;
		if(oldp.RT_isRunning())
		{
			prompt+=",it_in_run";
			b_can_imp = false;
		}
	}
	else if(namep!=null)
	{
		prompt = "prj_n_existed";//Project with this name already existed" ;
	}
%>
<div class="imp_item" style="background-color: <%=bgcolor%>;border:1px solid;min-height:160px;">
  <table style="height:100%;width:100%">
    <tr>
      <td colspan="3" align="center"><h4><wbt:g><%=prompt %></wbt:g></h4></td>
    </tr>
    <tr>
      <td valign="middle"  align="right"><wbt:g>name</wbt:g>:</td>
      <td><input type="text" id="inputn_<%=id %>" value="<%=idn.getName() %>" style="width:90%"/>
     
      </td>
      <td></td>
    </tr>
    <tr>
      <td valign="middle" align="right"><wbt:g>title</wbt:g>:</td>
      <td>
      <input type="text" id="inputt_<%=id %>" value="<%=idn.getTitle() %>  "  style="width:90%"/>
      </td>
      <td></td>
    </tr>
    <tr>
      <td colspan="3" align="center">
      <input type="radio" id="" name="radio_<%=id %>" value="ignore"><wbt:g>do_not_imp</wbt:g>
<%
	if(oldp!=null)
	{
%>
      <input type="radio" id="" name="radio_<%=id %>" value="replace" checked="checked"><wbt:g>replace</wbt:g>
<%
	}
	
    if(namep!=null)
	{
%><input type="radio" id="" name="radio_<%=id %>" value="rename" checked="checked"><wbt:g>rename_imp</wbt:g><%
	}
	else
	{
%><input type="radio" id="" name="radio_<%=id %>" value="new" checked="checked"><wbt:g>create_new</wbt:g><%
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
				dlg.msg("<wbt:g>name</wbt:g> ["+newname+"] <wbt:g>must_be_chg</wbt:g>") ;
				return ;
			}
			var tt=$("#inputt_"+id).val();
			pms["id_"+id+"_title"]=tt;
			pms["id_"+id]="rename_"+newname ;
		}
		else
		{
			var newname = $("#inputn_"+id).val() ;
			var tt=$("#inputt_"+id).val();
			pms["id_"+id+"_title"]=tt;
			pms["id_"+id]="rename_"+newname ;
		}
			
		b=true ;
	}
	
	if(!b)
	{
		dlg.msg("<wbt:g>pls_sel_imp</wbt:g>") ;
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
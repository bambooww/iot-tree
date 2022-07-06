<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
	org.iottree.core.comp.*,
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
	File tmpf = new File(Config.getDataTmpDir(),tmpfn) ;
	if(!tmpf.exists())
	{
		out.print("no upload file found") ;
		return ;
	}
	
	CompManager devmgr = CompManager.getInstance() ;
	HashMap<String,String> pms = devmgr.parseCompLibZipFileMeta(tmpf) ;
	if(pms==null)
	{
		out.print("no invalid HMI Comp file") ;
		return ;
	}
	
	String tp = pms.get("tp") ;
	String libid = pms.get("libid");
	String libtitle = pms.get("libtitle") ;
	
	if(!"complib".equals(tp) || Convert.isNullOrEmpty(libid))
	{
		out.print("invlid import file!") ;
		tmpf.delete();
		return ;
	}
//	DevDriver dd = devmgr.getDriver(drvname) ;
//	if(dd==null)
//	{
//		out.print("no Driver found") ;
//		return ;
//	}
	
	CompLib complib = devmgr.getCompLibById(libid);
	
%>
<html>
<head>
<title>HMI Comp Lib importer</title>
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
String bgcolor = "#8dd35f" ;
String prompt="New library will import" ;
boolean b_can_imp=true;
if(complib!=null)
{
	bgcolor = "#ff8080" ;
	prompt = "HMI Comp library is already existed " ;
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
      <td>
      Title:<input type="text" id="inputt" value="<%=libtitle %>" />
      </td>
      <td></td>
    </tr>
    <tr>
      <td colspan="3" align="center">
      <input type="radio" id="" name="radio_op" value="ignore">Do not import
<%
	if(complib!=null)
	{
%>
      <input type="radio" id="" name="radio_op" value="replace" >Replace
<%
	}

%><input type="radio" id="" name="radio_op" value="new" checked="checked">Create New One<%
	
%>
	
     
      </td>
    </tr>
    <tr>
  </table>
	 
</div>
</body>
<script type="text/javascript">
var tmpfn = "<%=tmpfn%>" ;
var libid="<%=libid%>";

function win_close()
{
	dlg.close(0);
}


function do_submit(cb)
{
	var pms = {tmpfn:tmpfn} ;

		var op = $("input[name='radio_op']:checked").val();
		pms["op"]=op;
		pms["tmpfn"] = tmpfn ;
		pms["libid"] = libid ;
		var tt = $("#inputt").val() ;
		if(tt==null||tt=="")
		{
			dlg.mdg("please input title") ;
			return ;
		}
		pms["libtitle"]=tt ;
	
	//console.log(pms);
	
	send_ajax('comp_lib_imp_ajax.jsp',pms,function(bsucc,ret)
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
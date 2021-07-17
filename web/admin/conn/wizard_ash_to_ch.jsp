<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
	org.iottree.core.conn.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*
	"%><%
	// tcpserver connprovider may accept multi connection from remote.
	//  and every remote conn point hardware will be same except it's id
	//  so,iottree will use this wizard to create connectors channels and devices automatically
	
	if(!Convert.checkReqEmpty(request, out, "prjid","cpid"))
	return;
String prjid = request.getParameter("prjid") ;
String cpid = request.getParameter("cpid") ;
ConnProTcpServer cp = (ConnProTcpServer)ConnManager.getInstance().getConnProviderById(prjid, cpid) ;
if(cp==null)
{
	out.print("no ConnProvider found") ;
	return ;
}


String name = cp.getName() ;
String title= cp.getTitle() ;
String chked = "" ;
if(cp.isEnable())
	chked = "checked='checked'" ;
String desc = cp.getDesc();
String cp_tp = cp.getProviderType() ;
String local_ip = cp.getLocalIP() ;
int local_port = cp.getLocalPort() ;
String ashn = cp.getAshName() ;
List<ConnProTcpServer.AcceptedSockHandler> ashs = ConnProTcpServer.getAcceptedSockHandlers() ;
%>
<html>
<head>
<title>tcp client cp editor</title>
<jsp:include page="../head.jsp"></jsp:include>
<script>
dlg.resize_to(700,400);
</script>
</head>
<style>
.dev_item
{
top:0px;height:30px;border:1px solid;border-color: #009688
}

</style>
<body>
<form class="layui-form" action="">
  
	  <div class="layui-form-item">
    <label class="layui-form-label">Drvier</label>
    <div class="layui-input-inline">
      <input type="text" name="drv_title" id="drv_title" value="" onclick="select_drv()" class="layui-input"/>
      <input type="hidden" name="drv" id="drv" value="" />
      
    </div>
    <div class="layui-form-mid layui-word-aux">Each channel will use this device driver</div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label">Devices</label>
    <div class="layui-input-inline">
    </div>
    
    <div class="layui-form-mid"><button id="btn_add_devdef" title="add device" type="button" class="layui-btn layui-btn-sm" onclick="sel_devdef()"><i class="fa fa-plus"></i></button></div>
  </div>
   <div class="layui-form-item">
    <label class="layui-form-label">Conn/Ch:</label>
    <div class="layui-input-block">
      <table id="view_colorval"  class="layui-table" lay-filter="dl_list"  lay-size="sm" lay-even="true" style="width:95%" border="1">
		   <thead style="background-color: #cccccc">
		     <tr>
			  <td>ConnId</td>
			  <td>Name</td>
			  <td>Title</td>
			  <td></td>
			</tr>
		  </thead>
		  <tbody id="client_list">
			
		  </tbody>
		  <tfoot style="height: 50px">
		    <td style="width:15%"><input id="input_id" type="hidden" value=""/><input id="input_connid" type="text" size="4" /></td>
			 <td style="width:30%"><input id="input_name" type="text" size="13" /></td>
			  <td style="width:40%"><input id="input_title" type="text" size="20" /></td>
			  <td><a href="javascript:set_item()"><i class="fa fa-plus"></i></a></td>
			</tr>
		  </tfoot>
		</table>
	  </div>
	 </div>
	 
	</form>
</body>
<script type="text/javascript">

var _tmpid = 0 ;

function get_new_id()
{
	_tmpid++ ;
	var d = new Date() ;
	var tmps = 'id_' ;
	tmps += d.getFullYear() ;
	var i = d.getMonth() ;
	if(i<10)
		tmps += '0'+i ;
	else
		tmps += i ;
	i = d.getDay() ;
	if(i<10)
		tmps += '0'+i ;
	else
		tmps += i ;
	i=d.getHours();
	if(i<10)
		tmps += '0'+i ;
	else
		tmps += i ;
	i=d.getMinutes();
	if(i<10)
		tmps += '0'+i ;
	else
		tmps += i ;
	i=d.getSeconds() ;
	if(i<10)
		tmps += '0'+i ;
	else
		tmps += i ;
	tmps += "_"+_tmpid ;
	return tmps ;
	//return "id_"+scada_tmpid ;
}

function sel_item(id)
{
	var tds = $("#"+id).children('td') ;
	
	var connid = tds.eq(0).html() ;
	var name = tds.eq(1).html() ;
	var title = tds.eq(2).html() ;
	$('#input_id').val(id);
	$('#input_name').val(name);
	$('#input_connid').val(connid);
	$('#input_title').val(title);
	 form.render(); 
}

function set_item()
{
	var id = $('#input_id').val();
	var name = $('#input_name').val();
	var connid = $('#input_connid').val();
	var title = $('#input_title').val();
	if(connid==null||connid=="")
	{
		dlg.msg("no ConnId input") ;
		return ;
	}
	let failedr={} ;
	if(!chk_var_name(name,true,failedr))
	{
		dlg.msg(failedr.txt) ;
		return ;
	}
	if(title==null||title=="")
		title = name ;
	set_item_c(id,connid,name,title);
	$('#input_id').val("");
	$('#input_name').val("");
	$('#input_connid').val("");
	$('#input_title').val("");
}


function set_item_c(id,connid,name,title)
{
	if(id==null||id=='')
		id = get_new_id() ;
	if($("#"+id).length<=0)
	{
		var str = "<tr id='"+id+"' onclick=\"sel_item('"+id+"')\">"+
		  "<td>"+connid+"</td>"+
		  "<td>"+name+"</td>"+
		  "<td>"+title+"</td>"+
		  "<td><a href=\"javascript:del_item('"+id+"')\"><i class='fa fa-times' ></i></a></td></tr>";
		$('#client_list').append(str) ;
	}
	else
	{
		var str = "<td>"+connid+"</td>"+
		  "<td>"+name+"</td>"+
		  "<td>"+title+"</td>"+
		  "<td><a href=\"javascript:del_item('"+id+"')\">X</a></td>" ;
		$("#"+id).html(str) ;
	}
}

function del_item(id)
{
	//$('#view_colorval').remove($("#"+id));
	$("#"+id).remove();
}


function select_drv()
{
	dlg.open_win("../ua/drv_selector.jsp?edit=true",
			{title:"<wbt:lang>sel_drv_title</wbt:lang>",w:'400',h:'535'},
			[{title:'<wbt:lang>ok</wbt:lang>',style:""},{title:'<wbt:lang>cancel</wbt:lang>',style:"primary"}],
			[
				function(dlgw)
				{
					dlgw.do_submit(function(res,ret){
						if(res)
						{
							$("#drv_title").val(ret.title) ;
							$("#drv").val(ret.name) ;
							
							dlg.close();
						}
						else
						{
							dlg.msg(ret) ;
						}
					}) ;
					
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

var selectedDevs=[] ;

function sel_devdef()
{
	
	let drv_name = $("#drv").val() ;
	if(drv_name==null||drv_name=="")
	{
		dlg.msg("please select device driver first");
		return ;
	}
	dlg.open_win("./wizard_sel_devdef.jsp?hide_drv=true&drv="+drv_name,
			{title:"Select Device in Library",w:'1000',h:'560'},
			['<wbt:lang>ok</wbt:lang>','<wbt:lang>cancel</wbt:lang>'],
			[
				function(dlgw)
				{
					var sel = dlgw.get_selected() ;
					if(sel==null)
					{
						//dlg.msg("please select device") ;
						return ;
					}//sel.cat_title+"-"+
					let tt = sel.title+"["+sel.name+"]" ;
					let tmpid = sel.id ;
					
					selectedDevs.push(sel);
					let tmps = "<div class='layui-form-mid dev_item' id='dev_id_"+tmpid+"' dev_id='"+tmpid+"'><span>"+tt+"</span><a href='javascript:del_dev_item(\""+tmpid+"\")'><i class='fa fa-times' ></i></a></div>";
					let tmpp = $(tmps) ;
					tmpp.insertBefore($("#btn_add_devdef"));
					
					dlg.close();
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
	return false;
}

function del_dev_item(id)
{
	for(let sdev of selectedDevs)
	{
		if(sdev.id==id)
		{
			selectedDevs.remove(sdev) ;
			break ;
		}
	}
	
	$("#dev_id_"+id).remove();
}

function do_submit(cb)
{
	let drv_name = $("#drv").val() ;
	if(drv_name==null||drv_name=="")
	{
		cb(false,"please select device driver first");
		return ;
	}
	
	//let devids =[] ;
	//$(".dev_item").each(function(){
	//	let did = $(this).attr("dev_id") ;
	//	devids.push(did) ;
	//});
	if(selectedDevs.length<=0)
	{
		cb(false,"please select devices");
		return null;
	}
	
	let conn_nts = [] ;
	let ccs = $('#client_list').children('tr') ;
	ccs.each(function(){
		var tds = $(this).children('td') ;
		
		var connid = tds.eq(0).html() ;
		var name = tds.eq(1).html() ;
		var title = tds.eq(2).html() ;
		conn_nts.push({connid:connid,name:name,title:title}) ;
	});
	cb(true,{drv:drv_name,devs:selectedDevs,conn_nts:conn_nts}) ;
}

</script>
</html>
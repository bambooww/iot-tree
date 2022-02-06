<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
	org.iottree.core.task.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
				org.iottree.core.sim.*,
				org.iottree.driver.common.modbus.sim.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="wbt"%>
<%
	if(!Convert.checkReqEmpty(request, out,"insid","chid"))
	return ;

	String insid=request.getParameter("insid");
	SimInstance ins = SimManager.getInstance().getInstance(insid) ;
	if(ins==null)
	{
		out.print("no instance found") ;
		return ;
	}
	
	String chid=request.getParameter("chid");
	String devid=request.getParameter("devid");
	
	SimChannel sch = ins.getChannel(chid);
	if(sch==null)
	{
		out.print("no channel found") ;
		return ;
	}
	SlaveDev dev = (SlaveDev)sch.getDev(devid) ;
		if(dev==null)
		{
	out.print("no device found") ;
	return ;
		}
	int addr = dev.getDevAddr() ;
	
	List<SlaveDevSeg> segs = dev.getSegs() ;
	
String name = "" ;
String title = "" ;
String desc = "" ;
String init_sc = "" ;
String run_sc = "" ;
String end_sc = "" ;

boolean benable = true ;

if(dev!=null)
{
		name = dev.getName() ;
		title = dev.getTitle() ;
		benable = dev.isEnable() ;
}

if(name==null)
	name = "" ;
if(title==null)
	title = "" ;
if(desc==null)
	desc = "" ;

String chked_en = "" ;

if(benable)
	chked_en = "checked=checked";
%><html>
<head>
  <meta charset="utf-8">
  <title></title>
<jsp:include page="../head.jsp"></jsp:include>
    <style>
        .layui-form-label{
            width: 120px;
        }
        .layui-input-block {
            margin-left: 140px;
            min-height: 36px;
            width:240px;
        }
        .layui-table-view
        {
        	margin-top: 0px;
        }
          .layui-table-cell {
            height: auto;
            line-height: 18px;
        }
    </style>
</head>
<body>
 <legend>Setup</legend>
<div class="layui-form-item" style="align-content: ">
 <div class="layui-inline" style="left:0px">
  <label class="layui-form-mid">Device Id</label>
  <div class="layui-input-inline" style="width: 150px;">
    <input type="text" id="addr"  name="addr" class="layui-input" value="<%=addr %>" >
  </div>
  <div class="layui-form-mid"></div>
  <div class="layui-input-inline" style="width: 150px;">
    
  </div>
</div>
</div>
<table id="view_colorval"  class="layui-table" lay-filter="dl_list"  lay-size="sm" lay-even="true" style="width:95%" border="1">
   <thead style="background-color: #cccccc">
     <tr>
	  <td>FC</td>
	  <td>Address</td>
	  <td>Length</td>
	  
	  <td></td>
	</tr>
  </thead>
  <tbody id="color_list">
	
  </tbody>
  <tfoot>
    <td style="width:15%">
		<select>
<%
for(Map.Entry<Integer,String> f2t:SlaveDevSeg.listFCs().entrySet())
{
	int k = f2t.getKey() ;
	String t = k+":"+f2t.getValue() ;
%><option value="<%=k%>"><%=t %></option><%
}
%>
		</select>
    </td>
	 <td style="width:15%"><input id="input_ev" type="text" size="2" /></td>
	 <td style="width:30%"><input id="input_t" type="text" size="10" /></td>
	  
	  <td><a href="javascript:add_item()"><i class="fa fa-plus fa-lg " aria-hidden="true"></i></a></td>
	</tr>
  </tfoot>
</table>
<div class="layui-form-item">
  		<div class="layui-input-block">
  			<button class="layui-btn layui-btn-normal" type="button" lay-filter="set_param" onclick="set_view()">Set</button>
  		</div>
  	</div>
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


function add_item_c(sv,ev,color,t)
{
	var nid = get_new_id() ;
	var str = "<tr id='"+nid+"'>"+
	  "<td>"+sv+"</td>"+
	  "<td>"+ev+"</td>"+
	  "<td>"+t+"</td>"+
	  "<td>"+color+"</td>"+
	  "<td><span style='width:30px;height:30px;background-color: "+color+";cursor: crosshair'>&nbsp;&nbsp;&nbsp;&nbsp;</span></td>"+
	  "<td><a href=\"javascript:del_item('"+nid+"')\">X</a></td></tr>";
	$('#color_list').append(str) ;
}

<%

	for(SlaveDevSeg seg:segs)
	{
		
%>
add_item_c('<%=""%>','<%=""%>','<%=""%>','<%=""%>');
<%
	}

%>

function add_item()
{
	var sv = $('#input_sv').val();
	var ev = $('#input_ev').val();
	var t = $('#input_t').val();
	add_item_c(sv,ev,$('#input_c').val(),t);
	$('#input_sv').val(ev);
	$('#input_ev').val("");
	$('#input_t').val("");
}

function del_item(id)
{
	//$('#view_colorval').remove($("#"+id));
	$("#"+id).remove();
}

function set_view()
{
	var pm = "viewid="+$('#view_id').val() ;
	pm += "&viewtitle="+utf8UrlEncode($('#view_title').val()) ;
	
	var ct="" ;
	var bfirst=true ;
	$('#color_list').children('tr').each(function(){
		if(bfirst)bfirst=false;
		else ct+='|' ;
		var tds = $(this).children('td') ;
		var sv = tds.eq(0).html() ;
		var ev = tds.eq(1).html() ;
		var t = tds.eq(2).html() ;
		var color=tds.eq(3).html() ;
		ct += sv+":"+ev+":"+color+":"+t ;
	});
	pm += "&cl="+utf8UrlEncode(ct) ;
	send_ajax("dlv_edit_ajax.jsp?op=set",pm,function(bsucc,ret){
		dlg.msg(ret);
	}) ;
}
</script>
</html>
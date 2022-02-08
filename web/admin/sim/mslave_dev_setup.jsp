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
<b>Segments</b>
<table id="view_colorval"  class="layui-table" lay-filter="dl_list"  lay-size="sm" lay-even="true" style="width:95%" border="1">
   <thead style="background-color: #cccccc">
     <tr>
	  <td style="width:45%">FC</td>
	  <td  style="width:25%">Address</td>
	  <td style="width:30%">Length</td>
	  
	  <td style="width:5%"></td>
	</tr>
  </thead>
  <tbody id="item_list">
	
  </tbody>
  <tfoot>
  	<tr>
    <td >
		<select id="sel_fc">
<%
for(Map.Entry<Integer,String> f2t:SlaveDevSeg.listFCs().entrySet())
{
	int k = f2t.getKey() ;
	String t = k+":"+f2t.getValue() ;
%><option id="fc_<%=k %>" fc_t="<%=t %>" value="<%=k%>"><%=t %></option><%
}
%>
		</select>
    </td>
	 <td><input id="input_regidx" type="number" size="2"  style="width:50px" onchange="fit_num('input_regidx',0)"/></td>
	 <td ><input id="input_regnum" type="number" size="10"  style="width:50px" onchange="fit_num('input_regnum',1)"/></td>
	  
	  <td style="width:5%"><a href="javascript:add_item()"><i class="fa fa-plus fa-lg" aria-hidden="true"></i></a></td>
	</tr>
  </tfoot>
</table>
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


function add_item_c(nid,fc,regidx,regnum)
{
	if(!nid)
		nid = get_new_id() ;
	var fc_t = $("#fc_"+fc).attr("fc_t") ;
	var str = "<tr id='"+nid+"'>"+
	  "<td id='fc_"+nid+"' vv='"+fc+"'>"+fc_t+"</td>"+
	  "<td id='regidx_"+nid+"' vv='"+regidx+"'>"+regidx+"</td>"+
	  "<td id='regnum_"+nid+"' vv='"+regnum+"'>"+regnum+"</td>"+
	  "<td><a href=\"javascript:del_item('"+nid+"')\">X</a></td></tr>";
	$('#item_list').append(str) ;
}
<%

	for(SlaveDevSeg seg:segs)
	{
		String id = seg.getId() ;
		int fc = seg.getFC() ;
		int regidx = seg.getRegIdx() ;
		int regnum = seg.getRegNum() ;
%>
add_item_c('<%=id%>',<%=fc%>,<%=regidx%>,<%=regnum%>);
<%
	}

%>

function fit_num(id,minv)
{
	var ob = $("#"+id);
	var v = ob.val() ;
	var v = parseInt(v);
	if(v==NaN||v<minv)
	{
		ob.val(minv) ;
		return ;
	}
	if(v<=0)
		ob.val(minv) ;
}

function add_item()
{
	var fc = $('#sel_fc').val();
	//var fc_t = $("#fc_"+fc).attr("fc_t") ;
	var regidx = $('#input_regidx').val();
	var regnum = $('#input_regnum').val();
	var regidx = parseInt(regidx);
	if(isNaN(regidx)||regidx<0)
	{
		dlg.msg("Address must be integer >= 0") ;
		return ;
	}
	var regnum = parseInt(regnum);
	if(isNaN(regnum)||regnum<=0)
	{
		dlg.msg("Length must be integer > 0") ;
		return ;
	}
	add_item_c(null,fc,regidx,regnum);

	$('#input_regidx').val(""+(regidx+1));
	$('#input_regnum').val("");
}

function del_item(id)
{
	//$('#view_colorval').remove($("#"+id));
	$("#"+id).remove();
}

function get_segs()
{
	var ret = [] ;
	$('#item_list tr').each(function(i){
		var tmpid = $(this).attr("id") ;
		var tds = $(this).children('td') ;
		var seg={} ;
		seg.id = tmpid ;
		seg.fc = parseInt(tds.eq(0).attr("vv")) ;
		seg.reg_idx = parseInt(tds.eq(1).attr("vv")) ;
		seg.reg_num = parseInt(tds.eq(2).attr("vv")) ;
		ret.push(seg) ;
	});
	return ret ;
}

function do_submit(cb)
{
	var devid = document.getElementById('addr').value;
	var devid = parseInt(devid);
	if(isNaN(devid)||devid<=0||devid>254)
	{
		cb(false,'Please input Modbus Device Id (1-254)') ;
		return ;
	}
	var segs = get_segs() ;
	if(segs.length<=0)
	{
		cb(false,'Please input segments') ;
		return ;
	}
	var r = {dev_addr:devid,segs:get_segs()};
	console.log(r) ;
	cb(true,{jstr:JSON.stringify(r)})
	
	//document.getElementById('form1').submit() ;
}
</script>
</html>
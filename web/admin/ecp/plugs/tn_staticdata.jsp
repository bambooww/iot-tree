<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
	org.iottree.core.util.*,
	java.io.*,org.json.*,
	java.util.*,
	java.net.*,org.iottree.core.devtree.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="wbt"%>
<%
if(!Convert.checkReqEmpty(request, out, "treeid"))
	return ;

String treeid = request.getParameter("treeid") ;
String tree_nid = request.getParameter("tree_nid") ;
DTTree tree = DTTreeManager.getInstance().getTreeById(treeid);
if(tree==null)
{
	out.println("no device tree found") ;
	return ;
}
String title = "" ;
DTNode node = null;
DTStaticData sd = null;
JSONObject static_data_jo = new JSONObject() ;
if(Convert.isNotNullEmpty(tree_nid))
{
	node = tree.findNodeById(tree_nid);
	if(node==null)
	{
		out.print("no tree node found") ;
		return ;
	}
	title = node.getTitle() ;
	sd = node.getStaticData() ;
	if(sd!=null)
		static_data_jo = sd.toJO() ;
}

if(node==null)
{
	response.sendRedirect("tn_null.jsp") ;
	return;
}
%>
<html>
<head>
<title></title>
<jsp:include page="../../head.jsp">
	<jsp:param value="true" name="simple"/>
</jsp:include>
<style>
body {background-color: #fff;}
table {table-layout: fixed;width:100%;border:1px solid #ccc;}
.layui-table-view .layui-table td, .layui-table-view .layui-table th {
    padding: 1px 0;
}
.seled {color:green;font-weight: bold;}
</style>
</head>
<body style="overflow:hidden;">
<div class="toolbar" style="width:100%;height:30px;line-height:30px;border:1px solid #ccc;text-align: center;">
	<wbt:g>node</wbt:g>:[<%=title %>] <button id="btn_set_to_node" style="width:30px;height:99%;font-size:20px;color:#ccc;float:right;margin-right:5px;" title="set data to node" onclick="set_to_node()"><i class="fa-solid fa-angles-right"></i></button>
	
</div>
<fieldset class="layui-elem-field">
  <legend><wbt:g>static,props</wbt:g>-<button id="" class="layui-btn layui-btn-xs layui-btn-primary" onclick="add_prop()" title="&nbsp;新增计算模块"><i class="fa fa-plus"></i></button>
<button id="" class="layui-btn layui-btn-xs layui-btn-primary" onclick="location.reload()" title="&nbsp;刷新"><i class="fa fa-refresh"></i></button>
  &nbsp;&nbsp;&nbsp;<span title="Debug" style="color:green;cursor:pointer" onclick="debug_run_blks()"><i class="fa fa-bug"></i></span>
	</legend>
  <div class="layui-field-box" id="" style="overflow: hidden;">
<table id="props_list"  lay-filter="props_list"  lay-size="sm" lay-even="true" style="top:1px;width:99%;">
</table>
</div>
</fieldset>

<fieldset class="layui-elem-field">
  <legend><wbt:g>links</wbt:g>-</legend>
  <div class="layui-field-box" style="overflow: hidden;">
  
	</div>
 </fieldset>

</body>
<script type="text/html" id="p_toolbar">
<div class="layui-btn-group">
  <button type="button" class="layui-btn layui-btn-xs layui-btn-primary" lay-event="del"><i class="fa-solid fa-times"></i></button>
  </div>
</script>
<script type="text/javascript">
var treeid= "<%=treeid%>";
var tree_nid = "<%=tree_nid%>" ;
var static_data_jo = <%=static_data_jo%>;
var form;
var table;
layui.use(['form','table'], function(){
	  form = layui.form;
	  form.render();
	  
	  table = layui.table;
	  
	  render_tb();
	  update_ui();
});

function get_vt_sel_html(vt,idx)
{
	let ss = `<select id="input_vt_\${idx}" lay-ignore class="pe" >` ;
	let seled="str" ;
<%
for(UAVal.ValTP vt:UAVal.ValTP.values())
{
	String s = vt.getStr() ;
	%>
	seled = vt=='<%=s%>'?"selected":"";
	ss += `<option value="<%=s%>" \${seled} ><%=s%></option>`
	<%;
}
%>
	ss += `</select>`;
	return ss ;
}

function render_tb()
{
	  let cols = [];
	 cols.push({field:'n',title: '<wbt:g>name</wbt:g>', width:'30%',templet: function(d) {
	      let str = d.n;
	      return `<input class="pe" style="width:100%" value="\${str}" id="input_n_\${d._idx}"/>`;
	    }});
	 cols.push({field: 'v', title: '<wbt:g>val</wbt:g>', width:'40%',templet: function(d) {
	      let str = d.v;
	      return `<input class="pe" style="width:100%" value="\${str}" id="input_v_\${d._idx}"/>`;
	    }});
	 cols.push({field: 'vt', title: 'Type', width:'20%',templet: function(d) {
	 	  let vt = d.vt||'str' ;
		  return get_vt_sel_html(vt,d._idx);
	    }});
	 cols.push({field: 'Oper', title: '<wbt:g>oper</wbt:g>', width:"10%" ,toolbar: '#p_toolbar'}) ;
	 
	table.render({
	    elem: '#props_list'
    	    ,height: "200px",className: 'ellipsis-left'
    	    ,page: false,even: true,limit:100000
    	    ,cols: [cols]
    		  ,data:[]
    		  ,text: {
    		      none: 'No Data'
    		  }
    	  });
	  
	  table.on('tool(props_list)', function(obj){ // lay-filter="mc_acc_list"
		  var data = obj.data; //cur d
		  var lay_evt = obj.event; // lay-event
		  var tr = obj.tr; //tr DOM

		  if(lay_evt === 'del')
		  {
			  del_prop(data);
		  }
		  else if(lay_evt === 'edit')
		  {
			  edit_runblk(data) ;
		  }
		  else if(lay_evt==='edit_ok')
		  {
			  set_prop_row_ok(data)
		  }
		  else if(lay_evt === 'up')
		  {
			  updown_runblk(data,true) ;
		  }
		  else if(lay_evt === 'down')
		  {
			  updown_runblk(data,false) ;
		  }
		});
	  table.on('row(props_list)', function(obj)
			  {
		  var trs = $(".layui-table-body.layui-table-main tr");
		  trs.each(function(){
			  $(this).removeClass("seled") ;
		  })
		  obj.tr.addClass("seled");
				  var data = obj.data; //cur d
				  	on_sel_tag(data)
			  });
	  
	  //refresh_table(true);
}

function on_sel_tag(d){}

function tb_reload(data)
{
	table.reload("props_list",{data:data});
	
	$('.pe').on('input change', function() {
		set_dirty(true)
	});
}

function update_ui()
{
	if(!static_data_jo.props)
		static_data_jo.props=[]
	for(let i = 0 ; i < static_data_jo.props.length; i ++)
	{
		static_data_jo.props[i]._idx=i;
	}
	tb_reload(static_data_jo.props);
}

function get_prop_idx(d)
{
	if(!static_data_jo.props)
		return -1 ;
	return static_data_jo.props.indexOf(d) ;
}

function get_prop_by_name(n)
{
	if(!static_data_jo.props)
		return null ;
	for(let p of static_data_jo.props)
	{
		if(p.n==n)
			return p ;
	}
	return null ;
}

function add_prop()
{
	static_data_jo.props=acq_props_data();
	static_data_jo.props.push({_ed:true,n:"",v:"",vt:"str"})
	update_ui();
	set_dirty(true)
}

function del_prop(d)
{
	let idx = d._idx;
	if(idx<0)
		return false;
	static_data_jo.props=acq_props_data();
	static_data_jo.props.splice(idx,1) ;
	update_ui();
	set_dirty(true)
}

function set_dirty(b)
{
	if(b)
	{
		b_dirty = true;
		$("#btn_set_to_node").css("background-color","yellow") ;
	}
	else
	{
		b_dirty = false;
		$("#btn_set_to_node").css("background-color","") ;
	}
	
}

function acq_row(idx)
{
	let n = $("#input_n_"+idx).val();
	let v = $("#input_v_"+idx).val();
	let vt = $("#input_vt_"+idx).val();
	return {_idx:idx,n:n,v:v,vt:vt}
}

function acq_props_data()
{
	let ret=[];
	if(!static_data_jo.props)
		static_data_jo.props=[]
	for(let p of static_data_jo.props)
	{
		let idx = p._idx ;
		let row = acq_row(idx);
		ret.push(row);
	}
	return ret;
}

function chk_props_data()
{
	
	return true;
}

function set_to_node()
{
	if(!b_dirty)
		return;
	let ps = acq_props_data()
	static_data_jo.props = ps;
	let chkres = chk_props_data() ;
	if(chkres!==true)
	{
		dlg.msg(chkres);return;
	}
	let dd = {props:ps}
	dlg.loading(true);
	send_ajax("../dt_tree_ajax.jsp",{op:'set_static_data',treeid:treeid,tree_nid:tree_nid,jstr:JSON.stringify(dd)},(bsucc,ret)=>{
		dlg.loading(false);
		if(!bsucc || ret!='succ')
		{
			dlg.msg(ret);return;
		}
		
		parent.reload_tree();
		set_dirty(false)
	})
	
}

function on_tag_set_to_node(tg)
{
	//console.log("on tag set",tg);
	let oldtg = get_tg_by_path(tg.tagp);
	if(oldtg)
	{
		dlg.msg("Tag "+tg.tagp+" is already existed") ;return;
	}
	tags_list.push(tg) ;
	update_ui();
	$("#btn_set_to_node").css("background-color","yellow") ;
	b_dirty = true;
}

function on_single_node_seled(node)
{
	if(!node)
	{
		location.href="dt_tree_tn_runtags.jsp?treeid="+treeid;
		return;
	}
	let tags_if = $("#tags_if")[0].contentWindow;
	let prjn = "";
	let search_txt="" ;
	if(tags_if)
	{
		prjn = tags_if.prjn||"";
		search_txt = tags_if.search_txt||"";
	}
	location.href="tn_runtags.jsp?treeid="+treeid+"&tree_nid="+node.id+"&prjn="+prjn+"&search_txt="+search_txt;
}
	
function win_close()
{
	dlg.close(0);
}

function get_input_val(id,defv,bnum)
{
	var n = $('#'+id).val();
	if(n==null||n=='')
	{
		return defv ;
	}
	if(bnum)
		return parseInt(n);
	return n;
}

function get_tags_list()
{
	return tags_list;
}



function resize_iframe_h()
{
	   var h = $(window).height() -330;
	   $("#if_blk_detail").css("height",h+"px");
}

var resize_cc = 0 ;
$(window).resize(function(){
	resize_iframe_h();
	
	});
resize_iframe_h()
</script>
</html>                                                                                                                                                                                                                            
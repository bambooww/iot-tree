<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,org.json.*,
	org.iottree.core.*,
	org.iottree.core.basic.*,
	org.iottree.core.task.*,
	org.iottree.core.util.*,
	org.iottree.core.dict.*,
	org.iottree.core.comp.*,org.iottree.core.util.jt.*,
	org.iottree.core.msgnet.*
	"%><%@ taglib uri="wb_tag" prefix="w"%><%
	if(!Convert.checkReqEmpty(request, out, "container_id","netid"))
			return ;
	String container_id = request.getParameter("container_id");
	String netid = request.getParameter("netid") ;
	String itemid = request.getParameter("itemid") ;
	
	MNManager mnm= MNManager.getInstanceByContainerId(container_id) ;
	if(mnm==null)
	{
		out.print("no MsgNet Manager with container_id="+container_id) ;
		return ;
	}

	MNNet net = mnm.getNetById(netid) ;
	if(net==null)
	{
		out.print("no net found") ;
		return ;
	}
	MNBase item =net.getItemById(itemid) ;
	if(item==null)
	{
		out.print("no item found") ;
		return ;
	}
	
	
	String tp = item.getTPFull() ;
	String title = item.getTitle() ;
	
	String mark_str = "" ;
	List<String> marks = item.getMarks();
	if(marks!=null && marks.size()>0)
		mark_str = Convert.combineStrWith(marks, ',') ;

	String res_name = item.getMNResName() ;
	if(res_name==null)
		res_name="" ;
	String pm_url = item.getCat().getParamUrl(item);// "./nodes/"+tp+"_pm.jsp" ;
	if(pm_url==null)
		pm_url="" ;
	String pm_url_base = "" ;
	
	if(Convert.isNotNullEmpty(pm_url))
	{
		int k = pm_url.lastIndexOf('/') ;
		if(k>0)
			pm_url_base = pm_url.substring(0,k) ;
		
		k = pm_url.lastIndexOf('?') ;
		if(k<=0)
			pm_url+="?container_id="+container_id ;
		else
			pm_url+= "&container_id="+container_id ;
	}
	
	String mn ="n" ;
	if(item instanceof MNModule)
		mn = "m" ;
	 
	//System.out.println(pm_url);
	JSONObject jo = item.getParamJO() ;
	String jstr = "{}" ;
	if(jo!=null)
		jstr = jo.toString() ;
	
	String ben_chked = item.isEnable()?"checked":"" ;
	String fulltp = item.getTPFull() ;
	
	boolean can_save = true ;
	//JSONTemp in_jt = null ;
	String in_title = "" ;
	if(item instanceof MNNode)
	{
		MNNode mnnode = (MNNode)item;
		in_title =mnnode.RT_getInTitle();// Convert.plainToJsStr(mnnode.RT_getInTitle()) ;
		if(in_title==null)
			in_title="" ;
		//if(in_jt!=null)
		//	in_title = Convert.plainToJsStr(in_jt.toJSONStr()) ;
		MNModule mmm = mnnode.getOwnRelatedModule() ;
		if(mmm!=null)
			can_save=false;
	}
	
	boolean b_running= false;
	if(item instanceof IMNRunner)
	{
		b_running = ((IMNRunner)item).RT_isRunning() ;
	}
%>
<html>
<head>
<title></title>
<jsp:include page="../head.jsp">
	<jsp:param value="true" name="simple"/>
	<jsp:param value="true" name="tree"/>

</jsp:include>
<script type="text/javascript">

var container_id="<%=container_id%>";
var netid="<%=netid%>";
var itemid="<%=itemid%>";


var pm_url="<%=pm_url%>" ;
var PM_URL_BASE = "<%=pm_url_base%>" ;
var __pm_jo = <%=jstr%> ;
var mn = "<%=mn%>";
var fulltp = "<%=fulltp%>" ;
var form ;
var element;

function get_page_url_base()
{
	let p = window.location.pathname;
	let k = p.lastIndexOf("/") ;
	return p.substring(0,k+1);
}

function init_pm()
{
	if(!pm_url)
		return ;

	send_ajax(pm_url,{container_id:container_id,netid:netid,itemid:itemid,pm_jo:JSON.stringify(__pm_jo)},(bsucc,ret)=>{
		$("#pm_cont").html(ret) ;
		
		on_init_pm_ok() ;
		
		if(typeof(on_after_pm_show)=="function")
			on_after_pm_show(form);
	}) ;

}

function on_init_pm_ok()
{// called by sub pm js
	if(typeof(get_pm_size)=='function')
	{
		let wh = get_pm_size() ;
		if(wh.w<800)
			wh.w = 800;
		dlg.resize_to(wh.w,wh.h+110) ;
	}
	
	if(typeof(set_pm_jo)=='function')
	{
		set_pm_jo(__pm_jo) ;
	}
	
	 form.render();
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

function do_submit(cb)
{
	let pmjo = {};
	if(typeof (get_pm_jo)=='function')
	{
		//cb(false,"no pm edit items found") ;
		//return ;\
		pmjo = get_pm_jo();
	}
	
	let tt = $('#title').val();
	let marks = $("#marks").val();
	let ben = $("#enable").prop("checked") ;
	let res_name = $("#res_name").val() ;
	if(typeof(pmjo) == "string")
	{
		cb(false,pmjo) ;
		return ;
	}
	let rr = {title:tt,enable:ben,pm_jo:pmjo,res_name:res_name,marks:marks};
	cb(true,rr);
}

function save_to_lib()
{
	let pmjo = null;
	if(typeof (get_pm_jo)=='function')
	{
		pmjo = get_pm_jo();
	}
	if(!pmjo)
	{
		dlg.msg("<w:g>no_pm_get</w:g>");
		return ;
	}
	
	if(typeof(pmjo) == "string")
	{
		dlg.msg(pmjo);
		return ;
	}
	
	let tt = $('#title').val()||"";
	dlg.open("../util/dlg_input_txt.jsp?txt_title=<w:g>title</w:g>",
			{title:'<w:g>pls_sure_lib_tt</w:g>',w:'500px',h:'400px',txt:tt},
			['<wbt:lang>ok</wbt:lang>','<wbt:lang>cancel</wbt:lang>'],
			[
				function(dlgw)
				{
					let txt = dlgw.get_input();
					if(!txt)
					{
						dlg.msg("<w:g>pls,input,title</w:g>") ;
						return ;
					}
					tt = txt ;
					let rr = {op:"save_to_lib",container_id:container_id,netid:netid,mn:mn,title:tt,fulltp:fulltp,jstr:JSON.stringify(pmjo)};
					send_ajax("mn_ajax.jsp",rr,(bsucc,ret)=>{
						if(!bsucc||ret!='succ')
						{
							dlg.msg(ret) ;
							return ;
						}
						dlg.msg("save ok") ;
						dlg.close() ;
					}) ;
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}
</script>
<style>
.save_btn
{
	position: absolute;
	right:5px;
	top:5px;
	color:#27ba7d;
	
}

.in_title {position: absolute;left:2px;top:50px;border:1px solid #ccc;background-color: #003a36;color:#00ffe2;cursor: pointer;}
.in_title:hover ~ .child  {display: block;}
</style>
</head>

<body>
<%
if(can_save)
{
%>
<button class="layui-btn layui-btn-sm layui-btn-primary save_btn"  title="<w:g>save_to_lib</w:g>" onclick="save_to_lib()"><i class="fa fa-arrow-right fa-lg"></i><i class="fa fa-book fa-lg"></i></button>
<%
}
%>
<form class="layui-form"  onsubmit="return false;">
  <div class="layui-form-item">
    <label class="layui-form-label"><w:g>title</w:g>:</label>
    <div class="layui-input-inline" style="width:300px;">
      <input type="text" id="title" name="title" value="<%=title %>"  class="layui-input">
    </div>
    <div class="layui-input-inline" style="width:80px;">
      <w:g>enable</w:g><input type="checkbox" class="layui-input" lay-skin="primary" id="enable"  <%=ben_chked %> />
    </div>
    <label class="layui-form-mid"><w:g>mark</w:g>:</label>
    <div class="layui-input-inline" style="width:170px;">
      <input type="text" class="layui-input" lay-skin="primary" id="marks"  value="<%=mark_str %>" />
    </div>
  </div>
<%
if(item instanceof IMNNodeRes)
{
%>
<div class="layui-form-item">
    <label class="layui-form-label"><w:g>res_name</w:g>:</label>
    <div class="layui-input-inline" style="width:46%;" title="<w:g>res_name_ppt</w:g>">
      <input type="text" id="res_name" name="res_name" value="<%=res_name %>"  class="layui-input">
    </div>
    
  </div>
<%
}
%>
  <div id="pm_cont">
  	
  </div>
 </form>
<%
if(b_running)
{
%><div style="border:1px solid red;color:red"><w:g>node_running_stop_first</w:g></div>
<%
}
if(Convert.isNotNullEmpty(in_title))
{
%>
	<button class="in_title" onclick="show_in_title()" onblur="show_in_title(false)" title="input data format"><i class="fa fa-arrow-right"></i></button>
	<span id="intitle_txt" class="in_title child" style="display:none;top:65px;"><%=in_title %></span>
<script type="text/javascript">
function show_in_title(b)
{
	if(b===true)
	{
		$("#intitle_txt").css("display","") ;return;
	}
	if(b===false)
	{
		$("#intitle_txt").css("display","none") ;return;
	}
	if($("#intitle_txt").css("display")=="none")
		$("#intitle_txt").css("display","") ;
	else
		$("#intitle_txt").css("display","none") ;
}
</script>
<%
}

%>

</body>

<script type="text/javascript">


layui.use('form', function(){
	  form = layui.form;
	  element = layui.element;
	  form.render();
	  
	  init_pm() ;
	 
});


</script>

</html>                                                                                                                                                                                                                            
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%><%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
				org.iottree.driver.zigbee.*,
				org.iottree.driver.zigbee.zspec.*,
	java.io.*,org.json.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="w"%><%!
	
	static class NavItem
	{
		String id ;
		
		String ico ;
		
		String tt_en ;
		
		String tt_cn ;
		
		String url ;

		public NavItem(String id,String ico,String tt_en,String tt_cn,String u)
		{
			this.id = id ;
			this.ico = ico ;
			this.tt_en = tt_en ;
			this.tt_cn = tt_cn ;
			this.url = u ;
		}
		
		public String getTitle()
		{
			String ln = Lan.getUsingLang() ;
			if("cn".equals(ln))
				return this.tt_cn ;
			else
				return this.tt_en ;
		}
		
		public JSONObject toJO()
		{
			return new JSONObject().put("id",this.id).put("tt",this.getTitle()).put("u",this.url) ;
		}
	}
	
	static NavItem[] nav_items = new NavItem[]{
			//new NavItem("home","fa-solid fa-house-user","Home","主页","home.jsp")
			new NavItem("dashboard","fa-solid fa-table-columns","Dashboard","仪表板","dashboard.jsp")
			//,new NavItem("devices","fa-solid fa-plug","Devices","设备","devices.jsp")
			,new NavItem("groups","fa-solid fa-border-all","Groups","分组","") //groups.jsp
			,new NavItem("network","fa-solid fa-share-nodes","Network","网络","") //network.jsp
			,new NavItem("setup","fa-solid fa-gear","Setup","设置","") //setup.jsp
	};
	%><%
	if(!Convert.checkReqEmpty(request, out, "path"))
		return ;
	
	String path = request.getParameter("path") ;
	StringBuilder failedr = new StringBuilder() ;
	ZSDriver dd = ZigbeeManager.getZSDriverByPath(path, failedr) ;
	if(dd==null)
	{
		out.print(failedr.toString()) ;
		return ;
	}
	
	String drv_name = dd.getName() ;
	String nav_t = path ;
	String dd_t = dd.getTitle() ;
%><html>
<head>
<title></title>
<jsp:include page="../../head.jsp">
	<jsp:param value="true" name="simple"/>
</jsp:include>
<style type="text/css">
body {background-color: #f5f5f5;overflow: hidden;}
.top {position: absolute;top:0px;left:5px;width:100%;height:50px;background-color:#d9d9d9;
	border-bottom: 1px solid #e6e6e6;line-height: 40px;padding-left:5px;color:#333;}
.top .sub {position: absolute;top:17px;left:28px;font-size:12px;}
.list {position: absolute;top:60px;left:0px;width:100%;bottom: 0px;overflow: auto;padding-top: 5px;overflow: hidden;}

.tn_icon {font-size: 10px;}
.tn_edit {color:green;}
.tn_plug_ico {width:30px;height:30px;background-color:#333333;color:#81ec21;}

.d_item
{
	position: relative;
	display:inline-block;
	float:left;
	width:90%;
	left:5px;
	height:30px;
	border:0px solid #aaa;
	border-radius:6px;cursor:pointer;
	margin:3px;
}
.d_item:hover {background-color:#aeecff}
.d_item .i {position: absolute;left:5px;top:10px;font-size: 12px;}
.d_item .n
{
	position:absolute;
	left:25px;
	top:3px;line-height:30px;
	font-size:12px;
}
.d_item .nc
{
	position:absolute;
	left:25px;
	top:6px;line-height:20px;
	font-size:12px;
}
.d_item .c
{
	position:absolute;
	left:25px;
	bottom:3px;
	font-size:12px;
}
.dddd
{
	position: relative;
	display:inline-block;
	width:90%;
	left:5px;
	height:20px;
	margin:3px;
}
.seled {background-color: #aeecff}
</style>
<script>
</script>
</head>
<body>

<div class="top"  onclick="window.open('main_<%=drv_name %>.jsp?path=<%=path%>')">
	<i class="fa-solid fa-tower-broadcast" style="font-size:13px;border:0px;"></i>&nbsp;&nbsp;<%=nav_t %>
	<div class="sub"><%=dd_t %></div>
</div>
<div class="list">
<%
NavItem first_ni = null ;
for(NavItem ni:nav_items)
{
	if(first_ni==null)
		first_ni = ni ;
	String id = ni.id ;
	String t = ni.getTitle() ;
	String ico = ni.ico ;
	String u = ni.url ;
	String css = "";
	if(Convert.isNullOrEmpty(u))
		css ="color:#aaa;";
%>
 <div class="d_item" id="d_item_<%=id%>" style="<%=css %>" onclick="on_list_ditem_clk('<%=id%>','<%=t%>','<%=u%>')">
 	<div class="i"><i class="<%=ico%>"></i></div>
 	<div class="nc"><%=t %></div>
 </div>
<%
}
%>
<div class="dddd">
</div>
 <button class="d_item" id="d_item_permit_join" onclick="permit_join()" style="border:1px solid #670dd0;text-align: center;">
 	<i class="fa-solid fa-satellite-dish" id="permit_j_ico" style="color:red;"></i>&nbsp;<span id="permit_j_tt"><w:g>permit_join</w:g></span>
 </button>
 
</div>

</body>
<script type="text/javascript">
var path = "<%=path%>" ;
var form ;

var first_ni = <%=first_ni.toJO()%>

layui.use('form', function(){
	  form = layui.form;
	  form.on('select(cat_dds)', function(obj){
		    refresh_tree();
	});
	  
	  form.render() ;
});

function on_permit_join()
{
	let sec = 250;
	
}

function on_count_down_tick(left_s)
{
	$("#permit_j_ico").css("color","green") ;
	let min = ""+parseInt(""+left_s/60) ;
	if(min.length==1)
		min = '0'+min ;
	let sec = ""+parseInt(""+left_s%60) ;
	if(sec.length==1)
		sec = '0'+sec ;
	$("#permit_j_tt").html("<w:g>disable_join</w:g> "+min+":"+sec)
}

function on_count_down_end()
{
	$("#permit_j_ico").css("color","red") ;
	$("#permit_j_tt").html("<w:g>permit_join</w:g>")
}

var remaining = 250; 
var timerId = null;
var b_permit_join_do = false;

function stop()
{
  if (timerId !== null)
  {
    clearInterval(timerId);
    timerId = null;
  }
  
  on_count_down_end();
}

function tick()
{
  remaining -= 1;
  on_count_down_tick(remaining);
  
	if(parent && parent.on_permit_join_tick)
		parent.on_permit_join_tick();
	
  if (remaining <= 0)
  {
    stop();
  }
}

function permit_join()
{
	if(b_permit_join_do)
		return ;
	b_permit_join_do = true ;
	
	if(timerId)
	{
		send_ajax("drv_ins.ajax.jsp",{op:"permit_join",path:path,permit:false},(bsucc,ret)=>{
			b_permit_join_do = false;
			console.log(ret) ;
			if(!bsucc || ret.indexOf('succ=')!=0)
			{
				dlg.msg(ret);return ;
			}
			remaining = parseInt(ret.substring(5)) ;
			//on_count_down_tick(remaining);
			//timerId = setInterval(tick, 1000);
			stop() ;
		});
		return ;
	}
	
	send_ajax("drv_ins.ajax.jsp",{op:"permit_join",path:path,permit:true},(bsucc,ret)=>{
		b_permit_join_do = false;
		console.log(ret) ;
		if(!bsucc || ret.indexOf('succ=')!=0)
		{
			dlg.msg(ret);return ;
		}
		remaining = parseInt(ret.substring(5)) ;
		on_count_down_tick(remaining);
		timerId = setInterval(tick, 1000);
	});
	
}

function on_sel_list()
{
	$("#btn_sel_tree").css("border-color","");
	$("#btn_sel_list").css("border-color","#03a8d8");
	$(".tree").css("display","none") ;
	$(".list").css("display","block") ;
}

var cur_sel_item_id = null ;

function update_list()
{
	$(".d_item").removeClass("seled") ;
	$("#d_item_"+cur_sel_item_id).addClass("seled") ;
}

function on_list_ditem_clk(itemid,tt,u)
{
	if(!u) return ;
	cur_sel_item_id = itemid ;
	u+="?path="+path ;
	update_list()
	if(parent && parent.set_cur_item)
		parent.set_cur_item(itemid,tt,u)
}

on_list_ditem_clk(first_ni.id,first_ni.tt,first_ni.u) ;
</script>
</html>                                                                                                                                                                                                                            
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

        .segs
        {
        	
        }
    </style>
</head>
<body>
 <legend><%=sch.getTitle() %> - <%=dev.getTitle() %>  runtime controller</legend>
<b>Segments</b>

<%
for(SlaveDevSeg seg:segs)
{
	String segid = seg.getId() ;
	int fc = seg.getFC() ;
	String fct = seg.getFCTitle() ;
	int regidx = seg.getRegIdx() ;
	int regnum = seg.getRegNum() ;
	boolean bw = seg.canWriter() ;
%>
<div class="layui-collapse">
 <div class="layui-colla-item">
    <h2 class="layui-colla-title"><%=fc%>:<%=fct %> Address:<%=regidx %> Length:<%=regnum %></h2>
    <div class="layui-colla-content layui-show">
<%
	for(int i = 0 ; i < regnum ; i ++)
	{
		String addr_str = seg.getAddressStr(regidx+i) ;
%><%=addr_str %>: <span id="r_<%=segid%>_<%=i%>" onclick="set_reg_val('<%=segid%>',<%=i%>)" style="cursor:hand;"></span>&nbsp;<%
	}
%>
	</div>
  </div>
</div>
<%
}
%>

</body>
<script src="/_js/layui/layui.all.js"></script>
<script type="text/javascript">

var insid = "<%=insid%>" ;
var chid = "<%=chid%>" ;
var devid = "<%=devid%>" ;

layui.use('element', function(){
	  var element = layui.element;

	});

function refresh_rt()
{
	send_ajax("mslave_rt_ajax.jsp",{insid:insid,chid:chid,devid:devid},function(bsucc,ret){
		if(!bsucc||ret.indexOf("{")!=0)
		{
			return ;
		}
		var ob = null ;
		eval("ob="+ret) ;
		for(var seg of ob.segs)
		{
			var segid = seg.id ;
			for(var i = 0 ; i < seg.datas.length ; i ++)
			{
				$("#r_"+segid+"_"+i).html(""+seg.datas[i]) ;
			}
		}
	});
}
	
function set_reg_val(segid,idx)
{
	event.preventDefault();
	dlg.open("mslave_rt_input.jsp?insid="+insid+"&chid="+chid+"&devid="+devid+"&segid="+segid+"&regidx="+idx,
			{title:"Set Channel Connection"},
			['Ok','Cancel'],
			[
				function(dlgw)
				{
					dlgw.do_submit((bsucc,ret)=>{
						 if(!bsucc)
						 {
							 dlg.msg(ret) ;
							 return;
						 }
						 
						 ret.op="input_v" ;
						 ret.chid = chid ;
						 ret.insid = insid ;
						 ret.devid = devid ;
						 ret.segid=segid ;
						 ret.regidx = idx ;
							send_ajax("./mslave_rt_ajax.jsp",ret,function(bsucc,rr){
								if(!bsucc||"succ"!=rr)
								{
									dlg.msg(rr) ;
									return ;
								}
								
								dlg.close();
								//refresh_me();
							}).fail(function(req, st, err) {
								dlg.msg(err);
							});
				 	});
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}
refresh_rt() ;
	setInterval(refresh_rt,3000) ;
</script>
</html>
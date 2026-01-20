<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="org.json.*,
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.node.*,
				org.iottree.core.router.*,org.iottree.ext.roa.*,
				org.iottree.core.basic.*,
				org.iottree.core.util.web.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="w"%><%--
	RTSP	rtsp://user:pass@192.168.1.100:554/stream1	 
	RTMP	rtmp://server/live/key	 
	HTTP-FLV	http://server/live.flv	 
	HLS (m3u8)	http://server/playlist.m3u8	 
	File	/tmp/video.mp4 或 file:///tmp/video.mp4	 
	WebRTC	webrtc://server/stream	 
	UDP TS	udp://@:5000	 
	TCP	tcp://server:5000	 
	--%><%
	String id = request.getParameter("id") ;
	boolean b_create = false;
	if(Convert.isNullOrEmpty(id))
	{
		id = "ip_"+IdCreator.newSeqId() ; ;
		b_create = true ;
	}
%>
<html>
<head>
<title></title>
<jsp:include page="../../head.jsp"></jsp:include>
<script>
dlg.resize_to(550,300);
</script>
<style>
.conf
{
	position: relative;
	width:90%;
	height:30px;
	border:1px solid ;
}
</style>
</head>
<body>
<form class="layui-form" action="">
<div class="layui-form-item">
    <label class="layui-form-label">Title:</label>
	  <div class="layui-input-inline" style="width: 250px;">
	    <input type="text" id="t" name="t" value=""  autocomplete="off" class="layui-input">
	  </div>
 </div>
<div class="layui-form-item">
    <label class="layui-form-label">Url:</label>
    <div class="layui-input-inline" style="width: 350px;">
      <input type="text" id="u" name="u" value=""  autocomplete="off"  class="layui-input" >
    </div>
   
 </div>
 <div class="layui-form-item">
    <div class="layui-form-label">Options:</div>
	  <div class="layui-input-inline" style="width: 350px;">
	    <textarea type="text" id="opts" name="opts" style="height:60px;" autocomplete="off" class="layui-input"></textarea>
	  </div>
 </div>
</form>
</body>
<script type="text/javascript">

var id = "<%=id%>";
var b_create = <%=b_create%> ;
var pm = dlg.get_opener_opt("input") ;

var node_inp = dlg.get_opener_opt("node_inp") ;

layui.use('form', function(){
	  var form = layui.form;
	  
	  update_ui() ;
	  
	  form.render();
	});

function update_ui()
{
	if(pm)
	{//for edge
		//console.log(pm);
		id = pm.id ;
		$("#t").val(pm.t||"") ;
		$("#u").val(pm.u||"") ;
		$("#opts").val(pm.opts||"") ;
	}
	if(node_inp)
	{
		id = node_inp.id ;
		$("#t").val(node_inp.t||"") ;
		$("#u").val(node_inp.u||"") ;
		$("#opts").val(node_inp.opts||"") ;
	}
}

function win_close()
{
	dlg.close(0);
}

function do_submit(cb)
{
	let t =  $("#t").val() ;
	let u =  $("#u").val() ;
	let opts =  $("#opts").val() ;
	if(!t || !u)
	{
		cb(false,'<w:g>pls,input,title</w:g> Url') ;
		return ;
	}
	
	cb(true,{id:id,t:t,url:u,opts:opts,create:b_create}) ;
}

function do_node_submit(cb)
{
	let t =  $("#t").val() ;
	let u =  $("#u").val() ;
	let opts =  $("#opts").val() ;
	if(!t || !u)
	{
		cb(false,'<w:g>pls,input,title</w:g> Url') ;
		return ;
	}
	
	cb(true,{id:id,t:t,u:u,opts:opts,create:b_create}) ;
}

</script>
</html>
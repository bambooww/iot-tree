<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,org.json.*,
	org.iottree.core.*,
	org.iottree.core.basic.*,
	org.iottree.core.task.*,
	org.iottree.core.util.*,
	org.iottree.core.dict.*,
	org.iottree.core.msgnet.*
	"%><%@ taglib uri="wb_tag" prefix="w"%><%!

%><%
if(!Convert.checkReqEmpty(request, out, "container_id"))
	return ;

String container_id = request.getParameter("container_id");
MNManager mnm= MNManager.getInstanceByContainerId(container_id) ;
if(mnm==null)
{
	out.print("no MsgNet Manager with container_id="+container_id) ;
	return ;
}
List<MNNet> nets = mnm.listNets() ;
JSONArray jarr = new JSONArray() ;
for(MNNet n:nets)
{
	jarr.put(n.toListJO()) ;
}
%><html>
<head>
<title></title>
<jsp:include page="../head.jsp">
<jsp:param value="true" name="simple"/>
</jsp:include>
</head>
<style>

 .btn_sh
 {
  visibility: hidden;display:inline-block;
  width:70px;border:0px solid #ccc;
 }
 
 .btn_sh_c:hover .btn_sh
 {
visibility: visible;
 }
 
.net_item
{
position:relative;
	height:25px;
	border:1px solid;border-radius:5px;
	border-color: #499ef3;
	margin:5px;cursor:pointer;
	white-space: nowrap;
	display:inline-block;
	vertical-align:left;
	padding:2px;
}

.bitem
{
	cursor: pointer;width:30px;margin:1px;
}

</style>
<body marginwidth="0" marginheight="0" style="overflow: hidden;background: #eee;">
 <div id="ccc" style="overflow:auto;width:100%;height:100%;border:0px solid;">
<%
int cc = 0 ;
for(MNNet net:nets)
{
		cc ++ ;
		String cssstr = "" ;
		String tmpid = "" ;
		
			tmpid = "net_"+net.getId() ;
		boolean ben = net.isEnable() ;
		String borderc = "" ;
		String tt = "" ;
		if(!ben)
		{
			borderc = "border-color:#999999;";
			//tt = "<w:g>flow_is_not_en</w:g>";
		}
%>
	<div class="net_item btn_sh_c"  style="<%=borderc%>" title="<%=tt%>">
		&nbsp;<i class="fa fa-code-fork fa-lg fa-rotate-90"></i> <a class="text title" onclick="javascript:open_net('<%=net.getId()%>','<%=net.getTitle() %>')" ><%=net.getTitle() %></a>
		
		<div class="btn_sh" >
            <span class="bitem" onclick="add_or_edit_flow('<%=net.getId()%>')" title="<w:g>edit</w:g>">
            <i class="fa fa-pencil "></i>
           </span>
            <span class="bitem" onclick="exp_flow('<%=net.getId()%>')" title="<w:g>export</w:g>">
            <i class="fa-regular fa-circle-up"></i>
           </span>
           <span class="bitem"  style="color: #e33a3e" onclick="flow_del('<%=net.getId()%>')" title="<w:g>delete</w:g>">
           &nbsp;&nbsp;<i class="fa fa-times "></i>
           </span>
      </div>

	</div>
<%
}
%>
	<button class="net_item" style="position:absolute;right:10px;top:0px;width:40px;" onclick="add_or_edit_flow()"><span class="bitem"><i class="fa-solid fa-plus fa-lg" style="top:10px;"></i></span></button>
	<button class="net_item" style="position:absolute;right:10px;top:33px;width:40px;" onclick="imp_flow()" title="<w:g>import</w:g>"><span class="bitem"><i class="fa-regular fa-circle-left fa-lg" style="top:10px;"></i></span></button>
	
</div>

<%--
  <span class="fa-stack">
							  <i class="fa fa-square fa-stack-1x"></i>
							  <i class="fa fa fa-times fa-stack-1x fa-inverse"></i>
							</span>
 --%>
<script>

var container_id="<%=container_id%>" ;

function open_net(netid,tt)
{
	parent.add_tab("___msgnet_"+netid,`<i class="fa fa-code-fork fa-lg fa-rotate-90"></i> \${tt}`,
			`./mn/mn_net_flow_edit.jsp?container_id=\${container_id}&netid=\${netid}`) ;
}

function add_or_edit_flow(id)
{
	let tt = "<w:g>add,flow</w:g>" ;
	if(id)
		tt  ="<w:g>edit,flow</w:g>" ;
	else
		id=""
	dlg.open("./mn_net_edit.jsp?container_id="+container_id+"&netid="+id,
			{title:tt},
			['<w:g>ok</w:g>','<w:g>cancel</w:g>'],
			[
				function(dlgw)
				{
					dlgw.do_submit((bsucc,ret)=>{
						 if(!bsucc)
						 {
							 dlg.msg(ret) ;
							 return;
						 }
						 
						 ret.op="add_edit_net" ;
						 ret.container_id = container_id ;
						 ret.netid=id ;
						 send_ajax("mn_ajax.jsp",ret,(buscc,ret)=>{
							 if(!bsucc||ret!='succ')
							 {
								 dlg.msg(ret) ;
								 return ;
							 }
							 dlg.msg("<w:g>done</w:g>") ;
							 dlg.close();
							 location.reload();
						 });
				 	});
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

function exp_flow(netid)
{
	dlg.open("./mn_imp_exp.jsp?container_id="+container_id+"&netid="+netid,
			{title:"<w:g>export</w:g>"},
			['<w:g>cancel</w:g>'],
			[
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

function imp_flow()
{
	dlg.open("./mn_imp_exp.jsp",
			{title:"<w:g>import</w:g>"},
			['<w:g>import</w:g>','<w:g>cancel</w:g>'],
			[
				function(dlgw)
				{
					let txt = dlgw.get_txt();
					if(!txt)
					{
						dlg.msg("please input exported json txt") ;
						return ;
					}
					let ob = null ;
					try{
						eval("ob="+txt) ;
					}catch(e) {dlg.msg("not valid json txt:"+e);return;}
					
					let n = ob.name ;
					dlg.close();
					pre_imp(n,ob) ;
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

function pre_imp(n,jo)
{
	dlg.open("./mn_imp_pre.jsp?container_id="+container_id+"&name="+n,{title:"pre import"},
			['<w:g>import</w:g>','<w:g>cancel</w:g>'],
			[
				function(dlgw)
				{
					let txt = dlgw.do_submit((bsucc,ret)=>{
						if(!bsucc) {dlg.msg(ret);return}
						send_ajax("mn_ajax.jsp",{op:"imp_net",container_id:container_id,jstr:JSON.stringify(jo),...ret},(bsucc,ret)=>{
							if(!bsucc || ret!="succ")
							{
								dlg.msg(ret);return;
							}
							dlg.close();
							location.reload();
						});
					});
					
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

function flow_del(id)
{
	dlg.confirm('<w:g>del,this,flow</w:g>?',{btn:["<w:g>yes</w:g>","<w:g>cancel</w:g>"],title:"<w:g>del,confirm</w:g>"},function ()
    {
		let op={op:"del_net",container_id:container_id,netid:id};
		send_ajax("mn_ajax.jsp",op,(bsucc,ret)=>{
			 if(!bsucc||ret!='succ')
			 {
				 dlg.msg(ret) ;
				 return ;
			 }
			 dlg.msg("<w:g>done</w:g>") ;
			 location.reload();
		 });
	});
}

/*
function resize_h()
{
	var h = $(window).height();
	$("#ccc").css("height",h+"px");
}

$(window).resize(function(){
	resize_h();
});

resize_h()
*/
</script>

</body>
</html>
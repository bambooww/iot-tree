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
if(!Convert.checkReqEmpty(request, out, "prjid"))
	return ;

String prjid = request.getParameter("prjid");
UAPrj prj = UAManager.getInstance().getPrjById(prjid) ;
if(prj==null)
{
	out.print("no prj found") ;
	return ;
}
MNManager mnm= MNManager.getInstance(prj) ;
List<MNNet> nets = mnm.listNets() ;
JSONArray jarr = new JSONArray() ;
for(MNNet n:nets)
{
	jarr.put(n.toListJO()) ;
}
%><html>
<head>
<title></title>
<jsp:include page="../head.jsp"></jsp:include>
</head>
<style>

 .btn_sh
 {
  //display:none;
  visibility: hidden;
 }
 
 .btn_sh_c:hover .btn_sh
 {
visibility: visible;
 }
 
.lib_item
{
position:relative;
	height:35px;
	border:1px solid;
	border-color: #499ef3;
	margin:5px;
	white-space: nowrap;
	display:inline-block;
	vertical-align:middle;
	padding:2px;
}

.bitem
{
	cursor: pointer;
}

</style>
<body marginwidth="0" marginheight="0" style="overflow: hidden;">
 <div style="overflow:auto;">
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
	<span class="lib_item btn_sh_c"  style="<%=borderc%>" title="<%=tt%>">
		<i class="fa fa-code-fork fa-lg fa-rotate-90"></i> &nbsp;<a class="text title" href="javascript:open_net('<%=net.getId()%>','<%=net.getTitle() %>')" ><%=net.getTitle() %></a>
		
		<span class="btn_sh">
           <span class="bitem" onclick="add_or_edit_flow('<%=net.getId()%>')" title="<w:g>edit</w:g>">
              <span class="fa-stack fa-1x">
							  <i class="fa fa-square fa-stack-1x"></i>
							  <i class="fa fa fa-pencil  fa-stack-1x fa-inverse"></i>
							</span>
           </span>
           <span class="bitem"  style="color: #e33a3e" onclick="flow_del('<%=net.getId()%>')" title="<w:g>delete</w:g>">
              <span class="fa-stack">
							  <i class="fa fa-square fa-stack-1x"></i>
							  <i class="fa fa fa-times fa-stack-1x fa-inverse"></i>
							</span>
           </span>
           </span>

	</span>
<%
}
%>
	<span class="lib_item" onclick="add_or_edit_flow()"><span class="bitem"><i class="fa-solid fa-plus fa-lg" style="top:10px;"></i></span></span>
</div>
<script>

var prjid="<%=prjid%>" ;

function open_net(netid,tt)
{
	parent.add_tab("___msgnet_"+netid,`<i class="fa fa-code-fork fa-lg fa-rotate-90"></i> \${tt}`,
			`./mn/mn_net_flow_edit.jsp?prjid=\${prjid}&netid=\${netid}`) ;
}

function add_or_edit_flow(id)
{
	let tt = "<w:g>add,flow</w:g>" ;
	if(id)
		tt  ="<w:g>edit,flow</w:g>" ;
	else
		id=""
	dlg.open("./mn_net_edit.jsp?prjid="+prjid+"&netid="+id,
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
						 ret.prjid = prjid ;
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

function flow_del(id)
{
	dlg.confirm('<w:g>del,this,flow</w:g>?',{btn:["<w:g>yes</w:g>","<w:g>cancel</w:g>"],title:"<w:g>del,confirm</w:g>"},function ()
    {
		let op={op:"del_net",prjid:prjid,netid:id};
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

</script>

</body>
</html>
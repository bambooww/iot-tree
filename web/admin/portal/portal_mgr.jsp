<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,org.json.*,
	org.iottree.core.*,
	org.iottree.core.basic.*,
	org.iottree.core.task.*,
	org.iottree.core.util.*,
	org.iottree.core.dict.*,
	org.iottree.portal.*
	"%><%@ taglib uri="wb_tag" prefix="w"%><%!

%><%
if(!Convert.checkReqEmpty(request, out, "prjid"))
	return ;

String prjid = request.getParameter("prjid");
UAPrj prj = UAManager.getInstance().getPrjById(prjid) ;
if(prj==null)
{
	out.print("no prj found");return ;
}
PortalManager pmgr = prj.getPortalManager() ;
Collection<NavFrame> navs = pmgr.getNavFrameAll().values() ;

%><html>
<head>
<title></title>
<jsp:include page="../head.jsp">
<jsp:param value="true" name="simple"/>
</jsp:include>
</head>
<style>
body {font-size:12px;}
 .btn_sh
 {
  visibility: hidden;display:inline-block;
  width:50px;border:0px solid #ccc;
 }
 
 .btn_sh_c:hover .btn_sh
 {
visibility: visible;
 }
 
.net_item
{
position:relative;
	height:20px;
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
.top {overflow:auto;position: absolute;width:95%;top:2px;bottom:50px;border:1px solid #ccc;border-radius: 5px;margin:5px;}
.mid {position: absolute;bottom:35px;width:95%;height:20px;text-align: center;border:0px solid #ccc;font-size:20px;}
.btm {position: absolute;bottom:2px;width:95%;height:30px;border:1px solid #ccc;text-align: center;margin-left:5px;}

</style>
<body marginwidth="0" marginheight="0" style="overflow: hidden;background: #fff;">
 <div id="ccc" class="top">
 <w:g>nav_frame</w:g>
<%
int cc = 0 ;
for(NavFrame net:navs)
{
		cc ++ ;
		String cssstr = "" ;
		String tmpid = "" ;
		
			tmpid = "net_"+net.getId() ;
		String borderc = "" ;
		String tt = "" ;
		if(net.isDefault())//(!ben)
		{
			borderc = "border:2px solid green;";
			//tt = "<w:g>flow_is_not_en</w:g>";
		}
%>
	<div class="net_item btn_sh_c"  style="<%=borderc%>" title="<%=tt%>">
		&nbsp;<i class="fa-solid fa-chalkboard fa-lg fa-rotate-90"></i> <a class="text title" onclick="javascript:open_nf('<%=net.getId()%>','<%=net.getTitle() %>')" ><%=net.getTitle() %>[<%=net.getName() %>]</a>
		&nbsp;<div class="btn_sh" >
            <span class="bitem" onclick="add_or_edit_nf('<%=net.getId()%>','<%=net.getTitle() %>','<%=net.getName() %>')" title="<w:g>edit</w:g>">
            <i class="fa fa-pencil "></i>
           </span>

           <span class="bitem"  style="color: #e33a3e" onclick="nf_del('<%=net.getId()%>')" title="<w:g>delete</w:g>">
           &nbsp;&nbsp;<i class="fa fa-times "></i>
           </span>
      </div>

	</div>
<%
}
%>
	<button class0="net_item" style="right:16px;top:0px;width:30px;height:25px;" onclick="add_or_edit_nf()"><span class="bitem"><i class="fa-solid fa-plus fa-lg" style="top:10px;"></i></span></button>
</div>

<div class="mid"><i class="fa-solid fa-down-long"></i></div>
<div class="btm">
<button style="top:0px;min-width:30px;height:25px;" onclick="page_config()"><span class="bitem"><i class="fa-regular fa-file-lines fa-lg" style="top:10px;"></i><w:g>page,config</w:g></span></button>
<button style="top:0px;min-width:30px;height:25px;" onclick="page_temp()"><span class="bitem"><i class="fa-regular fa-file fa-lg" style="top:10px;"></i></span><w:g>page,templet</w:g></button>
</div>
<script>

var prjid="<%=prjid%>" ;

function page_config()
{
	parent.add_tab("___portal_pc_",`<i class="fa-regular fa-file-lines fa-lg"></i> <w:g>page,config</w:g>`,
	`/admin/portal/portal_main.jsp?prjid=\${prjid}`) ;
}
function page_temp()
{
	parent.add_tab("___portal_temp_",`<i class="fa-regular fa-file"></i> <w:g>page,templet</w:g>`,
	`/admin/portal/templet_main.jsp?prjid=\${prjid}`) ;
}

function open_nf(nf_id,tt)
{
	parent.add_tab("___navfrm_"+nf_id,`<i class="fa-solid fa-chalkboard fa-lg fa-rotate-90"></i><w:g>portal</w:g>-\${tt}`,
			`/admin/portal/portal_navframe_edit.jsp?prjid=\${prjid}&nf_id=\${nf_id}`) ;
}

function add_or_edit_nf(id,t,n)
{
	let tt = "<w:g>add,nav_frame</w:g>" ;
	if(id)
		tt  ="<w:g>edit,nav_frame</w:g>" ;
	else
		id=""
	dlg.open("../util/n_t_d_edit.jsp",
			{title:tt,input:{name:n||"",title:t||""}},
			['<w:g>ok,set_as_main,nav_frame</w:g>','<w:g>ok</w:g>','<w:g>cancel</w:g>'],
			[
				function(dlgw)
				{
					dlgw.do_submit((bsucc,ret)=>{
						 if(!bsucc)
						 {
							 dlg.msg(ret) ;
							 return;
						 }
						 
						 ret.op="set_nf" ;
						 ret.prjid = prjid ;
						 ret.nf_id=id ;
						 ret.def=true;
						 send_ajax("portal_ajax.jsp",ret,(buscc,ret)=>{
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
					dlgw.do_submit((bsucc,ret)=>{
						 if(!bsucc)
						 {
							 dlg.msg(ret) ;
							 return;
						 }
						 
						 ret.op="set_nf" ;
						 ret.prjid = prjid ;
						 ret.nf_id=id ;
						 send_ajax("portal_ajax.jsp",ret,(buscc,ret)=>{
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

function nf_del(id)
{
	dlg.confirm('<w:g>del,this,nav_frame</w:g>?',{btn:["<w:g>yes</w:g>","<w:g>cancel</w:g>"],title:"<w:g>del,confirm</w:g>"},function ()
    {
		let op={op:"del_nf",prjid:prjid,nf_id:id};
		send_ajax("portal_ajax.jsp",op,(bsucc,ret)=>{
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
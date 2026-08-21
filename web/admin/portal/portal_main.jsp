<%@ page contentType="text/html;charset=UTF-8"%><%@page 
	import="org.iottree.core.*,org.iottree.core.util.*,
		org.json.*,org.w3c.dom.*,java.util.*,org.iottree.core.util.xmldata.*" 
		%><%@ taglib uri="wb_tag" prefix="w"%><%
	if(!Convert.checkReqEmpty(request, out, "prjid"))
		return ;
		
	String prjid = request.getParameter("prjid") ;
	UAPrj prj = UAManager.getInstance().getPrjById(prjid) ;
	if(prj==null)
	{
		out.print("no prj found") ;
		return ;
	}
%><html>
<head>
        <jsp:include page="../head.jsp">
        <jsp:param value="true" name="simple"/>
        </jsp:include>
</head>
<style>
iframe
{
width:100%;height:100%;
overflow: hidden;
border:0px;
}
.ccc
{
position:absolute;left:0px;
top:0px;bottom:0px;
overflow:hidden;
margin:5px;
border:1px solid #cecece;
}

.rrr
{
position:absolute;right:0px;
top:0px;bottom:0px;
overflow:hidden;
margin:5px;
border:1px solid #cecece;
}

.top {position: absolute;top:0px;left:0px;width:100%;height:40px;background-color0: #f2f2f2;border-bottom: 1px solid #e6e6e6;}
.btm {position: absolute;top:45px;left:0px;width:100%;bottom: 0px;overflow: auto;}

.h_item {border:1px solid #5d6882;border-radius:5px;width:90%;height:40px;left:3%;position: relative;margin:3px;padding-top:10px;}
.h_item .ppt {position: absolute;top:8px;color:#333333;border:0px solid;text-overflow:ellipsis;white-space: nowrap;overflow: hidden;left:10px;}
.h_item .dt {position: absolute;top:25px;border:0px solid;font-size:10px;color:#a7ec21;padding-left:36px;line-height:20px;}
.h_item:hover {
	background-color: #aaaaaa;
}
.seled {background-color: #aaaaaa;}
.rounded-box {
position: absolute;left:0px;top:200px;
  display: inline-block;
  padding: 2px; 
  border: 2px solid #999999;
  border-radius: 5px; 
  background-color: #a0a0a0; 
  text-align: center; 
  line-height: 1.5;
  font-size: 14px; cursor:pointer;
}

.rrrr-box {
position: absolute;right:0px;top:200px;
  display: inline-block;
  padding: 2px; 
  border: 2px solid #999999;
  border-radius: 5px; 
  background-color: #a0a0a0; 
  text-align: center; 
  line-height: 1.5;
  font-size: 14px; cursor:pointer;
}
</style>
<script>
dlg.dlg_top=true ;
</script>
<body style="overflow: hidden;">
<%--
<div class="ccc"  style="left:00px;top:0px;bottom:0px;width:600px;">
    <iframe id="if_item_list" name="if_item_list"  src="page_list.jsp" style="width:100%;overflow: hidden;"></iframe>
</div>
 --%>
<div class="ccc"  style="left:0px;top:0px;bottom:0px;right:0px;">
	<iframe id="if_detail" name="if_detail"  src="" style="width:100%;overflow: hidden;"></iframe>
</div>


<div class="ccc" id="left_panel" style="left:0%;top:0px;bottom:0px;width:550px;display:none;z-index: 10px;background-color: #ffffff" >
</div>

<div class="rrr" id="right_panel" style="right:0%;top:0px;bottom:0px;width:550px;display:none;z-index: 10px;background-color: #ffffff" >
   <iframe id="if_item_list" name="if_item_list"  src="page_list.jsp?prjid=<%=prjid %>" style="width:100%;overflow: hidden;"></iframe>
</div>
<button id="topr_show_hd" style="position:absolute;top:5px;right:5px;" class="layui-btn layui-btn-sm layui-btn-primary" onclick="show_right_setup()" title="&nbsp;"><i class="fa-solid fa-angle-right"></i></button>

</body>
<script type="text/javascript">
var prjid = "<%=prjid%>" ;

function on_blk_set(op,page_uid,blkn,pblk_tp,pblk_tpt)
{
	switch(op)
	{
	case 'set':
		blk_tp_sel(page_uid,blkn);
		break;
	case 'edit':
		edit_pblk(page_uid,blkn,pblk_tp,pblk_tpt);
		break;
	}
}

function blk_tp_sel(page_uid,blkn)
{
	dlg.open("./page_blk_tp_sel.jsp",
			{title:"选择类型"},
			['关闭'],
			[
				function(dlgw)
				{
					dlg.close();
				}
			],(ret)=>{
				if(!ret)
					return ;
				if(ret.tp=='')
					del_pblk(page_uid,blkn);
				else
					edit_pblk(page_uid,blkn,ret.tp,ret.tt);
			});
}

function edit_pblk(page_uid,blkn,pblk_tp,pblk_tpt)
{
	if(event)
		event.stopPropagation();
	console.log(page_uid,blkn,pblk_tp,pblk_tpt);
	dlg.open(`./page_blk_edit.\${pblk_tp}.jsp?page_uid=\${page_uid}&blkn=\${blkn}`,{title:"编辑区块 - "+pblk_tpt,w:'500px',h:'400px',input:{}},
			['<w:g>ok</w:g>','<w:g>cancel</w:g>'],
			[
				function(dlgw)
				{
					dlgw.do_submit(function(bsucc,ret){
						 if(!bsucc)
		        	     {
							 dlg.msg(ret) ;
							 return ;
		        	     }
						 //console.log(ret) ;
						 send_ajax("page_blk_ajax.jsp",{op:"set_pblk_detail",prjid:prjid,page_uid:page_uid,blkn:blkn,pblk_tp:pblk_tp,jstr:JSON.stringify(ret)},(bsucc,ret)=>{
							 if(!bsucc || ret.indexOf("succ")!=0)
							 {
								 dlg.msg(ret) ;
								 return ;
							 }
							 dlg.close() ;
							 update_detail_show()
						 }) ;
				 	});
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

function del_pblk(page_uid,blkn)
{
	if(event)
		event.stopPropagation();
	dlg.confirm('删除这个块配置 ['+blkn+'] ?', {btn:["确定","关闭"],title:"删除确认"},function ()
	{
		send_ajax("./page_ajax.jsp",{op:"del_pblk",page_uid:page_uid,blkn:blkn},(bsucc,ret)=>{
    		if(!bsucc||ret!="succ")
    		{
    			dlg.msg(ret);
    			return ;
    		}
    		update_detail_show()
    	}) ;
	});
}

function on_page_sel(item)
{
	$("#if_detail").attr("src",`page_detail.jsp?prjid=\${prjid}&page_uid=\${item.page_uid}`)
}

function update_detail_show()
{
	$("#if_detail")[0].contentWindow.location.reload();
}

function show_page_setup(b)
{
	if(b)
		slide_toggle($('#right_panel'),"550px");
	else
		hide_toggle($('#right_panel'));
}

function show_page_setup_win(b)
{
	dlg.open_multi("");
}

function show_page_list(b)
{
	if(b)
		slide_toggle($('#left_panel'),"550px");
	else
		hide_toggle($('#left_panel'));
}

function show_right_setup()
{
	let obj = $('#right_panel') ;
	if(obj.attr('topm_show')=='1')
	{
		obj.animate({width: '0px', opacity: 'hide'}, 'normal',function(){ obj.hide();});
		obj.attr('topm_show',"0") ;
		$("#topr_show_hd").html(`<i class="fa-solid fa-angle-left"></i>`)
		return 0 ;
	}
	else
	{
		obj.animate({width: "550px", opacity: 'show'}, 'normal',function(){ obj.show();});
		obj.attr('topm_show',"1") ;
		$("#topr_show_hd").html(`<i class="fa-solid fa-angle-right"></i>`)
		return 1 ;
	}
}
show_right_setup()
</script>
</html>
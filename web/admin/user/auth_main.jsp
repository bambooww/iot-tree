<%@ page contentType="text/html;charset=UTF-8"%><%@page 
	import="java.io.*,java.util.*,org.json.*,
		org.iottree.core.util.web.*,
				org.iottree.core.util.*,
	org.iottree.core.msgnet.*" %><%@ taglib uri="wb_tag" prefix="wbt"%><%! 

%><%
LoginUtil.SessionItem si = LoginUtil.getUserLoginSession(request) ;
boolean b_admin =  si.isAdmin() ;

%><!DOCTYPE html>
<html>
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
	margin-top: 1px;
}
  .layui-table-cell {
    height: auto;
    line-height: 18px;
}

    </style>
</head>
<body  style="overflow: hidden;">
<form class="layui-form"  onsubmit="return false;">
<table style="width:100%;height:40px;border-bottom: 1px solid #e6e6e6;">
	<tr>
		<td style="width:70%;padding-left:5px;font-weight: bold;">用户列表 <span id="top_tt"></span></td>
		<td style="padding:5px;">

      </td>
		<td style="text-align: right;padding-right:5px;width:100px;">
		<button id="top_oper_add" class="layui-btn layui-btn-sm layui-btn-primary" onclick="edit_user()" title="&nbsp;新增"><i class="fa fa-plus"></i></button>
		</td>
	</tr>
</table>
</form>
<table id="user_list"  lay-filter="user_list"  lay-size="sm" lay-even="true" style="top:1px;width:99%;">

</table>
<script type="text/html" id="row_toolbar">
<div class="layui-btn-group">
<%
if(b_admin)
{
%>
<button type="button" class="layui-btn layui-btn-xs layui-btn-primary" lay-event="edit"><i class="fa fa-pencil"></i></button>
<button type="button" class="layui-btn layui-btn-xs layui-btn-primary" lay-event="chg_psw" title="修改用户登录密码" ><i class="fa-solid fa-key"></i></button>
<button type="button" class="layui-btn layui-btn-xs layui-btn-primary layui-border-red"  lay-event="del" title="delete"><i class="fa fa-times"></i></button>
<%
}
%>
  
</div>
</script>

<script>

var form ;
var table ;
var table_cur_page = 1 ;
//if(b_sel)
//	dlg.resize_to(600,700) ;

function edit_user(user)
{	
	let editt = "新增用户" ;
	let username = "" ;
	let pm = null ;
	let op="user_add" ;
	if(user)
	{
		//console.log(user) ;
		username = user.usern ;
		pm={username:username,disnamen:user.disn} ;
		editt = "编辑用户" ;
		op = "user_edit" ;
	}
	
	dlg.open("auth_user_edit.jsp",
			{title:editt,w:'500px',h:'400px',pm:pm},
			['确定','取消'],
			[
				function(dlgw)
				{
					dlgw.do_submit(function(bsucc,vv){
						 if(!bsucc)
		        	     {
							 dlg.msg(vv) ;
							 return ;
		        	     }
						 
						 send_ajax("auth_ajax.jsp",{op:op,...vv},(bsucc,ret)=>{
							 if(!bsucc || ret.indexOf("succ")!=0)
							 {
								 dlg.msg(ret) ;
								 return ;
							 }
							 
							 refresh_table();
							 dlg.close() ;
						 }) ;
						 
				 	});
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

function render_tb()
{
	  let cols = [];
	 //cols.push({field: 'n', title: '<wbt:g>name</wbt:g>', width:'25%'}) ;
	 cols.push({field: 'usern', title: '注册名', width:'20%'});
	 cols.push({field: 'disn', title: '显示名', width:'20%'});
	 //cols.push({field: 'name_en', title: '英文名', width:'20%'});
	
	 cols.push({field: 'st', title: '状态', width:'10%',templet:function(res){
		 //if(res.st!=1) return res.stt||"" ;
		 //console.log(res) ;
		 //return `\${res.stt} <button style="border:1px solid #ccc" onclick="recover_user('\${res.userid}')">恢复</button>`
		 return "";
	 }});

	 cols.push({field: 'Oper', title: '<wbt:g>oper</wbt:g>', width:"15%", templet:function(res){
		 if(res.usern=='root'||res.usern=='admin')
			 return "" ;
		 return `
		 <div class="layui-btn-group">	 
		 <button type="button" class="layui-btn layui-btn-xs layui-btn-primary" lay-event="edit"><i class="fa fa-pencil"></i></button>
		 <button type="button" class="layui-btn layui-btn-xs layui-btn-primary" lay-event="chg_psw" title="修改用户登录密码" ><i class="fa-solid fa-key"></i></button>
		 <button type="button" class="layui-btn layui-btn-xs layui-btn-primary layui-border-red"  lay-event="del" title="delete"><i class="fa fa-times"></i></button>
		 </div>`;
	 },toolbar0: '#row_toolbar'}) ;
	 
	
	table.render({
	    elem: '#user_list'
	    ,height: "full-40"
	    ,url: `auth_ajax.jsp?op=list_users`
	    ,page: {layout:['prev', 'page', 'next'],limit:25,theme:"#c00"} //open page
	    ,cols: [cols]
	  ,parseData:function(res){
			if(res.data.length==0){
				return{
					'code':'201',
					'msg':'无用户'
				};
			};
		}
	    ,done:function(res, curr, count){
		   	 table_cur_page = curr ;
		   	 var trs = $(".layui-table-body.layui-table-main tr");
		   	 if(res && res.data)
		   	 {
		   		for(var i = 0 ; i < res.data.length;i++)
		  		 {
		  		    //if(i%2==1)
			    	//	 trs.eq(i).css("background-color","#f2f2f2");
			     }
		   	 }
	   	 }
	  });
	  
	  table.on('tool(user_list)', function(obj){ // lay-filter="mc_acc_list"
		  var data = obj.data; //cur d
		  var lay_evt = obj.event; // lay-event
		  var tr = obj.tr; //tr DOM
		 
		  if(lay_evt === 'setup'){ //
			  
		  }
		  else if(lay_evt==='chg_psw')
		  {
			 chg_user_psw(data);
		  }
		  else if(lay_evt === 'del')
		  {
			  del_user(data.userid);
		  }
		  else if(lay_evt === 'edit')
		  {
			  edit_user(data) ;
		  }
		});
	  
	  table.on('row(user_list)', function(obj)
			  {
		  var trs = $(".layui-table-body.layui-table-main tr");
		  trs.each(function(){
			  $(this).removeClass("seled") ;
		  })
		  obj.tr.addClass("seled");
				  var data = obj.data; //cur d
				  on_sel_user(data)
			  });
	 // refresh_table(true);
}

function refresh_table()
{
	let pm = {url:"auth_ajax.jsp?op=list_users&orgid="+(orgid||"")+"&roleid="+(roleid||"")};
	table.reload("user_list",pm);
}

function recover_user(userid)
{
	if(event) event.stopPropagation();
	dlg.confirm('确定要恢复此用户么?',{btn:["确定","取消"],title:"恢复确认"},function ()
		    {
			send_ajax("auth_ajax.jsp",{op:"user_set_state",userid:userid,userst:0},function(bsucc,ret){
	    		if(!bsucc || ret!='succ')
	    		{
	    			dlg.msg("恢复错误:"+ret) ;
	    			return ;
	    		}
				refresh_table();
	    	}) ;
		});
}

function chg_user_psw(u)
{
	if(event) event.stopPropagation();
	
	//console.log(u) ;
	dlg.open("./auth_chg_psw.jsp",{title:`修改用户 \${u.name_cn} [\${u.name}]登录密码`,w:'500px',h:'400px'},
			['确定','取消'],
			[
				function(dlgw)
				{
					let rrr = dlgw.get_new_psw();
					if(typeof(rrr)=='string')
					{
						dlg.msg(rrr);return;
					}
					
					 send_ajax("./auth_ajax.jsp",{op:"admin_chg_psw",name:u.name,...rrr},(bsucc,ret)=>{
						 if(!bsucc || ret!="succ")
						 {
							 dlg.msg(ret) ;
							 return ;
						 }
						 dlg.msg("修改成功")
						 dlg.close() ;
					 }) ;
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

function del_user(userid)
{
	dlg.confirm('确定要删除此用户么?',{btn:["<wbt:g>yes</wbt:g>","<wbt:g>cancel</wbt:g>"],title:"<wbt:g>del,confirm</wbt:g>"},function ()
		    {
					//send_ajax("auth_ajax.jsp",{op:"user_del",userid:userid},function(bsucc,ret){
					send_ajax("auth_ajax.jsp",{op:"user_set_state",userid:userid,userst:1},function(bsucc,ret){
			    		if(!bsucc || ret!='succ')
			    		{
			    			dlg.msg("<wbt:g>del,err</wbt:g>:"+ret) ;
			    			return ;
			    		}
						refresh_table();
			    	}) ;
		});
}
	
var cur_sel_user = null;

function on_sel_user(user)
{
	let userid = user.userid;
	let t = user.t;
	let fmid = FindFrameWin('auth_user_roles');
	let forgs = FindFrameWin('auth_user_orgs');
	if(fmid)
		fmid.location.href = "auth_role.jsp?userid="+userid ;
	if(forgs)
		forgs.location.href = "auth_user_orgs.jsp?userid="+userid ;
	cur_sel_user = user ;
}

function get_cur_sel_user()
{
	return cur_sel_user ;
}

function set_user_orgn(userid)
{
	//console.log(dc_id,deviceid) ;
	dlg.open("../util/dlg_tree.jsp",{title:"设置用户所属部门",w:'500px',h:'400px',pm:{du:"../user_mgr/auth_ajax.jsp?op=treen"}},
			['确定','取消'],
			[
				function(dlgw)
				{
					let sel = dlgw.get_selected();
					//console.log(sel) ;
					if(!sel)
					{
						dlg.msg("请选择某个部门");
						return ;
					}
					
					send_ajax("auth_ajax.jsp",{op:"user_set_bt_org",userid:userid,orgid:sel.id},(bsucc,ret)=>{
						if(!bsucc || ret!="succ")
						{
							dlg.msg(ret) ;
							return ;
						}
						dlg.close() ;
						 refresh_table();
					});
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

layui.use(['table','form'], function()
		{
	form = layui.form;
	  
	  form.on('select(lib_list)', function(obj){
		      on_lib_chg();
		  });
	  
		  table = layui.table;
		  render_tb() ;
		});


function set_org_node(orgnid,nd)
{
	orgid = orgnid ;
	orgn = nd ;
	roleid==null;
	$("#top_tt").html("部门："+orgn.orgt) ;
	refresh_table() ;
}

function set_role(r)
{
	roleid = r.roleid;
	role = r ;
	orgid = null ;
	$("#top_tt").html("角色："+r.t) ;
	refresh_table() ;
}

</script>
</body>
</html>
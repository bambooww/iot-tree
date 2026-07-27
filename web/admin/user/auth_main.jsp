<%@ page contentType="text/html;charset=UTF-8"%><%@page 
	import="java.io.*,java.util.*,org.json.*,
		org.iottree.core.util.web.*,
				org.iottree.core.util.*,
	org.iottree.core.msgnet.*" %><%@ taglib uri="wb_tag" prefix="w"%><%! 

%><%
LoginUtil.SessionItem si = LoginUtil.getUserLoginSession(request) ;
boolean b_admin =  si.isAdmin() ;
boolean b_dlg = "true".equals(request.getParameter("dlg")) ;
JSONArray roles_jarr = LoginUtil.listRoleAllJArr() ;
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
.left {position: absolute;top:0px;bottom:0px;width:70%;left:0px;}
.right {position: absolute;top:0px;bottom:0px;width:30%;right:0px;border:1px solid #ccc;}
.right #role_list {width:100%;overflow-y:auto;top:40px;bottom:15px;border:0px solid red;position: absolute;}

.role {position: relative;width:90%;margin:10px;border:1px solid #ccc;border-radius: 5px;height:30px;}
.role .role_chk {position: absolute;top:3px;left:5px;}
.role .n {position: absolute;bottom:5px;right:55px;}
.role .t {position: absolute;top:5px;left:23px;font-weight:bold;}
.role .op{position: absolute;top:5px;right:5px;}
.role .op .ico{cursor: pointer;}
    </style>
</head>
<body  style="overflow: hidden;">
<form class="layui-form"  onsubmit="return false;">

</form>
<div class="left">
<table style="width:100%;height:40px;border-bottom: 1px solid #e6e6e6;">
	<tr>
		<td style="width:70%;padding-left:5px;font-weight: bold;"><w:g>user,list</w:g> <span id="top_tt"></span>
		&nbsp;&nbsp;&nbsp;<button id="top_oper_add" class="layui-btn layui-btn-sm layui-btn-primary" onclick="edit_user()" title="&nbsp;add user"><i class="fa fa-plus"></i></button>
		</td>
		<td style="padding:5px;">

      </td>
		<td style="text-align: right;padding-right:5px;width:10px;">
		
		</td>
	</tr>
</table>
<table id="user_list"  lay-filter="user_list"  lay-size="sm" lay-even="true" style="top:1px;width:99%;">

</table>
</div>
<div class="right">
<table style="width:100%;height:40px;border-bottom: 1px solid #e6e6e6;">
	<tr>
		<td style="width:70%;padding-left:5px;font-weight: bold;"><w:g>roles,list</w:g> <span id="top_tt"></span>
		&nbsp;&nbsp;&nbsp;<button id="top_oper_add" class="layui-btn layui-btn-sm layui-btn-primary" onclick="edit_role()" title="&nbsp;add role"><i class="fa fa-plus"></i></button>
		&nbsp;&nbsp;&nbsp;<button id="top_set_user_role" class="layui-btn layui-btn-sm layui-btn-primary" onclick="set_user_roles()" title="&nbsp;save role to user"><i class="fa fa-save"></i><i class="fa fa-arrow-right"></i><i class="fa fa-user"></i></button>
		</td>
		<td style="padding:5px;">

      </td>
		<td style="text-align: right;padding-right:5px;width:10px;">
		
		</td>
	</tr>
</table>
<div id="role_list"  >

</div>
</div>
<script type="text/html" id="row_toolbar">
<div class="layui-btn-group">
<%
if(b_admin)
{
%>
<button type="button" class="layui-btn layui-btn-xs layui-btn-primary" lay-event="edit"><i class="fa fa-pencil"></i></button>
<button type="button" class="layui-btn layui-btn-xs layui-btn-primary" lay-event="chg_psw" title="change password" ><i class="fa-solid fa-key"></i></button>
<button type="button" class="layui-btn layui-btn-xs layui-btn-primary layui-border-red"  lay-event="del" title="delete"><i class="fa fa-times"></i></button>
<%
}
%>
  
</div>
</script>

<script>
var b_dlg = <%=b_dlg%>;
if(b_dlg)
	dlg.resize_to(1000,700) ;
var form ;
var table ;
var table_cur_page = 1 ;
//if(b_sel)
//	dlg.resize_to(600,700) ;
var roles = <%=roles_jarr %>

function update_roles()
{
	let ss = "" ;
	for(let r of roles)
	{
		ss += `<div class="role">
			<input type="checkbox" class="role_chk" role="\${r.role_n}" id="rolechk_\${r.role_n}" onclick='on_role_chk_clk()'/>
			<div class="t">\${r.role_t}</div><div class="n">\${r.role_n}</div>
			<div class="op">`;
			if(r.role_n!='admin')
				ss += `<i class="fa fa-pencil ico" onclick="edit_role('\${r.role_n}','\${r.role_t}')"></i>&nbsp;&nbsp;&nbsp;<i class="fa fa-times ico" onclick="del_role('\${r.role_n}')"></i>`
		ss += `</div></div>` ;
	}
	$("#role_list").html(ss) ;
}

function reload_roles()
{
	send_ajax("auth_ajax.jsp",{op:"list_roles"},(bsucc,ret)=>{
		if(!bsucc||ret.indexOf("[")!=0)
		{
			dlg.msg(ret);return;
		}
		eval("roles="+ret) ;
		update_roles()
	})
}

update_roles();

function edit_role(n,t)
{	
	let editt = "<w:g>add,role</w:g>" ;
	let username = "" ;
	let pm = null ;
	if(n)
	{
		//console.log(user) ;
		pm={name:n,title:t} ;
		editt = "<w:g>edit,role</w:g>" ;
	}
	
	dlg.open("../util/n_t_d_edit.jsp?hide_d=true",
			{title:editt,w:'500px',h:'400px',input:pm},
			['<w:g>ok</w:g>','<w:g>cancel</w:g>'],
			[
				function(dlgw)
				{
					dlgw.do_submit(function(bsucc,vv){
						 if(!bsucc)
		        	     {
							 dlg.msg(vv) ;
							 return ;
		        	     }
						 let sendpm = {op:"set_role",...vv}
						 send_ajax("auth_ajax.jsp",sendpm,(bsucc,ret)=>{
							 if(!bsucc || ret.indexOf("succ")!=0)
							 {
								 dlg.msg(ret) ;
								 return ;
							 }
							 
							 reload_roles()
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

function del_role(n)
{
	dlg.confirm('<w:g>del,this,role</w:g>?',{btn:["<w:g>yes</w:g>","<w:g>cancel</w:g>"],title:"<w:g>del,confirm</w:g>"},function ()
		    {
					//console.log(u);
					send_ajax("auth_ajax.jsp",{op:"del_role",name:n},function(bsucc,ret){
			    		if(!bsucc || ret!='succ')
			    		{
			    			dlg.msg("<w:g>del,err</w:g>:"+ret) ;
			    			return ;
			    		}
			    		reload_roles()
			    	}) ;
		});
}

function edit_user(user)
{	
	let editt = "<w:g>add,user</w:g>" ;
	let username = "" ;
	let pm = null ;
	let op="user_add" ;
	if(user)
	{
		//console.log(user) ;
		username = user.usern ;
		pm={username:username,disname:user.disn} ;
		editt = "<w:g>edit,user</w:g>" ;
		op = "user_edit" ;
	}
	
	dlg.open("auth_user_edit.jsp",
			{title:editt,w:'500px',h:'400px',pm:pm},
			['<w:g>ok</w:g>','<w:g>cancel</w:g>'],
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
	 //cols.push({field: 'n', title: '<w:g>name</w:g>', width:'25%'}) ;
	 cols.push({field: 'usern', title: '<w:g>name</w:g>', width:'20%'});
	 cols.push({field: 'disn', title: '<w:g>show,name</w:g>', width:'20%'});
	 cols.push({field: 'roles_t', title: '<w:g>user,roles</w:g>', width:'30%'});
	
	 cols.push({field: 'st', title: '<w:g>state</w:g>', width:'10%',templet:function(res){
		 return res.state_t_c;
	 }});

	 cols.push({field: 'Oper', title: '<w:g>oper</w:g>', width:"15%", templet:function(res){
		 if(res.usern=='root'||res.usern=='admin')
			 return "" ;
		 return `
		 <div class="layui-btn-group">	 
		 <button type="button" class="layui-btn layui-btn-xs layui-btn-primary" lay-event="edit"><i class="fa fa-pencil"></i></button>
		 <button type="button" class="layui-btn layui-btn-xs layui-btn-primary" lay-event="chg_psw" title="change password" ><i class="fa-solid fa-key"></i></button>
		 <button type="button" class="layui-btn layui-btn-xs layui-btn-primary layui-border-red"  lay-event="del" title="delete"><i class="fa fa-times"></i></button>
		 </div>`;
	 },toolbar0: '#row_toolbar'}) ;
	 
	
	table.render({
	    elem: '#user_list'
	    ,height: "full-50"
	    ,url: `auth_ajax.jsp?op=list_users`
	    ,page: {layout:['prev', 'page', 'next'],limit:25,theme:"#c00"} //open page
	    ,cols: [cols]
	  ,parseData:function(res){
			if(res.data.length==0){
				return{
					'code':'201',
					'msg':'No Data'
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
			  del_user(data);
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
	let pm = {url:"auth_ajax.jsp?op=list_users"};
	table.reload("user_list",pm);
}

function recover_user(userid)
{
	if(event) event.stopPropagation();
	dlg.confirm('<w:g>confirm,recover,user</w:g>?',{btn:["<w:g>ok</w:g>","<w:g>cancel</w:g>"],title:"<w:g>recover,confirm</w:g>"},function ()
		    {
			send_ajax("auth_ajax.jsp",{op:"user_set_state",userid:userid,userst:0},function(bsucc,ret){
	    		if(!bsucc || ret!='succ')
	    		{
	    			dlg.msg("<w:g>recover,err</w:g>:"+ret) ;
	    			return ;
	    		}
				refresh_table();
	    	}) ;
		});
}

function chg_user_psw(u)
{
	if(event) event.stopPropagation();
	
	dlg.open("./auth_chg_psw.jsp",{title:`<w:g>edit,user</w:g> \${u.disn} [\${u.usern}]<w:g>psw</w:g>`,w:'500px',h:'400px'},
			['<w:g>ok</w:g>','<w:g>cancel</w:g>'],
			[
				function(dlgw)
				{
					let rrr = dlgw.get_new_psw();
					if(typeof(rrr)=='string')
					{
						dlg.msg(rrr);return;
					}
					 send_ajax("./auth_ajax.jsp",{op:"admin_chg_psw",username:u.usern,...rrr},(bsucc,ret)=>{
						 if(!bsucc || ret!="succ")
						 {
							 dlg.msg(ret) ;
							 return ;
						 }
						 dlg.msg("done")
						 dlg.close() ;
					 }) ;
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

function del_user(u)
{
	dlg.confirm('<w:g>del,this,user</w:g>?',{btn:["<w:g>yes</w:g>","<w:g>cancel</w:g>"],title:"<w:g>del,confirm</w:g>"},function ()
		    {
					//console.log(u);
					send_ajax("auth_ajax.jsp",{op:"user_del",username:u.usern},function(bsucc,ret){
			    		if(!bsucc || ret!='succ')
			    		{
			    			dlg.msg("<w:g>del,err</w:g>:"+ret) ;
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
	//console.log(user) ;
	cur_sel_user = user ;
	show_cur_user_roles();
}

function get_cur_sel_user()
{
	return cur_sel_user ;
}

function show_cur_user_roles()
{
	let rrs=[];
	if(cur_sel_user)
	{
		rrs = cur_sel_user.roles||[] ;
	}
	$(".role_chk").each(function(){
		let ob = $(this) ;
		let rolen = ob.attr("role") ;
		ob.prop("checked",rrs.indexOf(rolen)>=0);
	})
}

function set_user_roles()
{
	if(!cur_sel_user)
	{
		dlg.msg("<w:g>pls,select,user</w:g>") ;return ;
	}
	
	let selrs = [];
	$(".role_chk").each(function(){
		let ob = $(this) ;
		let rolen = ob.attr("role") ;
		if(ob.prop("checked"))
			selrs.push(rolen) ;
	})
	send_ajax("auth_ajax.jsp",{op:"user_set_roles",username:cur_sel_user.usern,roles:selrs.join(",")},(bsucc,ret)=>{
		if(!bsucc||ret!='succ')
		{
			dlg.msg(ret);return;
		}
		refresh_table();
		set_role_dirty(false);
	})
}

function on_role_chk_clk()
{
	if(!cur_sel_user)
	{
		set_role_dirty(false);return ;
	}
	set_role_dirty(true);
}

function set_role_dirty(b)
{
	$("#top_set_user_role").css("background-color",b?"yellow":"") ;
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


</script>
</body>
</html>
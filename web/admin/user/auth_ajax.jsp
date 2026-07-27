<%@ page contentType="text/html;charset=UTF-8"%><%@page 
	import="java.io.*,java.util.*,org.json.*,
		org.iottree.core.util.web.*,
				org.iottree.core.util.*,
	org.iottree.core.msgnet.*" %><%!
	
	
	
%><%
if(!Convert.checkReqEmpty(request, out,"op"))
	return ;

LoginUtil.SessionItem si = LoginUtil.getUserLoginSession(request) ;
if(si==null)
{
	out.print("no right") ;
	return ;
}
boolean badmin =  si.isAdmin();
String op = request.getParameter("op");

String name = request.getParameter("name") ;
String title = request.getParameter("title") ;
String username = request.getParameter("username") ;
String disname = request.getParameter("disname") ;
List<String> roles = Convert.splitStrWith(request.getParameter("roles"), ",|") ;
boolean b_self = si.usern.equals(username) ;

boolean b_sel = "true".equals(request.getParameter("sel")) ;
LoginUtil.UserAuthItem user = null ;


String psw = request.getParameter("psw") ;
String old_psw = request.getParameter("old_psw") ;
String new_psw = request.getParameter("new_psw") ;

StringBuilder failedr = new StringBuilder() ;
//System.out.println("op=="+op) ;
try
{
	switch(op)
	{
	case "list_roles":
		JSONArray roles_jarr = LoginUtil.listRoleAllJArr() ;
		roles_jarr.write(out) ;
		return ;
	case "set_role":
		if(!Convert.checkReqEmpty(request, out,"name","title"))
			return ;
		if(LoginUtil.setRole(name, title, failedr)!=null)
			out.print("succ");
		else
			out.print(failedr.toString()) ;
		return ;
	case "del_role":
		if(!Convert.checkReqEmpty(request, out,"name"))
			return ;
		if(LoginUtil.delRole(name)!=null)
			out.print("succ");
		else
			out.print("delete role failed") ;
		return ;
	case "list_users":
		if(!badmin)
		{
			out.print("no right") ;
			return ;
		}
		LinkedHashMap<String,LoginUtil.UserAuthItem> us = LoginUtil.listUserAll(); ;
	%>
	{"code":0,"msg":"","count":<%=us.size()%>,
	"data":
		[
	<%
	boolean bfirst = true; 
	JSONObject tmpjo ;
	for(LoginUtil.UserAuthItem u:us.values())
	{
		if(u.getState()!=LoginUtil.UserState.Normal)
			continue ;
		if(bfirst)
			bfirst=false;
		else
			out.print(",");
		
		tmpjo = u.toListJO() ;
		tmpjo.write(out) ;
	}
	%>
		]
	}
	<%
		return ;
	case "user_edit":
		if(!Convert.checkReqEmpty(request, out,"username","disname"))
			return ;
		if(!badmin && !b_self)
		{
			out.print("no right") ;
			return ;
		}
		
		if(LoginUtil.updateUser(username, disname, failedr) !=null)
			out.print("succ") ;
		else
			out.print("edit failed:"+failedr.toString()) ;
		return ;
	case "user_add":
		if(!Convert.checkReqEmpty(request, out,"username","disname","psw"))
			return ;
		if(!badmin)
		{
			out.print("no right") ;
			return ;
		}
		
		if(LoginUtil.addUser(username, disname,psw, failedr) !=null)
			out.print("succ") ;
		else
			out.print("edit failed:"+failedr.toString()) ;
		return ;
	case "user_set_roles":
		if(!Convert.checkReqEmpty(request, out,"username"))
			return ;
		if(!badmin)
		{
			out.print("no right") ;
			return ;
		}
		
		if(LoginUtil.setUserRoles(username, roles, failedr) !=null)
			out.print("succ") ;
		else
			out.print(failedr.toString()) ;
		return ;
	case "user_del":
		if(!Convert.checkReqEmpty(request, out,"username"))
			return ;
		if(!badmin)
		{
			out.print("no right") ;
			return ;
		}
		if(LoginUtil.delUser(username,false,failedr))
			out.print("succ") ;
		else
			out.print("del failed:"+failedr) ;
		return ;
	case "admin_chg_psw":
		if(!Convert.checkReqEmpty(request, out,"username","new_psw"))
			return ;
		if(!badmin)
		{
			out.print("no right") ;
			return ;
		}
		
		if(LoginUtil.changeUserPswAdmin(username,new_psw,failedr)!=null)
			out.print("succ") ;
		else
			out.print("修改密码失败") ;
		return ;
	case "chg_psw":
		if(!Convert.checkReqEmpty(request, out,"username","new_psw","old_psw"))
			return ;
		if(!badmin || !b_self)
		{
			out.print("no right") ;
			return ;
		}
		if(LoginUtil.changeUserPsw(username,new_psw,old_psw,failedr)!=null)
			out.print("succ") ;
		else
			out.print("修改密码失败") ;
		return ;
	
default:
		break ;
	}
}
catch(Exception eeee)
{
	eeee.printStackTrace() ;
	out.print(eeee.getMessage());
}
%>
<%@ page contentType="text/html;charset=UTF-8" isELIgnored="false"%>
<%@ page import="java.util.*,
				java.io.*,
				java.util.*,
				org.iottree.system.*,
				java.net.*"%><%
	String id = request.getParameter("id");
	if(Convert.isNullOrEmpty(id))
	{
		out.print("no id input") ;
		return ;
	}
	String txt = request.getParameter("txt") ;
	
	File f = new File(Config.getDataDirBase()+"/tmp/"+id+".txt") ;
	if(!f.getParentFile().exists())
		f.getParentFile().mkdirs();
	if("save".equalsIgnoreCase(request.getParameter("op")))
	{
		try(FileOutputStream fos = new FileOutputStream(f))
		{
	fos.write(txt.getBytes("UTF-8"));
		}
		out.print("save ok");
		return ;
	}
	
	if(f.exists())
	{
		txt= Convert.readFileTxt(f, "UTF-8") ;
		out.print(txt) ;
		return ;
	}
%>
{"name":"lay3","bvis":true,"dis":[{"id":"g1","_cn":"DrawItems","x":50,"y":300,
	"items":[
	{"id":"id1","_cn":"DIRect","x":1,"y":-10,"w":20,"h":200,"fillColor":"rgba(0,0,230,0.5)",radius:4,"borderColor":"#1ca261",border:2}
   ,{"id":"id2","_cn":"DIRect","x":0,"y":10,"w":200,"h":20,border:2,radius:3,borderColor:"red"}
	]
}
	,{"id":"id3","_cn":"DIRect","x":-120,"y":-110,"w":50,"h":50,"fillColor":"rgba(230,0,230,0.5)",radius:4,"borderColor":"#1ca261",border:2}
	,{"id":"id4","_cn":"DIRect","x":-60,"y":-110,"w":50,"h":50,"fillColor":"rgba(230,0,230,0.5)",radius:4,"borderColor":"#1ca261",border:2}
	,{"id":"id5","_cn":"DIRect","x":0,"y":-110,"w":50,"h":50,"fillColor":"rgba(230,0,230,0.5)",radius:4,"borderColor":"#1ca261",border:2}
	,{"id":"id6","_cn":"DIRect","x":60,"y":-110,"w":50,"h":50,"fillColor":"rgba(230,0,230,0.5)",radius:4,"borderColor":"#1ca261",border:2}
]}

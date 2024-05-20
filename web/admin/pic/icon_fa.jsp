<%@ page contentType="text/html;charset=UTF-8"%><%@page 
	import="java.io.*,java.util.*,org.iottree.core.*,
				org.iottree.core.util.*
		" %><%!static ArrayList<String> faNames = null;
public static ArrayList<String> getFANames() throws Exception
{
	if(faNames!=null)
		return faNames;
	ArrayList<String> ss = new ArrayList<>();
	File nf = new File(Config.getWebappBase()+"/_js/font470_icon_names.txt");
	try(FileInputStream fis = new FileInputStream(nf);)
	{
		InputStreamReader isr = new InputStreamReader(fis);
		BufferedReader br = new BufferedReader(isr);
		String ln;
		while((ln=br.readLine())!=null)
		{
			ln = ln.trim();
			if(Convert.isNullOrEmpty(ln))
				continue ;
			int k = ln.indexOf(' ');
			if(k>0)
				ln = ln.substring(0,k);
			ss.add(ln) ;
		}
	}
	
	faNames=ss ;
	return ss ;
}%><%
	
//ArrayList<String> fans = getFANames();
%><!DOCTYPE html>
<html>
<head>
<title></title>
<script src="/_js/jquery-1.12.0.min.js"></script>
<script src="/_js/bootstrap/js/bootstrap.min.js"></script>
<script type="text/javascript" src="/_js/ajax.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script src="/_js/dlg_layer.js"></script>
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js"></script>
<link  href="/_js/bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css" >
<script src="/_js/oc/oc.js"></script>
<link type="text/css" href="/_js/oc/oc.css" rel="stylesheet" />
<link  href="/_js/font4.7.0/css/font-awesome.css"  rel="stylesheet" type="text/css" >
</head>
<style>
i:hover{
color: red;
}
</style>
<script type="text/javascript">
function drag(ev)
{
	var tar = ev.target;
	
	var cn = "\\u"+tar.getAttribute("fa_icon");
	
	console.log("ss="+cn);
	
	//ev.dataTransfer.setData("_val",cn);
	//ev.dataTransfer.setData("_tp","icon_fa");
	
	oc.util.setDragEventData(ev,{_val:cn,_tp:"icon_fa"})
}
</script>
<body>
	<div id="win_act1"  class="oc-toolbar" style="width:100%;z-index:1" >
						<div class="titlebar" >
							<span class="i18n">Font Awesome</span><div class="collapse icon-eda-fold"></div>
						</div>
						<div class="btns">
						
						
<%
int N = 736;
//int i = 0 ;
//for(String fan:fans)
for(int i = 0;i<N;i++)
{
	String fan = Integer.toHexString(0xf000+i);
	//i++;
	//if(i>=100)
	//	break;
	%>
<div title=""  class="toolbarbtn" ><i class="fa" fa_icon="<%=fan%>" title="<%=fan%>"  draggable="true" ondragstart="drag(event)">&#x<%=fan %></i></div>
	<%
}
%>
						</div>
					</div>
</body>
</html>	
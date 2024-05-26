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
<jsp:include page="../head.jsp"></jsp:include>

<link type="text/css" href="/_js/oc/oc.css" rel="stylesheet" />
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
int N = 2304;
//int i = 0 ;
//for(String fan:fans)
for(int i = 0;i<N;i++)
{
	String fan = Integer.toHexString(0xf000+i);
	//i++;
	//if(i>=100)
	//	break;
	%>
<div title=""  class="toolbarbtn"  style="font-size: 30px"><i class="fa" fa_icon="<%=fan%>" title="<%=fan%>"  draggable="true" ondragstart="drag(event)">&#x<%=fan %></i></div>
	<%
}
%>
						</div>
					</div>
</body>
</html>	
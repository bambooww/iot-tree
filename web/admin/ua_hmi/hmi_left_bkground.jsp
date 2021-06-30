<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,
	org.iottree.core.*,
	org.iottree.core.util.*,
	org.iottree.core.gr.*,
	org.iottree.core.comp.*
	"%><%!
static final int LIST_D = 80 ;
	
static class PicItem
{
		String path = null ;
		
		int width = 800 ;
		
		int height = 600 ;
		
		int listW ;
		
		int listH ;
		
		public PicItem(String p,int w,int h)
		{
			this.path = p ;
			this.width = w ;
			this.height = h ;
			if(w>h)
			{
				listW = LIST_D ;
				listH = listW*h/w ;
			}
			else
			{
				listH = LIST_D ;
				listW = w*listH/h ;
			}
		}
		
}
static PicItem[] PICITEMS = new PicItem[]{} ;
static
{
	PICITEMS = new PicItem[]{
			new PicItem("/_iottree/pics/scene/background.png",800,600),
			new PicItem("/_iottree/pics/scene/background1.png",800,600),
			new PicItem("/_iottree/pics/scene/background2.png",800,600),
			new PicItem("/_iottree/pics/scene/background2.png",300,600),
	} ;
}
%><%
%>
<!DOCTYPE html>
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

.oc-toolbar .toolbarbtn
{
  width:90px;
  height:90px;
  margin:10px;
}


</style>
<script type="text/javascript">
function drag(ev)
{
	var tar = ev.target;
	var p = tar.getAttribute("pic_path");
	var picw = parseInt(tar.getAttribute("pic_w"));
	var pich = parseInt(tar.getAttribute("pic_h"));
	var ob = {path:p,w:picw,h:pich} ;
	oc.util.setDragEventData(ev,{_val:JSON.stringify(ob),_tp:"bkground"})
}
</script>
<body>
	<div id="win_act1"  class="oc-toolbar" style="width:100%;z-index:1" >
						<div class="titlebar" >
							<span class="i18n"></span><div class="collapse icon-eda-fold"></div>
						</div>
			<div class="btns">
<%
for(PicItem pi:PICITEMS)
{
%>
<div title=""  class="toolbarbtn" >
 <div style="width:80px;height:80px">
<img width="<%=pi.listW %>px" height="<%=pi.listH %>px" draggable="true" ondragstart="drag(event)" pic_path="<%=pi.path%>"  pic_w="<%=pi.width %>" pic_h="<%=pi.height %>" src="<%=pi.path%>"/>
</div>
 <%=pi.width %>X<%=pi.height %>
</div>
<%
}
%>

	
				</div>
			</div>
</body>
</html>	
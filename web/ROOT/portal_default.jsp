<%@ page contentType="text/html;charset=UTF-8"%><%@page
	import="org.iottree.core.*,org.iottree.core.util.*,
		org.iottree.core.util.web.*,org.iottree.portal.*,
		org.w3c.dom.*,java.util.*,org.iottree.core.util.xmldata.*"%><%
		String prjid = request.getParameter("prjid") ;
		String nf_id = request.getParameter("nf_id") ;
		
		LoginUtil.SessionItem si = LoginUtil.getUserLoginSession(request) ;
		if(si==null)
		{
			out.print("no right") ;
			return ;
		}
		
		String user_n = si.usern ;
		String user_disn = si.disn ;

	NavFrame nf = NavFrame.getNavFrame(prjid, nf_id) ;
	if(nf==null)
	{
		out.print("no nf found") ;
		return ;
	}
	String title =nf.getSysTitle() ;
String icon =nf.getLogo() ;
if(icon==null)
	icon="" ;
String home_u = nf.getHomeUrl() ;

	//	picon = "" ;
	String nav_bk_color = Config.getConfElementRoot().getAttribute("nav_bk_color") ;
	if(Convert.isNullOrEmpty(nav_bk_color))
		nav_bk_color ="#353535" ;
	String nav_txt_color = Config.getConfElementRoot().getAttribute("nav_txt_color") ;
	if(Convert.isNullOrEmpty(nav_txt_color))
		nav_txt_color ="#fff" ;
	
	String login_page = LoginUtil.getLoginPage() ;
%><!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title><%=title %></title>
    <jsp:include page="head.jsp">
    	<jsp:param value="true" name="simple"/>
    	<jsp:param value="true" name="nav"/>
    </jsp:include>
    <script src="/_js/jquery.cookie.js"></script>  
   <link rel="stylesheet" href="/_js/selectmenu/selectmenu.css" />
	<script src="/_js/selectmenu/selectmenu.min.js"></script>
    <script src="/ent/inc/fmain.js"></script>
    <link href="/ent/inc/fmain.css" rel="stylesheet" />
<style type="text/css">
.logo_title {color:#555555;}
#nav .main-nav-text { color:<%=nav_txt_color%>;margin-top:5px;}
#nav .main-nav {height: 48px;}
#main #main-bd{width: 100%;}
.full_not {position:absolute;right:5px;top:40px;cursor:pointer;z-index:10000;font-size:12px;color:#1d89f0}
</style>
<script>
        var contentPath = "";
        $(function () {
        	init_page();
        });

        function init_page()
        {
            $("#container").height($(window).height());
            $(window).resize(function (e) {
                $("#container").height($(window).height());
            });
            
        	send_ajax("nav_left_ajax.jsp","",function (bsucc,ret)
                {
        			if(!bsucc)
        			{
        				layer.msg(ret) ;
        				return ;
        			}
                    var data = null ;
                    eval("data="+ret) ;
                    load_nav('nav_tab_list',data,[
                       { id: 'home', title: '<w:g>home</w:g>', closed: false, icon: 'fa fa fa-desktop', url: contentPath + '<%=home_u%>' }
                       ]
                   );
                });
            
            
            
            $("#HomePage").click(function () {
            	//set_tab({ id: 'home', title: '<wbt:lang>home</wbt:lang>', closed: false, icon: 'fa fa-desktop', url: contentPath + '/home.jsp' });
            	//set_tab({ id: 'home', title: '<wbt:lang>home</wbt:lang>', closed: false, icon: 'fa fa-desktop', url: '/wtlsha/map/map_main.jsp' });
            });
           
            $(window).load(function () {
                window.setTimeout(function () {
                    $('#loading_panel').fadeOut();
                }, 300);
            });
        }
        
        function set_tab_device(deviceid,tt)
        {
        	set_tab({ id: "device_setup_"+deviceid, title: "设备配置-"+tt, closed: true, icon: "fa a fa-globe", url: "/platform/dev_org/device_setup.jsp?deviceid="+deviceid});
        }
        
        function set_tab_id_tt_url(id,tt,url,ico)
        {
        	set_tab({ id: id, title: tt, closed: true, icon: ico, url: url});
        }
        
        function nav_showhide()
        {
            if ($('#side').is(":hidden"))
                $('#side').show();
            else
                $('#side').hide();
            $(window).trigger('resize');
        }
        
        function login_out()
        {
        	dlg.confirm("请确定要退出登录么?",{btn:["确定","取消"],title:"退出登录"},function ()
        	{
                    //show_loading(true, "<wbt:lang>logouting</wbt:lang>");
                    send_ajax("./login/login_ajax.jsp",{op:"logout"},function(bsucc,ret){
                    	location.href = "<%=login_page%>" ;
                    }) ;
            },{
            	btn:["Ok","Exit"]
            });
        }
    </script>
</head>

<body style="overflow: hidden;">
    <div id="loading_panel" style="cursor: progress; position: fixed; top: -50%; left: -50%; width: 200%; height: 200%; background: #fff; z-index: 10000; overflow: hidden;">
        <img0 src="css/loading.gif" style="position: absolute; top: 0; left: 0; right: 0; bottom: 0; margin: auto;" />
    </div>
    <div id="container">
        <div id="side" style="background: <%=nav_bk_color%>">
<%
if(Convert.isNotNullEmpty(icon))
{
%><img id="icon-vension" src="<%=icon %>"  width="52" height="52" alt="" style="left:15px;top:10px">
<%
}
%>
            <ul id="nav"  style="padding-top: 80px"></ul>
        </div>
        
        <div id="main" style="background:#f9f9f9">
            <div id="main-hd"  style="height:35px">
                <div class="logo_fleft">
                 <img class="logo_icon" src="" /><div class="logo_title" style="margin-top:2px"><%=title %></div>
                </div>
                <div style="float: left">
                   <%--
                    <ul id="topnav">
                    
                        <li class="list" id="HomePage" title="首页">
                            <a title="首页">
                                <span><i class="fa fa-home" style="color:#e35b5a"></i></span>
                            </a>
                        </li>
                         
                        <li class="list" id="map_main" title="地图数据层">
                            <a>
                                <span><i class="fa fa-globe" style="color:#14aae4"></i></span>
                            </a>
                        </li>
                        
                        <li class="list" id="taskchain" title="任务链">
                            <a>
                                <span><i class="fa fa-superpowers" style="color:#ffcd42"></i></span>
                            </a>
                        </li>
                        <li class="list" id="help"  title="帮助">
                            <a>
                                <span><i class=" fa fa-question-circle" style="color:#43a72d"></i></span>
                            </a>
                        </li>
                      
                    </ul>
                    --%>
                </div>
                <div style="float: right">
                    <ul id="topnav_r">
                    	
                        
                        <li class="list" id="SysSetting">
                            <a style="height:30px;width:80px;font-size:13px;vertical-align: middle;" title=""><%=user_disn %>&nbsp;&nbsp;&nbsp;<i class="fa-solid fa-angle-down"></i></a>
                        </li>
                         <%--
                        <li class="list" onclick="nav_showhide()">
                            <a><span><i class="fa fa-bell"></i></span><wbt:lang>notify</wbt:lang></a>
                        </li>
                         --%>
                        <li class="list" onclick="login_out()">
                            <a style="height:30px" title="Logout" style="vertical-align:middle;"><i class="fa fa-power-off"></i></a>
                        </li>
                    </ul>
                </div>
            </div>
            <div id="main-bd" style="top:0px">
                <div id="nav_tab_list">
                </div>
                <div class="contextmenu" >
                    <ul>
                        <li onclick="$.removeTab('reloadCurrent')">reload current</li>
                        <li onclick="$.removeTab('closeCurrent')">close current</li>
                        <li onclick="$.removeTab('closeAll')">close all</li>
                        <li onclick="$.removeTab('closeOther')">close others</li>
                        <div class='m-split'></div>
                        <li>退出</li>
                    </ul>
                </div>
            </div>
        </div>
    </div>

    <div id="loading_bg" class="loading_background" style="display: none;"></div>
    <div id="loading_m">
        Loading...
    </div>
    <div onclick="full_or_not()" class="full_not"><i class="fa-solid fa-expand fa-2x"></i></div>
</body>
<script type="text/javascript">

var cur_lang="cn";
function chg_psw()
{
	alert("chg psw");
}

function chg_lang(ln,tt)
{
	if(cur_lang==ln)
		return ;
	
	dlg.confirm("Change Language ["+tt+"] will refresh window?",{btn:["Sure","Cancel"],title:"Warn"},function ()
    {
		send_ajax('/system/user/login_lang_ajax.jsp',"lang="+ln,function(bsucc,ret){
			document.location.href=document.location.href;
		});
    });
}

var langs = ['cn'];
var lang_menu = [

	{content:'<i class="fa-solid fa-globe"></i> <wbt:lang>language</wbt:lang>',header: true},
];

for(var lang of langs)
{
	var tmps = "chg_lang(\""+lang.n+"\")"
	lang_menu.push({content:lang.t+" "+((lang.n==cur_lang)?"*":""),lang:lang.n,langt:lang.t,callback:function(){chg_lang(this.lang,this.langt)}});
}
lang_menu.push({content:'<i class="fa-solid fa-user-large"></i> <wbt:lang>user</wbt:lang>',header: true})
lang_menu.push({content:'<wbt:lang>chg_psw</wbt:lang>',callback:chg_psw});


$('#SysSetting').click(function(){
	$(this).selectMenu({
		//title : 'Add  ',
		regular : true,
		data : lang_menu,
		position: { my : "left-100 center", at: "left-100 top",collision :"fit" }
	});
});

function toggle_if(ifname)
{
	const iframe = document.getElementById('tabs_iframe_'+ifname);

	if (iframe.style.position === 'fixed') {
	    // Restore original size
	    iframe.style.position = '';
	    iframe.style.width = '100%';
	    iframe.style.height = '100%';
	    //button.textContent = 'Toggle Fullscreen';
	} else {
	    // Make iframe full screen
	    iframe.style.position = 'fixed';
	    iframe.style.width = '100vw';
	    iframe.style.height = '100vh';
	    iframe.style.top = '0';
	    iframe.style.left = '0';
	    //button.textContent = 'Restore';
	}
}

function full_or_not()
{
	let cur_ifn = get_tab_frame_id();
	if(!cur_ifn || cur_ifn.indexOf("tabs_iframe_")!=0) return ;
	let n = cur_ifn.substring("tabs_iframe_".length) ;
	toggle_if(n);
}
</script>
</html>

<%@ page contentType="text/html;charset=UTF-8"%><%@ page import="java.util.*,
	java.io.*,org.json.*,
	org.iottree.core.*,
	org.iottree.core.basic.*,
	org.iottree.core.task.*,
	org.iottree.core.util.*,
	org.iottree.core.dict.*,
	org.iottree.core.comp.*,org.iottree.core.util.jt.*,
	org.iottree.core.msgnet.*
	"%><%!
	static JSONObject opened_jo = new JSONObject().put("opened", true);
	
	static JSONObject transAllApiTree(boolean show_only_used,String sel_id)
	{
		
		JSONObject ret = new JSONObject() ;
		ret.put("text","<i class='fa-solid fa-desktop'></i> Local Server").put("id","_").put("state",opened_jo).put("_tp","server") ;
		JSONArray prjs = new JSONArray() ;
		for(UAPrj prj:UAManager.getInstance().listPrjs())
		{
			List<MNNet> nets = prj.listMNNetsAll();
			JSONArray net_jarr = new JSONArray() ;
			for(MNNet net:nets)
			{
				Map<String,MNBase> nodes = net.getNamedItemsAll();
				JSONArray node_jarr =new JSONArray() ;
				for(MNBase node:nodes.values())
				{
					JSONArray api_jarr = new JSONArray() ;
					
					Map<String,MNBase.OuterApi> apis = node.listOuterApiAll() ;
					if(show_only_used)
						apis = node.getUsingOuterApis() ;
					boolean b_open_api = false;
					for(MNBase.OuterApi api: apis.values())
					{
						String api_uid = api.getApiUID() ;
						JSONObject apio = new JSONObject().put("text","<span title='api-"+api.getName()+"'><i class='fa-solid fa-circle' style='color:#3f9a62'></i> "+api.getTitle()+"</span>")
								.put("id",api_uid).put("api_n",api.getName()).put("node_n",node.getName()).put("prj_n",prj.getName())
								.put("net_n",net.getName()).put("_tp","api") ;
						api_jarr.put(apio) ;
						
						b_open_api = api_uid.equals(sel_id) ;
					}
					if(api_jarr.length()<=0)
						continue ;
					
					String node_uid = prj.getName()+"."+net.getName()+"."+node.getName();
					JSONObject nodeo = new JSONObject().put("text","<span title='node-"+node.getName()+":"+node.getTPTitle()+"'><i class='fa-solid fa-tablet-screen-button fa-lg fa-rotate-90'></i> "+node.getTitle()+"</span>")
							.put("id",node_uid).put("node_n",node.getName()).put("prj_n",prj.getName())
							.put("net_n",net.getName()).put("_tp","node")
							.put("children",api_jarr);
					if(b_open_api || node_uid.equals(sel_id))
						nodeo.put("state",opened_jo) ;
					node_jarr.put(nodeo) ;
				}
				
				if(node_jarr.length()<=0)
					continue ;
				
				String net_uid = prj.getName()+"."+net.getName();
				JSONObject neto = new JSONObject().put("text","<span title='Msg Net-"+net.getName()+"'><i class='fa fa-code-fork fa-lg fa-rotate-90'></i> "+net.getTitle()+"</span>")
						.put("id",net_uid).put("net_n",net.getName()).put("prj_n",prj.getName())
						.put("children",node_jarr) .put("state",opened_jo).put("_tp","net");
				
				net_jarr.put(neto) ;
			}
			
			if(net_jarr.length()<=0)
				continue ;
			
			JSONObject prjo = new JSONObject().put("text","<span title='project-"+prj.getName()+"'><i class='fa-solid fa-sitemap'></i> "+prj.getTitle()+"</span>")
					.put("id",prj.getName()).put("prj_n",prj.getName()).put("_tp","prj")
					.put("children",net_jarr).put("state",opened_jo) ;
			prjs.put(prjo) ;
		}
		
		if(prjs.length()>0)
			ret.put("children",prjs) ;
		return ret ;
	}
	
	static String getInputJOSample(String prj_n,String net_n,String node_n,String api_n)
	{
		return "" ;
	}
	
	%><%@ taglib uri="wb_tag" prefix="w"%><%
    //path is null "" = server outer api
	//prj_n  prj outer api
	//prj_n.net_n msg net outer api
	//prj_n.net_n.node_n node outer api
	//prj_n.net_n.node_n.api_n 
	String path = request.getParameter("path") ;
	if(Convert.isNullOrEmpty(path))
		path = request.getParameter("uid") ;
	if(Convert.isNullOrEmpty(path))
		path = "" ;
	List<String> ss = null ;
	ss = Convert.splitStrWith(path, "./|") ;
	if(ss!=null&&ss.size()>0)
		path = Convert.combineStrWith(ss, ".") ;
	String prj_n = null ;
	String net_n = null ;
	String node_n = null ;
	String api_n = null ;
	
	UAPrj prj = null ;
	MNNet net = null ;
	MNBase node = null ;
	MNBase.OuterApi api = null ;
	int ssn = 0 ;
	String uri = "/"+MNBase.OuterApi.MN_OUTER_API_PRE ; 
	if(ss!=null&&(ssn = ss.size())>0)
	{
		prj_n = ss.get(0) ;
		uri += "/"+prj_n ;
		prj = UAManager.getInstance().getPrjByName(prj_n) ;
		if(prj==null){out.print("no prj found") ;return ;}
		
		if(ssn>1)
		{
			net_n = ss.get(1) ;
			uri += "/"+net_n ;
			net = prj.getMNManager().getNetByName(net_n) ;
			if(net==null){out.print("no net found") ;return ;}
		}
		
		if(ssn>2)
		{
			node_n = ss.get(2) ;
			uri += "/"+node_n ;
			node = net.getItemByName(node_n);
			if(node==null){out.print("no node found") ;return ;}
		}
		if(ssn>3)
		{
			api_n = ss.get(3) ;
			uri += "/"+api_n ;
			api = node.getUsingOuterApi(api_n) ;
			if(api==null){out.print("no api found") ;return ;}
		}
	}
	
	ArrayList<MNBase.OuterApi> apis = new ArrayList<>() ;
	
	boolean b_dlg = "true".equals(request.getParameter("dlg")) ;
	List<String> sub_api_uids = Convert.splitStrWith(request.getParameter("sub_api_uids"), ",") ;
	if(sub_api_uids==null)
		sub_api_uids = Arrays.asList() ;
	JSONArray sub_api_uids_jarr=new JSONArray(sub_api_uids) ;
%>
<html>
<head>
<title></title>
<jsp:include page="/head.jsp">
	<jsp:param value="true" name="simple"/>
	<jsp:param value="true" name="tree"/>
</jsp:include>
<style>
.left
{
	position: absolute;
	left:0px;
	width:30%;
	bottom: 0px;
	top:0px;overflow-y:auto;
	border:1px solid #ececev;
}
.right
{
	position: absolute;
	right:0px;
	width:70%;
	bottom: 0px;
	top:0px;
	border:1px solid #ececec;
	display: flex;
    flex-direction: column;
}
.layui-elem-quote {
    margin-bottom: 1px;
    padding: 8px;font-weight:bold;
    line-height: 15px;
    border-left: 5px solid #009688;
    border-radius: 0 2px 2px 0;
    background-color: #f2f2f2;
}

.save_btn
{
	position: absolute;
	right:5px;
	top:5px;
	color:#27ba7d;
}

.in_title {position: absolute;left:2px;top:50px;border:1px solid #ccc;background-color: #003a36;color:#00ffe2;cursor: pointer;}
.in_title:hover ~ .child  {display: block;}
.layui-form-item .layui-form-checkbox[lay-skin=primary] {
    margin-top:0px;
}
.layui-form-item {
    margin-bottom: 3px;
    margin-top: 3px;
}
.abc {word-break: break-all;white-space: normal;}
#tree {width:100%;}

.jstree-default .jstree-themeicon {
    background-image: none !important;
    width: 0 !important;   /* 去除占位宽度 */
    padding: 0 !important;
}
.code-block {
    white-space: pre-wrap;
    font-family: 'Courier New', Consolas, 'Source Code Pro', monospace;
    
    background-color: #f4f4f4;
    padding: 16px 20px;
    border-radius: 8px;
    border: 1px solid #e1e4e8;
    
    font-size: 14px;
    max-height:400px;
    overflow-y: auto;
}

.layui-table td, .layui-table th {
    padding: 2px 5px;
    min-height: 20px;
    line-height: 20px;
    font-size: 14px;}
textarea {width:100%;height:42%;}
</style>
</head>
<%--
<script type="text/json" id="sample_server">
// client post to server JSON format 
[
	{
		prj_n:"prj_name_x",
		net_n:"msg_net_name",
		node_n:"msg_node_name",
		api_n:"msgnode_api_n",
		api_in:{} // Call API specific parameters
	},
	... // Other API call input
]

//server response to client JSON format 
[
	{
		prj_n:"prj_name_x",
		net_n:"msg_net_name",
		node_n:"msg_node_name",
		api_n:"msgnode_api_n",
		api_out:{} // Call API specific return
	},
	...  // Other API return
]
</script>
<script type="text/json" id="sample_prj">
// client post to server JSON format 
[
	{
		net_n:"msg_net_name",
		node_n:"msg_node_name",
		api_n:"msgnode_api_n",
		api_in:{} // Call API specific parameters
	},
	...  // Other API call input
]

//server response to client JSON format 
[
	{
		prj_n:"prj_name_x",
		net_n:"msg_net_name",
		node_n:"msg_node_name",
		api_n:"msgnode_api_n",
		api_out:{} // Call API specific return
	},
	... // Other API return
]
</script>
<script type="text/json" id="sample_net">
// client post to server JSON format 
[
	{
		node_n:"msg_node_name",
		api_n:"msgnode_api_n",
		api_in:{} // Call API specific parameters
	},
	...  // Other API call input
]

//server response to client JSON format 
[
	{
		prj_n:"prj_name_x",
		net_n:"msg_net_name",
		node_n:"msg_node_name",
		api_n:"msgnode_api_n",
		api_out:{} // Call API specific return
	},
	...  //Other API return
]
</script>
<script type="text/json" id="sample_node">
// client post to server JSON format 
[
	{
		api_n:"msgnode_api_n",
		api_in:{} // Call API specific parameters
	},
	... // Other API call input
]

//server response to client JSON format 
[
	{
		prj_n:"prj_name_x",
		net_n:"msg_net_name",
		node_n:"msg_node_name",
		api_n:"msgnode_api_n",
		api_out:{} // Call API specific return
	},
	... // Other API return
]
</script>
--%>
<body>
<div class="left">
	<div id="tree"></div>
</div>
<div class="right">
	<blockquote class="layui-elem-quote">
  <span style="color:green;"><span class="pre"></span><%=MNBase.OuterApi.MN_OUTER_API_PRE%><span id="cur_path"></span></span>
<%--
<br><%
if(prj!=null)
{
%><w:g>prj</w:g>:<%=prj_n %>
<%}
if(net!=null)
{
%> - <w:g>msgnet</w:g>:<%=net_n %>
<%
}
if(node!=null)
{
%>
 - <w:g>node</w:g>:<%=node_n %>
<%
}
if(api!=null)
{
%>
 - Api:<%=api_n %>
<%
}
%>

--%>
</blockquote>
<div  style="border:1px solid #ccc;overflow-y: auto;flex-shrink: 0;">
<table class="layui-table" id="tb_sub_api">
	<thead>
		<tr>
			<td></td><td>sub apis</td><td><w:g>title</w:g></td>
		</tr>
	</thead>
	<tbody id="sub_apis" >
	</tbody>
</table>
</div>
<div id="call_in_out" code="code-block"  style="overflow-y:auto;flex:1">
	
</div>

</div>

 </body>
<script>
dlg.resize_to(1000,800) ;
var dlg = <%=b_dlg%> ;
var path = "<%=path%>" ;
var sub_api_uids = <%=sub_api_uids_jarr%> ;
let pre = location.protocol+"//"+location.host ;

function update_pre()
{
	$(".pre").html(pre);
}
update_pre()
var tree_data = <%=transAllApiTree(true,path)%>;
function tree_init()
{
	$.jstree.destroy();
	this.jsTree = $('#tree').jstree(
				{
					'core' : {
						'data' : tree_data,
						'themes' : {
							//'responsive' : false,
							'variant' : 'small',
							'stripes' : true
						}
					},
					
					'unique' : {
						'duplicate' : function (name, counter) {
							return name + ' ' + counter;
						}
					},
					'plugins' : ['types','unique'] //'state',','contextmenu' 'dnd',
				}
		);
		this.jsTree.on('activate_node.jstree',(e,data)=>{
			on_tree_node_sel(data.node.original)
		}).on('ready.jstree', function() {
	        //$(this).jstree('open_all');
	        if(path)
	        {
	        	$(this).jstree('select_node', path);
	        	$(this).jstree('activate_node',path);
	        }
	    });
		
}

var cur_nd = null ;

function on_tree_node_sel(nd)
{
	if(!nd)
		return ;
	console.log(nd);
	cur_nd = nd ;
	let path_url =calc_uri_path(nd);
	$("#cur_path").html(path_url) ;
	$("#call_in_out").html("");
	
	$("#sub_apis").html("") ;
	let tp = nd._tp ;
	if(tp=='api')
	{
		//send_ajax("/mn_outer_api_ajax.jsp",{op:"api_detail",...nd},(bsucc,ret)=>{
		//	if(!bsucc) {dlg.msg(ret);return}
		//	$("#call_in_out").html("<pre><code>"+ret+"</code></pre>") ;
		//	update_pre();
		//})
		update_api_sample(nd)
		return ;
	}
	else
	{
		//let ss = $("#sample_"+nd._tp).html() ;
		//$("#call_in_out").html("<pre><code>"+ss+"</code></pre>") ;
		update_sub_apis(nd)
		//update_pre()
	}
}

function calc_uri_path(nd)
{
	let path_url = ""
	if(nd.prj_n) path_url+= "/"+nd.prj_n ;
	if(nd.net_n) path_url+= "/"+nd.net_n ;
	if(nd.node_n) path_url+= "/"+nd.node_n ;
	if(nd.api_n) path_url+= "/"+nd.api_n ;
	return path_url ;
}

function get_cur_uri_path()
{
	if(!cur_nd) return "" ;
	return calc_uri_path(cur_nd);
}

function update_sub_apis(nd)
{
	send_ajax("/mn_outer_api_ajax.jsp",{op:"sub_apis",...nd},(bsucc,ret)=>{
		if(!bsucc || ret.indexOf("[")!=0) {dlg.msg(ret);return}
		$("#tb_sub_api").css("display","") ;
		let oas = null;
		eval("oas="+ret) ;
		let ss ="" ;
		for(let oa of oas)
		{
			ss += `<tr>
			<td><input type="checkbox" class='chk_sub_api' uid='\${oa.uid}' onclick="update_cur_sample()"/></td><td>\${oa.sub_path}</td>
			<td>\${oa.api_t||""}</td>
				</tr>`
		}
		$("#sub_apis").html(ss) ;
		
		if(sub_api_uids&&sub_api_uids.length>0)
		{
			$(".chk_sub_api").each(function(){
				let chk = $(this) ;
				let uid = chk.attr("uid");
				if(sub_api_uids.indexOf(uid)>=0)
					chk.prop("checked",true) ;
			})
			sub_api_uids=[];
			update_cur_sample()
		}
		//update_pre();
	})
}

function get_cur_sub_uids()
{
	let uids = [];
	$(".chk_sub_api").each(function(){
		let ob = $(this) ;
		if(ob.prop("checked"))
		{
			let uid = ob.attr("uid") ;
			uids.push(uid) ;
		}
	})
	return uids ;
}

function update_cur_sample()
{
	if(!cur_nd)
		return ;
	let uids = get_cur_sub_uids();
	
	send_ajax("/mn_outer_api_ajax.jsp",{op:"sub_api_sample",...cur_nd,sub_uids:uids.join(',')},(bsucc,ret)=>{
		if(!bsucc) {dlg.msg(ret);return}
		$("#tb_sub_api").css("display","") ;
		$("#call_in_out").html("<pre><code>"+ret+"</code></pre>") ;
	})
}

function update_api_sample(nd)
{
	send_ajax("/mn_outer_api_ajax.jsp",{op:"api_sample",...nd},(bsucc,ret)=>{
		if(!bsucc) {dlg.msg(ret);return}
		$("#tb_sub_api").css("display","none") ;
		$("#call_in_out").html("<pre><code>"+ret+"</code></pre>") ;
	})
}
tree_init()
</script>
</html>
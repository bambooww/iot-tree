<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,org.json.*,
	org.iottree.core.*,
	org.iottree.core.basic.*,
	org.iottree.core.task.*,
	org.iottree.core.util.*,
	org.iottree.core.dict.*,
	org.iottree.core.comp.*,
	org.iottree.core.msgnet.*,org.iottree.core.msgnet.nodes.*,
	org.iottree.core.msgnet.util.*
	"%><%@ taglib uri="wb_tag" prefix="w"%><%
	String prjid = request.getParameter("container_id");
	String netid = request.getParameter("netid") ;
	String itemid = request.getParameter("itemid") ;
	
	//UAPrj prj = UAManager.getInstance().getPrjById(prjid) ;
	//if(prj==null)
	//{
	//	out.print("no prj found") ;
	//	return ;
	//}
	MNManager mnm= MNManager.getInstanceByContainerId(prjid);//.getInstance(prj) ;
	MNNet net = mnm.getNetById(netid) ;
	if(net==null)
	{
		out.print("no net found") ;
		return ;
	}
	MNBase item =net.getItemById(itemid) ;
	if(item==null)
	{
		out.print("no item found") ;
		return ;
	}
	
	NM_JsFunc node = (NM_JsFunc)item ;
	
	
	MNMsg msg = null;
	if(item instanceof MNNode)
		msg = ((MNNode)item).RT_getLastMsgIn() ;
	if(msg==null)
		msg = new MNMsg() ;
%>
<script>
        var require = {
            paths: {
                'vs': '/_js/monaco/vs'
            }
        };
        
</script>
<script src="/_js/monaco/vs/loader.js"></script>
<script src="/_js/monaco/vs/editor/editor.main.nls.js"></script>
<script src="/_js/monaco/vs/editor/editor.main.js"></script>
<style>
.help_tree
{
	position:absolute;
	left:5px;top:0px;
	width:300px;
	height:500px;
	overflow: auto;
	text-align: left;
}
.help_lb
{
	position:absolute;
	left:5px;top:400px;
	width:300px;
	height:200px;
	overflow: auto;
	text-align: left;
}
.layui-tab-title li
{
	line-height0:30px;
}

.pm
{
color:#2f2f2f;
}

.out_tt {width:150px;}
.out_tt .idx {width:20px;text-align: right;display:inline-block;}
.out_tt .inp {width:120px;}
</style>
<div class="layui-form-item" style="position: absolute;right:10px;z-index:1000;">
    <label class="layui-form-label">Outputs:</label>
    <div class="layui-input-inline" style="width:70px;">
    	<input type="number" class="layui-input" id="out_num" value="1" min="1" onchange="refresh_out_tts(true)"/>
    </div>
  </div>
  <div style="position: absolute;right:5px;top:100px;z-index:1000;width:150px;border:1px solid #ccc;">
  	<div class="out_tt">&nbsp;&nbsp;Output Titles</div>
  	<div id="out_tts">
    
    </div>
  </div>
  <div class="layui-form-item">
    <div class="layui-form-label" style="width:300px;text-align: left;">
    <div  class="help_tree">
    	&nbsp;&nbsp;<i class="fa fa-question" aria-hidden="true" /></i> Help
    	<div id="help_tree"  class="tree">
		    <ul>
		      <li title="last in message">topic:str</li>
		      <li title="last in message">heads:{}</li>
		      <li title="last in message">payload:any</li>
		      <li>node
<%
	item.CXT_PK__renderTree(out) ;
%>
			</li>
		      <li>flow
<%
	net.CXT_PK__renderTree(out) ;
%>
		      </li>
		    </ul>
  </div>
    </div>
    <div class="help_lb" id="help_lb">
    </div>
    </div>
    <div class="layui-input-inline" style="width:720px;border:0px solid;">
     
	    <div class="layui-tab" lay-filter="js_tabs" style="width:100%;margin:0px;">
  <ul class="layui-tab-title" style0="height:30px;">
    <li ><w:g>init</w:g>JS</li>
    <li class="layui-this"><w:g>run</w:g>JS</li>
    <li><w:g>end</w:g>JS</li>
    
  </ul>
  <div class="layui-tab-content" style="padding:0px;">
    <div class="layui-tab-item" id="tab_init_js">
    	
		<div id='init_js'  style="overflow: scroll;width:100%;height:490px;border:0px solid #e6e6e6;margin-top:2px;" ></div>
		
	</div>
    <div class="layui-tab-item  layui-show" >
    	<span style="color:#229ecc">function</span> <span style="color:#ffcc9e;font-weight: bold;">on_msg_in</span>(<span class="pm" title="Msg From">from_node</span>,<span class="pm" title="In Msg's Topic">topic</span>,<span class="pm" title="In Msg's Heads">heads</span>,<span class="pm" title="In Msg's Payload">payload</span>,<span class="pm" title="this runing node">node</span>,<span class="pm" title="This Flow/Net">flow</span>) <span style="color:#901f1f">{</span>
		<div id='run_js'  style="overflow: scroll;width:100%;height:490px;border:0px solid #e6e6e6;margin-top:2px;"></div>
		<span style="color:#901f1f">}</span>
	</div>
    <div class="layui-tab-item">
    	<span style="color:#229ecc">function</span> <span style="color:#ffcc9e;font-weight: bold;">on_end</span>(<span class="pm" title="this runing node">node</span>,<span class="pm" title="This Flow/Net">flow</span>) <span style="color:#901f1f">{</span>
		<div id='end_js'  style="overflow: scroll;width:100%;height:490px;border:0px solid #e6e6e6;margin-top:2px;"></div>
		<span style="color:#901f1f">}</span>
    </div>
  
  </div>
</div>

    </div>
    
  </div>
  
<script>
var jsfunc_prjid="<%=prjid%>";
var jsfunc_netid="<%=netid%>";
var jsfunc_itemid="<%=itemid%>";

var jsTree = $('#help_tree').jstree(
		{
			'core' : {
				'data' : {
					'url' :`./mn_help_ajax.jsp?op=sub_json&container_id=\${container_id}&netid=\${netid}&itemid=\${itemid}`,
					"dataType" : "json",
					"data":function(node){
                        return {"id" : node.id};
                    }
				},
				
				'themes' : {
					//'responsive' : false,
					'variant' : 'small',
					'stripes' : true
				}
			},
			'types' : {
				'default' : { 'icon' : 'folder' },
				'file' : { 'valid_children' : [], 'icon' : 'file' }
			},
			'unique' : {
				'duplicate' : function (name, counter) {
					return name + ' ' + counter;
				}
			},
			'plugins' : ['types','unique'] //'state',','contextmenu' 'dnd',
		}
);
jsTree.on('activate_node.jstree',(e,data)=>{
	on_tree_node_sel(data.node.original)
})

function on_tree_node_sel(n)
{
	let id = n.id ;
	send_ajax("mn_help_ajax.jsp",{op:"sub_detail",container_id:container_id,netid:netid,itemid:itemid,id:id},(bsucc,ret)=>{
		$("#help_lb").html(ret) ;
	}) ;
}

function open_help_ob(cn)
{
	dlg.open_win("./mn_help_ob.jsp?cn="+cn,
			{title:"JS Object Helper",w:'400px',h:'300px'},
			[],[]);
}

var init_editor = null;
var run_editor = null;
var end_editor = null;

var init_js = "" ;
var run_js = "" ;
var end_js = "" ;

function on_init_show()
{
	if(init_editor)
		return ;
	
	init_editor = monaco.editor.create(document.getElementById('init_js'), 
			{model: null,minimap: {enabled: false}}
	);
	const init_model = monaco.editor.createModel(init_js,"javascript");
	 init_editor.setModel(init_model);
}
function on_end_show()
{
	if(end_editor)
		return ;
	
	end_editor = monaco.editor.create(document.getElementById('end_js'), 
			{model: null,minimap: {enabled: false}});
	const end_model = monaco.editor.createModel(end_js,"javascript");
	 end_editor.setModel(end_model);
}

function on_after_pm_show(form)
{
	element.on("tab(js_tabs)",function(data){
		if(data.index==0)
			on_init_show() ;
		else if(data.index==2)
			on_end_show() ;
	}) ;
	form.render() ;
	js_edit_init();
}

function js_edit_init()
{
	require(['vs/editor/editor.main'], function () {
        
		
		run_editor = monaco.editor.create(document.getElementById('run_js'), 
					{model: null,minimap: {enabled: false}});
        const run_model = monaco.editor.createModel(run_js,"javascript");
        run_editor.setModel(run_model);
    })

}


var DEF_INIT_JS = "// add you init vars or functions here\r\n// it will run once before msg income \r\n";
var DEF_RUN_JS = "// add you code when msg in\r\n //you can use vars or functions defined in init JS \r\n\r\n\r\n\r\n" 
	+"\r\n\r\n\r\n\r\n//return payload for out msg ,null will has no out msg. [] for idx output\r\n"
	+" return {} ;";
	
var DEF_END_JS = "// add you end js code here \r\n// it will run once when flow is stopping\r\n";

var out_tts = [] ;

function read_out_tts()
{
	let rets=[] ;
	$(".out_tt").find(".inp").each(function(){
		let ob = $(this);
		let tt = ob.val()||"" ;
		rets.push(tt) ;
	});
	return rets;
}

function refresh_out_tts(b_read)
{
	if(b_read)
		out_tts = read_out_tts() ;
	
	let tmps = "";
	let out_num = get_input_val("out_num",true,1) ;
	if(out_num<=0) out_num = 1 ;
	for(let i = 0 ; i < out_num ; i ++)
	{
		let tt = "";
		if(out_tts&&out_tts.length>i)
			tt = out_tts[i]||"" ;
		tmps += `<div class="out_tt"><span class="idx">\${i}</span>&nbsp;<input type="text" class="inp" value="\${tt}" /></div>`
	}
	$("#out_tts").html(tmps) ;
}

function get_pm_jo()
{
	let out_num = get_input_val("out_num",true,1) ;
	let out_tts = read_out_tts() ;
	let i_js = init_editor?init_editor.getValue():init_js; 
	let m_js = run_editor.getValue();
	let e_js = end_editor?end_editor.getValue():end_js;
	return {out_num:out_num,out_tts:out_tts,on_init_js:i_js,on_msg_js:m_js,on_end_js:e_js} ;
}

function set_pm_jo(jo)
{
	$("#out_num").val(jo.out_num) ;
	out_tts = jo.out_tts||[] ;
	refresh_out_tts(false);
	if(init_editor)
	{
		//init_editor.setValue(jo.on_init_js||DEF_INIT_JS);
	//	run_editor.setValue(jo.on_msg_js||DEF_RUN_JS);
		//end_editor.setValue(jo.on_end_js||DEF_END_JS);
	}
	else
	{
		
	}
	init_js = jo.on_init_js||DEF_INIT_JS;
	run_js = jo.on_msg_js||DEF_RUN_JS;
	end_js = jo.on_end_js||DEF_END_JS;
}

function get_pm_size()
{
	return {w:1200,h:550} ;
}



</script>
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,org.json.*,
	org.iottree.core.*,
	org.iottree.core.basic.*,
	org.iottree.core.task.*,
	org.iottree.core.util.*,
	org.iottree.core.dict.*,
	org.iottree.core.comp.*,
	org.iottree.core.msgnet.*
	"%><%@ taglib uri="wb_tag" prefix="wbt"%><%
	if(!Convert.checkReqEmpty(request, out, "prjid","netid","nodeid"))
			return ;
	String prjid = request.getParameter("prjid");
	String netid = request.getParameter("netid") ;
	String nodeid = request.getParameter("nodeid") ;
	
	UAPrj prj = UAManager.getInstance().getPrjById(prjid) ;
	if(prj==null)
	{
		out.print("no prj found") ;
		return ;
	}
	MNManager mnm= MNManager.getInstance(prj) ;
	MNNet net = mnm.getNetById(netid) ;
	if(net==null)
	{
		out.print("no net found") ;
		return ;
	}
	MNNode node = net.getNodeById(nodeid) ;
	if(node==null)
	{
		out.print("no node found") ;
		return ;
	}
	
	String tp = node.getNodeTP() ;
	String title = node.getTitle() ;

	String pm_url = "./nodes/"+tp+"_pm.jsp" ;
	JSONObject jo = node.getParamJO() ;
	String jstr = "{}" ;
	if(jo!=null)
		jstr = jo.toString() ;
%>
<html>
<head>
<title></title>
<jsp:include page="../head.jsp">
	<jsp:param value="true" name="simple"/>
</jsp:include>
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
<script type="text/javascript">

var pm_url="<%=pm_url%>" ;
var pm_jo = <%=jstr%> ;

var form ;

function init_pm0()
{
	send_ajax(pm_url,{},(bsucc,ret)=>{
		$("#pm_cont").html(ret) ;
	}) ;

}

function on_init_pm_ok()
{// called by sub pm js
	if(get_pm_size!=undefined && get_pm_size)
	{
		let wh = get_pm_size() ;
		dlg.resize_to(wh.w,wh.h+100) ;
	}
	
	if(set_pm_jo)
	{
		set_pm_jo(pm_jo) ;
	}
	
	 form.render();
}
	
	
function win_close()
{
	dlg.close(0);
}

function get_input_val(id,defv,bnum)
{
	var n = $('#'+id).val();
	if(n==null||n=='')
	{
		return defv ;
	}
	if(bnum)
		return parseInt(n);
	return n;
}

function do_submit(cb)
{
	if(!get_pm_jo)
	{
		cb(false,"no pm edit items found") ;
		return ;
	}
	
	var tt = $('#title').val();
	
	let pmjo = get_pm_jo();
	if(typeof(pmjo) == "string")
	{
		cb(false,pmjo) ;
		return ;
	}
	cb(true,{title:tt,pm_jo:pmjo});
}
</script>
<style>
</style>
</head>

<body>
<div id='js_txt'  style="overflow: scroll;width:100%;height:550px;border:1px solid;"></div>
</body>

<script type="text/javascript">

var editor = null;

function js_edit_init(cb)
{
	require(['vs/editor/editor.main'], function () {
        // "javascript"
        editor = monaco.editor.create(document.getElementById('script_test'), {
            model: null,
            minimap: {
                enabled: false
            }
        });
        monaco.languages.registerCompletionItemProvider('javascript', {
            provideCompletionItems: function(model, position) {
                // Define the completion items
                var suggestions = [
                    {
                        label: 'console.log',
                        kind: monaco.languages.CompletionItemKind.Function,
                        insertText: 'console.log(\${1});',
                        insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
                        documentation: 'Log output to console'
                    },
                    {
                        label: 'function',
                        kind: monaco.languages.CompletionItemKind.Keyword,
                        insertText: 'function \${1:fname}(\${2:args}) {\n\t$0\n}',
                        insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
                        documentation: 'Define a function'
                    }
                ];
                return { suggestions: suggestions };
            }
        });
        const newModel = monaco.editor.createModel(
        		"",
                "javascript"
        );
        editor.setModel(newModel);
        
        //on_init_pm_ok() ;
        if(cb)
        	cb();
    })

}

js_edit_init()

layui.use('form', function(){
	  form = layui.form;
	  
	  init_pm(on_init_pm_ok) ;
	 
});
</script>

</html>                                                                                                                                                                                                                            
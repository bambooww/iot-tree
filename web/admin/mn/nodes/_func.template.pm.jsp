<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,org.json.*,
	org.iottree.core.*,
	org.iottree.core.basic.*,
	org.iottree.core.task.*,
	org.iottree.core.util.*,
	org.iottree.core.dict.*,
	org.iottree.core.comp.*,
	org.iottree.core.msgnet.*,
	org.iottree.core.msgnet.nodes.*,
	org.iottree.core.msgnet.util.*
	"%><%@ taglib uri="wb_tag" prefix="w"%><%
	String container_id = request.getParameter("container_id");
	String netid = request.getParameter("netid") ;
	String itemid = request.getParameter("itemid") ;
	
	MNManager mnm = MNManager.getInstanceByContainerId(container_id) ;
	MNNet net = mnm.getNetById(netid) ;
	if(net==null)
	{
		out.print("no net found") ;
		return ;
	}
	
	MNNode node = net.getNodeById(itemid) ;
	if(node==null)
	{
		out.print("no node item found") ;
		return ;
	}
	
	
	MNMsg msg = node.RT_getLastMsgIn() ;
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
.layui-form-label
{
	width:150px;
}
.help_tree
{
	position:absolute;
	left:5px;top:50px;
	width:150px;
	height:400px;
	overflow: auto;
	text-align: left;
}
</style>

  <div class="layui-form-item">
    <div class="layui-form-label"><w:g>template</w:g> <w:g>highlight</w:g>
    	<select id="highlight" lay-skin="primary" lay-filter="highlight" onchange="update_highlight()" lay-ignore style="border-color:#e6e6e6">
    		<option value="mustache">mustache</option>
    		<option value="json">JSON</option>
    		<option value="javascript">javascript</option>
    	</select>
    	<br>
    	<div  class="help_tree">
    	&nbsp;&nbsp;<i class="fa fa-question" aria-hidden="true" /></i> Help
    	<div id="help_tree">
		    <ul>
		       <li title="last in message">topic:str</li>
		       <li title="last in message">heads:{}</li>
		      <li title="last in message">payload
<%
	msg.CXT_PK__renderPayloadTree(out) ;
%>
		      </li>
		      <li>node
<%
	node.CXT_PK__renderTree(out) ;
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
    </div>
    <div class="layui-input-inline" style="width:500px;">
    
    	<div id='temp'  style="overflow: scroll;width:100%;height:480px;border:1px solid #e6e6e6;"></div>
    	
    </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label"><w:g>out_fmt</w:g></label>
    <div class="layui-input-inline" style="width:250px;">
    	<select id="out_fmt" lay-skin="primary" lay-filter="out_fmt"  >
<%
for(NM_Template.OutFmt outf:NM_Template.OutFmt.values())
{
	String n = outf.name() ;
%>
    		<option value="<%=n%>"><%=n%></option>
<%
}
%>
    	</select>
    </div>
  </div>
<script>
var editor = null;

$("#help_tree").jstree() ;

function js_edit_init()
{
	require(['vs/editor/editor.main'], function () {
		monaco.languages.register({ id: 'mustache' });

        monaco.languages.setMonarchTokensProvider('mustache', {
            tokenizer: {
                root: [
                    [/\{\{[#\/][^\}]+\}\}/, 'keyword'],
                    [/\{\{[^\}]+\}\}/, 'variable'],
                    [/[{}]/, 'delimiter'],
                    [/[a-z_$][\w$]*/, 'identifier'],
                    [/\s+/, 'white']
                ]
            }
        });

        monaco.languages.setLanguageConfiguration('mustache', {
            brackets: [
                ['{{', '}}']
            ],
            autoClosingPairs: [
                { open: '{{', close: '}}' }
            ]
        });
        
        editor = monaco.editor.create(document.getElementById('temp'), {
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
                "mustache"
        );
        editor.setModel(newModel);
        
        on_init_pm_ok() ;
        
        
    })

}

	
function on_after_pm_show(form)
{
	  //form.on('select(highlight)', function (data) {
	//	  update_highlight();
	 // });
}

function update_highlight()
{
	if(!editor) return ;
	
	let md = $("#highlight").val() ;
	let newm = monaco.editor.createModel(editor.getValue(),md);
	editor.setModel(newm);
}


function get_pm_jo()
{
	let jstxt = editor.getValue();
	let outf = $("#out_fmt").val() ;
	return {temp:jstxt,out_fmt:outf} ;
}

function set_pm_jo(jo)
{
	if(!jo || !editor) return ;
	
	$("#out_fmt").val(jo.out_fmt||txt);
	editor.setValue(jo.temp||`{
	"dd":"{{payload}}"
}`);
}

function get_pm_size()
{
	return {w:750,h:650} ;
}

js_edit_init();

</script>
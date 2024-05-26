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
	org.iottree.core.msgnet.util.*
	"%><%@ taglib uri="wb_tag" prefix="w"%><%
	
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

<div class="layui-form-item">
    <label class="layui-form-label">Outputs:</label>
    <div class="layui-input-inline" style="width:250px;">
    	<input type="number" class="layui-input" id="out_num" value="1" min="1"/>
    </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label">On Msg JS:</label>
    <div class="layui-input-inline" style="width:550px;">
    <span style="color:#229ecc">function</span> <span style="color:#dddc8c;font-weight: bold;">func</span>(<span style="color:#2f2f2f">$node,$flow,$global,$msg</span>) <span style="color:#901f1f">{</span>
    	<div id='js_txt'  style="overflow: scroll;width:100%;height:480px;border:1px solid #e6e6e6;"></div>
    	<span style="color:#901f1f">}</span>
    </div>
  </div>
  
<script>
var editor = null;

function js_edit_init()
{
	require(['vs/editor/editor.main'], function () {
        // "javascript"
        editor = monaco.editor.create(document.getElementById('js_txt'), {
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
        
        on_init_pm_ok() ;
    })

}



function get_pm_jo()
{
	let out_num = get_input_val("out_num",true,1) ;
	let jstxt = editor.getValue();
	return {out_num:out_num,on_msg_js:jstxt} ;
}

function set_pm_jo(jo)
{
	$("#out_num").val(jo.out_num) ;
	editor.setValue(jo.on_msg_js||"\r\nreturn $msg;");
}

function get_pm_size()
{
	return {w:700,h:650} ;
}

js_edit_init();

</script>
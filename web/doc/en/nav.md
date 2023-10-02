<script src="/_js/jquery-1.12.0.min.js"></script>
<script src="/_js/bootstrap/js/bootstrap.min.js"></script>
<script type="text/javascript" src="/_js/ajax.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js?v="></script>
<link  href="/_js/bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css" >
<link  href="/_js/font4.7.0/css/font-awesome.css"  rel="stylesheet" type="text/css" >
            <link href="./inc/common.css" rel="stylesheet" type="text/css">
        <link href="./inc/index.css" rel="stylesheet" type="text/css">



### Catalogue





#### <a doc_path="en/README.md" >1 Summary</a>
#### <a doc_path="en/doc/quick_start.md" >2 Quick Start</a>






#### <a doc_path="en/doc/case/index.md" >3 Cases and best practices</a>
##### <a doc_path="en/doc/case/example_lamp_demo.md" target="main">&nbsp;&nbsp;&nbsp;3.1 Lamp Demo</a>
##### <a doc_path="en/doc/case/case_auto.md" target="main">&nbsp;&nbsp;&nbsp;3.2 Automation</a>
##### <a doc_path="en/doc/case/case_rs485_sniffer.md" target="main">&nbsp;&nbsp;&nbsp;3.3 Sniffe RS485 MODBUS bus data</a>
##### <a doc_path="en/doc/case/case_jsplugin_db.md" target="main">&nbsp;&nbsp;&nbsp;3.4 Automatically record to relational database (including JS plug-in development)</a>

##### <a doc_path="en/doc/case/case_ref_hmi_auth.md" target="main">&nbsp;&nbsp;&nbsp;3.5 Integrate HMI into your system (including permission plug-in development)</a>

##### <a doc_path="en/doc/case/case_opc_da.md" target="main">&nbsp;&nbsp;&nbsp;3.6 Access data through OPC DA Client</a>




#### <a doc_path="en/doc/quick/index.md" >4 Quick Know</a>
##### <a doc_path="cn/doc/quick/quick_know_iottree.md" target="main">&nbsp;&nbsp;&nbsp;4.1 The overall IOT-Tree Server</a>
##### <a doc_path="cn/doc/quick/quick_know_conn.md" target="main">&nbsp;&nbsp;&nbsp;4.2 Quickly understand Connector</a>
##### <a doc_path="en/doc/quick/quick_know_tree.md" target="main">&nbsp;&nbsp;&nbsp;4.3 Quickly understand the tree of IOT tree server</a>
##### <a doc_path="en/doc/quick/quick_know_ch_conn_drv.md" target="main">&nbsp;&nbsp;&nbsp;4.4 Connector-Channel-Driver</a>
##### <a doc_path="en/doc/quick/quick_know_devdef.md" target="main">&nbsp;&nbsp;&nbsp;4.5 Quickly understand device definition</a>
##### <a doc_path="en/doc/quick/quick_know_tcpserver_connector.md" target="main">&nbsp;&nbsp;&nbsp;4.6 Quickly understand tcpserver connector</a>
##### <a doc_path="en/doc/quick/quick_know_hmi.md" target="main">&nbsp;&nbsp;&nbsp;4.7 Monitoring UI,Human computer interaction(HMI)</a>
##### <a doc_path="en/doc/quick/quick_know_server.md" target="main">&nbsp;&nbsp;&nbsp;4.8 Quickly understand IOT tree server as your background support system</a>
##### <a doc_path="en/doc/conn/msg_http_url_html.md" target="main">&nbsp;&nbsp;&nbsp;4.9 HTTP Url HTML format(Reptile aggregation)Connector</a>











#### <a doc_path="en/doc/advanced/adv_plugin.md" >8 Extends Functions</a>
##### <a doc_path="en/doc/advanced/adv_plugin_jsapi.md" target="main">&nbsp;&nbsp;&nbsp;5.1 JsApi Plugin</a>
##### <a doc_path="en/doc/advanced/adv_plugin_auth.md" target="main">&nbsp;&nbsp;&nbsp;5.2 Authority plugin</a>
##### <a doc_path="en/doc/advanced/adv_ui_comp.md" target="main">&nbsp;&nbsp;&nbsp;6.1 Customize UI Components</a>
##### <a doc_path="en/doc/advanced/adv_prj_task.md" target="main">&nbsp;&nbsp;&nbsp;6.2 Custom JS script plug-in</a>
##### <a doc_path="en/doc/advanced/adv_prj_task.md" target="main">&nbsp;&nbsp;&nbsp;6.3 Define project tasks</a>
##### <a doc_path="en/doc/advanced/adv_prj_task_ctrl.md" target="main">&nbsp;&nbsp;&nbsp;6.4 Implementing control scripts using project tasks</a>






#### <a doc_path="en/doc/version.md" >9 Version </a>


<script>

$("a").css("cursor","pointer") ;
$("a").each(function(){
    var docp = $(this).attr("doc_path") ;
    if(docp)
    {
        $(this).click(function(){
            parent.nav_to($(this).attr("doc_path"));
        });
    }
});
</script>

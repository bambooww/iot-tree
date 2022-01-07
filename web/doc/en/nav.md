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





#### <a doc_path="en/doc/quick/index.md" >3 Quick Know</a>
##### <a doc_path="en/doc/quick/quick_know_tree.md" target="main">&nbsp;&nbsp;&nbsp;3.1 Quickly understand the tree of IOT tree server</a>
##### <a doc_path="en/doc/quick/quick_know_ch_conn_drv.md" target="main">&nbsp;&nbsp;&nbsp;3.2 Connector-Channel-Driver</a>
##### <a doc_path="en/doc/quick/quick_know_devdef.md" target="main">&nbsp;&nbsp;&nbsp;3.3 Quickly understand device definition</a>
##### <a doc_path="en/doc/quick/quick_know_tcpserver_connector.md" target="main">&nbsp;&nbsp;&nbsp;3.4 Quickly understand tcpserver connector</a>
##### <a doc_path="en/doc/quick/quick_know_hmi.md" target="main">&nbsp;&nbsp;&nbsp;3.5 Monitoring UI,Human computer interaction(HMI)</a>
##### <a doc_path="en/doc/quick/quick_know_server.md" target="main">&nbsp;&nbsp;&nbsp;3.6 Quickly understand IOT tree server as your background support system</a>






#### <a doc_path="en/doc/advanced/index.md" >4 Advanced</a>
##### <a doc_path="en/doc/advanced/adv_ui_comp.md" target="main">&nbsp;&nbsp;&nbsp;4.1 Customize UI Components</a>






#### <a doc_path="en/doc/case/index.md" >5 Cases and best practices</a>
##### <a doc_path="en/doc/case/example_lamp_demo.md" target="main">&nbsp;&nbsp;&nbsp;5.1 Lamp Demo</a>
##### <a doc_path="en/doc/case/example_case.md" target="main">&nbsp;&nbsp;&nbsp;5.2 Automation</a>




<script>
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

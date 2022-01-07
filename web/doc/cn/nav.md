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


### 目录




#### <a doc_path="cn/README.md">1 概述</a>
#### <a doc_path="cn/doc/quick_start.md">2 快速开始</a>





#### <a doc_path="cn/doc/quick/index.md" >3 快速理解</a>
##### <a doc_path="cn/doc/quick/quick_know_tree.md" target="main">&nbsp;&nbsp;&nbsp;3.1 快速理解IOT-Tree Server的树</a>
##### <a doc_path="cn/doc/quick/quick_know_ch_conn_drv.md" target="main">&nbsp;&nbsp;&nbsp;3.2 快速理解 接入(Connector)-通道(Channel)-驱动(Driver)</a>
##### <a doc_path="cn/doc/quick/quick_know_devdef.md" target="main">&nbsp;&nbsp;&nbsp;3.3 快速理解设备定义Device Definition</a>
##### <a doc_path="cn/doc/quick/quick_know_tcpserver_connector.md" target="main">&nbsp;&nbsp;&nbsp;3.4 快速理解TcpServer接入器</a>
##### <a doc_path="cn/doc/quick/quick_know_hmi.md" target="main">&nbsp;&nbsp;&nbsp;3.5 快速了解交互UI(HMI)</a>

##### <a doc_path="cn/doc/quick/quick_know_server.md" target="main">&nbsp;&nbsp;&nbsp;3.6 快速了解IOT-Tree Server作为你的后台支撑系统</a>






#### <a doc_path="cn/doc/advanced/index.md" >4 深入理解</a>
##### <a doc_path="cn/doc/advanced/adv_ui_comp.md" target="main">&nbsp;&nbsp;&nbsp;4.1 自定义UI的控件</a>






#### <a doc_path="cn/doc/case/index.md" >5 案例和最佳实践</a>
##### <a doc_path="cn/doc/case/example_lamp_demo.md" target="main">&nbsp;&nbsp;&nbsp;5.1 简单的灯光控制</a>
##### <a doc_path="cn/doc/case/example_case.md" target="main">&nbsp;&nbsp;&nbsp;5.2 工业现场自动化系统</a>






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


*<script src="/_js/jquery-1.12.0.min.js"></script><script src="/_js/bootstrap/js/bootstrap.min.js"></script><script type="text/javascript" src="/_js/ajax.js"></script><script src="/_js/layui/layui.all.js"></script><script src="/_js/dlg_layer.js?v="></script>

<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" /><link  href="/_js/bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css" ><link  href="/_js/font4.7.0/css/font-awesome.css"  rel="stylesheet" type="text/css" ><link href="./inc/common.css" rel="stylesheet" type="text/css"><link href="./inc/index.css" rel="stylesheet" type="text/css">


### 目录

#### <a href="README.md">1 概述</a>
#### <a href="doc/quick_start.md">2 快速开始</a>

#### <a href="doc/case/index.md" doc_path="doc/case/index.md" >3 案例和最佳实践</a>
##### <a href="doc/case/example_lamp_demo.md" target="main">&nbsp;&nbsp;&nbsp;3.1 简单的灯光控制</a>
##### <a href="doc/case/case_auto.md" target="main">&nbsp;&nbsp;&nbsp;3.2 工业现场自动化系统</a>
##### <a href="doc/case/example_tcpserver_conn.md" target="main">&nbsp;&nbsp;&nbsp;3.3 TcpServer接入案例</a>
##### <a href="doc/case/case_rs485_sniffer.md" target="main">&nbsp;&nbsp;&nbsp;3.4 监听RS485 Modbus 总线数据</a>
##### <a href="doc/case/case_ref_hmi_auth.md" target="main">&nbsp;&nbsp;&nbsp;3.5 整合HMI画面到你的系统中（含权限插件开发）</a>
##### <a href="doc/case/back_sys.md" target="main">&nbsp;&nbsp;&nbsp;3.6 作为你的后台支撑系统</a>
<!--
##### <a href="doc/case/case_opc_da.md" target="main">&nbsp;&nbsp;&nbsp;3.6 通过OPC DA Client接入数据</a>
##### <a href="doc/case/case_jsplugin_db.md" target="main">&nbsp;&nbsp;&nbsp;3.5 自动记录到关系数据库（含JS插件开发）</a>
-->


#### <a href="doc/main/index.md" >4 总体说明和项目组成</a>
##### <a href="doc/main/prjs.md" target="main">&nbsp;&nbsp;&nbsp;4.1 项目(Projects)</a>
##### <a href="doc/main/conn.md" target="main">&nbsp;&nbsp;&nbsp;4.2 接入(Connectors)</a>
##### <a href="doc/main/ch_dev_tagg.md" target="main">&nbsp;&nbsp;&nbsp;4.3 容器节点：通道-设备-标签组</a>
##### <a href="doc/main/tags.md" target="main">&nbsp;&nbsp;&nbsp;4.4 标签列表(Tags)</a>
##### <a href="doc/main/ch_conn_drv.md" target="main">&nbsp;&nbsp;&nbsp;4.5 接入(Connector)-通道(Channel)-设备驱动(Driver)之间的关系</a>
##### <a href="doc/main/properties.md" target="main">&nbsp;&nbsp;&nbsp;4.6 属性Properties</a>
##### <a href="doc/main/hmi.md" target="main">&nbsp;&nbsp;&nbsp;4.7 特殊节点：UI(HMI)</a>
##### <a href="doc/main/task.md" target="main">&nbsp;&nbsp;&nbsp;4.8 项目任务</a>
##### <a href="doc/main/store.md" target="main">&nbsp;&nbsp;&nbsp;4.9 数据储存</a>
##### <a href="doc/main/dict.md" target="main">&nbsp;&nbsp;&nbsp;4.10 数据字典</a>
##### <a href="doc/main/outer_ref.md" target="main">&nbsp;&nbsp;&nbsp;4.11 对外接口RESTful</a>


#### <a href="doc/conn/index.md" >5 接入(Connector)</a>
##### <a href="doc/conn/link_tcpclient.md" target="main">&nbsp;&nbsp;&nbsp;5.1 链路-Tcp Client</a>
##### <a href="doc/conn/link_tcpserver.md" target="main">&nbsp;&nbsp;&nbsp;5.2 链路-Tcp Server</a>
##### <a href="doc/conn/link_com.md" target="main">&nbsp;&nbsp;&nbsp;5.3 链路-串口(COM)</a>
##### <a href="doc/conn/bind_opcda.md" target="main">&nbsp;&nbsp;&nbsp;5.4 绑定 - OPC DA</a>
##### <a href="doc/conn/bind_opcua.md" target="main">&nbsp;&nbsp;&nbsp;5.5 绑定 - OPC UA</a>
##### <a href="doc/conn/msg_mqtt.md" target="main">&nbsp;&nbsp;&nbsp;5.6 消息 - MQTT</a>
##### <a href="doc/conn/msg_http_url.md" target="main">&nbsp;&nbsp;&nbsp;5.7 消息 - HTTP URL</a>
##### <a href="doc/conn/msg_http_url_html.md" target="main">&nbsp;&nbsp;&nbsp;5.8 消息 - HTTP URL HTML</a>
##### <a href="doc/conn/msg_websocket.md" target="main">&nbsp;&nbsp;&nbsp;5.9 消息 - WebSocket Client</a>
##### <a href="doc/conn/other_iottree_node.md" target="main">&nbsp;&nbsp;&nbsp;5.10 其他 - IOTTree Node</a>
##### <a href="doc/conn/other_virtual.md" target="main">&nbsp;&nbsp;&nbsp;5.11 其他 - Virtual</a>

#### <a href="doc/device/index.md" >6 设备、设备库和驱动</a>
##### <a href="doc/device/dev_lib.md" target="main">&nbsp;&nbsp;&nbsp;6.1 设备库</a>
##### <a href="doc/device/dev_def.md" target="main">&nbsp;&nbsp;&nbsp;6.2 设备定义（设备库）Device Definition</a>
##### <a href="doc/device/drv_modbus.md" target="main">&nbsp;&nbsp;&nbsp;6.3 Modbus协议驱动</a>
##### <a href="doc/device/drv_ppi.md" target="main">&nbsp;&nbsp;&nbsp;6.4 PPI协议驱动</a>
##### <a href="doc/device/drv_siemens_eth.md" target="main">&nbsp;&nbsp;&nbsp;6.5 西门子以太网</a>

#### <a href="doc/hmi/index.md" >7 人机交互（HMI）</a>
##### <a href="doc/hmi/hmi_edit.md" target="main">&nbsp;&nbsp;&nbsp;7.1 HMI编辑
##### <a href="doc/hmi/hmi_props.md" target="main">&nbsp;&nbsp;&nbsp;7.2 图元通用属性说明
##### <a href="doc/hmi/hmi_bind_evt.md" target="main">&nbsp;&nbsp;&nbsp;7.3 HMI属性绑定和事件处理
##### <a href="doc/hmi/hmi_comp.md" target="main">&nbsp;&nbsp;&nbsp;7.4 HMI组件(控件)</a>



#### <a href="doc/js/index.md"> 8 JS脚本支持</a>

##### <a href="doc/js/js_in_midtag.md">&nbsp;&nbsp;&nbsp;8.1 JS在中间标签(Middle Tag)的使用</a>
##### <a href="doc/js/js_in_task.md">&nbsp;&nbsp;&nbsp;8.2 JS在任务(Task)中的使用</a>
##### <a href="doc/js/js_client.md">&nbsp;&nbsp;&nbsp;8.3 Client端JS</a>
##### <a href="doc/js/js_in_ui_event.md">&nbsp;&nbsp;&nbsp;8.4 JS在UI事件处理中的使用</a>


#### <a href="doc/advanced/index.md" >9 高级特性</a>
##### <a href="doc/advanced/adv_plugin.md" >&nbsp;&nbsp;&nbsp;9.1 插件开发</a>
##### <a href="doc/advanced/adv_js.md" target="main">&nbsp;&nbsp;&nbsp;9.2 JS脚本支持</a>
##### <a href="doc/advanced/adv_plugin_jsapi.md" target="main">&nbsp;&nbsp;&nbsp;9.3 JsApi</a>
##### <a href="doc/advanced/adv_plugin_auth.md" target="main">&nbsp;&nbsp;&nbsp;9.4 权限插件</a>
##### <a href="doc/advanced/adv_js_plugin.md" target="main">&nbsp;&nbsp;&nbsp;9.5 自定义JS脚本插件</a>
##### <a href="doc/advanced/adv_prj_task.md" target="main">&nbsp;&nbsp;&nbsp;9.6 定义项目任务</a>
##### <a href="doc/advanced/adv_prj_task_ctrl.md" target="main">&nbsp;&nbsp;&nbsp;9.7 使用项目任务实现控制脚本</a>
##### <a href="doc/advanced/adv_prj_task_ctrl.md" target="main">&nbsp;&nbsp;&nbsp;9.8 对外提供OPC UA接口</a>
##### <a href="doc/advanced/adv_dev_simulator.md" target="main">&nbsp;&nbsp;&nbsp;9.9 设备模拟器</a>
##### <a href="doc/advanced/main_sub_station.md" target="main">&nbsp;&nbsp;&nbsp;9.10 主站-子站远程控制</a>

#### <a href="doc/version.md" >10 版本说明</a>


<script>
<!--

var lang="cn";


$("a").css("cursor","pointer") ;
$("a").each(function(){
    var docp = $(this).attr("href") ;
    $(this).removeAttr("href");
    $(this).attr("doc_path",lang+"/"+docp);
    if(docp)
    {
        
        $(this).click(function(){
            parent.nav_to($(this).attr("doc_path"));
        });
    }
});
-->
</script>

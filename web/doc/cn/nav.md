
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
##### <a href="doc/case/case_s7200_smart.md" target="main">&nbsp;&nbsp;&nbsp;3.6 连接西门子PLC S7-200 Smart</a>


#### <a href="doc/main/index.md" >4 总体说明和项目组成</a>
##### <a href="doc/main/prjs.md" target="main">&nbsp;&nbsp;&nbsp;4.1 项目(Projects)</a>
##### <a href="doc/main/conn.md" target="main">&nbsp;&nbsp;&nbsp;4.2 接入(Connectors)</a>
##### <a href="doc/main/ch_dev_tagg.md" target="main">&nbsp;&nbsp;&nbsp;4.3 容器节点：通道-设备-标签组</a>
##### <a href="doc/main/tags.md" target="main">&nbsp;&nbsp;&nbsp;4.4 标签列表(Tags)</a>
##### <a href="doc/main/ch_conn_drv.md" target="main">&nbsp;&nbsp;&nbsp;4.5 接入(Connector)-通道(Channel)-设备驱动(Driver)之间的关系</a>
##### <a href="doc/main/properties.md" target="main">&nbsp;&nbsp;&nbsp;4.6 属性Properties</a>
##### <a href="doc/main/hmi.md" target="main">&nbsp;&nbsp;&nbsp;4.7 特殊节点：UI(HMI)</a>
##### <a href="doc/main/task.md" target="main">&nbsp;&nbsp;&nbsp;4.8 项目任务</a>
##### <a href="doc/main/alert.md" target="main">&nbsp;&nbsp;&nbsp;4.9 报警支持</a>
##### <a href="doc/store/index.md" target="main">&nbsp;&nbsp;&nbsp;4.9 数据储存</a>
##### <a href="doc/main/dict.md" target="main">&nbsp;&nbsp;&nbsp;4.10 数据字典</a>
##### <a href="doc/main/hmi_client.md" target="main">&nbsp;&nbsp;&nbsp;4.11 HMI客户端支持</a>


#### <a href="doc/conn/index.md" >5 接入(Connector)</a>
##### <a href="doc/conn/link_tcpclient.md" target="main">&nbsp;&nbsp;&nbsp;5.1 链路-Tcp Client</a>
##### <a href="doc/conn/link_tcpserver.md" target="main">&nbsp;&nbsp;&nbsp;5.2 链路-Tcp Server</a>
##### <a href="doc/conn/link_com.md" target="main">&nbsp;&nbsp;&nbsp;5.3 链路-串行通信(串口COM)</a>
##### <a href="doc/conn/bind_opcda.md" target="main">&nbsp;&nbsp;&nbsp;5.4 绑定 - OPC DA</a>
##### <a href="doc/conn/bind_opcua.md" target="main">&nbsp;&nbsp;&nbsp;5.5 绑定 - OPC UA</a>
##### <a href="doc/conn/msg_mqtt.md" target="main">&nbsp;&nbsp;&nbsp;5.6 消息 - MQTT</a>
##### <a href="doc/conn/msg_http_url.md" target="main">&nbsp;&nbsp;&nbsp;5.7 消息 - HTTP URL</a>
##### <a href="doc/conn/msg_http_url_html.md" target="main">&nbsp;&nbsp;&nbsp;5.8 消息 - HTTP URL HTML</a>
##### <a href="doc/conn/msg_websocket.md" target="main">&nbsp;&nbsp;&nbsp;5.9 消息 - WebSocket Client</a>
##### <a href="doc/conn/oth_iottree_node.md" target="main">&nbsp;&nbsp;&nbsp;5.10 其他 - IOTTree Node</a>
##### <a href="doc/conn/oth_virtual.md" target="main">&nbsp;&nbsp;&nbsp;5.11 其他 - Virtual</a>

#### <a href="doc/device/index.md" >6 设备、设备库和驱动</a>
##### <a href="doc/device/dev_lib.md" target="main">&nbsp;&nbsp;&nbsp;6.1 设备库</a>
##### <a href="doc/device/dev_def.md" target="main">&nbsp;&nbsp;&nbsp;6.2 设备定义（设备库）Device Definition</a>
##### <a href="doc/device/drv_modbus.md" target="main">&nbsp;&nbsp;&nbsp;6.3 Modbus协议驱动</a>
##### <a href="doc/device/drv_ppi.md" target="main">&nbsp;&nbsp;&nbsp;6.4 PPI协议驱动</a>
##### <a href="doc/device/drv_siemens_eth.md" target="main">&nbsp;&nbsp;&nbsp;6.5 西门子以太网</a>
##### <a href="doc/device/drv_fx.md" target="main">&nbsp;&nbsp;&nbsp;6.6 三菱FX</a>
##### <a href="doc/device/drv_fx_net.md" target="main">&nbsp;&nbsp;&nbsp;6.7 三菱FX Net</a>
##### <a href="doc/device/drv_omron_hl_fins_serial.md" target="main">&nbsp;&nbsp;&nbsp;6.8 欧姆龙 HostLink FINS Serial</a>

#### <a href="doc/js/index.md"> 7 JS脚本支持</a>

##### <a href="doc/js/js_in_midtag.md">&nbsp;&nbsp;&nbsp;7.1 JS在中间标签(Middle Tag)的使用</a>
##### <a href="doc/js/js_in_task.md">&nbsp;&nbsp;&nbsp;7.2 JS在任务(Task)中的使用</a>
##### <a href="doc/js/js_in_ui_event.md">&nbsp;&nbsp;&nbsp;7.3 JS在UI事件处理中的使用</a>
##### <a href="doc/js/js_inner_plugin_http.md">&nbsp;&nbsp;&nbsp;7.4 Js Api内部插件 - \$$http

#### <a href="doc/store/index.md" >8 数据存储、处理和展示</a>
##### <a href="doc/store/store.md" >&nbsp;&nbsp;&nbsp;8.1 标签数据简单存储和输出</a>
##### <a href="doc/store/inner_tssdb.md" target="main">&nbsp;&nbsp;&nbsp;8.2 内部时序段记录器 (TSSDB)</a>
##### <a href="doc/store/inner_recpro.md" target="main">&nbsp;&nbsp;&nbsp;8.3 记录数据二次处理</a>


#### <a href="doc/hmi/index.md" >9 人机交互(HMI)和对话框(Dialog)</a>
##### <a href="doc/hmi/hmi_node.md" target="main">&nbsp;&nbsp;&nbsp;9.1 界面HMI（UI）节点
##### <a href="doc/hmi/hmi_edit.md" target="main">&nbsp;&nbsp;&nbsp;9.2 HMI节点编辑
##### <a href="doc/hmi/hmi_props.md" target="main">&nbsp;&nbsp;&nbsp;9.3 图元通用属性说明
##### <a href="doc/hmi/hmi_bind_evt.md" target="main">&nbsp;&nbsp;&nbsp;9.4 HMI属性绑定和事件处理
##### <a href="doc/hmi/hmi_comp.md" target="main">&nbsp;&nbsp;&nbsp;9.5 HMI组件(控件)</a>
##### <a href="doc/hmi/hmi_data_show.md" target="main">&nbsp;&nbsp;&nbsp;9.6 HMI客户端数据列表展示</a>
##### <a href="doc/hmi/hmi_s4tss_rec.md" target="main">&nbsp;&nbsp;&nbsp;9.7 记录数据展示</a>

#### <a href="doc/msgnet/index.md" >10 消息流/网络</a>
##### <a href="doc/msgnet/mf_for_ctrl.md" >&nbsp;&nbsp;&nbsp;10.1 使用消息流程实现控制逻辑</a>
##### <a href="doc/msgnet/demo_auto_rec.md" >&nbsp;&nbsp;&nbsp;10.2 演示：自动数据记录</a>
##### <a href="doc/msgnet/demo_alert.md" >&nbsp;&nbsp;&nbsp;10.3 演示：报警发现与输出</a>
##### <a href="doc/msgnet/n__com.manual.md">&nbsp;&nbsp;&nbsp;10.4 节点:手动触发器</a>
##### <a href="doc/msgnet/n__com.timer.md">&nbsp;&nbsp;&nbsp;10.5 节点:定时触发器</a>
##### <a href="doc/msgnet/n__com.debug.md">&nbsp;&nbsp;&nbsp;10.6 节点:调试</a>
##### <a href="doc/msgnet/n__com.mem_que.md">&nbsp;&nbsp;&nbsp;10.7 节点:内存队列</a>
##### <a href="doc/msgnet/n__func.js_func.md">&nbsp;&nbsp;&nbsp;10.8 节点:JS函数</a>
##### <a href="doc/msgnet/n__func.template.md">&nbsp;&nbsp;&nbsp;10.9 节点:模板</a>
##### <a href="doc/msgnet/n__func.change.md">&nbsp;&nbsp;&nbsp;10.10 节点:设置修改</a>
##### <a href="doc/msgnet/n__func.switch.md">&nbsp;&nbsp;&nbsp;10.11 节点:路径切换</a>
##### <a href="doc/msgnet/n__func.onoff.md">&nbsp;&nbsp;&nbsp;10.12 节点:开关</a>
##### <a href="doc/msgnet/n__dev.tag_reader.md">&nbsp;&nbsp;&nbsp;10.13 节点:标签数据读取</a>
##### <a href="doc/msgnet/n__dev.tag_writer.md">&nbsp;&nbsp;&nbsp;10.14 节点:标签数据写入</a>
##### <a href="doc/msgnet/n__dev.tag_filter.md">&nbsp;&nbsp;&nbsp;10.15 节点:标签读取过滤器</a>
##### <a href="doc/msgnet/n__dev.tag_filter_w.md">&nbsp;&nbsp;&nbsp;10.16 节点:标签写入过滤器</a>
##### <a href="doc/msgnet/n__dev.tag_evt_trigger.md">&nbsp;&nbsp;&nbsp;10.17 节点:标签事件触发器</a>

#### <a href="doc/advanced/index.md" >11 高级特性</a>
##### <a href="doc/advanced/adv_plugin.md" >&nbsp;&nbsp;&nbsp;11.1 插件开发</a>
##### <a href="doc/advanced/adv_plugin_jsapi.md" target="main">&nbsp;&nbsp;&nbsp;11.2 插件开发 - JsApi</a>
##### <a href="doc/advanced/adv_plugin_auth.md" target="main">&nbsp;&nbsp;&nbsp;11.3 插件开发 - Authority</a>
##### <a href="doc/advanced/adv_self_app.md" target="main">&nbsp;&nbsp;&nbsp;11.4 自定义项目管理(Webapp)</a>
##### <a href="doc/advanced/adv_restful_out.md" target="main">&nbsp;&nbsp;&nbsp;11.5 对外提供RESTFul接口</a>
##### <a href="doc/advanced/adv_opc_ua_out.md" target="main">&nbsp;&nbsp;&nbsp;11.6 对外提供OPC UA接口</a>
##### <a href="doc/advanced/main_sub_station.md" target="main">&nbsp;&nbsp;&nbsp;11.7 中心-子站远程监控</a>
##### <a href="doc/advanced/adv_dev_simulator.md" target="main">&nbsp;&nbsp;&nbsp;11.8 设备模拟器</a>

#### <a href="doc/version.md" >12 版本说明</a>


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

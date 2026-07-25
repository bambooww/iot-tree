<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,org.json.*,
	org.iottree.core.*,
	org.iottree.core.basic.*,
	org.iottree.core.task.*,
	org.iottree.core.util.*,org.iottree.core.msgnet.util.*,
	org.iottree.core.dict.*,org.iottree.core.msgnet.nodes.*,
	org.iottree.core.comp.*,org.iottree.core.util.jt.*,
	org.iottree.core.msgnet.*
	"%><%@ taglib uri="wb_tag" prefix="w"%><%
	if(!Convert.checkReqEmpty(request, out, "container_id","netid","itemid"))
			return ;
	String container_id = request.getParameter("container_id");
	String netid = request.getParameter("netid") ;
	String itemid = request.getParameter("itemid") ;
	String op = request.getParameter("op") ;
	MNManager mnm= MNManager.getInstanceByContainerId(container_id) ;
	if(mnm==null)
	{
		out.print("no MsgNet Manager with container_id="+container_id) ;
		return ;
	}

	MNNet net = mnm.getNetById(netid) ;
	if(net==null)
	{
		out.print("no net found") ;
		return ;
	}
	NE_Debug item =(NE_Debug)net.getItemById(itemid) ;
	if(item==null)
	{
		out.print("no item found") ;
		return ;
	}
	
	int buf_len = item.getMaxBufferedMsgNum();
	LinkedHashMap<String,NE_Debug.AnaItem> subn2ai = item.getSubN2AnaItem();
	JSONArray jarr = new JSONArray() ;
	if(subn2ai!=null)
	{
		for(NE_Debug.AnaItem ai:subn2ai.values())
		{
			jarr.put(ai.toJO()) ;
		}
	}
%>
<html>
<head>
<title></title>
<jsp:include page="../../head.jsp">
	<jsp:param value="true" name="simple"/>
	<jsp:param value="true" name="echarts"/>
</jsp:include>
<script type="text/javascript">

</script>
<style>
#ggg {width:100%;height:100%;border:0px;position: absolute;left:0px;top:0px;overflow: hidden;}
</style>
</head>

<body style="overflow: hidden;">
<div id="ggg" >1111</div>
</body>

<script type="text/javascript">

var container_id="<%=container_id%>";
var netid="<%=netid%>";
var itemid="<%=itemid%>";
var buf_len = <%=buf_len%> ;
var lns = <%=jarr%> ;
var chart = null ;
var option = null ;
var subn2data={}; 

layui.use('form', function(){
	form = layui.form;
	element = layui.element;
	form.render();
	init_ui()
});

function init_ui()
{
	let dom = document.getElementById('ggg');
	chart = echarts.init(dom,null, { renderer: 'canvas' });
    
	let data_tts = [] ;
	let y_axis = [] ;
	let series = [] ;
	let left_i = -1 ,right_i = -1 ;
	
	let showy = true;
	for(let i = 0 ; i< lns.length ; i ++)
	{
		let ln = lns[i] ;
		subn2data[ln.subn]=[];
		let tmpn = ln.title+(ln.unit?"\n("+ln.unit+")":"");
		data_tts.push(tmpn) ;

		let offset = 0 ;
		if(ln.yaxis_right)
		{
			right_i ++ ;
			offset = right_i*60 ;
		}
		else
		{
			left_i ++ ;
			offset = left_i*60 ;
		}
		
		let y_pm = null ;
		if(!showy)
		{
			 y_pm={show:false,
			            min: ln.min,
			            max: ln.max,
			            axisLine: {show: false},
			            axisLabel: {show:false},
			            splitLine:{show:false},axisTick: { show: false}
			        }
		}
		else
		{
			 y_pm={  type: 'value',show:true,
		            name:tmpn,nameRotate: 20,
		            min: ln.min,
		            max: ln.max,
		            position: ln.yaxis_right?'right':'left',
		            offset: offset,
		            axisLine: {
		                show: true,
		                lineStyle: {
		                    color: ln.color||'#FF0000' //轴的颜色
		                }
		            },
		            axisLabel: {
		                formatter: '{value} '+ln.unit,
		            },
		            splitLine:{show:false}
		        }
		}
		
		y_axis.push(y_pm);
		
		series.push(
	                {
	                    name: tmpn,
	                    type: 'line',
	                    id:"ln_"+i,
	                    yAxisIndex: i,animation0: false,
	                    data:[],
	                    showSymbol: false,
	                    itemStyle: {
	                        normal: {
	                            color: ln.color||'#FF0000',
	                            fontSize: 12,
	                            lineStyle:{
	                                width:1,
	                                color: ln.color||'#FF0000'
	                            }
	                        }
	                    }
	                }
				);
	}
	
	option = {

            tooltip: {
                trigger: 'axis',
                formatter: function(params) {
                	let x_dt = new Date(params[0].axisValue) ;
                	let x_v = params[0].data[1] ;
                	let result = x_dt.format_local("yyyy-MM-dd hh:mm");
                	result += `<br>\${x_v}`;
                	return result;
                }
            },
            legend: {
                data: data_tts
            },
            grid: {
                left: '1%',top:'15px',right: '1%',bottom: '3px',
                containLabel: true
            },
            xAxis: {
                type: 'time',interval: 1000,
                boundaryGap: true,
            },
            yAxis: y_axis,
            series: series
        };


    chart.setOption(option);
    
    const resizeObserver = new ResizeObserver(entries => {
        //for (let entry of entries) {
        //    //chart.resize();
       // }
    	chart.resize();
    });

    resizeObserver.observe(dom);
}

function on_debug_pushed(ditem)
{
	//console.log("on_debug_pushed",ditem) ;
	let msg = ditem.msg ;
	if(!msg) return ;
	let ms = msg.time_ms ;
	let pld = msg.payload ;
	//if(typeof(pld)!='number')
	//	return ;
	update_ln(ms,msg) ;
}

function get_sub_v(pld,subn)
{
	let v = pld ;
	let ss = subn.split(".") ;
	for(let s of ss)
	{
		v = v[s] ;
		if(!v) return null ;
	}
	return v ;
}

function update_ln(ms,msg)
{
	let ser_dds = [] ;
	for(let i = 0 ; i< lns.length ; i ++)
	{
		let ln = lns[i] ;
		let dd = subn2data[ln.subn];
		if(!dd) continue ;
		let v = get_sub_v(msg,ln.subn) ;
		if(!v) continue ;
		//console.log(v);
		dd.push([ms, v]);
		if (dd.length > buf_len)
	        dd.shift();
		//option.series[i] = {type: 'line',id:"ln_"+i,data:dd}
		option.series[i].data = dd;
		//ser_dds.push({type: 'line',id:"ln_"+i,data:dd}) ;
	}
	//chart.clear();
	// chart.setOption({series: ser_dds},true);
	chart.setOption(option,true);
	//chart.setOption({series:ser_dds},true);
	
	 /*
	chart.setOption({
         series: [{
             data: ln_data
         }]
     }, true);//add
     */
}

function on_rt_panel_btn()
{
	let strv = $("#strv").val() ;
	if(!strv)
	{
		dlg.msg("no input") ;return ;
	}
	
	let pm = {op:"send",container_id:container_id,netid:netid,itemid:itemid,strv:strv} ;
	send_ajax("_com.manual.rt.jsp",pm,(bsucc,ret)=>{
		if(!bsucc||ret!="succ")
		{
			dlg.msg(ret);return;
		}
		dlg.msg("send ok") ;
	});
}

</script>

</html>                                                                                                                                                                                                                            
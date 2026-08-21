<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
				org.json.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*
	"%><%@ taglib uri="wb_tag" prefix="wbt"%><%
	if(!Convert.checkReqEmpty(request, out, "prjid","ids"))
		return ;
	String path = request.getParameter("path") ;
	String prjid = request.getParameter("prjid") ;
	String ids_str = request.getParameter("ids") ;
	List<String> ids = Convert.splitStrWith(ids_str,",") ;
	if(ids==null||ids.size()<=0)
	{
		out.print("no ids input");return ;
	}
	UAPrj prj = UAManager.getInstance().getPrjById(prjid) ;
	if(prj==null)
	{
		out.print("no prj found");return ;
	}
	
	ArrayList<UATag> tags = new ArrayList<>() ;
	for(String id:ids)
	{
		UATag tag = prj.findTagById(id) ;
		if(tag==null)
			continue ;
		tags.add(tag) ;
	}
	if(tags.size()<=0)
	{
		out.print("no tag found");return ;
	}
	JSONArray tags_jarr = new JSONArray() ;
	for(UATag tag:tags)
	{
		
	}
	
%><!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title></title>
<jsp:include page="../head.jsp">
	<jsp:param value="true" name="tree"/>
	<jsp:param value="true" name="echarts"/>
</jsp:include>
<style>
body {
	margin: 0px;
	padding: 0px;
	font-size: 12px;
-moz-user-select : none;
-webkit-user-select: none;
background-color: #f5f5f5;
}
.top {
	position: fixed;
	
	left: 0;
	top: 0;
	bottom: 0;
	z-index0: 999;
	height: 45px;
	width:100%;
	text-align: left;
	margin:0px;
	padding:0px;
	overflow: hidden
}

.layui-nav-itemed>.layui-nav-child
{
background-color: #ffffff;
}

.right {left:0px;right:0px;height:100%;top:0px;background-color: #fff;
	position: absolute;margin:5px;border:1px solid #cecece;}

.left_btm
{
	position: absolute;
	left:0px;width:30%;
	bottom: 0px;
	height:70%;
	border: 1px solid;
	border-color: #cccccc;
}

.left_btm .show_hid
{
	position: absolute;
	right:3px;top:3px;
	width:20px;
	text-align:center;
}

.tab .tab-header-item .close
{
	display:none;
}

.tb_item
{
	
	border: 1px solid #5199ee;
	min-width:90px;
	float:left;
	height:30px;
	padding-top:5px;
	margin:5px;
	cursor:pointer;
	text-align: center;
}

.tb_item:hover {
	background-color: #e2e2e2;;
}

.lns_item {
position:relative;
	border: 1px solid #5199ee;
	width:90%;
	cursor:pointer;
	height:30px;
	margin:5px;
}
.lns_item:hover {background-color: #e2e2e2;}

.lns_item .tt
{
	position:absolute;
	left:3px;top:6px;font:12px;
}

.lns_item .op
{
	position:absolute;
	right:3px;top:4px;
}
.seled {background-color: #aaaaaa;}

.tip_di {display:inline-block;margin-right:5px;border-radius:10px;margin-top:10px;min-width:100px;height:10px;}
#ctrl_panel {position: absolute;top:10px;right:10px;border:1px solid #ccc;}
</style>

</head>
<script type="text/javascript">
dlg.dlg_top=true;
</script>
<body style="overflow-x:hidden;overflow-y:hidden;">
<div id="div_content"  class="right">
            <div id="draw_area" style="right:0px;left:0px;top:0px;bottom:10px;overflow:hidden;position: absolute;">
          	
          </div>
	<div id="draw_dt_r" style="position: relative;left:10%;width:80%;visibility:hidden;">YYYY-MM-dd <div id="ymd" ></div></div>
     <div style="position: relative;left:10%;width:80%;display:none;">hh:mm:ss <div id="hms" ></div></div>
</div>
<div id="ctrl_panel">
	<input id="chk_auto" type="checkbox" checked="checked"/><wbt:g>auto_refresh</wbt:g>
</div>
<script>
var path = "<%=path%>";//node cxt path
var prjid = "<%=prjid%>" ;
var ids_str = "<%=ids_str%>" ;

var table_uid = null ;

var cur_lns =[
    {
        "tagp": "ch1.aio.wl_val",
        "aggr_tpt": "均值",
        "color": "#8e1f1f",
        //"max": 100,
        "dec_ptn": -1,
        "aggr_color": "",
        "aggr_tp": "mean",
        "yaxis_right": false,
        "title": "water level value",
        "aggr_min": 0,
        "unit": "m",
       // "min": 0,
        "aggr_max": 100
    },
    {
        "tagp": "ch1.flow.flow_val",
        "aggr_tpt": "均值",
        "color": "#15c1ad",
        //"max": 100,
        "dec_ptn": -1,
        "aggr_color": "",
        "aggr_tp": "mean",
        "yaxis_right": false,
        "title": "speed value",
        "aggr_min": 0,
        "unit": "m³/h",
        //"min": 0,
        "aggr_max": 100
    }
];

function resize_iframe_h()
{
	   var h = $(window).height()-80+45;
	   $("iframe").css("height",h+"px");
}

function resize_tree()
{
	var h = $(window).height()-120+45;
	$("#tree").css("height",h+"px");
}

resize_tree();
//////////edit panel

var resize_cc = 0 ;
$(window).resize(function(){
	resize_iframe_h();
	resize_tree();
	//panel.updatePixelSize() ;
	resize_cc ++ ;
	//if(resize_cc<=1)
		//draw_fit();
	});
	


$(document).ready(function()
{

});

function gen_color()
{
	const hue = Math.floor(Math.random() * 360);
    const sat = 60 + Math.floor(Math.random() * 40); // 60~100%
    const light = 20 + Math.floor(Math.random() * 25); // 20~45%

    function hslToRgb(h, s, l) {
        let r, g, b;
        if (s === 0) {
            r = g = b = l;
        } else {
            const hue2rgb = (p, q, t) => {
                if (t < 0) t += 1;
                if (t > 1) t -= 1;
                if (t < 1/6) return p + (q - p) * 6 * t;
                if (t < 1/2) return q;
                if (t < 2/3) return p + (q - p) * (2/3 - t) * 6;
                return p;
            };
            const q = l < 0.5 ? l * (1 + s) : l + s - l * s;
            const p = 2 * l - q;
            r = hue2rgb(p, q, h + 1/3);
            g = hue2rgb(p, q, h);
            b = hue2rgb(p, q, h - 1/3);
        }
        return [r, g, b];
    }

    const [r, g, b] = hslToRgb(hue / 360, sat / 100, light / 100);
    const toHex = (num) => Math.round(num * 255).toString(16).padStart(2, '0');
    return `#\${toHex(r)}\${toHex(g)}\${toHex(b)}`;
}
	
var ccc = echarts.init($("#draw_area")[0]);

var option = null ;

function draw_lines(lns)
{
	let data_tts = [] ;
	let y_axis = [] ;
	let series = [] ;
	let left_i = -1 ,right_i = -1 ;
	let y_step = 60 ;
	
	let d_l = 0 ;
	let showy = true;//$("#show_y").prop("checked");
	for(let i = 0 ; i< lns.length ; i ++)
	{
		let ln = lns[i] ;
		ln.color =gen_color();
		let tmpn = ln.title+(ln.unit?"\n("+ln.unit+")":"");
		data_tts.push(tmpn) ;
		
		let offset = d_l ;
		if(ln.yaxis_right)
		{
			right_i ++ ;
			offset = d_l+right_i*y_step ;
		}
		else
		{
			left_i ++ ;
			offset = d_l+left_i*y_step ;
		}
		
		let y_pm = null ;
		if(!showy)
		{
			 y_pm={show:false,
			            //min: ln.min,
			            //max: ln.max,
			            scale: true,
			            axisLine: {show: false},
			            axisLabel: {show:false},
			            splitLine:{show:false},axisTick: { show: false}
			        }
		}
		else
		{
			 y_pm={  type: 'value',show:true,
		            name:tmpn,nameRotate: 20,
		            //min: ln.min,
		            //max: ln.max,
		            scale: true,
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
		let tagdd = ln.data||[];//ob[ln.tagp] ;
		if(tagdd)
		{
			for(let o of tagdd)
			{
				o[0] = new Date(o[0]).format_local("yyyy-MM-dd hh:mm:ss") ;
			}
		}
		series.push(
	                {
	                    name: tmpn,
	                    type: 'line',
	                    yAxisIndex: i,
	                    data:tagdd,
	                    showSymbol: ln.bstr,symbol: 'circle',symbolSize: 4,showAllSymbol: true,
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
            title0: {
                text: "title"
            },
            tooltip: {
                trigger: 'axis',
                formatter: function(params) {
                	let x_dt = new Date(params[0].axisValue) ;
                	let result = x_dt.format_local("yyyy-MM-dd hh:mm:ss");
                	
                	for(let k = 0 ; k < params.length ; k ++)
                	{
                		let item = params[k] ;
                		let aggr_t = "" ;
                		let val = item.value[1]
                		let valt = "" ;
                		if(item.value.length>=3)
                			valt ="\""+ item.value[2] +"\"";
                		result += `<br><span class="tip_di" style="color:\${item.color}">&nbsp;&nbsp;&nbsp;<i class="fa-solid fa-circle"></i>&nbsp;\${aggr_t} \${item.seriesName}: \${val} \${valt}</span>`;
                	}
                    
                    return result;
                }
            },
            legend: {
            	top:12,
                data: data_tts
            },
            grid: {
                left: 10+(lns.length - 1) * 30,
                right: '3%',
                bottom: '3%',
                containLabel: true
            },
            xAxis: {
                type: 'time',
                boundaryGap: true,
            },
            yAxis: y_axis,
            series: series
        };
	ccc.clear();
	ccc.setOption(option);
}

function draw_data(lns)
{
	let series=[];
	for(let i = 0 ; i< lns.length ; i ++)
	{
		let tagdd = lns[i].data||[];//ob[ln.tagp] ;
		if(tagdd)
		{
			for(let o of tagdd)
			{
				o[0] = new Date(o[0]).format_local("yyyy-MM-dd hh:mm:ss") ;
				//dd.push([dt,o[1]]) ;
			}
		}
		series.push(
	                {
	                    type: 'line',
	                    yAxisIndex: i,
	                    data:tagdd,
	                }
				);
	}
	
	ccc.setOption({series: series},false);
}

var bfirst = true ;

function read_data()
{
	if(!$("#chk_auto").prop("checked"))
		return ;
	let pm = {op:"tag_his_multi",path:path,prjid:prjid,ids:ids_str}
	send_ajax("tag_ajax.jsp",pm,(bsucc,ret)=>{
		if(!bsucc || ret.indexOf("[")!=0) {console.log(ret);return;}
		let d = null;
		eval("d="+ret) ;
		if(bfirst)
		{
			bfirst = false;
			draw_lines(d) ;
		}
		else
		{
			draw_data(d) ;
		}
	})
}
read_data()

setInterval(read_data,1000);
</script>
</body>
</html>

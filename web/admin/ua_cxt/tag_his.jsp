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
	if(!Convert.checkReqEmpty(request, out, "prjid","id"))
		return ;
	String prjid = request.getParameter("prjid") ;
	String id = request.getParameter("id") ;
	UAPrj prj = UAManager.getInstance().getPrjById(prjid) ;
	if(prj==null)
	{
		out.print("no prj found");return ;
	}
	UATag tag = prj.findTagById(id) ;
	if(tag==null)
	{
		out.print("no tag found");return ;
	}
	
	String ttp = tag.getNodeCxtPathTitleIn(prj) ;
	String path = tag.getNodePathCxt();
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="UTF-8">
<title></title>
<jsp:include page="../head.jsp">
	<jsp:param value="true" name="simple"/>
	<jsp:param value="true" name="echarts"/>
</jsp:include>
<style>
  html,body{margin:0;padding:0;height:100%}
  #main{width:100%;height:100%}
</style>
</head>
<body>
<div id="main"></div>
<script>
var title = "<%=ttp%>" ;
var path = "<%=path%>";

function cal_min_max_ave(row_dd)
{
	let ret = null ;
	let cc =0 ;
	for(let d of row_dd)
	{
		if(d.length==3 && d[2]===false) continue ;
		cc ++ ;
		if(ret==null)
		{
			ret = {min:d[1],max:d[1],sum:d[1],ave:d[1]} ;
			continue ;
		}
		let v = d[1] ;
		if(ret.min>v) ret.min=v ;
		if(ret.max<v) ret.max=v ;
		ret.sum += v ;
		ret.ave = ret.sum/cc ;
	}
	return ret ;
}

function process_invalid(row_dd)
{
	var mma = cal_min_max_ave(row_dd);
	if(!mma) return ;
	for(let d of row_dd)
	{
		if(d.length==3 && d[2]===false)
			d[1] = mma.ave ;
	}
}

function split_segs(data)
{
  const lines   = [];
  const invalid = [];
  let cur_seg = [];

  data.forEach(([x, y, valid = true]) => {
    if (valid) {
      cur_seg.push([x, y]);
    } else {
      if (cur_seg.length) {
        lines.push(cur_seg);
        cur_seg = [];
      }
      invalid.push([x, y]);//invalid pts
      lines.push([[x, null]]); //force broke
    }
  });
  if (cur_seg.length) lines.push(cur_seg);// tail left
  return { lines, invalid };
}

function create_series(row_dd)
{
	process_invalid(row_dd)
	const { lines, invalid } = split_segs(row_dd);
	//console.log(lines) ;
	const series = [];

	for(let seg of lines)
	{
		//console.log("seg",seg);
		if(!(seg[1]===null))
		{
			series.push({
			    type: 'line',
			    data: seg,
			    smooth: true,
			    lineStyle: { color: '#5470c6', width: 1 },
			    connectNulls: false,
			    showSymbol: false
			  });
		}
		else
		{
			series.push({
			    type: 'scatter',
			    data: seg,
			    symbolSize: 2,
			    itemStyle: { color: '#5470c6' }
			  });
		}
	}
	series.push(
	  {
	    name: '<wbt:g>invalid_pts</wbt:g>',
	    type: 'scatter',
	    data: invalid,
	    symbolSize: 8,
	    itemStyle: { color: '#ee6666' }
	  });
	return series;
}


//var series = create_series(rawData)
//console.log(rawData) ;


const chart = echarts.init(document.getElementById('main'));
var option={
	  title: { text: title, left: 'center' },
	  tooltip: {
	    trigger: 'axis',
	    formatter: arr => {
	      const p = arr[0];
	      const [x, y] = p.data;
	      //const item = rawData.find(d => d[0] === x);
	      const flag = true;// item?.[2] ?? true;
	      return `<wbt:g>time</wbt:g>: \${new Date(x).toLocaleString()}<br/> \${y||""}\${!flag ? ' (<wbt:g>invalid</wbt:g>)' : ''}`;
	    }
	  },
	  legend: { data: ['data','<wbt:g>invalid_pts</wbt:g>'], top: 35 },
	  xAxis: { type: 'time', name: '<wbt:g>time</wbt:g>',
		  axisLabel: {
	          formatter: function(value) {
	              return new Date(value).toLocaleTimeString();
	          }
	      } },
	  yAxis: { type: 'value', name: '<wbt:g>val</wbt:g>',scale:true},
	  animation:false,
	  series:[]
	}

chart.setOption(option);

function update_ln(ob)
{
	if(!ob.bnum && ob.vt!='bool')
	{// show data list
		return ;
	}
	
	var series = create_series(ob.data||[])
	chart.setOption({series:series},false);
}

function update_lines()
{
	send_ajax("tag_ajax.jsp",{op:"tag_his",path:path},(bsucc,ret)=>{
		if(!bsucc || ret.indexOf("{")!=0)
		{
			console.log(ret);return;
		}
		let ob = null;
		eval("ob="+ret) ;
		//console.log(ob);
		update_ln(ob) ;
	})
}

setInterval(update_lines,1000);
update_lines();
window.addEventListener('resize', () => chart.resize());
</script>
</body>
</html>
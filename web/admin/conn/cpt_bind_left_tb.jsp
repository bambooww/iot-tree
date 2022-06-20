<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
	org.iottree.core.conn.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
				org.eclipse.milo.opcua.stack.core.types.structured.*,
				org.eclipse.milo.opcua.stack.core.types.builtin.*,
				org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.*,
				org.eclipse.milo.opcua.sdk.client.nodes.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"
	%><script>


function set_or_add_bind_item(bpath,tpath)
{
	//check tpath exist
	var tr = $("tr[tagp$='"+tpath+"']") ;
	if(tr.length<=0)
	{
		add_bind_item(bpath,tpath);
		return ;
	}
	map_set_to_tr(tr,bpath);
}


var cur_lefts_trs = [] ;


function on_left(tr)
{
	if(!b_ctrl_down)
	{
		cur_lefts_trs.length=0;
	}
	
	//TODO shift support later
	
	if(cur_lefts_trs.indexOf(tr)<0)
			cur_lefts_trs.push(tr) ;
	
	refresh_left();
}

function refresh_left()
{
	$("#bind_tb_body tr").each(function(){
		$(this).removeClass("map_sel") ;
	});
	for(var tmptr of cur_lefts_trs)
		$(tmptr).addClass("map_sel") ;
}

/*
function copy_or_not(b)
{
	if(b)
	{
		if(cur_lefts_trs.length<=0)
		{
			dlg.msg("please select items left") ;
			return ;
		}
		
		for(var tr of cur_lefts_trs)
		{
			var tn = $(tr) ;
			//var opctp = tn.attr("opc_tp") ;
			var v = tn.attr("path") + ":"+tn.attr("vt") ;
			if(has_selected_val(v))
				continue ;
			$("#bind_selected").append("<option value='"+v+"'>"+v+"</option>");
		}
		return;
	}
	
	//un sel
	$("#bind_selected  option:selected").each(function(){
	    //var tmpv = $(this).attr("value") ;
	    $(this).remove() ;
	})
}
*/


function tb_get_left_vals()
{
	if(cur_lefts_trs.length<=0)
	{
		return [];
	}
	
	var ret=[];
	for(var tr of cur_lefts_trs)
	{
		var tn = $(tr) ;
		var p = tn.attr("path");
		var v = p + ":"+tn.attr("vt") ;
		
		ret.push(v) ;
	}
	return ret ;
	
}

/*
function get_selected_vals()
{
	var ret=[];
	$("#bind_selected  option").each(function(){
	    var tmpv = $(this).attr("value") ;
	    ret.push(tmpv) ;
	})
	return ret;
}

function get_bindlist_valstr()
{
	var bindids = get_selected_vals()
	var bindstr = "" ;
	if(bindids!=null)
	{
		for(var bid of bindids)
			bindstr += '|'+bid ;
		bindstr = bindstr.substr(1) ;
	}
	return bindstr ;
}

function has_selected_val(v)
{
	var r = false;
	$("#bind_selected  option").each(function(){
	    if( $(this).attr("value") ==v)
	    	r = true ;
	})
	return r ;
}
*/


var LOAD_ROWS = 40 ;
var page_last_idx = 0 ;
var page_has_next = true;
var search_key = "" ;

function search(bclear)
{
	
	var sk = "";
	if(bclear)
		$("#inp_search").val("") ;
	else
		sk = $("#inp_search").val() ;
	search_key = sk ;
	if(!sk)
	{
		refresh_tb_list(false)
		return ;
	}
	
	$('#bind_tb_body').html("") ;
	page_last_idx = 0 ;
	page_has_next = true;
	show_tb_list();
	return ;
}

function do_search_ret(e)
{
    var evt = window.event || e;
    if (evt.keyCode == 13) {
    	search(false)
    }
}

function refresh_tb_list(breload)
{
	if(!breload)
	{
		$('#bind_tb_body').html("") ;
		page_last_idx = 0 ;
		page_has_next = true;
		show_tb_list();
		return ;
	}
	
	send_ajax("cpt_bind_ajax.jsp",{prjid:prjid,op:"clear_cache",cpid:cpid,cptp:cptp,connid:connid},(bsucc,ret)=>{
			$('#bind_tb_body').html("") ;
			page_last_idx = 0 ;
			page_has_next = true;
			show_tb_list();
			return ;
	}) ;
}

function show_tb_list()
{
	if(no_ajax)
		return ;
	
	var idx = page_last_idx;
	var size = LOAD_ROWS ;
	dlg.loading(true) ;
	send_ajax("cpt_bind_ajax.jsp",{prjid:prjid,op:"list",cpid:cpid,cptp:cptp,connid:connid,idx:idx,size:size,sk:search_key},function(bsucc,ret){
		dlg.loading(false) ;
	//	console.log("ret len="+ret.length) ;
		if(!bsucc||ret.indexOf("[")!=0)
		{
			dlg.msg(ret) ;
			return ;
		}
		var tbb = $('#bind_tb_body');
		var obs = null;
		eval("obs="+ret) ;
		page_has_next = obs.length>=size;
		page_last_idx += obs.length ;
		for(var ob of obs)
		{
			tbb.append(ob2tr_row(ob));
		}
		
	});
}


function show_parent_no_ajax()
{
	var ow = dlg.get_opener_w() ;
	if(!ow)
		return ;
	if(ow.get_bind_list)
	{
		var obs = ow.get_bind_list();
		var tbb = $('#bind_tb_body');
		for(var ob of obs)
		{
			tbb.append(ob2tr_row(ob)) ;
		}
	}
	
	if(ow.get_map_list)
	{
		var mbs = ow.get_map_list() ;
		for(var ob of mbs)
			set_or_add_bind_item(ob.bindp,ob.tagp)
	}
	
	
}


var ROW_MAX_LEN = 30 ;

function ob2tr_row(ob)
{
	var tt = ob.tt ;
	if(!tt)
		tt = "" ;
	var ret = "<tr title='"+tt+"' path='"+ob.path+"' vt='"+ob.vt+"' onclick='on_left(this)'>" ;
	var txt = ob.path ;
	var txtlen = txt.length ;
	if(txtlen>ROW_MAX_LEN)
	{
		ret += "<td title='"+txt+"'>..."+txt.substring(txtlen-ROW_MAX_LEN)+"</td>";
	}
	else
	{
		ret += "<td>"+txt+"</td>";
	}
	ret += "<td>"+tt+"</td>";
	ret += "<td>"+ob.vt+"</td>";
	ret += "<td></td>";
	ret += "</tr>"
	return ret ;
}

function read_tmp_paths_vals()
{
	if(cur_lefts_trs.length<=0)
		return ;
	var pstr = $(cur_lefts_trs[0]).attr("path") ;
	for(var i = 1 ; i < cur_lefts_trs.length;i++)
		pstr += ","+$(cur_lefts_trs[i]).attr("path") ;
	send_ajax("cpt_bind_ajax.jsp",{prjid:prjid,op:"tmp_paths_vals",cpid:cpid,cptp:cptp,connid:connid,paths:pstr},function(bsucc,ret){
		
	//	console.log("ret len="+ret.length) ;
		if(!bsucc||ret.indexOf("{")!=0)
		{
			console.log(ret) ;
			return ;
		}
		var tbb = $('#bind_tb_body');
		var ob = null;
		eval("ob="+ret) ;
		for(var n in ob)
		{
			var v = ob[n] ;
			var tr = tbb.find("tr[path$='"+n+"']") ;
			tr.children('td').eq(2).html(v);
		}
		
	});
	
}

//setInterval(read_tmp_paths_vals,2000) ;

var allshow=false;

var sdiv = $("#list_table")[0] ;
$("#list_table").scroll(()=>{
	if(no_ajax)
		return ;
	
	var wholeHeight=sdiv.scrollHeight;
	 var scrollTop=sdiv.scrollTop;
	 var divHeight=sdiv.clientHeight;
	 if(divHeight+scrollTop>=wholeHeight)
	 {//reach btm
		 if(!page_has_next)
			{
				if(!allshow)
					lj.msg("all list ok");
				allshow=true;
				return;
			}
				
			//console.log("show more");
			show_tb_list();
		    $("list_table").scroll(scrollTop);
	 }
	 if(scrollTop==0)
	 {//reach top
		
	}
});

if(no_ajax)
	show_parent_no_ajax();
else
	show_tb_list();

/*
function set_bind_pm()
{
	dlg.open_win("./ext/cpt_bindparam_"+cptp+".jsp?prjid="+prjid+"&cptp="+cptp+"&cpid="+cpid+"&connid="+connid,
			{title:"Binding Parameters",w:'600',h:'450'},
			['Ok',{title:'Cancel',style:"primary"}],
			[
				function(dlgw)
				{
					var bindids = dlgw.get_selected_vals();
					
					dlg.close();
				},
				function(dlgw)
				{
					dlg.close();
				}
				
			]);
}

function syn_tag_ch()
{
	var bindstr = get_bindlist_valstr();

	send_ajax("cpt_bind_ajax.jsp",{prjid:prjid,op:"syn_bind_tags",cpid:cpid,cptp:cptp,connid:connid,bindids:bindstr},function(bsucc,ret){
		if(!bsucc||ret!='succ')
		{
			dlg.msg(ret) ;
			return ;
		}
		dlg.msg("syn ok,please refresh tree to check");
	});
}
*/
</script>
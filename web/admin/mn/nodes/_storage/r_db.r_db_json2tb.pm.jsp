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
	if(!Convert.checkReqEmpty(request, out, "container_id"))
		return ;
	
	String container_id = request.getParameter("container_id") ;
UAPrj prj = UAManager.getInstance().getPrjById(container_id) ;
if(prj==null)
{
	out.print("no project node found");
	return ;
}

String prjpath = prj.getNodePath() ;
%>
<style>
.rule
{
	position: relative;
	width:98%;
	left:50px;
	border:1px solid;
	border-color: #dddddd;
	margin-top: 5px;
}

.rule .del
{
	position: absolute;
	right:2px;
	top:2px;z-index:10;
	width:20px;
	height:20px;
	color:#dddddd;
	border-color:#dddddd;
}
.rule .del:hover
{
	background-color: red;
	
}

.tb {width:90%;,margin-left:5%;font-size:12px;}
th {border:1px solid #333;}
td {border:1px solid #ccc;}
.inp {width:100px;}
.sel {width:80px;}
.del {width:30px;}

</style>
<button onclick="add_nor_col()" style="border-color:#dddddd">+Add</button>
<table class="tb">
 <thead>
  <tr>
  	<th></th>
    <th>JSON PN</th>
    <th>Column</th>
    <th >Type</th>
    <th>Title</th>
    <th >Max Length</th>
    <th>Has Index</th>
    <th></th>
  </tr>
  <tr id="pk_col">
  	<td>PK</td>
    <td><input type="text" class="jo_pn inp"/>
    	<input type="checkbox" class="bauto" lay-ignore/>auto
    </td>
    <td><input type="text" class="col inp" /></td>
    <td><select lay-ignore class="val_tp inp">
    		<option value="vt_str"     >str   </option>
			<option value="vt_int32"   >int32 </option>
			<option value="vt_int64"   >int64 </option>
    	</select></td>
    <td><input type="text" class="title inp" /></td>
    <td><input type="number" class="max_len inp" /></td>
    <td></td>
  </tr>
 </thead>
 <tbody id="nor_cols">
 </tbody>
 <tr id="temp" style="display:none" class="nor_col">
  	<td></td>
    <td><input type="text" class="jo_pn inp" /></td>
    <td><input type="text" class="col inp" /></td>
    <td>
    	<select lay-ignore class="val_tp inp">
    		<option value="vt_str"     >str   </option>
			<option value="vt_int32"   >int32 </option>
			<option value="vt_float"   >float </option>
			<option value="vt_bool"    >bool  </option>
			<option value="vt_int16"   >int16 </option>
			<option value="vt_int64"   >int64 </option>
			<option value="vt_double"  >double</option>
			<option value="vt_date"  >date</option>
    	</select>
    </td>
    <td><input type="text" class="title inp" /></td>
    <td><input type="number" class="max_len inp" /></td>
    <td><input type="checkbox" class="has_idx sel" lay-ignore/></td>
    <td><button class="del" onclick="del_nor_col(this)">X</button></td>
  </tr>
</table>

 
 <div >
</div>
<script>

var prjpath = "<%=prjpath%>" ;

var id_cc = 1 ;


function add_nor_col(jo)
{
	let html = $("#temp")[0].outerHTML ;
	let ele = $(html) ;
	ele.css("display","") ;
	let newid = "nor_col_"+id_cc;
	ele.attr("id",newid) ;
	id_cc ++ ;
	$("#nor_cols").append(ele) ;

	if(jo)
	{
		let row = $("#"+newid) ;
		row.find(".jo_pn").val(jo.jo_pn||"") ;
		row.find(".col").val(jo.col||"") ;
		row.find(".val_tp").val(jo.val_tp||"str") ;
		row.find(".title").val(jo.title||"") ;
		let mlen = jo.max_len>0?jo.max_len:"";
		row.find(".max_len").val(mlen) ;
		row.find(".has_idx").prop("checked",jo.has_idx||false) ;
	}
	
	return ele ;
}

function del_nor_col(ele)
{
	//console.log(ele) ;
	$(ele).parent().parent().remove() ;
}

function extract_col_jo(ele)
{
	let ret={} ;
	ret.jo_pn = ele.find(".jo_pn").val()||"" ;
	ret.bauto = ele.find(".bauto").prop("checked") ;
	if(ret.jo_pn=="" && !ret.bauto)
		return "JSON PN cannot empty" ;
	ret.col = ele.find(".col").val()||"" ;
	if(ret.col=="")
		return "Column cannot empty" ;
	
	ret.val_tp = ele.find(".val_tp").val()||"" ;
	ret.title = ele.find(".title").val()||"" ;
	ret.max_len = parseInt(ele.find(".max_len").val()||"-1") ;
	ret.has_idx = ele.find(".has_idx").prop("checked") ;
	
	if(ret.val_tp=='vt_str' && ret.max_len<=0)
		return "str column mast has Max Length>0" ;
	return ret ;
}

function on_after_pm_show(form)
{
	 
}


function get_chk_vals(name)
{
	let ret=[] ;
	$("input[name="+name+"]").each( function () {
	       let ob = $(this) ;
	       if(ob.prop("checked"))
	    	   ret.push(ob.val()) ;
	   });
	return ret ;
}

function get_pm_jo()
{
	let jo = {} ;
	jo.pk_col = extract_col_jo($("#pk_col")) ;
	if(typeof(jo.pk_col)=='string')
		return jo.pk_col ;
	
	let nor_cols = [] ;
	jo.nor_cols = nor_cols ;
	
	let err=null;
	$("#nor_cols").find(".nor_col").each(function(){
		let ele = $(this) ;
		let tmpjo = extract_col_jo(ele) ;
		if(typeof(tmpjo)=='string')
			return err = tmpjo ;
		nor_cols.push(tmpjo) ;
	}) ;
	if(err)
		return err ;
	//console.log(jo) ;
	return jo ;
}

function set_pm_jo(jo)
{//console.log(jo) ;
	if(!jo || !jo.pk_col)
	{
		$("#pk_col").find(".col").val("ID")
		$("#pk_col").find(".bauto").prop("checked",true);
		$("#pk_col").find(".max_len").val(30)
	}
	else
	{
		$("#pk_col").find(".col").val(jo.pk_col.col||"ID")
		$("#pk_col").find(".bauto").prop("checked",jo.pk_col.bauto||false);
		$("#pk_col").find(".jo_pn").val(jo.pk_col.jo_pn||"")
		$("#pk_col").find(".val_tp").val(jo.pk_col.val_tp||"")
		$("#pk_col").find(".title").val(jo.pk_col.title||"")
		let mlen = jo.pk_col.max_len>0?jo.pk_col.max_len:"";
		$("#pk_col").find(".max_len").val(mlen)
	}
	
	if(!jo || !jo.nor_cols || jo.nor_cols.length<=0)
	{
		add_nor_col(null) ;
	}
	else
	{
		for(let c of jo.nor_cols)
		{
			add_nor_col(c) ;
		}
	}
}

function get_pm_size()
{
	return {w:700,h:550} ;
}

//on_init_pm_ok() ;
</script>
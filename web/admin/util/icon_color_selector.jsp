<%@ page contentType="text/html;charset=UTF-8"%><%@page 
	import="java.io.*,java.util.*,org.iottree.core.*,
				org.iottree.core.util.*
		" %><%@ taglib uri="wb_tag" prefix="wbt"%><%!
		%><%
	
%><!DOCTYPE html>
<html>
<head>
<title></title>
<jsp:include page="../head.jsp">
	<jsp:param value="true" name="simple"/>
</jsp:include>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<link type="text/css" href="/_js/oc/oc.css" rel="stylesheet" />
</head>
<style>
i:hover{
color: red;
}
.btns
{
	top:0px;
	height:430px;
	width:100%;
	overflow: auto;
}
.icon_item
{
}
.icon_item:hover {
	background-color: grey;
}
.btop
{
	font-weight: bold;
	font-size:16px;
	background-color: #f2f2f2;
	height:40px;
}
</style>
<script type="text/javascript">
dlg.resize_to(700,600) ;


</script>
<body>
<div class="btop" style="">
	<div style0="color:red"><i id="sel_icon" class="fa" style="font-size: 30px"></i> <span id="sel_txt"></span>
		<wbt:g>modify,color</wbt:g><input type="color" id="sel_color" onchange="on_chg(this)"/>
	</div>
</div>
	<div class="oc-toolbar btns" id="icons_c">
<%--
for(int i = 0;i<975;i++)
{
	String fan = Integer.toHexString(0xf000+i);
	//i++;
	//if(i>=100)
	//	break;
	%>
<div title=""  class="toolbarbtn icon_item"  style="border:1px solid;" fa_icon="<%=fan%>"  onclick="select(this)"><i class="fa" style="font-size: 20px">&#x<%=fan %></i></div>
	<%
}
--%>
</div>
</body>
<script type="text/javascript">
const codes = [
	  "f26e", "f2b9", "f2ba", "f2bb", "f2bc", "f042", "f170", "f037", "f039", "f036",
	  "f038", "f270", "f0f9", "f2a3", "f13d", "f17b", "f209", "f103", "f100", "f101",
	  "f102", "f107", "f104", "f105", "f106", "f179", "f187", "f1fe", "f0ab", "f0a8",
	  "f01a", "f190", "f18e", "f01b", "f0a9", "f0aa", "f063", "f060", "f061", "f062",
	  "f047", "f0b2", "f07e", "f07d", "f2a2", "f069", "f1fa", "f29e", "f1b9", "f04a",
	  "f24e", "f05e", "f2d5", "f19c", "f080", "f02a", "f0c9", "f2cd", "f240", "f244",
	  "f243", "f242", "f241", "f236", "f0fc", "f1b4", "f1b5", "f0f3", "f0a2", "f1f6",
	  "f1f7", "f206", "f1e5", "f1fd", "f171", "f172", "f15a", "f27e", "f29d", "f293",
	  "f294", "f032", "f0e7", "f1e2", "f02d", "f02e", "f097", "f2a1", "f0b1", "f188",
	  "f1ad", "f0f7", "f0a1", "f140", "f207", "f20d", "f1ba", "f1ec", "f073", "f274",
	  "f272", "f133", "f271", "f273", "f030", "f083", "f0d7", "f0d9", "f0da", "f150",
	  "f191", "f152", "f151", "f0d8", "f218", "f217", "f20a", "f1f3", "f24c", "f1f2",
	  "f24b", "f1f1", "f1f4", "f1f5", "f1f0", "f0a3", "f0c1", "f127", "f00c", "f058",
	  "f05d", "f14a", "f046", "f13a", "f137", "f138", "f139", "f078", "f053", "f054",
	  "f077", "f1ae", "f268", "f111", "f10c", "f1ce", "f1db", "f0ea", "f017", "f24d",
	  "f00d", "f0c2", "f0ed", "f0ee", "f157", "f121", "f126", "f1cb", "f284", "f0f4",
	  "f013", "f085", "f0db", "f075", "f0e5", "f27a", "f27b", "f086", "f0e6", "f14e",
	  "f066", "f20e", "f26d", "f0c5", "f1f9", "f25e", "f09d", "f283", "f125", "f05b",
	  "f13c", "f1b2", "f1b3", "f0c4", "f0f5", "f0e4", "f210", "f1c0", "f2a4", "f03b",
	  "f1a5", "f108", "f1bd", "f219", "f1a6", "f155", "f192", "f019", "f17d", "f2c2",
	  "f2c3", "f16b", "f1a9", "f282", "f044", "f2da", "f052", "f141", "f142", "f1d1",
	  "f0e0", "f003", "f2b6", "f2b7", "f199", "f299", "f12d", "f2d7", "f153", "f0ec",
	  "f12a", "f06a", "f071", "f065", "f23e", "f08e", "f14c", "f06e", "f070", "f1fb",
	  "f2b4", "f09a", "f230", "f082", "f049", "f050", "f1ac", "f09e", "f182", "f0fb",
	  "f15b", "f1c6", "f1c7", "f1c9", "f1c3", "f1c5", "f1c8", "f016", "f1c1", "f1c4",
	  "f15c", "f0f6", "f1c2", "f008", "f0b0", "f06d", "f134", "f269", "f2b0", "f024",
	  "f11e", "f11d", "f0c3", "f16e", "f0c7", "f07b", "f114", "f07c", "f115", "f031",
	  "f280", "f286", "f211", "f04e", "f180", "f2c5", "f119", "f1e3", "f11b", "f0e3",
	  "f154", "f22d", "f265", "f260", "f261", "f06b", "f1d3", "f1d2", "f09b", "f113",
	  "f092", "f296", "f184", "f000", "f2a5", "f2a6", "f0ac", "f1a0", "f0d5", "f2b3",
	  "f0d4", "f1ee", "f19d", "f2d6", "f0c0", "f0fd", "f1d4", "f255", "f258", "f0a7",
	  "f0a5", "f0a4", "f0a6", "f256", "f25b", "f25a", "f257", "f259", "f2b5", "f292",
	  "f0a0", "f1dc", "f025", "f004", "f08a", "f21e", "f1da", "f015", "f0f8", "f254",
	  "f251", "f252", "f253", "f250", "f27c", "f13b", "f246", "f2c1", "f20b", "f03e",
	  "f2d8", "f01c", "f03c", "f275", "f129", "f05a", "f156", "f16d", "f26b", "f224",
	  "f208", "f033", "f1aa", "f1cc", "f084", "f11c", "f159", "f1ab", "f109", "f202",
	  "f203", "f06c", "f212", "f094", "f149", "f148", "f1cd", "f0eb", "f201", "f0e1",
	  "f08c", "f2b8", "f17c", "f03a", "f022", "f0cb", "f0ca", "f124", "f023", "f175",
	  "f177", "f178", "f176", "f2a8", "f0d0", "f076", "f064", "f112", "f122", "f183",
	  "f279", "f041", "f278", "f276", "f277", "f222", "f227", "f229", "f22b", "f22a",
	  "f136", "f20c", "f23a", "f0fa", "f2e0", "f11a", "f223", "f2db", "f130", "f131",
	  "f068", "f056", "f146", "f147", "f289", "f10b", "f285", "f0d6", "f186", "f21c",
	  "f245", "f001", "f22c", "f1ea", "f247", "f248", "f263", "f264", "f23d", "f19b",
	  "f26a", "f23c", "f03b", "f18c", "f1fc", "f1d8", "f1d9", "f0c6", "f1dd", "f04c",
	  "f28b", "f28c", "f1b0", "f1ed", "f040", "f14b", "f295", "f095", "f098", "f200",
	  "f2ae", "f1a8", "f1a7", "f0d2", "f231", "f0d3", "f072", "f04b", "f144", "f01d",
	  "f1e6", "f067", "f055", "f0fe", "f196", "f2ce", "f011", "f02f", "f288", "f12e",
	  "f1d6", "f029", "f128", "f059", "f29c", "f2c4", "f10d", "f10e", "f1d0", "f074",
	  "f2d9", "f1b8", "f1a1", "f281", "f1a2", "f021", "f25d", "f18b", "f01e", "f079",
	  "f018", "f135", "f0e2", "f158", "f143", "f267", "f28a", "f002", "f010", "f00e",
	  "f213", "f233", "f1e0", "f1e1", "f14d", "f045", "f132", "f21a", "f214", "f290",
	  "f291", "f07a", "f2cc", "f090", "f2a7", "f08b", "f012", "f215", "f0e8", "f216",
	  "f17e", "f198", "f1de", "f1e7", "f118", "f2ab", "f2ac", "f2ad", "f2dc", "f0dc",
	  "f15d", "f15e", "f160", "f161", "f0de", "f0dd", "f162", "f163", "f1be", "f197",
	  "f110", "f1b1", "f1bc", "f0c8", "f096", "f18d", "f16c", "f005", "f089", "f123",
	  "f006", "f1b6", "f1b7", "f048", "f051", "f0f1", "f249", "f24a", "f04d", "f28d",
	  "f28e", "f21d", "f0cc", "f1a4", "f1a3", "f12c", "f239", "f0f2", "f185", "f2dd",
	  "f12b", "f0ce", "f10a", "f0ae", "f2c6", "f26c", "f1d5", "f120", "f034", "f035",
	  "f00a", "f009", "f00b", "f2b2", "f2c7", "f2cb", "f2ca", "f2c9", "f2c8", "f08d",
	  "f165", "f088", "f087", "f164", "f145", "f057", "f05c", "f2d3", "f2d4", "f043",
	  "f204", "f205", "f25c", "f238", "f225", "f1f8", "f014", "f1bb", "f181", "f262",
	  "f091", "f0d1", "f195", "f1e4", "f173", "f174", "f1e8", "f099", "f081", "f0e9",
	  "f0cd", "f29a", "f09c", "f13e", "f093", "f287", "f007", "f2bd", "f2be", "f0f0",
	  "f2c0", "f234", "f21b", "f235", "f221", "f226", "f228", "f237", "f2a9", "f2aa",
	  "f03d", "f27d", "f194", "f1ca", "f189", "f2a0", "f027", "f026", "f028", "f1d7",
	  "f18a", "f232", "f193", "f29b", "f1eb", "f266", "f2d0", "f2d1", "f2d2", "f17a",
	  "f19a", "f297", "f2de", "f298", "f0ad", "f168", "f169", "f23b", "f19e", "f1e9",
	  "f2b1", "f167", "f16a", "f166"
	];
function show_icons()
{
	let ss = "" ;
	for(let c of codes)
	{
		ss += `<div title=""  class="toolbarbtn icon_item"  style="border:1px solid;" fa_icon="\${c}"  onclick="select(this)"><i class="fa" style="font-size: 20px">&#x\${c}</i></div>`;
	}
	$("#icons_c").html(ss) ;
}
show_icons()
let pm = dlg.get_opener_opt("pm") ;
if(pm)
{
	//console.log(pm) ;
	$("#sel_icon").html("&#x"+pm.icon) ;
	$("#sel_icon").css("color",pm.color) ;
	$("#sel_color").val(pm.color) ;
}
else
	pm={color:'#000'}

var cur_ob = null ;

function select(ob)
{
	cur_ob = $(ob) ;
	let icon = cur_ob.attr("fa_icon") ;
	$("#sel_icon").html("&#x"+icon) ;
	$("#sel_txt").html(icon) ;
	pm = {icon:icon,color:pm.color} ;
}

function on_chg(ele)
{
	let color = $(ele).val() ;
	if(pm)
		pm.color = color ;
	$("#sel_icon").css("color",pm.color) ;
}

function do_submit(cb)
{
	if(!pm)
	{
		cb(false,"<wbt:g>pls,select</wbt:g>")
		return ;
	}
	cb(true,pm) ;
}
</script>
</html>

if(typeof(layui)=='object')
{
	layui.use('element', function(){
		  var element = layui.element;
		  
		  //…
		});
}


var hcount = 0 ;

function outline()
{
	$("a").css("cursor","pointer") ;
	//var idstr = "" ;
	var listr = "" ;
	$("h1,h2,h3,h4,h5,h6").each(function(){
		hcount ++ ;
		var tmpid = "h_"+hcount ;
		var ob = $(this) ;
		ob.attr("id",tmpid) ;
	    var tt = ob.html();
	    var htag = ob[0].tagName ;
	    var hn = parseInt(htag.substr(1)) ;
	    var sps = "" ;
	    for(var k = 0 ; k < hn ; k ++)
	    	sps += "&nbsp;";
	    //idstr += htag + " "+tt ;
	    listr += "<li><a href='#"+tmpid+"'><cite>"+sps+tt+"</cite></a></li>\r\n" ;
	  });
	var lpos = ($(window).width()-180)+"px" ;
	$("#outline_list").html(listr) ;
	$("#outline_list").css("overflow-y","auto") ;
	$("#outline_list").css("height",($(window).height()-120)+"px") ;
	//console.log(listr) ;
	
	$("#outline_div").css("left",lpos);
	$("#outline_head").click(function(){
		if("1"==$("#outline_list").attr("doc_hide"))
		{
			$("#outline_list").css("display","");
			$("#outline_list").attr("doc_hide","0"); 
		}
		else
		{
			$("#outline_list").css("display","none");
			$("#outline_list").attr("doc_hide","1"); 
		}
		
	});
}

outline() ;
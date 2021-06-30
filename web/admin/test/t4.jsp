<%@ page contentType="text/html;charset=UTF-8" isELIgnored="false"%>
<%@ page
	import="java.util.*,
				java.io.*,
				java.util.*,
				com.google.common.base.*,
				java.net.*"%>
<%
	float r = 0.5f ;
System.out.println(Joiner.on(',').join(Arrays.asList(1,5,6)));
Splitter sp = Splitter.on(',').omitEmptyStrings().trimResults();
for(String s:sp.split("asdf, , asdf  aa"))
{
	System.out.println("--"+s);
}
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title></title>
<style type="text/css">
.ttt
{
left:0px;
top:0px;
border:3px solid #f00;
position:absolute;
transform: scale(<%=r%>,<%=r%>);
-ms-transform: scale(<%=r%>,<%=r%>); /* IE 9 */
-webkit-transform: scale(<%=r%>,<%=r%>); /* Safari and Chrome */
}
</style>
</head>
<body>
<div class="ttt" style="overflow: auto;">
adafdasfasdfasdfasf<br>
<span style="font-size: 50px">asdfafdasdfaswdf</span><br>

asdfasdfasdfasdf
</div>
</body>

</html>
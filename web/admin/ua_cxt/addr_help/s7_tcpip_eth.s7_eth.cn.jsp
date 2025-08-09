<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%><%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
	java.io.*,
	java.util.*,
	org.json.*,
	java.net.*,
	java.util.*"%><%

%> 
<html>
<head>
<title>Tag Address Helper </title>
<jsp:include page="../../head.jsp">
	<jsp:param value="true" name="simple"/>
</jsp:include>
<style type="text/css">
body {margin:15px;}
h4 {margin-top:3px;}
</style>
</head>
<body>
<h2>标准 S7-300/400/1200/1500 项语法</h2>
<hr style="height:2px;border:none;background:#333;margin:2px 0;">
<h3>地址语法</h3>
<h4>输入、输出、外设、标志内存类型</h4> 
<pre>
<内存类型><S7 数据类型Closed此参数用于指定在物理设备中找到此标记时，该标记的数据格式。在大多数情况下，这也是数据返回客户端Closed一种软件程序，用于联系同一台计算机或另一台计算机上的服务器Closed一款软件应用程序，用于在设备、控制器或数据源与客户端应用程序之间进行通信。服务器只能响应客户端发出的请求。例如，HMI (客户端) 请求从 OPC 服务器访问特定进程的最新值。客户端也有关。软件程序及从中获取数据。客户端提出请求，服务器完成该请求。例如，客户端将会是连接到邮件服务器的电子邮件程序，或者是连接到 Web 服务器的 Internet 浏览Closed允许用户查看可用项的界面。器客户端。就 OPCClosed通过开放标准实现开放互连 (OPC) 是一套基于微软 OLE/COM 技术的标准接口。OPC 标准接口的应用使自动化/控制应用程序、字段系统/设备等之间的互操作性成为可能。 而言，HMIClosed人机界面是一款软件应用程序 (通常为图形用户界面或 GUI)，用于向操作员提供进程状态信息，并接受和执行操作员的控制指令。它还可以解释工厂信息，并指导操作员与系统进行交互。又可缩写为 MMIClosed人机界面是一款软件应用程序 (通常为图形用户界面)，用于向操作员提供进程状态信息，以及接受和执行操作员的控制指令。它还可以解释工厂信息，并指导操作员与系统进行交互。又可缩写为 HMI。。 将会是连接到 OPC 服务器的客户端应用程序。服务器也有关。这是一种软件程序，用于联系（同一台计算机或不同计算机上的）服务器软件程序及从中获取数据。客户端提出请求，服务器完成该请求。另请参阅：服务器时的格式。数据类型设置是通信驱动程序如何读取并将数据写入设备的重要组成部分。对于多数驱动程序，数据特定部分的数据类型完全固定，而且驱动程序知道在读取设备数据时需使用何种格式。但是，在某些情况下，对设备数据的解释很大程度上由用户决定。以一个使用 16 位Closed二进制数位。数据寄存器的设备为例。通常会指明数据为“短整型”或“字”。许多基于寄存器的设备还支持跨越两个寄存器的值。在这些情况下，双寄存器值可能是长整型、双字型或浮点型。如果正在使用的驱动程序支持此级别的灵活性，用户则须告知驱动程序如何读取此标记的数据。通过选择相应的数据类型来告知驱动程序读取一个、两个、四个、八个或十六个寄存器或者可能的布尔值。驱动程序控制所选取的数据格式。><地址>
<内存类型><S7 数据类型><地址><.位>
<内存类型><S7 数据类型><地址><.字符串长度>*
</pre>
 

<h4>计时器和计数器内存类型</h4>
<pre>
<内存类型><地址>
</pre>
 

<h4>DB 内存类型</h4>
<pre>
DB<数字>,<S7 数据类型><地址>
DB <数字>，<S7 数据类型><地址><.位>
DB <数字>，<S7 数据类型><地址><.字符串长度>*
</pre>
 

<pre>
其中，<数字> 的范围介于 1 至 65535 之间。

*适用于支持字符串的 S7 数据类型。字符串长度的范围为 0&lt;n&lt;= 932，S7 数据类型字符串 (范围为 0&lt;n&lt;= 254) 除外。
 
</pre>
 

 另请参阅：示例、字符串支持。 

 

<h3>内存类型</h3>
<table class="layui-table">
	<thead>
		<tr>
<td>内存类型</td>
<td>说明</td>
 <td>地址范围</td>
 <td>数据类型</td>
 <td>访问</td>
		</tr>
	</thead>
	<tbody>
		<tr><td>I</td><td>输入</td> <td colspan="2" rowspan="4">取决于 S7 数据类型</td><td> 读/写</td></tr>
		<tr><td>Q</td><td>输出</td><td> 读/写</td></tr>

		<tr><td>M</td><td>标志内存</td><td> 读/写</td></tr>
		<tr><td>DB</td><td>数据块</td><td> 读/写</td></tr>
		<tr><td>T</td><td> 计时器</td> <td>T0-T65535</td> <td> 双字型、长整型</td><td> 读/写</td></tr>
		<tr><td>C</td><td>计数器</td> <td>C0-C65535</td> <td>字、短整型</td><td> 读/写</td></tr>
	</tbody>
</table>


 另请参阅：示例

<h3>S7 数据类型</h3>

S7 数据类型可用于强制转换标记的数据类型。它不适用于计时器和计数器。默认数据类型以粗体显示。

<table class="layui-table">
	<thead>
		<tr>
<td>S7 数据类型</td>
<td>说明</td>
 <td>地址范围</td>
 <td>数据类型</td>
		</tr>
	</thead>
	<tbody>
		<tr><td rowspan="2">B 字节</td><td rowspan="2">无符号字节</td> <td>B0-B65535<br>BYTE0-BYTE65535</td><td>字节、字符</td></tr>
		<tr><td>B0.b-B65535.b<br>BYTE0.b-BYTE65535.b<br>.b 为位数 0-7</td><td> 布尔型</td></tr>
		
		<tr><td rowspan="2">C 字符</td><td rowspan="2">有符号字节</td> <td>C0-C65535</td><td>字节、字符</td></tr>
		<tr><td>C0.b-C65535.b<br><br>.b 为位数 0-7</td><td> 布尔型</td></tr>
		
		<tr><td rowspan="2">D DWORD</td><td rowspan="2">无符号双精度字</td> <td>D0-D65532</td><td>双字型、长整型、浮点型</td></tr>
		<tr><td>D0.b-D65532.b<br>.b 为位数 0-31</td><td> 布尔型</td></tr>
<%--
		<tr><td >日期</td><td >S7 日期<br>
存储为 WORD，从 1990 年 1 月 1 日开始，以 1 天为增量递增。<br>
显示为 "yyyy-mm-dd" 字符串格式，范围介于 "1990-01-01" 至 "2168-12-31" 之间。<br>
读/写
</td> <td>DATE0-DATE65534</td><td>字符串</td></tr>
 --%>
		<tr><td rowspan="2">DI</td><td rowspan="2">有符号双精度字</td> <td>DI0-DI65532</td><td>双字型、长整型、浮点型</td></tr>
		<tr><td>DI0.b-DI65532.b<br>.b 为位数 0-31</td><td> 布尔型</td></tr>
		
		
		
		<tr><td rowspan="2">I</td><td rowspan="2">有符号字</td> <td>I0-I65534</td><td>字、短整型</td></tr>
		<tr><td>I0.b-I65534.b<br>.b 为位数 0-15</td><td> 布尔型</td></tr>
		
		<tr><td >REAL</td><td >IEEE 浮点数</td> <td>REAL0-REAL65532</td><td>浮点型</td></tr>
		
		<tr><td >STRING</td><td >S7字符串</td> <td>STRING0.n-STRING65532.n<br>.n是字符串长度 (1-254)</td><td>字符串</td></tr>
		
		<tr><td rowspan="2">W 字</td><td rowspan="2">无符号字</td> <td>W0-W65534</td><td>字、短整型</td></tr>
		<tr><td>W0.b-W65534.b<br>.b 为位数 0-15</td><td> 布尔型</td></tr>
		
		<tr><td >X</td><td >位</td> <td>X0.b-X65535.b<br>.b 为位数 0-7</td><td>布尔型</td></tr>
		
	</tbody>
</table>

 
</body>
</html>
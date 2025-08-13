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
<h2>标准 S7-200 Smart/S7-300/400/1200/1500 项语法</h2>
<hr style="height:2px;border:none;background:#333;margin:2px 0;">
<h3>地址语法</h3>
<h4>输入、输出、外设、标志内存类型</h4> 
<pre>
<内存类型><S7 数据类型><地址>
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

 
<br><br>
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
<br><br>
<h3>S7 数据类型</h3>

<pre>
S7 数据类型可用于强制转换标记的数据类型。它不适用于计时器和计数器。默认数据类型以粗体显示。
</pre>

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
<pre>
*这些是原始字符串，其结构和用途与 STEP 7 字符串数据类型有所不同。

 修改字型、短整型、双字型和长整型时请小心，原因在于每个地址均会在设备内某一字节偏移量处开始。因此，字 MW0 和 MW1 在字节 1 处重叠。写入 MW0 还会修改保存在 MW1 中的值。同样，双字型和长整型也会重叠。建议使用这些内存类型，以避免发生重叠。例如，可以使用双字型 MD0、MD4、MD8 等以防出现重叠字节。
</pre>

 另请参阅：示例

 <br><br>
<h3>字符串支持</h3>

<h4>原始字符串</h4> 

<pre>
对于地址 DBx,By.n @ 字符串，读取和写入的字符串值存储在字节偏移量 y 处。
</pre>
<table class="layui-table">
	<tbody>
		<tr><td>y</td><td>y+1</td> <td>y+2</td><td>...</td><td>y+n-1</td></tr>
		<tr><td>''</td><td>''</td> <td>''</td><td>...</td><td>''</td></tr>
	</tbody>
</table>
 
 <pre>
 原始字符串为空终止字符串。如果最大字符串长度为 10，并且写入了 3 个字符，则第 4 个字符将设置为 NULL，而第 5-10 个字符保持不变。
</pre>
 
<b>注意：</b><pre>对于原始字符串，请求的字节总数不能超过协商 PDU大小的数据部分。超过协商 PDU 大小的原始字符串可能无法读写。</pre>

 

<h4>字符串支持</h4>

<pre>
字符串子类型遵循 STEP 7 字符串数据类型定义。字符串 S7 数据类型的语法为 STRINGy.n，其中 y 是字节偏移量，n 是最大字符串长度。如果未指定 n，那么最大字符串长度将为 254 个字符。读取和写入的字符串值存储在数据块 x 中的字节偏移量 y+2 处。实际字符串长度会在每次写入时根据正在写入的字符串长度进行更新。
</pre>
 
 <table class="layui-table">
	<tbody>
		<tr><td>y</td><td>y+1</td> <td>y+2</td><td>y+3</td><td>y+4</td><td>...</td><td>y+2+n-1</td></tr>
		<tr><td>最大字符串长度 (n)</td><td>实际字符串长度</td><td>''</td><td>''</td><td>''</td><td>...</td><td>''</td></tr>
	</tbody>
</table>

<b>注意：</b> 
<br>
<pre>
1. 字符串以空值填充。如果最大字符串长度为 10，且已写入 3 个字符，则字符 4-10 设置为空值。
 
2. 如果协商 PDU 为 240，则无法读取长度大于 222 的 STEP 7 字符串，且无法写入长度大于 212 的字符串。
</pre>
 
<br>
<h3>十六进制字符串</h3>

<pre>
HEXSTRING 子类型特定于 Siemens TCP/IP Ethernet 驱动程序。HEXSTRING 子类型的语法是 HEXSTRINGy.n，其中 y 是字节偏移量，n 是长度。必须在 1 到 932 的范围内指定 n 值。字符串是 HEXSTRING 标记的唯一有效数据类型。

赋给 HEXSTRING 的值必须是偶数个字符。没有填充符，因此必须指定整个字符串。例如，定义为 DB1,STRING0.10 的标记 HexStr，使用 10 个字节的存储量，显示长度为 20。要赋值，字符串长度必须为 20 个字符，并且只包Closed表示正在进行请求的线上的数据字节流。数据包大小受到限制。含有效的十六进制字符。例如：此标记的有效十六进制字符串为 "56657273696f6E353137"。

数组Closed地址具有指定数组元素的客户端/服务器数组标记。例如，ARRAYTAG [0] {5}。支持

[行数][列数] 符号会附加到地址中，以指定数组 (如 MW0[2][5])。如果未指定任何行，则假定行数为 1。不支持布尔数组和字符串数组。

对于字型、短整型和 BCD 数组，基址 + (行数 * 列数 * 2) 不能超过 65536。请记住，数组的元素是位于字边界上的字。例如，IW0[4] 将返回 IW0、IW2、IW4 和 IW6。

对于浮点型、双字型、长整型和长 BCD 数组，基 + (行数 * 列数 * 4) 不能超过 65536。请记住，数组的元素是位于双字边界上的双字。例如，ID0[4] 将返回 ID0、ID4、ID8、ID12。

对于所有数组，请求的字节总数不能超过协商 PDU 大小的数据部分。例如，对于 960 字节 PDU 大小，可读写的最大单个数组是 932 字节。超过协商 PDU 大小的数组可能无法读写。
</pre>
 

<br>
<h3>计时器</h3>

<pre>
Siemens TCP/IP Ethernet 驱动程序 会根据 Siemens S5 时间格式自动缩放 T 值。计时器数据在 PLC中作为字进行存储，但在驱动程序中会换算为双字。返回的值将根据相应的 Siemens 时基进行缩放。因此，这些值始终作为毫秒计数返回。当写入 T 内存类型时，还会应用 Siemens 时基。要为控制器中的计时器赋值，请将所需的值作为毫秒计数写入相应的计时器。
</pre>
 
 <br>
<h3>计数器</h3>
<pre>
针对 C 内存类型返回的值将自动转换为 BCD 值。
</pre>

<br>
<h3>示例</h3>
<table class="layui-table">
	<thead>
		<tr>
<td>S7 数据类型</td>
<td>数据类型</td>
 <td>输入</td>
 <td>标志</td>
 <td>数据块</td>
		</tr>
	</thead>
	<tbody>
		<tr><td rowspan="2">B 字节</td><td>字节</td> <td>IB0</td><td>MB0</td><td>DB1,B0</td></tr>
		<tr><td>布尔型</td><td>IB0.7</td><td>MB0.7</td><td>DB1,B0.7</td></tr>
		
		<tr><td rowspan="2">C 字符</td><td>字符</td> <td>IC0</td><td>MC0</td><td>DB1,C0</td></tr>
		<tr><td>布尔型</td><td>IC0.7</td><td>MC0.7</td><td>DB1,C0.7</td></tr>
		
		<tr><td rowspan="2">D DWORD</td><td>双字型</td> <td>ID0</td><td>MD0</td><td>DB1,D0</td></tr>
		<tr><td>布尔型</td><td>ID0.31</td><td>MD0.31</td><td>DB1,D0.31</td></tr>
		
		<tr><td rowspan="2">DI</td><td>长整型</td> <td>IDI0</td><td>MDI0</td><td>DB1,DI0</td></tr>
		<tr><td>布尔型</td><td>IDI0.31</td><td>MDI0.31</td><td>DB1,DI0.31</td></tr>
		
		<tr><td rowspan="2">I</td><td>短整型</td> <td>II0</td><td>MI0</td><td>DB1,I0</td></tr>
		<tr><td>布尔型</td><td>II0.15</td><td>MI0.15</td><td>DB1,I0.15</td></tr>
		
		<tr><td rowspan="1">REAL</td><td>浮点数</td> <td>IREAL0</td><td>MREAL0</td><td>DB1,REAL0</td></tr>
		
		<tr><td rowspan="1">STRING</td><td>字符串</td> <td>ISTRING0.10</td><td>MSTRING0.10</td><td>DB1,STRING0.10</td></tr>
		
		<tr><td rowspan="2">W</td><td>字</td> <td>IW0</td><td>MW0</td><td>DB1,W0</td></tr>
		<tr><td>布尔型</td><td>IW0.15</td><td>MW0.15</td><td>DB1,W0.15</td></tr>
		
		<tr><td rowspan="1">X</td><td>布尔型</td> <td>IX0.7</td><td>MX0.7</td><td>DB1,X0.7</td></tr>
		
	</tbody>
</table>

 <b>注意：</b><pre>数据块中的原子类型标记的偏移由 Step 7 中的“地址”列表示，如上所示。在 Siemens TIA Portal 编程环境中，此偏移由“偏移”列表示。</pre>
 
</body>
</html>
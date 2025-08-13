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
<h2>Standard S7-200 Smart/S7-300/400/1200/1500 Item Syntax</h2>
<hr style="height:2px;border:none;background:#333;margin:2px 0;">
<h3>Address Syntax</h3>
<h4>Input, Output, Peripheral, Flag Memory Types </h4> 
<pre>
&lt;memory type>&lt;S7 data type>&lt;address>
&lt;memory type>&lt;S7 data type>&lt;address>&lt;.bit>
&lt;memory type>&lt;S7 data type>&lt;address>&lt;.string length>*
</pre>
 
<br>
<h4>Timer and Counter Memory Types </h4>
<pre>
&lt;memory type>&lt;address>
</pre>
 
<br>
<h4>DB Memory Type</h4>
<pre>
DB&lt;num>,&lt;S7 data type>&lt;address>
DB&lt;num>,&lt;S7 data type>&lt;address>&lt;.bit>
DB&lt;num>,&lt;S7 data type>&lt;address>&lt;.string length>*
</pre>
<br>
where &lt;num> ranges from 1 to 65535. 
<pre>
*Applies to S7 data types that support string. String length can vary from 0<n<= 932, with the exception of S7 data type string (which can vary from 0<n<= 254). 
</pre>
 
 See Also: Examples,String Support. 
 
<br><br>
<h3>Memory Types</h3>
<table class="layui-table">
	<thead>
		<tr>
<td>Memory Type</td>
<td>Description</td>
 <td>Address Range</td>
 <td>Data Type</td>
 <td>Access</td>
		</tr>
	</thead>
	<tbody>
		<tr><td>I</td><td>Inputs</td> <td colspan="2" rowspan="4">Dependent on S7 Data Type</td><td>Read/Write</td></tr>
		<tr><td>Q</td><td>Outputs</td><td>Read/Write</td></tr>

		<tr><td>M</td><td>Flag Memory</td><td>Read/Write</td></tr>
		<tr><td>DB</td><td>Data Blocks</td><td>Read/Write</td></tr>
		<tr><td>T</td><td> Timers</td> <td>T0-T65535</td> <td> DWord,Long</td><td> Read/Write</td></tr>
		<tr><td>C</td><td>Counters</td> <td>C0-C65535</td> <td>Word,Short</td><td>Read/Write</td></tr>
	</tbody>
</table>


 See Also: Examples
 
<br><br>
<h3>S7 Data Types</h3>

<pre>
The S7 data type is used to coerce the data type for a tag. It does not apply to Timers and Counters. 
</pre>

<table class="layui-table">
	<thead>
		<tr>
<td>S7 Data Type</td>
<td>Description</td>
 <td>Address Range</td>
 <td>Data Type</td>
		</tr>
	</thead>
	<tbody>
		<tr><td rowspan="2">B Byte</td><td rowspan="2">Unsigned Byte</td> <td>B0-B65535<br>BYTE0-BYTE65535</td><td>Byte,Char</td></tr>
		<tr><td>B0.b-B65535.b<br>BYTE0.b-BYTE65535.b<br>.b is Bit Number 0-7</td><td>Boolean</td></tr>
		
		<tr><td rowspan="2">C Char</td><td rowspan="2">Signed Byte</td> <td>C0-C65535</td><td>Byte,Char</td></tr>
		<tr><td>C0.b-C65535.b<br><br>.b is Bit Number 0-7</td><td> Boolean</td></tr>
		
		<tr><td rowspan="2">D DWORD</td><td rowspan="2">Unsigned Double Word</td> <td>D0-D65532</td><td>DWord,Long,Float</td></tr>
		<tr><td>D0.b-D65532.b<br>.b is Bit Number 0-31</td><td> Boolean</td></tr>
<%--
		<tr><td >日期</td><td >S7 日期<br>
存储为 WORD，从 1990 年 1 月 1 日开始，以 1 天为增量递增。<br>
显示为 "yyyy-mm-dd" 字符串格式，范围介于 "1990-01-01" 至 "2168-12-31" 之间。<br>
读/写
</td> <td>DATE0-DATE65534</td><td>字符串</td></tr>
 --%>
		<tr><td rowspan="2">DI</td><td rowspan="2">Signed Double Word</td> <td>DI0-DI65532</td><td>DWord,Long,Float</td></tr>
		<tr><td>DI0.b-DI65532.b<br>.b is Bit Number 0-31</td><td>Boolean</td></tr>
		
		
		
		<tr><td rowspan="2">I</td><td rowspan="2">Signed Word</td> <td>I0-I65534</td><td>Word,Short</td></tr>
		<tr><td>I0.b-I65534.b<br>.b is Bit Number 0-15</td><td>Boolean</td></tr>
		
		<tr><td >REAL</td><td >IEEE Float</td> <td>REAL0-REAL65532</td><td>Float</td></tr>
		
		<tr><td >STRING</td><td >S7 String</td> <td>STRING0.n-STRING65532.n<br>.n is string length (1-254)</td><td>String</td></tr>
		
		<tr><td rowspan="2">W Word</td><td rowspan="2">Unsigned Word</td> <td>W0-W65534</td><td>Word,Short</td></tr>
		<tr><td>W0.b-W65534.b<br>.b is Bit Number 0-15</td><td>Boolean</td></tr>
		
		<tr><td >X</td><td >位</td> <td>X0.b-X65535.b<br>.b is Bit Number 0-7</td><td>Boolean</td></tr>
	</tbody>
</table>
<pre>
*These are raw strings that differ in structure and usage from the STEP 7 string data type.

Use caution when modifying Word, Short, DWord, and Long type as each address starts at a byte offset within the device. Therefore, Words MW0 and MW1 overlap at byte 1. Writing to MW0 will also modify the value held in MW1. Similarly, DWord, and Long types can also overlap. It is recommended that these memory types be used so that overlapping does not occur. For example, DWord MD0, MD4, MD8, and so on can be used to prevent overlapping bytes.
</pre>

 See Also: Examples

 <br><br>
<h3>String Support</h3>

<h4>Raw Strings </h4> 

<pre>
For an address DBx,By.n @ string, string values read and written are stored at byte offset y.
</pre>
<table class="layui-table">
	<tbody>
		<tr><td>y</td><td>y+1</td> <td>y+2</td><td>...</td><td>y+n-1</td></tr>
		<tr><td>''</td><td>''</td> <td>''</td><td>...</td><td>''</td></tr>
	</tbody>
</table>
 
 <pre>
Raw strings are null terminated. If the maximum string length is 10 and 3 characters are written, the fourth character is set to NULL, while characters 5-10 are left untouched.
</pre>
 
<b>Note: </b><pre>For raw strings, the total number of bytes requested cannot exceed the data portion of the negotiated PDUClosedProtocol Design Utility size. If raw strings exceed the negotiated PDU size, they may fail to be read or written.</pre>


<h4>String Support</h4>

<pre>
The string subtype follows the STEP 7 string data type definition. The syntax for the string S7 data type is STRINGy.n where y is the Byte offset, and n is the maximum string length. If n is not specified, the maximum string length will be 254 characters. String values read and written are stored at byte offset y+2 in data block x. The actual string length gets updated with every write based on the string length of the string being written.
</pre>
 
 <table class="layui-table">
	<tbody>
		<tr><td>y</td><td>y+1</td> <td>y+2</td><td>y+3</td><td>y+4</td><td>...</td><td>y+2+n-1</td></tr>
		<tr><td>maximum string length (n)</td><td>actual string length</td><td>''</td><td>''</td><td>''</td><td>...</td><td>''</td></tr>
	</tbody>
</table>

<b>Notes:</b> 
<br>
<pre>
1. String strings are NULL padded. If the maximum string length is 10 and 3 characters are written, characters 4-10 are set to NULL. 
 
2. If a PDU of 240 is negotiated, STEP 7 strings with a length greater than 222 may fail to be read and strings with a length greater than 212 may fail to be written.
</pre>
 
<br>
<h3>Hex Strings</h3>

<pre>
The HEXSTRING subtype is specific to the Siemens TCP/IPClosedTransmission Control Protocol/Internet Protocol is the basic communication language or protocol of the Internet. Ethernet Driver. The syntax for the HEXSTRING subtype is HEXSTRINGy.n, where y is the byte offset and n is the length. The n value must be specified in the range of 1 through 932. String is the only valid data type for a HEXSTRING tag.

 

The value assigned to a HEXSTRING must be an even number of characters. There is no padding, so the entire string must be specified. For example, tag HexStr defined as DB1,STRING0.10 uses 10 bytes of storage and has a display length of 20. To assign a value, the string must be 20 characters long and contain only valid hexadecimal characters. An example valid hex string for this tag is “56657273696f6E353137”.

 

Array Support

The [rows][cols] notation is appended to an address to specify an array (such as MW0[2][5]). If no rows are specified, row count of 1 is assumed. Boolean arrays and string arrays are not supported.

 

For Word, Short, and BCD arrays, the base address + (rows * cols * 2) cannot exceed 65536. Keep in mind that the elements of the array are words, located on a word boundary. For example, IW0[4] would return IW0, IW2, IW4, and IW6.

 

For Float, DWord, Long, and Long BCD arrays, the base address + (rows * cols * 4) cannot exceed 65536. Keep in mind that the elements of the array are DWord, located on a DWord boundary. For example, ID0[4] will return ID0, ID4, ID8, ID12.

 

For all arrays, the total number of bytes requested cannot exceed the data portion of the negotiated PDU size. For example, for a 960-byte PDU size, the largest single array that may be read or written is 932 bytes. If arrays exceed the negotiated PDU size, they may fail to be read or written.

 


</pre>
 

<br>
<h3>Timers</h3>

<pre>
The Siemens TCP/IP Ethernet Driver automatically scales T values based on the Siemens S5 time format. Timer data is stored as a Word in the PLC but scaled to a DWord in the driver. The value returned will already be scaled using the appropriate Siemens time base. As a result, the values are always returned as a count of milliseconds. When writing to T memory, the Siemens time base will also be applied. To assign a value to a timer in the controller, write the desired value as a count of milliseconds to the appropriate timer.
</pre>
 
 <br>
<h3>Counters</h3>
<pre>
The value returned for C memory will automatically be converted to a BCD value.
</pre>

<br>
<h3>Examples</h3>
<table class="layui-table">
	<thead>
		<tr>
<td>S7 Data Type</td>
<td>Data Type</td>
 <td>Input</td>
 <td>Flags</td>
 <td>Data Blocks</td>
		</tr>
	</thead>
	<tbody>
		<tr><td rowspan="2">B Byte</td><td>Byte</td> <td>IB0</td><td>MB0</td><td>DB1,B0</td></tr>
		<tr><td>Boolean</td><td>IB0.7</td><td>MB0.7</td><td>DB1,B0.7</td></tr>
		
		<tr><td rowspan="2">C Char</td><td>Char</td> <td>IC0</td><td>MC0</td><td>DB1,C0</td></tr>
		<tr><td>Boolean</td><td>IC0.7</td><td>MC0.7</td><td>DB1,C0.7</td></tr>
		
		<tr><td rowspan="2">D DWORD</td><td>DWord</td> <td>ID0</td><td>MD0</td><td>DB1,D0</td></tr>
		<tr><td>Boolean</td><td>ID0.31</td><td>MD0.31</td><td>DB1,D0.31</td></tr>
		
		<tr><td rowspan="2">DI</td><td>Long</td> <td>IDI0</td><td>MDI0</td><td>DB1,DI0</td></tr>
		<tr><td>Boolean</td><td>IDI0.31</td><td>MDI0.31</td><td>DB1,DI0.31</td></tr>
		
		<tr><td rowspan="2">I</td><td>Short</td> <td>II0</td><td>MI0</td><td>DB1,I0</td></tr>
		<tr><td>Boolean</td><td>II0.15</td><td>MI0.15</td><td>DB1,I0.15</td></tr>
		
		<tr><td rowspan="1">REAL</td><td>Float</td> <td>IREAL0</td><td>MREAL0</td><td>DB1,REAL0</td></tr>
		
		<tr><td rowspan="1">STRING</td><td>String</td> <td>ISTRING0.10</td><td>MSTRING0.10</td><td>DB1,STRING0.10</td></tr>
		
		<tr><td rowspan="2">W</td><td>字</td> <td>IW0</td><td>MW0</td><td>DB1,W0</td></tr>
		<tr><td>Boolean</td><td>IW0.15</td><td>MW0.15</td><td>DB1,W0.15</td></tr>
		
		<tr><td rowspan="1">X</td><td>Boolean</td> <td>IX0.7</td><td>MX0.7</td><td>DB1,X0.7</td></tr>
		
	</tbody>
</table>

 <b>Note:</b><pre>The offset for an atomic type tag in a data block is denoted by the column "Address" in Step 7, as shown above. This offset is denoted by the column "Offset" in the Siemens TIA Portal programming environment.</pre>
 
</body>
</html>
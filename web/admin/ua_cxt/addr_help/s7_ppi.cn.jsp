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
td {border:1px solid #ccc;}
.txt_bd {font-size:12px;}
.tb_head {font-weight:bold;}
</style>
</head>
<BODY>
 <P style="border-bottom:1.50pt solid black;">S7 PPI Addressing</P>
 <P class="txt_bd" style="margin-top:0;margin-bottom:0">&nbsp;</P>
 <P class="txt_bd" >Default data types for dynamically defined tags are shown in<span style="font-weight:bold; "> bold</span>.</P>
 <P class="txt_bd" style="margin-top:0;margin-bottom:0">&nbsp;</P>
 <TABLE width="100%" cellspacing='0' cellpadding="1">
  <TR valign=top>
  <TD width="169">
  <P class="tb_head" >Device Type</P></TD>
  <TD width="184">
  <P class="tb_head" >Range Data</P></TD>
  <TD width="184">
  <P class="tb_head" >Type</P></TD>
  <TD width="184">
  <P class="tb_head" >Access</P></TD></TR>
  <TR valign=top>
  <TD width="169">
  <P class="txt_bd" >Discrete Inputs</P></TD>
  <TD width="184">
  <P class="txt_bd" >I00000.bb - I65535.bb*</P>
  <P class="txt_bd" style="margin-top:0;margin-bottom:0">&nbsp;</P>
  <P class="txt_bd" >Ixxxxx.0 - Ixxxxx.7</P>
  <P class="txt_bd" >Ixxxxx.0 - Ixxxxx.15</P>
  <P class="txt_bd" >Ixxxxx.0 - Ixxxxx.31</P></TD>
  <TD width="184">
  <P class="txt_bd" >Boolean, Byte,<span style="font-weight:bold; "> Word</span>, Short, DWord, Long, Float</P>
  <P class="txt_bd" ><span style="font-weight:bold; ">Byte</span></P>
  <P class="txt_bd" >Boolean, <span style="font-weight:bold; ">Word</span>, Short</P>
  <P class="txt_bd" ><span style="font-weight:bold; ">Dword</span>, Long</P></TD>
  <TD width="184">
  <P class="txt_bd" >Read - Write</P></TD></TR>
  <TR valign=top>
  <TD width="169">
  <P class="txt_bd" style="margin-top:0;margin-bottom:0">&nbsp;</P></TD>
  <TD width="184">
  <P class="txt_bd" style="margin-top:0;margin-bottom:0">&nbsp;</P></TD>
  <TD width="184">
  <P class="txt_bd" style="margin-top:0;margin-bottom:0">&nbsp;</P></TD>
  <TD width="184">
  <P class="txt_bd" style="margin-top:0;margin-bottom:0">&nbsp;</P></TD></TR>
  <TR valign=top>
  <TD width="169">
  <P class="txt_bd" >Discrete Outputs</P></TD>
  <TD width="184">
  <P class="txt_bd" >Q00000.bb - Q65535.bb<span style="font-weight:bold; ">*</span></P>
  <P class="txt_bd" style="margin-top:0;margin-bottom:0">&nbsp;</P>
  <P class="txt_bd" >Qxxxxx.0 - Qxxxxx.7</P>
  <P class="txt_bd" >Qxxxxx.0 - Qxxxxx.15</P>
  <P class="txt_bd" >Qxxxxx.0 - Qxxxxx.31</P></TD>
  <TD width="184">
  <P class="txt_bd" >Boolean, Byte, <span style="font-weight:bold; ">Word</span>, Short, DWord, Long, Float</P>
  <P class="txt_bd" ><span style="font-weight:bold; ">Byte</span></P>
  <P class="txt_bd" >Boolean, <span style="font-weight:bold; ">Word</span>, Short</P>
  <P class="txt_bd" ><span style="font-weight:bold; ">Dword</span>, Long</P></TD>
  <TD width="184">
  <P class="txt_bd" >Read - Write</P></TD></TR>
  <TR valign=top>
  <TD width="169">
  <P class="txt_bd" style="margin-top:0;margin-bottom:0">&nbsp;</P></TD>
  <TD width="184">
  <P class="txt_bd" style="margin-top:0;margin-bottom:0">&nbsp;</P></TD>
  <TD width="184">
  <P class="txt_bd" style="margin-top:0;margin-bottom:0">&nbsp;</P></TD>
  <TD width="184">
  <P class="txt_bd" style="margin-top:0;margin-bottom:0">&nbsp;</P></TD></TR>
  <TR valign=top>
  <TD width="169">
  <P class="txt_bd" >Internal Memory&nbsp;</P></TD>
  <TD width="184">
  <P class="txt_bd" >M00000.bb - M65535.bb<span style="font-weight:bold; ">*</span></P>
  <P class="txt_bd" style="margin-top:0;margin-bottom:0">&nbsp;</P>
  <P class="txt_bd" >Mxxxxx.0 - Mxxxxx.7</P>
  <P class="txt_bd" >Mxxxxx.0 - Mxxxxx.15</P>
  <P class="txt_bd" >Mxxxxx.0 - Mxxxxx.31</P></TD>
  <TD width="184">
  <P class="txt_bd" >Boolean, Byte, <span style="font-weight:bold; ">Word</span>, Short, DWord, Long, Float</P>
  <P class="txt_bd" ><span style="font-weight:bold; ">Byte</span></P>
  <P class="txt_bd" >Boolean, <span style="font-weight:bold; ">Word</span>, Short</P>
  <P class="txt_bd" ><span style="font-weight:bold; ">Dword</span>, Long</P></TD>
  <TD width="184">
  <P class="txt_bd" >Read - Write</P></TD></TR>
  <TR valign=top>
  <TD width="169">
  <P class="txt_bd" style="margin-top:0;margin-bottom:0">&nbsp;</P></TD>
  <TD width="184">
  <P class="txt_bd" style="margin-top:0;margin-bottom:0">&nbsp;</P></TD>
  <TD width="184">
  <P class="txt_bd" style="margin-top:0;margin-bottom:0">&nbsp;</P></TD>
  <TD width="184">
  <P class="txt_bd" style="margin-top:0;margin-bottom:0">&nbsp;</P></TD></TR>
  <TR valign=top>
  <TD width="169">
  <P class="txt_bd" >Special Memory</P></TD>
  <TD width="184">
  <P class="txt_bd" >SM00000.bb - SM65535.bb<span style="font-weight:bold; ">*</span></P>
  <P class="txt_bd" style="margin-top:0;margin-bottom:0">&nbsp;</P>
  <P class="txt_bd" >SMxxxxx.0 - SMxxxxx.7</P>
  <P class="txt_bd" >SMxxxxx.0 - SMxxxxx.15</P>
  <P class="txt_bd" >SMxxxxx.0 - SMxxxxx.31</P></TD>
  <TD width="184">
  <P class="txt_bd" >Boolean, Byte, <span style="font-weight:bold; ">Word</span>, Short, DWord, Long, Float</P>
  <P class="txt_bd" ><span style="font-weight:bold; ">Byte</span></P>
  <P class="txt_bd" >Boolean, <span style="font-weight:bold; ">Word</span>, Short</P>
  <P class="txt_bd" ><span style="font-weight:bold; ">Dword</span>, Long</P></TD>
  <TD width="184">
  <P class="txt_bd" >Read - Write</P>
  <P class="txt_bd" >(SM0 - SM29 are Read/Only)</P></TD></TR>
  <TR valign=top>
  <TD width="169">
  <P class="txt_bd" style="margin-top:0;margin-bottom:0">&nbsp;</P></TD>
  <TD width="184">
  <P class="txt_bd" style="margin-top:0;margin-bottom:0">&nbsp;</P></TD>
  <TD width="184">
  <P class="txt_bd" style="margin-top:0;margin-bottom:0">&nbsp;</P></TD>
  <TD width="184">
  <P class="txt_bd" style="margin-top:0;margin-bottom:0">&nbsp;</P></TD></TR>
  <TR valign=top>
  <TD width="169">
  <P class="txt_bd" >Variable Memory</P></TD>
  <TD width="184">
  <P class="txt_bd" >V00000.bb - V65535.bb<span style="font-weight:bold; ">*</span></P>
  <P class="txt_bd" style="margin-top:0;margin-bottom:0">&nbsp;</P>
  <P class="txt_bd" >Vxxxxx.0 - Vxxxxx.7</P>
  <P class="txt_bd" >Vxxxxx.0 - Vxxxxx.15</P>
  <P class="txt_bd" >Vxxxxx.0 - Vxxxxx.31</P>
  <P class="txt_bd" >Vxxxxx.1-Vxxxxx.218</P></TD>
  <TD width="184">
  <P class="txt_bd" >Boolean, Byte, <span style="font-weight:bold; ">Word</span>, Short, DWord, Long, Float, String</P>
  <P class="txt_bd" ><span style="font-weight:bold; ">Byte</span></P>
  <P class="txt_bd" >Boolean,<span style="font-weight:bold; "> Word</span>, Short</P>
  <P class="txt_bd" ><span style="font-weight:bold; ">Dword</span>, Long</P>
  <P class="txt_bd" ><span style="font-weight:bold; ">String</span></P></TD>
  <TD width="184">
  <P class="txt_bd" >Read - Write</P></TD></TR>
  <TR valign=top>
  <TD width="169">
  <P class="txt_bd" style="margin-top:0;margin-bottom:0">&nbsp;</P></TD>
  <TD width="184">
  <P class="txt_bd" style="margin-top:0;margin-bottom:0">&nbsp;</P></TD>
  <TD width="184">
  <P class="txt_bd" style="margin-top:0;margin-bottom:0">&nbsp;</P></TD>
  <TD width="184">
  <P class="txt_bd" style="margin-top:0;margin-bottom:0">&nbsp;</P></TD></TR>
  <TR valign=top>
  <TD width="169">
  <P class="txt_bd" >Timer Current Values</P></TD>
  <TD width="184">
  <P class="txt_bd" >T00000 - T65535</P></TD>
  <TD width="184">
  <P class="txt_bd" ><span style="font-weight:bold; ">DWord</span>, Long</P></TD>
  <TD width="184">
  <P class="txt_bd" >Read - Write</P></TD></TR>
  <TR valign=top>
  <TD width="169">
  <P class="txt_bd" style="margin-top:0;margin-bottom:0">&nbsp;</P></TD>
  <TD width="184">
  <P class="txt_bd" style="margin-top:0;margin-bottom:0">&nbsp;</P></TD>
  <TD width="184">
  <P class="txt_bd" style="margin-top:0;margin-bottom:0">&nbsp;</P></TD>
  <TD width="184">
  <P class="txt_bd" style="margin-top:0;margin-bottom:0">&nbsp;</P></TD></TR>
  <TR valign=top>
  <TD width="169">
  <P class="txt_bd" >Timer Status Bits</P></TD>
  <TD width="184">
  <P class="txt_bd" >T00000 - T65535</P></TD>
  <TD width="184">
  <P class="txt_bd" >Boolean<span style="font-weight:bold; ">**</span></P></TD>
  <TD width="184">
  <P class="txt_bd" >Read - Only</P></TD></TR>
  <TR valign=top>
  <TD width="169">
  <P class="txt_bd" style="margin-top:0;margin-bottom:0">&nbsp;</P></TD>
  <TD width="184">
  <P class="txt_bd" style="margin-top:0;margin-bottom:0">&nbsp;</P></TD>
  <TD width="184">
  <P class="txt_bd" style="margin-top:0;margin-bottom:0">&nbsp;</P></TD>
  <TD width="184">
  <P class="txt_bd" style="margin-top:0;margin-bottom:0">&nbsp;</P></TD></TR>
  <TR valign=top>
  <TD width="169">
  <P class="txt_bd" >Counter Current Values</P></TD>
  <TD width="184">
  <P class="txt_bd" >C00000 - C65535</P></TD>
  <TD width="184">
  <P class="txt_bd" ><span style="font-weight:bold; ">Word</span>, Short</P></TD>
  <TD width="184">
  <P class="txt_bd" >Read - Write</P></TD></TR>
  <TR valign=top>
  <TD width="169">
  <P class="txt_bd" style="margin-top:0;margin-bottom:0">&nbsp;</P></TD>
  <TD width="184">
  <P class="txt_bd" style="margin-top:0;margin-bottom:0">&nbsp;</P></TD>
  <TD width="184">
  <P class="txt_bd" style="margin-top:0;margin-bottom:0">&nbsp;</P></TD>
  <TD width="184">
  <P class="txt_bd" style="margin-top:0;margin-bottom:0">&nbsp;</P></TD></TR>
  <TR valign=top>
  <TD width="169">
  <P class="txt_bd" >Counter Status Bits</P></TD>
  <TD width="184">
  <P class="txt_bd" >C00000 - C65535</P></TD>
  <TD width="184">
  <P class="txt_bd" >Boolean<span style="font-weight:bold; ">**</span></P></TD>
  <TD width="184">
  <P class="txt_bd" >Read - Only</P></TD></TR>
  <TR valign=top>
  <TD width="169">
  <P class="txt_bd" style="margin-top:0;margin-bottom:0">&nbsp;</P></TD>
  <TD width="184">
  <P class="txt_bd" style="margin-top:0;margin-bottom:0">&nbsp;</P></TD>
  <TD width="184">
  <P class="txt_bd" style="margin-top:0;margin-bottom:0">&nbsp;</P></TD>
  <TD width="184">
  <P class="txt_bd" style="margin-top:0;margin-bottom:0">&nbsp;</P></TD></TR>
  <TR valign=top>
  <TD width="169">
  <P class="txt_bd" >High Speed Counters</P></TD>
  <TD width="184">
  <P class="txt_bd" >HC00000 - HC65535</P></TD>
  <TD width="184">
  <P class="txt_bd" ><span style="font-weight:bold; ">DWord</span>, Long</P></TD>
  <TD width="184">
  <P class="txt_bd" >Read - Only</P></TD></TR>
  <TR valign=top>
  <TD width="169">
  <P class="txt_bd" style="margin-top:0;margin-bottom:0">&nbsp;</P></TD>
  <TD width="184">
  <P class="txt_bd" style="margin-top:0;margin-bottom:0">&nbsp;</P></TD>
  <TD width="184">
  <P class="txt_bd" style="margin-top:0;margin-bottom:0">&nbsp;</P></TD>
  <TD width="184">
  <P class="txt_bd" style="margin-top:0;margin-bottom:0">&nbsp;</P></TD></TR>
  <TR valign=top>
  <TD width="169">
  <P class="txt_bd" >Analog Inputs</P></TD>
  <TD width="184">
  <P class="txt_bd" >AI00000 - AI65534***</P></TD>
  <TD width="184">
  <P class="txt_bd" ><span style="font-weight:bold; ">Word</span>, Short</P></TD>
  <TD width="184">
  <P class="txt_bd" >Read - Only</P></TD></TR>
  <TR valign=top>
  <TD width="169">
  <P class="txt_bd" style="margin-top:0;margin-bottom:0">&nbsp;</P></TD>
  <TD width="184">
  <P class="txt_bd" style="margin-top:0;margin-bottom:0">&nbsp;</P></TD>
  <TD width="184">
  <P class="txt_bd" style="margin-top:0;margin-bottom:0">&nbsp;</P></TD>
  <TD width="184">
  <P class="txt_bd" style="margin-top:0;margin-bottom:0">&nbsp;</P></TD></TR>
  <TR valign=top>
  <TD width="169">
  <P class="txt_bd" >Analog Outputs</P></TD>
  <TD width="184">
  <P class="txt_bd" >AQ00000 - AQ65534***</P></TD>
  <TD width="184">
  <P class="txt_bd" ><span style="font-weight:bold; ">Word</span>, Short</P></TD>
  <TD width="184">
  <P class="txt_bd" >Write - Only</P></TD></TR>
 </TABLE>
 <P class="txt_bd" style="margin-top:0;margin-bottom:0">&nbsp;</P>
 <P class="txt_bd" style="margin-top:0;margin-bottom:0">&nbsp;</P>
 <P class="txt_bd" ><span style="font-weight:bold; ">(*)</span> <span style="font-weight:bold; ">Note:</span> On Byte, Word, Short, DWord or Long type an option .bb (dot bit) can be appended to the address to reference a bit in a particular value.  The valid ranges for the optional bit is 0 - 7 for Byte types, 0 - 15 for Word, Short and Boolean types, 0 - 31 for DWord and Long types, and 1 - 218 for String types..  Float types do not support bit operations.  Boolean and String types require a bit number (see special case **).  The bit number for String types specifies the number of characters in the string.  The following diagram illustrates how the driver maps bits within the controller:</P>
 <P class="txt_bd" style="margin-top:0;margin-bottom:0">&nbsp;</P>
 <P class="txt_bd" >Dynamic addresses with bit numbers in the range of 0 to 7 will default to Byte, 8 to 15 will default to Word, and 16 to 31 will default to DWord.  V Memory addresses with a bit number larger than 31 will default to String.</P>
 <P class="txt_bd" style="margin-top:0;margin-bottom:0">&nbsp;</P>
 <P class="txt_bd" style="margin-top:0;margin-bottom:0">&nbsp;</P>
 <P class="txt_bd" ><IMG src="./inc/p1.png" border="0" height="259" width="687" ></P>
 <P class="txt_bd" >[e.g., V30.10@bool, V30.2@byte, V30.26@dword all reference the same bit in the controller]</P>
 <P class="txt_bd" style="margin-top:0;margin-bottom:0">&nbsp;</P>
 <P class="txt_bd" ><span style="font-weight:bold; "><font  face='MS Sans Serif'>(**) Note:</font></span><font  face='MS Sans Serif'> For Timer and Counter status bits, a dot bit notation is not used.  The status bit for timer 7 would be T7 declared as Boolean.</font></P>
 <P class="txt_bd" style="margin-top:0;margin-bottom:0">&nbsp;</P>
 <P class="txt_bd" ><span style="font-weight:bold; "><font  face='MS Sans Serif'>(***) Note:</font></span><font  face='MS Sans Serif'> For Analog Inputs and Outputs the address must be even (AI0, AI2, AI4...).</font></P>
 <P class="txt_bd" style="margin-top:0;margin-bottom:0">&nbsp;</P>
 <P class="txt_bd" >The actual number of addresses of each type is dependent on the Siemens S7-200 device in use (each type does not necessarily support an address of 0 to 65535).  Refer to the device documentation for address ranges.</P>
 <P class="txt_bd" style="margin-top:0;margin-bottom:0">&nbsp;</P>
 <P class="KT-SubTopicTitle" >Arrays</P>
 <P class="txt_bd" >In addition to the address formats listed above, certain memory types (I, Q, M, SM, V, AI, AQ) support an array operation.  At this time Boolean arrays are not allowed.  To specify an array address, append &quot;<span style="font-weight:bold; ">[rows][cols]</span>&quot; to the end of an address.  If only [cols] is specified, [rows] will default to 1.  With the array type, it is possible to read and write a block of 200 bytes at one time. </P>
 <P class="txt_bd" style="margin-top:0;margin-bottom:0">&nbsp;</P>
 <P class="txt_bd" >The maximum array size for Word and Short types is 100, and for DWord, Long and Float types is 50.  The array size is determined by the multiplication of rows and cols.</P>
 <P class="txt_bd" >Note: The maximum array size is also dependent on the maximum block size of the device being used. </P>
 <P class="txt_bd" style="margin-top:0;margin-bottom:0">&nbsp;</P>
 <P class="KT-SubTopicTitle" >Examples</P>
 <P class="txt_bd" >To read and write an array of 10 Variable Memory Float values starting with V10, declare an address as follows:</P>
 <P class="txt_bd" >&nbsp;V10 [1][10] (choose Float for the data type.)</P>
 <P class="txt_bd" >&nbsp;This array will read and write values to registers V10, V14, V18, V22 ... V46.</P>
 <P class="txt_bd" style="margin-top:0;margin-bottom:0">&nbsp;</P>
 <P class="txt_bd" >To read and write to bit 23 of Internal Memory Long M20, declare an address as follows:</P>
 <P class="txt_bd" >&nbsp;M20.23&nbsp;(choose Long for the data type.)</P>
 <P class="txt_bd" style="margin-top:0;margin-bottom:0">&nbsp;</P>
 <P class="KT-SubTopicTitle" >Strings</P>
 <P class="txt_bd" >The driver allows for variable length strings to be stored in Variable Memory locations.  The bit number specifies the string length (1 - 218) in characters.  String data that is sent to the device, but is smaller in length than the string character count (bit number) is null terminated.  String data that meets or exceeds the character length is truncated to the character count and sent to the device without a null terminator.</P>
 <P class="txt_bd" >To read and write a string starting at V5 for a length of 10 characters (V Memory locations V5-V14 would be used to store this 10 character string), declare an address as follows:</P>
 <P class="txt_bd" >&nbsp;V5.10 (choose string for the data type.)</P>
 <P class="txt_bd" >Not all devices will support up to 218 character requests in a single transaction.  Consult your device documentation to determine the maximum number of characters that can be requested in a transaction.  This value is the largest string the driver can read/write to/from the device.</P>
 <P class="txt_bd" style="margin-top:0;margin-bottom:0">&nbsp;</P>
 <P class="KT-SubTopicTitle" >Notes:</P>
 <P class="txt_bd" >The user should use caution when modifying Word, Short, DWord, Long and Float types.  Each address starts at a byte offset within the device.  Therefore, Words V0 and V1 overlap at byte 1.  Writing to V0 will also modify the value held in V1.  Similarly, DWord, Long and Float types can also overlap.  It is recommended that you use these memory types so that overlapping does not occur.  As an example, when using DWords you might want to use V0, V4, V8 ... and so on, to prevent overlapping bytes.</P>
</BODY>
</HTML>
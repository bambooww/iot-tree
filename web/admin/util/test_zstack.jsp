<%@ page contentType="text/html;charset=UTF-8"%>
<%@page import = "java.io.PrintStream,java.util.* , java.io.* , java.net.*,org.iottree.core.*,org.iottree.core.util.*,
 java.security.*, javax.crypto.*,javax.crypto.spec.*,org.iottree.driver.zigbee.zstack.*,
 org.iottree.driver.zigbee.*

 " %><%!
	private static byte[] hex2bs(String hex)
	{
		hex = hex.replaceAll("-", "") ;
		return Convert.hexStr2ByteArray(hex) ;
	}
 
	public void test_nor_msg(String pre,String ss,JspWriter out) throws IOException
    {
		StringBuilder failedr = new StringBuilder() ;
		byte[] bs = hex2bs(ss) ;
		MsgNor m = MsgNor.parseFrom(bs,failedr) ;
		out.print(pre+" "+ss+"  -> "+m+"  "+failedr+"<br>");
    }
%><%

test_nor_msg(">>","FE-00-21-01-20",out) ;
test_nor_msg("<<","FE-02-61-01-59-06-3D",out) ;


test_nor_msg(">>","FE-00-21-02-23",out) ;
test_nor_msg("<<","FE-0A-61-02-02-01-02-07-01-46-D9-34-01-00-C4",out) ;

String ext_pan_id = "E8D88EFEFA6EB576";
byte[] pan_bs = new byte[12] ;
byte[] panbs8 = new byte[8] ;
DataUtil.transStr2PanId(ext_pan_id, pan_bs, 2) ;
DataUtil.transStr2PanId(ext_pan_id, panbs8, 0) ;
out.print("ext_pan_id src="+ext_pan_id) ;
out.print("<br>&nbsp;&nbsp;[pack bs12="+Convert.byteArray2HexStr(pan_bs)+" bs8="+Convert.byteArray2HexStr(panbs8) +"]");
out.print("<br>&nbsp;&nbsp;[tar bs12="+DataUtil.transPanId(pan_bs, 2)+" bs8="+DataUtil.transPanId(panbs8, 0) +"]<br>");
%>
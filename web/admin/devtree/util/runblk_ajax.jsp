<%@ page contentType="text/html;charset=UTF-8"%><%@ page import="java.util.*,
	java.io.*,org.json.*,
	org.iottree.core.*,
	org.iottree.core.util.*,
	org.iottree.core.devtree.*
	"%><%!

%><%
if(!Convert.checkReqEmpty(request, out,"op"))
	return ;
String op = request.getParameter("op");
String treeid = request.getParameter("treeid");
String tree_nid = request.getParameter("tree_nid");
String cat_name = request.getParameter("cat");

String tree_nids_str = request.getParameter("tree_nids");
String runblk_uid = request.getParameter("runblk_uid");

DTTree tree = null ;
if(Convert.isNotNullEmpty(treeid))
{
	tree = DTTreeManager.getInstance().getTreeById(treeid) ;
	if(tree==null)
	{
		out.print("no tree found with id="+treeid) ;
		return ;
	}
}

String ins_name = request.getParameter("ins_name") ;
int idx = Convert.parseToInt32(request.getParameter("idx"), -1) ;

//  0 - sub node , 1 - sibling  2 - sibling head 
int add_sty = Convert.parseToInt32(request.getParameter("sty"), 0) ;
List<String> tree_nids = Convert.splitStrWith(tree_nids_str, ",|") ;
DTNode dn = null ;
if(Convert.isNotNullEmpty(tree_nid))
{
	dn = tree.findNodeById(tree_nid) ;
	if(dn==null)
	{
		out.print("no tree node found") ;
		return ;
	}
}

String pn_id = request.getParameter("pn_id");
DTNode pn_node = null ;
if(Convert.isNotNullEmpty(pn_id))
{
	DTNode tmpnd = tree.findNodeById(pn_id) ;
	if(tmpnd==null || !(tmpnd instanceof DTNode))
	{
		out.print("no pn node grp found") ;
		return ;
	}
	pn_node = (DTNode)tmpnd ;
}
String name = request.getParameter("name") ;
String title = request.getParameter("title") ;
String desc = request.getParameter("desc") ;
String jarr_str = request.getParameter("jarr") ;
String jstr = request.getParameter("jstr") ;
JSONArray input_jarr = null ;
JSONObject input_jo = null ;
if(Convert.isNotNullEmpty(jarr_str))
	input_jarr = new JSONArray(jarr_str) ;
if(Convert.isNotNullEmpty(jstr))
	input_jo = new JSONObject(jstr) ;

DTTreeRenderCtrl ctrl = new DTTreeRenderCtrl() ;

JSONObject tmpjo = null ;
StringBuilder failedr = new StringBuilder() ;
try
{
switch(op)
{

case "list_runblks_cat":
	if(!Convert.checkReqEmpty(request, out,"cat"))
		return ;
	DTRunBlkCat cat = DTRunBlkManager.getInstance().getRunBlkCat(cat_name) ;
	if(cat==null)
	{
		out.print("no cat found");return ;
	}
	cat.toListRunBlksJArr().write(out) ;
	break ;
case "del_runblk_ins":
	if(!Convert.checkReqEmpty(request, out,"treeid","tree_nid","ins_name"))
		return ;
	if(tree.delRunBlkIns(tree_nid, ins_name, failedr)==null)
		out.print(failedr);
	else
		out.print("succ");
	break ;
case "set_runblk_ins_basic":
	if(!Convert.checkReqEmpty(request, out,"treeid","tree_nid","jstr"))
		return ;
	boolean b_add = "true".equals(request.getParameter("add")) ;
	if(tree.setRunBlkInsBasicByJO(b_add, tree_nid, input_jo, failedr))
		out.print("succ") ;
	else
		out.print(failedr) ;
	return ;
case "list_runblk_inss":
	if(!Convert.checkReqEmpty(request, out,"treeid","tree_nid"))
		return ;
	%>
	{"code":0,"msg":"","count":0, "data":[
	<%
		boolean bfirst = true;

			for(DTRunBlkIns blk0: dn.getRunBlkInssMap().values()) //ab.listLocRunBlks())
			{
				if(bfirst)
					bfirst=false;
				else
					out.print(",");
				
				tmpjo = blk0.toListJO() ;
				tmpjo.write(out) ;
			}
		
	%>
	]}
	<%
	break;
}
}
catch(Exception ee)
{
	out.print(ee.getMessage());
}
%>
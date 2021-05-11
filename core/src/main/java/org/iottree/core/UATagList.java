package org.iottree.core;

import java.util.*;

import org.iottree.core.util.xmldata.data_class;
import org.iottree.core.util.xmldata.data_obj;
import org.iottree.core.basic.PropGroup;
import org.json.*;

/**
 * a leaf node for oc
 * but it's content is a list
 * @author zzj
 *
 */
@data_class
public class UATagList extends UANode implements IOCList //UANodeOC
{
	public static final String MEMBER_TP = "tag_list" ;
	
	
	transient UANodeOCTags belongToNode = null;
	
	@data_obj(obj_c=UATag.class)
	List<UATag> tags = new ArrayList<>();
	
	void constructNodeTree()
	{
		for(UATag tg:tags)
		{
			tg.belongToNode = belongToNode ;
			//tg.belongToNode = this ;
		}
		//super.constructNodeTree();
	}
	
	public String getMemberTp()
	{
		return MEMBER_TP ;
	}
	
	public UANodeOCTags getBelongToNode()
	{
		return belongToNode;
	}
	
	public String getNodePathName()
	{
		UANode p = belongToNode;
		if(p==null)
			return null;
		return p.getNodePathName();
	}
	
	public String getNodePathTitle()
	{
		UANode p = belongToNode;
		if(p==null)
			return null;
		return p.getNodePathTitle();
	}
	
	@Override
	public List<Object> OCList_getItems()
	{
		ArrayList<Object> rets = new ArrayList<>(tags.size()) ;
		rets.addAll(tags) ;
		return rets;
	}
	
	static JSONArray LIST_HEAD = new JSONArray() ;
	static
	{
		//[{n:"tagn",t:"Tag Name"},{n:"addr",t:"Address"},{n:"datatp",t:"Data Type"},{n:"val",t:"Value"}
		LIST_HEAD.put(new JSONObject().put("n", "midt").put("t", "Mid"));
		LIST_HEAD.put(new JSONObject().put("n", "name").put("t", "Tag Name"));
		LIST_HEAD.put(new JSONObject().put("n", "title").put("t", "Title"));
		LIST_HEAD.put(new JSONObject().put("n", "addr").put("t", "Address"));
		LIST_HEAD.put(new JSONObject().put("n", "vtt").put("t", "Value Type"));
		LIST_HEAD.put(new JSONObject().put("n", "val").put("t", "Value"));
		LIST_HEAD.put(new JSONObject().put("n", "ts").put("t", "TimeStamp"));
		LIST_HEAD.put(new JSONObject().put("n", "qt").put("t", "Quality"));
	}
	
	public JSONArray OCList_getListHead()
	{
		return LIST_HEAD; 
	}
	
	
	public List<UATag> listTags()
	{
		return tags ;
	}
	
	public UATag getTagByName(String n)
	{
		for(UATag t:tags)
		{
			if(n.contentEquals(t.getName()))
				return t ;
		}
		return null ;
	}
	
	public UATag getTagById(String id)
	{
		for(UATag t:tags)
		{
			if(id.contentEquals(t.getId()))
				return t ;
		}
		return null ;
	}

	public boolean delTag(UATag t) throws Exception
	{
		if(tags.remove(t))
		{
			//this.getBelongToDev().getRep().save();
			return true;
		}
		return false;
	}
	
	
//	@Override
//	public List<UANode> getSubNodes()
//	{
//		ArrayList<UANode> rets = new ArrayList<>() ;
//		rets.addAll(tags);
//		return rets;//no sub node
//	}

	@Override
	public boolean OC_supportSub()
	{
		return false;
	}

	@Override
	public List<IOCBox> OC_getSubs()
	{
//		ArrayList<IOCBox> rets = new ArrayList<>() ;
//		rets.addAll(tags);
//		return rets;//no sub node
		return null ;
	}

	@Override
	public JSONObject OC_getPropsJSON()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void OC_setPropsJSON(JSONObject jo)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<UANode> getSubNodes()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean chkValid()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void onPropNodeValueChged()
	{
		// TODO Auto-generated method stub
		
	}

}

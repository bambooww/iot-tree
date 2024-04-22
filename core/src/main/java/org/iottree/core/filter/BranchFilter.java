package org.iottree.core.filter;

import org.iottree.core.UANodeOCTagsGCxt;

public class BranchFilter implements ICFilter
{

	@Override
	public boolean acceptNodeCxt(UANodeOCTagsGCxt nodecxt)
	{
		return false;
	}

}

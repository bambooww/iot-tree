package org.iottree.core.aiagent;

import java.util.HashMap;

public class EmbeddingFields
{
	public HashMap<String,EmbeddingField> name2field = new HashMap<>() ;
	
	
	public void setField(EmbeddingField fd)
	{
		this.name2field.put(fd.name,fd) ;
	}
}

package org.iottree.core.aiagent;

import org.json.JSONObject;

public class EmbeddingVal
{
	public String doc_id ;
	
	public JSONObject field_jo ;
	
	public float[] embedding_val ;
	
	public float queryScore = -1 ; // set by query embedding
	
	public EmbeddingVal()
	{}
	
	public EmbeddingVal(String doc_id,JSONObject field_jo,float[] embed_val)
	{
		this.doc_id = doc_id ;
		this.field_jo = field_jo ;
		this.embedding_val = embed_val ;
	}
}

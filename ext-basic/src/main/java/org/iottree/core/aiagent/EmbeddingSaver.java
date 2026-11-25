package org.iottree.core.aiagent;

import java.util.List;
import org.json.JSONObject;

public abstract class EmbeddingSaver implements AutoCloseable
{
	public static final String VECTOR_FIELD_EMB = "_embed";

	protected final String collection_name;
	
	protected int dim = 768;

	protected final EmbeddingFields fields;
	
	protected final String dirPath ;
	
	public EmbeddingSaver(String collection_name, String path, EmbeddingFields fields)
	{
		this.collection_name = collection_name;
		this.fields = fields;
		this.dirPath = path ;
	}
	
	public int getDim()
	{
		return this.dim ;
	}
	
	public void setDim(int dim)
	{
		this.dim = dim ;
	}

	public String getCollectionName()
	{
		return this.collection_name;
	}
	
	public int insert(String doc_id, JSONObject field_jo, float[] embedding)
	{
		return insert(new EmbeddingVal(doc_id, field_jo, embedding));
	}

	public int insert(EmbeddingVal ev)
	{
		return insert(List.of(ev));
	}


	public abstract int insert(List<EmbeddingVal> evs) ;
	
	public abstract int update(List<EmbeddingVal> evs) ;
	
	public abstract int deleteByPks(List<String> pk_ids) ;
	
	public abstract List<EmbeddingVal> getByPks(List<String> pk_ids);
	
	public abstract List<EmbeddingVal> queryByEmbedding(float[] embeds, int limit_top_k_num);
}

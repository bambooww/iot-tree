package org.iottree.core.aiagent;

import org.zvec.binding.Zvec;
import org.iottree.core.UAVal;
import org.json.JSONObject;
import org.zvec.binding.Collection;
import org.zvec.binding.CollectionSchema;
import org.zvec.binding.FieldSchema;
import org.zvec.binding.IndexParams;
import org.zvec.binding.MetricType;
import org.zvec.binding.VectorQuery;
import org.zvec.binding.DataType;
import org.zvec.binding.Doc;

import java.util.*;

public class EmbeddingSaverZvec extends EmbeddingSaver
{
	
	
	private final Collection collection;

	public EmbeddingSaverZvec(String collection_name, String path, EmbeddingFields fields)
	{
		super( collection_name,  path,  fields);
		
		// System.loadLibrary("zvec");
		try (CollectionSchema schema = new CollectionSchema(collection_name);)
		{
			try (FieldSchema vecField = new FieldSchema(VECTOR_FIELD_EMB, DataType.VECTOR_FP32, false, dim))
			{
				try (IndexParams hnsw = IndexParams.createHNSW(MetricType.COSINE, 16, 200))
				{
					vecField.setIndexParams(hnsw);
				}
				schema.addField(vecField);
			}

			for (EmbeddingField f : fields.name2field.values())
			{
				UAVal.ValTP vt = f.vt;
				DataType dt = transDataType(vt);
				if (dt == null)
					throw new IllegalArgumentException("unsupport field val tp - " + vt);

				try (FieldSchema fs = new FieldSchema(f.name, dt, true, 0))
				{
					// try (IndexParams invert = TODO f idx
					// IndexParams.c.cr.createInvert(true, false)) {
					// titleField.setIndexParams(invert); // Add a string field
					// with invert index
					// }
					//
					// try (IndexParams invert = IndexParams.createInvert(true,
					// false)) {
					// titleField.setIndexParams(invert); // Add a string field
					// with invert index
					// }
					schema.addField(fs);
				}
			}

			schema.validate();

			this.collection = Zvec.createAndOpen(path, schema, null);
		}
	}

	public static DataType transDataType(UAVal.ValTP vt)
	{
		switch (vt)
		{
		case vt_str:
			return DataType.STRING;
		case vt_bool:
			return DataType.BOOL;
		case vt_int32:
			return DataType.INT32;
		case vt_int64:
			return DataType.INT64;
		case vt_uint32:
			return DataType.UINT32;
		case vt_uint64:
			return DataType.UINT64;
		case vt_float:
			return DataType.FLOAT;
		case vt_double:
			return DataType.DOUBLE;
		default:
			return null;
		}
	}

	public static DataType transDataTypeVector(UAVal.ValTP vt)
	{
		switch (vt)
		{
		case vt_int16:
			return DataType.VECTOR_INT16;
		case vt_float:
			return DataType.VECTOR_FP32;
		case vt_double:
			return DataType.VECTOR_FP64;
		default:
			return null;
		}
	}

	public static void setDocFieldVal(Doc doc, String fieldn, UAVal.ValTP vt, Object val)
	{
		if (val == null)
			return;
		switch (vt)
		{
		case vt_str:
			doc.addStringField(fieldn, val.toString());
			return;
		case vt_bool:
			doc.addBoolField(fieldn, (Boolean) val);
			return;
		case vt_int32:
			doc.addInt32Field(fieldn, ((Number) val).intValue());
			return;
		case vt_int64:
			doc.addInt64Field(fieldn, ((Number) val).longValue());
			return;
		case vt_uint32:
			doc.addUint32Field(fieldn, ((Number) val).longValue());
			return;
		case vt_uint64:
			doc.addInt64Field(fieldn, ((Number) val).longValue());
			return;
		case vt_float:
			doc.addFloatField(fieldn, ((Number) val).floatValue());
			return;
		case vt_double:
			doc.addDoubleField(fieldn, ((Number) val).doubleValue());
			return;
		default:
			throw new IllegalArgumentException("unsupport type " + vt);
		}
	}

	public static Object getDocFieldVal(Doc doc, String fieldn, UAVal.ValTP vt)
	{
		switch (vt)
		{
		case vt_str:
			return doc.getStringField(fieldn);
		case vt_bool:
			return doc.getBoolField(fieldn);
		case vt_int32:
			return doc.getInt32Field(fieldn);
		case vt_int64:
			return doc.getInt64Field(fieldn);
		case vt_uint32:
			return doc.getUint32Field(fieldn);
		case vt_uint64:
			return doc.getInt64Field(fieldn);
		case vt_float:
			return doc.getFloatField(fieldn);
		case vt_double:
			return doc.getDoubleField(fieldn);
		default:
			throw new IllegalArgumentException("unsupport type " + vt);
		}
	}

	@Override
	public int insert(List<EmbeddingVal> evs)
	{
		return insertOrUpdate(evs, true);
	}

	private void setDocByEmbedVal(Doc doc, EmbeddingVal ev)
	{
		doc.setPK(ev.doc_id);
		doc.addVectorFP32Field(VECTOR_FIELD_EMB, ev.embedding_val);
		for (EmbeddingField f : this.fields.name2field.values())
		{
			String fn = f.name ;
			UAVal.ValTP vt = f.vt ;
			Object val = ev.field_jo.opt(fn);
			setDocFieldVal(doc, fn, vt, val);
		}
	}

	private EmbeddingVal setEmbedValByDoc(Doc d)
	{
		String pkid = d.getPK();
		JSONObject jo = new JSONObject();
		for (EmbeddingField f : this.fields.name2field.values())
		{
			String fn = f.name ;
			UAVal.ValTP vt = f.vt ;

			Object val = getDocFieldVal(d, fn, vt);
			jo.putOpt(fn, val);
		}
		EmbeddingVal ev = new EmbeddingVal(pkid, jo, null);
		ev.queryScore = d.getScore();
		return ev;
	}

	private int insertOrUpdate(List<EmbeddingVal> evs, boolean b_insert_or_update)
	{
		List<Doc> docs = new ArrayList<>();
		try
		{
			for (EmbeddingVal ev : evs)
			{
				Doc doc = new Doc();
				docs.add(doc);
				setDocByEmbedVal(doc, ev);
			}

			Collection.WriteResult wr = null;
			if (b_insert_or_update)
			{
				wr = collection.insert(docs);
				collection.flush();
			}
			else
			{
				wr = collection.update(docs);
			}
			return (int) wr.successCount;
		}
		finally
		{
			if (docs != null)
				Doc.freeDocs(docs);
		}
	}

	@Override
	public int update(List<EmbeddingVal> evs)
	{
		return insertOrUpdate(evs, false);
	}

	@Override
	public int deleteByPks(List<String> pk_ids)
	{
		Collection.WriteResult wr = collection.delete(pk_ids);
		return (int) wr.successCount;
	}

	@Override
	public List<EmbeddingVal> getByPks(List<String> pk_ids)
	{
		List<Doc> fetched = null;
		try
		{
			ArrayList<EmbeddingVal> rets = new ArrayList<>();
			fetched = collection.fetch(pk_ids);
			for (Doc d : fetched)
			{
				EmbeddingVal ev = setEmbedValByDoc(d);
				if (ev == null)
					continue;
				rets.add(ev);
			}
			return rets;
		}
		finally
		{
			if (fetched != null)
				Doc.freeDocs(fetched);
		}
	}

	@Override
	public List<EmbeddingVal> queryByEmbedding(float[] embeds, int limit_top_k_num)
	{
		try (VectorQuery query = new VectorQuery())
		{
			query.setFieldName(VECTOR_FIELD_EMB);
			query.setTopK(limit_top_k_num);
			query.setQueryVector(embeds);
			query.setIncludeVector(true);

			List<Doc> results = null;
			try
			{
				ArrayList<EmbeddingVal> rets = new ArrayList<>();
				results = collection.query(query);
				for (Doc r : results)
				{
					EmbeddingVal ev = setEmbedValByDoc(r);
					if (ev == null)
						continue;
					rets.add(ev);
				}
				return rets;
			}
			finally
			{
				if (results != null)
					Doc.freeDocs(results);
			}
		}
	}

	@Override
	public void close()
	{
		if (collection != null)
		{
			collection.close();
		}
	}

	static
	{
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run()
			{
				Zvec.shutdown();
			}
		});
	}
}
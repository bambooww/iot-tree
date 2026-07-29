package org.iottree.ext.grpc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.iottree.core.UAManager;
import org.iottree.core.UAPrj;
import org.iottree.core.util.logger.*;
import org.iottree.core.UATag;
import org.iottree.core.UAVal;
import org.iottree.ext.grpc.RtSyn.PrjItem;
import org.iottree.ext.grpc.RtSyn.PrjList;
import org.iottree.ext.grpc.RtSyn.ReqClient;
import org.iottree.ext.grpc.RtSyn.ReqPrj;
import org.iottree.ext.grpc.RtSyn.ReqTagPaths;
import org.iottree.ext.grpc.RtSyn.ReqTagW;
import org.iottree.ext.grpc.RtSyn.Result;
import org.iottree.ext.grpc.RtSyn.TagItem;
import org.iottree.ext.grpc.RtSyn.TagList;
import org.iottree.ext.grpc.RtSyn.TagSynVal;
import org.iottree.ext.grpc.RtSyn.TagSynVals;

import io.grpc.stub.ServerCallStreamObserver;
import io.grpc.stub.StreamObserver;

public class IOTTreeServerImpl extends IOTTreeServerGrpc.IOTTreeServerImplBase
{
	static ILogger log = LoggerManager.getLogger(IOTTreeServerImpl.class) ;
	
	private final LinkedHashMap<String, ClientItem> client2item = new LinkedHashMap<>();
	private final Map<String, StreamObserver<TagSynVals>> clientObservers = new ConcurrentHashMap<>();

	private final Map<String, HashSet<String>> clientid2tagpaths = new ConcurrentHashMap<>();

	@Override
	public void listPrjs(ReqClient request, StreamObserver<PrjList> responseObserver)
	{
		PrjList.Builder b = PrjList.newBuilder();
		for (UAPrj prj : UAManager.getInstance().listPrjs())
		{
			PrjItem pi = PrjItem.newBuilder().setName(prj.getName()).setTitle(prj.getTitle()).build();
			b.addPrjs(pi);
		}
		PrjList prjs = b.build();
		responseObserver.onNext(prjs);
		responseObserver.onCompleted();
	}

	@Override
	public void listTagsInPrj(ReqPrj request, StreamObserver<TagList> responseObserver)
	{
		String prjn = request.getPrjName();
		UAPrj prj = UAManager.getInstance().getPrjByName(prjn);
		TagList.Builder b = TagList.newBuilder();
		if (prj != null)
		{
			for (UATag tag : prj.listTagsAll())
			{
				UAVal.ValTP vt = tag.getValTp();
				String tp = "";
				if (vt != null)
					tp = vt.getStr();
				TagItem ti = TagItem.newBuilder().setPrjName(prjn).setPath(tag.getNodePathCxt()).setIid(tag.getIID())
						.setTitle(tag.getTitle()).setTp(tp).build();
				b.addTags(ti);
			}
		}
		TagList taglist = b.build();
		responseObserver.onNext(taglist);
		responseObserver.onCompleted();
	}

	@Override
	public void setSynTagPath(ReqTagPaths request, StreamObserver<TagList> responseObserver)
	{
		String client_id = request.getClientId();
		int cc = request.getTagPathsCount();
		HashSet<String> ss = new HashSet<>(cc);
		for (int i = 0; i < cc; i++)
		{
			ss.add(request.getTagPaths(i));
		}
		clientid2tagpaths.put(client_id, ss);
		clearBuffer(client_id) ;
		
		TagList taglist = getTagListByClientId(client_id);
		responseObserver.onNext(taglist);
		responseObserver.onCompleted();
	}

	private TagList getTagListByClientId(String client_id)
	{
		TagsBuffer tbuf = getTagsBufferByClient(client_id) ;
		
		TagList.Builder b = TagList.newBuilder();
		if(tbuf!=null)
		{
			for (TagsBuffer.TagItem ti : tbuf.getPath2Tag().values())
			{
				UATag tag = ti.tag ;
				UAPrj prj = tag.getBelongToPrj() ;
				UAVal.ValTP vt = tag.getValTp();
				String tp = "";
				if (vt != null)
					tp = vt.getStr();
				TagItem tgi = TagItem.newBuilder().setPrjName(prj.getName()).setPath(tag.getNodePathCxt()).setIid(tag.getIID())
						.setTitle(tag.getTitle()).setTp(tp).build();
				b.addTags(tgi);
			}
		}
		TagList taglist = b.build();
		return taglist;
	}

	@Override
	public void getSynTagPath(ReqClient request, StreamObserver<TagList> responseObserver)
	{
		String client_id = request.getClientId() ;
		TagList taglist = getTagListByClientId(client_id);
		responseObserver.onNext(taglist);
		responseObserver.onCompleted();
	}

	@Override
	public void startSyn(ReqClient request, StreamObserver<TagSynVals> responseObserver)
	{
		String clientId = request.getClientId();
		clientObservers.put(clientId, responseObserver);
	}

	@Override
	public void stopSyn(ReqClient request, StreamObserver<Result> responseObserver)
	{
		String clientId = request.getClientId();
		StreamObserver<TagSynVals> ob = clientObservers.remove(clientId);
		if(ob!=null)
			ob.onCompleted();
		Result res = Result.newBuilder().setSucc(true).build() ;
		responseObserver.onNext(res);
		responseObserver.onCompleted();
	}

	@Override
	public void writeTagVal(ReqTagW request, StreamObserver<Result> responseObserver)
	{
		writeOrSetTagVal(true,request, responseObserver) ;
	}

	@Override
	public void setTagVal(ReqTagW request, StreamObserver<Result> responseObserver)
	{
		writeOrSetTagVal(false,request, responseObserver) ;
	}
	
	private void writeOrSetTagVal(boolean b_write,ReqTagW request, StreamObserver<Result> responseObserver)
	{
		String client_id = request.getClientId() ;
		// TODO may check client right
		
		String tagp = request.getTagPath() ;
		String strv = request.getStrVal() ;
		TagsBuffer tagbuf = this.getTagsBufferByClient(client_id) ;
		UATag tag = null ;
		if(tagbuf!=null)
		{
			TagsBuffer.TagItem ti = tagbuf.getPath2Tag().get(tagp) ;
			if(ti!=null)
				tag = ti.tag ;
		}
		Result res = null;
		if(tag!=null)
		{
			StringBuilder failedr = new StringBuilder() ;
			boolean bres =true;
			if(b_write)
				bres = tag.RT_writeValStr(strv, failedr) ;
			else
				tag.RT_setValRawStr(strv);

			res = Result.newBuilder().setSucc(bres).setInfo(failedr.toString()).build() ;
		}
		else
		{
			res = Result.newBuilder().setSucc(false).setInfo("no tag found").build() ;
		}
		responseObserver.onNext(res);
		responseObserver.onCompleted();
	}
	
	private transient Map<String,TagsBuffer> client2tagsbuf = new ConcurrentHashMap<>() ;
	
	private void clearBuffer(String clientid)
	{
		client2tagsbuf.remove(clientid) ;
	}
	
	private TagsBuffer getTagsBufferByClient(String clientId)
	{
		HashSet<String> tagps = clientid2tagpaths.get(clientId) ;
		if(tagps==null||tagps.size()<=0)
			return null ;
		
		TagsBuffer tbuf = client2tagsbuf.get(clientId) ;
		if(tbuf==null)
		{
			tbuf = new TagsBuffer(clientId,tagps) ;
			client2tagsbuf.put(clientId,tbuf) ;
		}
		return tbuf ;
	}
		

//	private TagSynVals RT_fetchTagValsByClientId(String clientId)
//	{
//		
//	}

	void RT_sendSynTagsToClients()
	{
		ArrayList<String> clientids = new ArrayList<>() ;
		clientids.addAll(this.clientid2tagpaths.keySet()) ;
		for(String clientid:clientids)
		{
			try
			{
				sendSynTagsToClient(clientid) ;
			}
			catch(Exception ee)
			{
				log.warn(ee.getMessage(),ee);
				if(log.isDebugEnabled())
					log.debug("sendSynTagsToClient err clientid="+clientid, ee);
			}
		}
	}
	/**
	 * send syn tag values to client string tag_path = 1 ; int32 iid = 2 ; int64
	 * update_dt = 3 ; int64 change_dt = 4; string str_val = 5 ;
	 * 
	 * @param clientId
	 * @param message
	 */
	private void sendSynTagsToClient(String clientId)
	{
		StreamObserver<TagSynVals> observer = clientObservers.get(clientId);
		if (observer == null)
			return;

		TagsBuffer tbuf = getTagsBufferByClient(clientId) ;
		if(tbuf==null)
			return  ;
		
		TagSynVals synvals = tbuf.filterUpdatedTagSynVals(tbuf._bNew) ;
		
		if(synvals.getTagValsCount()<=0)
			return ;
		
// 		TagSynVals synvals = RT_fetchTagValsByClientId(clientId);
		if (synvals == null)
			return;

		ServerCallStreamObserver<TagSynVals> scso = (ServerCallStreamObserver<TagSynVals>) observer;
		if(scso.isReady())
		{
			scso.onNext(synvals);
			if(log.isDebugEnabled())
				log.debug("send syn tags num="+synvals.getTagValsCount());
			tbuf._bNew = false; //send ok set
		}
		
//		scso.setOnReadyHandler(() -> {
//			if (scso.isReady()) //called in thread pool
//			{
//				scso.onNext(synvals);
//			}
//		});
	}

	public List<ClientItem> RT_listClientItems()
	{
		ArrayList<ClientItem> rets = new ArrayList<>();
		rets.addAll(client2item.values());
		return rets;
	}

	public List<String> RT_listClientSyning()
	{
		ArrayList<String> rets = new ArrayList<>();
		rets.addAll(this.clientObservers.keySet());
		return rets;
	}
}

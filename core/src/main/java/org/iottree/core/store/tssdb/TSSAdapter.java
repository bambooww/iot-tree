package org.iottree.core.store.tssdb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

/**
 * 支持Tag进行一定缓存长度的记录支持功能，支持数据快速记录，能够快速获取Tags数据的所有变化
 * 
 * 可以认为是一个简化版时序数据库
 * 
 * 支持bool int float 三种数据类型
 * 
 * @author jason.zhu
 */
public abstract class TSSAdapter
{
	public static interface ISavedListener
	{
		/**
		 * 
		 * @param b_insert
		 *            true=insert false=update
		 * @param tagsegs
		 * @param valseg
		 * @param saved_enddt
		 *            结束时间不能使用valseg，很可能并发情况下，valseg中的值会被改变
		 */
		public void onTagSegSaved(boolean b_insert, TSSTagSegs<?> tagsegs, Integer tagidx, TSSValSeg<?> valseg,
				long saved_enddt);

		// public void onTagSegUpdated(TSSTagSegs<?> tagsegs,TSSValSeg<?>
		// valseg,long saved_enddt);
	}

	/**
	 * 打包保存监听器
	 * 
	 * @author jason.zhu
	 *
	 */
	public static interface IPkSavedListener
	{
		/**
		 * 
		 * @param segs
		 */
		public void onTagSegsSaved(List<TSSSavePK> savepks);

		// public void onTagSegUpdated(TSSTagSegs<?> tagsegs,TSSValSeg<?>
		// valseg,long saved_enddt);
	}
	// protected String name = null ;

	protected HashMap<String, TSSTagSegs<Boolean>> tag2segs_bool = null;// new
																		// HashMap<>()
																		// ;
	protected HashMap<String, TSSTagSegs<Long>> tag2segs_int = null;
	protected HashMap<String, TSSTagSegs<Double>> tag2segs_float = null;

	protected Map<String, TSSTagParam> alltag2pm = null;

	protected int maxPointNum = 10000;

	// protected long minSaveGapMS = 1000 ;

	private Thread th = null;

	private ISavedListener savedLis = null;

	private IPkSavedListener pkSavedLis = null;

	/**
	 * 对应于独立的目录
	 * 
	 * @param name
	 */
	protected TSSAdapter()
	{
	}

	/**
	 * must be called before init and run
	 * 
	 * @param tag2vt
	 * @return
	 */
	public TSSAdapter asTagParams(List<TSSTagParam> pms)
	{
		HashMap<String, TSSTagParam> tag2pm = new HashMap<>();
		for (TSSTagParam p : pms)
			tag2pm.put(p.tag, p);

		alltag2pm = tag2pm;
		return this;
	}

	// public TSSAdapter asSavedListener(ISavedListener lis)
	// {
	// savedLis = lis ;
	// return this ;
	// }

	public TSSAdapter asPkSavedListener(IPkSavedListener lis)
	{
		pkSavedLis = lis;
		return this;
	}

	protected abstract long getSaveIntervalMS();

	private void checkCanStart()
	{
		if (alltag2pm == null)
		{
			throw new RuntimeException("no tag 2 param map set");
		}
		if (this.getIO() == null)
			throw new RuntimeException("no IO gotten");
	}

	protected boolean initOk = false;

	/**
	 * call by outer
	 * 
	 * @param failedr
	 * @return
	 */
	public final boolean RT_init(boolean force_reinit, StringBuilder failedr)
	{
		if (!force_reinit && initOk)
		{
			return true;
		}

		if (RT_init(failedr))
		{
			initOk = true;
			return true;
		}
		return false;
	}

	protected boolean RT_init(StringBuilder failedr) // throws Exception
	{
		if (alltag2pm == null)
		{
			failedr.append("no tag 2 param map set");
			return false;
		}

		try
		{
			TSSIO io = this.getIO();

			HashMap<String, Integer> tag2idx = io.getTagsMap();

			HashMap<Integer, TSSValSegFT<Boolean>> tagidx2lastseg_bool = io.<Boolean>readTagIdx2MinMaxSeg(Boolean.class);
			HashMap<Integer, TSSValSegFT<Long>> tagidx2lastseg_int = io.<Long>readTagIdx2MinMaxSeg(Long.class);
			HashMap<Integer, TSSValSegFT<Double>> tagidx2lastseg_float = io.<Double>readTagIdx2MinMaxSeg(Double.class);

			HashMap<String, TSSTagSegs<Boolean>> t2ts_bool = new HashMap<>();
			HashMap<String, TSSTagSegs<Long>> t2ts_int = new HashMap<>();
			HashMap<String, TSSTagSegs<Double>> t2ts_float = new HashMap<>();

			for (Map.Entry<String, TSSTagParam> tag2vt : alltag2pm.entrySet())
			{
				String tag = tag2vt.getKey();
				Integer idx = tag2idx.get(tag);
				if (idx == null)
					continue; // no data
				TSSTagParam pm = tag2vt.getValue();

				if (pm.isValInt())
				{
					TSSValSegFT<Long> ft = tagidx2lastseg_int.get(idx);
					if (ft == null)
						continue;
					if(ft.from.equals(ft.to))
						ft.to = ft.from ;//只有一条记录的特殊情况
					
					TSSTagSegs<Long> ts = new TSSTagSegs<Long>(this, tag, idx, pm,ft.from, ft.to, false);
					t2ts_int.put(tag, ts);
					continue;
				}

				if (pm.isValBool())
				{
					TSSValSegFT<Boolean> ft = tagidx2lastseg_bool.get(idx);
					if (ft == null)
						continue;
					if(ft.from.equals(ft.to))
						ft.to = ft.from ;
					TSSTagSegs<Boolean> ts = new TSSTagSegs<Boolean>(this, tag, idx, pm,ft.from, ft.to, false);
					t2ts_bool.put(tag, ts);
					continue;
				}

				if (pm.isValFloat())
				{
					TSSValSegFT<Double> ft = tagidx2lastseg_float.get(idx);
					if (ft == null)
						continue;
					if(ft.from.equals(ft.to))
						ft.to = ft.from ;
					TSSTagSegs<Double> ts = new TSSTagSegs<Double>(this, tag, idx, pm,ft.from, ft.to, false);
					t2ts_float.put(tag, ts);
					continue;
				}
			}

			tag2segs_bool = t2ts_bool;
			tag2segs_int = t2ts_int;
			tag2segs_float = t2ts_float;
			return true;
		}
		catch ( Exception ee)
		{
			ee.printStackTrace();
			failedr.append(ee.getMessage());
			return false;
		}
	}

	public TSSTagSegs<?> getTagSegs(String tag)
	{
		TSSTagSegs<?> ret = null;
		if (tag2segs_int != null)
		{
			ret = tag2segs_int.get(tag);
			if (ret != null)
				return ret;
		}

		if (tag2segs_float != null)
		{
			ret = tag2segs_float.get(tag);
			if (ret != null)
				return ret;
		}

		if (tag2segs_bool != null)
		{
			ret = tag2segs_bool.get(tag);
			if (ret != null)
				return ret;
		}
		return null;
	}

	// protected abstract DBConnPool getConnPool() ;

	protected abstract TSSIO getIO();

	// public String getName()
	// {
	// return this.name ;
	// }

	private Runnable runner = new Runnable() {

		@Override
		public void run()
		{
			try
			{

				while (th != null)
				{
					doRunInLoop();
				}
			}
			finally
			{
				th = null;
			}
		}
	};

	// private long lastSaveDT = -1 ;

	private synchronized void doRunInLoop()
	{
		try
		{
			// long ddt = System.currentTimeMillis()-lastSaveDT;
			// if(ddt<this.minSaveGapMS)
			// this.wait(this.minSaveGapMS-ddt);
			// else
			// this.wait(minSaveGapMS); //wait for notify
			Thread.sleep(getSaveIntervalMS());

		}
		catch ( Exception ee)
		{
		}

		// if(System.currentTimeMillis()-lastSaveDT<this.minSaveGapMS)
		// return ;

		try
		{
			doSave();
		}
		catch ( Exception ee)
		{
			ee.printStackTrace();
		}
		finally
		{
			try
			{// try save gracefully
				doSave();
			}
			catch ( Throwable t)
			{
				t.printStackTrace();
			}
			// lastSaveDT = System.currentTimeMillis() ;
		}
	}

	private void doSave() throws Exception
	{
		TSSIO io = getIO();
		ArrayList<TSSTagSegs<?>> segss = new ArrayList<>();
		// for(TSSTagSegs<Boolean> r:this.tag2segs_bool.values())
		// {
		// segss.addAll(this.tag2segs_bool.values()) ;
		// }
		// for(TSSTagSegs<Long> r:this.tag2segs_int.values())
		// {
		// io.saveTag(r);
		// }
		// for(TSSTagSegs<Double> r:this.tag2segs_float.values())
		// {
		// io.saveTag(r);
		// }
		//
		segss.addAll(this.tag2segs_bool.values());
		segss.addAll(this.tag2segs_int.values());
		segss.addAll(this.tag2segs_float.values());

		List<TSSSavePK> spks = io.saveTagSegs(segss);

		//
		if (this.pkSavedLis != null && spks != null && spks.size() > 0)
		{
//			ArrayList<TSSTagSegs<?>> ss = new ArrayList<>(spks.size());
//			for (TSSSavePK spk : spks)
//				ss.add(spk.segs);
			this.fireTagSegsSaved(spks);
		}

	}

	// protected final void fireTagValSegSaved(boolean b_insert,TSSTagSegs<?>
	// tagsegs,Integer tagidx,TSSValSeg<?> valseg,long saved_enddt)
	// {
	// if(savedLis==null)
	// return ;
	// savedLis.onTagSegSaved(b_insert,tagsegs,tagidx, valseg,saved_enddt);
	// }

	protected final void fireTagSegsSaved(List<TSSSavePK> savepks)
	{
		if (savepks == null)
			return;
		pkSavedLis.onTagSegsSaved(savepks);
	}

	// protected final void fireTagSegUpdated(TSSTagSegs<?> tagsegs,TSSValSeg<?>
	// valseg,long saved_enddtl)
	// {
	// if(savedLis==null)
	// return ;
	// savedLis.onTagSegUpdated(tagsegs, valseg, saved_enddtl);
	// }

	public int getUnsavedSegsNum()
	{
		int n = 0;
		if (tag2segs_bool != null)
		{
			for (TSSTagSegs<?> ts : tag2segs_bool.values())
				n += ts.getUnsavedSegsNum();
		}

		if (tag2segs_int != null)
		{
			for (TSSTagSegs<?> ts : tag2segs_int.values())
				n += ts.getUnsavedSegsNum();
		}
		if (tag2segs_float != null)
		{
			for (TSSTagSegs<?> ts : tag2segs_float.values())
				n += ts.getUnsavedSegsNum();
		}

		return n;
	}

	public <T> void addTagValue(String tag, long dt, boolean bvalid, T val)
	{
		TSSTagParam pm = alltag2pm.get(tag);
		if (pm == null)
			throw new IllegalArgumentException("no param found with tag=" + tag);

		if (bvalid)
		{// 无效状态因为值可以认为是不变的，也就不需要gap控制数据量了
			if (pm.minRecordGap > 0 && (dt - pm.lastAddDT < pm.minRecordGap))
				return;// ignore to record
		}

		try
		{
			addTagValue(pm, dt, bvalid, val);
		}
		finally
		{
			pm.lastAddDT = System.currentTimeMillis();
		}
	}

	private <T> void addTagValue(TSSTagParam pm, long dt, boolean bvalid, T val)
	{
		String tag = pm.tag;

		if (pm.isValInt())
		{
			Long lv = null;
			if (val != null)
				lv = ((Number) val).longValue();

			TSSTagSegs<Long> ts = tag2segs_int.get(tag);
			if (ts == null)
			{
				// Integer idx = getIO().getOrAddTagIdx(tag) ;
				TSSValSeg<Long> lastseg = new TSSValSeg<>(dt, bvalid, lv, true);// b_new)
				ts = new TSSTagSegs<>(this, tag, null, pm,lastseg, lastseg, true);// (this,tag,100,5,20000)
				// ;
				tag2segs_int.put(tag, ts);
			}
			else
			{
				if (bvalid)
					ts.addPointValid(dt, lv);
				else
					ts.addPointInvalid(dt, false);
			}
			return;
		}

		if (pm.isValFloat())
		{
			Double dv = null;
			if (val != null)
				dv = ((Number) val).doubleValue();

			TSSTagSegs<Double> ts = tag2segs_float.get(tag);
			if (ts == null)
			{
				// Integer idx = getIO().getOrAddTagIdx(tag) ; // may cause
				// speed low
				TSSValSeg<Double> lastseg = new TSSValSeg<>(dt, bvalid, dv, true);// b_new)
				ts = new TSSTagSegs<>(this, tag, null, pm,lastseg, lastseg, true);// (this,tag,100,5,20000)
				// ;
				tag2segs_float.put(tag, ts);
			}
			else
			{
				if (bvalid)
					ts.addPointValid(dt, dv);
				else
					ts.addPointInvalid(dt, false);
			}
			return;
		}

		if (pm.isValBool())
		{
			TSSTagSegs<Boolean> ts = tag2segs_bool.get(tag);
			if (ts == null)
			{
				// Integer idx = getIO().getOrAddTagIdx(tag) ;
				TSSValSeg<Boolean> lastseg = new TSSValSeg<>(dt, bvalid, (Boolean) val, true);// b_new)
				ts = new TSSTagSegs<>(this, tag, null, pm,lastseg, lastseg, true);// (this,tag,100,5,20000)
				// ;
				tag2segs_bool.put(tag, ts);
			}
			else
			{
				if (bvalid)
					ts.addPointValid(dt, (Boolean) val);
				else
					ts.addPointInvalid(dt, false);
			}
			return;
		}
	}

	public synchronized boolean RT_start()
	{
		if (th != null)
			return true;

		checkCanStart();

		StringBuilder failedr = new StringBuilder();
		if (!RT_init(false, failedr))
		{
			throw new RuntimeException(failedr.toString());
		}

		th = new Thread(runner);
		th.start();
		return true;
	}

	public synchronized void RT_stop()
	{
		th = null;
	}

	public boolean RT_isRunning()
	{
		return th != null;
	}

	public static class RTInfo
	{
		long lastSaveDT = -1;
		long lastSaveCost = -1;
		int lastSaveSegNum = -1;
		int unsaveSegsNum = -1;
		boolean b_running = false;

		private JSONObject jo = new JSONObject();

		public JSONObject toJO()
		{
			jo.put("last_save_dt", lastSaveDT);
			jo.put("last_save_cost", lastSaveCost);
			jo.put("last_save_num", lastSaveSegNum);
			jo.put("not_save_num", unsaveSegsNum);
			jo.put("running", b_running);
			return jo;
		}
	}

	private transient RTInfo rtInfo = new RTInfo();

	public RTInfo RT_getInfo()
	{
		rtInfo.unsaveSegsNum = getUnsavedSegsNum();

		TSSIO io = this.getIO();
		rtInfo.lastSaveDT = io.RT_getLastSaveDT();
		rtInfo.lastSaveCost = io.RT_getLastSaveCost();
		rtInfo.lastSaveSegNum = io.RT_getLastSaveSegNum();
		rtInfo.b_running = this.RT_isRunning();

		return rtInfo;
	}

	// read
	
	public final HashMap<String,Integer> getTagsMap()  throws Exception
	{
		return this.getIO().getTagsMap() ;
	}
	
	public <T> List<TSSValSeg<T>> readValSegs(String tag, long from_dt, long to_dt,boolean b_desc,int limit_num) throws Exception
	{
		@SuppressWarnings("unchecked")
		TSSTagSegs<T> ts = (TSSTagSegs<T>)getTagSegs(tag) ;
		if(ts==null)
			return null ;
		
		return this.getIO().readValSegs(ts, from_dt, to_dt,b_desc,limit_num);
	}

	public <T> List<TSSValSeg<T>> readValSegs(TSSTagSegs<T> ts, long from_dt, long to_dt,boolean b_desc,int limit_num) throws Exception
	{
		// TODO same cache to improve speed

		return this.getIO().readValSegs(ts, from_dt, to_dt,b_desc,limit_num);
	}
	
	
	
	public <T> TSSValSeg<T> readValSegAt(String tag, long at_dt) throws Exception
	{
		@SuppressWarnings("unchecked")
		TSSTagSegs<T> ts = (TSSTagSegs<T>)getTagSegs(tag) ;
		if(ts==null)
			return null ;
		
		return this.getIO().readValSegAt(ts, at_dt);
	}

	public <T> TSSValSeg<T> readValSegAt(TSSTagSegs<T> ts, long at_dt) throws Exception
	{
		return this.getIO().readValSegAt(ts, at_dt);
	}
	
	public <T> TSSValSegHit<T> readValSegAt(String tag,long at_dt,boolean b_prev,boolean b_next) throws Exception
	{
		@SuppressWarnings("unchecked")
		TSSTagSegs<T> ts = (TSSTagSegs<T>)getTagSegs(tag) ;
		if(ts==null)
			return null ;
		return this.getIO().readValSegAt(ts, at_dt,b_prev,b_next);
	}
	public <T> TSSValSegHit<T> readValSegAt(TSSTagSegs<T> ts,long at_dt,boolean b_prev,boolean b_next) throws Exception
	{
		return this.getIO().readValSegAt(ts, at_dt,b_prev,b_next);
	}
	
	
	public <T> List<TSSValSeg<T>> readValSegAt2(String tag, long at_dt1, long at_dt2) throws Exception
	{
		@SuppressWarnings("unchecked")
		TSSTagSegs<T> ts = (TSSTagSegs<T>)getTagSegs(tag) ;
		if(ts==null)
			return null ;
		
		return this.getIO().readValSegAt2(ts, at_dt1, at_dt2);
	}

	public <T> List<TSSValSeg<T>> readValSegAt2(TSSTagSegs<T> ts, long at_dt1, long at_dt2) throws Exception
	{
		return this.getIO().readValSegAt2(ts, at_dt1, at_dt2);
	}

	
	public <T> TSSValPt<T> readValPt(String tag, long at_dt) throws Exception
	{
		@SuppressWarnings("unchecked")
		TSSTagSegs<T> ts = (TSSTagSegs<T>)getTagSegs(tag) ;
		if(ts==null)
			return null ;
		return this.getIO().readValPt(ts, at_dt);
	}
	public <T> TSSValPt<T> readValPt(TSSTagSegs<T> ts, long at_dt) throws Exception
	{
		return this.getIO().readValPt(ts, at_dt);
	}

	public <T> TSSValSegHitNext<T> readValSegAtAndNext(String tag, long at_dt) throws Exception
	{
		@SuppressWarnings("unchecked")
		TSSTagSegs<T> ts = (TSSTagSegs<T>)getTagSegs(tag) ;
		if(ts==null)
			return null ;
		return this.getIO().readValSegAtAndNext(ts, at_dt);
	}
	public <T> TSSValSegHitNext<T> readValSegAtAndNext(TSSTagSegs<T> ts, long at_dt) throws Exception
	{
		return this.getIO().readValSegAtAndNext(ts, at_dt);
	}

	public <T> TSSValSegHitPrev<T> readValSegAtAndPrev(String tag, long at_dt) throws Exception
	{
		@SuppressWarnings("unchecked")
		TSSTagSegs<T> ts = (TSSTagSegs<T>)getTagSegs(tag) ;
		if(ts==null)
			return null ;
		return this.getIO().readValSegAtAndPrev(ts, at_dt);
	}
	public <T> TSSValSegHitPrev<T> readValSegAtAndPrev(TSSTagSegs<T> ts, long at_dt) throws Exception
	{
		return this.getIO().readValSegAtAndPrev(ts, at_dt);
	}
	
	public <T> TSSValSeg<T> readValSegNext(TSSTagSegs<T> ts,TSSValSeg<T> vs) throws Exception
	{
		return this.getIO().readValSegNext(ts, vs);
	}
	
	public <T> TSSValSeg<T> readValSegAtOrNext(TSSTagSegs<T> ts,long at_dt) throws Exception
	{
		return this.getIO().readValSegAtOrNext(ts, at_dt);
	}
	
	
	public <T> void iterValSegsFrom(String tag, long from_dt,IValSegSelectCB<T> cb) throws Exception
	{
		@SuppressWarnings("unchecked")
		TSSTagSegs<T> ts = (TSSTagSegs<T>)getTagSegs(tag) ;
		if(ts==null)
			return  ;
		this.getIO().iterValSegsFrom(ts, from_dt, cb);
	}
	public <T> void iterValSegsFrom(TSSTagSegs<T> ts,long from_dt,IValSegSelectCB<T> cb) throws Exception
	{
		this.getIO().iterValSegsFrom(ts, from_dt, cb);
	}
}

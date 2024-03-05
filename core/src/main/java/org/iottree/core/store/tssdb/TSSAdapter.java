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

			HashMap<Integer, TSSValSeg<Boolean>> tagidx2lastseg_bool = io.<Boolean>loadLastTagsSeg(Boolean.class);
			HashMap<Integer, TSSValSeg<Long>> tagidx2lastseg_int = io.<Long>loadLastTagsSeg(Long.class);
			HashMap<Integer, TSSValSeg<Double>> tagidx2lastseg_float = io.<Double>loadLastTagsSeg(Double.class);

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
					TSSValSeg<Long> lastseg = tagidx2lastseg_int.get(idx);
					if (lastseg == null)
						continue;
					TSSTagSegs<Long> ts = new TSSTagSegs<Long>(this, tag, pm, lastseg, false);
					t2ts_int.put(tag, ts);
					continue;
				}

				if (pm.isValBool())
				{
					TSSValSeg<Boolean> lastseg = tagidx2lastseg_bool.get(idx);
					if (lastseg == null)
						continue;

					TSSTagSegs<Boolean> ts = new TSSTagSegs<Boolean>(this, tag, pm, lastseg, false);
					t2ts_bool.put(tag, ts);
					continue;
				}

				if (pm.isValFloat())
				{
					TSSValSeg<Double> lastseg = tagidx2lastseg_float.get(idx);
					if (lastseg == null)
						continue;
					TSSTagSegs<Double> ts = new TSSTagSegs<Double>(this, tag, pm, lastseg, false);
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

		io.saveTagSegs(segss);
	}

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

		if (pm.isValInt())
		{
			Long lv = null;
			if (val != null)
				lv = ((Number) val).longValue();

			TSSTagSegs<Long> ts = tag2segs_int.get(tag);
			if (ts == null)
			{
				TSSValSeg<Long> lastseg = new TSSValSeg<>(dt, bvalid, lv, true);// b_new)
				ts = new TSSTagSegs<>(this, tag, pm, lastseg, true);// (this,tag,100,5,20000)
																	// ;
				tag2segs_int.put(tag, ts);
			}
			else
			{
				ts.addPointValid(dt, lv);
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
				TSSValSeg<Double> lastseg = new TSSValSeg<>(dt, bvalid, dv, true);// b_new)
				ts = new TSSTagSegs<>(this, tag, pm, lastseg, true);// (this,tag,100,5,20000)
																	// ;
				tag2segs_float.put(tag, ts);
			}
			else
			{
				ts.addPointValid(dt, dv);
			}
			return;
		}

		if (pm.isValBool())
		{
			TSSTagSegs<Boolean> ts = tag2segs_bool.get(tag);
			if (ts == null)
			{
				TSSValSeg<Boolean> lastseg = new TSSValSeg<>(dt, bvalid, (Boolean) val, true);// b_new)
				ts = new TSSTagSegs<>(this, tag, pm, lastseg, true);// (this,tag,100,5,20000)
																	// ;
				tag2segs_bool.put(tag, ts);
			}
			else
			{
				ts.addPointValid(dt, (Boolean) val);
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
		if (!RT_init(failedr))
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
}

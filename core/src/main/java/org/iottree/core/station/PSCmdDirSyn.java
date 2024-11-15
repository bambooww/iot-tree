package org.iottree.core.station;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.iottree.core.Config;
import org.iottree.core.util.Convert;
import org.iottree.core.util.UpdateUtil;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.json.JSONArray;
import org.json.JSONObject;

public class PSCmdDirSyn extends PSCmd
{
	static ILogger log = LoggerManager.getLogger(PSCmdDirSyn.class);

	public static class SubFile
	{
		String subDir;

		String fileName;
	}

	public static class SubDirChksum
	{
		String subDir;

		HashMap<String, String> fn2chksum = new HashMap<>();

		public SubDirChksum(String subdir)
		{
			this.subDir = subdir;
		}

		public String getChksumByFn(String fn)
		{
			return fn2chksum.get(fn);
		}

		public HashSet<String> getFileNameSet()
		{
			HashSet<String> fns = new HashSet<>();
			fns.addAll(fn2chksum.keySet());
			return fns;
		}

		public JSONObject toJO()
		{
			JSONObject jo = new JSONObject();
			jo.put("subdir", this.subDir);
			jo.put("fn2chk", fn2chksum);
			return jo;
		}

		public void fromJO(JSONObject jo)
		{
			this.subDir = jo.getString("subdir");
			JSONObject fn2ck = jo.getJSONObject("fn2chk");
			for (String fn : fn2ck.keySet())
			{
				String chk = fn2ck.getString(fn);
				this.fn2chksum.put(fn, chk);
			}
		}
	}

	public static class DirChksum
	{
		String module;

		String path;

		HashMap<String, SubDirChksum> subDir2filechks = new HashMap<>();

		public DirChksum(String m, String p)
		{
			this.module = m;
			this.path = p;
		}

		public JSONObject toJO()
		{
			JSONObject jo = new JSONObject();
			jo.put("module", module);
			jo.put("path", path);
			JSONObject subd2fchks = new JSONObject();
			for (Map.Entry<String, SubDirChksum> d2fcks : this.subDir2filechks.entrySet())
			{
				subd2fchks.put(d2fcks.getKey(), d2fcks.getValue().toJO());
			}
			jo.put("subdir2filechks", subd2fchks);
			return jo;
		}

		public void fromJO(JSONObject jo)
		{
			this.module = jo.getString("module");
			this.path = jo.getString("path");
			JSONObject tmpjo = jo.getJSONObject("subdir2filechks");
			for (String n : tmpjo.keySet())
			{
				JSONObject jo0 = tmpjo.getJSONObject(n);
				SubDirChksum sbc = new SubDirChksum(n);
				sbc.fromJO(jo0);
				this.subDir2filechks.put(n, sbc);
			}
		}
	}

	public static class DirDiff
	{
		ArrayList<String> addSubFs = new ArrayList<>();

		ArrayList<String> updateSubFs = new ArrayList<>();

		ArrayList<String> delSubFs = new ArrayList<>();

		public DirDiff()
		{
		}

		public DirDiff(ArrayList<String> add_fs, ArrayList<String> update_fs, ArrayList<String> del_fs)
		{
			this.addSubFs = add_fs;
			this.updateSubFs = update_fs;
			this.delSubFs = del_fs;
		}

		public boolean isEmpty()
		{
			if (addSubFs != null && this.addSubFs.size() > 0)
				return false;
			if (updateSubFs != null && this.updateSubFs.size() > 0)
				return false;
			if (delSubFs != null && this.delSubFs.size() > 0)
				return false;

			return true;
		}

		public JSONObject toJO()
		{
			JSONObject jo = new JSONObject();
			if (addSubFs != null)
			{
				JSONArray jarr = new JSONArray(addSubFs);
				jo.put("add_fs", jarr);
			}
			if (updateSubFs != null)
			{
				JSONArray jarr = new JSONArray(updateSubFs);
				jo.put("update_fs", jarr);
			}
			if (delSubFs != null)
			{
				JSONArray jarr = new JSONArray(delSubFs);
				jo.put("del_fs", jarr);
			}
			return jo;
		}

		public void fromJO(JSONObject jo)
		{
			JSONArray jarr = jo.optJSONArray("add_fs");
			if (jarr != null)
			{
				ArrayList<String> ss = new ArrayList<>();
				int n = jarr.length();
				for (int i = 0; i < n; i++)
				{
					String tmps = jarr.getString(i);
					ss.add(tmps);
				}
				addSubFs = ss;
			}

			jarr = jo.optJSONArray("update_fs");
			if (jarr != null)
			{
				ArrayList<String> ss = new ArrayList<>();
				int n = jarr.length();
				for (int i = 0; i < n; i++)
				{
					String tmps = jarr.getString(i);
					ss.add(tmps);
				}
				updateSubFs = ss;
			}

			jarr = jo.optJSONArray("del_fs");
			if (jarr != null)
			{
				ArrayList<String> ss = new ArrayList<>();
				int n = jarr.length();
				for (int i = 0; i < n; i++)
				{
					String tmps = jarr.getString(i);
					ss.add(tmps);
				}
				delSubFs = ss;
			}
		}
	}

	public final static String CMD = "dir_syn";

	@Override
	public String getCmd()
	{
		return CMD;
	}

	public static String calDirBase(String module)
	{
		int k = module.indexOf(':');
		String m = module;
		String mn = null;

		if (k > 0)
		{
			m = module.substring(0, k);
			mn = module.substring(k + 1);
		}

		String r = null;
		switch (m)
		{
		case "data":
			r = Config.getDataDirBase();
			break;
		case "lib":
			r = Config.getLibDirBase();
			break;
		case "web":
			r = Config.getWebappBase();
			break;
		default:
			return null;
		}

		if (Convert.isNotNullEmpty(mn))
			return r + "/" + mn;
		return r;
	}

	public static File calDir(String module, String path)
	{
		String fp = calDirBase(module);
		if (Convert.isNullOrEmpty(fp))
			return null;
		return new File(fp + "/" + path);
	}

	private static void calSubFileChecksum(File curdir, String curbase, DirChksum subd2chksum) throws Exception
	{
		for (File subf : curdir.listFiles())
		{
			if (subf.isDirectory())
			{
				calSubFileChecksum(subf, curbase + subf.getName() + "/", subd2chksum);
				continue;
			}

			// long st = System.currentTimeMillis() ;
			String chksum = UpdateUtil.getHashByFile(subf);
			// long et = System.currentTimeMillis() ;
			// System.out.println("cal hash cost="+(et-st)) ;

			SubDirChksum sdc = subd2chksum.subDir2filechks.get(curbase);
			if (sdc == null)
			{
				sdc = new SubDirChksum(curbase);
				subd2chksum.subDir2filechks.put(curbase, sdc);

			}
			sdc.fn2chksum.put(subf.getName(), chksum);
			// subd2chksum.subDir2filechks.put(curbase+subf.getName(), chksum) ;
		}
	}

	public PSCmdDirSyn asCheckDiff(String module, String path) throws Exception
	{
		File dir = calDir(module, path);
		if (dir == null || !dir.exists() || !dir.isDirectory())
			throw new IllegalArgumentException(module + ":" + path + " has no dir found");

		// HashMap<String,String> subf2chksum = new HashMap<>() ;
		DirChksum dcs = new DirChksum(module, path);
		calSubFileChecksum(dir, "/", dcs);// subf2chksum) ;

		this.asParams(Arrays.asList("diff"));
		// JSONObject jo= new JSONObject() ;
		// jo.put("module", module) ;
		// jo.put("path", path) ;

		// jo.put("subf2chksum", subf2chksum) ;
		this.asCmdDataJO(dcs.toJO());
		return this;
	}

	public PSCmdDirSyn asDoSyn(String token,String module, String path, ArrayList<String> add_subfs, ArrayList<String> update_subfs,
			ArrayList<String> del_subfs) throws Exception
	{
		File dir = calDir(module, path);
		if (dir == null || !dir.exists() || !dir.isDirectory())
			throw new IllegalArgumentException(module + ":" + path + " has no dir found");

		// DirChksum dcs = new DirChksum(module,path);
		// calSubFileChecksum(dir,"/",dcs);//subf2chksum) ;

		DirDiff diff = new DirDiff(add_subfs, update_subfs, del_subfs);

		JSONObject jo = diff.toJO();
		jo.put("module", module);
		jo.put("path", path);
		jo.put("token", token);
		this.asParams(Arrays.asList("syn"));
		// System.out.println(jo.toString());
		this.asCmdDataJO(jo);
		return this;
	}

	@Override
	public void RT_onRecvedInStationLocal(StationLocal sl) throws Exception
	{
		String tp = this.getParamByIdx(0);
		if (Convert.isNullOrEmpty(tp))
			return;

		switch (tp)
		{
		case "diff":
			RT_doDiff(sl);
			break;
		case "syn":
			RT_doSyn(sl);
			break;
		default:
			return;
		}
	}

	private void RT_doDiff(StationLocal sl) throws Exception
	{
		JSONObject jo = this.getCmdDataJO();
		if (jo == null)
			return;
		String module = jo.optString("module");
		String path = jo.optString("path");

		// JSONObject subf2chkjo = jo.optJSONObject("subf2chksum") ;
		if (Convert.isNullOrEmpty(module) || Convert.isNullOrEmpty(path)) // ||subf2chkjo==null)
			return;

		DirChksum dcs = new DirChksum(module, path);
		dcs.fromJO(jo);
		File localdir = calDir(module, path);
		if (localdir == null)
			return;

		// HashMap<String,String> subf2chk = new HashMap<>() ;
		// for(String n : subf2chkjo.keySet())
		// {
		// String subf = subf2chkjo.getString(n) ;
		// subf2chk.put(n, subf) ;
		// }
		DirDiff dd = calLocalDirDiff(localdir, dcs);
		if (dd == null)
			return;

		PSCmdDirSynAck ack = new PSCmdDirSynAck();
		ack.asAckDiff(dd);
		StringBuilder failedr = new StringBuilder();
		if (!sl.RT_sendCmd(ack, failedr))
		{
			//
			if (log.isDebugEnabled())
			{
				log.debug(failedr.toString());
			}
		}
	}

	private void RT_doSyn(StationLocal sl) throws Exception
	{
		JSONObject jo = this.getCmdDataJO();
		if (jo == null)
			return;
		String module = jo.optString("module");
		String path = jo.optString("path");
		String token = jo.optString("token") ;
		// JSONObject subf2chkjo = jo.optJSONObject("subf2chksum") ;
		if (Convert.isNullOrEmpty(module) || Convert.isNullOrEmpty(path)) // ||subf2chkjo==null)
			return;

		File caldir = calDir(module, path);
		if (caldir == null)
		{
			log.trace(module + ":" + path + " has no dir found");
			return;
		}

		DirDiff dd = new DirDiff();
		dd.fromJO(jo);

		String basedir = caldir.getCanonicalPath();
		if (log.isTraceEnabled())
			log.trace("PSCmdDirSyn RT_doSyn in dir=" + basedir);

		if (dd.isEmpty())
		{
			log.trace("PSCmdDirSyn RT_doSyn Diff is empty");
			return;
		}

		// do file down load and update or del

		for (String subf : dd.addSubFs)
		{//
			if (log.isTraceEnabled())
				log.trace("   RT_doSyn add file=" + subf);

			File tarf = new File(basedir + subf);
			readFromPlatformToFile(sl, token,module, path,subf, tarf);
			sendSynAck(sl, "add_fs", subf);
		}

		for (String subf : dd.updateSubFs)
		{//
			if (log.isTraceEnabled())
				log.trace("   RT_doSyn update file=" + subf);
			File tarf = new File(basedir + subf);
			readFromPlatformToFile(sl, token, module, path,subf, tarf) ;
			
			sendSynAck(sl, "update_fs", subf);
		}

		for (String subf : dd.delSubFs)
		{//
			if (log.isTraceEnabled())
				log.trace("   RT_doSyn del file=" + subf);
			File tarf = new File(basedir + subf);
			tarf.delete() ;
			sendSynAck(sl, "del_fs", subf);
		}
	}

	private void sendSynAck(StationLocal sl, String op, String subf)
	{
		PSCmdDirSynAck ack = new PSCmdDirSynAck();
		ack.asAckSyn(op, subf);
		StringBuilder failedr = new StringBuilder();
		if (!sl.RT_sendCmd(ack, failedr))
		{
			if (log.isDebugEnabled())
			{
				log.debug(failedr.toString());
			}
		}
	}
	
	public static final String TOKEN = "iottree_token";

	private static void readFromPlatformToFile(StationLocal sl, String token,
			String module, String path,String subf, File tarf)
			throws IOException
	{
		String url = "http://" + sl.getPlatfromHost() + ":" + sl.getPlatfromPort() + "/station_dl.jsp";
		URL u = new URL(url);
		HttpURLConnection http_conn = null;

		try
		{
			http_conn = (HttpURLConnection) u.openConnection();

			String urlpm = "module=" + module + "&path=" + path+"&subf="+subf+"&stationid="+sl.getStationId();

			http_conn.setRequestMethod("POST");
			http_conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			http_conn.setRequestProperty("Content-Length", String.valueOf(urlpm.length()));
			http_conn.setRequestProperty("User-Agent", "Mozilla/5.0");
			http_conn.setRequestProperty(TOKEN, token);

			http_conn.setDoOutput(true);
			try (DataOutputStream out = new DataOutputStream(http_conn.getOutputStream()))
			{
				out.writeBytes(urlpm);
				out.flush();
			}
			
			if(!tarf.getParentFile().exists())
				tarf.getParentFile().mkdirs() ;
			
			File tmpf = new File(tarf.getCanonicalPath()+".dling") ;
			// httpConn.connect();
			int responseCode = http_conn.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK)
			{
				//save to tmp
				try (InputStream inputs = http_conn.getInputStream();
						BufferedInputStream bufferedis = new BufferedInputStream(inputs);
						FileOutputStream fos = new FileOutputStream(tmpf))
				{
					byte[] buffer = new byte[4096];
					int readlen;

					while ((readlen = bufferedis.read(buffer)) != -1)
					{
						fos.write(buffer, 0, readlen);
					}
				}
				//
				tmpf.renameTo(tarf) ;
			}
			else
			{
				throw new IOException("No file to download. Server replied with HTTP code: " + responseCode);
			}
		}
		finally
		{
			if (http_conn != null)
				http_conn.disconnect();
		}
	}

	private static DirDiff calLocalDirDiff(File localdir, DirChksum remote_dcs) throws Exception
	{
		DirDiff dd = new DirDiff();
		calSubFileDirDiff(localdir, "/", remote_dcs, dd);
		return dd;
	}

	private static void calSubFileDirDiff(File curdir, String curbase, DirChksum remote_dcs, DirDiff diff)
			throws Exception
	{
		SubDirChksum r_dircs = remote_dcs.subDir2filechks.get(curbase);
		File[] localfs = curdir.listFiles();

		// diff cur dir files
		HashSet<String> r_fns = null;
		if (r_dircs != null)
			r_fns = r_dircs.getFileNameSet();
		if (r_fns == null)
			r_fns = new HashSet<>();

		for (File subf : localfs)
		{
			if (subf.isDirectory())
				continue;
			if(!subf.exists())
				continue ; // linux file name may has ���� and not existed
			String fn = subf.getName();

			r_fns.remove(fn);

			String chksum = UpdateUtil.getHashByFile(subf);
			String r_chksum = null;
			if (r_dircs == null || (r_chksum = r_dircs.getChksumByFn(fn)) == null)
			{
				diff.delSubFs.add(curbase + fn);
				continue;
			}

			if (chksum.equals(r_chksum))
				continue;// same

			diff.updateSubFs.add(curbase + fn);
		}

		for (String tmpf : r_fns)
			diff.addSubFs.add(curbase + tmpf);

		// more dir
		for (File subf : localfs)
		{
			if (!subf.isDirectory())
			{
				continue;
			}

			calSubFileDirDiff(subf, curbase + subf.getName() + "/", remote_dcs, diff);
		}
	}
}

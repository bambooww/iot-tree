package org.iottree.ext.av;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeMid;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.util.Convert;
import org.iottree.core.util.Lan;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.json.JSONArray;
import org.json.JSONObject;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;
import org.opencv.videoio.Videoio;

public class CameraCap_NM extends MNNodeMid
{
	static ILogger log = LoggerManager.getLogger(CameraCap_NM.class);

	static Lan lan = Lan.getLangInPk(CameraCap_NM.class);

	public static class CamItem
	{
		public final int index;

		public final String title;

		public final String path;

		public CamItem(int index, String title, String path)
		{
			this.index = index;
			this.title = title;
			this.path = path;
		}

		public String getShowTitle()
		{
			if (Convert.isNullOrEmpty(path))
				return title;
			return title + "[" + path + "]";
		}
	}

	public static class CamPM
	{
		public int width;
		public int height;
		public int fps;

		@Override
		public String toString()
		{
			return String.format("%dx%d@%dfps", width, height, fps);
		}
		
		public JSONObject toJO()
		{
			return new JSONObject().put("w",width).put("h", height).put("fps", fps) ;
		}
	}

	int cameraId = -1;

	int autoCancelAfterSec = -1;
	
	CamPM camPM = null ;
	
	boolean bTransGray = false;

	@Override
	public int getOutNum()
	{
		return 2;
	}

	@Override
	public String getTP()
	{
		return "camera_cap";
	}

	@Override
	public String getTPTitle()
	{
		return g("camera_cap");
	}

	@Override
	public String getColor()
	{
		return "#11caff";
	}

	@Override
	public String getIcon()
	{
		return "\\uf03d";
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		if (this.cameraId < 0)
		{
			failedr.append("no camera id set");
			return false;
		}

		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject ret = new JSONObject();
		ret.put("camera_id", this.cameraId);
		ret.putOpt("auto_cancel_after_sec", this.autoCancelAfterSec);
		ret.putOpt("trans_gray", bTransGray) ;
		if(camPM!=null)
		{
			ret.put("pm_w", this.camPM.width) ;
			ret.put("pm_h", this.camPM.height) ;
			ret.put("pm_fps", this.camPM.fps) ;
		}
		return ret;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		this.cameraId = jo.optInt("camera_id", -1);
		this.autoCancelAfterSec = jo.optInt("auto_cancel_after_sec", -1);
		this.bTransGray = jo.optBoolean("trans_gray", false) ;
		CamPM cpm = new CamPM() ;
		cpm.width = jo.optInt("pm_w", -1) ;
		cpm.height = jo.optInt("pm_h", -1) ;
		cpm.fps = jo.optInt("pm_fps", -1) ;
		this.camPM = cpm ;
		RT_delCap();
	}

	@Override
	public String getPmTitle()
	{
		if (this.cameraId < 0)
			return "";
		CamItem ci = getCachedCam(cameraId);
		if (ci == null)
			return "[" + cameraId + "]";
		return "[" + cameraId + "]" + ci.title;
	}

	@Override
	public boolean getShowOutTitleDefault()
	{
		return true;
	}

	@Override
	protected void RT_onBeforeNetRun()
	{
		try
		{

		}
		catch ( Exception ee)
		{
			ee.printStackTrace();
		}
	}

	@Override
	protected synchronized void RT_onAfterNetStop()
	{
		RT_delCap();
	}

	private VideoCapture videoCap = null;
	private Mat frame = new Mat();
	private Mat grayFrame = new Mat();
	private MatOfByte buffer = new MatOfByte();
	private transient long last_cap_dt = System.currentTimeMillis();

	private synchronized VideoCapture RT_getCap()
	{
		if (this.cameraId < 0)
			return null;
		if (videoCap != null)
			return videoCap;
		last_cap_dt = System.currentTimeMillis();
		frame = new Mat();
		buffer = new MatOfByte();
		VideoCapture vc = new VideoCapture(cameraId, Videoio.CAP_DSHOW);
		if(this.camPM.width>0&&this.camPM.height>0)
		{
			vc.set(Videoio.CAP_PROP_FRAME_WIDTH, this.camPM.width);
			vc.set(Videoio.CAP_PROP_FRAME_HEIGHT, this.camPM.height);
		}
		
		if(this.camPM.fps>0)
			vc.set(Videoio.CAP_PROP_FPS, 30);

		vc.set(Videoio.CAP_PROP_FOURCC, VideoWriter.fourcc('M', 'J', 'P', 'G'));
		return videoCap = vc;
	}

	private synchronized void RT_delCap()
	{
		if (videoCap == null)
			return;
		try
		{
			videoCap.release();
		}
		finally
		{
			videoCap = null;
		}
	}

	@Override
	protected synchronized void RT_onNetTick1S()
	{
		if (videoCap == null || autoCancelAfterSec <= 0)
			return;

		if (System.currentTimeMillis() - last_cap_dt > autoCancelAfterSec * 1000)
		{
			RT_delCap();
		}
	}

	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg) throws Exception
	{
		VideoCapture capture = RT_getCap();
		if (capture == null)
		{
			return RTOut.createOutIdx().asIdxMsgStr(1, "no camera set");
		}

		try
		{
			boolean b = false;
			synchronized (this)
			{
				b = capture.read(frame);
			}

			if (b)
			{
				Mat f = frame ;
				if(bTransGray)
				{
					Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
					f = grayFrame ;
				}
				
				// encode to JPG
				Imgcodecs.imencode(".jpg", f, buffer);
				byte[] bs = buffer.toArray();
				return RTOut.createOutIdx().asIdxMsgBytes(0, bs);
			}

			return null;
		}
		finally
		{
			last_cap_dt = System.currentTimeMillis();
		}
	}

	@Override
	public String RT_getOutTitle(int idx)
	{
		switch (idx)
		{
		case 0:
			return "Frame Out";
		case 1:
			return "error";
		}
		return null;
	}

	@Override
	public String RT_getOutColor(int idx)
	{
		switch (idx)
		{
		case 1:
			return "red";
		}
		return null;
	}

	// ---

	public static List<CamItem> listCameras()
	{
		String os = System.getProperty("os.name").toLowerCase();

		if (os.contains("win"))
		{
			return listWindowsCameras();
		}
		else if (os.contains("mac"))
		{
			return listMacCameras();
		}
		else
		{
			return listLinuxCameras();
		}
	}

	// ========== Windows ==========
	static List<CamItem> ALL_CACHED_CAMS = null;

	private static CamItem getCachedCam(int idx)
	{
		if (ALL_CACHED_CAMS == null)
			return null;
		for (CamItem ci : ALL_CACHED_CAMS)
			if (ci.index == idx)
				return ci;
		return null;
	}

	private static List<CamItem> listWindowsCameras()
	{
		List<CamItem> cameras = new ArrayList<>();

		try
		{
			// PowerShell 获取摄像头
			String psCommand = "Get-PnpDevice -Class Camera | " + "Where-Object {$_.Status -eq 'OK'} | "
					+ "Select-Object Name, InstanceId | Format-List";

			ProcessBuilder pb = new ProcessBuilder("powershell.exe", "-Command", psCommand);
			pb.redirectErrorStream(true);

			Process process = pb.start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

			String line;
			String currentName = null;
			Pattern namePattern = Pattern.compile("Name\\s*:\\s*(.+)");

			while ((line = reader.readLine()) != null)
			{
				Matcher m = namePattern.matcher(line);
				if (m.find())
				{
					currentName = m.group(1).trim();
					cameras.add(new CamItem(cameras.size(), currentName, "Windows"));
					// System.out.printf("✓ [%d] %s%n",
					// cameras.size()-1, currentName);
				}
			}

		}
		catch ( Exception e)
		{
			// 回退：尝试索引
			fallbackIndexScan(cameras);
		}
		finally
		{
			ALL_CACHED_CAMS = cameras;
		}

		return cameras;
	}

	// ========== Linux ==========
	private static List<CamItem> listLinuxCameras()
	{
		List<CamItem> cameras = new ArrayList<>();

		try
		{
			// 使用 v4l2-ctl
			ProcessBuilder pb = new ProcessBuilder("v4l2-ctl", "--list-devices");
			pb.redirectErrorStream(true);

			Process process = pb.start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

			String line;
			String currentName = null;
			Pattern videoPattern = Pattern.compile("/dev/video(\\d+)");

			while ((line = reader.readLine()) != null)
			{
				if (!line.startsWith("\t") && line.trim().endsWith(":"))
				{
					currentName = line.trim().replace(":", "");
				}
				else
				{
					Matcher m = videoPattern.matcher(line);
					if (m.find() && currentName != null)
					{
						int index = Integer.parseInt(m.group(1));
						cameras.add(new CamItem(index, currentName, "/dev/video" + index));
						// System.out.printf("✓ [%d] %s (%s)%n",
						// index, currentName, "/dev/video" + index);
					}
				}
			}

		}
		catch ( Exception e)
		{
			// v4l2-ctl 不存在，回退
			fallbackIndexScan(cameras);
		}

		return cameras;
	}

	// ========== Mac ==========
	private static List<CamItem> listMacCameras()
	{
		List<CamItem> cameras = new ArrayList<>();

		try
		{
			// 使用 system_profiler
			ProcessBuilder pb = new ProcessBuilder("system_profiler", "SPCameraDataType");
			pb.redirectErrorStream(true);

			Process process = pb.start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

			String line;
			Pattern cameraPattern = Pattern.compile("Model ID:\\s*(.+)");

			while ((line = reader.readLine()) != null)
			{
				Matcher m = cameraPattern.matcher(line);
				if (m.find())
				{
					String name = m.group(1).trim();
					cameras.add(new CamItem(cameras.size(), name, "Mac"));
					// System.out.printf("✓ [%d] %s%n",
					// cameras.size()-1, name);
				}
			}

		}
		catch ( Exception e)
		{
			fallbackIndexScan(cameras);
		}

		return cameras;
	}

	//
	private static void fallbackIndexScan(List<CamItem> cameras)
	{
		// System.out.println("Using fallback index scan...");
		for (int i = 0; i < 4; i++)
		{
			cameras.add(new CamItem(i, "Camera " + i, "index"));
		}
	}

	public static List<CamPM> getCameraPM(int cam_idx)
	{
		String os = System.getProperty("os.name").toLowerCase();
		if (os.contains("win"))
		{
			return getWindows(cam_idx);
		}
		else if (os.contains("mac"))
		{
			return getMac(cam_idx);
		}
		else
		{
			return getLinux(cam_idx);
		}
	}

	private static List<CamPM> getWindows(int cam_idx)
	{
		List<CamPM> list = new ArrayList<>();

		try
		{
			ProcessBuilder pb = new ProcessBuilder("ffmpeg", "-f", "dshow", "-list_options", "true", "-i",
					"video=\"" + cam_idx+"\"");
			pb.redirectErrorStream(true);

			Process p = pb.start();
			BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));

			String line;
			Pattern ptn = Pattern.compile("min s=(\\d+)x(\\d+).*fps=(\\d+)");

			while ((line = r.readLine()) != null)
			{
				Matcher m = ptn.matcher(line);
				while (m.find())
				{
					CamPM c = new CamPM();
					c.width = Integer.parseInt(m.group(1));
					c.height = Integer.parseInt(m.group(2));
					c.fps = Integer.parseInt(m.group(3));
					list.add(c);
				}
			}
		}
		catch ( Exception e)
		{
			e.printStackTrace();
		}

		return list;
	}

	// Linux: using v4l2-ctl
	private static List<CamPM> getLinux(int cam_idx)
	{
		List<CamPM> list = new ArrayList<>();

		try
		{
			ProcessBuilder pb = new ProcessBuilder("v4l2-ctl", "--device=/dev/video" + cam_idx, "--list-formats-ext");
			pb.redirectErrorStream(true);

			Process p = pb.start();
			BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));

			String line;
			int w = 0, h = 0;
			Pattern sizePtn = Pattern.compile("Size: Discrete (\\d+)x(\\d+)");
			Pattern fpsPtn = Pattern.compile("Interval: Discrete .+\\((\\d+)\\.\\d+ fps\\)");

			while ((line = r.readLine()) != null)
			{
				Matcher sm = sizePtn.matcher(line);
				if (sm.find())
				{
					w = Integer.parseInt(sm.group(1));
					h = Integer.parseInt(sm.group(2));
				}

				Matcher fm = fpsPtn.matcher(line);
				if (fm.find() && w > 0)
				{
					CamPM c = new CamPM();
					c.width = w;
					c.height = h;
					c.fps = Integer.parseInt(fm.group(1));
					list.add(c);
				}
			}
		}
		catch ( Exception e)
		{
			System.out.println("v4l2-ctl error: " + e.getMessage());
		}

		return list;
	}

	// Mac: using ffmpeg
	private static List<CamPM> getMac(int cam_idx)
	{
		List<CamPM> list = new ArrayList<>();

		try
		{
			ProcessBuilder pb = new ProcessBuilder("ffmpeg", "-f", "avfoundation", "-list_options", "true", "-i",
					"" + cam_idx);
			pb.redirectErrorStream(true);

			Process p = pb.start();
			BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));

			String line;
			Pattern ptn = Pattern.compile("(\\d+)x(\\d+).*?(\\d+)\\.\\d+ fps");

			while ((line = r.readLine()) != null)
			{
				Matcher m = ptn.matcher(line);
				while (m.find())
				{
					CamPM c = new CamPM();
					c.width = Integer.parseInt(m.group(1));
					c.height = Integer.parseInt(m.group(2));
					c.fps = Integer.parseInt(m.group(3));
					list.add(c);
				}
			}
		}
		catch ( Exception e)
		{
			System.out.println("ffmpeg not found");
		}

		return list;
	}
}

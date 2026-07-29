package org.iottree.driver.bacnet;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.iottree.core.UAManager;
import org.iottree.core.UAPrj;
import org.iottree.core.UATag;
import org.iottree.core.UAVal;
import org.iottree.core.UAVal.ValTP;
import org.iottree.core.service.AbstractService;
import org.iottree.core.util.Convert;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;

import com.serotonin.bacnet4j.type.primitive.Double;
import com.serotonin.bacnet4j.type.primitive.Boolean;
import com.ibm.icu.util.GregorianCalendar;
import com.serotonin.bacnet4j.LocalDevice;
import com.serotonin.bacnet4j.RemoteDevice;
import com.serotonin.bacnet4j.RemoteObject;
import com.serotonin.bacnet4j.event.DeviceEventAdapter;
import com.serotonin.bacnet4j.exception.BACnetServiceException;
import com.serotonin.bacnet4j.npdu.ip.IpNetwork;
import com.serotonin.bacnet4j.npdu.ip.IpNetworkBuilder;
import com.serotonin.bacnet4j.obj.AnalogInputObject;
import com.serotonin.bacnet4j.obj.AnalogOutputObject;
import com.serotonin.bacnet4j.obj.AnalogValueObject;
import com.serotonin.bacnet4j.obj.BACnetObject;
import com.serotonin.bacnet4j.obj.BinaryInputObject;
import com.serotonin.bacnet4j.obj.BinaryOutputObject;
import com.serotonin.bacnet4j.obj.BinaryValueObject;
import com.serotonin.bacnet4j.service.Service;
import com.serotonin.bacnet4j.transport.DefaultTransport;
import com.serotonin.bacnet4j.type.Encodable;
import com.serotonin.bacnet4j.type.constructed.Address;
import com.serotonin.bacnet4j.type.constructed.Choice;
import com.serotonin.bacnet4j.type.constructed.DateTime;
import com.serotonin.bacnet4j.type.constructed.PropertyValue;
import com.serotonin.bacnet4j.type.constructed.SequenceOf;
import com.serotonin.bacnet4j.type.constructed.StatusFlags;
import com.serotonin.bacnet4j.type.constructed.TimeStamp;
import com.serotonin.bacnet4j.type.enumerated.BinaryPV;
import com.serotonin.bacnet4j.type.enumerated.EngineeringUnits;
import com.serotonin.bacnet4j.type.enumerated.EventState;
import com.serotonin.bacnet4j.type.enumerated.EventType;
import com.serotonin.bacnet4j.type.enumerated.MessagePriority;
import com.serotonin.bacnet4j.type.enumerated.NotifyType;
import com.serotonin.bacnet4j.type.enumerated.Polarity;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.enumerated.Reliability;
import com.serotonin.bacnet4j.type.notificationParameters.NotificationParameters;
import com.serotonin.bacnet4j.type.primitive.CharacterString;
import com.serotonin.bacnet4j.type.primitive.Date;
import com.serotonin.bacnet4j.type.primitive.Null;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;
import com.serotonin.bacnet4j.type.primitive.Primitive;
import com.serotonin.bacnet4j.type.primitive.Real;
import com.serotonin.bacnet4j.type.primitive.SignedInteger;
import com.serotonin.bacnet4j.type.primitive.Time;
import com.serotonin.bacnet4j.type.primitive.Unsigned8;
import com.serotonin.bacnet4j.type.primitive.UnsignedInteger;

public class BACnetService extends AbstractService
{
	private static ILogger log = LoggerManager.getLogger(BACnetService.class);

	// private Property
	// public static final PropertyIdentifier CHG_DT_MS =
	// new PropertyIdentifier(0x4001, "change_ms");
	// public static final PropertyIdentifier PROP_QUALITY_LEVEL =
	// new PropertyIdentifier(0x4002, "QualityLevel");

	public static class AddrItem
	{
		public Inet4Address ipAddr;

		public String subNet;

		public int netPrefixLen;

		public AddrItem(Inet4Address addr, String subnet, int net_pre_len)
		{
			this.ipAddr = addr;
			this.subNet = subnet;
			this.netPrefixLen = net_pre_len;
		}

		public boolean equalsIP(String ip)
		{
			if (Convert.isNullOrEmpty(ip))
				return false;
			return ip.equals(this.ipAddr.getHostAddress());
		}

		private IpNetwork toIpNetwork(int port)
		{
			IpNetwork network = new IpNetworkBuilder().withLocalBindAddress(ipAddr.getHostAddress())
					.withSubnet(this.subNet, netPrefixLen).withPort(port).withReuseAddress(true).build();
			return network;
		}

		public String toString()
		{
			return ipAddr.getHostAddress() + " (" + this.subNet + ")";
		}
	}

	public static class TagItem
	{
		int iid;

		UATag tag;

		BACnetObject bacnetObj;
		
		

		public TagItem(int iid, UATag tag, BACnetObject obj)
		{
			this.iid = iid;
			this.tag = tag;
			this.bacnetObj = obj;
		}

		private static Encodable calEncodVal(ValTP vtp, Object obj_v, boolean b_invalid_default)
		{
			switch (vtp)
			{
			case vt_bool:
				if (obj_v != null)
					return ((java.lang.Boolean) obj_v).booleanValue() ? BinaryPV.active : BinaryPV.inactive;
				if (b_invalid_default)
					return BinaryPV.inactive;
				break;
			case vt_byte:
				if (obj_v != null)
					return new SignedInteger(((Number) obj_v).shortValue());
				if (b_invalid_default)
					return new SignedInteger(0);
				break;
			case vt_char:
			case vt_int16:
				if (obj_v != null)
					return new SignedInteger(((Number) obj_v).shortValue());
				if (b_invalid_default)
					return new SignedInteger(0);
				break;
			case vt_uint8:
				if (obj_v != null)
					return new UnsignedInteger(((Number) obj_v).shortValue());
				if (b_invalid_default)
					return new UnsignedInteger(0);
				break;
			case vt_int32:
				if (obj_v != null)
					return new SignedInteger(((Number) obj_v).intValue());
				if (b_invalid_default)
					return new SignedInteger(0);
				break;
			case vt_uint16:
				if (obj_v != null)
					return new UnsignedInteger(((Number) obj_v).intValue());
				if (b_invalid_default)
					return new UnsignedInteger(0);
				break;
			case vt_int64:
				if (obj_v != null)
					return new SignedInteger(((Number) obj_v).longValue());
				if (b_invalid_default)
					return new SignedInteger(0);
				break;
			case vt_uint32:
			case vt_uint64:
				if (obj_v != null)
					return new UnsignedInteger(((Number) obj_v).longValue());
				if (b_invalid_default)
					return new UnsignedInteger(0);
				break;
			case vt_float:
				if (obj_v != null)
					return new Real(((Number) obj_v).floatValue());
				if (b_invalid_default)
					return new Real(0.0f);
				break;
			case vt_double:
				if (obj_v != null)
					return new Double(((Number) obj_v).doubleValue());
				if (b_invalid_default)
					return new Double(0.0);
				break;
			case vt_str:
				if (obj_v != null)
					return new CharacterString(obj_v.toString());
				if (b_invalid_default)
					return new CharacterString("");
				break;
			case vt_date:
				if (obj_v != null)
				{
					java.util.Date dt = (java.util.Date) obj_v;
					return new DateTime(dt.getTime());
				}
				if (b_invalid_default)
					return new DateTime(0);
				break;
			default:
				return null;
			}
			return null;
		}


		void RT_update(boolean b_invalid_default)
		{
			ValTP vtp = tag.getValTp();
			UAVal val = tag.RT_getVal();

			boolean bvalid = false;
			boolean balarm = tag.hasAlerts();

			long up_dt = -1;
			long chg_dt = -1;
			Object obj_v = val.getObjVal();
			if (val != null)
			{
				bvalid = val.isValid();
				up_dt = val.getValDT();
				chg_dt = val.getValChgDT();
			}

			Encodable enc_val = calEncodVal(vtp, obj_v, b_invalid_default);

			if (enc_val != null)
				bacnetObj.writePropertyInternal(PropertyIdentifier.presentValue, enc_val);
			bacnetObj.writePropertyInternal(PropertyIdentifier.reliability,
					bvalid ? Reliability.noFaultDetected : Reliability.activationFailure);
			bacnetObj.writePropertyInternal(PropertyIdentifier.statusFlags,
					new StatusFlags(balarm, !bvalid, false, false));
			bacnetObj.writePropertyInternal(PropertyIdentifier.changeOfStateTime, new DateTime(chg_dt));
			bacnetObj.writePropertyInternal(PropertyIdentifier.activationTime, new DateTime(up_dt));
		}
		
		public boolean checkWritePropVal(PropertyValue pv)
		{
			if(!tag.isCanWrite())
				return false;
			
			PropertyIdentifier pi = pv.getPropertyIdentifier() ;
			return pi.equals(PropertyIdentifier.presentValue) ;
		}
		
		/**
		 * on BACnet property value write
		 * @param pv
		 */
		public void RT_writePropVal(PropertyValue pv)
		{
			if(!checkWritePropVal(pv))
				return ;
			Object v = pv.getValue() ;
			if(v==null)
				return ;
			String strv = null ;
			if(v instanceof BinaryPV)
			{
				strv= v.equals(BinaryPV.active)+"" ;
			}
			else if(v instanceof SignedInteger)
			{
				strv = ((SignedInteger)v).longValue()+"" ;
			}
			else if(v instanceof UnsignedInteger)
			{
				strv = ((UnsignedInteger)v).longValue()+"" ;
			}
			else if(v instanceof Real)
			{
				strv =  ((Real)v).floatValue()+"" ;
			}
			else if(v instanceof Double)
			{
				strv =  ((Double)v).doubleValue()+"" ;
			}
			else if(v instanceof CharacterString)
			{
				strv =  ((CharacterString)v).getValue();
			}
			else if(v instanceof DateTime)
			{
				DateTime dt = (DateTime)v; 
				Date date = dt.getDate() ;
				Time time = dt.getTime() ;
				
				Calendar gc = java.util.GregorianCalendar.getInstance() ;
				gc.set(Calendar.YEAR,date.getYear() + 1900);
				gc.set(Calendar.MONTH,date.getMonth().getId()-1);
		        gc.set(Calendar.DATE,date.getDay());
		        gc.set(Calendar.HOUR_OF_DAY,time.getHour());
		        gc.set(Calendar.MINUTE,time.getMinute());
		        gc.set(Calendar.SECOND,time.getSecond());
		        gc.set(Calendar.MILLISECOND,0) ;
		        strv = Convert.toFullYMDHMS(gc.getTime()) ;
			}
			if(strv==null)
				return ;
			tag.RT_writeValStr(strv) ;
		}
	}

	public static class PrjTagItems
	{
		UAPrj prj;

		HashMap<Integer, TagItem> iid2tagitem = new HashMap<>();

		public PrjTagItems(UAPrj prj)
		{
			this.prj = prj;
		}
	}

	public static final String NAME = "iottree_bacnet";

	int devId = -1;
	String devName = "IOTTree-BACnet";
	List<String> prjIds = null;

	HashMap<String, Integer> prjid2base_iid = null;

	String locIP = "";
	private int locPort = 47808;

	/**
	 * 
	 */
	private long updateIntvMS = 1000;

	private transient LocalDevice locDev = null;

	private transient HashMap<String, PrjTagItems> prjn2pti = null;

	private transient HashMap<Integer, TagItem> iid2tagitem = null;

	@Override
	public String getName()
	{
		return NAME;
	}

	@Override
	public String getTitle()
	{
		return "BACnet Device";
	}

	@Override
	public String getBrief()
	{
		return "BACnet Device";
	}

	public int getDevId()
	{
		return devId;
	}

	public String getDevName()
	{
		if (Convert.isNullOrEmpty(this.devName))
			return "IOTTree-BACnet";
		return this.devName;
	}

	public String getLocIP()
	{
		return this.locIP;
	}

	public int getLocPort()
	{
		return locPort;
	}

	public List<UAPrj> getPrjs()
	{
		ArrayList<UAPrj> rets = new ArrayList<>();
		if (this.prjIds == null || this.prjIds.size() <= 0)
			return rets;

		for (UAPrj prj : UAManager.getInstance().listPrjs())
		{
			// if(!prj.isOpcUAOpen())
			// continue ;
			if (this.prjIds.contains(prj.getId()))
				rets.add(prj);
		}
		return rets;
	}

	public boolean hasPrjId(String prjid)
	{
		if (this.prjIds == null || this.prjIds.size() <= 0)
			return false;
		return this.prjIds.contains(prjid);
	}

	public HashMap<String, Integer> getPrjId2BaseIID()
	{
		return this.prjid2base_iid;
	}

	@Override
	protected void initService(HashMap<String, String> pms) throws Exception
	{
		super.initService(pms);

		devId = Convert.parseToInt32(pms.get("dev_id"), 12345);
		devName = pms.get("dev_n");
		this.locIP = pms.get("loc_ip");
		this.locPort = Convert.parseToInt32(pms.get("loc_port"), 47808);
		this.prjIds = Convert.splitStrWith(pms.get("prjs"), ",");

		List<String> ss = Convert.splitStrWith(pms.get("prjid2base_iid"), ",|");
		this.prjid2base_iid = new HashMap<>();
		if (ss != null)
		{
			for (String s : ss)
			{
				int k = s.indexOf('=');
				if (k <= 0)
					continue;
				String prjid = s.substring(0, k).trim();
				String iid_str = s.substring(k + 1).trim();
				int base_iid = Convert.parseToInt32(iid_str, -1);
				if (base_iid < 0)
					continue;
				this.prjid2base_iid.put(prjid, base_iid);
			}
		}
		// this.secModeNone = "true".equals(pms.get("sm_none"));
		// this.secModeSign = "true".equals(pms.get("sm_sign"));
		// this.secModeSignEncrypt = !"false".equals(pms.get("sm_sign_enc"));
	}

	private TagItem getTagItemByIID(int iid)
	{
		if (iid2tagitem == null)
			return null;
		return iid2tagitem.get(iid);
	}

	private LocalDevice createDev(StringBuilder failedr) throws Exception
	{
		if (Convert.isNullOrEmpty(locIP))
		{
			failedr.append("no local address set");
			return null;
		}

		AddrItem addri = findAddrItemByHost(locIP);
		if (addri == null)
		{
			failedr.append("no address item found with ip=" + this.locIP);
			return null;
		}
		IpNetwork network = addri.toIpNetwork(this.locPort);

		DefaultTransport transport = new DefaultTransport(network);

		LocalDevice localDevice = new LocalDevice(this.devId, transport);
		localDevice.getEventHandler().addListener(deviceEvtAdp);
		localDevice.getDeviceObject().writePropertyInternal(PropertyIdentifier.objectName,
				new CharacterString(this.getDevName()));
		// localDevice.getDeviceObject().writePropertyInternal(PropertyIdentifier.description,
		// new CharacterString(this.getDevName()));
		localDevice.getDeviceObject().writePropertyInternal(PropertyIdentifier.modelName,
				new CharacterString("iottree"));
		localDevice.getDeviceObject().writePropertyInternal(PropertyIdentifier.vendorIdentifier,
				new UnsignedInteger(0));
		localDevice.getDeviceObject().writePropertyInternal(PropertyIdentifier.vendorName,
				new CharacterString("IOT-Tree Owner"));

		this.iid2tagitem = new HashMap<>();
		this.prjn2pti = initLocalDevice(localDevice, this.iid2tagitem);
		// ai0.writeProperty(PropertyIdentifier.objectName, new
		// CharacterString("AI0-Temperature"));
		// ai0.writeProperty(PropertyIdentifier.units,
		// EngineeringUnits.degreesCelsius);
		// ai0.writeProperty(PropertyIdentifier.presentValue, new Real(21.0f));
		// localDevice.addObject(ai0);
		return localDevice;
	}

	private HashMap<String, PrjTagItems> initLocalDevice(LocalDevice ld, HashMap<Integer, TagItem> iid2tagi)
			throws BACnetServiceException
	{
		HashMap<String, PrjTagItems> ret = new HashMap<>();
		List<UAPrj> prjs = this.getPrjs();
		if (prjs == null || prjs.size() <= 0)
			return ret;
		for (UAPrj prj : prjs)
		{
			PrjTagItems ptis = initPrj(ld, prj);
			if (ptis == null)
				continue;
			ret.put(prj.getId(), ptis);

			for (TagItem ti : ptis.iid2tagitem.values())
				iid2tagi.put(ti.iid, ti);
		}
		return ret;
	}

	private PrjTagItems initPrj(LocalDevice ld, UAPrj prj) throws BACnetServiceException
	{
		if (prjid2base_iid == null)
			return null;
		Integer base_iid = prjid2base_iid.get(prj.getId());
		if (base_iid == null || base_iid < 0)
			return null;
		base_iid *= 1000000;
		PrjTagItems rets = new PrjTagItems(prj);
		List<UATag> tags = prj.listTagsAll();
		for (UATag tag : tags)
		{
			TagItem ti = createTagItem(base_iid, ld, tag);
			if (ti == null)
				continue;
			rets.iid2tagitem.put(ti.tag.getIID(), ti);
		}

		return rets;
	}

	private TagItem createTagItem(int base_iid, LocalDevice ld, UATag tag) throws BACnetServiceException
	{
		int iid = tag.getIID();
		if (iid < 0)
			return null;
		iid += base_iid;
		boolean b_write = tag.isCanWrite();
		String path = tag.getNodePathCxt();
		ValTP vtp = tag.getValTp();

		BACnetObject bacobj = null;

		AnalogInputObject ai = null;
		AnalogValueObject av = null;
		AnalogOutputObject ao = null;

		if (vtp == ValTP.vt_bool)
		{
			if (b_write)
				bacobj = new BinaryOutputObject(ld, iid, path, BinaryPV.inactive, false,Polarity.normal,BinaryPV.inactive);
			else
				bacobj = new BinaryInputObject(ld, iid, path, BinaryPV.inactive, false, Polarity.normal);
		}
		else if (vtp == ValTP.vt_none)
			bacobj = null;
		else
		{
			if (b_write)
				bacobj = new AnalogOutputObject(ld, iid, path, 0.0f, EngineeringUnits.noUnits, false,0);
			else
				bacobj = new AnalogInputObject(ld, iid, path, 0.0f, EngineeringUnits.noUnits, false);
		}

		if (bacobj == null)
			return null;

		TagItem ret = new TagItem(iid, tag, bacobj);

		ret.RT_update(true);
		// if(tag.isCanWrite())

		return ret;
	}

	DeviceEventAdapter deviceEvtAdp = new DeviceEventAdapter() {
		public void iAmReceived(RemoteDevice d)
		{
			// log_inf("I-Am from {}" + d);
		}

		@Override
		public void listenerException(Throwable e)
		{

		}

		@Override
		public boolean allowPropertyWrite(Address from, BACnetObject obj, PropertyValue pv)
		{
			int iid = obj.getInstanceId();
			TagItem ti = getTagItemByIID(iid);
			if (ti == null)
				return false;
			boolean b= ti.checkWritePropVal(pv) ;
			return b;
		}

		@Override
		public void propertyWritten(Address from, BACnetObject obj, PropertyValue pv)
		{
			int iid = obj.getInstanceId();
			TagItem ti = getTagItemByIID(iid);
			if (ti == null)
				return ;
			ti.RT_writePropVal(pv) ;
		}

		@Override
		public void iHaveReceived(RemoteDevice d, RemoteObject o)
		{
			// log_inf("iHaveReceived" + d + " --- " + o);
		}

		@Override
		public void covNotificationReceived(UnsignedInteger subscriberProcessIdentifier,
				ObjectIdentifier initiatingDeviceIdentifier, ObjectIdentifier monitoredObjectIdentifier,
				UnsignedInteger timeRemaining, SequenceOf<PropertyValue> listOfValues)
		{

		}

		@Override
		public void textMessageReceived(ObjectIdentifier textMessageSourceDevice, Choice messageClass,
				MessagePriority messagePriority, CharacterString message)
		{

		}

		@Override
		public void synchronizeTime(Address from, DateTime dateTime, boolean utc)
		{

		}

		@Override
		public void requestReceived(Address from, Service service)
		{

		}
	};

	private ScheduledExecutorService schedExec = null;

	private void RT_doUpdate()
	{
		if (prjn2pti == null || prjn2pti.size() <= 0)
			return;

		for (PrjTagItems pti : prjn2pti.values())
		{
			for (TagItem ti : pti.iid2tagitem.values())
			{
				ti.RT_update(false);
			}
		}
		// try
		// {
		// float v = ((Real)
		// ai0.get(PropertyIdentifier.presentValue)).floatValue() + 0.1f;
		// ai0.writePropertyInternal(PropertyIdentifier.presentValue, new
		// Real(v));
		// // log_inf("AI0 updated to {:.1f} 鈩�"+ v);
		// } catch (Exception e) {
		// log.debug(e);
		// }
	}

	// private Runnable updateRunner = new Runnable() {
	//
	// @Override
	// public void run()
	// {
	// RT_doUpdate();
	// }} ;

	@Override
	synchronized public boolean startService(StringBuilder failedr)
	{
		if (locDev != null)
			return true;

		LocalDevice ld = null;
		try
		{
			ld = this.createDev(failedr);
			if (ld == null)
				return false;
			ld.initialize();
			locDev = ld;

			if (schedExec == null)
				schedExec = Executors.newSingleThreadScheduledExecutor();

			schedExec.scheduleWithFixedDelay(() -> {
				RT_doUpdate();
			}, updateIntvMS, updateIntvMS, TimeUnit.MILLISECONDS);
			return true;
		}
		catch ( Exception ee)
		{
			log.error("start BACnet Service err", ee);
			failedr.append(failedr.toString());
			return false;
		}
	}

	@Override
	synchronized public boolean stopService()
	{
		if (locDev == null)
			return true;
		try
		{
			// locDev.
			locDev.terminate();
			if (schedExec != null)
				schedExec.shutdown();
			return true;
		}
		finally
		{
			locDev = null;
			schedExec = null;
		}
	}

	@Override
	public boolean isRunning()
	{
		return locDev != null;
	}

	public static List<AddrItem> listAddrItemAll() throws SocketException, UnknownHostException
	{
		ArrayList<AddrItem> rets = new ArrayList<>();
		Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
		while (nis.hasMoreElements())
		{
			NetworkInterface nic = nis.nextElement();
			if (!nic.isUp() || nic.isLoopback() || nic.isVirtual())
				continue;
			for (InterfaceAddress ia : nic.getInterfaceAddresses())
			{
				InetAddress addr = ia.getAddress();
				if (!(addr instanceof Inet4Address))
					continue;

				Inet4Address tmpaddr = (Inet4Address) addr;

				int prefix = ia.getNetworkPrefixLength(); // 24、16、8 ...
				// 转成 255.255.255.0 形式
				int mask = 0xffffffff << (32 - prefix);
				String subnet_ip = String.format("%d.%d.%d.%d", (mask >> 24) & 0xff, (mask >> 16) & 0xff,
						(mask >> 8) & 0xff, mask & 0xff);
				rets.add(new AddrItem(tmpaddr, subnet_ip, prefix));
			}
		}
		return rets;
	}

	public static AddrItem findAddrItemByHost(String host) throws SocketException, UnknownHostException
	{
		InetAddress in_addr = Inet4Address.getByName(host);
		if (in_addr == null || !(in_addr instanceof Inet4Address))
			return null;

		Inet4Address addr4 = null;
		Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
		while (nis.hasMoreElements())
		{
			NetworkInterface nic = nis.nextElement();
			if (!nic.isUp() || nic.isLoopback() || nic.isVirtual())
				continue;
			for (InterfaceAddress ia : nic.getInterfaceAddresses())
			{
				InetAddress addr = ia.getAddress();
				if (!(addr instanceof Inet4Address))
					continue;

				Inet4Address tmpaddr = (Inet4Address) addr;
				if (tmpaddr.equals(in_addr))
				{
					int prefix = ia.getNetworkPrefixLength(); // 24、16、8 ...
					// 转成 255.255.255.0 形式
					int mask = 0xffffffff << (32 - prefix);
					String subnet_ip = String.format("%d.%d.%d.%d", (mask >> 24) & 0xff, (mask >> 16) & 0xff,
							(mask >> 8) & 0xff, mask & 0xff);
					// addr4 = tmpaddr ;
					return new AddrItem(tmpaddr, subnet_ip, prefix);
				}
			}
		}
		return null;
	}

}

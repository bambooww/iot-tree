package org.iottree.core.basic;

import java.util.LinkedList;
import java.util.List;

import org.iottree.core.cxt.JSObMap;
import org.iottree.core.cxt.JsProp;
import org.iottree.core.util.Convert;
import org.iottree.core.util.ILang;
import org.iottree.core.util.Lan;
import org.json.JSONObject;

public abstract class ValEventTp extends JSObMap implements ILang
{
	static Lan lan = Lan.getLangInPk(ValEventTp.class) ;
	
	public static enum GradientTP
	{
		up(0),
		down(1),
		updown(2);
		
		private final int val ;
		
		GradientTP(int v)
		{
			val = v ;
		}
		
		public int getInt()
		{
			return val ;
		}
		
		public String getTitle()
		{
			return lan.g("trigger_tp_"+this.name()) ;
		}
		
		public static GradientTP fromInt(int val)
		{
			switch(val)
			{
			
			case 1:
				return down;
			case 2:
				return updown;
			case 0:
			default:
				return up;
			}
		}
	}
	
	public static final ValEventTp[] ALL = new ValEventTp[] { new VAT_OnOff(), new VAT_NegT(), new VAT_PosT(),
			new VAT_BitEqu(), new VAT_BitOffToOn(), new VAT_BitOnToOff(), new VAT_ValEqu(), new VAT_ValNotEqu(),
			new VAT_ValGt(), new VAT_ValGtEqu(), new VAT_ValLt(), new VAT_ValLtEqu() ,new VAT_Gradient()};

	public static ValEventTp getTp(int v)
	{
		if (v < 1 || v > 13)
			return null;
		return ALL[v - 1];
	}

	public static ValEventTp createTp(ValEvent va, int tp_v)
	{
		ValEventTp tp = getTp(tp_v);
		if (tp == null)
			return null;
		tp = tp.copyMe(va);
		return tp;
	}

	// private int val;
	// private String name ;
//	private String titleEn;
//	private String titleCn;
//
//	private String descEn = "";
//	private String descCn = "";
//
//	protected String triggerEn = "";
//	protected String triggerCn = "";
//
//	protected String releaseEn = "";
//	protected String releaseCn = "";

//	protected String param1TitleCn = "";
//	protected String param1TitleEn = "";
//
//	protected String param2TitleCn = "";
//	protected String param2TitleEn = "";
//
//	protected String param3TitleCn = "";
//	protected String param3TitleEn = "";

	ValEvent valEvent = null;

	boolean bValid = false;

	String invalidReson = null;

	public ValEventTp()
	{
		// asVA(va);
	}

	public ValEventTp asVA(ValEvent va)
	{
		this.valEvent = va;
		if (va != null)
		{
			StringBuilder failedr = new StringBuilder();
			bValid = initVA(va, failedr);
			invalidReson = failedr.toString();
		}
		return this;
	}

	public abstract int getTpVal();

	public abstract String getName();

	protected abstract ValEventTp newIns();
	
	public boolean isSelfJOConfig()
	{//check jo config self ui
		return false;
	}
	
	public boolean isNeedLastVal()
	{
		return false;
	}
	
	public boolean isFloatVal()
	{
		return false;
	}

	public final ValEventTp copyMe(ValEvent va)
	{
		ValEventTp newins = newIns();
//		newins.titleEn = this.titleEn;
//		newins.titleCn = this.titleCn;
//		newins.descEn = this.descEn;
//		newins.descCn = this.descCn;
//		newins.triggerEn = this.triggerEn;
//		newins.triggerCn = this.triggerCn;
//		newins.releaseEn = this.releaseEn;
//		newins.releaseCn = this.releaseCn;
//		newins.param1TitleCn = this.param1TitleCn;
//		newins.param1TitleEn = this.param1TitleEn;
//		newins.param2TitleCn = this.param2TitleCn;
//		newins.param2TitleEn = this.param2TitleEn;
//		newins.param3TitleCn = this.param3TitleCn;
//		newins.param3TitleEn = this.param3TitleEn;
		newins.asVA(va);
		return newins;
	}

//	public ValAlertTp asTitle(String en, String cn)
//	{
//		titleEn = en;
//		titleCn = cn;
//		return this;
//	}
//
//	public ValAlertTp asDesc(String en, String cn)
//	{
//		descEn = en;
//		descCn = cn;
//		return this;
//	}
//
//	public ValAlertTp asTrigger(String en, String cn)
//	{
//		triggerEn = en;
//		triggerCn = cn;
//		return this;
//	}
//
//	public ValAlertTp asRelease(String en, String cn)
//	{
//		releaseEn = en;
//		releaseCn = cn;
//		return this;
//	}
//
//	public ValAlertTp asParam1Title(String en, String cn)
//	{
//		param1TitleEn = en;
//		param1TitleCn = cn;
//		return this;
//	}
//
//	public ValAlertTp asParam2Title(String en, String cn)
//	{
//		param2TitleEn = en;
//		param2TitleCn = cn;
//		return this;
//	}
//
//	public ValAlertTp asParam3Title(String en, String cn)
//	{
//		param3TitleEn = en;
//		param3TitleCn = cn;
//		return this;
//	}

	public String getTitle()
	{
		return g("vatp_"+this.getName()) ;//this.titleEn;
	}

//	public String getTitleCn()
//	{
//		return this.titleCn;
//	}

	 public String getTriggerCond()
	 {
		 return g("vatp_"+this.getName(),"trigger") ;
	 }
	//
	//
	//
	// public void setTriggerEn(String triggerEn)
	// {
	// this.triggerEn = triggerEn;
	// }
	//
	// public String getTriggerCn()
	// {
	// return triggerCn;
	// }
	//
	// public void setTriggerCn(String triggerCn)
	// {
	// this.triggerCn = triggerCn;
	// }

//	public String getTriggerCond(String lang)
//	{
//		switch (lang)
//		{
//		case "cn":
//			if (Convert.isNullOrEmpty(this.triggerCn))
//				return null;
////"触发条件:" + 
//			return this.triggerCn;
//		default:
//			if (Convert.isNullOrEmpty(this.triggerEn))
//				return null;
//			//"Trigger Condition:" +
//			return  this.triggerEn;
//		}
//	}

	 public String getReleaseCond()
	 {
		 return g("vatp_"+this.getName(),"release") ;
	 }
	//
	// public void setReleaseEn(String releaseEn)
	// {
	// this.releaseEn = releaseEn;
	// }
	//
	// public String getReleaseCn()
	// {
	// return releaseCn;
	// }
	//
	// public void setReleaseCn(String releaseCn)
	// {
	// this.releaseCn = releaseCn;
	// }

//	public String getReleaseCond(String lang)
//	{
//		switch (lang)
//		{
//		case "cn":
//			if (Convert.isNullOrEmpty(this.releaseCn))
//				return null;
//			//"解除条件:" +
//			return  this.releaseCn;
//		default:
//			if (Convert.isNullOrEmpty(this.releaseEn))
//				return null;
//			//"Release Condition:" +
//			return  this.releaseEn;
//		}
//	}

	public String getDesc()
	{
		return g("vatp_"+this.getName(),"desc") ;
	}

//	public void setDescEn(String descEn)
//	{
//		this.descEn = descEn;
//	}
//
//	public String getDescCn()
//	{
//		return descCn;
//	}
//
//	public void setDescCn(String descCn)
//	{
//		this.descCn = descCn;
//	}

	public String getParam1Title()
	{
		return g("vatp_"+this.getName(),"param1tt","") ;
//		switch (lang)
//		{
//		case "cn":
//			return this.param1TitleCn;
//		default:
//			return this.param1TitleEn;
//		}
	}

	public String getParam2Title()
	{
		return g("vatp_"+this.getName(),"param2tt","") ;
//		switch (lang)
//		{
//		case "cn":
//			return this.param2TitleCn;
//		default:
//			return this.param2TitleEn;
//		}
	}

	public String getParam3Title()
	{
		return g("vatp_"+this.getName(),"param3tt","") ;
//		switch (lang)
//		{
//		case "cn":
//			return this.param3TitleCn;
//		default:
//			return this.param3TitleEn;
//		}
	}

	public final boolean isValid()
	{
		return this.bValid;
	}

	public final String getInvalidReson()
	{
		return this.invalidReson;
	}

	abstract boolean initVA(ValEvent va, StringBuilder failedr);

	public abstract boolean checkTrigger(Number lastv, Number val);

	public abstract boolean checkRelease(Number lastv, Number val);
	
	
	public boolean checkByUpdate()
	{
		return false;
	}
	
	public boolean checkReleaseByUpdate(Number val)
	{
		return false;
	}
	
	public boolean checkTriggerByUpdate(Number val)
	{
		return false;
	}
	
	public abstract String calValEventTitle(ValEvent va) ;
	
	@Override
	public Object JS_get(String  key)
	{
		Object ob = super.JS_get(key) ;
		if(ob!=null)
			return ob ;
		
		switch(key)
		{
		case "name":
			return this.getName() ;
		case "title":
			return this.getTitle() ;
		}
		return null;
	}
	
	@Override
	public List<JsProp> JS_props()
	{
		List<JsProp> ss = super.JS_props() ;
		ss.add(new JsProp("name",null,String.class,false,"Event Type Name","unique name as Event Type")) ;
		ss.add(new JsProp("title",null,String.class,false,"Event Type Title","")) ;
		return ss ;
	}
	
}

class VAT_OnOff extends ValEventTp
{
	int p1 = 0;

	public VAT_OnOff()// (ValAlert va)
	{
		// super(va);

//		this.asTitle("On Off", "开关量").asTrigger("The current value==Alarm value", "当前值==报警值(0/1)")
//				.asRelease("The current value<>Alarm value", "当前值<>报警值(0/1)")
//				.asParam1Title("Alarm Value(0/1)", "报警值(0/1)");
	}

	protected ValEventTp newIns()
	{
		return new VAT_OnOff();
	}

	public int getTpVal()
	{
		return 1;
	}

	public String getName()
	{
		return "on_off";
	}
	
	@Override
	public String calValEventTitle(ValEvent va)
	{
		return "On=="+va.getParamStr1() ;
	}

	@Override
	boolean initVA(ValEvent va, StringBuilder failedr)
	{
		String pstr1 = va.getParamStr1();

		if ("1".equals(pstr1))
			p1 = 1;
		else if ("0".equals(pstr1))
			p1 = 0;
		else
		{
			failedr.append(g("invalid_pm")+"1");
			return false;
		}
		return true;
	}

	@Override
	public boolean checkTrigger(Number lastv, Number val)
	{
		return val.intValue() == p1;
	}

	@Override
	public boolean checkRelease(Number lastv, Number val)
	{
		return val.intValue() != p1;
	}
}

class VAT_NegT extends ValEventTp
{
	public VAT_NegT()// ValAlert va)
	{
//		this.asTitle("Negative Transition", "负跳变")
//				.asTrigger("The current value has changed from non-0 to 0", "当前值由非0变化为0")
//				.asRelease("The current value has changed from 0 to non-0", "当前值由0变化为非0");
	}

	protected ValEventTp newIns()
	{
		return new VAT_NegT();
	}

	public int getTpVal()
	{
		return 2;
	}

	public String getName()
	{
		return "neg_t";
	}
	
	public boolean isNeedLastVal()
	{
		return true;
	}
	
	@Override
	public String calValEventTitle(ValEvent va)
	{
		return "non-0 to 0";
	}

	@Override
	boolean initVA(ValEvent va, StringBuilder failedr)
	{
		return true;
	}

	@Override
	public boolean checkTrigger(Number lastv, Number val)
	{
		return lastv.intValue() != 0 && val.intValue() == 0;
	}

	@Override
	public boolean checkRelease(Number lastv, Number val)
	{
		return lastv.intValue() == 0 && val.intValue() != 0;
	}
}

class VAT_PosT extends ValEventTp
{
	public VAT_PosT() // (ValAlert va)
	{
		// super(va);
//		this.asTitle("Positive transition", "正跳变")
//				.asTrigger("The current value has changed from 0 to non-0", "当前值由0变化为非0")
//				.asRelease("The current value has changed from non-0 to 0", "当前值由非0变化为0");

	}

	protected ValEventTp newIns()
	{
		return new VAT_PosT();
	}

	public int getTpVal()
	{
		return 3;
	}

	public String getName()
	{
		return "pos_t";
	}
	
	public boolean isNeedLastVal()
	{
		return true;
	}
	
	@Override
	public String calValEventTitle(ValEvent va)
	{
		return "0 to non-0";
	}

	@Override
	boolean initVA(ValEvent va, StringBuilder failedr)
	{
		return true;
	}

	@Override
	public boolean checkTrigger(Number lastv, Number val)
	{
		return lastv.intValue() == 0 && val.intValue() != 0;
	}

	@Override
	public boolean checkRelease(Number lastv, Number val)
	{
		return lastv.intValue() != 0 && val.intValue() == 0;
	}
}

class VAT_BitEqu extends ValEventTp
{
	int bitPos = 0;
	int refV = 0;

	public VAT_BitEqu() // (ValAlert va)
	{
		// super(va);

//		this.asTitle("Bit==", "Bit==")
//				.asTrigger("Current value of specified bit==specified value(0/1)", "当前数值指定位==指定值(0/1)")
//				.asRelease("Current value of specified bit<>specified value(0/1)", "当前数值指定位<>指定值(0/1)")
//				.asParam1Title("Bit Position", "指定位").asParam2Title("Alarm Value(0/1)", "指定值(0/1)");
	}

	protected ValEventTp newIns()
	{
		return new VAT_BitEqu();
	}

	public int getTpVal()
	{
		return 4;
	}

	public String getName()
	{
		return "bit_equ";
	}
	
	@Override
	public String calValEventTitle(ValEvent va)
	{
		return "Bit("+va.getParamStr1()+")=="+va.getParamStr2();
	}

	@Override
	boolean initVA(ValEvent va, StringBuilder failedr)
	{
		String p1 = va.getParamStr1();
		String p2 = va.getParamStr2();
		bitPos = Convert.parseToInt32(p1, -1);
		if (bitPos < 0 || bitPos >= 64)
		{
			failedr.append("Bit Position must 0-63");
			return false;
		}

		if ("1".equals(p2))
			refV = 1;
		else if ("0".equals(p2))
			refV = 0;
		else
		{
			failedr.append("Alarm Value must be (0/1)");
			return false;
		}
		return true;
	}

	@Override
	public boolean checkTrigger(Number lastv, Number val)
	{
		long v = val.longValue();
		int resv = (int) (v >> bitPos & 1);
		return resv == refV;
	}

	@Override
	public boolean checkRelease(Number lastv, Number val)
	{
		long v = val.longValue();
		int resv = (int) (v >> bitPos & 1);
		return resv != refV;
	}
}

class VAT_BitOffToOn extends ValEventTp
{
	int bitPos = 0;

	public VAT_BitOffToOn()// (ValAlert va)
	{
		// super(va);

//		this.asTitle("Bit Off->On", "位0->1")
//				.asTrigger("The current value of the specified bit changes from 0 to 1", "当前数值指定位由0变1")
//				.asRelease("The current value of the specified bit changes from 1 to 0", "当前数值指定位由1变0")
//				.asParam1Title("Bit Position", "指定位");
	}

	protected ValEventTp newIns()
	{
		return new VAT_BitOffToOn();
	}

	public int getTpVal()
	{
		return 5;
	}

	public String getName()
	{
		return "bit_off_to_on";
	}
	
	@Override
	public String calValEventTitle(ValEvent va)
	{
		return "Bit("+va.getParamStr1()+") 0 to 1";
	}

	@Override
	boolean initVA(ValEvent va, StringBuilder failedr)
	{
		String p1 = va.getParamStr1();
		bitPos = Convert.parseToInt32(p1, -1);
		if (bitPos < 0 || bitPos >= 64)
		{
			failedr.append("Bit Position must 0-63");
			return false;
		}
		return true;
	}
	
	public boolean isNeedLastVal()
	{
		return true;
	}

	@Override
	public boolean checkTrigger(Number lastv, Number val)
	{
		int lv = (int) (lastv.longValue() >> bitPos & 1);
		int v = (int) (val.longValue() >> bitPos & 1);
		return lv == 0 && v == 1;
	}

	@Override
	public boolean checkRelease(Number lastv, Number val)
	{
		int lv = (int) (lastv.longValue() >> bitPos & 1);
		int v = (int) (val.longValue() >> bitPos & 1);
		return lv == 1 && v == 0;
	}
}

class VAT_BitOnToOff extends ValEventTp
{
	int bitPos = 0;

	public VAT_BitOnToOff()// (ValAlert va)
	{
		// super(va);

//		this.asTitle("Bit On->Off", "位1->0")
//				.asTrigger("The current value of the specified bit changes from 1 to 0", "当前数值指定位由1变0")
//				.asRelease("The current value of the specified bit changes from 0 to 1", "当前数值指定位由0变1")
//				.asParam1Title("Bit Position", "指定位");
	}

	protected ValEventTp newIns()
	{
		return new VAT_BitOnToOff();
	}

	public int getTpVal()
	{
		return 6;
	}

	public String getName()
	{
		return "bit_on_to_off";
	}

	@Override
	public String calValEventTitle(ValEvent va)
	{
		return "Bit("+va.getParamStr1()+") 1 to 0";
	}
	
	@Override
	boolean initVA(ValEvent va, StringBuilder failedr)
	{
		String p1 = va.getParamStr1();
		bitPos = Convert.parseToInt32(p1, -1);
		if (bitPos < 0 || bitPos >= 64)
		{
			failedr.append("Bit Position must 0-63");
			return false;
		}
		return true;
	}
	
	public boolean isNeedLastVal()
	{
		return true;
	}

	@Override
	public boolean checkTrigger(Number lastv, Number val)
	{
		int lv = (int) (lastv.longValue() >> bitPos & 1);
		int v = (int) (val.longValue() >> bitPos & 1);
		return lv == 1 && v == 0;
	}

	@Override
	public boolean checkRelease(Number lastv, Number val)
	{
		int lv = (int) (lastv.longValue() >> bitPos & 1);
		int v = (int) (val.longValue() >> bitPos & 1);
		return lv == 0 && v == 1;
	}
}

class VAT_ValEqu extends ValEventTp
{
	double refV;

	double triggerErr;
	double releaseErr;

	public VAT_ValEqu()// (ValAlert va)
	{

//		this.asTitle("Value ==", "值==").asTrigger(
//				"Current value>=reference value - trigger error, and current value<=reference value+trigger error",
//				"当前值>=基准值-触发误差 且 当前值<=基准值+触发误差")
//				.asRelease(
//						"Current value<reference value - release error, or current value>reference value+release error",
//						"当前值<基准值-解除误差 或 当前值>基准值+解除误差")
//				.asParam1Title("Reference Value", "基准值").asParam2Title("Trigger Error", "触发误差")
//				.asParam3Title("Release Error", "解除误差");
	}

	protected ValEventTp newIns()
	{
		return new VAT_ValEqu();
	}

	public int getTpVal()
	{
		return 7;
	}

	public String getName()
	{
		return "val_equ";
	}
	
	@Override
	public String calValEventTitle(ValEvent va)
	{
		return "Val>="+va.getParamStr1()+"-"+va.getParamStr2() +"&& Val<="+va.getParamStr1()+"+"+va.getParamStr2();
	}

	@Override
	boolean initVA(ValEvent va, StringBuilder failedr)
	{
		String p1 = va.getParamStr1();
		String p2 = va.getParamStr2();
		String p3 = va.getParamStr3();

		refV = Convert.parseToDouble(p1, Double.MIN_VALUE);
		if (Double.MIN_VALUE == refV)
		{
			failedr.append("Reference Value is invalid");
			return false;
		}
		triggerErr = Convert.parseToDouble(p2, Double.MIN_VALUE);
		if (Double.MIN_VALUE == triggerErr)
		{
			failedr.append("Trigger Error is invalid");
			return false;
		}
		releaseErr = Convert.parseToDouble(p3, Double.MIN_VALUE);
		if (Double.MIN_VALUE == releaseErr)
		{
			failedr.append("Release Error is invalid");
			return false;
		}
		return true;
	}
	
	public boolean isFloatVal()
	{
		return true;
	}

	@Override
	public boolean checkTrigger(Number lastv, Number val)
	{
		// double lv = lastv.doubleValue() ;
		double cv = val.doubleValue();
		return (cv >= this.refV - this.triggerErr) && (cv <= this.refV + this.triggerErr);
	}

	@Override
	public boolean checkRelease(Number lastv, Number val)
	{
		double cv = val.doubleValue();
		return (cv < this.refV - this.releaseErr) || (cv > this.refV + this.releaseErr);
	}
}

class VAT_ValNotEqu extends ValEventTp
{
	double refV;

	double triggerErr;
	double releaseErr;

	public VAT_ValNotEqu() // (ValAlert va)
	{

//		this.asTitle("Value <>", "值<>")
//				.asTrigger(
//						"Current value<reference value - trigger error, or current value>reference value+trigger error",
//						"当前值<基准值-触发误差 或 当前值>基准值+触发误差")
//				.asRelease(
//						"Current value>=reference value - release error, and current value<=reference value+release error",
//						"当前值>=基准值-解除误差 且 当前值<=基准值+解除误差")
//				.asParam1Title("Reference Value", "基准值").asParam2Title("Trigger Error", "触发误差")
//				.asParam3Title("Release Error", "解除误差");
	}

	protected ValEventTp newIns()
	{
		return new VAT_ValNotEqu();
	}

	public int getTpVal()
	{
		return 8;
	}

	public String getName()
	{
		return "val_not_equ";
	}
	
	@Override
	public String calValEventTitle(ValEvent va)
	{
		return "Val<"+va.getParamStr1()+"-"+va.getParamStr2() +"||Val>"+va.getParamStr1()+"+"+va.getParamStr2();
	}

	@Override
	boolean initVA(ValEvent va, StringBuilder failedr)
	{
		String p1 = va.getParamStr1();
		String p2 = va.getParamStr2();
		String p3 = va.getParamStr3();

		refV = Convert.parseToDouble(p1, Double.MIN_VALUE);
		if (Double.MIN_VALUE == refV)
		{
			failedr.append("Reference Value is invalid");
			return false;
		}
		triggerErr = Convert.parseToDouble(p2, Double.MIN_VALUE);
		if (Double.MIN_VALUE == triggerErr)
		{
			failedr.append("Trigger Error is invalid");
			return false;
		}
		releaseErr = Convert.parseToDouble(p3, Double.MIN_VALUE);
		if (Double.MIN_VALUE == releaseErr)
		{
			failedr.append("Release Error is invalid");
			return false;
		}
		return true;
	}

	public boolean isFloatVal()
	{
		return true;
	}
	
	@Override
	public boolean checkTrigger(Number lastv, Number val)
	{
		// double lv = lastv.doubleValue() ;
		double cv = val.doubleValue();
		return (cv < this.refV - this.triggerErr) || (cv > this.refV + this.triggerErr);
	}

	@Override
	public boolean checkRelease(Number lastv, Number val)
	{
		double cv = val.doubleValue();
		return (cv >= this.refV - this.releaseErr) && (cv <= this.refV + this.releaseErr);
	}
}

class VAT_ValGt extends ValEventTp
{
	double refV;

	double triggerErr;
	double releaseErr;

	public VAT_ValGt() // (ValAlert va)
	{

//		this.asTitle("Value >", "值>").asTrigger("Current value>reference value + trigger error", "当前值>基准值+触发误差")
//				.asRelease("Current value<=reference value + release error", "当前值<=基准值+解除误差")
//				.asParam1Title("Reference Value", "基准值").asParam2Title("Trigger Error", "触发误差")
//				.asParam3Title("Release Error", "解除误差");
	}

	protected ValEventTp newIns()
	{
		return new VAT_ValGt();
	}

	public int getTpVal()
	{
		return 9;
	}

	public String getName()
	{
		return "val_gt";
	}
	
	public boolean isFloatVal()
	{
		return true;
	}
	
	@Override
	public String calValEventTitle(ValEvent va)
	{
		return "Val>"+va.getParamStr1()+"+"+va.getParamStr2() ;
	}

	@Override
	boolean initVA(ValEvent va, StringBuilder failedr)
	{
		String p1 = va.getParamStr1();
		String p2 = va.getParamStr2();
		String p3 = va.getParamStr3();

		refV = Convert.parseToDouble(p1, Double.MIN_VALUE);
		if (Double.MIN_VALUE == refV)
		{
			failedr.append("Reference Value is invalid");
			return false;
		}
		triggerErr = Convert.parseToDouble(p2, Double.MIN_VALUE);
		if (Double.MIN_VALUE == triggerErr)
		{
			failedr.append("Trigger Error is invalid");
			return false;
		}
		releaseErr = Convert.parseToDouble(p3, Double.MIN_VALUE);
		if (Double.MIN_VALUE == releaseErr)
		{
			failedr.append("Release Error is invalid");
			return false;
		}
		return true;
	}

	@Override
	public boolean checkTrigger(Number lastv, Number val)
	{
		// double lv = lastv.doubleValue() ;
		double cv = val.doubleValue();
		return cv > this.refV + this.triggerErr;
	}

	@Override
	public boolean checkRelease(Number lastv, Number val)
	{
		double cv = val.doubleValue();
		return cv <= this.refV + this.releaseErr;
	}
}

class VAT_ValGtEqu extends ValEventTp
{
	double refV;

	double triggerErr;
	double releaseErr;

	public VAT_ValGtEqu() // (ValAlert va)
	{

//		this.asTitle("Value >=", "值>=").asTrigger("Current value>=reference value + trigger error", "当前值>=基准值+触发误差")
//				.asRelease("Current value<reference value + release error", "当前值<基准值+解除误差")
//				.asParam1Title("Reference Value", "基准值").asParam2Title("Trigger Error", "触发误差")
//				.asParam3Title("Release Error", "解除误差");
	}

	protected ValEventTp newIns()
	{
		return new VAT_ValGtEqu();
	}

	public int getTpVal()
	{
		return 10;
	}

	public String getName()
	{
		return "val_gt_equ";
	}
	
	public boolean isFloatVal()
	{
		return true;
	}
	
	@Override
	public String calValEventTitle(ValEvent va)
	{
		return "Val>="+va.getParamStr1()+"+"+va.getParamStr2() ;
	}

	@Override
	boolean initVA(ValEvent va, StringBuilder failedr)
	{
		String p1 = va.getParamStr1();
		String p2 = va.getParamStr2();
		String p3 = va.getParamStr3();

		refV = Convert.parseToDouble(p1, Double.MIN_VALUE);
		if (Double.MIN_VALUE == refV)
		{
			failedr.append("Reference Value is invalid");
			return false;
		}
		triggerErr = Convert.parseToDouble(p2, Double.MIN_VALUE);
		if (Double.MIN_VALUE == triggerErr)
		{
			failedr.append("Trigger Error is invalid");
			return false;
		}
		releaseErr = Convert.parseToDouble(p3, Double.MIN_VALUE);
		if (Double.MIN_VALUE == releaseErr)
		{
			failedr.append("Release Error is invalid");
			return false;
		}
		return true;
	}

	@Override
	public boolean checkTrigger(Number lastv, Number val)
	{
		// double lv = lastv.doubleValue() ;
		double cv = val.doubleValue();
		return cv >= this.refV + this.triggerErr;
	}

	@Override
	public boolean checkRelease(Number lastv, Number val)
	{
		double cv = val.doubleValue();
		return cv < this.refV + this.releaseErr;
	}
}

class VAT_ValLt extends ValEventTp
{
	double refV;

	double triggerErr;
	double releaseErr;

	public VAT_ValLt() // (ValAlert va)
	{

//		this.asTitle("Value <", "值<").asTrigger("Current value<reference value + trigger error", "当前值<基准值+触发误差")
//				.asRelease("Current value>=reference value + release error", "当前值>=基准值+解除误差")
//				.asParam1Title("Reference Value", "基准值").asParam2Title("Trigger Error", "触发误差")
//				.asParam3Title("Release Error", "解除误差");
	}

	protected ValEventTp newIns()
	{
		return new VAT_ValLt();
	}

	public int getTpVal()
	{
		return 11;
	}

	public String getName()
	{
		return "val_lt";
	}

	public boolean isFloatVal()
	{
		return true;
	}
	
	@Override
	public String calValEventTitle(ValEvent va)
	{
		return "Val<"+va.getParamStr1()+"+"+va.getParamStr2() ;
	}
	
	@Override
	boolean initVA(ValEvent va, StringBuilder failedr)
	{
		String p1 = va.getParamStr1();
		String p2 = va.getParamStr2();
		String p3 = va.getParamStr3();

		refV = Convert.parseToDouble(p1, Double.MIN_VALUE);
		if (Double.MIN_VALUE == refV)
		{
			failedr.append("Reference Value is invalid");
			return false;
		}
		triggerErr = Convert.parseToDouble(p2, Double.MIN_VALUE);
		if (Double.MIN_VALUE == triggerErr)
		{
			failedr.append("Trigger Error is invalid");
			return false;
		}
		releaseErr = Convert.parseToDouble(p3, Double.MIN_VALUE);
		if (Double.MIN_VALUE == releaseErr)
		{
			failedr.append("Release Error is invalid");
			return false;
		}
		return true;
	}

	@Override
	public boolean checkTrigger(Number lastv, Number val)
	{
		// double lv = lastv.doubleValue() ;
		double cv = val.doubleValue();
		return cv < this.refV + this.triggerErr;
	}

	@Override
	public boolean checkRelease(Number lastv, Number val)
	{
		double cv = val.doubleValue();
		return cv >= this.refV + this.releaseErr;
	}
}

class VAT_ValLtEqu extends ValEventTp
{
	double refV;

	double triggerErr;
	double releaseErr;

	public VAT_ValLtEqu() // (ValAlert va)
	{

//		this.asTitle("Value <=", "值<=").asTrigger("Current value<=reference value + trigger error", "当前值<=基准值+触发误差")
//				.asRelease("Current value>reference value + release error", "当前值>基准值+解除误差")
//				.asParam1Title("Reference Value", "基准值").asParam2Title("Trigger Error", "触发误差")
//				.asParam3Title("Release Error", "解除误差");
	}

	protected ValEventTp newIns()
	{
		return new VAT_ValLtEqu();
	}

	public int getTpVal()
	{
		return 12;
	}

	public String getName()
	{
		return "val_lt_equ";
	}

	public boolean isFloatVal()
	{
		return true;
	}
	
	@Override
	public String calValEventTitle(ValEvent va)
	{
		return "Val<="+va.getParamStr1()+"+"+va.getParamStr2() ;
	}
	
	@Override
	boolean initVA(ValEvent va, StringBuilder failedr)
	{
		String p1 = va.getParamStr1();
		String p2 = va.getParamStr2();
		String p3 = va.getParamStr3();

		refV = Convert.parseToDouble(p1, Double.MIN_VALUE);
		if (Double.MIN_VALUE == refV)
		{
			failedr.append("Reference Value is invalid");
			return false;
		}
		triggerErr = Convert.parseToDouble(p2, Double.MIN_VALUE);
		if (Double.MIN_VALUE == triggerErr)
		{
			failedr.append("Trigger Error is invalid");
			return false;
		}
		releaseErr = Convert.parseToDouble(p3, Double.MIN_VALUE);
		if (Double.MIN_VALUE == releaseErr)
		{
			failedr.append("Release Error is invalid");
			return false;
		}
		return true;
	}

	@Override
	public boolean checkTrigger(Number lastv, Number val)
	{
		// double lv = lastv.doubleValue() ;
		double cv = val.doubleValue();
		return cv <= this.refV + this.triggerErr;
	}

	@Override
	public boolean checkRelease(Number lastv, Number val)
	{
		double cv = val.doubleValue();
		return cv > this.refV + this.releaseErr;
	}
}

class VAT_Gradient extends ValEventTp
{
	
	LinkedList<Double> win_vals = new LinkedList<>() ;
	
	GradientTP trigger_tp = GradientTP.up ;
	
	int data_len = 20 ;
	
	double refV; //参考值
	

	public VAT_Gradient() // (ValAlert va)
	{

//		this.asTitle("Value >=", "值>=").asTrigger("Current value>=reference value + trigger error", "当前值>=基准值+触发误差")
//				.asRelease("Current value<reference value + release error", "当前值<基准值+解除误差")
//				.asParam1Title("Reference Value", "基准值").asParam2Title("Trigger Error", "触发误差")
//				.asParam3Title("Release Error", "解除误差");
	}

	protected ValEventTp newIns()
	{
		return new VAT_Gradient();
	}

	public int getTpVal()
	{
		return 13;
	}

	public String getName()
	{
		return "val_gradient";
	}
	
	@Override
	public boolean isFloatVal()
	{
		return true;
	}
	
	@Override
	public String calValEventTitle(ValEvent va)
	{
		return this.trigger_tp.getTitle()+" - "+this.refV;
	}
	
	@Override
	public boolean isSelfJOConfig()
	{//check jo config self ui
		return true;
	}

	@Override
	boolean initVA(ValEvent va, StringBuilder failedr)
	{
		JSONObject jo = va.getParamJO() ;
		if(jo==null)
			jo = new JSONObject() ;
		this.refV = jo.optDouble("ref_val",1.0);
		this.trigger_tp = GradientTP.fromInt(jo.optInt("trigger_tp", 0)) ;
		this.data_len = jo.optInt("data_len", 10) ;
		return true;
	}
	
	private void putVal(double v)
	{
		win_vals.addLast(v);
		if(win_vals.size()>this.data_len)
			win_vals.removeFirst() ;
	}
	
	private double calGradientVal()
	{
		return 0 ;
	}

	@Override
	public boolean checkTrigger(Number lastv, Number val)
	{
		return false;
	}

	@Override
	public boolean checkRelease(Number lastv, Number val)
	{
		return false;
	}
	
	@Override
	public boolean checkByUpdate()
	{
		return true;
	}
	
	public boolean checkReleaseByUpdate(Number val)
	{
		double cv = val.doubleValue();
		//return cv >= this.refV + this.triggerErr;
		putVal(cv) ;
		double gd = calGradientVal() ;
		return gd<this.refV ;
		
	}
	
	public boolean checkTriggerByUpdate(Number val)
	{

		// double lv = lastv.doubleValue() ;
				double cv = val.doubleValue();
				//return cv >= this.refV + this.triggerErr;
				putVal(cv) ;
				double gd = calGradientVal() ;
				return gd>=this.refV ;
	}
}

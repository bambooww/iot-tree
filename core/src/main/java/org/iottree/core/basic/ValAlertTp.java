package org.iottree.core.basic;

import org.iottree.core.util.Convert;

public abstract class ValAlertTp
{
	public static final ValAlertTp[] ALL = new ValAlertTp[] { new VAT_OnOff(), new VAT_NegT(), new VAT_PosT(),
			new VAT_BitEqu(), new VAT_BitOffToOn(), new VAT_BitOnToOff(), new VAT_ValEqu(), new VAT_ValNotEqu(),
			new VAT_ValGt(), new VAT_ValGtEqu(), new VAT_ValLt(), new VAT_ValLtEqu() };

	public static ValAlertTp getTp(int v)
	{
		if (v < 1 || v > 12)
			return null;
		return ALL[v - 1];
	}

	public static ValAlertTp createTp(ValAlert va, int tp_v)
	{
		ValAlertTp tp = getTp(tp_v);
		if (tp == null)
			return null;
		tp = tp.copyMe(va);
		return tp;
	}

	// private int val;
	// private String name ;
	private String titleEn;
	private String titleCn;

	private String descEn = "";
	private String descCn = "";

	protected String triggerEn = "";
	protected String triggerCn = "";

	protected String releaseEn = "";
	protected String releaseCn = "";

	protected String param1TitleCn = "";
	protected String param1TitleEn = "";

	protected String param2TitleCn = "";
	protected String param2TitleEn = "";

	protected String param3TitleCn = "";
	protected String param3TitleEn = "";

	ValAlert valAlert = null;

	boolean bValid = false;

	String invalidReson = null;

	public ValAlertTp()
	{
		// asVA(va);
	}

	public ValAlertTp asVA(ValAlert va)
	{
		this.valAlert = va;
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

	protected abstract ValAlertTp newIns();
	
	public boolean isNeedLastVal()
	{
		return false;
	}
	
	public boolean isFloatVal()
	{
		return false;
	}

	public final ValAlertTp copyMe(ValAlert va)
	{
		ValAlertTp newins = newIns();
		newins.titleEn = this.titleEn;
		newins.titleCn = this.titleCn;
		newins.descEn = this.descEn;
		newins.descCn = this.descCn;
		newins.triggerEn = this.triggerEn;
		newins.triggerCn = this.triggerCn;
		newins.releaseEn = this.releaseEn;
		newins.releaseCn = this.releaseCn;
		newins.param1TitleCn = this.param1TitleCn;
		newins.param1TitleEn = this.param1TitleEn;
		newins.param2TitleCn = this.param2TitleCn;
		newins.param2TitleEn = this.param2TitleEn;
		newins.param3TitleCn = this.param3TitleCn;
		newins.param3TitleEn = this.param3TitleEn;
		newins.asVA(va);
		return newins;
	}

	public ValAlertTp asTitle(String en, String cn)
	{
		titleEn = en;
		titleCn = cn;
		return this;
	}

	public ValAlertTp asDesc(String en, String cn)
	{
		descEn = en;
		descCn = cn;
		return this;
	}

	public ValAlertTp asTrigger(String en, String cn)
	{
		triggerEn = en;
		triggerCn = cn;
		return this;
	}

	public ValAlertTp asRelease(String en, String cn)
	{
		releaseEn = en;
		releaseCn = cn;
		return this;
	}

	public ValAlertTp asParam1Title(String en, String cn)
	{
		param1TitleEn = en;
		param1TitleCn = cn;
		return this;
	}

	public ValAlertTp asParam2Title(String en, String cn)
	{
		param2TitleEn = en;
		param2TitleCn = cn;
		return this;
	}

	public ValAlertTp asParam3Title(String en, String cn)
	{
		param3TitleEn = en;
		param3TitleCn = cn;
		return this;
	}

	public String getTitleEn()
	{
		return this.titleEn;
	}

	public String getTitleCn()
	{
		return this.titleCn;
	}

	// public String getTriggerEn()
	// {
	// return triggerEn;
	// }
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

	public String getTriggerCond(String lang)
	{
		switch (lang)
		{
		case "cn":
			if (Convert.isNullOrEmpty(this.triggerCn))
				return null;

			return "触发条件:" + this.triggerCn;
		default:
			if (Convert.isNullOrEmpty(this.triggerEn))
				return null;
			return "Trigger Condition:" + this.triggerEn;
		}
	}

	// public String getReleaseEn()
	// {
	// return releaseEn;
	// }
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

	public String getReleaseCond(String lang)
	{
		switch (lang)
		{
		case "cn":
			if (Convert.isNullOrEmpty(this.releaseCn))
				return null;
			return "解除条件:" + this.releaseCn;
		default:
			if (Convert.isNullOrEmpty(this.releaseEn))
				return null;
			return "Release Condition:" + this.releaseEn;
		}
	}

	public String getDescEn()
	{
		return descEn;
	}

	public void setDescEn(String descEn)
	{
		this.descEn = descEn;
	}

	public String getDescCn()
	{
		return descCn;
	}

	public void setDescCn(String descCn)
	{
		this.descCn = descCn;
	}

	public String getParam1Title(String lang)
	{
		switch (lang)
		{
		case "cn":
			return this.param1TitleCn;
		default:
			return this.param1TitleEn;
		}
	}

	public String getParam2Title(String lang)
	{
		switch (lang)
		{
		case "cn":
			return this.param2TitleCn;
		default:
			return this.param2TitleEn;
		}
	}

	public String getParam3Title(String lang)
	{
		switch (lang)
		{
		case "cn":
			return this.param3TitleCn;
		default:
			return this.param3TitleEn;
		}
	}

	public final boolean isValid()
	{
		return this.bValid;
	}

	public final String getInvalidReson()
	{
		return this.invalidReson;
	}

	abstract boolean initVA(ValAlert va, StringBuilder failedr);

	public abstract boolean checkTrigger(Number lastv, Number val);

	public abstract boolean checkRelease(Number lastv, Number val);
	
	public abstract String calValAlertTitle(ValAlert va) ;
	
}

class VAT_OnOff extends ValAlertTp
{
	int p1 = 0;

	public VAT_OnOff()// (ValAlert va)
	{
		// super(va);

		this.asTitle("On Off", "开关量").asTrigger("The current value==Alarm value", "当前值==报警值(0/1)")
				.asRelease("The current value<>Alarm value", "当前值<>报警值(0/1)")
				.asParam1Title("Alarm Value(0/1)", "报警值(0/1)");
	}

	protected ValAlertTp newIns()
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
	public String calValAlertTitle(ValAlert va)
	{
		return "On=="+va.getParamStr1() ;
	}

	@Override
	boolean initVA(ValAlert va, StringBuilder failedr)
	{
		String pstr1 = va.getParamStr1();

		if ("1".equals(pstr1))
			p1 = 1;
		else if ("0".equals(pstr1))
			p1 = 0;
		else
		{
			failedr.append("invalid param1 ");
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

class VAT_NegT extends ValAlertTp
{
	public VAT_NegT()// ValAlert va)
	{
		// super(va);

		this.asTitle("Negative Transition", "负跳变")
				.asTrigger("The current value has changed from non-0 to 0", "当前值由非0变化为0")
				.asRelease("The current value has changed from 0 to non-0", "当前值由0变化为非0");

	}

	protected ValAlertTp newIns()
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
	public String calValAlertTitle(ValAlert va)
	{
		return "non-0 to 0";
	}

	@Override
	boolean initVA(ValAlert va, StringBuilder failedr)
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

class VAT_PosT extends ValAlertTp
{
	public VAT_PosT() // (ValAlert va)
	{
		// super(va);
		this.asTitle("Positive transition", "正跳变")
				.asTrigger("The current value has changed from 0 to non-0", "当前值由0变化为非0")
				.asRelease("The current value has changed from non-0 to 0", "当前值由非0变化为0");

	}

	protected ValAlertTp newIns()
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
	public String calValAlertTitle(ValAlert va)
	{
		return "0 to non-0";
	}

	@Override
	boolean initVA(ValAlert va, StringBuilder failedr)
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

class VAT_BitEqu extends ValAlertTp
{
	int bitPos = 0;
	int refV = 0;

	public VAT_BitEqu() // (ValAlert va)
	{
		// super(va);

		this.asTitle("Bit==", "Bit==")
				.asTrigger("Current value of specified bit==specified value(0/1)", "指定位当前值==指定值(0/1)")
				.asRelease("Current value of specified bit<>specified value(0/1)", "指定位当前值<>指定值(0/1)")
				.asParam1Title("Bit Position", "指定位").asParam2Title("Alarm Value(0/1)", "指定值(0/1)");
	}

	protected ValAlertTp newIns()
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
	public String calValAlertTitle(ValAlert va)
	{
		return "Bit("+va.getParamStr1()+")=="+va.getParamStr2();
	}

	@Override
	boolean initVA(ValAlert va, StringBuilder failedr)
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

class VAT_BitOffToOn extends ValAlertTp
{
	int bitPos = 0;

	public VAT_BitOffToOn()// (ValAlert va)
	{
		// super(va);

		this.asTitle("Bit Off->On", "位0->1")
				.asTrigger("The current value of the specified bit changes from 0 to 1", "指定位当前值由0变1")
				.asRelease("The current value of the specified bit changes from 1 to 0", "指定位当前值由1变0")
				.asParam1Title("Bit Position", "指定位");
	}

	protected ValAlertTp newIns()
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
	public String calValAlertTitle(ValAlert va)
	{
		return "Bit("+va.getParamStr1()+") 0 to 1";
	}

	@Override
	boolean initVA(ValAlert va, StringBuilder failedr)
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

class VAT_BitOnToOff extends ValAlertTp
{
	int bitPos = 0;

	public VAT_BitOnToOff()// (ValAlert va)
	{
		// super(va);

		this.asTitle("Bit On->Off", "位1->0")
				.asTrigger("The current value of the specified bit changes from 1 to 0", "指定位当前值由1变0")
				.asRelease("The current value of the specified bit changes from 0 to 1", "指定位当前值由0变1")
				.asParam1Title("Bit Position", "指定位");
	}

	protected ValAlertTp newIns()
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
	public String calValAlertTitle(ValAlert va)
	{
		return "Bit("+va.getParamStr1()+") 1 to 0";
	}
	
	@Override
	boolean initVA(ValAlert va, StringBuilder failedr)
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

class VAT_ValEqu extends ValAlertTp
{
	double refV;

	double triggerErr;
	double releaseErr;

	public VAT_ValEqu()// (ValAlert va)
	{

		this.asTitle("Value ==", "值==").asTrigger(
				"Current value>=reference value - trigger error, and current value<=reference value+trigger error",
				"当前值>=基准值-触发误差 且 当前值<=基准值+触发误差")
				.asRelease(
						"Current value<reference value - release error, or current value>reference value+release error",
						"当前值<基准值-解除误差 或 当前值>基准值+解除误差")
				.asParam1Title("Reference Value", "基准值").asParam2Title("Trigger Error", "触发误差")
				.asParam3Title("Release Error", "解除误差");
	}

	protected ValAlertTp newIns()
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
	public String calValAlertTitle(ValAlert va)
	{
		return "Val>="+va.getParamStr1()+"-"+va.getParamStr2() +"&& Val<="+va.getParamStr1()+"+"+va.getParamStr2();
	}

	@Override
	boolean initVA(ValAlert va, StringBuilder failedr)
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

class VAT_ValNotEqu extends ValAlertTp
{
	double refV;

	double triggerErr;
	double releaseErr;

	public VAT_ValNotEqu() // (ValAlert va)
	{

		this.asTitle("Value <>", "值<>")
				.asTrigger(
						"Current value<reference value - trigger error, or current value>reference value+trigger error",
						"当前值<基准值-触发误差 或 当前值>基准值+触发误差")
				.asRelease(
						"Current value>=reference value - release error, and current value<=reference value+release error",
						"当前值>=基准值-解除误差 且 当前值<=基准值+解除误差")
				.asParam1Title("Reference Value", "基准值").asParam2Title("Trigger Error", "触发误差")
				.asParam3Title("Release Error", "解除误差");
	}

	protected ValAlertTp newIns()
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
	public String calValAlertTitle(ValAlert va)
	{
		return "Val<"+va.getParamStr1()+"-"+va.getParamStr2() +"||Val>"+va.getParamStr1()+"+"+va.getParamStr2();
	}

	@Override
	boolean initVA(ValAlert va, StringBuilder failedr)
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

class VAT_ValGt extends ValAlertTp
{
	double refV;

	double triggerErr;
	double releaseErr;

	public VAT_ValGt() // (ValAlert va)
	{

		this.asTitle("Value >", "值>").asTrigger("Current value>reference value + trigger error", "当前值>基准值+触发误差")
				.asRelease("Current value<=reference value + release error", "当前值<=基准值+解除误差")
				.asParam1Title("Reference Value", "基准值").asParam2Title("Trigger Error", "触发误差")
				.asParam3Title("Release Error", "解除误差");
	}

	protected ValAlertTp newIns()
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
	public String calValAlertTitle(ValAlert va)
	{
		return "Val>"+va.getParamStr1()+"+"+va.getParamStr2() ;
	}

	@Override
	boolean initVA(ValAlert va, StringBuilder failedr)
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

class VAT_ValGtEqu extends ValAlertTp
{
	double refV;

	double triggerErr;
	double releaseErr;

	public VAT_ValGtEqu() // (ValAlert va)
	{

		this.asTitle("Value >=", "值>=").asTrigger("Current value>=reference value + trigger error", "当前值>=基准值+触发误差")
				.asRelease("Current value<reference value + release error", "当前值<基准值+解除误差")
				.asParam1Title("Reference Value", "基准值").asParam2Title("Trigger Error", "触发误差")
				.asParam3Title("Release Error", "解除误差");
	}

	protected ValAlertTp newIns()
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
	public String calValAlertTitle(ValAlert va)
	{
		return "Val>="+va.getParamStr1()+"+"+va.getParamStr2() ;
	}

	@Override
	boolean initVA(ValAlert va, StringBuilder failedr)
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

class VAT_ValLt extends ValAlertTp
{
	double refV;

	double triggerErr;
	double releaseErr;

	public VAT_ValLt() // (ValAlert va)
	{

		this.asTitle("Value <", "值<").asTrigger("Current value<reference value + trigger error", "当前值<基准值+触发误差")
				.asRelease("Current value>=reference value + release error", "当前值>=基准值+解除误差")
				.asParam1Title("Reference Value", "基准值").asParam2Title("Trigger Error", "触发误差")
				.asParam3Title("Release Error", "解除误差");
	}

	protected ValAlertTp newIns()
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
	public String calValAlertTitle(ValAlert va)
	{
		return "Val<"+va.getParamStr1()+"+"+va.getParamStr2() ;
	}
	
	@Override
	boolean initVA(ValAlert va, StringBuilder failedr)
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

class VAT_ValLtEqu extends ValAlertTp
{
	double refV;

	double triggerErr;
	double releaseErr;

	public VAT_ValLtEqu() // (ValAlert va)
	{

		this.asTitle("Value <=", "值<=").asTrigger("Current value<=reference value + trigger error", "当前值<=基准值+触发误差")
				.asRelease("Current value>reference value + release error", "当前值>基准值+解除误差")
				.asParam1Title("Reference Value", "基准值").asParam2Title("Trigger Error", "触发误差")
				.asParam3Title("Release Error", "解除误差");
	}

	protected ValAlertTp newIns()
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
	public String calValAlertTitle(ValAlert va)
	{
		return "Val<="+va.getParamStr1()+"+"+va.getParamStr2() ;
	}
	
	@Override
	boolean initVA(ValAlert va, StringBuilder failedr)
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

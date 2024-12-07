package org.iottree.core.basic;

import org.iottree.core.util.Lan;

public enum ValUnit
{

	C, F, K,

	m, km, ft, in, yd, mi,

	mps, kmph, mph,

	Pa, mb, mmHg, inHg, Bar, MPa, kPa,

	g, kg, t, oz, lb,

	L, gal, m3, ft3,

	m3ps, m3ph, Lpmin, Lps, gpm, gph, cfm, cim,

	g_pm3, percent,

	A, mA, uA, V, mV, uV, R, kR, MR, Fr, mFr, uFr, nFr, pFr, H, mH, uH, W, kW, MW,

	J, kJ, Wh, kWh,

	Hz, kHz, MHz, GHz,

	col, mcol, ucol,

	S, mS, uS,spm,mspcm,
	
	ntu,jtu,mgpl,  ppm,
	
	rpm,rps,rph,dps,radps,cpm,

	s, min, hr, d, wk, yr;

	private static Lan lan = Lan.getLangInPk(ValUnit.class);

	public String getTitle()
	{
		return lan.g("vu_" + this.name());
	}

	public String getUnit()
	{
		return lan.gn("vu_" + this.name()).getAttr("unit");
	}
}

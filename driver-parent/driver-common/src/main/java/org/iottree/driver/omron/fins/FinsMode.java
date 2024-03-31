package org.iottree.driver.omron.fins;

import java.util.HashMap;

import org.iottree.core.UAVal.ValTP;

public class FinsMode
{
	public static class AreaCode
	{
		String name ;
		
		String title ;
		
		ValTP dataTp ;
		
		int code ;
		
		public AreaCode(String n,String t,ValTP data_tp,int code)
		{
			this.name = n ;
			this.title = t ;
			this.dataTp = data_tp ;
			this.code = code ;
		}
		
		public String getName()
		{
			return this.name ;
		}
		
		public String getTitle()
		{
			return title ;
		}
		
		public short getCode()
		{
			return (short)this.code;
		}
		
		public boolean isBit()
		{
			return dataTp==ValTP.vt_bool ;
		}
		
		public boolean isWord()
		{
			return dataTp==ValTP.vt_int16 || dataTp==ValTP.vt_uint16 ;
		}
	}
	
	protected HashMap<String,AreaCode> name2code_bit = new HashMap<>() ;
	
	protected HashMap<String,AreaCode> name2code_word = new HashMap<>() ;
	
	protected FinsMode()
	{}
	
	protected void setAreaCode(AreaCode ac)
	{
		if(ac.isBit())
		{
			name2code_bit.put(ac.name, ac) ;
		}
		else
		{
			name2code_word.put(ac.name, ac) ;
		}
	}
	
	public AreaCode getAreaCodeBit(String name)
	{
		return name2code_bit.get(name) ;
	}
	
	public AreaCode getAreaCodeWord(String name)
	{
		return name2code_word.get(name) ;
	}
	
	private static FinsMode_CS modelCSCJ = new FinsMode_CS() ;
	
	public static FinsMode getModel_CS_CJ()
	{
		return modelCSCJ ;
	}
}

/**
 * mode cs /cj
 * @author jason.zhu
 *
 */
class FinsMode_CS extends FinsMode
{
	protected FinsMode_CS()
	{
		setAreaCode(new AreaCode("CIO","CIO Bit Area",ValTP.vt_bool,0x30));
		setAreaCode(new AreaCode("WR","Work Area Bit Area",ValTP.vt_bool,0x31));
		setAreaCode(new AreaCode("HR","Holding Bit Area",ValTP.vt_bool,0x32));
		setAreaCode(new AreaCode("AR","Auxiliary Bit Area",ValTP.vt_bool,0x33));
		
		setAreaCode(new AreaCode("CIO","CIO Area",ValTP.vt_int16,0xB0));
		setAreaCode(new AreaCode("WR","Work Area",ValTP.vt_int16,0xB1));
		setAreaCode(new AreaCode("HR","Holding Area",ValTP.vt_int16,0xB2));
		setAreaCode(new AreaCode("AR","Auxiliary Area",ValTP.vt_int16,0xB3));
		
	}
}

class FinsMode_CV extends FinsMode
{
	
}

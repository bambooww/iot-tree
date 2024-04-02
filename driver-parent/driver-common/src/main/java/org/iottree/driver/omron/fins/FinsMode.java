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
	
	private static FinsMode_CS modelCSCJ = new FinsMode_CS(false) ;
	
	private static FinsMode_CS modelCJ2 = new FinsMode_CS(true) ;
	
	private static FinsMode_CV modeCV = new FinsMode_CV() ;
	
	public static FinsMode getMode_CS_CJ1()
	{
		return modelCSCJ ;
	}
	
	public static FinsMode getMode_CJ2()
	{
		return modelCJ2;
	}
	
	public static FinsMode getMode_CV()
	{
		return modeCV ;
	}
}

/**
 * mode cs /cj
 * @author jason.zhu
 *
 */
class FinsMode_CS extends FinsMode
{
	protected FinsMode_CS(boolean cj2)
	{
		setAreaCode(new AreaCode("CIO","CIO Bit Area",ValTP.vt_bool,0x30));
		setAreaCode(new AreaCode("W","Work Area Bit Area",ValTP.vt_bool,0x31));
		setAreaCode(new AreaCode("H","Holding Bit Area",ValTP.vt_bool,0x32));
		setAreaCode(new AreaCode("A","Auxiliary Bit Area",ValTP.vt_bool,0x33));
		
		setAreaCode(new AreaCode("CIO","CIO Area",ValTP.vt_int16,0xB0));
		setAreaCode(new AreaCode("W","Work Area",ValTP.vt_int16,0xB1));
		setAreaCode(new AreaCode("H","Holding Area",ValTP.vt_int16,0xB2));
		setAreaCode(new AreaCode("A","Auxiliary Area",ValTP.vt_int16,0xB3));
		
		setAreaCode(new AreaCode("TS","Timer Completion Flag (Status)",ValTP.vt_bool,0x09));
		setAreaCode(new AreaCode("CS","Counter Completion Flag (Status)",ValTP.vt_bool,0x09));
		
		setAreaCode(new AreaCode("T","Timer Area",ValTP.vt_int16,0x89));
		setAreaCode(new AreaCode("C","Counter Area",ValTP.vt_int16,0x89));
		
		setAreaCode(new AreaCode("D","Data Memory Area",ValTP.vt_bool,0x02));
		setAreaCode(new AreaCode("D","Data Memory Area",ValTP.vt_int16,0x82));
		
		setAreaCode(new AreaCode("DR","Data Register",ValTP.vt_int16,0xBC));
		

		for(int i = 0 ; i <= 0xF ; i ++)
		{
			String str_i = (i<10?("0"+i):(""+i)) ;
			setAreaCode(new AreaCode("E"+str_i+":","Expansion Data Memory",ValTP.vt_bool,0x20+i));
			if(cj2)
				setAreaCode(new AreaCode("E"+str_i+":","Expansion Data Memory",ValTP.vt_int16,0x50+i));
			else
				setAreaCode(new AreaCode("E"+str_i+":","Expansion Data Memory",ValTP.vt_int16,0xA0+i));
		}
		for(int i = 0x10 ; i <= 0x18 ; i ++)
		{
			String str_i = (i<10?("0"+i):(""+i)) ;
			setAreaCode(new AreaCode("E"+str_i+":","Expansion Data Memory",ValTP.vt_bool,0xE0+i));
			setAreaCode(new AreaCode("E"+str_i+":","Expansion Data Memory",ValTP.vt_int16,0x60+i));
		}
		
		setAreaCode(new AreaCode("IR","Index Register",ValTP.vt_int32,0xDC));
	}
}

class FinsMode_CV extends FinsMode
{
	protected FinsMode_CV()
	{
		setAreaCode(new AreaCode("CIO","CIO Bit Area",ValTP.vt_bool,0x00));
		setAreaCode(new AreaCode("A","Auxiliary Bit Area",ValTP.vt_bool,0x00));
		
		setAreaCode(new AreaCode("CIO","CIO Area",ValTP.vt_int16,0x80));
		setAreaCode(new AreaCode("A","Auxiliary Area",ValTP.vt_int16,0x80));
		
		setAreaCode(new AreaCode("TS","Timer Completion Flag (Status)",ValTP.vt_bool,0x01));
		setAreaCode(new AreaCode("CS","Counter Completion Flag (Status)",ValTP.vt_bool,0x01));
		setAreaCode(new AreaCode("T","Timer Area",ValTP.vt_int16,0x81));
		setAreaCode(new AreaCode("C","Counter Area",ValTP.vt_int16,0x81));
		
		setAreaCode(new AreaCode("D","Data Memory Area",ValTP.vt_int16,0x82));
		
		setAreaCode(new AreaCode("DR","Data Register",ValTP.vt_int16,0x9C));

		for(int i = 0 ; i <= 0x7 ; i ++)
		{
			String str_i = (i<10?("0"+i):(""+i)) ;
			setAreaCode(new AreaCode("E"+str_i+":","Expansion Data Memory",ValTP.vt_int16,0x90+i));
		}
	}
}

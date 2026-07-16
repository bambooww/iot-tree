package org.iottree.core.devtree.cxt;


import java.util.UUID;

import javax.script.CompiledScript;
import javax.script.ScriptException;

import org.iottree.core.util.Convert;

import com.oracle.truffle.js.builtins.JSONBuiltins.JSON;

public class DevNodeCodeItem
{

	static ThreadLocal<Boolean> thInJS = new ThreadLocal<>() ;
	
	public static boolean isRunInJS()
	{
		Boolean b = thInJS.get() ;
		if(b==null)
			return false; 
		
		return b ;
	}
			
	String name = null ;
	
	/**
	 * 
	 */
	String codeTxt = null ;
	
	transient CompiledScript codeCS = null ;
	
	//transient CompiledScript callFnCS = null ;
	
	transient String blockFn = null;
	
	transient DevContext cxt = null;
	
	transient boolean bValid = false;
	
	public DevNodeCodeItem()
	{}
	
	public DevNodeCodeItem(String name,String codetxt)
	{
		this.name = name ;
		this.codeTxt = codetxt ;
	}
	
	public DevNodeCodeItem(String name,String codetxt,DevContext cxt) throws ScriptException
	{
		this.name = name ;
		this.codeTxt = codetxt ;
		initItem(cxt) ;
	}
	
	public DevNodeCodeItem(String name,String block_fn,String codetxt,DevContext cxt) //throws ScriptException
	{
		this.name = name ;
		this.codeTxt = codetxt ;
		this.blockFn = block_fn ;
		this.cxt = cxt ;
		try
		{
			//cxt.getScriptEngine().eval(this.codeTxt);
			
			codeCS = cxt.scriptCompile(true, codetxt);
			
//			if(blockFn!=null)
//			{
//				codeCS.eval() ;
//				callFnCS = cp.compile(UAContext.FN_TEMP_VAR+"."+blockFn+"($input)") ;
//			}
			bValid = true ;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private String calParamStr(String ...param_names)
	{
		String tmps="" ;
		int pnum =param_names.length;
		if(pnum>0)
		{
			tmps += param_names[0] ;
			for(int i = 1 ; i < pnum ; i ++)
			{
				tmps += ","+param_names[i] ;
			}
		}
		return tmps ;
	}
	
	public boolean initItem(DevContext cxt,String... param_names) throws ScriptException
	{
		if(Convert.isNullOrEmpty(this.codeTxt))
			return false;
		this.cxt = cxt ;
		String tmps =  this.codeTxt = this.codeTxt.trim();
		boolean bblock = false;
		if(codeTxt.startsWith("{"))
		{//block 
			blockFn = createUniqueFn() ;
			String pmstr = calParamStr(param_names) ;
			tmps = "function "+blockFn+"_inn(";
			tmps += pmstr ;
			tmps+= ")"+this.codeTxt ;
			
			tmps += "\r\n function "+blockFn+"(" ;
			tmps += pmstr ;
			tmps+= "){\r\n" ;
			
			tmps+= "let ret="+blockFn+"_inn("+pmstr+");\r\n" ;
			tmps += "if(!ret) return ret ;\r\n" + 
					"		if(typeof(ret)=='object')\r\n" + 
					"			return JSON.stringify(ret) ;\r\n" + 
					"		return ret ;" ;
			tmps += "\r\n}" ;
			bblock=true ;
		}
		
		
		//this.codeTxt = UAContext.FN_TEMP_VAR+"."+blockFn+"=function($input)"+this.codeTxt ;
		//this.codeTxt = "function "+blockFn+"($input)"+this.codeTxt ;

			//cxt.getScriptEngine().eval(this.codeTxt);
			
			codeCS = cxt.scriptCompile(bblock, tmps);
			
//			if(blockFn!=null)
//			{
//				codeCS.eval() ;
//				callFnCS = cp.compile(UAContext.FN_TEMP_VAR+"."+blockFn+"($input)") ;
//			}
			bValid = true ;
			return true;
		
	}
	
	public boolean initItem(DevContext cxt) throws ScriptException
	{
		return initItem(cxt,"$input") ;

	}
	
	private String createUniqueFn()
	{
		return "f"+UUID.randomUUID().toString().replaceAll("-", "") ;
	}
	
	//public UACodeItem
	
	public String getName()
	{
		return this.name ;
	}
	
	public String getCodeTxt()
	{
		return codeTxt ;
	}
	
	public boolean isValid()
	{
		return bValid ;
	}
	
	public Object runCode() throws ScriptException, NoSuchMethodException
	{
		if(blockFn!=null)
			return runCodeFunc();
		
		try
		{
			thInJS.set(true);
			
			synchronized(cxt)
			{
				return codeCS.eval() ;
			}
		}
		finally
		{
			thInJS.remove();
		}
//		if(blockFn==null)
//			return codeCS.eval() ;
//		//Invocable inv = (Invocable)cxt.getScriptEngine() ;
//		//return inv.invokeFunction(UAContext.FN_TEMP_VAR+"."+blockFn) ;
//		//return this.cxt.getScriptEngine().eval(UAContext.FN_TEMP_VAR+"."+blockFn+"()");
//		return callFnCS.eval() ;
	}
	
	public Object runCodeFunc(Object... paramvals) throws NoSuchMethodException, ScriptException
	{
		if(!bValid)
			throw new NoSuchMethodException("no valid") ;
		try
		{
			thInJS.set(true);
			
			return cxt.scriptInvoke(blockFn, paramvals) ;
		}
		finally
		{
			thInJS.remove();
		}
	}
	
	
//	public Object runCodeInput(Object inputv) throws ScriptException, NoSuchMethodException
//	{
//		
//		if(blockFn==null)
//			return codeCS.eval() ;
//		//Invocable inv = (Invocable)cxt.getScriptEngine() ;
//		//return inv.invokeFunction(UAContext.FN_TEMP_VAR+"."+blockFn) ;
//		//return this.cxt.getScriptEngine().eval(UAContext.FN_TEMP_VAR+"."+blockFn+"()");
//		return callFnCS.eval() ;
//	}
	
//	public UAVal runCodeAsUAVal()
//	{
//		try
//		{
//			Object v = runCode() ;
//			long cdt = System.currentTimeMillis() ;
//			return new UAVal(true,v,cdt,cdt) ;
//		}
//		catch(Exception e)
//		{
//			UAVal r = new UAVal() ;
//			r.setValException("jscode_err",e) ;
//			return r ;
//		}
//	}
	
	public void delBlockCode() throws ScriptException
	{
		//this.cxt.getScriptEngine().eval("delete "+UAContext.FN_TEMP_VAR+"."+blockFn) ;
		this.cxt.scriptEval("delete "+blockFn) ;
	}
}


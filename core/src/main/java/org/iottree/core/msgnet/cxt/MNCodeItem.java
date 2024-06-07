package org.iottree.core.msgnet.cxt;

import java.util.UUID;

import javax.script.CompiledScript;
import javax.script.ScriptException;

import org.iottree.core.util.Convert;

public class MNCodeItem
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
	
	transient MNContext cxt = null;
	
	transient boolean bValid = false;
	
	public MNCodeItem()
	{}
	
	public MNCodeItem(String name,String codetxt)
	{
		this.name = name ;
		this.codeTxt = codetxt ;
	}
	
	public MNCodeItem(String name,String codetxt,MNContext cxt) throws ScriptException
	{
		this.name = name ;
		this.codeTxt = codetxt ;
		initItem(cxt) ;
	}
	
	public boolean initItem(MNContext cxt,String... param_names) //throws ScriptException
	{
		if(Convert.isNullOrEmpty(this.codeTxt))
			return false;
		this.cxt = cxt ;
		String tmps =  this.codeTxt = this.codeTxt.trim();
		boolean bblock = false;
		if(codeTxt.startsWith("{"))
		{//block 
			blockFn = createUniqueFn() ;
			tmps = "function "+blockFn+"(";
			int pnum =param_names.length;
			if(pnum>0)
			{
				tmps += param_names[0] ;
				for(int i = 1 ; i < pnum ; i ++)
				{
					tmps += ","+param_names[i] ;
				}
			}
			tmps+= ")"+this.codeTxt ;
			bblock=true ;
		}
		
		//this.codeTxt = UAContext.FN_TEMP_VAR+"."+blockFn+"=function($input)"+this.codeTxt ;
		//this.codeTxt = "function "+blockFn+"($input)"+this.codeTxt ;
		try
		{
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
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean initItem(MNContext cxt) //throws ScriptException
	{
		return initItem(cxt,"$input") ;
//		if(Convert.isNullOrEmpty(this.codeTxt))
//			return false;
//		this.cxt = cxt ;
//		this.codeTxt = this.codeTxt.trim();
//		boolean bblock = false;
//		if(codeTxt.startsWith("{"))
//		{//block 
//			blockFn = createUniqueFn() ;
//			this.codeTxt = "function "+blockFn+"($input)"+this.codeTxt ;
//			bblock=true ;
//		}
//		
//		//this.codeTxt = UAContext.FN_TEMP_VAR+"."+blockFn+"=function($input)"+this.codeTxt ;
//		//this.codeTxt = "function "+blockFn+"($input)"+this.codeTxt ;
//		try
//		{
//			//cxt.getScriptEngine().eval(this.codeTxt);
//			
//			Compilable cp = (Compilable)cxt.getScriptEngine() ;
//			codeCS = cp.compile(this.codeTxt) ;
//			if(bblock)
//				codeCS.eval() ;
////			if(blockFn!=null)
////			{
////				codeCS.eval() ;
////				callFnCS = cp.compile(UAContext.FN_TEMP_VAR+"."+blockFn+"($input)") ;
////			}
//			bValid = true ;
//			return true;
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//			return false;
//		}
	}
	
//	public void initItem()
//	{
//		Context context = Context.newBuilder().allowAllAccess(true).allowHostClassLoading(true).allowHostAccess(HostAccess.ALL).allowIO(true).allowNativeAccess(true).build();
//		context.getBindings(UANode.JS_NAME).putMember("mydalong",dalong);
//		context.
//	}
	
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
//		//function name must no in obj
//		Invocable inv = (Invocable)cxt.getScriptEngine() ;
//		return inv.invokeFunction(blockFn, paramvals) ;
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

	public void delBlockCode() throws ScriptException
	{
		//this.cxt.getScriptEngine().eval("delete "+UAContext.FN_TEMP_VAR+"."+blockFn) ;
		this.cxt.scriptEval("delete "+blockFn) ;
	}
}

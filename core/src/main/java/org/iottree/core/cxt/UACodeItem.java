package org.iottree.core.cxt;

import java.util.UUID;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;
import org.iottree.core.UAVal;
import org.iottree.core.util.Convert;

/**
 * code express or code block
 * 
 * @author zzj
 *
 */
public class UACodeItem
{
	String name = null ;
	
	/**
	 * 
	 */
	String codeTxt = null ;
	
	transient CompiledScript codeCS = null ;
	
	//transient CompiledScript callFnCS = null ;
	
	transient String blockFn = null;
	
	transient UAContext cxt = null;
	
	transient boolean bValid = false;
	
	public UACodeItem()
	{}
	
	public UACodeItem(String name,String codetxt)
	{
		this.name = name ;
		this.codeTxt = codetxt ;
	}
	
	public UACodeItem(String name,String codetxt,UAContext cxt) throws ScriptException
	{
		this.name = name ;
		this.codeTxt = codetxt ;
		initItem(cxt) ;
	}
	
	public boolean initItem(UAContext cxt) //throws ScriptException
	{
		if(Convert.isNullOrEmpty(this.codeTxt))
			return false;
		this.cxt = cxt ;
		this.codeTxt = this.codeTxt.trim();
		boolean bblock = false;
		if(codeTxt.startsWith("{"))
		{//block 
			blockFn = createUniqueFn() ;
			this.codeTxt = "function "+blockFn+"($input)"+this.codeTxt ;
			bblock=true ;
		}
		
		//this.codeTxt = UAContext.FN_TEMP_VAR+"."+blockFn+"=function($input)"+this.codeTxt ;
		//this.codeTxt = "function "+blockFn+"($input)"+this.codeTxt ;
		try
		{
			//cxt.getScriptEngine().eval(this.codeTxt);
			
			Compilable cp = (Compilable)cxt.getScriptEngine() ;
			codeCS = cp.compile(this.codeTxt) ;
			if(bblock)
				codeCS.eval() ;
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
			return runCodeFunc(null);
		return codeCS.eval() ;
//		if(blockFn==null)
//			return codeCS.eval() ;
//		//Invocable inv = (Invocable)cxt.getScriptEngine() ;
//		//return inv.invokeFunction(UAContext.FN_TEMP_VAR+"."+blockFn) ;
//		//return this.cxt.getScriptEngine().eval(UAContext.FN_TEMP_VAR+"."+blockFn+"()");
//		return callFnCS.eval() ;
	}
	
	public Object runCodeFunc(Object inputv) throws NoSuchMethodException, ScriptException
	{
		//function name must no in obj
		Invocable inv = (Invocable)cxt.getScriptEngine() ;
		return inv.invokeFunction(blockFn, inputv) ;
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
	
	public UAVal runCodeAsUAVal()
	{
		try
		{
			Object v = runCode() ;
			return new UAVal(true,v,System.currentTimeMillis()) ;
		}
		catch(Exception e)
		{
			UAVal r = new UAVal() ;
			r.setValException("jscode_err",e) ;
			return r ;
		}
	}
	
	public void delBlockCode() throws ScriptException
	{
		//this.cxt.getScriptEngine().eval("delete "+UAContext.FN_TEMP_VAR+"."+blockFn) ;
		this.cxt.getScriptEngine().eval("delete "+blockFn) ;
	}
}

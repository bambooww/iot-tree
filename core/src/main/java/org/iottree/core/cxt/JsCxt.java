package org.iottree.core.cxt;

import java.io.*;
import java.util.*;
import java.util.function.Predicate;

import javax.script.*;

/**
 * https://blog.csdn.net/xiao_jun_0820/article/details/76498268
 * @author zzj
 */
public class JsCxt
{
	//static final String JS_NAME="graal.js" ;
	static final String JS_NAME="nashorn";
	
	public void setTagVal(String tagid,Object val)
	{
		System.out.println("set tag val="+tagid+" "+val+"    "+val.getClass().getCanonicalName()) ;
	}
	
	private ScriptEngine test1() throws ScriptException
	{
		String script_txt = "var robj="+"{a:10,b:["
				+ "{n:'aaa',val:10,dt:123123123},"
				+ "{n:'a下a',val:3.5410,dt:123123123},"
				+ "{n:'bbb中文',val:8,dt:123123123}"
				+ "]}\r\n" ;
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName(JS_NAME);
		
		engine.eval(script_txt) ;
		engine.put("$out", this);
		
		script_txt= "for(var i= 0 ; i < robj.b.length ; i ++)"
				+"{ var tmpob =robj.b[i]; "
				+ " $out.setTagVal('xxx.xxx.'+tmpob.n,tmpob.val) ;"
				+ "}" ;
		
		engine.eval(script_txt) ;
		
		engine.eval("function _do_adapter(){\r\n"
				 +script_txt
				+"}\r\n");
		return engine ;
	}
	
	private void test2(ScriptEngine engine) throws ScriptException, NoSuchMethodException
	{
		String script_txt = "var robj="+"{a:10,b:["
				+ "{n:'aaa',val:10,dt:123123123},"
				+ "{n:'a下a',val:3.5410,dt:123123123},"
				+ "{n:'bbb中文',val:8,dt:123123123}"
				+ "]}\r\n" ;
		//ScriptEngineManager manager = new ScriptEngineManager();
		//ScriptEngine engine = manager.getEngineByName(JS_NAME);
		
		engine.eval(script_txt) ;
		engine.put("$out", this);
		Invocable jsInvoke = (Invocable) engine;
		
		jsInvoke.invokeFunction("_do_adapter");
	}
	
	public static void main(String[] args) throws Exception
	{
		JsCxt jc = new JsCxt();
		ScriptEngine engine = jc.test1();
		System.out.println("test 1  end --------") ;
		jc.test2(engine);
		System.out.println("test 2  end --------") ;
				
		testList();
		//testfile();
		// test在脚本中调用Java对象和方法();
		testjs();
		test向js脚本引擎传递变量();
		test脚本预编译();
	}
	
	public static class Adder
	{
		public int add(int a,int b)
		{
			return a+b ;
		}
	}

	@SuppressWarnings("restriction")
	private static void test脚本预编译() throws Exception
	{
		System.out.println("test脚本预编译--------");
		ScriptEngineManager manager = new ScriptEngineManager();

		ScriptEngine engine = manager.getEngineByName(JS_NAME);
		
		Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
		//bindings.
		//可以考虑这个，开启一切可开启的..
		//bindings.put("polyglot.js.allowAllAccess",true);
		        
		bindings.put("polyglot.js.allowHostAccess", true);
		bindings.put("polyglot.js.allowHostClassLookup", (Predicate<String>) s -> true);
		
		//ScriptEngine s1 = manager.getEngineByName("js");
		//engine.

		long st = System.currentTimeMillis() ;
		engine.eval("function f7e163a09fc7c4c17beee59b4f31e1479(){\r\n" + 
				"var a=12;\r\n" + 
				"return a;\r\n" + 
				"}");
		System.out.println("eval cost="+(System.currentTimeMillis()-st)) ;
		Invocable jsInvoke = (Invocable) engine;

		st = System.currentTimeMillis() ;
		Object result1 = jsInvoke.invokeFunction("f7e163a09fc7c4c17beee59b4f31e1479");
		System.out.println("inv func cost="+(System.currentTimeMillis()-st)) ;
		System.out.println("result1=" + result1);
		
		st = System.currentTimeMillis() ;
		result1 = jsInvoke.invokeFunction("f7e163a09fc7c4c17beee59b4f31e1479");
		System.out.println("inv func 2 cost="+(System.currentTimeMillis()-st)) ;
		System.out.println("result1 2=" + result1);

		//grass js 
		Adder adder = jsInvoke.getInterface(Adder.class);
		int result2 = adder.add(10, 5);
		System.out.println("result2=" + result2);
	}

	private static void test向js脚本引擎传递变量()
	{
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName(JS_NAME);
		engine.put("a", 1);
		engine.put("b", 5);

		Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
		Object a = bindings.get("a");
		Object b = bindings.get("b");
		System.out.println("a = " + a);
		System.out.println("b = " + b);

		Object result;
		try
		{
			result = engine.eval("c = a + b;");
			System.out.println("a + b = " + result);
		}
		catch (ScriptException e)
		{
			e.printStackTrace();
		}
	}

	private static void testjs()
	{
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName(JS_NAME);
		//engine.
		String script = "print ('www中文啊啊啊啊啊啊')";
		//engine.getContext().setWriter(writer);
		//ScriptContext sc = new ScriptContext() ;
		try
		{
			engine.eval(script);
		}
		catch (ScriptException e)
		{
			e.printStackTrace();
		}

	}

	private static void test在脚本中调用Java对象和方法()
	{

		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine jsEngine;
		jsEngine = manager.getEngineByExtension(JS_NAME);
		try
		{
			jsEngine.eval(
					"importPackage(javax.swing);" + "var optionPane =JOptionPane.showMessageDialog(null, 'Hello!');");
		} catch (ScriptException e)
		{
			e.printStackTrace();
		}
	}

//	private static void testfile()
//	{
//		ScriptEngineManager manager = new ScriptEngineManager();
//		ScriptEngine engine = manager.getEngineByName("js");
//		try(FileInputStream fis = new FileInputStream("src/my.js");
//				InputStreamReader reader = new InputStreamReader(fis, "UTF-8");
//				)
//		{
//			System.out.println(reader.getEncoding());
//			engine.eval(reader);
//			reader.close();
//		} catch (Exception e)
//		{
//			e.printStackTrace();
//		}
//	}

	private static void testList()
	{
		ScriptEngineManager manager = new ScriptEngineManager();
		List<ScriptEngineFactory> factoryList = manager.getEngineFactories();
		//System.out.println(factoryList.size());
		for (ScriptEngineFactory factory : factoryList)
		{
			System.out.println(factory.getEngineName() + "=" + factory.getLanguageName());
		}
	}

	
	
}

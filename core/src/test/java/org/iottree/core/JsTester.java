package org.iottree.core;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.SourceSection;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyObject;

import junit.framework.TestCase;

public class JsTester extends TestCase
{
	public void testMethod1()
	{
		System.out.println("Hello Java!");
		// ProxyObject.fromMap(values)
		try (Context context = Context.newBuilder().allowAllAccess(true).build())
		{
			context.eval("js", "print('Hello JavaScript!');");
			context.eval("js", "let user = {name:\"dalong\",age:333}; print(JSON.stringify(user))");
			java.math.BigDecimal v = context
					.eval("js",
							"var BigDecimal = Java.type('java.math.BigDecimal');" + "BigDecimal.valueOf(10).pow(20)")
					.asHostObject();
			System.out.println(v.toString());
		}
	}

	public void testMethod2() throws ScriptException, NoSuchMethodException
	{
		ScriptEngine eng = new ScriptEngineManager().getEngineByName("js");
		eng.eval("let user = {name:\"dalong\",age:333}; print(JSON.stringify(user))");
	}

	public void testMethod3() throws IOException
	{
		Value value = null;
		Source mysource = Source
				.newBuilder("js",
						"import demo from \"src/main/resources/demo2.js\"\n" + "let info  = demo()\n"
								+ "console.log(\"-----js-------\")\n" + "console.log(info)\n"
								+ "console.log(\"-----js-------\")\n",
						"demoeeee")
				.mimeType("application/javascript+module").build();
		try (Context context = Context.newBuilder().allowAllAccess(true).build())
		{
			value = context.parse(mysource);
			value.execute();
		} catch (PolyglotException e)
		{
			if (e.isSyntaxError())
			{
				SourceSection location = e.getSourceLocation();
			} else
			{
			}
			throw e;
		} finally
		{
		}
	}

	public static class C1
	{

	}

	public class Dalong {
		    //@HostAccess.Export
		    public   String  username;
		
		    //@HostAccess.Export
		    public   String  password;
		    
		    //@HostAccess.Export
		    public  String token() {
		        return String.format("%s------%s",this.username,this.password);
		    }
	}

	public void testAA()
	{
		Dalong dalong = new Dalong();
		dalong.password = "dalong";
		dalong.username = "dalong";

		Context context = Context.newBuilder().allowAllAccess(true).allowHostClassLoading(true)
				.allowHostAccess(HostAccess.ALL).allowIO(true).allowNativeAccess(true).build();
		context.getBindings("js").putMember("mydalong", dalong);

		System.out.println(context.getBindings("js").getMemberKeys());
		String myjs = "mydalong.username= \"dalongdemossss\";\n" + "mydalong.password= \"deeeeeeee\";\n" + "\n"
				+ "console.log(\"my token\",mydalong.token())\n" + "\n";
		context.eval("js", myjs);
	}
	
	public void testBB()
	{
		Map<String, Object> ob =new HashMap<>();
		Map<String, Object> ob2 =new HashMap<>();
		ob2.put("url","uu_ob2");
		ob.put("url","uu_ob");
		Map<String, Object> ob3 =new HashMap<>();
		ob3.put("url","uu_ob3");
		ob2.put("ob3",ProxyObject.fromMap(ob3));
		ob.put("id","id_ob");
		ob.put("ob2",ProxyObject.fromMap(ob2));
		
		String jstxt = "print(JSON.stringify(ob.ob2.ob3.url))" ;
		try (Context context = Context.newBuilder().allowAllAccess(true).build())
		{
			context.getBindings("js").putMember("ob", ProxyObject.fromMap(ob));
			context.eval("js",jstxt) ;
		} 
	}
}

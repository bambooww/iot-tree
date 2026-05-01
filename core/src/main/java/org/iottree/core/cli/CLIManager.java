package org.iottree.core.cli;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.iottree.core.*;
import picocli.CommandLine;

public class CLIManager
{
	private static CLIManager cliMgr = null;
	
	private static HashMap<String,CLIManager> name2cli = new HashMap<>() ;

	public static CLIManager getInstance(String prjname)
	{
		CLIManager ret = name2cli.get(prjname) ;
		if (ret != null)
			return ret;

		synchronized (CLIManager.class)
		{
			ret = name2cli.get(prjname) ;
			if (ret != null)
				return ret;

			UAPrj prj = UAManager.getInstance().getPrjByName(prjname) ;
			if(prj==null)
				return null ;
			ret = new CLIManager(prj);
			name2cli.put(prjname, ret) ;
			return ret ;
		}
	}
	
	public static CLIManager getInstance(UAPrj prj)
	{
		return getInstance(prj.getName()) ;
	}
	
	private final UAPrj prj ;

	private final CommandLine cmdLine;

	private CLIManager(UAPrj prj)
	{
		this.prj = prj ;
		this.cmdLine = new CommandLine(new RootCommand(),new CmdFactory(this.prj));
		// this.cmdLine..getParserSpec().withCaseInsensitiveEnumValuesAllowed(true).withOptionsCaseInsensitive(true);
	}

	public static String[] split(String rawInput)
	{
		if (rawInput == null || rawInput.trim().isEmpty())
		{
			return new String[0];
		}

		List<String> args = new ArrayList<>();
		StreamTokenizer st = new StreamTokenizer(new StringReader(rawInput));

		// 核心配置：
		st.resetSyntax();
		st.wordChars(33, 255); // 匹配绝大多数可见字符
		st.whitespaceChars(0, 32); // 忽略空格、换行、制表符
		st.quoteChar('"'); // 设定双引号为界定符
		st.quoteChar('\''); // 设定单引号为界定符（可选）

		try
		{
			while (st.nextToken() != StreamTokenizer.TT_EOF)
			{
				if (st.sval != null)
				{
					args.add(st.sval);
				}
			}
		}
		catch ( IOException e)
		{
			e.printStackTrace();
		}

		return args.toArray(new String[0]);
	}

	public CmdResult executeInternal(String rawInput)
	{
		// 使用我们之前写的 StreamTokenizer 逻辑拆分
		String[] args = split(rawInput);

		StringWriter out = new StringWriter();
		StringWriter err = new StringWriter();

		// 绑定输出流，这样命令执行的结果会被捕获到字符串里返回给 AI/UI
		cmdLine.setOut(new PrintWriter(out));
		cmdLine.setErr(new PrintWriter(err));

		// 执行
		int exitCode = cmdLine.execute(args);

		return new CmdResult(exitCode, out.toString(), err.toString());
	}

	/**
	 * 给 AI 生成 Prompt 的关键方法： 获取整个指令树的详细帮助文档
	 */
	public String getAllCommandDocs()
	{
		return cmdLine.getUsageMessage(CommandLine.Help.Ansi.OFF);
	}

//	public String getAllCommandDocs()
//	{
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		try (PrintStream ps = new PrintStream(baos, true, "UTF-8"))
//		{
//			// 使用特定的帮助设置
//			cmdLine.usage(ps, CommandLine.Help.Ansi.OFF);
//			// 如果有子命令，Picocli 的 usage 默认只显示第一层
//			// 如果想显示所有子命令的详细说明，需要递归或遍历
//			return baos.toString(StandardCharsets.UTF_8);
//		}
//		catch ( Exception e)
//		{
//			return "获取文档失败: " + e.getMessage();
//		}
//	}

}

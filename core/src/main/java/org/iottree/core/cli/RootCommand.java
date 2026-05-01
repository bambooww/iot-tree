package org.iottree.core.cli;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import java.util.concurrent.Callable;

// --- 根命令：作为容器 ---
@Command(name = "main", subcommands = { SensorCommand.class, SlamCommand.class,
		// SystemCommand.class
}, description = "IIOT Commands")
public class RootCommand implements Callable<Integer>
{
	@Override
	public Integer call()
	{
		// 直接输入主命令而不带子命令时显示帮助
		new CommandLine(this).usage(System.out);
		return 0;
	}
}

// --- 子命令 A：传感器相关 ---
@Command(name = "sensor", description = "Sensor cmds")
class SensorCommand implements Callable<Integer>
{
	@CommandLine.Option(names = { "-r", "--rate" }, description = "set sample rate")
	private int rate;

	@Override
	public Integer call()
	{
		System.out.println("current rate: " + rate);
		return 0;
	}
}

// --- 子命令 B：SLAM 算法控制 ---
@Command(name = "slam", description = "SLAM cmds")
class SlamCommand implements Callable<Integer>
{
	@CommandLine.Option(names = "--reset", description = "reset map")
	private boolean reset;

	@Override
	public Integer call()
	{
		if (reset)
			System.out.println("reseting SLAM map");
		return 0;
	}
}

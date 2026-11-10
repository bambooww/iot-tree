package org.iottree.server_api;

//import io.quarkus.runtime.Quarkus;
//import io.quarkus.runtime.QuarkusApplication;
//import io.quarkus.runtime.annotations.QuarkusMain;

public class ServerQuarkus
{

//	private static final Object SHUTDOWN_LOCK = new Object();
//	private static volatile boolean shutdownRequested = false;
//
//	public static void start(String... args)
//	{
//		Quarkus.run(MyApp.class, args);
//	}
//
//	public static void stop()
//	{
//		synchronized (SHUTDOWN_LOCK)
//		{
//			if (!shutdownRequested)
//			{
//				shutdownRequested = true;
//
//				SHUTDOWN_LOCK.notifyAll();
//				//
//				// stop waitForExit ，trigger ShutdownEvent
//				Quarkus.asyncExit(0);
//			}
//		}
//	}
//
//	/**
//	 * 内部类：Quarkus 启动完成后，会执行这里的 run 方法。 我们在这里阻塞主线程，等待 stop() 的信号。
//	 */
//	public static class MyApp implements QuarkusApplication
//	{
//		@Override
//		public int run(String... args) throws Exception
//		{
//			System.out.println("Quarkus 已启动，业务逻辑可在此执行或启动其他线程。");
//
//			// 阻塞当前（Quarkus 主）线程，直到 stop() 被调用
//			synchronized (SHUTDOWN_LOCK)
//			{
//				while (!shutdownRequested)
//				{
//					try
//					{
//						SHUTDOWN_LOCK.wait();
//					}
//					catch ( InterruptedException e)
//					{
//						Thread.currentThread().interrupt();
//						break;
//					}
//				}
//			}
//
//			System.out.println("收到停止请求，Quarkus 即将退出。");
//			return 0;
//		}
//	}
}

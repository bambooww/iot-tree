package org.iottree.ext.av;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

import org.vosk.LibVosk;
import org.vosk.LogLevel;
import org.vosk.Model;
import org.vosk.Recognizer;

public class VoRtRecognizer
{
	// 模型路径 - 替换为你的模型路径
	private static final String MODEL_PATH = "D:/work/work_python/vosk/models/vosk-model-small-cn-0.22";
	private static final int SAMPLE_RATE = 16000; // Vosk 要求的采样率
	private static final int BUFFER_SIZE = 4096; // 音频缓冲区大小

	public static void main(String[] args)
	{
		try
		{
			
			Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
			for (Mixer.Info info : mixerInfos)
			{
				String mix_n = info.getName();
				// String convertedName = new String(mix_n.getBytes("GBK"),
				// "UTF-8");
				System.out.println("Mixer: " + mix_n);
				
				//Mixer m = AudioSystem.getMixer(info) ;
				//m.getLineInfo()
			}

			// 1. 初始化 Vosk 模型
			LibVosk.setLogLevel(LogLevel.INFO); // 设置日志级别
			Model model = new Model(MODEL_PATH);
			Recognizer recognizer = new Recognizer(model, SAMPLE_RATE);

			// recognizer.setWords(true); // 启用单词识别
			// recognizer..addGrammar("custom", new String[]{"open", "close",
			// "lights", "temperature"});

			// 2. 配置音频输入设备
			AudioFormat format = new AudioFormat(SAMPLE_RATE, 16, 1, true, false);
			DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
			Mixer.Info info0 = null;
			Mixer m ;
			//m.getL
			if (!AudioSystem.isLineSupported(info))
			{
				System.err.println("不支持所需的音频格式: " + format);
				return;
			}

			// 3. 打开麦克风
			TargetDataLine microphone = (TargetDataLine) AudioSystem.getLine(info);
			microphone.open(format);
			microphone.start();

			System.out.println("开始监听麦克风... 按 Enter 键停止");
			System.out.println("音频格式: " + format);

			// 4. 创建控制线程的原子布尔值
			AtomicBoolean running = new AtomicBoolean(true);

			// 5. 启动识别线程
			Thread recognitionThread = new Thread(() -> {
				try
				{
					byte[] buffer = new byte[BUFFER_SIZE];
					while (running.get())
					{
						// 读取音频数据
						int bytesRead = microphone.read(buffer, 0, buffer.length);
						if (bytesRead > 0)
						{
							// 处理音频数据
							if (recognizer.acceptWaveForm(buffer, bytesRead))
							{
								// 获取完整识别结果
								String result = recognizer.getResult();
								System.out.println("识别结果: " + result);
							}
							else
							{
								// 获取部分识别结果
								// String partial =
								// recognizer.getPartialResult();
								// if (!partial.contains("\"partial\" : \"\""))
								// {
								// System.out.println("部分结果: " + partial);
								// }
							}
						}
					}
				}
				catch ( Exception e)
				{
					e.printStackTrace();
				}
				finally
				{
					// 清理资源
					microphone.close();
					recognizer.close();
					System.out.println("识别已停止");
				}
			});

			recognitionThread.start();

			// 6. 等待用户输入停止
			System.in.read();
			running.set(false);
			recognitionThread.join();

		}
		catch ( Exception e)
		{
			e.printStackTrace();
		}
	}
}

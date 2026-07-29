package org.iottree.ext.av.video;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

public class VideoSender
{
	static
	{
		// System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		//System.load("C:/dev/opencv/build/java/x64/opencv_java490.dll");
	}

	public static void test()
	{
		String wslIp = "172.20.226.189"; // 替换为 WSL 的 IP
		int port = 9999;

		try (Socket socket = new Socket(wslIp, port);
				OutputStream out = socket.getOutputStream();
				DataOutputStream dos = new DataOutputStream(out))
		{

			VideoCapture capture = new VideoCapture(0);
			Mat frame = new Mat();
			MatOfByte buffer = new MatOfByte();

			while (capture.read(frame))
			{
				// 将帧编码为 JPG 字节流
				Imgcodecs.imencode(".jpg", frame, buffer);
				byte[] bytes = buffer.toArray();

				// 发送数据大小头 (4字节) + 图像数据
				dos.writeInt(bytes.length);
				dos.write(bytes);
				dos.flush();
			}
			capture.release();
		}
		catch ( Exception e)
		{
			e.printStackTrace();
		}
	}
}

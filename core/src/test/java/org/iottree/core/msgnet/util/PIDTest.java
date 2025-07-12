package org.iottree.core.msgnet.util;

import junit.framework.TestCase;
import java.util.Random;

public class PIDTest extends TestCase
{
//	public void test1()
//	{
//		// 1. 创建PID控制器
//		// 参数: Kp=1.0, Ki=0.1, Kd=0.05, 采样时间0.1秒
//		PIDController pid = new PIDController(2.5, 0.1, 0.05, 0.1);
//
//		// 2. 设置输出限幅(例如PWM输出限制在0-255)
//		pid.setOutputLimits(0, 255);
//
//		// 3. 设置目标值
//		pid.setSetpoint(100.0); // 例如目标温度100°C
//
//		// 4. 模拟控制过程
//		double processValue = 20.0; // 初始过程值(当前温度)
//
//		for (int i = 0; i < 100; i++)
//		{
//			// 计算PID输出
//			double output = pid.compute(processValue);
//
//			// 模拟系统响应(这里简化处理)
//			processValue += output * 0.01;
//
//			// 打印结果
//			System.out.printf("Step %d: PV=%.2f, Output=%.2f\n", i, processValue, output);
//
//			// 模拟采样时间
//			try
//			{
//				Thread.sleep((long) (pid.getSampleTime() * 1000));
//			}
//			catch ( InterruptedException e)
//			{
//				e.printStackTrace();
//			}
//		}
//	}
//	
	// 模拟的加热炉物理模型
    static class HeatingFurnace {
        private double temperature; // 当前温度(°C)
        private double heaterPower; // 加热功率(0-1.0)
        private double ambientTemp = 25.0; // 环境温度
        private Random random = new Random();
        
        public HeatingFurnace(double initialTemp) {
            this.temperature = initialTemp;
        }
        
        // 更新炉温模型
        public void update(double dt, double heaterPower) {
            this.heaterPower = heaterPower;
            
            // 简化的一阶模型：加热和自然冷却
            // 加热效率系数和冷却系数是虚构的，实际系统需要实验测定
            double heatingEffect = heaterPower * 1.5;  // 加热效果系数
            double coolingEffect = 0.08;              // 自然冷却系数
            
            // 温度变化微分方程
            double deltaTemp = (heatingEffect - coolingEffect * (temperature - ambientTemp)) * dt;
            temperature += deltaTemp;
            
            // 添加随机噪声模拟传感器噪声
            temperature += (random.nextDouble() - 0.5) * 1.0; // ±0.5°C噪声
        }
        
        public double getTemperature() {
            return temperature;
        }
        
        public double getHeaterPower() {
            return heaterPower;
        }
    }

    /**
     * 控制目标：将加热炉温度稳定在150°C

传感器：温度传感器（假设有±0.5°C的随机噪声）

执行器：可控硅调功器（输出0-100%对应0-10V控制信号）

系统特性：升温较慢，降温更慢（模拟热惯性）
     */
    public void testTempCtrl()
    {
        // 1. 创建PID控制器
        // 参数经过简单整定: Kp=8.0, Ki=0.5, Kd=2.0
        // 采样时间500ms (0.5秒)
        PIDController pid = new PIDController(8.0, 5.9, 1.0,
        		0,200, //0-200温度范围
        		0,10); //0-10v输出
        
        // 2. 设置输出限幅(0-100%功率)
        //pid.setOutputLimits(0.0, 1.0);
        
        // 3. 设置积分限幅(防止积分饱和)
        //pid.setIntegralLimits(-1.0, 1.0);
        
        // 4. 设置目标温度
        double targetTemp = 120.0; // 150°C
        pid.setSetpoint(targetTemp);
        
        // 5. 创建加热炉模型，初始温度25°C
        HeatingFurnace furnace = new HeatingFurnace(25.0);
        
        // 6. 模拟运行2小时(实际14400秒，这里加速模拟)
        int simulationSteps = 120; // 120*0.5s = 60秒(加速演示)
        double dt = 0.5; // 与PID采样时间一致
        
        System.out.println("时间(s)\t实际温度(°C)\t设定温度(°C)\t加热功率(%)\tPID输出");
        System.out.println("-----------------------------------------------------------");
        
        for (int i = 0; i < simulationSteps; i++) {
            double currentTime = i * dt;
            double currentTemp = furnace.getTemperature();
            
            // 在t=30s时加入一个干扰(例如打开炉门)
            if (Math.abs(currentTime - 30.0) < 0.01) {
                furnace.update(dt, 0.0); // 模拟热量突然散失
                currentTemp = furnace.getTemperature();
                System.out.println(">> 干扰事件: 炉门打开导致温度骤降 <<");
            }
            
            // 计算PID输出(控制信号0-1.0)
            double controlSignal = pid.compute(currentTemp,System.currentTimeMillis());
            
            // 更新加热炉状态
            furnace.update(dt, controlSignal);
            
            // 打印状态
            System.out.printf("%.1f\t%.1f\t\t%.1f\t\t%.1f%%\t\t%.4fV\n",
                currentTime,
                currentTemp,
                targetTemp,
                furnace.getHeaterPower(),
                controlSignal);
            
            // 模拟实时控制等待
            try {
                Thread.sleep((long)(dt * 1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

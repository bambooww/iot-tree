package org.iottree.core.msgnet.util;


/**
 * 工业级PID控制器（带输入标准化和输出限幅）
 */
public class PIDController {
    // PID参数
    private double kp;          // 比例系数（无单位）
    private double ki;          // 积分系数（秒^-1）
    private double kd;          // 微分系数（秒）
    
    // 控制器状态
    private double setpoint; // 设定值
    private double integral;    // 积分项累计值
    private double prevError;   // 上一次误差
    private long lastTimeMS;      // 上一次计算时间（毫秒）
    //private double lastOutput; // 上一次输出值
    
    // 输入输出限制
    private final double inputMin;   // 输入最小值（物理量）
    private final double inputMax;   // 输入最大值（物理量）
    private final double outputMin;  // 输出最小值（物理量）
    private final double outputMax;  // 输出最大值（物理量）

    private transient double rtNorSetpoint;
    private transient double rtNorInput ;
    private transient double rtNorOutput ;
    /**
     * 初始化PID控制器
     * @param kp 比例系数
     * @param ki 积分系数
     * @param kd 微分系数
     * @param inputMin 输入物理量最小值（如温度0°C）
     * @param inputMax 输入物理量最大值（如温度150°C）
     * @param outputMin 输出物理量最小值（如4-20mA信号2000）
     * @param outputMax 输出物理量最大值（如4-20mA信号10000）
     */
    public PIDController(double kp, double ki, double kd, 
                                 double inputMin, double inputMax,
                                 double outputMin, double outputMax)
    {
        this.kp = kp;
        this.ki = ki;
        this.kd = kd;
        this.inputMin = inputMin;
        this.inputMax = inputMax;
        this.outputMin = outputMin;
        this.outputMax = outputMax;
        
        this.prevError = 0.0;
        this.integral = 0.0;
        this.lastTimeMS = System.currentTimeMillis(); //System.nanoTime();
        
        this.setpoint = rtNorSetpoint = 0.0;
        this.rtNorInput = 0.0;
        this.rtNorOutput = 0.0 ;
    }

    /**
     * 计算PID输出（带标准化和限幅）
     * @param setpoint 设定值（物理量，如50°C）
     * @param measuredValue 测量值（物理量，如当前温度）
     * @param measure_dt 采样时间，必须保证每次调用不断增加
     * @return 控制输出（物理量，如4-20mA信号）
     */
    public double compute(double measured_val,long measure_dt)
    {
    	double inputv = normalizeInput(measured_val);
    	double outv = computeNormal(rtNorInput=inputv, measure_dt);
    	return scalingOutput(rtNorOutput=outv) ;
    }
    
    
    /**
     * 归一化计算 ，输入0-1 输出0-1
     * 采样时间和函数被调用的时间相关。建议外界控制调用时间间隔来自动形成采样时间
     * @param measured_val_nor
     * @param measure_dt 采样时间，必须保证每次调用不断增加
     * @return
     */
    private double computeNormal(double measured_val_nor,long measure_dt)
    {
        // 1. 计算时间间隔（秒）
//        long now = System.System.nanoTime();
//        double dt = (now - lastTime) / 1e9;
//        lastTimeMS = now;
    	
    	//1. 计算时间间隔（秒）
    	 double dt = (measure_dt - lastTimeMS) / 1000.0;
         lastTimeMS = measure_dt;
        
        // 2. 输入标准化（将物理量转换为0-1.0范围）
        //double normalizedSetpoint = normalizeInput(this.setpoint);
        
        
        // 3. 计算误差
        double error = this.rtNorSetpoint - measured_val_nor;
        
        // 4. 计算比例项
        double proportional = kp * error;
        
        // 5. 计算积分项（带抗饱和逻辑）
        double newIntegral = integral + ki * error * dt;
        double integralTerm = newIntegral;
        
        // 6. 计算微分项
        double derivative = (error - prevError) / dt;
        double derivativeTerm = kd * derivative;
        
        // 7. 计算原始输出
        double output = proportional + integralTerm + derivativeTerm;
        
        // 8. 输出限幅（0.0-1.0）
        output = clamp(output, 0.0,1.0);//outputMin, outputMax);
        
        // 9. 抗积分饱和：若输出饱和，则冻结积分
        if (output != proportional + integralTerm + derivativeTerm) {
            // 输出被限幅，不更新积分项（避免windup）
        } else {
            integral = newIntegral; // 正常更新积分
        }
        
        // 10. 保存状态
        prevError = error;
        
        return output; // 返回0.0-1.0（可乘以100转为百分比）
    }

    /**
     * 将物理量标准化到0-1.0范围
     */
    private double normalizeInput(double value)
    {
        return (value - inputMin) / (inputMax - inputMin);
    }

    /**
     * 将0-1.0范围内的标准化值计算物理量
     * @param value
     * @return
     */
    private double scalingOutput(double value)
    {
    	return  (outputMax - outputMin)*value+outputMin ;
    }
    /**
     * 限制值在[min, max]范围内
     */
    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    // ------------ 参数配置方法 ------------
    public void setKp(double kp) { this.kp = kp; }
    public void setKi(double ki) { this.ki = ki; }
    public void setKd(double kd) { this.kd = kd; }
    
    public void setSetpoint(double setpoint)
    {
    	if(setpoint<this.inputMin || setpoint>this.inputMax)
    		throw new IllegalArgumentException("setpoint must in ["+this.inputMin+","+this.inputMax+"]");
    	
    	this.setpoint = setpoint;
    	this.rtNorSetpoint=normalizeInput(this.setpoint);
    }
    
    public void reset() { 
        integral = 0; 
        prevError = 0; 
        lastTimeMS = System.currentTimeMillis();//System.nanoTime();
        
        this.setpoint = rtNorSetpoint = 0.0;
        this.rtNorInput = 0.0;
        this.rtNorOutput = 0.0 ;
    }


    public double getKp() { return kp; }
    public double getKi() { return ki; }
    public double getKd() { return kd; }
    public double getIntegral() { return integral; }
    public double getSetpoint() { return setpoint; }
    
    public double getNorSetpoint() { return rtNorSetpoint; }
    public double getNorInput() { return rtNorInput; }
    public double getNorOutput() { return rtNorOutput; }
}
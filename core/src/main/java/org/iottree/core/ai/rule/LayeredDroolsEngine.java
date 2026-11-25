package org.iottree.core.ai.rule;

import org.kie.api.KieServices;
import org.kie.api.builder.*;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.Agenda;
import java.util.*;

public class LayeredDroolsEngine {

    private KieContainer kieContainer;
    
    private String lvl1 = """
package rules.level1;
import org.iottree.core.ai.rule.DeviceNode;

rule "Gas Tank Base Alarm"
    agenda-group "device_level" // 绑定到设备级议程组
    when
        $node : DeviceNode(type == "gas_tank", pressure > 0.8)
    then
        System.out.println("[L1-设备层] " + $node.getId() + " 基础超压告警");
end
    		""";
    
    private String lvl2 = """
package rules.level2;
import org.iottree.core.ai.rule.DeviceNode;
import org.iottree.core.ai.rule.TopologyEdge;

rule "Pipeline Cascade Risk"
    agenda-group "facility_level" // 绑定到设施级议程组
    when
        $valve : DeviceNode(type == "valve", open == false)
        $edge : TopologyEdge(source == $valve.id, type == "pipeline")
        $downstream : DeviceNode(id == $edge.target, type == "actuator")
    then
        System.out.println("[L2-设施层] 阀门 " + $valve.getId() + " 关闭，下游执行器 " + $downstream.getId() + " 存在失气风险！");
end
    		""";

    public LayeredDroolsEngine() {
        KieServices kieServices = KieServices.Factory.get();
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();

        // 加载所有层级的规则文件
        kieFileSystem.write("src/main/resources/rules/level1_device.drl",
                kieServices.getResources().newClassPathResource("rules/level1_device.drl"));
        kieFileSystem.write("src/main/resources/rules/level2_facility.drl",
                kieServices.getResources().newClassPathResource("rules/level2_facility.drl"));

        KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
        kieBuilder.buildAll();
        this.kieContainer = kieServices.newKieContainer(kieServices.getRepository().getDefaultReleaseId());
    }
    
//    public void loadOrUpdateRule(String ruleId, String drlContent) {
//        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
//        
//        //String dirb = Config.getDataDirBase()+"/test_rules/" ;
////        File d = new File(dirb) ;
////        if(!d.exists())
////        	d.mkdirs() ;
//        // 1. 将动态生成的 DRL 内容写入虚拟文件系统
//        // 路径可以自定义，通常按模块或设备类型划分
//        String path = "src/main/resources/rules/" + ruleId + ".drl";
//        kieFileSystem.write(path, drlContent);
//
//        // 2. 编译规则
//        KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem).buildAll();
//
//        // 3. 检查编译错误（非常重要，防止非法规则导致引擎崩溃）
//        if (kieBuilder.getResults().hasMessages(Message.Level.ERROR)) {
//            System.err.println("规则编译失败: " + kieBuilder.getResults().getMessages());
//            return;
//        }
//
//        // 4. 重新构建 KieContainer，实现热更新
//        // 注意：这里会生成一个新的 KieContainer，旧的会被垃圾回收
//        kieContainer = kieServices.newKieContainer(kieServices.getRepository().getDefaultReleaseId());
//        
//        int ruleCount = kieContainer.getKieBase().getKiePackages().stream()
//                .mapToInt(p -> p.getRules().size())
//                .sum();
//
//        System.out.println("规则 [" + ruleId + "] 加载成功，当前规则数: " + ruleCount);
//    }

    // 根据拓扑图动态执行规则
    public void executeTopologyRules(List<DeviceNode> nodes, List<TopologyEdge> edges, Set<String> activeLevels) {
        KieSession session = kieContainer.newKieSession();
        
        try {
            // 1. 插入拓扑事实
            nodes.forEach(session::insert);
            edges.forEach(session::insert);

            // 2. 根据拓扑层级激活对应的议程组 (Agenda Group)
            // 如果拓扑中包含设施级节点，则激活 facility_level
            Agenda agenda = session.getAgenda();
            for (String level : activeLevels) {
            	session.getAgenda().getAgendaGroup(level).setFocus(); // 激活对应层级的规则组
            }

            // 3. 触发规则
            int fired = session.fireAllRules();
            System.out.println("--- 本次拓扑推理共触发 " + fired + " 条规则 ---");

        } finally {
            session.dispose();
        }
    }

    public static void main(String[] args) {
        LayeredDroolsEngine engine = new LayeredDroolsEngine();

        // 模拟拓扑数据
        DeviceNode tank = new DeviceNode("tank_01", "gas_tank", 0.95);
        DeviceNode valve = new DeviceNode("valve_01", "valve", 0.4);
        valve.setOpen(false); // 阀门关闭
        DeviceNode actuator = new DeviceNode("act_01", "actuator", 0.0);

        TopologyEdge edge = new TopologyEdge("pipe_01", "valve_01", "act_01", "pipeline");

        // 激活设备层和设施层规则
        Set<String> levels = new HashSet<>(Arrays.asList("device_level", "facility_level"));
        
        engine.executeTopologyRules(
            Arrays.asList(tank, valve, actuator), 
            Arrays.asList(edge), 
            levels
        );
    }
}

class DeviceNode {
    private String id;
    private String type; // 例如: "gas_tank", "valve"
    private double pressure;
    private boolean alarm;
    private boolean valveOpen = false;

    public DeviceNode(String id, String type, double pressure) {
        this.id = id;
        this.type = type;
        this.pressure = pressure;
    }

    // 必须提供 Getter 和 Setter，Drools 依赖它们进行模式匹配
    public String getId() { return id; }
    public String getType() { return type; }
    public double getPressure() { return pressure; }
    public void setOpen(boolean b) {valveOpen = b;}
    public boolean getOpen() {return this.valveOpen;}
    public boolean isAlarm() { return alarm; }
    public void setAlarm(boolean alarm) { this.alarm = alarm; }
}

class TopologyEdge {
    private String id;
    private String type; // 例如: "gas_tank", "valve"
    private String source;
    private String target;

    public TopologyEdge(String id,String source,String target, String type)
    {
        this.id = id;
        this.type = type;
        this.source = source;
        this.target = target;
    }

    // 必须提供 Getter 和 Setter，Drools 依赖它们进行模式匹配
    public String getId() { return id; }
    public String getType() { return type; }
    public String getSource() { return source; }
    public String getTarget() { return target; }
    
}
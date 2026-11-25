package org.iottree.core.ai.rule;

import org.iottree.core.Config;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class DynamicRuleLoader {

    private static KieServices kieServices = KieServices.Factory.get();
    // 维护当前的规则容器，支持动态替换
    private static KieContainer kieContainer;

    /**
     * 动态加载或更新规则（可在运行时随时调用）
     * @param ruleId 规则唯一标识
     * @param drlContent 动态生成的 DRL 字符串
     */
    public static void loadOrUpdateRule(String ruleId, String drlContent) {
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
        
        //String dirb = Config.getDataDirBase()+"/test_rules/" ;
//        File d = new File(dirb) ;
//        if(!d.exists())
//        	d.mkdirs() ;
        // 1. 将动态生成的 DRL 内容写入虚拟文件系统
        // 路径可以自定义，通常按模块或设备类型划分
        String path = "src/main/resources/rules/" + ruleId + ".drl";
        kieFileSystem.write(path, drlContent);

        // 2. 编译规则
        KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem).buildAll();

        // 3. 检查编译错误（非常重要，防止非法规则导致引擎崩溃）
        if (kieBuilder.getResults().hasMessages(Message.Level.ERROR)) {
            System.err.println("规则编译失败: " + kieBuilder.getResults().getMessages());
            return;
        }

        // 4. 重新构建 KieContainer，实现热更新
        // 注意：这里会生成一个新的 KieContainer，旧的会被垃圾回收
        kieContainer = kieServices.newKieContainer(kieServices.getRepository().getDefaultReleaseId());
        
        int ruleCount = kieContainer.getKieBase().getKiePackages().stream()
                .mapToInt(p -> p.getRules().size())
                .sum();

        System.out.println("规则 [" + ruleId + "] 加载成功，当前规则数: " + ruleCount);
    }

    /**
     * 执行规则（使用最新的 KieContainer）
     */
    public static void executeRules(Map<String, Object> deviceData) {
        if (kieContainer == null) {
            System.err.println("规则容器未初始化");
            return;
        }
        KieSession session = kieContainer.newKieSession();
        try {
            session.insert(deviceData);
            session.fireAllRules();
        } finally {
            session.dispose();
        }
    }

    public static void main(String[] args) {
        // 场景：系统运行中，突然接入一个新型号的气罐，并下发了新的告警规则
        String newDeviceRule = """
            package rules.dynamic;
            import java.util.Map;

            rule "New Gas Tank High Pressure"
            when
                $d : Map( this["type"] == "new_gas_tank", this["pressure"] > 1.2 )
            then
                System.out.println("[动态规则] 新型气罐压力超标: " + $d.get("pressure"));
            end
            """;

        // 1. 运行时动态加载规则
        loadOrUpdateRule("new_tank_rule", newDeviceRule);

        // 2. 模拟新设备上报数据（使用 Map，无需新建 Java 类）
        Map<String, Object> newTankData = new HashMap<>();
        newTankData.put("type", "new_gas_tank");
        newTankData.put("pressure", 1.5);

        // 3. 执行规则，立即生效
        executeRules(newTankData);
    }
}
package org.iottree.core.cli;

import org.iottree.core.*;
import picocli.CommandLine;

public class CmdFactory implements CommandLine.IFactory
{
    private final UAPrj prj;

    public CmdFactory(UAPrj prj) { this.prj = prj; }

    @Override
    public <K> K create(Class<K> cls) throws Exception {
        // 如果 Picocli 想要创建 SensorCommand，我们就把 engine 塞进去
        if (cls == CmdTag.class) {
            return (K) new CmdTag(prj);
        }
        // 其他命令走默认创建逻辑
        return CommandLine.defaultFactory().create(cls);
    }
}

package net.codjo.dataprocess.server.plugin;
import net.codjo.mad.server.plugin.MadServerPlugin;
import net.codjo.plugin.server.ServerCore;
import net.codjo.workflow.server.plugin.WorkflowServerPlugin;
/**
 *
 */
public class DataProcessServerPluginMock extends DataProcessServerPlugin {
    public DataProcessServerPluginMock() {
        super(new MadServerPlugin("", DataProcessServerPlugin.class), new WorkflowServerPlugin(),
              new ServerCore());
    }
}

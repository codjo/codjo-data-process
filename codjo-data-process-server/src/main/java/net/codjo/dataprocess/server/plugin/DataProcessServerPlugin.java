/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.server.plugin;
import net.codjo.agent.AgentContainer;
import net.codjo.agent.ContainerConfiguration;
import net.codjo.agent.DFService;
import net.codjo.dataprocess.common.message.DataProcessJobRequest;
import net.codjo.dataprocess.server.audit.DataProcessStringifier;
import net.codjo.dataprocess.server.handler.DataProcessHandlerMapBuilder;
import net.codjo.dataprocess.server.kernel.TreatmentLauncher;
import net.codjo.dataprocess.server.plugin.DataProcessJobAgent.DefaultDataProcessLauncherFactory;
import net.codjo.mad.server.plugin.MadServerPlugin;
import net.codjo.mad.server.plugin.MadServerPlugin.MadServerPluginConfiguration;
import net.codjo.plugin.common.ApplicationCore;
import net.codjo.plugin.server.ServerPlugin;
import net.codjo.reflect.collect.ClassCollector;
import net.codjo.reflect.collect.PackageFilter;
import net.codjo.sql.server.JdbcServiceUtil;
import net.codjo.workflow.server.api.JobAgent;
import net.codjo.workflow.server.api.JobAgent.MODE;
import net.codjo.workflow.server.api.ResourcesManagerAgent;
import net.codjo.workflow.server.api.ResourcesManagerAgent.AgentFactory;
import net.codjo.workflow.server.plugin.WorkflowServerPlugin;
import java.io.IOException;
import org.picocontainer.MutablePicoContainer;
/**
 *
 */
public class DataProcessServerPlugin implements ServerPlugin {
    public static final String DATA_PROCESS_REQUEST_TYPE = DataProcessJobRequest.DATA_PROCESS_REQUEST_TYPE;
    private static final String PACKAGE_HANDLER_COMMAND = "net.codjo.dataprocess.server.handlercommand";
    private static final String PACKAGE_HANDLER_SELECT = "net.codjo.dataprocess.server.handlerselect";
    private DataProcessServerConfiguration configuration = new DataProcessServerConfiguration();
    private final MadServerPlugin madServerPlugin;
    private ApplicationCore applicationCore;
    private static final DataProcessPicoContainer PICO_CONTAINER = new DataProcessPicoContainer();


    public DataProcessServerPlugin(MadServerPlugin madServerPlugin,
                                   WorkflowServerPlugin workflowServerPlugin,
                                   ApplicationCore applicationCore) {
        this.madServerPlugin = madServerPlugin;
        this.applicationCore = applicationCore;
        PICO_CONTAINER.setPicoContainer(applicationCore.createChildPicoContainer());
        new DataProcessStringifier().install(workflowServerPlugin);
    }


    public void initContainer(ContainerConfiguration containerConfiguration) throws Exception {
        initHandlerCommand();
        initHandlerSelect();

        buildHandlerMapBuilder();
    }


    private void buildHandlerMapBuilder() {
        MadServerPluginConfiguration madServerConfiguration = madServerPlugin.getConfiguration();
        DataProcessHandlerMapBuilder mapBuilder =
              new DataProcessHandlerMapBuilder(applicationCore,
                                               madServerConfiguration.getHandlerMapBuilder(),
                                               getConfiguration().getClassCollectors());
        madServerConfiguration.setHandlerMapBuilder(mapBuilder);
        madServerConfiguration.addGlobalComponent(mapBuilder);
    }


    private void initHandlerCommand() throws Exception {
        ClassCollector collector = new ClassCollector(getClass());
        collector.addClassFilter(new PackageFilter(PACKAGE_HANDLER_COMMAND, true));
        try {
            Class[] handlerClassList = collector.collect();
            for (Class handlerCommand : handlerClassList) {
                madServerPlugin.getConfiguration().addHandlerCommand(handlerCommand);
            }
        }
        catch (IOException ex) {
            throw new Exception("Echec de la récupération de handler command ("
                                + PACKAGE_HANDLER_COMMAND + ") : " + ex.getLocalizedMessage(), ex);
        }
    }


    private void initHandlerSelect() throws Exception {
        ClassCollector collector = new ClassCollector(getClass());
        collector.addClassFilter(new PackageFilter(PACKAGE_HANDLER_SELECT, true));
        try {
            Class[] handlerClassList = collector.collect();
            for (Class handlerCommand : handlerClassList) {
                madServerPlugin.getConfiguration().addHandlerSql(handlerCommand);
            }
        }
        catch (IOException ex) {
            throw new Exception("Echec de la récupération de handler select ("
                                + PACKAGE_HANDLER_SELECT + ") : " + ex.getLocalizedMessage(), ex);
        }
    }


    public void start(AgentContainer agentContainer) throws Exception {
        agentContainer.acceptNewAgent("dataprocess-scheduler", new AfterControlScheduleAgent()).start();

        agentContainer.acceptNewAgent("data-process-drh-agent",
                                      new ResourcesManagerAgent(new DataProcessAgentFactory(),
                                                                DFService.createAgentDescription(
                                                                      DATA_PROCESS_REQUEST_TYPE))).start();

        agentContainer.acceptNewAgent("data-process-job-agent", createDataProcessJobAgent(MODE.NOT_DELEGATE))
              .start();
    }


    private static DataProcessJobAgent createDataProcessJobAgent(MODE mode) {
        return new DataProcessJobAgent(new JdbcServiceUtil(),
                                       new DefaultDataProcessLauncherFactory(),
                                       new TreatmentLauncher(),
                                       mode);
    }


    public void stop() throws Exception {
    }


    public DataProcessServerConfiguration getConfiguration() {
        return configuration;
    }


    public static MutablePicoContainer getPicoContainer() {
        return PICO_CONTAINER.getPicoContainer();
    }


    private static class DataProcessPicoContainer {
        private MutablePicoContainer picoContainer;


        public MutablePicoContainer getPicoContainer() {
            return picoContainer;
        }


        public void setPicoContainer(MutablePicoContainer picoContainer) {
            this.picoContainer = picoContainer;
        }
    }

    private static class DataProcessAgentFactory implements AgentFactory {
        public JobAgent create() throws Exception {
            return createDataProcessJobAgent(MODE.DELEGATE);
        }
    }
}

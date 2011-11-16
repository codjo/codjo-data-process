/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.server.plugin;
import net.codjo.agent.Aid;
import net.codjo.agent.ContainerConfigurationMock;
import net.codjo.agent.test.AgentContainerFixture;
import net.codjo.mad.server.plugin.MadServerPlugin;
import net.codjo.plugin.server.ServerCore;
import net.codjo.test.common.LogString;
import net.codjo.workflow.server.plugin.WorkflowServerPlugin;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static net.codjo.test.common.matcher.JUnitMatchers.*;
/**
 *
 */
public class DataProcessServerPluginTest {
    private DataProcessServerPlugin dataProcessServerPlugin;
    private LogString log = new LogString();
    private AgentContainerFixture agentContainerFixture = new AgentContainerFixture();


    @Test
    public void initAndStop() throws Exception {
        dataProcessServerPlugin.initContainer(new ContainerConfigurationMock(log));
        dataProcessServerPlugin.stop();
        log.assertContent("");
    }


    @Test
    public void start() throws Exception {
        dataProcessServerPlugin.start(agentContainerFixture.getContainer());
        agentContainerFixture.assertNumberOfAgentWithService(2,
                                                             DataProcessServerPlugin.DATA_PROCESS_REQUEST_TYPE);
        Aid[] aids
              = agentContainerFixture.searchAgentWithService(DataProcessServerPlugin.DATA_PROCESS_REQUEST_TYPE);
        assertThat(aids[0].getLocalName().substring(0, 12), equalTo("data-process"));
    }


    @Test
    public void getConfiguration() {
        assertThat(dataProcessServerPlugin.getConfiguration(), notNullValue());
    }


    @Before
    public void before() {
        agentContainerFixture.doSetUp();
        MadServerPlugin madServerPlugin = new MadServerPlugin("MadServerPluginTest_Castor.xml",
                                                              DataProcessServerPluginTest.class);
        dataProcessServerPlugin = new DataProcessServerPlugin(madServerPlugin,
                                                              new WorkflowServerPlugin(),
                                                              new ServerCore());
    }


    @After
    public void after() throws Exception {
        agentContainerFixture.doTearDown();
    }
}

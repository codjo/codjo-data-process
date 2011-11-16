package net.codjo.dataprocess.server.handlercommand;
import net.codjo.dataprocess.server.plugin.DataProcessServerPluginMock;
import net.codjo.mad.server.handler.HandlerCommand;
import net.codjo.mad.server.handler.HandlerCommandTestCase;
/**
 *
 */
public class CmdMapServerCommandTest extends HandlerCommandTestCase {
    @Override
    protected HandlerCommand createHandlerCommand() {
        return new CmdMapServerCommand(new DataProcessServerPluginMock());
    }


    @Override
    protected void initTokio() throws Exception {
    }


    @Override
    protected String getHandlerId() {
        return "cmdMapServer";
    }
}

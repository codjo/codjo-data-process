/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.server.handlercommand.dependency;
import net.codjo.mad.server.handler.HandlerCommand;
import net.codjo.mad.server.handler.HandlerCommandTestCase;
/**
 * Classe de test de {@link net.codjo.dataprocess.server.handlercommand.dependency.UpdateImplicationCommand}
 */
public class UpdateImplicationCommandTest extends HandlerCommandTestCase {
    @Override
    protected HandlerCommand createHandlerCommand() {
        return new UpdateImplicationCommand();
    }


    @Override
    protected void initTokio() throws Exception {
    }


    @Override
    protected String getHandlerId() {
        return "updateImplication";
    }
}

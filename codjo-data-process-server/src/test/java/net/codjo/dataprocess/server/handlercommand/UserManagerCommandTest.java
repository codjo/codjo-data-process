package net.codjo.dataprocess.server.handlercommand;
import net.codjo.mad.server.handler.HandlerCommand;
import net.codjo.mad.server.handler.HandlerCommandTestCase;
/**
 *
 */
public class UserManagerCommandTest extends HandlerCommandTestCase {
    @Override
    protected HandlerCommand createHandlerCommand() {
        return new UserManagerCommand();
    }


    @Override
    protected void initTokio() throws Exception {
    }


    @Override
    protected String getHandlerId() {
        return "userManager";
    }
}

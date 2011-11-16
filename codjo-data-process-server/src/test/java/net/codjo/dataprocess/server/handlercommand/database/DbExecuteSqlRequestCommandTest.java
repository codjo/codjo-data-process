package net.codjo.dataprocess.server.handlercommand.database;
import net.codjo.mad.server.handler.HandlerCommand;
import net.codjo.mad.server.handler.HandlerCommandTestCase;
/**
 *
 */
public class DbExecuteSqlRequestCommandTest extends HandlerCommandTestCase {
    @Override
    protected HandlerCommand createHandlerCommand() {
        return new DbExecuteSqlRequestCommand();
    }


    @Override
    protected void initTokio() throws Exception {
    }


    @Override
    protected String getHandlerId() {
        return "dbExecuteSqlRequest";
    }
}
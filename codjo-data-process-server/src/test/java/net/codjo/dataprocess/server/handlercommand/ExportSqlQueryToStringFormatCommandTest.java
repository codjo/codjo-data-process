/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.server.handlercommand;
import net.codjo.mad.server.handler.HandlerCommand;
import net.codjo.mad.server.handler.HandlerCommandTestCase;
/**
 * Classe de test de {@link  ExportSqlQueryToStringFormatCommand}
 */
public class ExportSqlQueryToStringFormatCommandTest extends HandlerCommandTestCase {
    @Override
    protected HandlerCommand createHandlerCommand() {
        return new ExportSqlQueryToStringFormatCommand();
    }


    @Override
    protected void initTokio() throws Exception {
    }


    @Override
    protected String getHandlerId() {
        return "exportSqlQueryToStringFormat";
    }
}

/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.server.handlercommand;
import net.codjo.dataprocess.common.exception.TreatmentException;
import net.codjo.dataprocess.server.handlerhelper.ConfigHandlerHelper;
import net.codjo.mad.server.handler.HandlerCommand;
import net.codjo.mad.server.handler.HandlerException;
import java.sql.Connection;
import java.sql.SQLException;
/**
 *
 */
public class GetConfigPropertyCommand extends HandlerCommand {
    @Override
    public CommandResult executeQuery(CommandQuery query) throws HandlerException, SQLException {
        Connection con = getContext().getTxConnection();
        String key = query.getArgumentString("key");
        try {
            String result = ConfigHandlerHelper.getConfigProperty(con, key);
            return createResult(result);
        }
        catch (TreatmentException ex) {
            throw new HandlerException(ex.getLocalizedMessage() + " (key = " + key + ") ", ex);
        }
        finally {
            con.close();
        }
    }
}
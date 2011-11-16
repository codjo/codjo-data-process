/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.server.handlercommand.repository;
import net.codjo.dataprocess.common.exception.TreatmentException;
import net.codjo.dataprocess.server.handlerhelper.ExecutionListParamImportHelper;
import net.codjo.mad.server.handler.HandlerCommand;
import net.codjo.mad.server.handler.HandlerException;
import java.sql.Connection;
import java.sql.SQLException;
/**
 *
 */
public class ExecutionListParamImportCommand extends HandlerCommand {
    @Override
    public CommandResult executeQuery(CommandQuery query) throws HandlerException, SQLException {
        Connection con = getContext().getTxConnection();
        String content = query.getArgumentString("content");
        boolean createMissingFamily = query.getArgumentBoolean("createMissingFamily");
        try {
            String report = new ExecutionListParamImportHelper().importExecutionListParam(con,
                                                                                          content,
                                                                                          createMissingFamily);
            return createResult(report);
        }
        catch (TreatmentException ex) {
            throw new HandlerException(
                  ex.getLocalizedMessage() + " (createMissingFamily = " + createMissingFamily + ") ", ex);
        }
        finally {
            con.close();
        }
    }
}
/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.server.handlercommand;
import net.codjo.dataprocess.server.handlerhelper.UtilHandlerHelper;
import net.codjo.mad.server.handler.HandlerCommand;
import net.codjo.mad.server.handler.HandlerException;
import java.sql.Connection;
import java.sql.SQLException;
/**
 *
 */
public class ExportSqlQueryToStringFormatCommand extends HandlerCommand {
    @Override
    public CommandResult executeQuery(CommandQuery query) throws HandlerException, SQLException {
        Connection con = getContext().getTxConnection();

        try {
            String sql = query.getArgumentString("sql");
            String separator = query.getArgumentString("separator");
            String quote = query.getArgumentString("quote");
            Boolean column = query.getArgumentBoolean("column");
            String result = UtilHandlerHelper.exportSqlQueryToStringFormat(con, sql, separator,
                                                                           quote, column);
            return createResult(result);
        }
        finally {
            con.close();
        }
    }
}

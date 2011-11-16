/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.server.handlercommand.database;
import net.codjo.dataprocess.server.util.DatabaseTools;
import net.codjo.mad.server.handler.HandlerCommand;
import net.codjo.mad.server.handler.HandlerException;
import java.sql.SQLException;
/**
 *
 */
public class DbExecuteSqlRequestCommand extends HandlerCommand {

    @Override
    public CommandResult executeQuery(CommandQuery query) throws HandlerException, SQLException {
        String sql = query.getArgumentString("sql").trim();
        int page = query.getArgumentInteger("currentPage");
        int pageSize = query.getArgumentInteger("pageSize");
        try {
            return createResult(DatabaseTools.executeQuery(getContext().getUser(),
                                                           getContext().getConnection(), sql, page,
                                                           pageSize));
        }
        catch (Exception ex) {
            throw new HandlerException(
                  ex.getLocalizedMessage() + "(sql = " + sql + ", page = " + page + ", pageSize = "
                  + pageSize + ") ", ex);
        }
    }
}

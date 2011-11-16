/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.server.handlercommand.dependency;
import net.codjo.dataprocess.server.handlerhelper.DependencyHandlerHelper;
import net.codjo.mad.server.handler.HandlerCommand;
import net.codjo.mad.server.handler.HandlerException;
import java.sql.Connection;
import java.sql.SQLException;
/**
 *
 */
public class IsExecutableCommand extends HandlerCommand {
    @Override
    public CommandResult executeQuery(CommandQuery query) throws HandlerException, SQLException {
        Connection con = getContext().getTxConnection();

        try {
            int repositoryId = query.getArgumentInteger("repositoryId");
            String executionListDep = query.getArgumentString("executionListDep");
            String result = DependencyHandlerHelper.isExecutable(con, repositoryId, executionListDep);
            return createResult(result);
        }
        finally {
            con.close();
        }
    }
}

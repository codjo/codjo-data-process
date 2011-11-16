/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.server.kernel;
import net.codjo.dataprocess.common.Log;
import net.codjo.dataprocess.common.model.ArgList;
import net.codjo.dataprocess.common.model.ArgModel;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.codjo.dataprocess.common.DataProcessConstants.COLUMN;
import static net.codjo.dataprocess.common.DataProcessConstants.QUOTE;
import static net.codjo.dataprocess.common.DataProcessConstants.SEPARATOR;
/**
 *
 */
abstract class AbstractSqlTreatment extends AbstractTreatment {
    @Override
    protected void buildArgument() {
        Log.debug(getClass(), " *********** BUILD ARGUMENTS SQL TREATMENT");
        Map<String, Argument> localArgs = new HashMap<String, Argument>();

        ArgList arglist = getTreatmentModel().getArguments();
        if (arglist.getArgs() != null) {
            for (ArgModel argModel : arglist.getArgs()) {
                String key;
                String name = argModel.getName().trim();
                if (SEPARATOR.equals(name) || QUOTE.equals(name) || COLUMN.equals(name)) {
                    key = name;
                }
                else {
                    key = String.valueOf(argModel.getPosition());
                }
                localArgs.put(key, new Argument(argModel));
            }
        }
        else {
            localArgs = null;
        }
        setArgs(localArgs);
    }


    String buildStoredProcQuery() {
        return "{call " + getTreatmentModel().getTarget() + ' ' + prepareArgumentsForProcStock() + '}';
    }


    protected void configureStatement(PreparedStatement prep) throws SQLException {
        Map<String, Argument> args = getArgs();

        if (args != null && !args.isEmpty()) {
            for (Map.Entry<String, Argument> entry : args.entrySet()) {
                String key = entry.getKey();
                if (!SEPARATOR.equalsIgnoreCase(key) && !QUOTE.equalsIgnoreCase(key)
                    && !COLUMN.equalsIgnoreCase(key)) {
                    Argument arg = entry.getValue();
                    prep.setObject(Integer.parseInt(key), arg.getValue(), arg.getType());
                    if (Log.isDebugEnabled()) {
                        Log.debug(getClass(),
                                  "*********** Paramètre SQL --> Position :" + Integer.parseInt(key)
                                  + " - Valeur :" + arg.getValue() + " - Type sql:" + arg.getType());
                    }
                }
            }
        }
    }


    String prepareArgumentsForProcStock() {
        Map<String, Argument> args = getArgs();

        if (args != null && !args.isEmpty()) {
            List<Argument> argumentList = new ArrayList<Argument>();
            argumentList.addAll(args.values());
            Collections.sort(argumentList, Argument.getPositionComparator());

            StringBuilder result = new StringBuilder();
            for (Argument arg : argumentList) {
                result.append(',').append(arg.getName()).append(" = ? ");
            }
            return result.substring(1);
        }
        return null;
    }
}

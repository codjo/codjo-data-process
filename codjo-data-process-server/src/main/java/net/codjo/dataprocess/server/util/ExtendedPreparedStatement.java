package net.codjo.dataprocess.server.util;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * Un parametre est notifié par ${name} dans la requete. L'espace est obligatoire aprés }
 *
 * Evolution : pouvoir mettre des expression java et un context
 *
 * ${System.currentTimeMillis()} ${age<18}
 */
public class ExtendedPreparedStatement {
    private static final String SELECT_PARAM_REGXEXP = "(\\$\\{[\\w|\\.]+\\})";
    private Map<String, Object> parameters = new HashMap<String, Object>();
    private Map<String, List<Integer>> parametersPositions = new HashMap<String, List<Integer>>();
    private String originalSql;
    private String compiledSql;


    public ExtendedPreparedStatement(String sql) {
        originalSql = sql;
        compiledSql = compileSql(originalSql, parametersPositions);
    }


    public void setParameterValues(Object... args) throws IllegalArgumentException {
        if (args.length == 0 || args.length % 2 != 0) {
            throw new IllegalArgumentException(
                  "args parameter must be like: setParameterValues(\"key1\",value1,\"key2\",value2 ... and so on");
        }
        for (int index = 0; index < args.length / 2; index++) {
            String key = (String)args[index * 2];
            Object value = args[index * 2 + 1];
            setParameterValue(key, value);
        }
    }


    public void setParameterValue(String name, Object value) throws IllegalArgumentException {
        if (parametersPositions.get(name) == null) {
            throw new IllegalArgumentException("Unknown parameter name : '" + name + "'");
        }
        parameters.put(name, value);
    }


    public PreparedStatement createAndSetPreparedStatement(Connection connection) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(compiledSql);
        for (Entry<String, List<Integer>> entry : parametersPositions.entrySet()) {
            List<Integer> positions = entry.getValue();
            Object value = parameters.get(entry.getKey());
            for (int currentPosition : positions) {
                preparedStatement.setObject(currentPosition, value);
            }
        }
        return preparedStatement;
    }


    protected ExtendedPreparedStatement() {
    }


    protected String compileSql(String sql, Map<String, List<Integer>> positions) {
        StringBuilder resultSql = new StringBuilder();
        int startPosition = 0;
        Pattern pattern = Pattern.compile(SELECT_PARAM_REGXEXP);
        Matcher matcher = pattern.matcher(sql);

        int groupIndex = 0;
        while (matcher.find()) {
            int matchedStart = matcher.start();
            String matchedGroup = matcher.group();

            resultSql.append(sql.substring(startPosition, matchedStart));
            resultSql.append("?");

            startPosition = matchedStart + matchedGroup.length();

            String varName = matchedGroup.substring(2);
            varName = varName.substring(0, varName.length() - 1);

            List<Integer> positionList = positions.get(varName);
            if (positionList == null) {
                positionList = new ArrayList<Integer>();
                positions.put(varName, positionList);
            }
            positionList.add(groupIndex + 1);
            groupIndex++;
        }
        resultSql.append(sql.substring(startPosition));
        return resultSql.toString();
    }


    public String getOriginalSql() {
        return originalSql;
    }


    public String getCompiledSql() {
        return compiledSql;
    }
}
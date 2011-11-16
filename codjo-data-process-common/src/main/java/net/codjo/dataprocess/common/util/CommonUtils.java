/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.common.util;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
/**
 *
 */
public class CommonUtils {
    private CommonUtils() {
    }


    public static String resultSetToStringFormat(ResultSet rs,
                                                 String separator,
                                                 String quote,
                                                 boolean column) throws SQLException {
        StringBuilder sb = new StringBuilder();
        ResultSetMetaData metadata = rs.getMetaData();
        int colmumnCount = metadata.getColumnCount();
        if (column) {
            for (int i = 1; i <= colmumnCount; i++) {
                sb.append(quote).append(metadata.getColumnName(i)).append(quote);
                if (i + 1 <= colmumnCount) {
                    sb.append(separator);
                }
            }
            sb.append("\n");
        }
        while (rs.next()) {
            for (int i = 1; i <= colmumnCount; i++) {
                sb.append(quote).append(rs.getObject(i)).append(quote);
                if (i + 1 <= colmumnCount) {
                    sb.append(separator);
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }


    public static List<String> tokenize(String str, String separator) {
        List<String> tokens = new ArrayList<String>();
        for (StringTokenizer strTokenizer = new StringTokenizer(str, separator);
             strTokenizer.hasMoreTokens();) {
            String token = strTokenizer.nextToken().trim();
            tokens.add(token);
        }
        return tokens;
    }


    public static List<String> stringToList(String parameters) {
        List<String> parametersList = new ArrayList<String>();

        StringTokenizer stringTokenizer = new StringTokenizer(parameters, ",");
        while (stringTokenizer.hasMoreTokens()) {
            String token = stringTokenizer.nextToken().trim();
            parametersList.add(token);
        }
        return parametersList;
    }


    public static String listToString(List<String> list) {
        StringBuilder result = new StringBuilder();

        if (list == null) {
            return "";
        }
        for (String aList : list) {
            result.append(aList).append(", ");
        }
        if (!list.isEmpty()) {
            return result.substring(0, result.length() - 2);
        }
        else {
            return result.toString();
        }
    }


    public static String timeMillisToString(long timeMillis) {
        StringBuilder strTime = new StringBuilder();
        long jour = (int)(timeMillis / 86400000);
        long heure = (int)((timeMillis - jour * 86400000) / 3600000);
        long minute = (int)((timeMillis - jour * 86400000 - heure * 3600000) / 60000);
        long seconde =
              (int)((timeMillis - jour * 86400000 - heure * 3600000 - minute * 60000) / 1000);
        long milliseconde =
              (int)(timeMillis - jour * 86400000 - heure * 3600000 - minute * 60000
                    - seconde * 1000);

        if (jour > 0) {
            strTime.append(jour).append("j ");
        }
        if (heure > 0) {
            strTime.append(heure).append("h ");
        }
        if (minute > 0) {
            strTime.append(minute).append("mn ");
        }
        if (seconde > 0) {
            strTime.append(seconde).append("s ");
        }
        if (jour + heure + minute + seconde == 0) {
            strTime.append(milliseconde).append("ms");
        }
        return strTime.toString().trim();
    }


    public static String doubleQuote(String str) {
        int pos = str.indexOf('\'');
        if (pos != -1) {
            str = str.substring(0, pos + 1) + "'" + doubleQuote(str.substring(pos + 1));
        }
        return str;
    }


    public static String localify(int repositoryId, String executionListName, String val) {
        return String.format("%d.%s.%s", repositoryId, executionListName, val.trim());
    }
}

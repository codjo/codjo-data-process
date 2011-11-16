/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.common.codec;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.commons.lang.StringUtils;
/**
 *
 */
public class ListCodec {
    private String separator = ",";
    private String openQuote = "'";
    private String closeQuote = "'";
    private boolean trim = false;


    public void setTrim(boolean trim) {
        this.trim = trim;
    }


    public String encode(List<String> parameters, String quoteOpenClose, String separatorParam) {
        this.openQuote = quoteOpenClose;
        this.closeQuote = quoteOpenClose;
        this.separator = separatorParam;
        return encode(parameters);
    }


    public String encode(List<String> parameters, Quote quote, String separatorParam) {
        this.openQuote = quote.openQuote;
        this.closeQuote = quote.closeQuote;
        this.separator = separatorParam;
        return encode(parameters);
    }


    public String encode(List<String> parameters) {
        if (parameters == null) {
            return "";
        }
        StringBuilder res = new StringBuilder();
        for (String parameter : parameters) {
            res.append(openQuote);
            if (trim) {
                res.append(parameter.trim());
            }
            else {
                res.append(parameter);
            }
            res.append(closeQuote);
            res.append(separator);
        }
        if (!parameters.isEmpty()) {
            return res.toString().substring(0, res.length() - separator.length());
        }
        else {
            return "";
        }
    }


    public List<String> decode(String listAsString, String quoteOpenClose, String separatorParam) {
        this.openQuote = quoteOpenClose;
        this.closeQuote = quoteOpenClose;
        this.separator = separatorParam;
        return decode(listAsString);
    }


    public List<String> decode(String listAsString, Quote quote, String separatorParam) {
        this.openQuote = quote.openQuote;
        this.closeQuote = quote.closeQuote;
        this.separator = separatorParam;
        return decode(listAsString);
    }


    public List<String> decode(String listAsString) {
        List<String> parameters = new ArrayList<String>();
        if (StringUtils.isEmpty(listAsString)) {
            return parameters;
        }
        StringBuilder partToken = new StringBuilder();
        StringTokenizer listTokenizer = new StringTokenizer(listAsString, separator);
        while (listTokenizer.hasMoreTokens()) {
            String token = listTokenizer.nextToken();

            if (proceedPartTok(parameters, partToken.toString())) {
                partToken = new StringBuilder();
            }

            if (token.trim().startsWith(openQuote) && token.trim().endsWith(closeQuote)) {
                token = removeQuote(token.trim());
                if (trim) {
                    token = token.trim();
                }
                parameters.add(token);
            }
            else {
                partToken.append(token);
                if (!partToken.toString().trim().endsWith(closeQuote)) {
                    partToken.append(separator);
                }
            }
        }
        proceedPartTok(parameters, partToken.toString());
        return parameters;
    }


    private boolean proceedPartTok(List<String> parameters, String partToken) {
        if (partToken.length() != 0) {
            if (partToken.trim().startsWith(openQuote)
                && partToken.trim().endsWith(closeQuote)) {
                partToken = removeQuote(partToken.trim());
                if (trim) {
                    partToken = partToken.trim();
                }
                parameters.add(partToken);
                return true;
            }
        }
        return false;
    }


    String removeQuote(String str) {
        return str.substring(openQuote.length(), str.length() - closeQuote.length());
    }


    static class Quote {
        private String openQuote;
        private String closeQuote;


        Quote(String openQuote, String closeQuote) {
            this.openQuote = openQuote;
            this.closeQuote = closeQuote;
        }
    }
}

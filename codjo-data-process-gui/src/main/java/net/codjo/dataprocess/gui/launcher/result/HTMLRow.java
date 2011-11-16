/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.gui.launcher.result;
/**
 *
 */
public class HTMLRow {
    static final int OK = 0;
    static final int NOT_STARTED = 1;
    static final int STARTED = 2;
    static final int ERROR = 3;
    private int state = NOT_STARTED;
    private String title;
    private String errorMessage;


    public HTMLRow(String title) {
        this.title = title;
    }


    public int getState() {
        return state;
    }


    public void start() {
        state = STARTED;
    }


    public void close() {
        state = OK;
    }


    public void declareError(Throwable err) {
        declareError(err.getLocalizedMessage());
    }


    public void declareError(String err) {
        state = ERROR;
        errorMessage = err;
    }


    public String buildReport() {
        return "<tr><th align=\"left\" valign=\"top\" bgcolor=\"#EEEEFF\">"
               + title + "</th><td align=\"left\" valign=\"top\" bgcolor=\""
               + getStateColor() + "\">" + getStateMessage() + "</td></tr>";
    }


    private String getStateColor() {
        switch (state) {
            case OK:
                return "#AAFFAA";
            case NOT_STARTED:
                return "#EEEEEE";
            case STARTED:
                return "#EEFFEE";
            default:
                return "#FFC800";
        }
    }


    private String getStateMessage() {
        switch (state) {
            case OK:
                return "OK";
            case NOT_STARTED:
                return "...";
            case STARTED:
                return "En cours...";
            default:
                return errorMessage.replace("\n", "<BR>");
        }
    }
}

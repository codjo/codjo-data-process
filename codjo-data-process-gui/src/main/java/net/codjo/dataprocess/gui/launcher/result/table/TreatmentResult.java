/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.gui.launcher.result.table;
/**
 *
 */
public class TreatmentResult {
    public static final int OK = 0;
    public static final int NOT_STARTED = 1;
    public static final int STARTED = 2;
    public static final int ERROR = 3;
    private String id;
    private String title;
    private int state = NOT_STARTED;
    private String message = "";
    private String errorMessage = null;


    public TreatmentResult(String id, String name) {
        this.id = id;
        this.title = name;
    }


    public String getTitle() {
        return title;
    }


    public String getId() {
        return id;
    }


    public int getState() {
        return state;
    }


    public String getMessage() {
        if (errorMessage != null) {
            return errorMessage;
        }
        else {
            return message;
        }
    }


    public void setMessage(String message) {
        this.message = message;
    }


    public void setStartProcessing() {
        this.state = STARTED;
        this.errorMessage = null;
    }


    public void setProcessingError(String error) {
        this.state = ERROR;
        this.errorMessage = error;
    }


    public void setEndProcessing() {
        this.state = OK;
        this.errorMessage = null;
    }


    public int getStateColor() {
        switch (state) {
            case OK:
                return 0xAAFFAA;
            case NOT_STARTED:
                return 0xEEEEEE;
            case STARTED:
                return 0xEEFFEE;
            default:
                return 0xFFC800;
        }
    }


    public String getStateLabel() {
        switch (state) {
            case OK:
                return "OK";
            case NOT_STARTED:
                return "...";
            case STARTED:
                return "En cours...";
            default:
                return "ERREUR";
        }
    }


    @Override
    public String toString() {
        return "[" + id + "::" + title + "::](" + getStateLabel() + ") " + message;
    }
}

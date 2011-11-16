/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.gui.util;
import java.awt.Component;
import java.io.PrintWriter;
import java.io.StringWriter;
/**
 *
 */
public class ErrorDialog {
    private ErrorDialog() {
    }


    public static void show(Component aFrame, String message, String exceptionMsg) {
        net.codjo.gui.toolkit.util.ErrorDialog.show(aFrame, message, getCustomizedMessage(exceptionMsg));
    }


    public static void show(Component aFrame, String message, Throwable error) {
        error.printStackTrace();
        String msg = "";
        if (error.getLocalizedMessage() != null) {
            msg = error.getLocalizedMessage();
        }
        show(aFrame, message, msg, buildStackTrace(error));
    }


    public static void show(Component aFrame, String message, String errorMessage, String errorDescription) {
        net.codjo.gui.toolkit.util.ErrorDialog.show(aFrame, message, getCustomizedMessage(errorMessage),
                                                  errorDescription);
    }


    public static void show(Component aFrame, String message, String errorMessage, Throwable error) {
        net.codjo.gui.toolkit.util.ErrorDialog.show(aFrame, message, getCustomizedMessage(errorMessage), error);
    }


    private static String getCustomizedMessage(String message) {
        return message + "\n\n\n"
               + "(Merci d'envoyer la totalité du contenu de l'onglet détail à votre responsable d'application)";
    }


    private static String buildStackTrace(Throwable exception) {
        StringWriter stackTrace = new StringWriter();
        exception.printStackTrace(new PrintWriter(stackTrace));
        return stackTrace.toString();
    }
}

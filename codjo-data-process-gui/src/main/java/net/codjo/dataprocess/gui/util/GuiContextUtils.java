/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.gui.util;
import net.codjo.dataprocess.common.DataProcessConstants;
import net.codjo.dataprocess.common.context.DataProcessContext;
import net.codjo.mad.gui.framework.MutableGuiContext;
/**
 *
 */
public class GuiContextUtils {
    private static final String USER_DATA_PROCESS_CONTEXT_PROP = "USER_DATA_PROCESS_CONTEXT";


    private GuiContextUtils() {
    }


    public static DataProcessContext getDataProcessContext(MutableGuiContext context) {
        return (DataProcessContext)getProperty(context, USER_DATA_PROCESS_CONTEXT_PROP);
    }


    public static void putDataProcessContext(MutableGuiContext context,
                                             DataProcessContext dataProcessContext) {
        context.putProperty(USER_DATA_PROCESS_CONTEXT_PROP, dataProcessContext);
    }


    private static Object getProperty(MutableGuiContext context, String key) {
        Object value = context.getProperty(key);
        if (value == null) {
            throw new IllegalArgumentException(key + " n'est pas dans le GuiContext");
        }
        return value;
    }


    public static String getCurrentRepository(MutableGuiContext ctxt) {
        return (String)ctxt.getProperty(DataProcessConstants.CURRENT_REPOSITORY_PROP);
    }


    public static void setCurrentRepository(MutableGuiContext ctxt, String repositoryId) {
        ctxt.putProperty(DataProcessConstants.CURRENT_REPOSITORY_PROP, repositoryId);
    }
}

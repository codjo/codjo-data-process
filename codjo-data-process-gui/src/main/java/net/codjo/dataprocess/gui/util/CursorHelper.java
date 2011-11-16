package net.codjo.dataprocess.gui.util;
import net.codjo.mad.gui.framework.GuiContext;
import java.awt.Cursor;
import javax.swing.JInternalFrame;
/**
 *
 */
public class CursorHelper {
    private CursorHelper() {
    }


    public static void defaultCursor(GuiContext guiCtxt) {
        changeCursorOnAllFrames(guiCtxt, Cursor.DEFAULT_CURSOR);
    }


    public static void waitCursor(GuiContext guiCtxt) {
        changeCursorOnAllFrames(guiCtxt, Cursor.WAIT_CURSOR);
    }


    private static void changeCursorOnAllFrames(GuiContext guiCtxt, int type) {
        JInternalFrame[] jInternalFrames = guiCtxt.getDesktopPane().getAllFrames();
        for (JInternalFrame frame : jInternalFrames) {
            frame.setCursor(Cursor.getPredefinedCursor(type));
        }
        guiCtxt.getMainFrame().setCursor(Cursor.getPredefinedCursor(type));
    }
}

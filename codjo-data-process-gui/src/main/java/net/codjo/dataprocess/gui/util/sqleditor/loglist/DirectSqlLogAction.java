/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.gui.util.sqleditor.loglist;
import net.codjo.dataprocess.gui.util.std.AbstractAction;
import net.codjo.mad.gui.framework.GuiContext;
import javax.swing.JInternalFrame;
/**
 *
 */
public class DirectSqlLogAction extends AbstractAction {

    public DirectSqlLogAction(GuiContext ctxt) {
        super(ctxt, "Log des appels sql direct", "Log des appels sql direct");
    }


    @Override
    protected JInternalFrame buildFrame(GuiContext ctxt) throws Exception {
        return new DirectSqlLogListWindow(getGuiContext());
    }
}

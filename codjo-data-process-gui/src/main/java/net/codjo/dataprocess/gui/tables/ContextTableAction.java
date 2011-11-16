/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.gui.tables;
import net.codjo.dataprocess.gui.util.std.AbstractAction;
import net.codjo.mad.gui.framework.GuiContext;
import javax.swing.JInternalFrame;
/**
 *
 */
public class ContextTableAction extends AbstractAction {
    public ContextTableAction(GuiContext ctxt) {
        super(ctxt, "PM_DP_CONTEXT", "Table PM_DP_CONTEXT");
    }


    @Override
    protected JInternalFrame buildFrame(GuiContext ctxt) throws Exception {
        return new ContextWindowTable(getGuiContext());
    }
}

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
public class ExecutionListTableAction extends AbstractAction {
    public ExecutionListTableAction(GuiContext ctxt) {
        super(ctxt, "PM_EXECUTION_LIST", "Table PM_EXECUTION_LIST");
    }


    @Override
    protected JInternalFrame buildFrame(GuiContext ctxt) throws Exception {
        return new ExecutionListWindowTable(getGuiContext());
    }
}

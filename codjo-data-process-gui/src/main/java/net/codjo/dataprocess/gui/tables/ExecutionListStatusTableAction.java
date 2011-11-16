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
public class ExecutionListStatusTableAction extends AbstractAction {
    public ExecutionListStatusTableAction(GuiContext ctxt) {
        super(ctxt, "PM_EXECUTION_LIST_STATUS", "Table PM_EXECUTION_LIST_STATUS");
    }


    @Override
    protected JInternalFrame buildFrame(GuiContext ctxt) throws Exception {
        return new ExecutionListStatusWindowTable(getGuiContext());
    }
}

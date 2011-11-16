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
public class RepositoryTableAction extends AbstractAction {
    public RepositoryTableAction(GuiContext ctxt) {
        super(ctxt, "PM_REPOSITORY", "Table PM_REPOSITORY");
    }


    @Override
    protected JInternalFrame buildFrame(GuiContext ctxt) throws Exception {
        return new RepositoryWindowTable(getGuiContext());
    }
}

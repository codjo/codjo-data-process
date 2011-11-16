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
public class DependencyTableAction extends AbstractAction {
    public DependencyTableAction(GuiContext ctxt) {
        super(ctxt, "PM_DEPENDENCY", "Table PM_DEPENDENCY");
    }


    @Override
    protected JInternalFrame buildFrame(GuiContext ctxt) throws Exception {
        return new DependencyWindowTable(getGuiContext());
    }
}

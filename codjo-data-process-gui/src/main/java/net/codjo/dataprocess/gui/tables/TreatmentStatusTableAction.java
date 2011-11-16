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
public class TreatmentStatusTableAction extends AbstractAction {
    public TreatmentStatusTableAction(GuiContext ctxt) {
        super(ctxt, "PM_TREATMENT_STATUS", "Table PM_TREATMENT_STATUS");
    }


    @Override
    protected JInternalFrame buildFrame(GuiContext ctxt) throws Exception {
        return new TreatmentStatusWindowTable(getGuiContext());
    }
}
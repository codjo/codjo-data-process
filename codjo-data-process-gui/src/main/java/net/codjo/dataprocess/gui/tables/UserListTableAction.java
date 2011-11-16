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
public class UserListTableAction extends AbstractAction {
    public UserListTableAction(GuiContext ctxt) {
        super(ctxt, "Gestion des droits sur les référentiels de traitement",
              "Gestion des droits sur les référentiels de traitement");
    }


    @Override
    protected JInternalFrame buildFrame(GuiContext ctxt) throws Exception {
        return new UserWindowTable(getGuiContext());
    }
}

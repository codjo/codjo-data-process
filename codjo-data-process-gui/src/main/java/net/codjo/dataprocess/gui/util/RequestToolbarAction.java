package net.codjo.dataprocess.gui.util;
import net.codjo.mad.gui.framework.AbstractGuiAction;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.mad.gui.request.RequestTable;
/**
 *
 */
public abstract class RequestToolbarAction extends AbstractGuiAction {
    private RequestTable requestTable;


    protected RequestToolbarAction(GuiContext ctxt, String name, String description) {
        super(ctxt, name, description);
    }


    protected RequestToolbarAction(GuiContext ctxt, String name, String description, String iconId) {
        super(ctxt, name, description, iconId);
    }


    protected RequestToolbarAction(GuiContext ctxt, String name, String description, String iconId,
                                   String actionId) {
        super(ctxt, name, description, iconId, actionId);
    }


    public void setRequestTable(RequestTable requestTable) {
        this.requestTable = requestTable;
    }


    public RequestTable getRequestTable() {
        return requestTable;
    }
}
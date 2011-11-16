package net.codjo.dataprocess.gui.util.std;
import net.codjo.dataprocess.gui.util.RequestToolbarAction;
import net.codjo.mad.gui.framework.GuiContext;
import java.util.List;
/**
 *
 */
public class DefaultListWindow extends AbstractListWindow {
    public DefaultListWindow(String title, GuiContext subCtxt, String preference, String table)
          throws Exception {
        super(title, subCtxt, preference, table);
    }


    public DefaultListWindow(String title,
                             GuiContext subCtxt,
                             String preference,
                             String table,
                             List<RequestToolbarAction> requestToolbarActionList) throws Exception {
        super(title, subCtxt, preference, table, requestToolbarActionList);
    }


    public DefaultListWindow(String title,
                             GuiContext subCtxt,
                             String preference,
                             String table,
                             List<RequestToolbarAction> requestToolbarActionList,
                             AbstractRequestTableLoader requestTableLoader) throws Exception {
        super(title, subCtxt, preference, table, requestToolbarActionList, requestTableLoader);
    }
}

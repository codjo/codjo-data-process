package net.codjo.dataprocess.gui.util.sqleditor.components;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
/**
 *
 */
public class DataBasePopupGui extends JPopupMenu {
    private JMenuItem menuItemGenerateSelectAll;
    private JMenuItem menuItemGenerateInsertInto;
    private JMenuItem menuItemGenerateUpdate;


    public DataBasePopupGui() {
        menuItemGenerateInsertInto = new JMenuItem("Generate insert into");
        menuItemGenerateSelectAll = new JMenuItem("Generate select *");
        menuItemGenerateUpdate = new JMenuItem("Generate update");

        add(menuItemGenerateSelectAll);
        add(menuItemGenerateInsertInto);
        add(menuItemGenerateUpdate);
    }


    public JMenuItem getMenuItemGenerateSelectAll() {
        return menuItemGenerateSelectAll;
    }


    public JMenuItem getMenuItemGenerateInsertInto() {
        return menuItemGenerateInsertInto;
    }


    public JMenuItem getMenuItemGenerateUpdate() {
        return menuItemGenerateUpdate;
    }

}

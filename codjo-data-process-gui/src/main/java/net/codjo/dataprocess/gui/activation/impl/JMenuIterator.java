package net.codjo.dataprocess.gui.activation.impl;
import net.codjo.dataprocess.gui.activation.spi.JComponentIterator;
import net.codjo.dataprocess.gui.activation.spi.JComponentPod;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;
/**
 *
 */
public class JMenuIterator implements JComponentIterator {
    private List<JComponentPod> menuList = new ArrayList<JComponentPod>();
    private int currentIndex = 0;


    public JMenuIterator(JMenuBar menuBar) {
        int menuCount = menuBar.getMenuCount();
        for (int index = 0; index < menuCount; index++) {
            JMenu menu = menuBar.getMenu(index);
            menuList.add(new JComponentPod(menu, menu.getText()));
            if (menu.getSubElements().length > 0) {
                computePopupMenu(menu.getSubElements()[0], menu.getText());
            }
        }
    }


    private void computePopupMenu(MenuElement menuElement, String parentPath) {
        MenuElement[] subElements = menuElement.getSubElements();
        for (MenuElement subElement : subElements) {
            if (subElement instanceof JPopupMenu) {
                JPopupMenu popupMenu = (JPopupMenu)subElement;
                computePopupMenu(popupMenu, parentPath);
            }
            else if (subElement instanceof JMenu) {
                JMenu subMenu = (JMenu)subElement;
                menuList.add(new JComponentPod(subMenu, parentPath + ":" + subMenu.getText()));
                if (subMenu.getSubElements().length > 0) {
                    computePopupMenu(subMenu.getSubElements()[0], parentPath + ":" + subMenu.getText());
                }
            }
            else if (subElement instanceof JMenuItem) {
                JMenuItem menuItem = (JMenuItem)subElement;
                menuList.add(new JComponentPod(menuItem, parentPath + ":" + menuItem.getText()));
            }
        }
    }


    public boolean hasMoreComponents() {
        return currentIndex < menuList.size();
    }


    public JComponentPod next() {
        return menuList.get(currentIndex++);
    }


    public void resetIndex() {
        currentIndex = 0;
    }
}

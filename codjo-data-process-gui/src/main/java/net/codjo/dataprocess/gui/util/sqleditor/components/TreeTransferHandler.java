package net.codjo.dataprocess.gui.util.sqleditor.components;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.tree.TreePath;
/**
 *
 */
public class TreeTransferHandler extends StringTransferHandler {

    // Donne le paquet à envoyer à l'editor
    @Override
    protected String exportString(JComponent component) {
        StringBuilder buff = new StringBuilder();
        JTree tree = (JTree)component;
        TreePath[] selectionPaths = tree.getSelectionPaths();

        for (int index = 0; index < selectionPaths.length; index++) {
            Object[] path = selectionPaths[index].getPath();
            if (path.length >= 2) {
                if (buff.length() > 0) {
                    buff.append(", ");
                }
                if (index > 0 && index % 3 == 0) {
                    buff.append("\n");
                }
                buff.append(path[1]);
                if (path.length == 3) {
                    buff.append(".").append(path[2]);
                }
            }
        }
        return buff.toString();
    }


    // Pas de drop sur la liste
    @Override
    protected void importString(JComponent comp, String str) {
    }


    //Pas de clean up les elements restent dans la liste
    @Override
    protected void cleanup(JComponent ccomp, boolean remove) {
    }
}
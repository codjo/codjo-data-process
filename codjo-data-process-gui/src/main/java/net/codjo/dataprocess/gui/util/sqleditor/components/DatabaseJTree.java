/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.gui.util.sqleditor.components;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JTree;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
/**
 *
 */
public class DatabaseJTree extends JTree {
    public DatabaseJTree() {
        configureDragNDrop();
    }


    public void setData(List<String> metaDataList) {
        DatabaseTreeModel model = new DatabaseTreeModel();

        for (String metaData : metaDataList) {
            String table = metaData.substring(0, metaData.indexOf('.'));
            String column = metaData.substring(metaData.indexOf('.') + 1);
            if (!model.getTableNamesList().contains(table)) {
                model.getTableNamesList().add(table);
                model.getColumnListByTable().put(table, new ArrayList<String>());
            }
            List<String> columnList = model.getColumnListByTable().get(table);
            if (!columnList.contains(column)) {
                columnList.add(column);
            }
        }
        setModel(model);
    }


    private void configureDragNDrop() {
        setDragEnabled(true);
        setTransferHandler(new TreeTransferHandler());
    }


    static class DatabaseTreeModel implements TreeModel {
        private List<String> tableNamesList;
        private Map<String, List<String>> columnListByTable;
        private String root = "Database";


        DatabaseTreeModel() {
            tableNamesList = new ArrayList<String>();
            columnListByTable = new HashMap<String, List<String>>();
        }


        public Map<String, List<String>> getColumnListByTable() {
            return columnListByTable;
        }


        public List<String> getTableNamesList() {
            return tableNamesList;
        }


        public void addTreeModelListener(TreeModelListener listener) {
        }


        public Object getChild(Object parent, int index) {
            if (parent == root) {
                return tableNamesList.get(index);
            }
            List<String> list = columnListByTable.get(parent.toString());
            return list.get(index);
        }


        public int getChildCount(Object parent) {
            if (parent == root) {
                return tableNamesList.size();
            }
            List<String> list = columnListByTable.get(parent.toString());
            return list.size();
        }


        public int getIndexOfChild(Object parent, Object child) {
            if (parent == root) {
                return tableNamesList.lastIndexOf(child.toString());
            }
            List<String> list = columnListByTable.get(parent.toString());
            return list.lastIndexOf(child.toString());
        }


        public Object getRoot() {
            return root;
        }


        public boolean isLeaf(Object node) {
            return node != root && !tableNamesList.contains(node.toString());
        }


        public void removeTreeModelListener(TreeModelListener listener) {
        }


        public void valueForPathChanged(TreePath path, Object newValue) {
        }
    }
}

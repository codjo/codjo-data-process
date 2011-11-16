package net.codjo.dataprocess.gui.util.tableexplorer;
import net.codjo.dataprocess.client.HandlerCommandSender;
import net.codjo.dataprocess.common.DataProcessConstants;
import net.codjo.dataprocess.gui.plugin.DataProcessGuiConfiguration;
import net.codjo.dataprocess.gui.util.GuiUtils;
import net.codjo.gui.toolkit.waiting.WaitingPanel;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.client.request.Result;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.mad.gui.framework.MutableGuiContext;
import net.codjo.mad.gui.request.Preference;
import net.codjo.mad.gui.request.PreferenceFactory;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyVetoException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
/**
 *
 */
public class TableExploratorGui {
    private JPanel mainPanel;
    private JTextField filterField;
    private JTree tableTree;
    private JButton quitButton;
    private JButton openButton;
    private JLabel statusLabel;
    private JTextArea whereTextArea;
    private JInternalFrame frame;
    private MutableGuiContext ctxt;
    private DataProcessGuiConfiguration dataProcessGuiConfig;
    private DatabaseTreeModel databaseTreeModel = new DatabaseTreeModel();
    private Map<String, Action> windowActionMap = new HashMap<String, Action>();
    private WaitingPanel waitingPanel = new WaitingPanel("Récupération de la liste des tables ...");


    TableExploratorGui(MutableGuiContext ctxt,
                       JInternalFrame frame,
                       DataProcessGuiConfiguration dataProcessGuiConfig) {
        this.frame = frame;
        this.ctxt = ctxt;
        this.dataProcessGuiConfig = dataProcessGuiConfig;

        frame.setGlassPane(waitingPanel);
        tableTree.setModel(new DefaultTreeModel(null));
        tableTree.setToggleClickCount(0);

        statusLabel.setText("");
        createListener();

        ctxt.getDesktopPane().add(frame);
        frame.pack();
        GuiUtils.setToLeftSide(frame);
        frame.setVisible(true);
        try {
            frame.setSelected(true);
        }
        catch (PropertyVetoException e) {
            ;
        }
    }


    private void createListener() {
        InputMap inputMap = frame.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        inputMap.put(KeyStroke.getKeyStroke("ESCAPE"), "cancel");
        final JInternalFrame frame1 = frame;
        frame.getActionMap().put("cancel", new javax.swing.AbstractAction() {
            public void actionPerformed(ActionEvent evt) {
                frame1.dispose();
            }
        });
        frame.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control F"),
                                                                 "focus_filterField");
        frame.getActionMap().put("focus_filterField", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                filterField.requestFocus();
                filterField.selectAll();
            }
        });
        filterField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent evt) {
                filterChanged();
            }


            public void removeUpdate(DocumentEvent evt) {
                filterChanged();
            }


            public void changedUpdate(DocumentEvent evt) {
                filterChanged();
            }
        });
        filterField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (KeyEvent.VK_DOWN == e.getKeyCode()) {
                    tableTree.requestFocus();
                    tableTree.setSelectionPath(new TreePath("Tables"));
                }
            }
        });
        openButton.addActionListener(new OpenTablesButtonListener());

        tableTree.addMouseListener(new MyMouseAdapter());
        tableTree.addKeyListener(new MyTreeKeyAdapter());
        quitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                frame.dispose();
            }
        });
    }


    private void filterChanged() {
        tableTree.setModel(new DefaultTreeModel(null));
        databaseTreeModel.filter(filterField.getText());
        tableTree.setModel(databaseTreeModel);
        updateStatus(true);
    }


    public void loadData(final boolean exclude) {
        waitingPanel.exec(new Runnable() {
            public void run() {
                try {
                    loadMetaData(exclude);
                    filterField.requestFocusInWindow();
                }
                catch (RequestException ex) {
                    GuiUtils.showErrorDialog(TableExploratorGui.this.frame, getClass(), "Erreur interne", ex);
                }
            }
        });
    }


    public JPanel getMainPanel() {
        return mainPanel;
    }


    private void loadMetaData(boolean exclude) throws RequestException {
        HandlerCommandSender commandSender = new HandlerCommandSender();
        Result result = commandSender.sendCommand(ctxt, "dbGetAllFieldNamesByTable", null);

        if (result.getRowCount() > 0) {
            String metaDataList = result.getRow(0).getFieldValue("result");
            StringTokenizer tokenizer = new StringTokenizer(metaDataList, ",");
            while (tokenizer.hasMoreElements()) {
                String metaData = (String)tokenizer.nextElement();
                String table = metaData.substring(0, metaData.indexOf('.'));
                if (!matchExclusionRules(table) || !exclude) {
                    String column = metaData.substring(metaData.indexOf('.') + 1);
                    if (!databaseTreeModel.getTableNamesList().contains(table)) {
                        databaseTreeModel.getTableNamesList().add(table);
                        databaseTreeModel.getTableNamesListRef().add(table);
                        databaseTreeModel.getColumnListByTable().put(table, new ArrayList<String>());
                        databaseTreeModel.getColumnListByTableRef().put(table, new ArrayList<String>());
                    }
                    List<String> columnList = databaseTreeModel.getColumnListByTable().get(table);
                    List<String> columnListRef = databaseTreeModel.getColumnListByTableRef().get(table);
                    if (!columnList.contains(column)) {
                        columnList.add(column);
                    }
                    if (!columnListRef.contains(column)) {
                        columnListRef.add(column);
                    }
                }
            }
        }
        for (Entry<String, List<String>> entry : databaseTreeModel.getColumnListByTable().entrySet()) {
            List<String> list = entry.getValue();
            Collections.sort(list);
        }
        for (Entry<String, List<String>> entry : databaseTreeModel.getColumnListByTableRef().entrySet()) {
            List<String> list = entry.getValue();
            Collections.sort(list);
        }
        tableTree.setModel(databaseTreeModel);
        updateStatus(exclude);
    }


    private void updateStatus(boolean showTooltip) {
        Set<String> exclusionRuleSet = dataProcessGuiConfig.getTableExploratorConfig().getExclusionRuleSet();
        statusLabel.setText(databaseTreeModel.getColumnListByTable().size() + " tables affichées / "
                            + databaseTreeModel.getColumnListByTableRef().size());
        if (showTooltip) {
            statusLabel.setToolTipText(exclusionRuleSet.size() + " tables cachées");
        }
        else {
            statusLabel.setToolTipText("");
        }
    }


    private boolean matchExclusionRules(String table) {
        Set<String> exclusionRuleSet = dataProcessGuiConfig.getTableExploratorConfig().getExclusionRuleSet();
        for (String rule : exclusionRuleSet) {
            Pattern pattern = Pattern.compile(rule);
            Matcher matcher = pattern.matcher(table);
            if (matcher.find()) {
                return true;
            }
        }
        return false;
    }


    private String getClassNameListWindow(String tableName) {
        StringBuilder sb = new StringBuilder();
        try {
            Preference preference = PreferenceFactory.getPreference(tableName);
            String detailWindowClassName = preference.getDetailWindowClass().getName();
            sb.append(detailWindowClassName
                            .substring(0, detailWindowClassName.length() - "DetailWindow".length()));
            sb.append("Action");
        }
        catch (Exception ex) {
            String actionClassName = dataProcessGuiConfig.getTableExploratorConfig()
                  .getTableAction(tableName);
            if (actionClassName != null) {
                sb.append(actionClassName);
            }
        }
        return sb.toString();
    }


    private void openTable(String preference) {
        String actionClassName = null;
        try {
            actionClassName = getClassNameListWindow(preference);
            Action action;
            if (windowActionMap.get(actionClassName) == null) {
                Constructor constructor = Class.forName(actionClassName).getConstructor(GuiContext.class);
                action = (Action)constructor.newInstance(ctxt);
                windowActionMap.put(actionClassName, action);
            }
            else {
                action = windowActionMap.get(actionClassName);
            }
            ctxt.putProperty(DataProcessConstants.WHERE_CLAUSE_KEY, whereTextArea.getText());
            action.actionPerformed(new ActionEvent(DataProcessConstants.TABLE_EXPLORATOR,
                                                   0,
                                                   whereTextArea.getText()));
        }
        catch (Exception ex) {
            GuiUtils.showErrorDialog(frame, getClass(),
                                     String.format("Impossible d'ouvrir l'action '%s' de la table"
                                                   + " avec la préférence '%s'", actionClassName, preference),
                                     ex);
        }
    }


    private static class DatabaseTreeModel implements TreeModel {
        private List<String> tableNamesListRef = new ArrayList<String>();
        private List<String> tableNamesList = new ArrayList<String>();
        private Map<String, List<String>> columnListByTableRef = new HashMap<String, List<String>>();
        private Map<String, List<String>> columnListByTable = new HashMap<String, List<String>>();
        private String root = "Tables";


        DatabaseTreeModel() {
        }


        public void filter(String filter) {
            tableNamesList.clear();
            columnListByTable.clear();
            for (String tableName : tableNamesListRef) {
                if (filter.startsWith("*")) {
                    if (tableName.toLowerCase().contains(filter.substring(1).toLowerCase())) {
                        tableNamesList.add(tableName);
                        columnListByTable.put(tableName, columnListByTableRef.get(tableName));
                    }
                }
                else {
                    if (tableName.toLowerCase().startsWith(filter.toLowerCase())) {
                        tableNamesList.add(tableName);
                        columnListByTable.put(tableName, columnListByTableRef.get(tableName));
                    }
                }
            }
        }


        public Map<String, List<String>> getColumnListByTable() {
            return columnListByTable;
        }


        public Map<String, List<String>> getColumnListByTableRef() {
            return columnListByTableRef;
        }


        public List<String> getTableNamesList() {
            return tableNamesList;
        }


        public List<String> getTableNamesListRef() {
            return tableNamesListRef;
        }


        public void addTreeModelListener(TreeModelListener l1) {
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


        public void removeTreeModelListener(TreeModelListener l1) {
        }


        public void valueForPathChanged(TreePath path, Object newValue) {
        }
    }

    private class MyMouseAdapter extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent evt) {
            int selRow = tableTree.getRowForLocation(evt.getX(), evt.getY());
            if (selRow != -1) {
                if (evt.getClickCount() == 2) {
                    TreePath path = tableTree.getSelectionModel().getSelectionPath();
                    if (path != null && path.getPathCount() >= 2) {
                        String tableName = (String)path.getPathComponent(1);
                        openTable(tableName);
                    }
                }
            }
        }
    }

    private class OpenTablesButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
            TreePath[] paths = tableTree.getSelectionModel().getSelectionPaths();
            if (paths != null) {
                for (TreePath path : paths) {
                    if (path.getPathCount() >= 2) {
                        String tableName = (String)path.getPathComponent(1);
                        openTable(tableName);
                    }
                }
            }
        }
    }

    private class MyTreeKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (KeyEvent.VK_ENTER == e.getKeyCode()) {
                TreePath[] paths = tableTree.getSelectionModel().getSelectionPaths();
                if (paths != null) {
                    for (TreePath path : paths) {
                        if (path.getPathCount() >= 2) {
                            String tableName = (String)path.getPathComponent(1);
                            openTable(tableName);
                        }
                    }
                }
            }
        }
    }
}

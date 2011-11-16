/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.gui.util.sqleditor;
import net.codjo.dataprocess.gui.util.GuiUtils;
import net.codjo.dataprocess.gui.util.sqleditor.components.DataBasePopupGui;
import net.codjo.dataprocess.gui.util.sqleditor.components.DatabaseJTree;
import net.codjo.dataprocess.gui.util.sqleditor.components.JHistoricComboBox;
import net.codjo.dataprocess.gui.util.sqleditor.components.ResultTabbedPane;
import net.codjo.dataprocess.gui.util.sqleditor.components.SQLSyntaxEditor;
import net.codjo.gui.toolkit.waiting.WaitingPanel;
import net.codjo.mad.client.request.RequestException;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.tree.TreeSelectionModel;
/**
 *
 */
public class SqlEditorDetailWindowGui extends JInternalFrame {
    private DatabaseJTree databaseTree;
    private ResultTabbedPane sqlResultTabs;
    private SQLSyntaxEditor sqlEditor;
    private JButton executeButton;
    private JButton quitButton;
    private JHistoricComboBox sqlHistory;
    private WaitingPanel waitingPanel = new WaitingPanel("Requête en cours...");
    private DataBasePopupGui popupMenu;
    private JTextField pageSize;


    public SqlEditorDetailWindowGui() throws RequestException {
        super("Editeur SQL", true, true, true, true);
        buildGui();
    }


    public void setDatabasePopupMenu(DataBasePopupGui popupMenu) {
        this.popupMenu = popupMenu;
    }


    public SQLSyntaxEditor getSqlEditor() {
        return sqlEditor;
    }


    public WaitingPanel getWaitingPanel() {
        return waitingPanel;
    }


    public JHistoricComboBox getSqlHistory() {
        return sqlHistory;
    }


    public JButton getQuitButton() {
        return quitButton;
    }


    public DataBasePopupGui getDatabasePopupMenu() {
        return popupMenu;
    }


    public ResultTabbedPane getSqlResultTabs() {
        return sqlResultTabs;
    }


    public JButton getExecuteButton() {
        return executeButton;
    }


    public DatabaseJTree getDatabaseTree() {
        return databaseTree;
    }


    public JTextField getPageSize() {
        return pageSize;
    }


    private void buildGui() {
        quitButton = new JButton(UIManager.getIcon("dataprocess.exit"));
        quitButton.setToolTipText("Fermer la fenêtre");
        GuiUtils.setMaxSize(quitButton, 25, 25);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBorder(new EmptyBorder(2, 2, 2, 2));
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
        bottomPanel.add(Box.createHorizontalGlue());
        bottomPanel.add(quitButton);

        setGlassPane(waitingPanel);
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(new TitledBorder("Tables"));
        databaseTree = new DatabaseJTree();
        databaseTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        leftPanel.add(BorderLayout.CENTER, new JScrollPane(databaseTree));

        JPanel rightTopPanel = new JPanel(new BorderLayout());
        rightTopPanel.setBorder(new TitledBorder("Requête"));
        sqlEditor = new SQLSyntaxEditor();
        rightTopPanel.add(BorderLayout.CENTER, new JScrollPane(sqlEditor));
        sqlHistory = new JHistoricComboBox(10);
        executeButton = new JButton("Executer");
        JPanel commandPanel = new JPanel(new BorderLayout());
        commandPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        commandPanel.add(BorderLayout.CENTER, sqlHistory);
        JPanel eastBlok = new JPanel(new BorderLayout());
        eastBlok.add(BorderLayout.WEST, new JLabel("  Taille de page  "));
        pageSize = new JTextField("1000");
        pageSize.setMinimumSize(new Dimension(100, 20));
        eastBlok.add(BorderLayout.CENTER, pageSize);
        eastBlok.add(BorderLayout.EAST, executeButton);
        commandPanel.add(BorderLayout.EAST, eastBlok);
        rightTopPanel.add(BorderLayout.NORTH, commandPanel);

        JPanel rightBottomPanel = new JPanel(new BorderLayout());
        rightBottomPanel.setBorder(new TitledBorder("Résultats"));
        sqlResultTabs = new ResultTabbedPane();
        rightBottomPanel.add(BorderLayout.CENTER, sqlResultTabs);

        JPanel rightPanel = new JPanel(new BorderLayout());
        JSplitPane rightSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, rightTopPanel,
                                                   rightBottomPanel);
        rightSplitPane.setDividerLocation(150);
        rightSplitPane.setDividerSize(5);
        rightSplitPane.setContinuousLayout(true);
        rightPanel.add(BorderLayout.CENTER, rightSplitPane);

        setLayout(new BorderLayout());
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(300);
        splitPane.setDividerSize(5);
        splitPane.setContinuousLayout(true);
        mainPanel.add(splitPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        add(mainPanel, BorderLayout.CENTER);

        setSize(900, 600);
    }


    public interface TAGS {
        String SQL_EDITOR = "sqlEditor";
        String EXEC_BUTTON = "ExecuteButton";
        String HISTORY = "SqlHistory";
        String DB_TREE = "DatabaseTree";
    }
}

package net.codjo.dataprocess.gui.util.sqleditor.components;
import net.codjo.dataprocess.gui.util.sqleditor.util.SQLEditorTools;
import java.awt.BorderLayout;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
/**
 *
 */
public class ResultPaneGui extends JPanel {

    private JButton closeButton;
    private JTextArea requestTextArea;
    private JTable table;


    public JButton getCloseButton() {
        return closeButton;
    }


    public JTable getTable() {
        return table;
    }


    public ResultPaneGui() {
        setLayout(new BorderLayout());
        closeButton = new JButton("Fermer");
    }


    public void init(String request,
                     StringBuffer buffer,
                     final int pageSize,
                     NavigationPanelLogic navigationPanelLogic, SQLEditorTools sqlEditorTools) {
        JPanel centerPanel = new JPanel();
        add(BorderLayout.CENTER, centerPanel);

        centerPanel.setLayout(new BorderLayout());
        requestTextArea = new JTextArea(request, 3, 200);
        requestTextArea.setEditable(false);

        String typeOfResult = sqlEditorTools.extractLine(buffer);
        if ("EX".equals(typeOfResult) || "RC".equals(typeOfResult)) {
            JTextArea textArea = new JTextArea(buffer.toString());
            centerPanel.add(BorderLayout.CENTER, new JScrollPane(textArea));
        }
        else {
            //ne se sert pas du nombre de colonnes
            sqlEditorTools.extractLine(buffer);
            int nbRow = Integer.parseInt(sqlEditorTools.extractLine(buffer));
            final int nbPage = nbRow / pageSize + (nbRow % pageSize > 0 ? 1 : 0);

            table = new JTable();
            table.setModel(ResultPaneLogic.createTableModelResult(buffer, sqlEditorTools));
            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

            JPanel resultSetPanel = new JPanel(new BorderLayout());
            resultSetPanel.add(BorderLayout.CENTER, new JScrollPane(table));

            JPanel totalCountPanel = new JPanel();
            totalCountPanel.setLayout(new BoxLayout(totalCountPanel, BoxLayout.X_AXIS));
            totalCountPanel.add(Box.createHorizontalGlue());
            totalCountPanel.add(new JLabel("Total : " + nbRow + " (" + pageSize + " par page)"));

            resultSetPanel.add(BorderLayout.SOUTH, totalCountPanel);
            if (nbPage > 1) {
                navigationPanelLogic.setNbOfPage(nbPage);
                resultSetPanel.add(BorderLayout.NORTH, navigationPanelLogic.getNavigationPanelGui());
            }

            centerPanel.add(BorderLayout.CENTER, resultSetPanel);
        }

        JPanel endPanel = new JPanel();
        endPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        endPanel.setLayout(new BoxLayout(endPanel, BoxLayout.X_AXIS));
        endPanel.add(Box.createHorizontalGlue());
        endPanel.add(closeButton);

        centerPanel.add(BorderLayout.SOUTH, endPanel);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(BorderLayout.CENTER, new JScrollPane(requestTextArea));

        centerPanel.add(BorderLayout.NORTH, topPanel);
    }


    public JTextArea getRequestTextArea() {
        return requestTextArea;
    }
}

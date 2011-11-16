/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.gui.launcher.result;
import net.codjo.dataprocess.common.model.ExecutionListModel;
import net.codjo.dataprocess.gui.launcher.result.table.ResultPanel;
import net.codjo.mad.gui.framework.GuiContext;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
/**
 *
 */
public class TreatmentStepGui implements ResultTreatmentGui {
    public static final int TABLE_MODE = 0;
    public static final int HTML_MODE = 1;
    private ResultPanel tableResultPanel = new ResultPanel();
    private JEditorPane editorPane = new JEditorPane();
    private CardLayout cardLayout = new CardLayout();
    private JPanel mainPanel;
    private String title;
    private GuiContext ctxt;
    private String executionListName;


    public TreatmentStepGui(GuiContext ctxt, String executionListName, String title, int mode) {
        this.ctxt = ctxt;
        this.executionListName = executionListName;
        this.title = title;
        buildGui();
        setMode(mode);
    }


    public void setMode(int mode) {
        cardLayout.show(getMainComponent(), "MODE" + mode);
    }


    public String getTitle() {
        return title;
    }


    public void buildTreatmentResult(ExecutionListModel executionListModel,
                                     TreatmentResultListener treatmentResultListener) {
        tableResultPanel.buildTreatmentResult(executionListModel);
        treatmentResultListener.setResultPanel(tableResultPanel);
        tableResultPanel.informationChanged();
        getMainComponent().repaint();
    }


    private void buildGui() {
        mainPanel = new JPanel();
        mainPanel.putClientProperty(ResultTreatmentGui.RESULT_TRT_GUI_PROP, this);
        mainPanel.setLayout(cardLayout);
        mainPanel.setBackground(Color.lightGray);

        editorPane.setContentType("text/html");
        editorPane.setEditable(false);
        JPanel htmlPanel = new JPanel(new BorderLayout());
        htmlPanel.add(new JScrollPane(editorPane), BorderLayout.CENTER);

        mainPanel.add(tableResultPanel, "MODE0");
        mainPanel.add(htmlPanel, "MODE1");
    }


    public JComponent getMainComponent() {
        return mainPanel;
    }


    public String getExecutionListName() {
        return executionListName;
    }


    public JEditorPane getReport() {
        return editorPane;
    }


    public void customizeTitle(JComponent component) {
        component.setFont(new Font(component.getFont().getFontName(),
                                   Font.BOLD,
                                   component.getFont().getSize()));
    }


    public void load() {
    }
}

/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.gui.util;
import net.codjo.gui.toolkit.LabelledItemPanel;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.gui.framework.MutableGuiContext;
import net.codjo.mad.gui.request.DetailDataSource;
import net.codjo.mad.gui.request.util.ButtonPanelLogic;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
/**
 *
 */
public abstract class AbstractDetailWindow extends JInternalFrame {
    private static final String DEFAULT_PAGE_NAME = "Page";
    private ButtonPanelLogic buttonPanelLogic = new ButtonPanelLogic();
    private int fieldsCount = 0;
    private int yCount = 0;
    private DetailDataSource dataSource;
    private LabelledItemPanel currentMainPanel = null;
    private Map<String, LabelledItemPanel> mainPanelList = new HashMap<String, LabelledItemPanel>();
    private JTabbedPane mainTabbedPane = new JTabbedPane();
    private List<String> pageOrder = new ArrayList<String>();
    private Map<String, Integer> yCountMap = new HashMap<String, Integer>();
    private boolean allReadyfocused = false;


    protected AbstractDetailWindow(DetailDataSource dataSource, String title) throws RequestException {
        super(title, true, true, true, false);
        this.dataSource = dataSource;
        setFrameIcon(UIManager.getIcon("icon"));
        setPreferredSize(new Dimension(484, 490));
        buttonPanelLogic.setMainDataSource(dataSource);
        mainTabbedPane.setName(getTitle() + '.' + "TabbedPane");
        currentMainPanel = new LabelledItemPanel();

        declareFields();
        dataSource.load();
        addFormPanels();
        postInitGui();
    }


    protected abstract void declareFields() throws RequestException;


    protected void postInitGui() {
    }


    public MutableGuiContext getGuiContext() {
        return (MutableGuiContext)dataSource.getGuiContext();
    }


    public boolean isUpdateMode() {
        return dataSource.getLoadFactory() != null;
    }


    public DetailDataSource getDataSource() {
        return dataSource;
    }


    public ButtonPanelLogic getButtonPanelLogic() {
        return buttonPanelLogic;
    }


    public LabelledItemPanel getCurrentMainPanel() {
        return currentMainPanel;
    }


    public void addSeparatorToPage(String pageName) {
        addBasicField(pageName, null, new JSeparator());
        yCount++;
    }


    public void addSeparator() {
        addBasicField(null, new JSeparator());
        yCount++;
    }


    public <T extends JComponent> T addComponent(String label, T comp) {
        addBasicField(label, comp);
        yCount++;
        focusFirstComponent(comp);
        return comp;
    }


    public <T extends JComponent> T addComponentToPage(String pageName, String label, T comp) {
        addBasicField(pageName, label, comp);
        yCountMap.put(pageName, yCountMap.get(pageName) + 1);
        focusFirstComponent(comp);
        return comp;
    }


    public JTextField addField(String fieldName, String label) {
        return addField(fieldName, label, new JTextField());
    }


    public <T extends JComponent> T addField(String fieldName, String label, T comp) {
        addBasicField(label, comp);
        dataSource.declare(fieldName, comp);
        comp.setName(getTitle() + '_' + fieldName);
        yCount++;
        focusFirstComponent(comp);
        return comp;
    }


    public <T extends JComponent> T addField(String fieldName, String label, T comp, JButton button) {
        addBasicField(label, comp, button);
        dataSource.declare(fieldName, comp);
        comp.setName(getTitle() + '_' + fieldName);
        button.setName(fieldName + "_Button");
        yCount++;
        focusFirstComponent(comp);
        return comp;
    }


    public <T extends JComponent> T addFieldToPage(String pageName, String fieldName, String label, T comp) {
        addBasicField(pageName, label, comp);
        dataSource.declare(fieldName, comp);
        comp.setName(getTitle() + '_' + fieldName);
        yCountMap.put(pageName, yCountMap.get(pageName) + 1);
        focusFirstComponent(comp);
        return comp;
    }


    public <T extends JComponent> T addFieldToPage(String pageName, String fieldName, String label, T comp,
                                                   JButton button) {
        addBasicField(pageName, label, comp, button);
        dataSource.declare(fieldName, comp);
        comp.setName(getTitle() + '_' + fieldName);
        button.setName(fieldName + "_Button");
        yCountMap.put(pageName, yCountMap.get(pageName) + 1);
        focusFirstComponent(comp);
        return comp;
    }


    private void focusFirstComponent(JComponent comp) {
        if (!allReadyfocused) {
            GuiUtils.setInitialFocus(this, comp);
            allReadyfocused = true;
        }
    }


    private LabelledItemPanel getCurrentPanel() {
        fieldsCount++;
        if (fieldsCount > 10) {
            fieldsCount = 0;
            yCount = 0;
            currentMainPanel = new LabelledItemPanel();
            addCurrentPanel(DEFAULT_PAGE_NAME + ' ' + (mainPanelList.size() + 1), currentMainPanel);
        }
        if (mainPanelList.isEmpty()) {
            currentMainPanel = new LabelledItemPanel();
            addCurrentPanel(DEFAULT_PAGE_NAME + ' ' + (mainPanelList.size() + 1), currentMainPanel);
        }
        return currentMainPanel;
    }


    public LabelledItemPanel getCurrentPanel(String pageName) {
        if (mainPanelList.get(pageName) == null) {
            currentMainPanel = new LabelledItemPanel();
            addCurrentPanel(pageName, currentMainPanel);
        }
        return mainPanelList.get(pageName);
    }


    protected void addBasicField(String label, JComponent comp) {
        if (comp instanceof JTextArea) {
            ((JTextArea)comp).setLineWrap(true);
            ((JTextArea)comp).setWrapStyleWord(true);
            getCurrentPanel().addItem(label, new JScrollPane(comp, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                                                             JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
            ((JTextArea)comp).setCaretPosition(0);
        }
        else if (comp instanceof JSeparator) {
            getCurrentPanel().addItem(comp);
        }
        else {
            getCurrentPanel().addItem(label, comp);
        }
    }


    protected void addBasicField(String pageName, String label, JComponent comp) {
        if (comp instanceof JTextArea) {
            ((JTextArea)comp).setLineWrap(true);
            ((JTextArea)comp).setWrapStyleWord(true);
            getCurrentPanel(pageName).addItem(label,
                                              new JScrollPane(comp, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                                                              JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
            ((JTextArea)comp).setCaretPosition(0);
        }
        else if (comp instanceof JSeparator) {
            getCurrentPanel(pageName).addItem(comp);
        }
        else {
            getCurrentPanel(pageName).addItem(label, comp);
        }
    }


    protected void addBasicField(String label, JComponent comp, JButton button) {
        if (comp instanceof JTextArea) {
            ((JTextArea)comp).setLineWrap(true);
            ((JTextArea)comp).setWrapStyleWord(true);
            getCurrentPanel().addItem(label, new JScrollPane(comp, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                                                             JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
            ((JTextArea)comp).setCaretPosition(0);
        }
        else {
            getCurrentPanel().addItem(label, comp);
            GridBagConstraints itemConstraints = new GridBagConstraints();

            itemConstraints.gridx = 2;
            itemConstraints.gridy = yCount;
            itemConstraints.insets = new Insets(10, 0, 0, 10);
            itemConstraints.weightx = 0.0;
            itemConstraints.anchor = GridBagConstraints.WEST;
            itemConstraints.fill = GridBagConstraints.NONE;
            fieldsCount--;
            getCurrentPanel().add(button, itemConstraints);
        }
    }


    protected void addBasicField(String pageName, String label, JComponent comp, JButton button) {
        if (comp instanceof JTextArea) {
            ((JTextArea)comp).setLineWrap(true);
            ((JTextArea)comp).setWrapStyleWord(true);
            getCurrentPanel(pageName).addItem(label,
                                              new JScrollPane(comp, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                                                              JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
            ((JTextArea)comp).setCaretPosition(0);
        }
        else {
            getCurrentPanel(pageName).addItem(label, comp);
            GridBagConstraints itemConstraints = new GridBagConstraints();

            itemConstraints.gridx = 2;
            itemConstraints.gridy = yCountMap.get(pageName);
            itemConstraints.insets = new Insets(10, 0, 0, 10);
            itemConstraints.weightx = 0.0;
            itemConstraints.anchor = GridBagConstraints.WEST;
            itemConstraints.fill = GridBagConstraints.NONE;

            getCurrentPanel(pageName).add(button, itemConstraints);
        }
    }


    private void addFormPanels() {
        setBackground(UIManager.getColor("Panel.background"));
        if (pageOrder.size() > 1) {
            getContentPane().add(mainTabbedPane, BorderLayout.CENTER);
            for (String pageName : pageOrder) {
                mainTabbedPane.addTab(pageName, mainPanelList.get(pageName));
            }
        }
        else if (pageOrder.size() == 1) {
            LabelledItemPanel panel = mainPanelList.get(pageOrder.get(0));
            panel.setBorder(BorderFactory.createEtchedBorder());
            getContentPane().add(panel, BorderLayout.CENTER);
        }
        getContentPane().add(buttonPanelLogic.getGui(), BorderLayout.SOUTH);
    }


    private void addCurrentPanel(String pageName, LabelledItemPanel labelledItemPanel) {
        mainPanelList.put(pageName, labelledItemPanel);
        yCountMap.put(pageName, 0);
        pageOrder.add(pageName);
    }
}

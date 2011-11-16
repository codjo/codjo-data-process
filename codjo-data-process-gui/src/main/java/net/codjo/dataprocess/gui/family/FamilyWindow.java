package net.codjo.dataprocess.gui.family;
import net.codjo.dataprocess.client.ExecutionListDB;
import net.codjo.dataprocess.client.FamilyClientHelper;
import net.codjo.dataprocess.common.DataProcessConstants;
import net.codjo.dataprocess.gui.selector.FamilyComparator;
import net.codjo.dataprocess.gui.selector.RepositoryComboBox;
import net.codjo.dataprocess.gui.util.GuiContextUtils;
import net.codjo.dataprocess.gui.util.GuiUtils;
import net.codjo.dataprocess.gui.util.InternalInputDialog;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.gui.framework.LocalGuiContext;
import net.codjo.mad.gui.framework.MutableGuiContext;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.AbstractAction;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
/**
 *
 */
public class FamilyWindow {
    private JPanel topPanel;
    private JList guiFamilyList;
    private JButton newFamilyButton;
    private JButton deleteFamilyButton;
    private JButton closeButton;
    private JPanel mainPanel;
    private MutableGuiContext ctxt;
    private JInternalFrame frame;
    private RepositoryComboBox repositoryComboBox;


    public FamilyWindow(MutableGuiContext ctxt, final JInternalFrame frame) {
        this.ctxt = ctxt;
        this.frame = frame;

        repositoryComboBox = new RepositoryComboBox(new LocalGuiContext(ctxt));
        repositoryComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                try {
                    initGuiFamilyList();
                }
                catch (RequestException ex) {
                    GuiUtils.showErrorDialog(frame, getClass(), "Erreur interne", ex);
                }
            }
        });
        try {
            initGuiFamilyList();
        }
        catch (RequestException ex) {
            GuiUtils.showErrorDialog(frame, getClass(), "Erreur interne", ex);
        }

        topPanel.setLayout(new BorderLayout());
        topPanel.add(getRepositoryPanel(), BorderLayout.WEST);

        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cancelButtonActionPerformed();
            }
        });
        newFamilyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                actionNewFamily();
            }
        });
        deleteFamilyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                deleteFamily();
            }
        });
        InputMap inputMap = frame.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        inputMap.put(KeyStroke.getKeyStroke("ESCAPE"), "cancel");
        frame.getActionMap().put("cancel", new AbstractAction() {
            public void actionPerformed(ActionEvent evt) {
                cancelButtonActionPerformed();
            }
        });
    }


    private JPanel getRepositoryPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
        gridBagConstraints1.gridy = 0;
        gridBagConstraints1.ipadx = 0;
        gridBagConstraints1.insets = new Insets(10, 10, 5, 1);
        gridBagConstraints1.gridx = 0;

        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.gridy = 0;
        gridBagConstraints2.ipadx = 100;
        gridBagConstraints2.fill = GridBagConstraints.NONE;
        gridBagConstraints2.insets = new Insets(10, 6, 5, 0);
        gridBagConstraints2.gridx = 1;

        repositoryComboBox.loadData();
        try {
            repositoryComboBox.setSelectedItem(GuiContextUtils.getCurrentRepository(ctxt));
        }
        catch (Exception ex) {
            ;
        }
        panel.add(new JLabel("Référentiel:"), gridBagConstraints1);
        panel.add(repositoryComboBox, gridBagConstraints2);
        return panel;
    }


    private Family getSelectedFamily() {
        return (Family)guiFamilyList.getModel().getElementAt(guiFamilyList.getSelectedIndex());
    }


    private void actionNewFamily() {
        try {
            Map<String, String> map = FamilyClientHelper.getFamilyByRepositoryId(ctxt,
                                                                                 repositoryComboBox.getSelectedRepositoryId());
            FamilyInputDialog familyInputDialog = new FamilyInputDialog(frame, map);
            String familyName = familyInputDialog.input();
            if (familyName == null) {
                return;
            }
            String familyId = FamilyClientHelper.createFamily(ctxt,
                                                              repositoryComboBox.getSelectedRepositoryId(),
                                                              familyName);
            if (DataProcessConstants.FAMILY_ALREADY_EXISTS.equals(familyId)) {
                JOptionPane.showInternalMessageDialog(frame,
                                                      "Il existe déjà une famille nommée '" + familyName
                                                      + "' pour le référentiel de traitement "
                                                      + repositoryComboBox
                                                            .getSelectedValueToDisplay("repositoryName"),
                                                      "Création impossible", JOptionPane.WARNING_MESSAGE);
                return;
            }
            refreshFamilyNameComboBox();
        }
        catch (RequestException ex) {
            GuiUtils.showErrorDialog(frame, getClass(), "Erreur interne", ex);
        }
    }


    private void deleteFamily() {
        int familyId;
        try {
            familyId = Integer.parseInt(getSelectedFamily().getFamilyId());
        }
        catch (Exception e) {
            return;
        }
        try {
            List<ExecutionListDB> executionList = FamilyClientHelper.getExecutionListsUsingFamily(ctxt,
                                                                                                  familyId);
            if (executionList.isEmpty()) {
                int result =
                      JOptionPane.showInternalConfirmDialog(frame,
                                                            "Voulez-vous vraiment supprimer cette famille ?",
                                                            "Confirmation de suppression",
                                                            JOptionPane.YES_NO_OPTION);

                if (result == JOptionPane.YES_OPTION) {
                    FamilyClientHelper.deleteFamily(ctxt, familyId);
                }
            }
            else {
                JOptionPane.showInternalMessageDialog(frame,
                                                      "Cette famille ne peut être supprimée car elle est utilisée par au moins une liste de traitements.\n",
                                                      "Erreur de suppression de famille",
                                                      JOptionPane.ERROR_MESSAGE);
            }
            refreshFamilyNameComboBox();
        }
        catch (RequestException ex) {
            GuiUtils.showErrorDialog(frame, getClass(), "Erreur interne", ex);
        }
    }


    private void refreshFamilyNameComboBox() {
        try {
            initGuiFamilyList();
        }
        catch (RequestException ex) {
            GuiUtils.showErrorDialog(frame, getClass(), "Erreur interne", ex);
        }
    }


    private void cancelButtonActionPerformed() {
        frame.dispose();
    }


    private void initGuiFamilyList() throws RequestException {
        DefaultListModel familyListModel = new DefaultListModel();
        guiFamilyList.setModel(familyListModel);
        guiFamilyList.setSelectionModel(new DefaultListSelectionModel());
        guiFamilyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        guiFamilyList.setCellRenderer(new FamilyListRenderer());

        Map<String, String> familiesMap = FamilyClientHelper
              .getFamilyByRepositoryId(ctxt, repositoryComboBox.getSelectedRepositoryId());
        String[] familyIdTab = familiesMap.keySet().toArray(new String[familiesMap.keySet().size()]);
        Arrays.sort(familyIdTab, new FamilyComparator(familiesMap));

        for (String familyId : familyIdTab) {
            String familyName = familiesMap.get(familyId);
            familyListModel.addElement(new Family(familyId, familyName));
        }
    }


    private static class Family {
        private String familyId;
        private String familyName;


        Family(String id, String name) {
            familyId = id;
            familyName = name;
        }


        public String getFamilyId() {
            return familyId;
        }


        public String getFamilyName() {
            return familyName;
        }
    }

    private static class FamilyInputDialog extends InternalInputDialog {
        private JInternalFrame frame;
        private Map<String, String> familiesMap;


        FamilyInputDialog(JInternalFrame frame, Map<String, String> familiesMap) {
            super(frame, "Ajout d'une nouvelle Famille", "Nom de la nouvelle famille:",
                  UIManager.getIcon("dataprocess.add2"));
            this.frame = frame;
            this.familiesMap = familiesMap;
        }


        @Override
        public String input() {
            String value = super.input();
            if (value == null) {
                return null;
            }
            for (Entry<String, String> entry : familiesMap.entrySet()) {
                String familyName = entry.getValue();
                if (familyName.equals(value)) {
                    JOptionPane.showInternalMessageDialog(frame,
                                                          "Ce nom de famille existe déjà dans ce référentiel de traitement.",
                                                          "Création impossible",
                                                          JOptionPane.ERROR_MESSAGE);
                    return input();
                }
            }
            return value;
        }
    }

    private static class FamilyListRenderer extends JLabel implements ListCellRenderer {
        private DefaultListCellRenderer renderer = new DefaultListCellRenderer();


        public Component getListCellRendererComponent(JList list, Object value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {
            return renderer.getListCellRendererComponent(list, getRendererValue(value),
                                                         index, isSelected, cellHasFocus);
        }


        private static Object getRendererValue(Object value) {
            Family family = (Family)value;
            return family.getFamilyName();
        }
    }


    public JPanel getMainPanel() {
        return mainPanel;
    }
}

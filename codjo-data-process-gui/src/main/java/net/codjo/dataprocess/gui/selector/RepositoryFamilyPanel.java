/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.gui.selector;
import net.codjo.dataprocess.client.RepositoryClientHelper;
import net.codjo.dataprocess.common.exception.RepositoryException;
import net.codjo.dataprocess.gui.util.ComboUpdateEventListener;
import net.codjo.dataprocess.gui.util.GuiContextUtils;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.gui.framework.LocalGuiContext;
import net.codjo.mad.gui.framework.MutableGuiContext;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JLabel;
import javax.swing.JPanel;
/**
 *
 */
public class RepositoryFamilyPanel extends JPanel {
    private FamilyComboBox familyComboBox;
    private JLabel jLabelRepository = new JLabel("Référentiel:");
    private JLabel jLabelFamily = new JLabel("Famille:");
    private ComboUpdateEventListener repositoryEventListener = null;
    private ComboUpdateEventListener familyEventListener = null;
    private int oldRepository;
    private int oldFamily;
    private RepositoryComboBox repositoryComboBox;
    private MutableGuiContext ctxt;
    private boolean enableRepositoryCombobox;
    private boolean isLoading;


    public RepositoryFamilyPanel(MutableGuiContext ctxt,
                                 boolean enableRepositoryCombobox,
                                 boolean showVisibleFamilyOnly) {
        this.ctxt = ctxt;
        this.enableRepositoryCombobox = enableRepositoryCombobox;

        repositoryComboBox = new RepositoryComboBox(new LocalGuiContext(ctxt));
        familyComboBox = new FamilyComboBox(ctxt, showVisibleFamilyOnly);
        buildGui();
    }


    public void load() {
        try {
            isLoading = true;
            repositoryComboBox.loadData();
            if (!enableRepositoryCombobox) {
                String currentRepositoryId = GuiContextUtils.getCurrentRepository(ctxt);
                repositoryComboBox.setSelectedRepository(
                      RepositoryClientHelper.getRepositoryName(ctxt, currentRepositoryId));
            }
            familyComboBox.load(getSelectedRepositoryId());
            setOldValues();
            isLoading = false;
        }
        catch (RepositoryException ex) {
            throw new IllegalStateException("Erreur d'initialisation du composant RepositoryFamilyPanel", ex);
        }
        catch (RequestException ex) {
            throw new IllegalStateException("Erreur d'initialisation du composant RepositoryFamilyPanel", ex);
        }
    }


    public int getOldFamily() {
        return oldFamily;
    }


    public int getOldRepository() {
        return oldRepository;
    }


    public void buildGui() {
        repositoryComboBox.setName("repositoryComboBox");
        familyComboBox.setName("familyComboBox");

        setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
        gridBagConstraints1.gridy = 0;
        gridBagConstraints1.ipadx = 0;
        gridBagConstraints1.insets = new Insets(5, 5, 5, 1);
        gridBagConstraints1.gridx = 0;

        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.gridy = 0;
        gridBagConstraints2.ipadx = 100;
        gridBagConstraints2.fill = GridBagConstraints.NONE;
        gridBagConstraints2.insets = new Insets(5, 6, 5, 0);
        gridBagConstraints2.gridx = 1;

        GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
        gridBagConstraints3.gridy = 0;
        gridBagConstraints3.ipadx = 0;
        gridBagConstraints3.insets = new Insets(5, 10, 5, 1);
        gridBagConstraints3.gridx = 2;

        GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
        gridBagConstraints4.gridy = 0;
        gridBagConstraints4.ipadx = 100;
        gridBagConstraints4.fill = GridBagConstraints.NONE;
        gridBagConstraints4.insets = new Insets(5, 6, 5, 0);
        gridBagConstraints4.gridx = 3;

        GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
        gridBagConstraints5.gridy = 0;
        gridBagConstraints5.ipadx = 0;
        gridBagConstraints5.insets = new Insets(5, 10, 5, 1);
        gridBagConstraints5.gridx = 4;

        GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
        gridBagConstraints6.gridy = 0;
        gridBagConstraints6.ipadx = 100;
        gridBagConstraints6.fill = GridBagConstraints.NONE;
        gridBagConstraints6.insets = new Insets(5, 6, 5, 0);
        gridBagConstraints6.gridx = 5;

        add(jLabelRepository, gridBagConstraints1);
        add(repositoryComboBox, gridBagConstraints2);
        add(jLabelFamily, gridBagConstraints3);
        add(familyComboBox, gridBagConstraints4);

        repositoryComboBox.addActionListener(new RepositoryActionListener());
        familyComboBox.addActionListener(new FamilyActionListener());

        repositoryComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if (!isLoading) {
                    familyComboBox.load(getSelectedRepositoryId());
                }
            }
        });
        repositoryComboBox.setEnabled(enableRepositoryCombobox);
    }


    public int getSelectedRepositoryId() {
        return repositoryComboBox.getSelectedRepositoryId();
    }


    public void setSelectedRepositoryId(MutableGuiContext ctxt, String repositoryId)
          throws RepositoryException, RequestException {
        repositoryComboBox.setSelectedRepository(RepositoryClientHelper.getRepositoryName(ctxt,
                                                                                          repositoryId));
    }


    public int getSelectedFamilyId() {
        return familyComboBox.getSelectedFamilyId();
    }


    public String getSelectedFamilyName() {
        return (String)familyComboBox.getSelectedItem();
    }


    public void addRepositoryEventListener(ComboUpdateEventListener repoEventListener) {
        this.repositoryEventListener = repoEventListener;
    }


    public void addFamilyEventListener(ComboUpdateEventListener familyEvtListener) {
        this.familyEventListener = familyEvtListener;
    }


    public String getSelectedRepositoryName() {
        return repositoryComboBox.getSelectedValueToDisplay("repositoryName");
    }


    private void setOldValues() {
        oldRepository = getSelectedRepositoryId();
        oldFamily = getSelectedFamilyId();
    }


    private class FamilyActionListener implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
            if (familyEventListener != null) {
                familyEventListener.executeUpdate();
            }
            setOldValues();
        }
    }
    private class RepositoryActionListener implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
            if (repositoryEventListener != null) {
                repositoryEventListener.executeUpdate();
            }
            setOldValues();
        }
    }
}

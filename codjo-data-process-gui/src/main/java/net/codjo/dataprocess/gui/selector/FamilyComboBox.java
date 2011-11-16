/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.gui.selector;
import net.codjo.dataprocess.client.FamilyClientHelper;
import net.codjo.dataprocess.gui.util.GenericRenderer;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.gui.framework.MutableGuiContext;
import java.util.Arrays;
import java.util.Map;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
/**
 *
 */
public class FamilyComboBox extends JComboBox {
    private MutableGuiContext ctxt;
    private boolean showVisibleFamilyOnly;
    private boolean isLoading = false;


    public FamilyComboBox(MutableGuiContext ctxt, boolean showVisibleFamilyOnly) {
        this.ctxt = ctxt;
        this.showVisibleFamilyOnly = showVisibleFamilyOnly;
    }


    public boolean isLoading() {
        return isLoading;
    }


    public void load(int repositoryId) {
        isLoading = true;
        try {
            setModel(getFamilyComboBoxModel(repositoryId));
        }
        catch (RequestException ex) {
            throw new IllegalStateException("Erreur d'initialisation du composant FamilyComboBox", ex);
        }
        finally {
            isLoading = false;
        }
    }


    private DefaultComboBoxModel getFamilyComboBoxModel(int repositoryId) throws RequestException {
        Map<String, String> familiesMap = FamilyClientHelper.getFamilyByRepositoryId(ctxt, repositoryId,
                                                                                     showVisibleFamilyOnly);
        GenericRenderer rendererCombo = new GenericRenderer(familiesMap);
        setRenderer(rendererCombo);

        String[] familyIdTab = familiesMap.keySet().toArray(new String[familiesMap.keySet().size()]);
        Arrays.sort(familyIdTab, new FamilyComparator(familiesMap));

        return new DefaultComboBoxModel(familyIdTab);
    }


    public int getSelectedFamilyId() {
        if (getSelectedItem() != null) {
            return Integer.parseInt((String)getSelectedItem());
        }
        return 0;
    }
}

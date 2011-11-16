package net.codjo.dataprocess.gui.launcher.result;
import javax.swing.JComponent;
/**
 *
 */
public interface ResultTreatmentGui {
    static String RESULT_TRT_GUI_PROP = "RESULT_TRT_GUI_PROP";


    JComponent getMainComponent();


    void load();


    String getExecutionListName();


    String getTitle();


    void customizeTitle(JComponent component);
}

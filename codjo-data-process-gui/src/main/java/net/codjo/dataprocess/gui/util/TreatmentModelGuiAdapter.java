package net.codjo.dataprocess.gui.util;
import net.codjo.dataprocess.common.DataProcessConstants;
import net.codjo.dataprocess.common.model.TreatmentModel;
/**
 *
 */
public class TreatmentModelGuiAdapter {
    public static final String SQL = "Requête sql";
    public static final String STORED_PROCEDURE = "Procédure stockée";
    public static final String JAVA_CODE = "Code Java";
    public static final String BEAN_SHELL = "Script java";

    private TreatmentModel treatmentModel;


    public TreatmentModelGuiAdapter(TreatmentModel treatmentModel) {
        this.treatmentModel = treatmentModel;
    }


    public void setType(String type, boolean isReturnResult) {
        if (SQL.equals(type)) {
            if (isReturnResult) {
                treatmentModel.setType(DataProcessConstants.SQL_QUERY_TYPE_WITH_RESULT);
            }
            else {
                treatmentModel.setType(DataProcessConstants.SQL_QUERY_TYPE);
            }
        }
        else if (STORED_PROCEDURE.equals(type)) {
            if (isReturnResult) {
                treatmentModel.setType(DataProcessConstants.STORED_PROC_TYPE_WITH_RESULT);
            }
            else {
                treatmentModel.setType(DataProcessConstants.STORED_PROC_TYPE);
            }
        }
        else if (JAVA_CODE.equals(type)) {
            if (isReturnResult) {
                treatmentModel.setType(DataProcessConstants.JAVA_TYPE_WITH_RESULT);
            }
            else {
                treatmentModel.setType(DataProcessConstants.JAVA_TYPE);
            }
        }
        else if (BEAN_SHELL.equals(type)) {
            if (isReturnResult) {
                treatmentModel.setType(DataProcessConstants.BSH_TYPE_WITH_RESULT);
            }
            else {
                treatmentModel.setType(DataProcessConstants.BSH_TYPE);
            }
        }
    }


    public String getType() {
        String type = treatmentModel.getType();
        if (DataProcessConstants.SQL_QUERY_TYPE.equals(type)
            || DataProcessConstants.SQL_QUERY_TYPE_WITH_RESULT.equals(type)) {
            return SQL;
        }
        else if (DataProcessConstants.STORED_PROC_TYPE.equals(type)
                 || DataProcessConstants.STORED_PROC_TYPE_WITH_RESULT.equals(type)) {
            return STORED_PROCEDURE;
        }
        else if (DataProcessConstants.JAVA_TYPE.equals(type)
                 || DataProcessConstants.JAVA_TYPE_WITH_RESULT.equals(type)) {
            return JAVA_CODE;
        }
        else if (DataProcessConstants.BSH_TYPE.equals(type)
                 || DataProcessConstants.BSH_TYPE_WITH_RESULT.equals(type)) {
            return BEAN_SHELL;
        }
        return "Erreur : Type inconnu";
    }
}

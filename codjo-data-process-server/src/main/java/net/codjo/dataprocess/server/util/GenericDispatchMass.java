package net.codjo.dataprocess.server.util;
import net.codjo.control.common.ControlContext;
import net.codjo.control.common.ControlException;
import net.codjo.control.common.Dictionary;
import net.codjo.control.common.MassControl;
import net.codjo.dataprocess.common.Log;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
/**
 *
 */
public class GenericDispatchMass implements MassControl {
    private String tableName;
    private static final int DEFAULT_BATCH_SIZE = 5000;
    private static final int DEFAULT_ERROR_CODE = 1000;

    public static final String CLEAN_TABLE_TRANSFERT = "clean_table_transfert";
    public static final String CONTINUE_ON_ERROR = "continue_on_error";
    public static final String ERROR_CODE = "error_code";
    public static final String BATCH_SIZE = "batch_size";
    private static final String FINAL_TABLE = "final.table";
    private static final String LINE_OK = "line.ok";
    private static final String OPERATION_KEY_VARIABLE = "operation_key";


    public void setContext(ControlContext context) {
    }


    public void setControlTable(String tableName) {
        this.tableName = tableName;
    }


    public Map<String, String> getSpecialColumnTreatementMap() {
        return new HashMap<String, String>();
    }


    private static String getValueFromDico(Dictionary dico, String name) {
        if (dico.getVariable(name) != null) {
            return dico.getVariable(name).getValue();
        }
        return null;
    }


    public void control(Connection con, Dictionary dico) throws ControlException {
        boolean bCleanTableTransfert;
        boolean bContinueOnError;

        String dest = dico.getParent().getVariable(FINAL_TABLE).getValue();
        String where = dico.getParent().getVariable(LINE_OK).getValue();

        String batchSize = getValueFromDico(dico.getParent(), BATCH_SIZE);
        String errorCode = getValueFromDico(dico.getParent(), ERROR_CODE);
        String cleanTableTransfert = getValueFromDico(dico, CLEAN_TABLE_TRANSFERT);
        String continueOnError = getValueFromDico(dico, CONTINUE_ON_ERROR);

        bCleanTableTransfert = cleanTableTransfert == null || "true".equalsIgnoreCase(cleanTableTransfert);
        bContinueOnError = "true".equalsIgnoreCase(continueOnError);

        if (Log.isDebugEnabled()) {
            Log.debug(getClass(), "Variable " + ERROR_CODE + " = " + errorCode);
            Log.debug(getClass(), "Variable " + BATCH_SIZE + " = " + batchSize);
            Log.debug(getClass(), "Variable " + CLEAN_TABLE_TRANSFERT + " =  " + cleanTableTransfert);
            Log.debug(getClass(), "Variable " + CONTINUE_ON_ERROR + " = " + continueOnError);
        }

        SQLUtilCopyTable.ResultCopyTable resultCopyTable = new SQLUtilCopyTable.ResultCopyTable();
        SQLUtilCopyTable.copyTable(con, tableName, "QUARANTINE_ID", dest, where,
                                   batchSize != null ?
                                   Integer.parseInt(batchSize) :
                                   DEFAULT_BATCH_SIZE,
                                   getSpecialColumnTreatementMap(),
                                   bCleanTableTransfert,
                                   resultCopyTable);
        if (!bCleanTableTransfert) {
            dico.getParent().addVariable(OPERATION_KEY_VARIABLE, Long.toString(resultCopyTable.getKey()));
            if (Log.isDebugEnabled()) {
                Log.debug(getClass(),
                          "Ajout dans le dictionary du plan d'intégration de '" + OPERATION_KEY_VARIABLE
                          + "' = " + Long.toString(resultCopyTable.getKey()));
            }
        }

        if (resultCopyTable.getException() != null) {
            Log.error(getClass(), resultCopyTable.getException());

            if (!bContinueOnError) {
                throw new ControlException(
                      errorCode != null ? Integer.parseInt(errorCode) : DEFAULT_ERROR_CODE, "",
                      resultCopyTable.getException());
            }
        }
    }
}

package net.codjo.dataprocess.server.control;
import net.codjo.control.common.AbstractControl;
import net.codjo.control.common.ControlException;
import net.codjo.control.common.Dictionary;
import java.sql.Connection;
/**
 *
 */
public class DataProcessJavaControl extends AbstractControl {
    private String tableName;
    private DataProcessControl dataProcessControl;


    public DataProcessJavaControl(String repositoryName, String treatmentId) {
        dataProcessControl = new DataProcessControl(repositoryName, treatmentId);
    }


    public void control(Object obj, Dictionary dico) throws ControlException {
        Connection con = getContext().getConnection();
        String localTableName = dataProcessControl.getValueFromDico(dico, DataProcessControl.FINAL_TABLE);
        if (localTableName == null) {
            throw new IllegalArgumentException(
                  "L'une des entree du dictionnaire est nulle (cf. plan d'integration) :\n"
                  + DataProcessControl.FINAL_TABLE + " = " + localTableName);
        }
        setTableName(localTableName);
        dataProcessControl.control(con, dico, new Object[]{new JavaControlParam(dico, getTableName(),
                                                                                getErrorCode(), obj)});
    }


    public void setTableName(String value) {
        this.tableName = value;
    }


    public String getTableName() {
        return tableName;
    }


    protected DataProcessControl getDataProcessControl() {
        return dataProcessControl;
    }


    public static class JavaControlParam {
        private Dictionary dico;
        private String tableName;
        private int errorCode;
        private Object obj;


        public JavaControlParam(Dictionary dico, String tableName, int errorCode, Object obj) {
            this.dico = dico;
            this.tableName = tableName;
            this.errorCode = errorCode;
            this.obj = obj;
        }


        public Dictionary getDico() {
            return dico;
        }


        public String getTableName() {
            return tableName;
        }


        public int getErrorCode() {
            return errorCode;
        }


        public Object getObj() {
            return obj;
        }
    }
}
package net.codjo.dataprocess.server.control;
import net.codjo.control.common.ControlContext;
import net.codjo.control.common.ControlException;
import net.codjo.control.common.Dictionary;
import net.codjo.control.common.MassControl;
import java.sql.Connection;
/**
 *
 */
public class DataProcessMassControl implements MassControl {
    private String controlTable;
    private DataProcessControl dataProcessControl;


    public DataProcessMassControl(String repositoryName, String treatmentId) {
        dataProcessControl = new DataProcessControl(repositoryName, treatmentId);
    }


    public void setContext(ControlContext context) {
    }


    public void setControlTable(String tabName) {
        controlTable = tabName;
    }


    protected DataProcessControl getDataProcessControl() {
        return dataProcessControl;
    }


    public void control(Connection con, Dictionary dico) throws ControlException {
        int errorCode = Integer.valueOf(dataProcessControl.getValueFromDico(dico,
                                                                            DataProcessControl.ERROR_CODE));
        dataProcessControl.control(con, dico,
                                   new Object[]{new MassControlParam(dico, controlTable, errorCode)});
    }


    public static class MassControlParam {
        private Dictionary dico;
        private String controlTable;
        private int errorCode;


        public MassControlParam(Dictionary dico, String controlTable, int errorCode) {
            this.dico = dico;
            this.controlTable = controlTable;
            this.errorCode = errorCode;
        }


        public Dictionary getDico() {
            return dico;
        }


        public String getControlTable() {
            return controlTable;
        }


        public int getErrorCode() {
            return errorCode;
        }
    }
}
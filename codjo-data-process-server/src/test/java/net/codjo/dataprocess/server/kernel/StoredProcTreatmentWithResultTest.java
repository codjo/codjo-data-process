package net.codjo.dataprocess.server.kernel;
import net.codjo.dataprocess.common.DataProcessConstants;
import net.codjo.dataprocess.common.context.DataProcessContext;
import net.codjo.dataprocess.common.exception.TreatmentException;
import net.codjo.dataprocess.common.model.ArgList;
import net.codjo.dataprocess.common.model.ArgModel;
import net.codjo.dataprocess.common.model.TreatmentModel;
import net.codjo.test.common.mock.ConnectionMock;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

import static net.codjo.test.common.matcher.JUnitMatchers.*;
/**
 *
 */
public class StoredProcTreatmentWithResultTest {

    @Test
    public void buildStoredProcQuery() {
        ArgList argList = new ArgList();
        TreatmentModel treatmentModel = new TreatmentModel();
        treatmentModel.setTarget("sp_maproc");
        treatmentModel.setType(DataProcessConstants.STORED_PROC_TYPE_WITH_RESULT);

        argList.setArgs(buildArgs());
        treatmentModel.setArguments(argList);

        AbstractSqlTreatment abstractSqlTreatment = new StoredProcTreatment();
        abstractSqlTreatment.setTreatmentModel(treatmentModel);
        abstractSqlTreatment.buildArgument();
        String spQuery = abstractSqlTreatment.buildStoredProcQuery();

        assertThat(spQuery, equalTo("{call sp_maproc argName1 = ? ,argName2 = ? ,argName3 = ? }"));
    }


    @Test
    public void proceedTreatmentEmpty() throws TreatmentException, SQLException {
        DataProcessContext context = new DataProcessContext();
        ArgList argList = new ArgList();
        TreatmentModel treatmentModel = new TreatmentModel();
        treatmentModel.setTarget(" ");
        treatmentModel.setType(DataProcessConstants.STORED_PROC_TYPE_WITH_RESULT);
        argList.setArgs(buildArgs());
        treatmentModel.setArguments(argList);

        AbstractTreatment abstractTreatment =
              TreatmentFactory.buildTreatment(new ConnectionMock(), treatmentModel, 0, null);
        abstractTreatment.configure(context);
        Object result = abstractTreatment.proceedTreatment(context);
        assertThat(result instanceof String, equalTo(true));
        if (result instanceof String) {
            assertThat(result.toString(), equalTo(""));
        }
    }


    private static List<ArgModel> buildArgs() {
        List<ArgModel> list = new ArrayList<ArgModel>();
        list.add(new ArgModel("argName1", "strValue", 1, Types.VARCHAR));
        list.add(new ArgModel("argName2", "2.5", 2, Types.NUMERIC));
        list.add(new ArgModel("argName3", "2003-12-31", 3, Types.DATE));
        return list;
    }
}

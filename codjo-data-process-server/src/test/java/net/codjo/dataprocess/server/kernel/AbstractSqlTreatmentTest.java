package net.codjo.dataprocess.server.kernel;
import net.codjo.dataprocess.common.DataProcessConstants;
import net.codjo.dataprocess.common.model.ArgList;
import net.codjo.dataprocess.common.model.ArgModel;
import net.codjo.dataprocess.common.model.TreatmentModel;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

import static net.codjo.test.common.matcher.JUnitMatchers.*;
/**
 *
 */
public class AbstractSqlTreatmentTest {

    @Test
    public void prepareArgumentsForProcStock() {
        ArgList argList = new ArgList();

        TreatmentModel treatmentModel = new TreatmentModel();
        treatmentModel.setTarget("sp_maproc");
        treatmentModel.setType(DataProcessConstants.STORED_PROC_TYPE);

        argList.setArgs(buildArgs());
        treatmentModel.setArguments(argList);

        AbstractSqlTreatment abstractSqlTreatment = new StoredProcTreatment();
        abstractSqlTreatment.setTreatmentModel(treatmentModel);
        abstractSqlTreatment.buildArgument();

        assertThat(abstractSqlTreatment.prepareArgumentsForProcStock(),
                   equalTo("argName1 = ? ,argName2 = ? ,argName3 = ? "));
    }


    private static List<ArgModel> buildArgs() {
        List<ArgModel> list = new ArrayList<ArgModel>();
        list.add(new ArgModel("argName1", "value", 1, Types.VARCHAR));
        list.add(new ArgModel("argName2", "5", 2, Types.NUMERIC));
        list.add(new ArgModel("argName3", "2010-09-30", 3, Types.DATE));
        return list;
    }
}

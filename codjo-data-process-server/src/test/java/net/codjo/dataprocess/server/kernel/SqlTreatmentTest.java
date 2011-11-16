/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.server.kernel;
import net.codjo.database.common.api.structure.SqlTable;
import net.codjo.dataprocess.common.DataProcessConstants;
import net.codjo.dataprocess.common.context.DataProcessContext;
import net.codjo.dataprocess.common.model.ArgList;
import net.codjo.dataprocess.common.model.ArgModel;
import net.codjo.dataprocess.common.model.TreatmentModel;
import net.codjo.tokio.TokioFixture;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static net.codjo.test.common.matcher.JUnitMatchers.*;
/**
 *
 */
public class SqlTreatmentTest {
    private TokioFixture fixture = new TokioFixture(getClass());


    @Before
    public void before() throws Exception {
        fixture.doSetUp();
        fixture.getJdbcFixture().advanced().dropAllObjects();
    }


    @After
    public void after() throws Exception {
        fixture.doTearDown();
    }


    private void createTableTest() throws SQLException {
        fixture.getJdbcFixture().create(SqlTable.temporaryTable("T_TEMP"),
                                        "CODE_PORTEFEUILLE varchar(20) null, QUANTITE numeric(25,5) default 0  null");
    }


    @Test
    public void proceedTreatment() throws Exception {
        String sqlQuery = "insert into #T_TEMP (CODE_PORTEFEUILLE, QUANTITE) values (?, ?)";

        DataProcessContext context = new DataProcessContext();
        context.setProperty("portfolioCode", "MIMI");

        createTableTest();
        fixture.insertInputInDb("SqlTreatment");

        ArgList argList = new ArgList();
        TreatmentModel treatmentModel = new TreatmentModel();
        treatmentModel.setTarget(sqlQuery);
        treatmentModel.setType(DataProcessConstants.SQL_QUERY_TYPE);
        argList.setArgs(buildArgsForQuery());
        treatmentModel.setArguments(argList);

        AbstractTreatment abstractTreatment =
              TreatmentFactory.buildTreatment(fixture.getConnection(), treatmentModel, 0, null);

        abstractTreatment.configure(context);
        Object result = abstractTreatment.proceedTreatment(context);
        assertThat(result instanceof Integer, equalTo(true));
        if (result instanceof Integer) {
            assertThat((Integer)result, equalTo(1));
        }
        fixture.assertAllOutputs("SqlTreatment");
    }


    @Test
    public void proceedTreatmentEmpty() throws Exception {
        DataProcessContext context = new DataProcessContext();
        ArgList argList = new ArgList();
        TreatmentModel treatmentModel = new TreatmentModel();
        treatmentModel.setTarget(" ");
        treatmentModel.setType(DataProcessConstants.SQL_QUERY_TYPE);
        argList.setArgs(buildArgsForQuery());
        treatmentModel.setArguments(argList);

        AbstractTreatment abstractTreatment =
              TreatmentFactory.buildTreatment(fixture.getConnection(), treatmentModel, 0, null);
        abstractTreatment.configure(context);
        Object result = abstractTreatment.proceedTreatment(context);
        assertThat(result instanceof Integer, equalTo(true));
        if (result instanceof Integer) {
            assertThat((Integer)result, equalTo(0));
        }
    }


    private static List<ArgModel> buildArgsForQuery() {
        List<ArgModel> list = new ArrayList<ArgModel>();
        list.add(new ArgModel("CODE_PORTEFEUILLE", "$portfolioCode$", 1, Types.VARCHAR));
        list.add(new ArgModel("QUANTITE", "0.12345", 2, Types.NUMERIC));
        return list;
    }
}

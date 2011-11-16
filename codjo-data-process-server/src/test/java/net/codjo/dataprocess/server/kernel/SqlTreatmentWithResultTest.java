/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.server.kernel;
import net.codjo.database.common.api.JdbcFixture;
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
public class SqlTreatmentWithResultTest {
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


    @Test
    public void proceedTreatment() throws Exception {
        createTableTest();

        DataProcessContext context = new DataProcessContext();
        context.setProperty("separator_value", ", ");
        context.setProperty("quote_value", "\"");
        context.setProperty("column_value", "true");

        ArgList argList = new ArgList();
        TreatmentModel treatmentModel = new TreatmentModel();
        treatmentModel.setTarget("select * from #T_TEMP");
        treatmentModel.setType(DataProcessConstants.SQL_QUERY_TYPE_WITH_RESULT);
        argList.setArgs(buildArgsForExport());
        treatmentModel.setArguments(argList);

        AbstractTreatment abstractTreatment =
              TreatmentFactory.buildTreatment(fixture.getConnection(), treatmentModel, 1, null);
        abstractTreatment.configure(context);
        Object result = abstractTreatment.proceedTreatment(context);
        assertThat(result instanceof String, equalTo(true));
        assertThat(result.toString(), equalTo(
              "\"CODE_PORTEFEUILLE\", \"QUANTITE\"\n\"CP1\", \"10.00000\"\n\"CP2\", \"20.00000\"\n"));
    }


    @Test
    public void proceedTreatmentEmpty() throws Exception {
        DataProcessContext context = new DataProcessContext();
        ArgList argList = new ArgList();
        TreatmentModel treatmentModel = new TreatmentModel();
        treatmentModel.setTarget("   ");
        treatmentModel.setType(DataProcessConstants.SQL_QUERY_TYPE_WITH_RESULT);
        argList.setArgs(buildArgsForExport());
        treatmentModel.setArguments(argList);

        AbstractTreatment abstractTreatment =
              TreatmentFactory.buildTreatment(fixture.getConnection(), treatmentModel, 1, null);
        abstractTreatment.configure(context);
        Object result = abstractTreatment.proceedTreatment(context);
        assertThat(result instanceof String, equalTo(true));
        assertThat(result.toString(), equalTo(""));
    }


    @Test
    public void getTemporaryTables() {
        String fakeSql =
              "select #table_temp.mon_champs, '#tatata' from #table_temp inner join #table_temp2 "
              + "on #table_temp.champ1 = #table_temp2.champ1";
        SqlTreatmentWithResult sqlTreatmentWithResult = new SqlTreatmentWithResult();
        List<String> result = sqlTreatmentWithResult.getTemporaryTables(fakeSql);
        assertThat(result.toString(), equalTo("[#table_temp, #table_temp2]"));
    }


    private void createTableTest() throws SQLException {
        JdbcFixture jdbcFixture = fixture.getJdbcFixture();
        jdbcFixture.create(SqlTable.temporaryTable("T_TEMP"),
                           "CODE_PORTEFEUILLE varchar(20)  null,  QUANTITE numeric(25,5) default 0  null");
        jdbcFixture.executeUpdate("insert into #T_TEMP (CODE_PORTEFEUILLE, QUANTITE) values('CP1', 10)");
        jdbcFixture.executeUpdate("insert into #T_TEMP (CODE_PORTEFEUILLE, QUANTITE) values('CP2', 20)");
    }


    private static List<ArgModel> buildArgsForExport() {
        List<ArgModel> list = new ArrayList<ArgModel>();
        list.add(new ArgModel("[separator]", "$separator_value$", 1, Types.VARCHAR));
        list.add(new ArgModel("[quote]", "$quote_value$", 2, Types.VARCHAR));
        list.add(new ArgModel("[with_column_header]", "true", 3, Types.VARCHAR));
        return list;
    }
}

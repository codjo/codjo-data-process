package net.codjo.dataprocess.server.kernel;
import net.codjo.database.common.api.DatabaseFactory;
import net.codjo.database.common.api.JdbcFixture;
import net.codjo.dataprocess.common.DataProcessConstants;
import net.codjo.dataprocess.common.context.DataProcessContext;
import net.codjo.dataprocess.common.exception.TreatmentException;
import net.codjo.dataprocess.common.model.ArgList;
import net.codjo.dataprocess.common.model.ArgModel;
import net.codjo.dataprocess.common.model.ExecutionListModel;
import net.codjo.dataprocess.common.model.TreatmentModel;
import java.sql.Connection;
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
public class TreatmentFactoryTest {
    private JdbcFixture jdbc;


    @Before
    public void before() throws Exception {
        jdbc = new DatabaseFactory().createJdbcFixture();
        jdbc.doSetUp();
    }


    @After
    public void after() {
        jdbc.doTearDown();
    }


    @Test
    public void sqlQueryType() throws Exception {
        Connection con = jdbc.getConnection();
        TreatmentModel treatmentModel = new TreatmentModel();
        treatmentModel.setId("traitement 1");
        treatmentModel.setType(DataProcessConstants.SQL_QUERY_TYPE);
        ArgList argList = buildArgsForQuery();
        treatmentModel.setArguments(argList);

        ExecutionListModel executionListModel = new ExecutionListModel();
        AbstractTreatment abstractTreatment = TreatmentFactory
              .buildTreatment(con, treatmentModel, 1, executionListModel);

        assertThat(abstractTreatment.getConnection(), is(sameInstance(con)));
        assertThat(abstractTreatment.getExecutionListModel(), is(sameInstance(executionListModel)));
        assertThat(abstractTreatment.getRepositoryId(), equalTo(1));
        assertThat(abstractTreatment.getTreatmentModel(), is(sameInstance(treatmentModel)));

        assertThat(abstractTreatment.getArgument("1"), equalTo("$portfolioCode$"));
        assertThat(abstractTreatment.getArgument("2"), equalTo("0.12345"));
    }


    @Test
    public void sqlQueryTypeWithResult() throws Exception {
        Connection con = jdbc.getConnection();
        TreatmentModel treatmentModel = new TreatmentModel();
        treatmentModel.setId("traitement 1");
        treatmentModel.setType(DataProcessConstants.SQL_QUERY_TYPE_WITH_RESULT);
        ArgList argList = buildArgsForQuery();
        treatmentModel.setArguments(argList);

        ExecutionListModel executionListModel = new ExecutionListModel();
        AbstractTreatment abstractTreatment = TreatmentFactory
              .buildTreatment(con, treatmentModel, 1, executionListModel);

        assertThat(abstractTreatment.getConnection(), is(sameInstance(con)));
        assertThat(abstractTreatment.getExecutionListModel(), is(sameInstance(executionListModel)));
        assertThat(abstractTreatment.getRepositoryId(), equalTo(1));
        assertThat(abstractTreatment.getTreatmentModel(), is(sameInstance(treatmentModel)));

        assertThat(abstractTreatment.getArgument("1"), equalTo("$portfolioCode$"));
        assertThat(abstractTreatment.getArgument("2"), equalTo("0.12345"));
    }


    @Test
    public void bsh() throws Exception {
        Connection con = jdbc.getConnection();
        TreatmentModel treatmentModel = new TreatmentModel();
        treatmentModel.setId("traitement 1");
        treatmentModel.setType(DataProcessConstants.BSH_TYPE);
        treatmentModel.setTarget("int a = 5;");
        ArgList argList = buildArgsForQuery();
        treatmentModel.setArguments(argList);

        ExecutionListModel executionListModel = new ExecutionListModel();
        AbstractTreatment abstractTreatment = TreatmentFactory
              .buildTreatment(con, treatmentModel, 1, executionListModel);

        assertThat(abstractTreatment.getConnection(), is(sameInstance(con)));
        assertThat(abstractTreatment.getExecutionListModel(), is(sameInstance(executionListModel)));
        assertThat(abstractTreatment.getRepositoryId(), equalTo(1));
        assertThat(abstractTreatment.getTreatmentModel(), is(sameInstance(treatmentModel)));

        DataProcessContext context = new DataProcessContext();
        context.setProperty("portfolioCode", "CP1");
        abstractTreatment.configure(context);

        assertThat(abstractTreatment.getArgument("CODE_VALEUR"), nullValue());
        assertThat(abstractTreatment.getArgument("CODE_PORTEFEUILLE"), equalTo("CP1"));
        assertThat(abstractTreatment.getArgument("QUANTITE"), equalTo("0.12345"));
    }


    @Test
    public void bshWithResult() throws Exception {
        Connection con = jdbc.getConnection();
        TreatmentModel treatmentModel = new TreatmentModel();
        treatmentModel.setId("traitement 2");
        treatmentModel.setType(DataProcessConstants.BSH_TYPE_WITH_RESULT);
        treatmentModel.setTarget("int b = 6;");
        ArgList argList = buildArgsForQuery();
        treatmentModel.setArguments(argList);

        ExecutionListModel executionListModel = new ExecutionListModel();
        AbstractTreatment abstractTreatment = TreatmentFactory
              .buildTreatment(con, treatmentModel, 2, executionListModel);

        assertThat(abstractTreatment.getConnection(), is(sameInstance(con)));
        assertThat(abstractTreatment.getExecutionListModel(), is(sameInstance(executionListModel)));
        assertThat(abstractTreatment.getRepositoryId(), equalTo(2));
        assertThat(abstractTreatment.getTreatmentModel(), is(sameInstance(treatmentModel)));

        DataProcessContext context = new DataProcessContext();
        context.setProperty("portfolioCode", "CP2");
        abstractTreatment.configure(context);

        assertThat(abstractTreatment.getArgument("CODE_VALEUR"), nullValue());
        assertThat(abstractTreatment.getArgument("CODE_PORTEFEUILLE"), equalTo("CP2"));
        assertThat(abstractTreatment.getArgument("QUANTITE"), equalTo("0.12345"));
    }


    @Test
    public void javaType() throws Exception {
        Connection con = jdbc.getConnection();
        TreatmentModel treatmentModel = new TreatmentModel();
        treatmentModel.setId("traitement 3");
        treatmentModel.setType(DataProcessConstants.JAVA_TYPE);
        treatmentModel.setTarget(JavaTreatmentExemple.class.getName());
        ArgList argList = buildArgsForQuery();
        treatmentModel.setArguments(argList);

        ExecutionListModel executionListModel = new ExecutionListModel();
        AbstractTreatment abstractTreatment = TreatmentFactory
              .buildTreatment(con, treatmentModel, 3, executionListModel);

        assertThat(abstractTreatment.getConnection(), is(sameInstance(con)));
        assertThat(abstractTreatment.getExecutionListModel(), is(sameInstance(executionListModel)));
        assertThat(abstractTreatment.getRepositoryId(), equalTo(3));
        assertThat(abstractTreatment.getTreatmentModel(), is(sameInstance(treatmentModel)));

        DataProcessContext context = new DataProcessContext();
        context.setProperty("portfolioCode", "CP1");
        abstractTreatment.configure(context);

        assertThat(abstractTreatment.getArgument("CODE_VALEUR"), nullValue());
        assertThat(abstractTreatment.getArgument("CODE_PORTEFEUILLE"), equalTo("CP1"));
        assertThat(abstractTreatment.getArgument("QUANTITE"), equalTo("0.12345"));
    }


    @Test
    public void javaTypeWithResult() throws Exception {
        Connection con = jdbc.getConnection();
        TreatmentModel treatmentModel = new TreatmentModel();
        treatmentModel.setId("traitement 4");
        treatmentModel.setType(DataProcessConstants.JAVA_TYPE_WITH_RESULT);
        treatmentModel.setTarget(JavaTreatmentExemple.class.getName());
        ArgList argList = buildArgsForQuery();
        treatmentModel.setArguments(argList);

        ExecutionListModel executionListModel = new ExecutionListModel();
        AbstractTreatment abstractTreatment = TreatmentFactory
              .buildTreatment(con, treatmentModel, 4, executionListModel);

        assertThat(abstractTreatment.getConnection(), is(sameInstance(con)));
        assertThat(abstractTreatment.getExecutionListModel(), is(sameInstance(executionListModel)));
        assertThat(abstractTreatment.getRepositoryId(), equalTo(4));
        assertThat(abstractTreatment.getTreatmentModel(), is(sameInstance(treatmentModel)));

        DataProcessContext context = new DataProcessContext();
        context.setProperty("portfolioCode", "CP4");
        abstractTreatment.configure(context);

        assertThat(abstractTreatment.getArgument("CODE_VALEUR"), nullValue());
        assertThat(abstractTreatment.getArgument("CODE_PORTEFEUILLE"), equalTo("CP4"));
        assertThat(abstractTreatment.getArgument("QUANTITE"), equalTo("0.12345"));
    }


    @Test
    public void storedProcType() throws Exception {
        Connection con = jdbc.getConnection();
        TreatmentModel treatmentModel = new TreatmentModel();
        treatmentModel.setId("traitement 5");
        treatmentModel.setType(DataProcessConstants.STORED_PROC_TYPE);
        ArgList argList = buildArgsForQuery();
        treatmentModel.setArguments(argList);

        ExecutionListModel executionListModel = new ExecutionListModel();
        AbstractTreatment abstractTreatment = TreatmentFactory
              .buildTreatment(con, treatmentModel, 5, executionListModel);

        assertThat(abstractTreatment.getConnection(), is(sameInstance(con)));
        assertThat(abstractTreatment.getExecutionListModel(), is(sameInstance(executionListModel)));
        assertThat(abstractTreatment.getRepositoryId(), equalTo(5));
        assertThat(abstractTreatment.getTreatmentModel(), is(sameInstance(treatmentModel)));

        assertThat(abstractTreatment.getArgument("1"), equalTo("$portfolioCode$"));
        assertThat(abstractTreatment.getArgument("2"), equalTo("0.12345"));
    }


    @Test
    public void storedProcTypeWithResult() throws Exception {
        Connection con = jdbc.getConnection();
        TreatmentModel treatmentModel = new TreatmentModel();
        treatmentModel.setId("traitement 6");
        treatmentModel.setType(DataProcessConstants.STORED_PROC_TYPE_WITH_RESULT);
        ArgList argList = buildArgsForQuery();
        treatmentModel.setArguments(argList);

        ExecutionListModel executionListModel = new ExecutionListModel();
        AbstractTreatment abstractTreatment = TreatmentFactory
              .buildTreatment(con, treatmentModel, 6, executionListModel);

        assertThat(abstractTreatment.getConnection(), is(sameInstance(con)));
        assertThat(abstractTreatment.getExecutionListModel(), is(sameInstance(executionListModel)));
        assertThat(abstractTreatment.getRepositoryId(), equalTo(6));
        assertThat(abstractTreatment.getTreatmentModel(), is(sameInstance(treatmentModel)));

        assertThat(abstractTreatment.getArgument("1"), equalTo("$portfolioCode$"));
        assertThat(abstractTreatment.getArgument("2"), equalTo("0.12345"));
    }


    @Test
    public void testUnknowType() throws Exception {
        Connection con = jdbc.getConnection();
        TreatmentModel treatmentModel = new TreatmentModel();
        treatmentModel.setId("traitement 7");
        treatmentModel.setType("bob");
        ArgList argList = buildArgsForQuery();
        treatmentModel.setArguments(argList);

        ExecutionListModel executionListModel = new ExecutionListModel();
        try {
            TreatmentFactory.buildTreatment(con, treatmentModel, 6, executionListModel);
            fail("Une exception était attendue !");
        }
        catch (TreatmentException ex) {
            assertThat(ex.getLocalizedMessage(), equalTo("Type de traitement inconnu : bob"));
        }
    }


    @Test
    public void testClassNotFound() throws Exception {
        Connection con = jdbc.getConnection();
        TreatmentModel treatmentModel = new TreatmentModel();
        treatmentModel.setId("traitement 7");
        treatmentModel.setType(DataProcessConstants.JAVA_TYPE);
        treatmentModel.setTarget("com.class.inexistante");
        ArgList argList = buildArgsForQuery();
        treatmentModel.setArguments(argList);

        ExecutionListModel executionListModel = new ExecutionListModel();
        try {
            TreatmentFactory.buildTreatment(con, treatmentModel, 7, executionListModel);
            fail("Une exception était attendue !");
        }
        catch (TreatmentException ex) {
            assertThat(ex.getLocalizedMessage(), equalTo(
                  "La classe 'com.class.inexistante' du traitement java 'traitement 7' n'existe pas, causée par: com.class.inexistante"));
        }
    }


    @Test
    public void testClassNotInstanciable() throws Exception {
        Connection con = jdbc.getConnection();
        TreatmentModel treatmentModel = new TreatmentModel();
        treatmentModel.setId("traitement 7");
        treatmentModel.setType(DataProcessConstants.JAVA_TYPE);
        treatmentModel.setTarget(JavaTreatmentExemple2.class.getName());
        ArgList argList = buildArgsForQuery();
        treatmentModel.setArguments(argList);

        ExecutionListModel executionListModel = new ExecutionListModel();
        try {
            TreatmentFactory.buildTreatment(con, treatmentModel, 7, executionListModel);
            fail("Une exception était attendue !");
        }
        catch (TreatmentException ex) {
            assertThat(ex.getLocalizedMessage(), equalTo(
                  "Problème d'instanciation/accessibilité de la classe 'net.codjo.dataprocess.server.kernel.JavaTreatmentExemple2' du traitement java 'traitement 7', causée par: Class net.codjo.dataprocess.server.kernel.TreatmentFactory can not access a member of class net.codjo.dataprocess.server.kernel.JavaTreatmentExemple2 with modifiers \"private\""));
        }
    }


    @Test
    public void getNotResolvableArgument() throws Exception {
        Connection con = jdbc.getConnection();
        TreatmentModel treatmentModel = new TreatmentModel();
        treatmentModel.setId("traitement 4");
        treatmentModel.setType(DataProcessConstants.JAVA_TYPE);
        treatmentModel.setTarget(JavaTreatmentExemple.class.getName());
        ArgList argList = buildArgsForQuery();
        treatmentModel.setArguments(argList);

        ExecutionListModel executionListModel = new ExecutionListModel();
        AbstractTreatment abstractTreatment = TreatmentFactory
              .buildTreatment(con, treatmentModel, 4, executionListModel);

        DataProcessContext context = new DataProcessContext();
        context.setProperty("periode", "200512");

        assertThat(abstractTreatment.getArgument("CODE_VALEUR"), nullValue());
        assertThat(abstractTreatment.getArgument("CODE_PORTEFEUILLE"), equalTo("$portfolioCode$"));
        assertThat(abstractTreatment.getArgument("QUANTITE"), equalTo("0.12345"));

        List<String> list = abstractTreatment.getNotResolvableArguments(context);

        assertThat(list.contains("Le paramètre 'code' n'est pas configuré."), equalTo(true));
        assertThat(list.contains("Le paramètre 'portfolioCode' n'est pas configuré."), equalTo(true));
    }


    private static ArgList buildArgsForQuery() {
        ArgList argList = new ArgList();

        List<ArgModel> list = new ArrayList<ArgModel>();
        list.add(new ArgModel("CODE_PORTEFEUILLE", "$portfolioCode$", 1, Types.VARCHAR));
        list.add(new ArgModel("QUANTITE", "0.12345", 2, Types.NUMERIC));
        list.add(new ArgModel("CODE", "$code$", 3, Types.NUMERIC));

        argList.setArgs(list);
        return argList;
    }
}

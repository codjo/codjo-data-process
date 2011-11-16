package net.codjo.dataprocess.server.kernel;
import net.codjo.database.common.api.DatabaseFactory;
import net.codjo.database.common.api.JdbcFixture;
import net.codjo.dataprocess.common.DataProcessConstants;
import net.codjo.dataprocess.common.context.DataProcessContext;
import net.codjo.dataprocess.common.exception.TreatmentException;
import net.codjo.dataprocess.common.model.ArgList;
import net.codjo.dataprocess.common.model.ArgModel;
import net.codjo.dataprocess.common.model.TreatmentModel;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static net.codjo.test.common.matcher.JUnitMatchers.*;
public class BshScriptTreatmentTest {
    private JdbcFixture fixture;


    @Before
    public void before() throws Exception {
        fixture = new DatabaseFactory().createJdbcFixture();
        fixture.doSetUp();
    }


    @After
    public void after() {
        fixture.doTearDown();
    }


    @Test
    public void basic() throws Exception {
        String code = "RESULT = Integer.toString(5 * 2);";

        DataProcessContext context = new DataProcessContext();
        AbstractTreatment abstractTreatment = createTreatment(code, context);
        Object result = abstractTreatment.proceedTreatment(context);
        assertThat(result.toString(), equalTo("10"));
    }


    @Test
    public void treatmentException() {
        String code = "import net.codjo.dataprocess.common.exception.TreatmentException;";
        code = code + " \n throw new TreatmentException(\"BAD\")";
        DataProcessContext context = new DataProcessContext();
        try {
            AbstractTreatment abstractTreatment = createTreatment(code, context);
            abstractTreatment.proceedTreatment(context);
            fail("Ce script Bean Shell aurait dû provoquer un TreatmentException !");
        }
        catch (Exception e) {
            assertThat("BAD", equalTo(e.getMessage()));
        }
    }


    @Test
    public void exception() {
        String code = "import net.codjo.dataprocess.common.exception.TreatmentException;";
        code = code + " \n throw new IllegalArgumentException(\"BAD\")";
        DataProcessContext context = new DataProcessContext();
        try {
            AbstractTreatment abstractTreatment = createTreatment(code, context);
            abstractTreatment.proceedTreatment(context);
            fail("Ce script Bean Shell aurait dû provoquer un TreatmentException !");
        }
        catch (Exception e) {
            assertThat(e.getCause() instanceof IllegalArgumentException, equalTo(true));
            assertThat(
                  "Erreur ligne 2 --> throw new IllegalArgumentException ( \"BAD\" ) ; \nSourced file: inline evaluation of: ``import net.codjo.dataprocess.common.exception.TreatmentException;   throw new Il . . . '' : TargetError",
                  equalTo(e.getMessage()));
            assertThat("BAD", equalTo(e.getCause().getMessage()));
        }
    }


    @Test
    public void errorInCode() {
        String code = "RESULT = Integer.toStrin(5 * 2);";
        DataProcessContext context = new DataProcessContext();
        try {
            AbstractTreatment abstractTreatment = createTreatment(code, context);
            abstractTreatment.proceedTreatment(context);
            fail("Ce script Bean Shell aurait dû provoquer une erreur !");
        }
        catch (Exception e) {
            assertThat("L'exécution du script Bean Shell a échoué !\n", equalTo(e.getMessage()));
            assertThat("Sourced file: inline evaluation of: ``RESULT = Integer.toStrin(5 * 2);'' : "
                       + "Error in method invocation: Static method toStrin( int ) not found in class'java.lang.Integer'",
                       equalTo(e.getCause().getMessage()));
        }
    }


    @Test
    public void getArgument() throws Exception {
        DataProcessContext context = new DataProcessContext();
        context.setProperty("portfolioCode", "MIMI");
        String code = "String tiret=\"-\";\n";
        code = code + "RESULT = tiret + treatment.getArgument(\"CODE_PORTEFEUILLE\") + tiret";

        AbstractTreatment abstractTreatment = createTreatment(code, context);
        Object result = abstractTreatment.proceedTreatment(context);

        assertThat(result.toString(), equalTo("-MIMI-"));
    }


    @Test
    public void getConnection() throws Exception {
        DataProcessContext context = new DataProcessContext();

        String code = "import java.sql.*;\n"
                      + "\n"
                      + "ResultSet rs = treatment.getConnection().createStatement().executeQuery(\"select getdate()\");\n"
                      + "try {\n"
                      + "rs.next();\n"
                      + "RESULT = rs.getString(1).substring(0, 10);"
                      + "}\n"
                      + "finally {\n"
                      + "rs.close();\n"
                      + "}";

        AbstractTreatment abstractTreatment = createTreatment(code, context);
        Object result = abstractTreatment.proceedTreatment(context);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            simpleDateFormat.parse(result.toString());
        }
        catch (ParseException e) {
            fail("Une exception était attendue !");
        }
        assertThat(result.toString(), equalTo(getDate()));
    }


    private AbstractTreatment createTreatment(String code, DataProcessContext context)
          throws TreatmentException {
        TreatmentModel treatmentModel = new TreatmentModel();
        treatmentModel.setTarget(code);
        treatmentModel.setType(DataProcessConstants.BSH_TYPE);
        treatmentModel.setArguments(buildArgsForQuery());
        treatmentModel.setId("traitementTest");
        AbstractTreatment abstractTreatment =
              TreatmentFactory.buildTreatment(fixture.getConnection(), treatmentModel, 0,
                                              null);
        abstractTreatment.configure(context);
        return abstractTreatment;
    }


    private static ArgList buildArgsForQuery() {
        ArgList argList = new ArgList();

        List<ArgModel> list = new ArrayList<ArgModel>();
        list.add(new ArgModel("CODE_PORTEFEUILLE", "$portfolioCode$", 1, Types.VARCHAR));
        list.add(new ArgModel("QUANTITE", "0.12345", 2, Types.NUMERIC));

        argList.setArgs(list);
        return argList;
    }


    private String getDate() throws SQLException {
        Statement stmt = fixture.getConnection().createStatement();
        ResultSet rs = stmt.executeQuery("select getdate()");
        try {
            rs.next();
            return rs.getString(1).substring(0, 10);
        }
        finally {
            rs.close();
            stmt.close();
        }
    }
}

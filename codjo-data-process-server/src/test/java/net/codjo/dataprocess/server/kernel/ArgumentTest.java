/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.server.kernel;
import net.codjo.database.common.api.DatabaseFactory;
import net.codjo.database.common.api.JdbcFixture;
import net.codjo.dataprocess.common.context.DataProcessContext;
import net.codjo.dataprocess.common.exception.TreatmentException;
import net.codjo.dataprocess.common.model.ArgModel;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static net.codjo.dataprocess.common.context.DataProcessContext.PACKAGE_ARGUMENT_MODIFIER;
import static net.codjo.dataprocess.server.kernel.Argument.PACKAGE_ARGUMENT_MODIFIER_ERROR;
import static net.codjo.dataprocess.server.kernel.Argument.getNotResolvedMessage;
import static net.codjo.test.common.matcher.JUnitMatchers.*;
public class ArgumentTest {
    private DataProcessContext context;
    private JdbcFixture jdbc;


    @Before
    public void before() throws Exception {
        jdbc = new DatabaseFactory().createJdbcFixture();
        jdbc.doSetUp();
        context = new DataProcessContext();
        context.setProperty("portfolioCode", "CODE1");
        context.setProperty("typestock", "HB2");
        context.setProperty("1.Ma liste d'exécution 1.monParamLocal",
                            "valeur locale bien locale");
        context.setProperty("1.Ma liste d'exécution 2.monParamLocal",
                            "local local");
    }


    @After
    public void after() {
        jdbc.doTearDown();
    }


    @Test
    public void construction() throws Exception {
        Argument argument = new Argument(new ArgModel("name", "value"));
        assertThat("value", equalTo(argument.getValue()));
    }


    @Test
    public void packageArgumentModifier() throws Exception {
        Argument argument = new Argument(new ArgModel("name",
                                                      "ArgumentModifierExample('$portfolioCode$', 'val1', 'val2')"));
        try {
            argument.computeValue(null, context, 0, null);
            fail("Ce test aurait dû produire une erreur!");
        }
        catch (Exception ex) {
            assertThat(ex.getLocalizedMessage(),
                       equalTo(
                             "Classe de modification d'arguments 'PACKAGE_ARGUMENT_MODIFIER_NOT_INITIALIZED.ArgumentModifierExample' inexistante, causée par: PACKAGE_ARGUMENT_MODIFIER_NOT_INITIALIZED.ArgumentModifierExample"));
        }
        context.setProperty(PACKAGE_ARGUMENT_MODIFIER, "net.codjo.dataprocess.server.kernel");
        argument.computeValue(null, context, 0, null);
        assertThat("CODE1-val1-val2", equalTo(argument.getValue()));
    }


    @Test
    public void processContextConstantArgument() throws Exception {
        Argument argument = new Argument(new ArgModel("name", "CONSTANT"));
        argument.computeValue(null, context, 0, null);
        assertThat("CONSTANT", equalTo(argument.getValue()));
        argument.computeValue(null, context, 0, null);
        assertThat("CONSTANT", equalTo(argument.getValue()));
    }


    @Test
    public void getPackageArgumentModifier() throws Exception {
        assertThat(Argument.getPackageArgumentModifier(context),
                   equalTo(PACKAGE_ARGUMENT_MODIFIER_ERROR + "."));
        context.setProperty(PACKAGE_ARGUMENT_MODIFIER, "net.codjo.dataprocess.server.kernel");
        assertThat(Argument.getPackageArgumentModifier(context),
                   equalTo("net.codjo.dataprocess.server.kernel."));
    }


    @Test
    public void processContextGlobalArgument() throws Exception {
        Argument argument = new Argument(new ArgModel("name", "$portfolioCode$"));
        assertThat("$portfolioCode$", equalTo(argument.getValue()));
        argument.computeValue(null, context, 0, null);
        assertThat("CODE1", equalTo(argument.getValue()));
        argument.computeValue(null, context, 0, null);
        assertThat("CODE1", equalTo(argument.getValue()));

        argument = new Argument(new ArgModel("name", "$typestock$"));
        argument.computeValue(null, context, 0, null);
        assertThat("HB2", equalTo(argument.getValue()));

        argument = new Argument(new ArgModel("name", "$notResolvable$"));
        argument.computeValue(null, context, 0, null);
        assertThat(argument.getValue(), nullValue());
    }


    @Test
    public void processContextArgumentModifier() throws Exception {
        Argument argument = new Argument(new ArgModel("name",
                                                      "net.codjo.dataprocess.server.kernel.ArgumentModifierExample('$portfolioCode$', 'val1', 'val2')"));
        argument.computeValue(null, context, 0, null);
        assertThat("CODE1-val1-val2", equalTo(argument.getValue()));

        argument = new Argument(new ArgModel("name",
                                             "net.codjo.dataprocess.server.kernel.ArgumentModifierExample('$portfolioCode$', '#monParamLocal#', 'CONSTANT')"));
        argument.computeValue(null, context, 1, "Ma liste d'exécution 1");
        assertThat("CODE1-valeur locale bien locale-CONSTANT", equalTo(argument.getValue()));

        argument = new Argument(new ArgModel("name",
                                             "net.codjo.dataprocess.server.kernel.ArgumentModifierExample('$notResolvable$', '#paramLocalNotResolvable#', 'CONSTANT')"));
        argument.computeValue(null, context, 1, "Ma liste d'exécution 1");
        assertThat("null-null-CONSTANT", equalTo(argument.getValue()));

        try {
            argument = new Argument(new ArgModel("name",
                                                 "net.codjo.dataprocess.server.kernel.ArgumentModifierExample('$portfolioCode$', '#monParamLocal#', 'CONSTANT')"));
            argument.computeValue(null, context, 1, null);
            fail("Une exception était attendue : utilisation argument local et pas de liste d'exécution");
        }
        catch (TreatmentException ex) {
            assertThat(
                  "La résolution d'un argument local (net.codjo.dataprocess.server.kernel.ArgumentModifierExample('$portfolioCode$', '#monParamLocal#', 'CONSTANT')) est impossible car le traitement est utilisé en dehors d'un repository et/ou d'une liste d'exécution",
                  equalTo(ex.getLocalizedMessage()));
        }

        try {
            argument = new Argument(new ArgModel("name",
                                                 "net.codjo.dataprocess.server.kernel.XXXX('$portfolioCode$', 'val1', 'val2')"));
            argument.computeValue(null, context, 0, null);
            fail("Une exception était attendue : la classe de modification d'arguments n'existe pas.");
        }
        catch (TreatmentException ex) {
            assertThat(
                  "Classe de modification d'arguments 'net.codjo.dataprocess.server.kernel.XXXX' inexistante, causée par: net.codjo.dataprocess.server.kernel.XXXX",
                  equalTo(ex.getLocalizedMessage()));
        }
        assertThat("net.codjo.dataprocess.server.kernel.XXXX('$portfolioCode$', 'val1', 'val2')",
                   equalTo(argument.getValue()));
    }


    @Test
    public void processContextArgumentLocaux() throws Exception {
        Argument argument = new Argument(new ArgModel("name", "#monParamLocal#"));
        argument.computeValue(null, context, 1, "Ma liste d'exécution 1");
        assertThat("valeur locale bien locale", equalTo(argument.getValue()));

        argument = new Argument(new ArgModel("name", "#monParamLocal#"));
        argument.computeValue(null, context, 1, "Ma liste d'exécution 2");
        assertThat("local local", equalTo(argument.getValue()));

        try {
            argument = new Argument(new ArgModel("name", "#monParamLocal#"));
            argument.computeValue(null, context, 1, null);
            fail("Une exception était attendue : utilisation argument local et pas de liste d'exécution");
        }
        catch (TreatmentException ex) {
            assertThat(
                  "La résolution d'un argument local (#monParamLocal#) est impossible car le traitement est utilisé en dehors d'un repository et/ou d'une liste d'exécution",
                  equalTo(ex.getLocalizedMessage()));
        }
    }


    @Test
    public void getNotResolvableValueArgumentModifier() {
        Argument argument = new Argument(new ArgModel("name",
                                                      "net.codjo.dataprocess.server.kernel.ArgumentModifierExample('$portfolioCode$', '#monParamLocal#', 'CONSTANT')"));
        List<String> argsList = argument.getNotResolvableValue(jdbc.getConnection(),
                                                               context,
                                                               1,
                                                               "Ma liste d'exécution 1");
        assertThat(0, equalTo(argsList.size()));

        argument = new Argument(new ArgModel("name",
                                             "net.codjo.dataprocess.server.kernel.ArgumentModifierExample('$portfolioCode$', '#monParamLocal#', 'CONSTANT')"));
        argsList = argument.getNotResolvableValue(jdbc.getConnection(), context, 0, "Ma liste d'exécution 1");
        assertThat(
              "[La résolution d'un argument local (net.codjo.dataprocess.server.kernel.ArgumentModifierExample('$portfolioCode$', '#monParamLocal#', 'CONSTANT')) est impossible car le traitement est utilisé en dehors d'un repository et/ou d'une liste d'exécution]",
              equalTo(argsList.toString()));

        argument = new Argument(new ArgModel("name",
                                             "net.codjo.dataprocess.server.kernel.ArgumentModifierExample('$portfolioCode$', '#monParamLocal#', 'CONSTANT')"));
        argsList = argument.getNotResolvableValue(jdbc.getConnection(), context, 1, null);
        assertThat(
              "[La résolution d'un argument local (net.codjo.dataprocess.server.kernel.ArgumentModifierExample('$portfolioCode$', '#monParamLocal#', 'CONSTANT')) est impossible car le traitement est utilisé en dehors d'un repository et/ou d'une liste d'exécution]",
              equalTo(argsList.toString()));

        argument = new Argument(new ArgModel("name",
                                             "net.codjo.dataprocess.common.util.ArgumentModifierFake('$portfolioCode$', '#monParamLocal#', 'CONSTANT')"));
        argsList = argument.getNotResolvableValue(jdbc.getConnection(), context, 1, "Ma liste d'exécution 1");
        assertThat(
              "[Classe de modification d'arguments 'net.codjo.dataprocess.common.util.ArgumentModifierFake' inexistante.]",
              equalTo(argsList.toString()));

        context.removeProperty("portfolioCode");
        argument = new Argument(new ArgModel("name",
                                             "net.codjo.dataprocess.server.kernel.ArgumentModifierExample('$portfolioCode$', '#monParamLocal#', 'CONSTANT')"));
        argsList = argument.getNotResolvableValue(jdbc.getConnection(), context, 1, "Ma liste d'exécution 1");
        assertThat("[" + getNotResolvedMessage("portfolioCode") + "]", equalTo(argsList.toString()));

        context.setProperty("portfolioCode", "CODE1");
        context.removeProperty("1.Ma liste d'exécution 1.monParamLocal");
        argument = new Argument(new ArgModel("name",
                                             "net.codjo.dataprocess.server.kernel.ArgumentModifierExample('$portfolioCode$', '#monParamLocal#', 'CONSTANT')"));
        argsList = argument.getNotResolvableValue(jdbc.getConnection(), context, 1, "Ma liste d'exécution 1");
        assertThat("[" + getNotResolvedMessage("monParamLocal") + "]", equalTo(argsList.toString()));

        context.removeProperty("portfolioCode");
        context.removeProperty("1.Ma liste d'exécution 1.monParamLocal");
        argument = new Argument(new ArgModel("name",
                                             "net.codjo.dataprocess.server.kernel.ArgumentModifierExample('$portfolioCode$', '#monParamLocal#', 'CONSTANT')"));
        argsList = argument.getNotResolvableValue(jdbc.getConnection(), context, 1, "Ma liste d'exécution 1");
        assertThat("[" + getNotResolvedMessage("portfolioCode") + ", "
                   + getNotResolvedMessage("monParamLocal") + "]", equalTo(argsList.toString()));
    }
}

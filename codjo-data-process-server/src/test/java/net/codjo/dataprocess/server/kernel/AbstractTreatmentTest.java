/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.server.kernel;
import net.codjo.dataprocess.common.context.DataProcessContext;
import net.codjo.dataprocess.common.exception.TreatmentException;
import net.codjo.dataprocess.common.model.ArgModel;
import net.codjo.dataprocess.common.model.ExecutionListModel;
import net.codjo.dataprocess.common.model.TreatmentModel;
import net.codjo.dataprocess.common.report.TreatmentReport;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;

import static net.codjo.test.common.matcher.JUnitMatchers.*;
/**
 *
 */
public class AbstractTreatmentTest {
    @Test
    public void getNotResolvableArgs() {
        TestAbstractTreatment testAbstractTreatment = new TestAbstractTreatment();
        DataProcessContext context = new DataProcessContext();
        context.setProperty("portfolioCode", "MIMI");
        context.setProperty("code", "codeValue");
        context.setProperty("1.Ma liste d'exécution 1.monParamLocal", "valeur locale bien locale");
        testAbstractTreatment.setRepositoryId(1);

        ExecutionListModel executionListModel = new ExecutionListModel();
        executionListModel.setName("Ma liste d'exécution 1");
        testAbstractTreatment.setExecutionListModel(executionListModel);
        //testAbstractTreatment.setExecutionListName("Ma liste d'exécution 1");
        testAbstractTreatment.setArgs(buildArgs());
        List<String> argsList = testAbstractTreatment.getNotResolvableArguments(context);
        assertThat(0, equalTo(argsList.size()));

        context.removeProperty("code");
        executionListModel = new ExecutionListModel();
        executionListModel.setName("Autre liste d'exécution");
        testAbstractTreatment.setExecutionListModel(executionListModel);
        argsList = testAbstractTreatment.getNotResolvableArguments(context);
        assertThat("[" + Argument.getNotResolvedMessage("code") + ", "
                   + Argument.getNotResolvedMessage("monParamLocal") + "]", equalTo(argsList.toString()));

        executionListModel = new ExecutionListModel();
        executionListModel.setName("Ma liste d'exécution 1");
        testAbstractTreatment.setExecutionListModel(executionListModel);
        testAbstractTreatment.setArgs(buildArgsWithFakeArgumentModifier());
        argsList = testAbstractTreatment.getNotResolvableArguments(context);
        assertThat("[" + Argument.getNotResolvedMessage("code") + ", "
                   + "Classe de modification d'arguments 'net.codjo.dataprocess.common.util.ArgumentModifierFake' inexistante.]",
                   equalTo(argsList.toString()));

        executionListModel = new ExecutionListModel();
        executionListModel.setName("Autre liste d'exécution");
        testAbstractTreatment.setExecutionListModel(executionListModel);
        argsList = testAbstractTreatment.getNotResolvableArguments(context);
        assertThat("[" + Argument.getNotResolvedMessage("code") + ", "
                   + Argument.getNotResolvedMessage("monParamLocal") + ", "
                   + "Classe de modification d'arguments 'net.codjo.dataprocess.common.util.ArgumentModifierFake' inexistante.]",
                   equalTo(argsList.toString()));
    }


    @Test
    public void getTreatmentReport() {
        TreatmentModel treatmentModel = new TreatmentModel();
        treatmentModel.setId("treatment1");
        TestAbstractTreatment testAbstractTreatment = new TestAbstractTreatment();
        testAbstractTreatment.setTreatmentModel(treatmentModel);
        testAbstractTreatment.setError(new Exception("erreur X"));
        testAbstractTreatment.setResult(5);

        assertThat(testAbstractTreatment.getTreatmentReport(),
                   equalTo(new TreatmentReport("treatment1", "5", "erreur X")));
    }


    private static Map<String, Argument> buildArgs() {
        Map<String, Argument> args = new HashMap<String, Argument>();
        args.put("name1", buildArgs("name1", "$portfolioCode$"));
        args.put("name2", buildArgs("name2", "$code$"));
        args.put("name3", buildArgs("name3",
                                    "net.codjo.dataprocess.server.kernel.ArgumentModifierExample('$portfolioCode$', '#monParamLocal#', 'BURNING')"));
        return args;
    }


    private static Map<String, Argument> buildArgsWithFakeArgumentModifier() {
        Map<String, Argument> args = new HashMap<String, Argument>();
        args.put("name1", buildArgs("name1", "$portfolioCode$"));
        args.put("name2", buildArgs("name2", "$code$"));
        args.put("name3", buildArgs("name3",
                                    "net.codjo.dataprocess.common.util.ArgumentModifierFake('$portfolioCode$', '#monParamLocal#', 'BURNING')"));
        return args;
    }


    private static Argument buildArgs(String name, String value) {
        return new Argument(new ArgModel(name, value));
    }


    static class TestAbstractTreatment extends AbstractTreatment {
        public Object proceedTreatment(DataProcessContext context, Object... param)
              throws TreatmentException {
            return null;
        }
    }
}

package net.codjo.dataprocess.common.model;
import net.codjo.dataprocess.common.exception.TreatmentException;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

import static net.codjo.test.common.matcher.JUnitMatchers.*;
/**
 *
 */
public class ExecutionListStoreHelperTest {
    private ExecutionListStoreHelper executionListStoreHelper;


    @Before
    public void before() {
        executionListStoreHelper = new ExecutionListStoreHelper();
    }


    @Test
    public void testAddExecutionList() throws Exception {
        ExecutionListModel executionListModel = buildExecutionListModel("List de traitement 1");
        executionListStoreHelper.addExecutionList(executionListModel);
        assertThat(executionListStoreHelper.getRepository().size(), equalTo(1));
        assertThat(executionListStoreHelper.getRepository().get(0).getName(),
                   equalTo("List de traitement 1"));

        try {
            executionListStoreHelper.addExecutionList(executionListModel);
            fail("Ce test aurait dû échoué !");
        }
        catch (TreatmentException ex) {
            assertThat(ex.getLocalizedMessage(),
                       equalTo("'List de traitement 1' existe déjà dans la liste de traitements !"));
        }
    }


    @Test
    public void testDeleteExecutionList() throws Exception {
        ExecutionListModel executionListModel = buildExecutionListModel("List de traitement 1");
        executionListStoreHelper.addExecutionList(executionListModel);
        executionListStoreHelper.deleteExecutionList(1, executionListModel);

        assertThat(executionListStoreHelper.getRepository().size(), equalTo(0));
        assertThat(executionListStoreHelper.getExecListsToDelete().size(), equalTo(1));
        assertThat(executionListStoreHelper.getExecListsToDelete().get(0).getExecutionListModel().getName(),
                   equalTo("List de traitement 1"));
        assertThat(executionListStoreHelper.getExecListsToDelete().get(0).getRepositoryId(), equalTo(1));
    }


    @Test
    public void testIsAlreadyInRepository() throws Exception {
        ExecutionListModel executionListModel = buildExecutionListModel("List de traitement 1");
        ExecutionListModel executionListModel2 = buildExecutionListModel("List de traitement 2");
        executionListStoreHelper.addExecutionList(executionListModel);

        assertThat(executionListStoreHelper.isAlreadyInRepository(executionListModel), equalTo(true));
        assertThat(executionListStoreHelper.isAlreadyInRepository(executionListModel2), equalTo(false));
    }


    @Test
    public void testToString() throws Exception {
        ExecutionListModel executionListModel = buildExecutionListModel("List de traitement 1");
        executionListStoreHelper.addExecutionList(executionListModel);

        assertThat(executionListStoreHelper.toString(), equalTo("name : List de traitement 1\n"
                                                                + "id : 1\n"
                                                                + "priority : 0\n"
                                                                + "family : 0\n"
                                                                + "status : 0\n"
                                                                + "executionDate : null\n"
                                                                + "Traitements de la liste :\n"
                                                                + "-------------------------\n"
                                                                + "id : ID1\tpriority : 1\tcomment : comment 1\ttitle : Traitement 1\tresultTableName : TABLE1\tselectAllHandler : selectAllPeriod1\n"
                                                                + "-------------------------\n"
                                                                + "id : ID2\tpriority : 2\tcomment : comment 2\ttitle : Traitement 2\tresultTableName : TABLE2\tselectAllHandler : selectAllPeriod2\n"
                                                                + "-------------------------\n"
                                                                + "id : ID3\tpriority : 3\tcomment : null\ttitle : Traitement 3\n"
                                                                + "-------------------------\n"
                                                                + "\n"
                                                                + "******************************************************\n"));
    }


    private static ExecutionListModel buildExecutionListModel(String name) {
        ExecutionListModel executionListModel = new ExecutionListModel();
        executionListModel.setId(1);
        executionListModel.setName(name);

        Map<UserTreatment, Integer> map = buildUserTreatmentMap();

        executionListModel.setPriorityMap(map);
        return executionListModel;
    }


    private static Map<UserTreatment, Integer> buildUserTreatmentMap() {
        Map<UserTreatment, Integer> map = new HashMap<UserTreatment, Integer>();
        TreatmentModel treatmentModel = new TreatmentModel();
        treatmentModel.setTitle("Traitement 1");
        treatmentModel.setId("ID1");
        treatmentModel.setComment("comment 1");
        treatmentModel.setResultTable(new ResultTable("TABLE1", "selectAllPeriod1"));
        UserTreatment usrTrt = new UserTreatment(treatmentModel);
        map.put(usrTrt, 1);

        treatmentModel = new TreatmentModel();
        treatmentModel.setTitle("Traitement 2");
        treatmentModel.setId("ID2");
        treatmentModel.setComment("comment 2");
        treatmentModel.setResultTable(new ResultTable("TABLE2", "selectAllPeriod2"));
        usrTrt = new UserTreatment(treatmentModel);
        map.put(usrTrt, 2);

        treatmentModel = new TreatmentModel();
        treatmentModel.setTitle("Traitement 3");
        treatmentModel.setId("ID3");
        usrTrt = new UserTreatment(treatmentModel);
        map.put(usrTrt, 3);

        return map;
    }
}

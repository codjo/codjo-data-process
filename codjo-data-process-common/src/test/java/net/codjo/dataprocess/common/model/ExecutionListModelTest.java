/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.common.model;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

import static net.codjo.test.common.matcher.JUnitMatchers.*;
/**
 *
 */
public class ExecutionListModelTest {
    @Test
    public void content() {
        ExecutionListModel executionListModel = buildExecutionListModel("List de traitement 1");
        assertThat(executionListModel.toString(), equalTo(
              "name : List de traitement 1\n"
              + "id : 1\n"
              + "priority : 0\n"
              + "family : 0\n"
              + "status : 0\n"
              + "executionDate : null\n"
              + "Traitements de la liste :\n"
              + "-------------------------\n"
              + "id : ID1\t"
              + "priority : 1\t"
              + "comment : comment 1\t"
              + "title : montraitement 1\t"
              + "resultTableName : TABLE1\t"
              + "selectAllHandler : selectAllPeriod1\n"
              + "-------------------------\n"
              + "id : ID2\t"
              + "priority : 2\t"
              + "comment : comment 2\t"
              + "title : montraitement 2\t"
              + "resultTableName : TABLE2\t"
              + "selectAllHandler : selectAllPeriod2\n"
              + "-------------------------\n"
              + "id : ID3\t"
              + "priority : 3\t"
              + "comment : null\t"
              + "title : montraitement 3\n"
              + "-------------------------\n"));
    }


    @Test
    public void testSearchAnItem() throws Exception {
        TreatmentModel treatmentModel = new TreatmentModel();
        treatmentModel.setId("ID1");
        treatmentModel.setTitle("montraitement 1");
        UserTreatment u1 = new UserTreatment(treatmentModel);

        Map<UserTreatment, Integer> map = buildUserTreatmentMap();
        UserTreatment result = ExecutionListModel.searchAnItem(u1, map);

        assertThat(u1.getTitle(), equalTo(result.getTitle()));
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
        treatmentModel.setTitle("montraitement 1");
        treatmentModel.setId("ID1");
        treatmentModel.setComment("comment 1");
        treatmentModel.setResultTable(new ResultTable("TABLE1", "selectAllPeriod1"));
        UserTreatment usrTrt = new UserTreatment(treatmentModel);
        map.put(usrTrt, 1);

        treatmentModel = new TreatmentModel();
        treatmentModel.setTitle("montraitement 2");
        treatmentModel.setId("ID2");
        treatmentModel.setComment("comment 2");
        treatmentModel.setResultTable(new ResultTable("TABLE2", "selectAllPeriod2"));
        usrTrt = new UserTreatment(treatmentModel);
        map.put(usrTrt, 2);

        treatmentModel = new TreatmentModel();
        treatmentModel.setTitle("montraitement 3");
        treatmentModel.setId("ID3");
        usrTrt = new UserTreatment(treatmentModel);
        map.put(usrTrt, 3);

        return map;
    }
}

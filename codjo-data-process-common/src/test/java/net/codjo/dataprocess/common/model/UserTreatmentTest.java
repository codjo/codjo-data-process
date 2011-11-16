package net.codjo.dataprocess.common.model;
import org.junit.Test;

import static net.codjo.test.common.matcher.JUnitMatchers.*;
/**
 *
 */
public class UserTreatmentTest {
    @Test
    public void usertreatment() {
        TreatmentModel treatmentModel = new TreatmentModel();
        treatmentModel.setId("id");
        treatmentModel.setComment("comment");
        treatmentModel.setResultTable(new ResultTable("T_TABLE", "selectAllPeriod"));
        treatmentModel.setTitle("TITLE");
        UserTreatment userTreatment = new UserTreatment(treatmentModel);
        userTreatment.setPriority(2);

        assertThat(userTreatment.getId(), equalTo("id"));
        assertThat(userTreatment.getComment(), equalTo("comment"));
        assertThat(userTreatment.getPriority(), equalTo(2));
        assertThat(userTreatment.getResultTable().getTable(), equalTo("T_TABLE"));
        assertThat(userTreatment.getResultTable().getSelectAllHandler(), equalTo("selectAllPeriod"));
        assertThat(userTreatment.getTitle(), equalTo("TITLE"));

        assertThat(userTreatment.toString(),
                   equalTo(
                         "id : id\tpriority : 2\tcomment : comment\ttitle : TITLE\tresultTableName : T_TABLE\tselectAllHandler : selectAllPeriod"));

        TreatmentModel treatmentModel2 = new TreatmentModel();
        treatmentModel2.setId("id");
        UserTreatment userTreatment2 = new UserTreatment(treatmentModel2);

        assertThat(userTreatment.equals(userTreatment2), equalTo(true));
    }
}

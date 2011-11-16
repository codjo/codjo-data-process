package net.codjo.dataprocess.common.codec;
import net.codjo.dataprocess.common.model.ResultTable;
import net.codjo.dataprocess.common.model.TreatmentModel;
import net.codjo.dataprocess.common.model.UserTreatment;
import org.junit.Test;

import static net.codjo.test.common.matcher.JUnitMatchers.*;
/**
 *
 */
public class UserTreatmentCodecTest {
    @Test
    public void encode() {
        TreatmentModel treatmentModel = new TreatmentModel();
        treatmentModel.setId("id1");
        treatmentModel.setComment("comment");
        treatmentModel.setResultTable(new ResultTable("T_TABLE", "selectAllPeriod"));
        treatmentModel.setTitle("TITLE");
        UserTreatment userTreatment = new UserTreatment(treatmentModel);
        userTreatment.setPriority(2);

        String result = UserTreatmentCodec.encode(userTreatment, true);
        assertThat(result, equalTo(
              "<userTrt id=\"id1\" comment=\"comment\" title=\"TITLE\"/>"));

        result = UserTreatmentCodec.encode(userTreatment, false);
        assertThat(result, equalTo(
              "<userTrt id=\"id1\" comment=\"comment\" title=\"TITLE\" priority=\"2\">\n"
              + "  <resultTable table=\"T_TABLE\" selectAllHandler=\"selectAllPeriod\"/>\n"
              + "</userTrt>"));
    }


    @Test
    public void decode() {
        UserTreatment userTreatment = UserTreatmentCodec.decode(
              "<userTrt id=\"id1\" comment=\"comment\" title=\"TITLE\" priority=\"2\">\n"
              + "  <resultTable table=\"T_TABLE\" selectAllHandler=\"selectAllPeriod\"/>\n"
              + "</userTrt>", false);
        assertThat(userTreatment.getId(), equalTo("id1"));
        assertThat(userTreatment.getComment(), equalTo("comment"));
        assertThat(userTreatment.getTitle(), equalTo("TITLE"));
        assertThat(userTreatment.getPriority(), equalTo(2));
        assertThat(userTreatment.getResultTable().getTable(), equalTo("T_TABLE"));
        assertThat(userTreatment.getResultTable().getSelectAllHandler(), equalTo("selectAllPeriod"));

        userTreatment = UserTreatmentCodec.decode(
              "<userTrt id=\"id1\" comment=\"comment\" title=\"TITLE\"/>", true);
        assertThat(userTreatment.getId(), equalTo("id1"));
        assertThat(userTreatment.getComment(), equalTo("comment"));
        assertThat(userTreatment.getTitle(), equalTo("TITLE"));
        assertThat(userTreatment.getPriority(), equalTo(0));
        assertThat(userTreatment.getResultTable(), equalTo(null));
    }
}

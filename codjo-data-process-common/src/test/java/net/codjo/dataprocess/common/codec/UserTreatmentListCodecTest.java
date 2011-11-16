/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.common.codec;
import net.codjo.dataprocess.common.exception.TreatmentException;
import net.codjo.dataprocess.common.model.ResultTable;
import net.codjo.dataprocess.common.model.TreatmentModel;
import net.codjo.dataprocess.common.model.UserTreatment;
import net.codjo.test.common.XmlUtil;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

import static net.codjo.test.common.matcher.JUnitMatchers.*;
/**
 *
 */
public class UserTreatmentListCodecTest {
    @Test
    public void encode() {
        String res = UserTreatmentListCodec.encode(buildModel(), true);
        assertThat(XmlUtil.areEquivalent(getXmlLight(), res), equalTo(true));

        res = UserTreatmentListCodec.encode(buildModel(), false);
        assertThat(XmlUtil.areEquivalent(getXml(), res), equalTo(true));
    }


    @Test
    public void decode() throws TreatmentException {
        List<UserTreatment> res = UserTreatmentListCodec.decode(getXmlLight(), true);
        List<UserTreatment> expectedList = buildModel();
        verifyResultLight(expectedList, res);
        assertThat(XmlUtil.areEquivalent(getXmlLight(), UserTreatmentListCodec.encode(expectedList, true)),
                   equalTo(true));

        res = UserTreatmentListCodec.decode(getXml(), false);
        expectedList = buildModel();
        verifyResult(expectedList, res);
        assertThat(XmlUtil.areEquivalent(getXml(), UserTreatmentListCodec.encode(expectedList, false)),
                   equalTo(true));
    }


    private static void verifyResult(List expected, List result) {
        assertThat(expected.size(), equalTo(result.size()));
        for (int i = 0; i < result.size(); i++) {
            UserTreatment trtExpected = (UserTreatment)expected.get(i);
            UserTreatment trtResult = (UserTreatment)result.get(i);
            assertThat(trtExpected.getId(), equalTo(trtResult.getId()));
            assertThat(trtExpected.getComment(), equalTo(trtResult.getComment()));
            assertThat(trtExpected.getTitle(), equalTo(trtResult.getTitle()));
            assertThat(trtExpected.getPriority(), equalTo(trtResult.getPriority()));
            assertThat(trtExpected.getResultTable().getTable(),
                       equalTo(trtResult.getResultTable().getTable()));
            assertThat(trtExpected.getResultTable().getSelectAllHandler(),
                       equalTo(trtResult.getResultTable().getSelectAllHandler()));
        }
    }


    private static void verifyResultLight(List expected, List result) {
        assertThat(expected.size(), equalTo(result.size()));
        for (int i = 0; i < result.size(); i++) {
            UserTreatment trtExpected = (UserTreatment)expected.get(i);
            UserTreatment trtResult = (UserTreatment)result.get(i);
            assertThat(trtExpected.getId(), equalTo(trtResult.getId()));
            assertThat(trtExpected.getComment(), equalTo(trtResult.getComment()));
            assertThat(trtExpected.getTitle(), equalTo(trtResult.getTitle()));
        }
    }


    private static String getXmlLight() {
        return "<root>\n"
               + "  <userTrt id=\"id1\" comment=\"commenté1\" title=\"TITLE1\"/>\n"
               + "  <userTrt id=\"id2\" comment=\"comment2\" title=\"TITLE2\"/>\n"
               + "  <userTrt id=\"id3\" title=\"TITLE3\"/>\n"
               + "  <userTrt id=\"id4\" title=\"TITLE4\"/>\n"
               + "</root>";
    }


    private static String getXml() {
        return "<root>\n"
               + "  <userTrt id=\"id1\" comment=\"commenté1\" title=\"TITLE1\" priority=\"1\">\n"
               + "    <resultTable table=\"AP_RES1\" selectAllHandler=\"selectAllPeriod1\"/>\n"
               + "  </userTrt>\n"
               + "  <userTrt id=\"id2\" comment=\"comment2\" title=\"TITLE2\" priority=\"2\">\n"
               + "    <resultTable table=\"AP_RES2\" selectAllHandler=\"selectAllPeriod2\"/>\n"
               + "  </userTrt>\n"
               + "  <userTrt id=\"id3\" title=\"TITLE3\" priority=\"3\">\n"
               + "    <resultTable selectAllHandler=\"selectAllPeriod3\"/>\n"
               + "  </userTrt>\n"
               + "  <userTrt id=\"id4\" title=\"TITLE4\" priority=\"4\">\n"
               + "    <resultTable table=\"AP_RES4\"/>\n"
               + "  </userTrt>\n"
               + "</root>";
    }


    private static List<UserTreatment> buildModel() {
        List<UserTreatment> list = new ArrayList<UserTreatment>();
        TreatmentModel treatmentModel = new TreatmentModel();
        treatmentModel.setId("id1");
        treatmentModel.setComment("commenté1");
        treatmentModel.setResultTable(new ResultTable("AP_RES1", "selectAllPeriod1"));
        treatmentModel.setTitle("TITLE1");
        UserTreatment usrTrt = new UserTreatment(treatmentModel);
        usrTrt.setPriority(1);
        list.add(usrTrt);

        treatmentModel = new TreatmentModel();
        treatmentModel.setId("id2");
        treatmentModel.setComment("comment2");
        treatmentModel.setResultTable(new ResultTable("AP_RES2", "selectAllPeriod2"));
        treatmentModel.setTitle("TITLE2");
        usrTrt = new UserTreatment(treatmentModel);
        usrTrt.setPriority(2);
        list.add(usrTrt);

        treatmentModel = new TreatmentModel();
        treatmentModel.setId("id3");
        treatmentModel.setResultTable(new ResultTable(null, "selectAllPeriod3"));
        treatmentModel.setTitle("TITLE3");
        usrTrt = new UserTreatment(treatmentModel);
        usrTrt.setPriority(3);
        list.add(usrTrt);

        treatmentModel = new TreatmentModel();
        treatmentModel.setId("id4");
        treatmentModel.setResultTable(new ResultTable("AP_RES4", null));
        treatmentModel.setTitle("TITLE4");
        usrTrt = new UserTreatment(treatmentModel);
        usrTrt.setPriority(4);
        list.add(usrTrt);

        return list;
    }
}

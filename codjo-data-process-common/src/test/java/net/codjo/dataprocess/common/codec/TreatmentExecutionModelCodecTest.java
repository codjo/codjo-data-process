/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.common.codec;
import net.codjo.dataprocess.common.exception.TreatmentException;
import net.codjo.dataprocess.common.model.ExecutionListModel;
import net.codjo.dataprocess.common.model.ResultTable;
import net.codjo.dataprocess.common.model.TreatmentModel;
import net.codjo.dataprocess.common.model.UserTreatment;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;

import static net.codjo.test.common.matcher.JUnitMatchers.*;
/**
 *
 */
public class TreatmentExecutionModelCodecTest {
    @Test
    public void decode() throws TreatmentException {
        ExecutionListModelCodec codec = new ExecutionListModelCodec();
        ExecutionListModel res = codec.decode(getXml());
        ExecutionListModel res2 = new ExecutionListModelCodec().decode(getXml());
        ExecutionListModel expected = buildModel(1, "test", 47, 0);

        assertThat(res.equals(expected), equalTo(true));
        assertThat(res2.equals(expected), equalTo(true));
        assertThat(res.equals(res2), equalTo(true));

        res.setPriorityMap(null);
        assertThat(res.equals(expected), equalTo(false));
        expected.setPriorityMap(null);
        assertThat(res.equals(expected), equalTo(true));
    }


    @Test
    public void decodeEncode() throws TreatmentException {
        ExecutionListModelCodec codec = new ExecutionListModelCodec();
        ExecutionListModel expected = buildModel(1, "test", 5, 0);
        String xml = codec.encode(expected);
        ExecutionListModel res = codec.decode(xml);
        ExecutionListModel res2 = new ExecutionListModelCodec().decode(xml);
        assertThat(res.equals(res2), equalTo(true));
        assertThat(res.equals(expected), equalTo(true));
        assertThat(res2.equals(expected), equalTo(true));
    }


    @Test
    public void decodeEncodeList() throws TreatmentException {
        List<ExecutionListModel> expectedList = new ArrayList<ExecutionListModel>();
        List<ExecutionListModel> resList;
        ExecutionListModel expected = buildModel(1, "test", 5, 0);
        expectedList.add(expected);
        expected = buildModel(2, "test2", 7, 0);
        expectedList.add(expected);
        expected = buildModel(3, "test3", 8, 0);
        expectedList.add(expected);

        ExecutionListModelCodec codec = new ExecutionListModelCodec();
        resList = codec.decodeList(getXmlForList());
        verifyResult(expectedList, resList);
    }


    @Test
    public void encodeList() {
        List<ExecutionListModel> trtList = new ArrayList<ExecutionListModel>();
        ExecutionListModel expected = buildModel(1, "test", 5, 0);
        trtList.add(expected);
        expected = buildModel(2, "test2", 6, 0);
        trtList.add(expected);
        ExecutionListModelCodec codec = new ExecutionListModelCodec();
        codec.encodeList(trtList);
    }


    private static String getXml() {
        return "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>"
               + " <executionList id=\"1\" name=\"test\" status=\"0\" scope=\"TREATMENT\" priority=\"47\">"
               + "<treatment id=\"1\" title=\"comment1\" priority=\"0\" resultTable=\"GOGO\"/>"
               + "<treatment id=\"2\" title=\"comment2\" priority=\"2\" resultTable=\"GAGA\"/>"
               + "<treatment id=\"3\" title=\"comment3\" priority=\"3\" resultTable=\"GOZUU\"/>"
               + "</executionList> \n";
    }


    private static String getXmlForList() {
        return "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n" + "<root>\n"
               + "    <executionList id=\"1\" name=\"test\" status=\"0\" scope=\"TREATMENT\">\n"
               + "        <treatment id=\"1\" title=\"comment1\" priority=\"0\"/>\n"
               + "        <treatment id=\"3\" title=\"comment3\" priority=\"3\"/>\n"
               + "        <treatment id=\"2\" title=\"comment2\" priority=\"2\"/>\n"
               + "    </executionList>\n"
               + "    <executionList id=\"2\" name=\"test2\" status=\"0\" scope=\"TREATMENT\">\n"
               + "        <treatment id=\"2\" title=\"comment2\" priority=\"2\"/>\n"
               + "        <treatment id=\"3\" title=\"comment3\" priority=\"3\"/>\n"
               + "        <treatment id=\"1\" title=\"comment1\" priority=\"0\"/>\n"
               + "    </executionList>\n"
               + "    <executionList id=\"3\" name=\"test3\" status=\"0\" scope=\"TREATMENT\">\n"
               + "        <treatment id=\"2\" title=\"comment2\" priority=\"2\"/>\n"
               + "        <treatment id=\"3\" title=\"comment3\" priority=\"3\"/>\n"
               + "        <treatment id=\"1\" title=\"comment1\" priority=\"0\"/>\n"
               + "    </executionList>\n" + "</root>";
    }


    private static String getXmlEmptyList() {
        return "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n" + "<root>\n"
               + "    <executionList id=\"1\" name=\"test\" status=\"0\" scope=\"TREATMENT\"/>\n"
               + "</root>";
    }


    @Test
    public void prepareStrList() throws TreatmentException {
        ExecutionListModelCodec codec = new ExecutionListModelCodec();
        List<ExecutionListModel> list = codec.decodeList(getXmlEmptyList());
        ExecutionListModel executionListModel = buildModel(1, "test", 0, 0);
        List<ExecutionListModel> expected = new ArrayList<ExecutionListModel>();
        expected.add(executionListModel);
        verifyResult(expected, list);
    }


    private static ExecutionListModel buildModel(int id, String name, int priority, int status) {
        ExecutionListModel trtMod = new ExecutionListModel();
        trtMod.setId(id);
        trtMod.setName(name);
        trtMod.setPriority(priority);
        trtMod.setStatus(status);

        trtMod.setPriorityMap(buildTreatmentMap());
        return trtMod;
    }


    private static Map<UserTreatment, Integer> buildTreatmentMap() {
        Map<UserTreatment, Integer> userTreatmentMap = new HashMap<UserTreatment, Integer>();

        TreatmentModel treatmentModel = new TreatmentModel();
        treatmentModel.setId("1");
        treatmentModel.setTitle("comment1");
        treatmentModel.setResultTable(new ResultTable("GOGO", "selectAllPeriod"));
        UserTreatment usrTrt = new UserTreatment(treatmentModel);
        usrTrt.setPriority(0);
        userTreatmentMap.put(usrTrt, 0);

        treatmentModel = new TreatmentModel();
        treatmentModel.setId("2");
        treatmentModel.setTitle("comment2");
        treatmentModel.setResultTable(new ResultTable("GAGA", "selectAllPeriod"));
        usrTrt = new UserTreatment(treatmentModel);
        usrTrt.setPriority(2);
        userTreatmentMap.put(usrTrt, 2);

        treatmentModel = new TreatmentModel();
        treatmentModel.setId("3");
        treatmentModel.setTitle("comment3");
        treatmentModel.setResultTable(new ResultTable("GOZUU", "selectAllPeriod"));
        usrTrt = new UserTreatment(treatmentModel);
        usrTrt.setPriority(3);
        userTreatmentMap.put(usrTrt, 3);

        return userTreatmentMap;
    }


    private static void verifyResult(List<ExecutionListModel> expected, List<ExecutionListModel> result) {
        assertThat(expected.size(), equalTo(result.size()));
        for (ExecutionListModel trtResult : result) {
            ExecutionListModel trtExpected = searchInList(expected, trtResult.getId());
            assertThat(trtExpected.getId(), equalTo(trtResult.getId()));
            assertThat(trtExpected.getName(), equalTo(trtResult.getName()));
            assertThat(trtExpected.getStatus(), equalTo(trtResult.getStatus()));
        }
    }


    private static ExecutionListModel searchInList(List<ExecutionListModel> result, int id) {
        for (ExecutionListModel trtResult : result) {
            if (trtResult.getId() == id) {
                return trtResult;
            }
        }
        return null;
    }
}

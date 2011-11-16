package net.codjo.dataprocess.common.codec;
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
public class ExecutionListModelCodecTest {
    @Test
    public void testEncodeDecode() throws Exception {
        ExecutionListModelCodec codec = new ExecutionListModelCodec();

        ExecutionListModel executionListModel = buildExecutionListModel();
        String result = codec.encode(executionListModel);
        assertThat(result, equalTo("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n"
                                   + "<executionList id=\"1\" name=\"List de traitement 1\" status=\"0\" priority=\"2\" familyId=\"0\">\n"
                                   + "  <treatment id=\"zzzztrt1\" comment=\"comment 1\" priority=\"1\" title=\"montraitement 1\" resultTable=\"table1\" selectAllHandler=\"selectPeriod\"/>\n"
                                   + "  <treatment id=\"ztrt2\" comment=\"comment 2\" priority=\"2\" title=\"montraitement 2\" selectAllHandler=\"SelectAllPeriod\"/>\n"
                                   + "  <treatment id=\"3trt\" priority=\"3\" title=\"montraitement 3\" resultTable=\"RES_TEMP\"/>\n"
                                   + "</executionList>"));
        ExecutionListModel executionListModelDecode = codec.decode(result);
        assertThat(executionListModel.equals(executionListModelDecode), equalTo(true));

        executionListModel = buildExecutionListModel2();
        result = codec.encode(executionListModel);
        assertThat(result, equalTo("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n"
                                   + "<executionList id=\"2\" name=\"List de traitement 2\" status=\"0\" priority=\"4\" familyId=\"0\">\n"
                                   + "  <treatment id=\"zzzztrt1\" comment=\"comment 1\" priority=\"0\" title=\"montraitement 1\"/>\n"
                                   + "  <treatment id=\"ztrt2\" comment=\"comment 2\" priority=\"1\" title=\"montraitement 2\" selectAllHandler=\"SelectAllPeriod\"/>\n"
                                   + "  <treatment id=\"3trt\" priority=\"2\" title=\"montraitement 3\" resultTable=\"RES_TEMP\"/>\n"
                                   + "</executionList>"));
        executionListModelDecode = codec.decode(result);
        assertThat(executionListModel.equals(executionListModelDecode), equalTo(true));
    }


    @Test
    public void testEncodeDecodeList() throws Exception {
        ExecutionListModel executionListModel = buildExecutionListModel();
        ExecutionListModel executionListModel2 = buildExecutionListModel2();
        ExecutionListModelCodec codec = new ExecutionListModelCodec();

        List<ExecutionListModel> list = new ArrayList<ExecutionListModel>();
        list.add(executionListModel);
        list.add(executionListModel2);
        String result = codec.encodeList(list);
        assertThat(result, equalTo("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n"
                                   + "<root>\n"
                                   + "  <executionList id=\"1\" name=\"List de traitement 1\" status=\"0\" priority=\"2\" familyId=\"0\">\n"
                                   + "    <treatment id=\"zzzztrt1\" comment=\"comment 1\" priority=\"1\" title=\"montraitement 1\" resultTable=\"table1\" selectAllHandler=\"selectPeriod\"/>\n"
                                   + "    <treatment id=\"ztrt2\" comment=\"comment 2\" priority=\"2\" title=\"montraitement 2\" selectAllHandler=\"SelectAllPeriod\"/>\n"
                                   + "    <treatment id=\"3trt\" priority=\"3\" title=\"montraitement 3\" resultTable=\"RES_TEMP\"/>\n"
                                   + "  </executionList>\n"
                                   + "  <executionList id=\"2\" name=\"List de traitement 2\" status=\"0\" priority=\"4\" familyId=\"0\">\n"
                                   + "    <treatment id=\"zzzztrt1\" comment=\"comment 1\" priority=\"0\" title=\"montraitement 1\"/>\n"
                                   + "    <treatment id=\"ztrt2\" comment=\"comment 2\" priority=\"1\" title=\"montraitement 2\" selectAllHandler=\"SelectAllPeriod\"/>\n"
                                   + "    <treatment id=\"3trt\" priority=\"2\" title=\"montraitement 3\" resultTable=\"RES_TEMP\"/>\n"
                                   + "  </executionList>\n"
                                   + "</root>"));
        List<ExecutionListModel> list2 = codec.decodeList(result);
        assertThat(list2.size(), equalTo(list.size()));
        assertArrayEquals(list2.toArray(), list.toArray());
    }


    private static ExecutionListModel buildExecutionListModel() {
        ExecutionListModel executionListModel = new ExecutionListModel();
        executionListModel.setId(1);
        executionListModel.setName("List de traitement 1");
        executionListModel.setPriority(2);

        Map<UserTreatment, Integer> map = new HashMap<UserTreatment, Integer>();
        TreatmentModel treatmentModel = new TreatmentModel();
        treatmentModel.setTitle("montraitement 1");
        treatmentModel.setId("zzzztrt1");
        treatmentModel.setComment("comment 1");
        treatmentModel.setResultTable(new ResultTable("table1", "selectPeriod"));
        UserTreatment usrTrt = new UserTreatment(treatmentModel);
        map.put(usrTrt, 1);

        treatmentModel = new TreatmentModel();
        treatmentModel.setTitle("montraitement 2");
        treatmentModel.setId("ztrt2");
        treatmentModel.setComment("comment 2");
        treatmentModel.setResultTable(new ResultTable(null, "SelectAllPeriod"));
        usrTrt = new UserTreatment(treatmentModel);
        map.put(usrTrt, 2);

        treatmentModel = new TreatmentModel();
        treatmentModel.setTitle("montraitement 3");
        treatmentModel.setId("3trt");
        treatmentModel.setResultTable(new ResultTable("RES_TEMP", null));
        usrTrt = new UserTreatment(treatmentModel);
        map.put(usrTrt, 3);

        executionListModel.setPriorityMap(map);
        return executionListModel;
    }


    private static ExecutionListModel buildExecutionListModel2() {
        ExecutionListModel executionListModel = new ExecutionListModel();
        executionListModel.setId(2);
        executionListModel.setName("List de traitement 2");
        executionListModel.setPriority(4);

        TreatmentModel treatmentModel = new TreatmentModel();
        treatmentModel.setTitle("montraitement 1");
        treatmentModel.setId("zzzztrt1");
        treatmentModel.setComment("comment 1");
        UserTreatment usrTrt = new UserTreatment(treatmentModel);
        executionListModel.addUserTreatment(usrTrt);

        treatmentModel = new TreatmentModel();
        treatmentModel.setTitle("montraitement 2");
        treatmentModel.setId("ztrt2");
        treatmentModel.setComment("comment 2");
        treatmentModel.setResultTable(new ResultTable(null, "SelectAllPeriod"));
        usrTrt = new UserTreatment(treatmentModel);
        executionListModel.addUserTreatment(usrTrt);

        treatmentModel = new TreatmentModel();
        treatmentModel.setTitle("montraitement 3");
        treatmentModel.setId("3trt");
        treatmentModel.setResultTable(new ResultTable("RES_TEMP", null));
        usrTrt = new UserTreatment(treatmentModel);
        executionListModel.addUserTreatment(usrTrt);

        return executionListModel;
    }
}

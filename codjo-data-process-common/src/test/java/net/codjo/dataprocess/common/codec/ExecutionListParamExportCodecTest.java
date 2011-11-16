package net.codjo.dataprocess.common.codec;
import net.codjo.dataprocess.common.exception.TreatmentException;
import net.codjo.dataprocess.common.model.ExecutionListModel;
import net.codjo.dataprocess.common.model.ExecutionListParamExport;
import net.codjo.dataprocess.common.model.ExecutionListParamExport.Family;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;

import static net.codjo.test.common.matcher.JUnitMatchers.*;
/**
 *
 */
public class ExecutionListParamExportCodecTest {

    @Test
    public void encode() throws TreatmentException {
        String result = new ExecutionListParamExportCodec().encode(buildTrtExecutionModelList());
        assertThat(result, equalTo("<repository name=\"HB1\">\n"
                                   + "  <family name=\"famille1\">\n"
                                   + "    <executionList name=\"bbb\" priority=\"1\">\n"
                                   + "      <treatment id=\"L1Mino3413\" priority=\"0\"/>\n"
                                   + "      <treatment id=\"L3.GenerationCodeScopOPCVM\" priority=\"1\"/>\n"
                                   + "      <treatment id=\"L5.E.0595.OLD.SubItemsSmsPmvl3\" priority=\"2\"/>\n"
                                   + "    </executionList>\n"
                                   + "    <executionList name=\"aaa\" priority=\"2\">\n"
                                   + "      <treatment id=\"L1.TranscoStockOpcvm\" priority=\"0\"/>\n"
                                   + "      <treatment id=\"L3.GenerationCodeScopOPCVM\" priority=\"1\"/>\n"
                                   + "    </executionList>\n"
                                   + "  </family>\n"
                                   + "  <family name=\"famille2\">\n"
                                   + "    <executionList name=\"uuu\" priority=\"1\"/>\n"
                                   + "  </family>\n"
                                   + "</repository>"));
    }


    @Test
    public void decode() {
        Map<String, String> familyMap = new HashMap<String, String>();
        familyMap.put("1", "famille1");
        familyMap.put("2", "famille2");
        familyMap.put("3", "famille3");

        String xml = "<repository name=\"HB1\">\n"
                     + "  <family name=\"famille1\">\n"
                     + "    <executionList name=\"bbb\" priority=\"1\">\n"
                     + "      <treatment id=\"L1Mino3413\" priority=\"0\"/>\n"
                     + "      <treatment id=\"L3.GenerationCodeScopOPCVM\" priority=\"1\"/>\n"
                     + "      <treatment id=\"L5.E.0595.OLD.SubItemsSmsPmvl3\" priority=\"2\"/>\n"
                     + "    </executionList>\n"
                     + "    <executionList name=\"aaa\" priority=\"2\">\n"
                     + "      <treatment id=\"L1.TranscoStockOpcvm\" priority=\"0\"/>\n"
                     + "      <treatment id=\"L3.GenerationCodeScopOPCVM\" priority=\"1\"/>\n"
                     + "    </executionList>\n"
                     + "  </family>\n"
                     + "  <family name=\"famille2\">\n"
                     + "    <executionList name=\"uuu\" priority=\"2\"/>\n"
                     + "  </family>\n"
                     + "</repository>";
        ExecutionListParamExport model = new ExecutionListParamExportCodec().decode(xml);
        assertThat(model.getName(), equalTo("HB1"));
        List<Family> familyList = model.getFamilyList();
        assertThat(familyList.size(), equalTo(2));
        Family family0 = familyList.get(0);
        Family family1 = familyList.get(1);
        assertThat(family0.getName(), equalTo("famille1"));
        assertThat(family1.getName(), equalTo("famille2"));

        ExecutionListModelCodec listModelCodec = new ExecutionListModelCodec();
        String result = listModelCodec.encodeList(family0.getExecutionListModelList());
        assertThat(result, equalTo("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n"
                                   + "<root>\n"
                                   + "  <executionList id=\"0\" name=\"bbb\" status=\"0\" priority=\"1\" familyId=\"0\">\n"
                                   + "    <treatment id=\"L1Mino3413\" priority=\"0\"/>\n"
                                   + "    <treatment id=\"L3.GenerationCodeScopOPCVM\" priority=\"1\"/>\n"
                                   + "    <treatment id=\"L5.E.0595.OLD.SubItemsSmsPmvl3\" priority=\"2\"/>\n"
                                   + "  </executionList>\n"
                                   + "  <executionList id=\"0\" name=\"aaa\" status=\"0\" priority=\"2\" familyId=\"0\">\n"
                                   + "    <treatment id=\"L1.TranscoStockOpcvm\" priority=\"0\"/>\n"
                                   + "    <treatment id=\"L3.GenerationCodeScopOPCVM\" priority=\"1\"/>\n"
                                   + "  </executionList>\n"
                                   + "</root>"));
        result = listModelCodec.encodeList(family1.getExecutionListModelList());
        assertThat(result, equalTo("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n"
                                   + "<root>\n"
                                   + "  <executionList id=\"0\" name=\"uuu\" status=\"0\" priority=\"2\" familyId=\"0\"/>\n"
                                   + "</root>"));
        model.updateExecutionListFamilyId(familyMap);
        List<ExecutionListModel> list = model.getFamilyList().get(0).getExecutionListModelList();
        assertThat(list.get(0).getFamilyId(), equalTo(1));
        assertThat(list.get(1).getFamilyId(), equalTo(1));

        list = model.getFamilyList().get(1).getExecutionListModelList();
        assertThat(list.get(0).getFamilyId(), equalTo(2));
    }


    private static ExecutionListParamExport buildTrtExecutionModelList() throws TreatmentException {
        Map<String, String> familyMap = new HashMap<String, String>();
        familyMap.put("1", "famille1");
        familyMap.put("2", "famille2");
        familyMap.put("3", "famille3");

        String str = "<root>\n"
                     + "  <executionList id=\"2\" name=\"bbb\" status=\"0\" priority=\"1\" familyId=\"1\">\n"
                     + "    <treatment id=\"L1Mino3413\" comment=\"L1Mino3413\" priority=\"0\" title=\"Boucle autour du parametrage PM_SCHEMA_MINO\"/>\n"
                     + "    <treatment id=\"L3.GenerationCodeScopOPCVM\" comment=\"L3.GenerationCodeScopOPCVM&gt;\" priority=\"1\" title=\"L3.GenerationCodeScopOPCVM\"/>\n"
                     + "    <treatment id=\"L5.E.0595.OLD.SubItemsSmsPmvl3\" comment=\"repassation des 070 074 de 4814*\" priority=\"2\" title=\"SubItems_Treatment3\"/>\n"
                     + "  </executionList>\n"
                     + "  <executionList id=\"3\" name=\"uuu\" status=\"0\" priority=\"2\" familyId=\"2\"/>\n"
                     + "  <executionList id=\"1\" name=\"aaa\" status=\"0\" priority=\"2\" familyId=\"1\">\n"
                     + "    <treatment id=\"L1.TranscoStockOpcvm\" priority=\"0\" title=\"transco AP_STOCK_OPCVM&gt; TI_CSCOP_STOCK_OPCVM_10 en HB2 et HB3\"/>\n"
                     + "    <treatment id=\"L3.GenerationCodeScopOPCVM\" comment=\"L3.GenerationCodeScopOPCVM&gt;\" priority=\"1\" title=\"L3.GenerationCodeScopOPCVM\"/>\n"
                     + "  </executionList>\n"
                     + "</root>";
        List<ExecutionListModel> trtExecutionListModel = new ExecutionListModelCodec().decodeList(str);
        return new ExecutionListParamExport("HB1", trtExecutionListModel, familyMap);
    }
}

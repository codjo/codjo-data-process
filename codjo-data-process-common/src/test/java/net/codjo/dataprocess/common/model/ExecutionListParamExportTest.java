package net.codjo.dataprocess.common.model;
import net.codjo.dataprocess.common.model.ExecutionListParamExport.Family;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;

import static net.codjo.test.common.matcher.JUnitMatchers.*;
/**
 *
 */
public class ExecutionListParamExportTest {
    @Test
    public void content() {
        Map<String, String> familyMap = new HashMap<String, String>();
        familyMap.put("1", "famille1");
        familyMap.put("2", "famille2");
        familyMap.put("3", "famille3");

        ExecutionListParamExport paramModel = new ExecutionListParamExport("HB1",
                                                                           buildTrtExecutionModelList(),
                                                                           familyMap);
        assertThat(paramModel.getName(), equalTo("HB1"));
        List<Family> familyList = paramModel.getFamilyList();
        assertThat(familyList.size(), equalTo(2));
        Family family0 = familyList.get(0);
        Family family1 = familyList.get(1);
        assertThat(family0.getName(), equalTo("famille1"));
        assertThat(family1.getName(), equalTo("famille2"));

        List<ExecutionListModel> list0 = family0.getExecutionListModelList();
        List<ExecutionListModel> list1 = family1.getExecutionListModelList();
        assertThat(list0.size(), equalTo(2));
        assertThat(list1.size(), equalTo(1));

        assertThat(list0.get(0).getName(), equalTo("bbb"));
        assertThat(list0.get(0).getPriority(), equalTo(1));

        assertThat(list0.get(1).getName(), equalTo("aaa"));
        assertThat(list0.get(1).getPriority(), equalTo(2));

        assertThat(list1.get(0).getName(), equalTo("uuu"));
        assertThat(list1.get(0).getPriority(), equalTo(1));
    }


    private static List<ExecutionListModel> buildTrtExecutionModelList() {
        List<ExecutionListModel> list = new ArrayList<ExecutionListModel>();

        ExecutionListModel elm = new ExecutionListModel();
        elm.setId(2);
        elm.setName("bbb");
        elm.setStatus(0);
        elm.setPriority(1);
        elm.setFamilyId(1);

        TreatmentModel treatmentModel = new TreatmentModel();
        treatmentModel.setId("L1Mino3413");
        treatmentModel.setComment("L1Mino3413");
        treatmentModel.setTitle("Boucle autour du parametrage PM_SCHEMA_MINO");
        UserTreatment ut = new UserTreatment(treatmentModel);
        ut.setPriority(0);
        elm.addUserTreatment(ut);

        treatmentModel = new TreatmentModel();
        treatmentModel.setId("L3.GenerationCodeScopOPCVM");
        treatmentModel.setComment("L3.GenerationCodeScopOPCVM");
        treatmentModel.setTitle("L3.GenerationCodeScopOPCVM");
        ut = new UserTreatment(treatmentModel);
        ut.setPriority(1);
        elm.addUserTreatment(ut);

        treatmentModel = new TreatmentModel();
        treatmentModel.setId("L5.E.0595.OLD.SubItemsSmsPmvl3");
        treatmentModel.setComment("repassation des 070 074 de 4814*");
        treatmentModel.setTitle("SubItems_Treatment3");
        ut = new UserTreatment(treatmentModel);
        ut.setPriority(2);
        elm.addUserTreatment(ut);
        list.add(elm);

        elm = new ExecutionListModel();
        elm.setId(3);
        elm.setName("uuu");
        elm.setStatus(0);
        elm.setPriority(2);
        elm.setFamilyId(2);
        list.add(elm);

        elm = new ExecutionListModel();
        elm.setId(1);
        elm.setName("aaa");
        elm.setStatus(0);
        elm.setPriority(3);
        elm.setFamilyId(1);

        treatmentModel = new TreatmentModel();
        treatmentModel.setId("L1.TranscoStockOpcvm");
        treatmentModel.setTitle("transco AP_STOCK_OPCVM TI_CSCOP_STOCK_OPCVM_10 en HB2 et HB3");
        ut = new UserTreatment(treatmentModel);
        ut.setPriority(0);
        elm.addUserTreatment(ut);

        treatmentModel = new TreatmentModel();
        treatmentModel.setId("L3.GenerationCodeScopOPCVM");
        treatmentModel.setComment("L3.GenerationCodeScopOPCVM");
        treatmentModel.setTitle("L3.GenerationCodeScopOPCVM");
        ut = new UserTreatment(treatmentModel);
        ut.setPriority(1);
        elm.addUserTreatment(ut);

        list.add(elm);
        return list;
    }
}

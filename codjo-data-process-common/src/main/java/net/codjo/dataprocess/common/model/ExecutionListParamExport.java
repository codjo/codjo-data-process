package net.codjo.dataprocess.common.model;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 *
 */
public class ExecutionListParamExport {
    private String name;
    private List<Family> familyList = new ArrayList<Family>();


    public ExecutionListParamExport(String name,
                                    List<ExecutionListModel> listModel,
                                    Map<String, String> familyMap) {
        this.name = name;
        Collections.sort(listModel, ExecutionListModel.getPriorityComparator());
        for (ExecutionListModel executionListModel : listModel) {
            String familyName = familyMap.get(String.valueOf(executionListModel.getFamilyId()));
            Family family = getFamily(familyName);
            family.addExecutionListModel(executionListModel);
        }
    }


    public void updateExecutionListFamilyId(Map<String, String> familyMap) {
        Map<String, String> reverseFamilyMap = new HashMap<String, String>();
        for (Map.Entry<String, String> entry : familyMap.entrySet()) {
            reverseFamilyMap.put(entry.getValue(), entry.getKey());
        }
        for (Family family : familyList) {
            String familyId = reverseFamilyMap.get(family.getName());
            if (familyId != null) {
                for (ExecutionListModel executionListModel : family.getExecutionListModelList()) {
                    executionListModel.setFamilyId(Integer.parseInt(familyId));
                }
            }
        }
    }


    private Family getFamily(String familyName) {
        for (Family family : familyList) {
            if (family.getName().equals(familyName)) {
                return family;
            }
        }
        Family family = new Family(familyName);
        familyList.add(family);
        return family;
    }


    public String getName() {
        return name;
    }


    public List<Family> getFamilyList() {
        return familyList;
    }


    public static class Family {
        private String name;
        private List<ExecutionListModel> executionListModelList = new ArrayList<ExecutionListModel>();


        private Family(String familyName) {
            this.name = familyName;
        }


        public String getName() {
            return name;
        }


        public void addExecutionListModel(ExecutionListModel executionListModel) {
            executionListModel.setPriority(executionListModelList.size() + 1);
            executionListModelList.add(executionListModel);
        }


        public List<ExecutionListModel> getExecutionListModelList() {
            return executionListModelList;
        }
    }
}

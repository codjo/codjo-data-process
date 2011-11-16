/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.common.model;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
/**
 *
 */
public class ExecutionListModel implements Serializable {
    private int id;
    private String name;
    private Map<UserTreatment, Integer> priorityMap = new HashMap<UserTreatment, Integer>();
    private int priority = 0;
    private int status;
    private int familyId;
    private Timestamp executionDate;


    public ExecutionListModel() {
    }


    private static boolean equalPriorityMap(Map<UserTreatment, Integer> map1,
                                            Map<UserTreatment, Integer> map2) {
        if (map1.size() == map2.size()) {
            for (Entry<UserTreatment, Integer> entry : map1.entrySet()) {
                if (entry.getValue().intValue() != map2.get(searchAnItem(entry.getKey(), map2)).intValue()) {
                    return false;
                }
            }
            return true;
        }
        else {
            return false;
        }
    }


    static UserTreatment searchAnItem(UserTreatment userTreatment, Map<UserTreatment, Integer> map) {
        for (UserTreatment aUserTreatment : map.keySet()) {
            if (aUserTreatment.equals(userTreatment)) {
                return aUserTreatment;
            }
        }
        return null;
    }


    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public boolean isEmpty() {
        return priorityMap.isEmpty();
    }


    public void addUserTreatment(UserTreatment trt) {
        priorityMap.put(trt, priorityMap.size());
    }


    public void removeUserTreatment(UserTreatment trt) {
        priorityMap.remove(trt);
    }


    public void addUserTreatment(UserTreatment trt, Integer localPriority) {
        priorityMap.put(trt, localPriority);
    }


    public Map<UserTreatment, Integer> getPriorityMap() {
        return priorityMap;
    }


    public UserTreatment getTreatmentByPriority(Integer localPriority) {
        for (Entry<UserTreatment, Integer> entry : priorityMap.entrySet()) {
            if (entry.getValue().equals(localPriority)) {
                return entry.getKey();
            }
        }
        return null;
    }


    public void setPriorityMap(Map<UserTreatment, Integer> priorityMap) {
        this.priorityMap = priorityMap;
    }


    public int getPriority() {
        return priority;
    }


    public void setPriority(int priority) {
        this.priority = priority;
    }


    public int getStatus() {
        return status;
    }


    public void setStatus(int status) {
        this.status = status;
    }


    public int getFamilyId() {
        return familyId;
    }


    public void setFamilyId(int familyId) {
        this.familyId = familyId;
    }


    public Timestamp getExecutionDate() {
        return executionDate;
    }


    public void setExecutionDate(Timestamp executionDate) {
        this.executionDate = executionDate;
    }


    public static Comparator<ExecutionListModel> getPriorityComparator() {
        return new ExecutionListPriorityComparator();
    }


    public static ExecutionListModel buildExecutionListModel(ResultSet rs) throws SQLException {
        ExecutionListModel executionListModel = new ExecutionListModel();
        executionListModel.setId(rs.getInt("EXECUTION_LIST_ID"));
        executionListModel.setName(rs.getString("EXECUTION_LIST_NAME"));
        executionListModel.setPriority(rs.getInt("PRIORITY"));
        executionListModel.setStatus(rs.getInt("STATUS"));
        executionListModel.setExecutionDate(rs.getTimestamp("EXECUTION_DATE"));
        executionListModel.setFamilyId(rs.getInt("FAMILY_ID"));
        return executionListModel;
    }


    public List<UserTreatment> getSortedTreatmentList() {
        List<UserTreatment> sortedList = new ArrayList<UserTreatment>();

        for (Entry<UserTreatment, Integer> entry : priorityMap.entrySet()) {
            entry.getKey().setPriority(entry.getValue());
            sortedList.add(entry.getKey());
        }

        return UserTreatment.orderList(sortedList);
    }


    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("name : ").append(name).append('\n');
        str.append("id : ").append(id).append('\n');
        str.append("priority : ").append(priority).append('\n');
        str.append("family : ").append(familyId).append('\n');
        str.append("status : ").append(status).append('\n');
        str.append("executionDate : ").append(executionDate).append('\n');
        str.append("Traitements de la liste :\n");
        str.append("-------------------------\n");

        List<UserTreatment> sortedList = getSortedTreatmentList();
        for (UserTreatment trt : sortedList) {
            str.append(trt.toString()).append('\n');
            str.append("-------------------------\n");
        }
        return str.toString();
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ExecutionListModel execListModel = (ExecutionListModel)obj;
        boolean eq = execListModel.getId() == id && execListModel.getFamilyId() == familyId
                     && execListModel.getName().equals(name)
                     && execListModel.getStatus() == status
                     && execListModel.getPriority() == priority;

        if (execListModel.getPriorityMap() != null && priorityMap != null) {
            eq = eq && equalPriorityMap(execListModel.getPriorityMap(), priorityMap);
        }
        else {
            eq = eq && execListModel.getPriorityMap() == priorityMap;
        }

        if (execListModel.getExecutionDate() != null && executionDate != null) {
            eq = eq && execListModel.getExecutionDate().equals(executionDate);
        }
        else {
            eq = eq && execListModel.getExecutionDate() == executionDate;
        }
        return eq;
    }


    private static class ExecutionListPriorityComparator implements Comparator<ExecutionListModel> {
        public int compare(ExecutionListModel e1, ExecutionListModel e2) {
            if (e1.getPriority() == e2.getPriority()) {
                return 0;
            }
            else if (e1.getPriority() < e2.getPriority()) {
                return -1;
            }
            else {
                return 1;
            }
        }
    }
}

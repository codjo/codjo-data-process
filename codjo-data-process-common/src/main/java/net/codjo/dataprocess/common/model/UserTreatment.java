/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.common.model;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
/**
 *
 */
public class UserTreatment {
    private TreatmentModel treatmentModel;
    private int priority;


    public UserTreatment(TreatmentModel treatmentModel) {
        this.treatmentModel = treatmentModel;
    }


    public String getId() {
        return treatmentModel.getId();
    }


    public String getComment() {
        return treatmentModel.getComment();
    }


    public String getTitle() {
        return treatmentModel.getTitle();
    }


    public ResultTable getResultTable() {
        return treatmentModel.getResultTable();
    }


    public void setPriority(int priority) {
        this.priority = priority;
    }


    public int getPriority() {
        return priority;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof UserTreatment)) {
            return false;
        }
        return getId().equals(((UserTreatment)obj).getId());
    }


    @Override
    public String toString() {
        String result;
        result = "id : " + getId()
                 + "\tpriority : " + getPriority()
                 + "\tcomment : " + getComment()
                 + "\ttitle : " + getTitle();
        if (getResultTable() != null) {
            result = result + "\tresultTableName : " + getResultTable().getTable()
                     + "\tselectAllHandler : " + getResultTable().getSelectAllHandler();
        }
        return result;
    }


    public static List<UserTreatment> orderList(List<UserTreatment> list) {
        Collections.sort(list, new UserTreatmentPriorityComparator());
        return list;
    }


    public static List<UserTreatment> buildUserTrtListWithPriority(Map<UserTreatment, Integer> userTrtMap) {
        List<UserTreatment> list = new ArrayList<UserTreatment>();

        for (Entry<UserTreatment, Integer> entry : userTrtMap.entrySet()) {
            entry.getKey().setPriority(entry.getValue());
            list.add(entry.getKey());
        }
        return list;
    }


    private static class UserTreatmentPriorityComparator implements Comparator<UserTreatment> {
        public int compare(UserTreatment u1, UserTreatment u2) {
            if (u1.getPriority() == u2.getPriority()) {
                return 0;
            }
            else if (u1.getPriority() > u2.getPriority()) {
                return 1;
            }
            else {
                return -1;
            }
        }
    }
}

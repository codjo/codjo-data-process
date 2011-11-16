package net.codjo.dataprocess.common.model;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;

import static net.codjo.dataprocess.common.DataProcessConstants.BSH_TYPE_WITH_RESULT;
import static net.codjo.dataprocess.common.DataProcessConstants.JAVA_TYPE_WITH_RESULT;
import static net.codjo.dataprocess.common.DataProcessConstants.SQL_QUERY_TYPE_WITH_RESULT;
import static net.codjo.dataprocess.common.DataProcessConstants.STORED_PROC_TYPE_WITH_RESULT;
/**
 *
 */
public class TreatmentModel {
    private String id;
    private String type;
    private String title;
    private String comment;
    private String target;
    private ResultTable resultTable;
    private String guiTarget;
    private ArgList arguments;


    public TreatmentModel() {
    }


    public TreatmentModel(TreatmentModel treatmentModel) {
        title = treatmentModel.getTitle();
        type = treatmentModel.getType();
        target = treatmentModel.getTarget();
        guiTarget = treatmentModel.getGuiTarget();
        id = treatmentModel.getId();
        comment = treatmentModel.getComment();
        resultTable = treatmentModel.getResultTable();
        arguments = new ArgList(treatmentModel.getArguments());
    }


    public void setId(String id) {
        this.id = id;
    }


    public String getId() {
        return id;
    }


    public void setType(String type) {
        this.type = type;
    }


    public String getType() {
        return type;
    }


    public void setTarget(String target) {
        this.target = target;
    }


    public String getTarget() {
        if (target != null) {
            return target.trim();
        }
        else {
            return target;
        }
    }


    public void setGuiTarget(String guiTarget) {
        this.guiTarget = guiTarget;
    }


    public String getGuiTarget() {
        if (guiTarget != null) {
            return guiTarget.trim();
        }
        else {
            return guiTarget;
        }
    }


    public void setArguments(ArgList arguments) {
        this.arguments = arguments;
    }


    public ArgList getArguments() {
        if (arguments == null) {
            arguments = new ArgList();
        }

        return arguments;
    }


    public void setComment(String comment) {
        this.comment = comment;
    }


    public String getComment() {
        return comment;
    }


    public void setTitle(String title) {
        this.title = title;
    }


    public String getTitle() {
        return title;
    }


    public void setResultTable(ResultTable resultTable) {
        this.resultTable = resultTable;
    }


    public ResultTable getResultTable() {
        return resultTable;
    }


    public boolean isReturnResult() {
        return SQL_QUERY_TYPE_WITH_RESULT.equals(type) || STORED_PROC_TYPE_WITH_RESULT.equals(type)
               || JAVA_TYPE_WITH_RESULT.equals(type) || BSH_TYPE_WITH_RESULT.equals(type);
    }


    public List<String> getTargetGuiClassParameters() {
        List<String> parameters = null;
        if (guiTarget != null) {
            if (guiTarget.indexOf('(') != -1) {
                parameters = stringToList(guiTarget.substring(guiTarget.indexOf('(')
                                                              + 1, guiTarget.length() - 1));
            }
        }
        return parameters;
    }


    private static List<String> stringToList(String parameters) {
        List<String> parametersList = new ArrayList<String>();

        StringTokenizer stringTokenizer = new StringTokenizer(parameters, ",");
        while (stringTokenizer.hasMoreTokens()) {
            String token = stringTokenizer.nextToken().trim();
            parametersList.add(token);
        }
        return parametersList;
    }


    public String getTargetGuiClassName() {
        if (guiTarget != null) {
            if (guiTarget.indexOf('(') != -1) {
                return guiTarget.substring(0, guiTarget.indexOf('(')).trim();
            }
        }
        return "";
    }


    public boolean isConfigurable(List<String> exclude) {
        for (ArgModel argModel : getArguments().getArgs()) {
            if (argModel.isGlobalValue() && !exclude.contains(argModel.getValue())) {
                return true;
            }
        }
        return false;
    }


    public static Comparator<TreatmentModel> getIdComparator() {
        return new TreatmentModelIdComparator();
    }


    private static class TreatmentModelIdComparator implements Comparator<TreatmentModel> {
        public int compare(TreatmentModel tm1, TreatmentModel tm2) {
            return tm1.getId().compareToIgnoreCase(tm2.getId());
        }
    }


    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("\nid : ").append(id);
        str.append("\ntitle : ").append(title);
        str.append("\ntype : ").append(type);
        str.append("\ntarget : ").append(target);
        str.append("\nguiTarget : ").append(guiTarget);
        str.append("\ncomment : ").append(comment);
        str.append("\nresultTable : ").append(resultTable);
        str.append("\narguments : ").append(arguments).append('\n');
        return str.toString();
    }
}

package net.codjo.dataprocess.gui.launcher.result.table;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 *
 */
public class TreatmentResultList {

    private List<TreatmentResult> treatmentList = new ArrayList<TreatmentResult>();
    private Map<String, TreatmentResult> treatmentIds = new HashMap<String, TreatmentResult>();


    void add(TreatmentResult treatmentResult) {
        treatmentList.add(treatmentResult);
        treatmentIds.put(treatmentResult.getId(), treatmentResult);
    }


    public List<TreatmentResult> getTreatmentList() {
        return new ArrayList<TreatmentResult>(treatmentList);
    }


    public Map<String, TreatmentResult> getTreatmentIds() {
        return new HashMap<String, TreatmentResult>(treatmentIds);
    }


    public int getSize() {
        return treatmentList.size();
    }


    public TreatmentResult getTreatmentResultByIndex(int index) {
        return treatmentList.get(index);
    }


    public TreatmentResult getTreatmentResultById(String id) {
        return treatmentIds.get(id);
    }
}

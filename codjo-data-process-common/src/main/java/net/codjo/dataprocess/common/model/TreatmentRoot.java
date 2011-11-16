/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.common.model;
import net.codjo.dataprocess.common.exception.TreatmentException;
import java.util.ArrayList;
import java.util.List;
/**
 *
 */
public class TreatmentRoot {
    private List<TreatmentModel> treatmentModelList;


    public TreatmentRoot() {
    }


    public TreatmentRoot(TreatmentRoot treatmentRoot) {
        List<TreatmentModel> treatmentList = treatmentRoot.getTreatmentModelList();

        if (treatmentList != null) {
            for (TreatmentModel treatmentModel : treatmentList) {
                getTreatmentModelList().add(new TreatmentModel(treatmentModel));
            }
        }
    }


    public List<TreatmentModel> getTreatmentModelList() {
        if (treatmentModelList == null) {
            treatmentModelList = new ArrayList<TreatmentModel>();
        }
        return treatmentModelList;
    }


    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();

        if (treatmentModelList != null) {
            for (TreatmentModel treatmentModel : treatmentModelList) {
                buffer.append(treatmentModel.getId()).append(';');
            }
        }
        else {
            buffer.append("treatmentModelList == null");
        }
        return buffer.toString();
    }


    public TreatmentModel getTreatmentModel(String id) throws TreatmentException {
        List<TreatmentModel> treatmentList = getTreatmentModelList();

        if (treatmentList != null) {
            for (TreatmentModel treatmentModel : treatmentList) {
                if (treatmentModel.getId().equals(id)) {
                    return treatmentModel;
                }
            }
        }
        throw new TreatmentException("Le traitement id = '" + id + "' est inexistant.");
    }
}

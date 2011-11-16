/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.gui.util;
import net.codjo.dataprocess.client.RepositoryClientHelper;
import net.codjo.dataprocess.common.codec.TreatmentRootCodec;
import net.codjo.dataprocess.common.exception.RepositoryException;
import net.codjo.dataprocess.common.model.TreatmentModel;
import net.codjo.dataprocess.common.model.TreatmentRoot;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.gui.framework.MutableGuiContext;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.DefaultListModel;
/**
 * Model de données utilitaire : stock notamment la map repositoryMap qui sert de cache entre l'IHM et la
 * BDD.
 */
public class RepositoryWindowModel {
    private static final String ENCODING = "ISO-8859-1";
    private Map<String, TreatmentRoot> repositoryMap = new HashMap<String, TreatmentRoot>();


    public TreatmentRoot getTreatmentRoot(MutableGuiContext ctxt, int repositoryId) throws RequestException {
        TreatmentRoot treatmentRoot = repositoryMap.get(Integer.toString(repositoryId));
        if (treatmentRoot == null) {
            treatmentRoot = TreatmentRootCodec.decode(RepositoryClientHelper.getRepositoryContent(ctxt,
                                                                                                  repositoryId));
        }
        return treatmentRoot;
    }


    public void addTreatmentRootToMap(int repositoryId, TreatmentRoot treatmentRoot) {
        repositoryMap.put(Integer.toString(repositoryId), treatmentRoot);
    }


    public void deleteTreatmentRootFromMap(int repositoryId) {
        repositoryMap.remove(Integer.toString(repositoryId));
    }


    public void saveRepositoryMap(MutableGuiContext ctxt) throws RepositoryException, RequestException {
        for (Entry<String, TreatmentRoot> entry : repositoryMap.entrySet()) {
            TreatmentRoot treatmentRoot = entry.getValue();

            String content = TreatmentRootCodec.encode(treatmentRoot);
            content = content.replace("<?xml version=\"1.0\"?>",
                                      "<?xml version=\"1.0\" encoding=\"" + ENCODING + "\"?>");

            String treatmentId =
                  RepositoryClientHelper.updateRepository(ctxt, Integer.parseInt(entry.getKey()), content);
            if (!"OK".equals(treatmentId)) {
                throw new RepositoryException(treatmentId
                                              + " est trop long ! La sauvegarde n'a pas été effectuée.");
            }
        }
    }


    public void initTreatmentListModel(MutableGuiContext ctxt,
                                       String repositoryId,
                                       DefaultListModel listModel,
                                       boolean sort) throws RequestException {
        TreatmentRoot treatmentRoot = getTreatmentRoot(ctxt, Integer.parseInt(repositoryId));
        addTreatmentRootToMap(Integer.parseInt(repositoryId), treatmentRoot);
        listModel.clear();

        List<TreatmentModel> treatmentList = treatmentRoot.getTreatmentModelList();
        if (sort) {
            TreatmentModel[] treatmentListAsArray = treatmentList
                  .toArray(new TreatmentModel[treatmentList.size()]);
            Arrays.sort(treatmentListAsArray, TreatmentModel.getIdComparator());
            treatmentList = Arrays.asList(treatmentListAsArray);
        }
        if (treatmentList != null) {
            for (TreatmentModel treatmentModel : treatmentList) {
                listModel.addElement(treatmentModel);
            }
        }
    }
}

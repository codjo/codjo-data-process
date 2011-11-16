package net.codjo.dataprocess.server.kernel;
import net.codjo.dataprocess.common.Log;
import net.codjo.dataprocess.common.exception.TreatmentException;
import net.codjo.dataprocess.common.model.ExecutionListModel;
import net.codjo.dataprocess.common.model.TreatmentModel;
import java.sql.Connection;

import static net.codjo.dataprocess.common.DataProcessConstants.BSH_TYPE;
import static net.codjo.dataprocess.common.DataProcessConstants.BSH_TYPE_WITH_RESULT;
import static net.codjo.dataprocess.common.DataProcessConstants.JAVA_TYPE;
import static net.codjo.dataprocess.common.DataProcessConstants.JAVA_TYPE_WITH_RESULT;
import static net.codjo.dataprocess.common.DataProcessConstants.SQL_QUERY_TYPE;
import static net.codjo.dataprocess.common.DataProcessConstants.SQL_QUERY_TYPE_WITH_RESULT;
import static net.codjo.dataprocess.common.DataProcessConstants.STORED_PROC_TYPE;
import static net.codjo.dataprocess.common.DataProcessConstants.STORED_PROC_TYPE_WITH_RESULT;
/**
 *
 */
class TreatmentFactory {
    private TreatmentModel treatmentModel;


    TreatmentFactory(TreatmentModel treatmentModel) {
        this.treatmentModel = treatmentModel;
    }


    public AbstractTreatment build(Connection con, int repositoryId, ExecutionListModel executionListModel)
          throws TreatmentException {
        String type = treatmentModel.getType();
        if (SQL_QUERY_TYPE.equals(type)) {
            return SqlTreatment.create(con, treatmentModel, repositoryId, executionListModel);
        }
        else if (SQL_QUERY_TYPE_WITH_RESULT.equals(type)) {
            return SqlTreatmentWithResult.create(con, treatmentModel, repositoryId, executionListModel);
        }
        else if (STORED_PROC_TYPE.equals(type)) {
            return StoredProcTreatment.create(con, treatmentModel, repositoryId, executionListModel);
        }
        else if (STORED_PROC_TYPE_WITH_RESULT.equals(type)) {
            return StoredProcTreatmentWithResult.create(con,
                                                        treatmentModel,
                                                        repositoryId,
                                                        executionListModel);
        }
        else if (BSH_TYPE.equals(type) || BSH_TYPE_WITH_RESULT.equals(type)) {
            return BshScriptTreatment.create(con, treatmentModel, repositoryId, executionListModel);
        }
        else if (JAVA_TYPE.equals(type) || JAVA_TYPE_WITH_RESULT.equals(type)) {
            String className = treatmentModel.getTarget();
            try {
                if (Log.isDebugEnabled()) {
                    Log.debug(getClass(), String.format("--> Instanciation de la classe '%s'"
                                                        + " pour le traitement '%s' de type '%s'",
                                                        className, treatmentModel.getId(), type));
                }
                AbstractTreatment treatment = (AbstractTreatment)Class.forName(className).newInstance();
                treatment.setConnection(con);
                treatment.setTreatmentModel(treatmentModel);
                treatment.setExecutionListModel(executionListModel);
                treatment.setRepositoryId(repositoryId);
                treatment.buildArgument();
                return treatment;
            }
            catch (ClassNotFoundException ex) {
                String errorMessage = String.format("La classe '%s' du traitement %s '%s' n'existe pas",
                                                    className, type, treatmentModel.getId());
                Log.error(getClass(), errorMessage, ex);
                throw new TreatmentException(errorMessage, ex);
            }
            catch (Exception ex) {
                String errorMessage = String.format("Problème d'instanciation/accessibilité de la classe '%s'"
                                                    + " du traitement %s '%s'",
                                                    className, type, treatmentModel.getId());
                Log.error(getClass(), errorMessage, ex);
                throw new TreatmentException(errorMessage, ex);
            }
        }
        else {
            String errorMessage = "Type de traitement inconnu : " + type;
            Log.error(getClass(), errorMessage);
            throw new TreatmentException(errorMessage);
        }
    }


    static AbstractTreatment buildTreatment(Connection con, TreatmentModel treatmentModel)
          throws TreatmentException {
        return buildTreatment(con, treatmentModel, 0, null);
    }


    static AbstractTreatment buildTreatment(Connection con,
                                            TreatmentModel treatmentModel,
                                            int repositoryId) throws TreatmentException {
        return buildTreatment(con, treatmentModel, repositoryId, null);
    }


    static AbstractTreatment buildTreatment(Connection con,
                                            TreatmentModel treatmentModel,
                                            int repositoryId,
                                            ExecutionListModel executionListModel)
          throws TreatmentException {
        TreatmentFactory treatmentFactory = new TreatmentFactory(treatmentModel);
        return treatmentFactory.build(con, repositoryId, executionListModel);
    }
}

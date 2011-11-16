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
public class ExecutionListStoreHelper {
    private List<ExecutionListModel> execListModelList;
    private List<ExecListModelToDelete> execListsModelToDelete;


    public ExecutionListStoreHelper() {
        execListModelList = new ArrayList<ExecutionListModel>();
        execListsModelToDelete = new ArrayList<ExecListModelToDelete>();
    }


    public List<ExecListModelToDelete> getExecListsToDelete() {
        return execListsModelToDelete;
    }


    public void addExecutionList(ExecutionListModel executionListModel) throws TreatmentException {
        if (isAlreadyInRepository(executionListModel)) {
            throw new TreatmentException(String.format("'%s' existe déjà dans la liste de traitements !",
                                                       executionListModel.getName()));
        }
        execListModelList.add(executionListModel);
    }


    public void deleteExecutionList(int repositoryId, ExecutionListModel executionListModel) {
        execListModelList.remove(executionListModel);
        execListsModelToDelete.add(new ExecListModelToDelete(repositoryId, executionListModel));
    }


    public List<ExecutionListModel> getRepository() {
        return execListModelList;
    }


    boolean isAlreadyInRepository(ExecutionListModel executionListModel) {
        for (ExecutionListModel execListModel : execListModelList) {
            if (executionListModel.getName().equals(execListModel.getName())) {
                return true;
            }
        }

        return false;
    }


    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        for (ExecutionListModel executionListModel : execListModelList) {
            buffer.append(executionListModel.toString());
            buffer.append("\n******************************************************\n");
        }
        return buffer.toString();
    }


    public static class ExecListModelToDelete {
        private int repositoryId;
        private ExecutionListModel executionListModel;


        ExecListModelToDelete(int repositoryId, ExecutionListModel executionListModel) {
            this.repositoryId = repositoryId;
            this.executionListModel = executionListModel;
        }


        public ExecutionListModel getExecutionListModel() {
            return executionListModel;
        }


        public int getRepositoryId() {
            return repositoryId;
        }
    }
}

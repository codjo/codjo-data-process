package net.codjo.dataprocess.client;
/**
 *
 */
public class ExecutionListDB {
    private String executionListId;
    private String repositoryId;
    private String executionListName;
    private String repositoryName;
    private String familyId;
    private String priority;


    public ExecutionListDB(String executionListId, String repositoryId, String executionListName,
                           String repositoryName, String familyId, String priority) {
        this.executionListId = executionListId;
        this.repositoryId = repositoryId;
        this.executionListName = executionListName;
        this.repositoryName = repositoryName;
        this.familyId = familyId;
        this.priority = priority;
    }


    public String getFamilyId() {
        return familyId;
    }


    public String getPriority() {
        return priority;
    }


    public String getExecutionListId() {
        return executionListId;
    }


    public String getRepositoryId() {
        return repositoryId;
    }


    public String getExecutionListName() {
        return executionListName;
    }


    public String getRepositoryName() {
        return repositoryName;
    }
}

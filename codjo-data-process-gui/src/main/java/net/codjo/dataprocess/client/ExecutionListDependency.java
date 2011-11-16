package net.codjo.dataprocess.client;
import java.util.List;
/**
 *
 */
public class ExecutionListDependency {
    private boolean isCycle;
    private List<String> executionList;


    public ExecutionListDependency(boolean isCycle, List<String> executionList) {
        this.isCycle = isCycle;
        this.executionList = executionList;
    }


    public boolean isCycle() {
        return isCycle;
    }


    public List<String> getExecutionList() {
        return executionList;
    }
}

package net.codjo.dataprocess.common.util;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.util.List;
/**
 *
 */
public class ExecListParamImportReport {
    public enum ErrorType {
        NO_ERROR,
        FAMILY_DONT_EXIST,
        NO_FAMILY,
        EXECUTION_LIST_ALLREADY_EXIST
    }
    private List<String> missingFamilyList;
    private List<String> allreadyExistExecutionList;
    private ErrorType errorType = ErrorType.NO_ERROR;
    private String errorMessage;


    public ExecListParamImportReport() {
    }


    public ExecListParamImportReport(ErrorType errorType) {
        this.errorType = errorType;
    }


    public List<String> getMissingFamilyList() {
        return missingFamilyList;
    }


    public void setMissingFamilyList(List<String> missingFamilyList) {
        this.missingFamilyList = missingFamilyList;
    }


    public ErrorType getErrorType() {
        return errorType;
    }


    public void setErrorType(ErrorType errorType) {
        this.errorType = errorType;
    }


    public boolean hasError() {
        return !(ErrorType.NO_ERROR == errorType);
    }


    public List<String> getAllreadyExistExecutionList() {
        return allreadyExistExecutionList;
    }


    public void setAllreadyExistExecutionList(List<String> allreadyExistExecutionList) {
        this.allreadyExistExecutionList = allreadyExistExecutionList;
    }


    public String getErrorMessage() {
        return errorMessage;
    }


    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }


    public String encode() {
        XStream xstream = new XStream(new DomDriver());
        return xstream.toXML(this);
    }


    public static ExecListParamImportReport decode(String xml) {
        XStream xstream = new XStream(new DomDriver());
        return (ExecListParamImportReport)xstream.fromXML(xml);
    }
}

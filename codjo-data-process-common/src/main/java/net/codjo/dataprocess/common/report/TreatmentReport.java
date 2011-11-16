package net.codjo.dataprocess.common.report;
/**
 *
 */
public class TreatmentReport {
    private String treatmentId;
    private String result;
    private String errorMessage;


    public TreatmentReport(String treatmentId) {
        this.treatmentId = treatmentId;
    }


    public TreatmentReport(String treatmentId, String result, String errorMessage) {
        this(treatmentId);
        this.result = result;
        this.errorMessage = errorMessage;
    }


    public String getTreatmentId() {
        return treatmentId;
    }


    public void setResult(String result) {
        this.result = result;
    }


    public String getResult() {
        return result;
    }


    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }


    public String getErrorMessage() {
        return errorMessage;
    }


    public boolean isError() {
        return errorMessage != null;
    }


    public String toString() {
        return treatmentId + (errorMessage != null ? ": " + errorMessage : ": ok");
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TreatmentReport that = (TreatmentReport)o;

        if (errorMessage != null ? !errorMessage.equals(that.errorMessage) : that.errorMessage != null) {
            return false;
        }
        if (result != null ? !result.equals(that.result) : that.result != null) {
            return false;
        }
        if (treatmentId != null ? !treatmentId.equals(that.treatmentId) : that.treatmentId != null) {
            return false;
        }

        return true;
    }
}

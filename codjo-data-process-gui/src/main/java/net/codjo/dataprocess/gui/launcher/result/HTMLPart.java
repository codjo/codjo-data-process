/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.gui.launcher.result;
/**
 *
 */
public class HTMLPart {
    private HTMLReport htmlReport = null;
    private HTMLRow processing;
    private String title;


    public HTMLPart(String title) {
        this.title = " ";
        processing = new HTMLRow(title);
    }


    public boolean isProcessingFinished() {
        return processing.getState() == HTMLRow.OK;
    }


    public void setHtmlReport(HTMLReport htmlReport) {
        this.htmlReport = htmlReport;
    }


    public void startProcessing() {
        processing.start();
        updateReport();
    }


    public void endProcessing() {
        processing.close();
        updateReport();
    }


    public void declareProcessingError(String err) {
        processing.declareError(err);
        updateReport();
    }


    public String buildReport() {
        return "<tr><td height='5'><strong>" + title + "</strong></td></tr>" + processing.buildReport();
    }


    private void updateReport() {
        if (htmlReport != null) {
            htmlReport.updateGui();
        }
    }
}

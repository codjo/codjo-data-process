/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.dataprocess.common.util;
import java.util.List;
/**
 *
 */
class MockTreatmentGui implements TreatmentGui {
    public Object proceedGuiTreatment(List<String> parameters) {
        return Integer.parseInt(parameters.get(0)) + " " + parameters.get(1);
    }
}

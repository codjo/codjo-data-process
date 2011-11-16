package net.codjo.dataprocess.gui.activation.spi;
import java.util.Map;
public interface Rule {

    boolean applyRuleAndFinish(JComponentPod component, Map<String, Object> activationContext);
}

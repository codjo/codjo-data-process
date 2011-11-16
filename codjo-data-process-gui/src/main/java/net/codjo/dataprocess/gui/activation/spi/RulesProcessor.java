package net.codjo.dataprocess.gui.activation.spi;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 *
 */
public class RulesProcessor {
    private List<Rule> rules = new ArrayList<Rule>();
    private final JComponentIterator componentIterator;
    private final Map<String, Object> initialContext;


    public RulesProcessor(JComponentIterator componentIterator, Map<String, Object> initialContext) {
        this.componentIterator = componentIterator;
        this.initialContext = initialContext;
    }


    public void clearRules() {
        rules.clear();
    }


    public void addRule(Rule rule) {
        rules.add(rule);
    }


    public void proceed() {
        Map<String, Object> activationContext = new HashMap<String, Object>(initialContext);
        componentIterator.resetIndex();
        while (componentIterator.hasMoreComponents()) {
            JComponentPod currentComponentLocation = componentIterator.next();
            boolean ruleActivated = false;
            for (int ruleIndex = 0; ruleIndex < rules.size() && !ruleActivated; ruleIndex++) {
                Rule currentRule = rules.get(ruleIndex);
                ruleActivated = currentRule.applyRuleAndFinish(currentComponentLocation, activationContext);
            }
        }
    }
}

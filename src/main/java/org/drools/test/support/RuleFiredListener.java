package org.drools.test.support;

import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.DefaultAgendaEventListener;

import java.util.HashMap;
import java.util.Map;


public class RuleFiredListener extends DefaultAgendaEventListener{

    Map<String, Integer> rulesFiredMap;

    public RuleFiredListener() {
        super();
        rulesFiredMap = new HashMap<String, Integer>();
    }

    public void clearRulesFired(){
        rulesFiredMap.clear();
    }

    @Override
    public void afterMatchFired(AfterMatchFiredEvent event) {
        String ruleName = event.getMatch().getRule().getName();
        Integer count = rulesFiredMap.get(ruleName);
        if ( count == null) {
            count = new Integer(0);
        }
        count++;
        rulesFiredMap.put(ruleName, count);
    }

    public Map<String, Integer> getRulesFiredMap() {
        return rulesFiredMap;
    }
}

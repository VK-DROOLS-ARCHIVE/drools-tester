package org.drools.test.junit4;

import org.drools.test.support.RuleFiredListener;
import org.drools.test.annotations.RulesMustFire;
import org.drools.test.annotations.RulesShouldNotFire;
import org.drools.test.exception.RuleNotFiredException;
import org.drools.test.exception.UnexpectedRuleFiredException;
import org.junit.ComparisonFailure;
import org.junit.internal.runners.statements.InvokeMethod;
import org.junit.runners.model.FrameworkMethod;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InvokeMethodAndCheckFiredRules extends InvokeMethod {
    private RuleFiredListener ruleFiredListener;

    public InvokeMethodAndCheckFiredRules(FrameworkMethod testMethod, Object target, RuleFiredListener ruleFiredListener) {
        super(testMethod, target);
        this.ruleFiredListener = ruleFiredListener;
        initRuleExceptations(testMethod);
    }

    @Override
    public void evaluate() throws Throwable {
        super.evaluate();
        checkForRulesFired();
        checkForRulesNotFired();
    }

    List<RulesMustFire> expectedRulesFiredList = new ArrayList<RulesMustFire>();
    List<RulesShouldNotFire> rulesShouldNotFireList = new ArrayList<RulesShouldNotFire>();

    protected void initRuleExceptations(FrameworkMethod frameworkMethod){
        Annotation[] annotations = frameworkMethod.getAnnotations();
        if (annotations != null ) {
            for (Annotation annotation : annotations ){
                if (annotation instanceof RulesMustFire) {
                    expectedRulesFiredList.add((RulesMustFire) annotation);
                }
                if ( annotation instanceof RulesShouldNotFire) {
                    rulesShouldNotFireList.add((RulesShouldNotFire) annotation);
                }
            }
        }
    }

    private void checkForRulesFired() {
        Map<String, Integer> rulesFiredMap = ruleFiredListener.getRulesFiredMap();
        for (RulesMustFire rulesMustFire : expectedRulesFiredList ){
            for (String rule: rulesMustFire.ruleNames()) {
                Integer count = rulesFiredMap.get(rule);
                if (count == null) {
                    throw new RuleNotFiredException("Rule ('"+rule+"') did not fire!");
                }
                if (count != rulesMustFire.count()) {
                    throw new ComparisonFailure("Rule ("+rule+") did not fire expected times", ""+ rulesMustFire.count(), ""+count.intValue());
                }
            }
        }
    }

    private void checkForRulesNotFired() {
        Map<String, Integer> rulesFiredMap = ruleFiredListener.getRulesFiredMap();
        for (RulesShouldNotFire ruleFired : rulesShouldNotFireList){
            for (String rule: ruleFired.ruleNames()) {
                Integer count = rulesFiredMap.get(rule);
                if (count != null) {
                    throw new UnexpectedRuleFiredException("Rule ("+rule+") fired unexpectedly!");
                }
            }
        }
    }
}

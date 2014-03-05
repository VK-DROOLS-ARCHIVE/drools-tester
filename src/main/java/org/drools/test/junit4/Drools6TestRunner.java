package org.drools.test.junit4;

import org.drools.test.support.RuleFiredListener;
import org.drools.test.utils.ReflectionUtils;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

import org.kie.api.KieBase;
import org.kie.api.event.KieRuntimeEventManager;
import org.kie.api.runtime.KieSession;

import java.lang.reflect.Field;

public class Drools6TestRunner extends BlockJUnit4ClassRunner {

    private Field kieBaseField, kieSessionField ;
    private KieBase kieBase;
    private String kieSessionName;
    private RuleFiredListener ruleFiredListener = new RuleFiredListener();
    private Object ksession = null;

    Field getKieBaseField() {
        return kieBaseField;
    }

    void setKieBaseField(Field kieBaseField) {
        this.kieBaseField = kieBaseField;
    }

    public Field getKieSessionField() {
        return kieSessionField;
    }

    void setKieSessionField(Field kieSessionField) {
        this.kieSessionField = kieSessionField;
    }

    KieBase getKieBase() {
        return kieBase;
    }

    void setKieBase(KieBase kieBase) {
        this.kieBase = kieBase;
    }

    String getKieSessionName() {
        return kieSessionName;
    }

    void setKieSessionName(String kieSessionName) {
        this.kieSessionName = kieSessionName;
    }

    public Drools6TestRunner(Class<?> klass) throws InitializationError {
        super(klass);
        //helper classes to remove clutter and organize related pieces of code.
        new KieFieldsReader(this).readKieFields(klass);
        new KieCreationHelper(this).createKieBase(klass);
    }

    @Override
    protected Object createTest() throws Exception {
        Object object = super.createTest();
        injectKieBase(object);
        injectKieSession(object);
        attachRuleListeners();
        return object;
    }

     /**
     * Returns a {@link Statement} that invokes {@code method} on {@code test}
     */
    protected Statement methodInvoker(FrameworkMethod method, Object test) {
        // Original JUnit 4.x code:
        //return new InvokeMethod(method, test);
        ruleFiredListener.clearRulesFired();
        return new InvokeMethodAndCheckFiredRules(method, test, ruleFiredListener);
    }

    private void deattachRuleListeners() {
        if ( ksession != null) {
            ((KieRuntimeEventManager)ksession).removeEventListener(ruleFiredListener);
        }
    }

    private void attachRuleListeners() {
        ruleFiredListener.clearRulesFired();
        if ( ksession != null) {
            ((KieRuntimeEventManager)ksession).addEventListener(ruleFiredListener);
        }
    }

    private void injectKieSession(Object object) throws Exception {
        if  ( kieSessionField != null){
            boolean stateful = kieSessionField.getType() == KieSession.class;
            if (stateful ) {
                ksession = kieBase.newKieSession();
            } else {
                ksession = kieBase.newStatelessKieSession();
            }
            if  (ksession != null) {
                ReflectionUtils.makeAccessible(kieSessionField);
                ReflectionUtils.setField(kieSessionField, object, ksession);
            }
        }
    }

    private void injectKieBase(Object object) throws Exception {
        if (kieBaseField != null) {
            ReflectionUtils.makeAccessible(kieBaseField);
            ReflectionUtils.setField(kieBaseField, object, kieBase);
        }
    }
}

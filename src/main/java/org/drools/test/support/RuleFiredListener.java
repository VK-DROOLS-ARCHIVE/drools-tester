package org.drools.test.support;

import org.drools.core.WorkingMemory;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.impl.StatelessKnowledgeSessionImpl;
import org.drools.test.annotations.ExecuteAfterRule;
import org.drools.test.annotations.ExecuteBeforeRule;
import org.junit.Test;
import org.junit.runners.model.InitializationError;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RuleFiredListener extends DefaultAgendaEventListener {

    Map<String, Integer> rulesFiredMap;


    private Object testObject;
    Map<String, List<Method>> afterMethods;
    Map<String, List<Method>> beforeMethods;
    public static final Logger logger = LoggerFactory.getLogger(RuleFiredListener.class);

    public RuleFiredListener(Class testClass) throws InitializationError
    {
        super();
        rulesFiredMap = new HashMap<String, Integer>();
        afterMethods = new HashMap<String, List<Method>>();
        beforeMethods = new HashMap<String, List<Method>>();
        setTestClass(testClass);
    }

    public void setTestClass(Class testClass) throws InitializationError {
        extractRuleFiredMethods(testClass);
    }

    public void setTestObject(Object testObject) {
        this.testObject = testObject;
    }

    private void extractRuleFiredMethods(Class testClass) throws InitializationError {
        if ( testClass != null) {
            Method[] methods = testClass.getMethods();
            for (Method method : methods){
                Class<?>[] parameterTypes = method.getParameterTypes();

                if (parameterTypes.length == 1 && "ExecutionContext".equalsIgnoreCase(parameterTypes[0].getSimpleName())){

                    Annotation testAnnotation = method.getAnnotation(Test.class);
                    ExecuteBeforeRule executeBeforeFiringRule = method.getAnnotation(ExecuteBeforeRule.class);
                    if (executeBeforeFiringRule != null){
                        if  (testAnnotation != null) {
                            throw new InitializationError("Single method cannot have both '@Test' and '@ExecuteBeforeRule annotations.");
                        }
                        String ruleName = executeBeforeFiringRule.value();
                        List<Method> methodsList = beforeMethods.get(ruleName);
                        if ( methodsList == null ) {
                            methodsList = new ArrayList<Method>();
                            beforeMethods.put(ruleName, methodsList);
                        }
                        methodsList.add(method);
                        logger.debug("Found method annotated with @ExecuteBeforeRule. Method:" +method.getName()+", Rule: "+ ruleName);
                    }

                    ExecuteAfterRule executeAfterFiringRule = method.getAnnotation(ExecuteAfterRule.class);
                    if (executeAfterFiringRule != null){
                        if  (testAnnotation != null) {
                            throw new InitializationError("Single method cannot have both '@Test' and '@ExecuteAfterRule annotations.");
                        }
                        String ruleName = executeAfterFiringRule.value();
                        List<Method> methodsList = afterMethods.get(ruleName);
                        if ( methodsList == null ) {
                            methodsList = new ArrayList<Method>();
                            afterMethods.put(ruleName, methodsList);
                        }
                        methodsList.add(method);
                        logger.debug("Found method annotated with @ExecuteAfterRule. Method:" +method.getName()+", Rule: "+ ruleName);
                    }
                }
            }
        }
    }

    public void clearRulesFired(){
        rulesFiredMap.clear();
    }

    @Override
    public void afterMatchFired(AfterMatchFiredEvent event) {
        String ruleName = event.getMatch().getRule().getName();
        Integer count = rulesFiredMap.get(ruleName);
        if ( count == null) {
            count = 0;
        }
        count++;
        rulesFiredMap.put(ruleName, count);

        List<Method> methodsList = afterMethods.get(ruleName);
        if ( methodsList !=null && methodsList.size() > 0) {
            WorkingMemory workingMemory = null;
            if (event.getKieRuntime() instanceof StatefulKnowledgeSessionImpl) {
                workingMemory = ((StatefulKnowledgeSessionImpl)event.getKieRuntime()).getInternalWorkingMemory();
            } else if ( event.getKieRuntime() instanceof StatelessKnowledgeSessionImpl ) {
                workingMemory = ((WorkingMemory)event.getKieRuntime());
            }
            if (workingMemory != null) {
                for (Method method : methodsList) {
                    try {
                        ExecutionContext executionContext = new ExecutionContext(workingMemory);
                        method.invoke(testObject, executionContext);
//                        if (!executionContext.shouldContinueExecution()) {
//                            workingMemory.halt();
//                        }
                    } catch (Exception e) {
                        logger.error("Exception while invoking @ExecuteAfterRule", e);
                    }
                }
            }
        }
    }

    @Override
    public void beforeMatchFired(BeforeMatchFiredEvent event) {
        String ruleName = event.getMatch().getRule().getName();
        List<Method> methodsList = beforeMethods.get(ruleName);
        if ( methodsList != null && methodsList.size() > 0) {
            WorkingMemory workingMemory = null;
            if (event.getKieRuntime() instanceof StatefulKnowledgeSessionImpl) {
                workingMemory = ((StatefulKnowledgeSessionImpl)event.getKieRuntime()).getInternalWorkingMemory();
            } else if ( event.getKieRuntime() instanceof StatelessKnowledgeSessionImpl ) {
                workingMemory = ((WorkingMemory)event.getKieRuntime());
            }
            if (workingMemory != null) {
                for ( Method method : methodsList) {
                    try {
                        ExecutionContext executionContext = new ExecutionContext(workingMemory);
                        method.invoke(testObject, executionContext);
//                        if (!executionContext.shouldContinueExecution()){
//                            event.getKieRuntime().halt();
//                        }
                    } catch (Exception e) {
                        logger.error("Exception while invoking @ExecuteBeforeRule", e);
                    }
                }
            }
        }
        super.beforeMatchFired(event);
    }

    public Map<String, Integer> getRulesFiredMap() {
        return rulesFiredMap;
    }
}

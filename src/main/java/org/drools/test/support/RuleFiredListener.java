package org.drools.test.support;

import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.impl.StatelessKnowledgeSessionImpl;
import org.drools.test.annotations.ExecuteAfterRule;
import org.drools.test.annotations.ExecuteBeforeRule;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public RuleFiredListener(final Object testObject) {
        super();
        this.testObject = testObject;
        rulesFiredMap = new HashMap<String, Integer>();
        afterMethods = new HashMap<String, List<Method>>();
        beforeMethods = new HashMap<String, List<Method>>();
        extractRuleFiredMethods();
    }

    private void extractRuleFiredMethods() {
        if ( testObject != null) {
            Method[] methods = testObject.getClass().getMethods();
            for (Method method : methods){
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length == 1 && "ExecutionContext".equalsIgnoreCase(parameterTypes[0].getSimpleName())){
                    ExecuteAfterRule executeAfterFiringRule = method.getAnnotation(ExecuteAfterRule.class);
                    if (executeAfterFiringRule != null){
                        String ruleName = executeAfterFiringRule.value();
                        List<Method> methodsList = afterMethods.get(ruleName);
                        if ( methodsList == null ) {
                            methodsList = new ArrayList<Method>();
                            afterMethods.put(ruleName, methodsList);
                        }
                        methodsList.add(method);
                        logger.debug("Found method annotated with @ExecuteAfterRule. Method:" +method.getName()+", Rule: "+ ruleName);
                    }
                    ExecuteBeforeRule executeBeforeFiringRule = method.getAnnotation(ExecuteBeforeRule.class);
                    if (executeBeforeFiringRule != null){
                        String ruleName = executeBeforeFiringRule.value();
                        List<Method> methodsList = beforeMethods.get(ruleName);
                        if ( methodsList == null ) {
                            methodsList = new ArrayList<Method>();
                            beforeMethods.put(ruleName, methodsList);
                        }
                        methodsList.add(method);
                        logger.debug("Found method annotated with @ExecuteBeforeRule. Method:" +method.getName()+", Rule: "+ ruleName);
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
            org.drools.core.WorkingMemory workingMemory = null;
            if (event.getKieRuntime() instanceof StatefulKnowledgeSessionImpl) {
                workingMemory = ((StatefulKnowledgeSessionImpl)event.getKieRuntime()).getInternalWorkingMemory();
            } else if ( event.getKieRuntime() instanceof StatelessKnowledgeSessionImpl ) {
                workingMemory = ((org.drools.core.WorkingMemory)event.getKieRuntime());
            }
            for ( Method method : methodsList) {
                try {
                    ExecutionContext executionContext = new ExecutionContext(workingMemory);
                    method.invoke(testObject, executionContext);
                    if (!executionContext.shouldContinueExecution()){
                        workingMemory.halt();
                    }
                } catch (Exception e) {
                    logger.error("Exception while invoking @ExecuteAfterRule", e);
                }
            }
        }
    }

    @Override
    public void beforeMatchFired(BeforeMatchFiredEvent event) {
        String ruleName = event.getMatch().getRule().getName();
        List<Method> methodsList = beforeMethods.get(ruleName);
        if ( methodsList != null && methodsList.size() > 0) {
            org.drools.core.WorkingMemory workingMemory = null;
            if (event.getKieRuntime() instanceof StatefulKnowledgeSessionImpl) {
                workingMemory = ((StatefulKnowledgeSessionImpl)event.getKieRuntime()).getInternalWorkingMemory();
            } else if ( event.getKieRuntime() instanceof StatelessKnowledgeSessionImpl ) {
                workingMemory = ((org.drools.core.WorkingMemory)event.getKieRuntime());
            }
            for ( Method method : methodsList) {
                try {
                    ExecutionContext executionContext = new ExecutionContext(workingMemory);
                    method.invoke(testObject, executionContext);
                    if (!executionContext.shouldContinueExecution()){
                        workingMemory.halt();
                    }                } catch (Exception e) {
                    logger.error("Exception while invoking @ExecuteBeforeRule", e);
                }
            }
        }
        super.beforeMatchFired(event);
    }

    public Map<String, Integer> getRulesFiredMap() {
        return rulesFiredMap;
    }
}

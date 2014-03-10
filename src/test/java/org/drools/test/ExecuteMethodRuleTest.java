package org.drools.test;

import org.drools.examples.drl.Person;
import org.drools.test.annotations.DroolsResource;
import org.drools.test.annotations.ExecuteAfterRule;
import org.drools.test.annotations.ExecuteBeforeRule;
import org.drools.test.annotations.RulesMustFire;
import org.drools.test.junit4.Drools6TestRunner;
import org.drools.test.support.ExecutionContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.cdi.KSession;
import org.kie.api.runtime.StatelessKieSession;

import java.util.Arrays;

import static org.junit.Assert.assertNotNull;

@RunWith(Drools6TestRunner.class)
@DroolsResource(path={"org/drools/examples/drl/Hal1.drl", "org/drools/examples/drl/Hal2.drl"})
public class ExecuteMethodRuleTest {

    @KSession("ksession1")
    StatelessKieSession kieSession;

    @Test
    public void KieSessionNotNull() {
        assertNotNull(kieSession);
    }

    @Test
    @RulesMustFire(ruleNames = {"rule 1", "rule 3"})
    public void testRuleFired() throws Exception {
        Person personObject = new Person();
        personObject.setName("HAL2");
        kieSession.execute(personObject);
    }

    @Test
    @RulesMustFire(ruleNames = {"rule 2", "rule 4"})
    public void testRuleFired2() throws Exception {
        Person personObject = new Person();
        personObject.setName("HAL");
        kieSession.execute(personObject);
    }

    @Test
    @RulesMustFire(ruleNames = {"rule 2"}, count = 2)
    public void testRuleFiredWithCount() throws Exception {
        Person personObject = new Person();
        personObject.setName("HAL");
        Person personObject2 = new Person();
        personObject2.setName("HAL");
        kieSession.execute(Arrays.asList(new Object[]{personObject, personObject2}));
    }

    @ExecuteAfterRule("rule 1")
    public void sampleMethodAfter(ExecutionContext executionContext) throws Exception {
        System.out.println("after rule 1");
    }

    @ExecuteBeforeRule("rule 1")
    public void sampleMethodBefore(ExecutionContext executionContext) throws Exception {
        System.out.println("before rule 1");
        executionContext.stopExecution();
    }
}

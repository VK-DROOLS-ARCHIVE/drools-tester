package org.drools.test.simple;

import org.drools.examples.drl.Person;
import org.drools.test.annotations.DroolsResource;
import org.drools.test.annotations.RulesShouldNotFire;
import org.drools.test.exception.UnexpectedRuleFiredException;
import org.drools.test.junit4.Drools6TestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.KieBase;
import org.kie.api.cdi.KBase;
import org.kie.api.cdi.KSession;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.StatelessKieSession;

import static org.junit.Assert.assertNotNull;

@RunWith(Drools6TestRunner.class)
@DroolsResource(path= "org/drools/examples/drl/Hal1.drl")
public class RulesShouldNotFireTest {

    @KSession("ksession1")
    StatelessKieSession kieSession;

    @KBase
    KieBase kbase;

    @Test
    public void KieSessionNotNull() {
        assertNotNull(kieSession);
    }

    @Test
    @RulesShouldNotFire(ruleNames = {"rule 2"})
    public void testRuleFired() throws Exception {
        Person personObject = new Person();
        personObject.setName("HAL2");
        kieSession.execute(personObject);
    }

    @Test
    @RulesShouldNotFire(ruleNames = {"rule 1"})
    public void testRuleFired2() throws Exception {
        Person personObject = new Person();
        personObject.setName("HAL");
        kieSession.execute(personObject);
    }


    @Test(expected = UnexpectedRuleFiredException.class)
    @RulesShouldNotFire(ruleNames = {"rule 2"})
    public void testUnexpectedRuleFiredException() throws Exception {
        Person personObject = new Person();
        personObject.setName("HAL");
        kieSession.execute(personObject);
    }

}

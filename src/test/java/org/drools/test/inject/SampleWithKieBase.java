package org.drools.test.inject;

import org.drools.test.annotations.DroolsResource;
import org.drools.test.junit4.Drools6TestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.KieBase;
import org.kie.api.cdi.KBase;

import static org.junit.Assert.assertNotNull;

@RunWith(Drools6TestRunner.class)
@DroolsResource(path= "org/drools/examples/drl/Hal1.drl")
public class SampleWithKieBase {

    @KBase
    KieBase kieBase;

    @Test
    public void test1(){
        System.out.println("...from test1()...");
    }

    @Test
    public void KieBaseNotNull() {
        assertNotNull(kieBase);
    }
}

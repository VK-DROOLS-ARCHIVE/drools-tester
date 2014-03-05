package org.drools.test.inject;

import org.drools.test.annotations.DroolsResource;
import org.drools.test.junit4.Drools6TestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.cdi.KSession;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(Drools6TestRunner.class)
@DroolsResource(path= "org/drools/examples/drl/Hal1.drl")
public class SampleWithKieSession {

    @KSession("ksession1")
    KieSession kieSession;

    @Test
    public void KieSessionNotNull() {
        assertNotNull(kieSession);
    }

    @Test
    public void KieSession() {
        assertTrue(kieSession instanceof KieSession);
    }
}

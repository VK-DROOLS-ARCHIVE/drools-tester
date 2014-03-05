package org.drools.test.inject;

import org.drools.test.annotations.DroolsResource;
import org.drools.test.junit4.Drools6TestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.cdi.KSession;
import org.kie.api.runtime.StatelessKieSession;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(Drools6TestRunner.class)
@DroolsResource(path= "org/drools/examples/drl/Hal1.drl")
public class SampleWithStatelessKieSession {

    @KSession("ksession1")
    StatelessKieSession kieSession;

    @Test
    public void KieSessionNotNull() {
        assertNotNull(kieSession);
    }

    @Test
    public void KieSession() {
        assertTrue(kieSession instanceof StatelessKieSession);
    }
}

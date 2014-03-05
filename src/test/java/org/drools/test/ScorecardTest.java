package org.drools.test;

import org.drools.test.annotations.DroolsResource;
import org.drools.test.annotations.RulesMustFire;
import org.drools.test.junit4.Drools6TestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.KieBase;
import org.kie.api.cdi.KBase;
import org.kie.api.cdi.KSession;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(Drools6TestRunner.class)
@DroolsResource(path="org/drools/examples/scorecards/scoremodel_c.sxls")
public class ScorecardTest {

    @KSession("ksession1")
    KieSession kieSession;

    @KBase
    KieBase kbase;

    @Test
    public void KieSessionNotNull() {
        assertNotNull(kieSession);
    }

    @Test
    @RulesMustFire(ruleNames = {"PartialScore_SampleScore_ValidLicenseScore_8", "PartialScore_SampleScore_AgeScore_10"})
    public void testRuleFired() throws Exception {
        FactType scorecardType = kbase.getFactType( "org.drools.scorecards.example","SampleScore" );
        Object scorecard = scorecardType.newInstance();
        scorecardType.set(scorecard, "age", 10);
        kieSession.insert( scorecard );
        kieSession.fireAllRules();
        kieSession.dispose();
        //occupation = 5, age = 25, validLicence -1
        assertEquals(29.0, scorecardType.get(scorecard, "scorecard__calculatedScore"));
    }

}

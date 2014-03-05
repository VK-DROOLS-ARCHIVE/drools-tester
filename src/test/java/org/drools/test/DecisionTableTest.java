/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.test;

import org.drools.examples.decisiontable.Driver;
import org.drools.examples.decisiontable.Policy;
import org.drools.test.annotations.DroolsResource;
import org.drools.test.annotations.RulesMustFire;
import org.drools.test.junit4.Drools6TestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.cdi.KSession;
import org.kie.api.runtime.StatelessKieSession;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(Drools6TestRunner.class)
@DroolsResource(path= "org/drools/examples/decisiontable/ExamplePolicyPricing.xls")
public class DecisionTableTest {

    @KSession("DecisionTableKS")
    StatelessKieSession ksession;

    @Test
    public void testKsessionNotNull(){
        assertNotNull(ksession);
    }

    @Test
    @RulesMustFire(ruleNames = {"Pricing bracket_18","Discounts_34"})
    public void testDT() {
        //now create some test data
        Driver driver = new Driver();
        Policy policy = new Policy();

        ksession.execute( Arrays.asList( new Object[]{driver, policy} ) );

        assertEquals(120, policy.getBasePrice() );
        assertEquals(20, policy.getDiscountPercent() );
    }

}

package org.drools.test.junit4;


import org.junit.runners.model.InitializationError;
import org.kie.api.KieBase;
import org.kie.api.cdi.KBase;
import org.kie.api.cdi.KSession;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;

import java.lang.reflect.Field;

class KieFieldsReader {

    private Drools6TestRunner drools6TestRunner;

    KieFieldsReader(Drools6TestRunner drools6TestRunner) {
        this.drools6TestRunner = drools6TestRunner;
    }

    void readKieFields(Class<?> klass) throws InitializationError {
        Field[] fields = klass.getDeclaredFields();
        for ( Field field : fields ) {
            if ( field.getType() == KieBase.class) {
                if (field.getAnnotation(KBase.class) != null){
                    drools6TestRunner.setKieBaseField(field);
                } else {
                    throw new InitializationError("Found KieBase field, but field is not annotated with @KBase.");
                }
            }
            KSession kSession= field.getAnnotation(KSession.class);
            if ( kSession != null) {
                if ( drools6TestRunner.getKieSessionField() != null) {
                    throw new InitializationError("Multiple fields found annotated with @KSession. Cannot continue.");
                }
                Class sessionType = field.getType();
                if (sessionType != KieSession.class && sessionType != StatelessKieSession.class) {
                    throw new InitializationError("@KSession annotation found on an invalid field. Field must be of type KieSession or StatelessKieSession.");
                }
                drools6TestRunner.setKieSessionField(field);
                drools6TestRunner.setKieSessionName(kSession.value());
            }
        }
        if ( drools6TestRunner.getKieBaseField() == null && drools6TestRunner.getKieSessionField() == null) {
            throw new InitializationError("No valid field found annotated with either @KBase or @KSession, cannot continue!");
        }
    }
}

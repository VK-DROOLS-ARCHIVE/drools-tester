package org.drools.test.junit4;

import org.drools.compiler.compiler.io.Resource;
import org.drools.core.io.impl.ByteArrayResource;
import org.drools.core.util.IoUtils;
import org.drools.test.annotations.DroolsResource;
import org.junit.runners.model.InitializationError;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.drools.compiler.kie.builder.impl.KieBuilderImpl.generatePomXml;
import static org.junit.Assert.assertTrue;

class KieCreationHelper {

    private Drools6TestRunner drools6TestRunner;

    public KieCreationHelper(Drools6TestRunner drools6TestRunner) {
        this.drools6TestRunner = drools6TestRunner;
    }

    private List<DroolsResource> readResourceAnnotations(Class<?> klass) throws InitializationError {
        Annotation[] annotations = klass.getDeclaredAnnotations();
        List<DroolsResource> droolsResourceList = new ArrayList<DroolsResource>();
        for (Annotation annotation : annotations){
            if ( annotation.annotationType() == DroolsResource.class){
                DroolsResource resource = (DroolsResource)annotation;
                droolsResourceList.add(resource);
            }
        }
        if ( droolsResourceList.size() == 0) {
            throw new InitializationError("Class ("+klass.getName()+") is annotated with one or more @DroolsResource. Cannot continue.");
        }
        return droolsResourceList;
    }

    void createKieBase(Class<?> klass) throws InitializationError {
        try {
            List<DroolsResource> resourceList = readResourceAnnotations(klass);
            KieServices ks = KieServices.Factory.get();

            ReleaseId releaseId = ks.newReleaseId("org.kie", "drools-test", "1.0-SNAPSHOT");
            build(klass, resourceList, ks, releaseId);
            KieContainer kieContainer = ks.newKieContainer(releaseId);
            drools6TestRunner.setKieBase(kieContainer.getKieBase());
        } catch (IOException e) {
            throw new InitializationError(e);
        }
    }

    void build(Class<?> klass, List<DroolsResource> resourceList, KieServices ks, ReleaseId releaseId) throws IOException {
        KieModuleModel kproj = ks.newKieModuleModel();

        KieBaseModel kBase1 = kproj.newKieBaseModel("KBase1").setDefault(true);
        if ( drools6TestRunner.getKieSessionField()!= null) {
            boolean stateful = drools6TestRunner.getKieSessionField().getType() == KieSession.class;
            kBase1.newKieSessionModel(drools6TestRunner.getKieSessionName()).setType(stateful? KieSessionModel.KieSessionType.STATEFUL: KieSessionModel.KieSessionType.STATELESS).setDefault(true);
        }
        KieFileSystem kfs = ks.newKieFileSystem();

        kfs.writeKModuleXML(kproj.toXML())
                .writePomXML( generatePomXml(releaseId) );

        for ( DroolsResource resource : resourceList) {
            for (String pathElement : resource.path()) {
                byte[] bytes = IoUtils.readBytesFromInputStream(klass.getResourceAsStream("/" + pathElement));
//            ByteArrayResource byteArrayResource = new ByteArrayResource(bytes);
//            byteArrayResource.setResourceType(ResourceType.getResourceType(resource.type().toString()));
                kfs.write("src/main/resources/" + pathElement, bytes);
            }
        }

        KieBuilder kieBuilder = ks.newKieBuilder(kfs);
        for ( Message message : kieBuilder.buildAll().getResults().getMessages()){
            System.err.println(message.getLine()+","+message.getColumn()+": "+message.getText());
        }
        assertTrue(kieBuilder.buildAll().getResults().getMessages().isEmpty());
    }
}

package org.drools.test.annotations;


import java.lang.annotation.*;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DroolsResource {

    String[] path();
//    Type type() default Type.DRL;
//
//    enum Type {
//       DRL,GDRL,RDRL ,XDRL,DSL,DSLR ,RDSLR,DRF,BPMN2,DTABLE,PKG,BRL,
//        CHANGE_SET,XSD,PMML,DESCR,JAVA,PROPERTIES,SCARD
//    }
}

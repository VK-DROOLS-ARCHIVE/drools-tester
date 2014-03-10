package org.drools.test.support;

import org.drools.core.FactHandle;
import org.drools.core.WorkingMemory;
import org.drools.core.WorkingMemoryEntryPoint;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.ObjectFilter;
import org.kie.api.time.SessionClock;

import java.util.Iterator;


public class ExecutionContext {
    private WorkingMemory workingMemory;
    private boolean continueExecution = true;

    public void stopExecution() {
        continueExecution = false;
    }

    boolean shouldContinueExecution(){
        return continueExecution;
    }

    ExecutionContext(WorkingMemory workingMemory){
        this.workingMemory = workingMemory;
    }

    public FactHandle getFactHandle(Object object) {
        return workingMemory.getFactHandle(object);
    }

    public FactHandle getFactHandleByIdentity(Object object) {
        return workingMemory.getFactHandleByIdentity(object);
    }

    public Iterator<?> iterateObjects() {
        return workingMemory.iterateObjects();
    }

    public Iterator<?> iterateObjects(ObjectFilter filter) {
        return workingMemory.iterateObjects(filter);
    }

    public Iterator<?> iterateFactHandles(ObjectFilter filter) {
        return workingMemory.iterateFactHandles(filter);
    }

    public Iterator<?> iterateFactHandles() {
        return workingMemory.iterateFactHandles();
    }

    public Object getObject(org.kie.api.runtime.rule.FactHandle handle) {
        return workingMemory.getObject(handle);
    }

    public Object getGlobal(String identifier) {
        return workingMemory.getGlobal(identifier);
    }

    public Environment getEnvironment() {
        return workingMemory.getEnvironment();
    }

    public SessionClock getSessionClock() {
        return workingMemory.getSessionClock();
    }

    public WorkingMemoryEntryPoint getWorkingMemoryEntryPoint(String id) {
        return workingMemory.getWorkingMemoryEntryPoint(id);
    }
}

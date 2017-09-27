package edu.harvard.iq.dataverse.workflow.stepproviderlib;

import java.util.Map;

/**
 * Interface for step factories. Implement this interface to be able to provide 
 * steps for workflows. 
 * 
 * @author michael
 */
public interface WorkflowStepSPI {
    
    WorkflowStep getStep( String stepType, Map<String,String> stepParameters );
    
}
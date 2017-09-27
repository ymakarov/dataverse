package edu.harvard.iq.dataverse.workflow.stepproviderlib;

/**
 * The context in which a workflow is performed. Contains information steps 
 * might need, such as information about the dataset being worked on and 
 * versioning data.
 * 
 * @author michael
 */
public interface WorkflowContext {
    
    /**
     * The type of trigger that started this workflow
     */
    public enum TriggerType {
        /** 
         * Dataset version is going from draft to published. May fail, in which
         * case the dataset version will become draft again.
         */
        PrePublishDataset,
        
        /** 
         * Dataset version has been published, now go do some 
         * post-processing, if needed.
         */
        PostPublishDataset
    }
    
    DatasetVersionData getDatasetVersionData();
    
    long getNextMinorVersionNumber();

    long getNextVersionNumber();

    boolean isMinorRelease();

    String getInvocationId();

    String getDoiProvider();

    TriggerType getTriggerType();
    
}

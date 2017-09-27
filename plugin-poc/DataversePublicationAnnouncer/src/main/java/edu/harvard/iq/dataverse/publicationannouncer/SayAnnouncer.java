package edu.harvard.iq.dataverse.publicationannouncer;

import edu.harvard.iq.dataverse.workflow.stepproviderlib.Failure;
import edu.harvard.iq.dataverse.workflow.stepproviderlib.WorkflowContext;
import edu.harvard.iq.dataverse.workflow.stepproviderlib.WorkflowStep;
import edu.harvard.iq.dataverse.workflow.stepproviderlib.WorkflowStepResult;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author michael
 */
public class SayAnnouncer implements WorkflowStep {

    @Override
    public WorkflowStepResult run(WorkflowContext context) {
        String text = "Publishing "
                      + " version " + context.getNextVersionNumber() + "." + context.getNextMinorVersionNumber()
                      + " of " + context.getDatasetVersionData().getDisplayName();
        say( text );
        return WorkflowStepResult.OK;
    }

    @Override
    public WorkflowStepResult resume(WorkflowContext context, Map<String, String> internalData, String externalData) {
        throw new UnsupportedOperationException("The say step does not support resume");
    }

    @Override
    public void rollback(WorkflowContext context, Failure reason) {
        String text = "Cancelled publishing "
                      + " version " + context.getNextVersionNumber() + "." + context.getNextMinorVersionNumber()
                      + " of " + context.getDatasetVersionData().getDisplayName()
                      + " due to " + reason.getMessage();
        
        say( text );
    }
    
    private void say( String text ) { 
        try {
            ProcessBuilder pb = new ProcessBuilder("say", text);
            pb.start();
        } catch (IOException ex) {
            Logger.getLogger(SayAnnouncer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void main(String[] args) {
        new SayAnnouncer().say("hello world from java");
    }
    
}

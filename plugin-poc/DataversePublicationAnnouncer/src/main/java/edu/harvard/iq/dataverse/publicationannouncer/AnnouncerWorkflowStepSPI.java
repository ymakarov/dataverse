package edu.harvard.iq.dataverse.publicationannouncer;

import com.google.auto.service.AutoService;
import edu.harvard.iq.dataverse.workflow.stepproviderlib.WorkflowStep;
import edu.harvard.iq.dataverse.workflow.stepproviderlib.WorkflowStepSPI;
import java.util.Map;

/**
 *
 * @author michael
 */
@AutoService(WorkflowStepSPI.class)
public class AnnouncerWorkflowStepSPI implements WorkflowStepSPI {

    @Override
    public WorkflowStep getStep(String stepType, Map<String, String> stepParameters) {
        switch (stepType) {
            case "slack":
                return new SlackAnnouncer(stepParameters.get("username"), 
                                          stepParameters.get("channel"),
                                          stepParameters.get("url"));
            case "say":
                return new SayAnnouncer();
                
            default:
                throw new RuntimeException("Unknown step type '" + stepType + "'");
                    
        }
    }
    
}


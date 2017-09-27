package edu.harvard.iq.dataverse.workflow.stepproviderlib;

import java.util.List;

/**
 * 
 * Holds information about a single version of a Dataset, and about the 
 * dataset itself.
 * 
 * @author michael
 */
public interface DatasetVersionData {
    
    String getDatasetId();
    String getIdentifier();
    String getGlobalId();
    String getDisplayName();
    String getCitation();
    List<String> getAuthorNames();
    
}

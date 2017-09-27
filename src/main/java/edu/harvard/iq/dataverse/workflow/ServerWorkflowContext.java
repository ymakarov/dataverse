package edu.harvard.iq.dataverse.workflow;

import edu.harvard.iq.dataverse.Dataset;
import edu.harvard.iq.dataverse.engine.command.DataverseRequest;
import edu.harvard.iq.dataverse.workflow.stepproviderlib.DatasetVersionData;
import edu.harvard.iq.dataverse.workflow.stepproviderlib.WorkflowContext;
import edu.harvard.iq.dataverse.workflow.stepproviderlib.WorkflowContext.TriggerType;
import java.util.List;
import java.util.UUID;

/**
 * Implements a workflow context within a server.
 * 
 * @author michael
 */
public class ServerWorkflowContext implements WorkflowContext {
    
    private final DataverseRequest request;
    private final Dataset dataset;
    private final long    nextVersionNumber;
    private final long    nextMinorVersionNumber;
    private final TriggerType    type;
    private final String  doiProvider;
    private final String invocationId;
    private DatasetVersionData datasetVersionData = null;

    public ServerWorkflowContext( DataverseRequest aRequest, Dataset aDataset, String doiProvider, TriggerType aTriggerType ) {
        this( aRequest, aDataset,
                aDataset.getLatestVersion().getVersionNumber(), 
                aDataset.getLatestVersion().getMinorVersionNumber(),
                aTriggerType, 
                doiProvider);
    }
    
    public ServerWorkflowContext(DataverseRequest request, Dataset dataset, long nextVersionNumber, 
                            long nextMinorVersionNumber, TriggerType type, String doiProvider) {
        this(request, dataset, nextVersionNumber, nextMinorVersionNumber, type,
                doiProvider, UUID.randomUUID().toString());
    }
    
    public ServerWorkflowContext(DataverseRequest request, Dataset dataset, long nextVersionNumber, 
                            long nextMinorVersionNumber, TriggerType type, String doiProvider,
                            String invocationId) {
        this.request = request;
        this.dataset = dataset;
        this.nextVersionNumber = nextVersionNumber;
        this.nextMinorVersionNumber = nextMinorVersionNumber;
        this.type = type;
        this.doiProvider = doiProvider;
        this.invocationId = invocationId;
    }

    public Dataset getDataset() {
        return dataset;
    }

    public DataverseRequest getRequest() {
        return request;
    }

    @Override
    public long getNextMinorVersionNumber() {
        return nextMinorVersionNumber;
    }

    @Override
    public long getNextVersionNumber() {
        return nextVersionNumber;
    }

    @Override
    public boolean isMinorRelease() {
        return getNextMinorVersionNumber()!=0;
    }

    @Override
    public String getInvocationId() {
        return invocationId;
    }

    @Override
    public String getDoiProvider() {
        return doiProvider;
    }

    @Override
    public TriggerType getTriggerType() {
        return type;
    }
    
    @Override
    public DatasetVersionData getDatasetVersionData() {
        if ( datasetVersionData == null ) {
            datasetVersionData = new DatasetVersionDataImpl();
        }
        return datasetVersionData;
    }

    private class DatasetVersionDataImpl implements DatasetVersionData {

        @Override
        public String getDatasetId() {
            return Long.toString(dataset.getId());
        }

        @Override
        public String getIdentifier() {
            return dataset.getIdentifier();
        }

        @Override
        public String getGlobalId() {
            return dataset.getGlobalId();
        }

        @Override
        public String getDisplayName() {
            return dataset.getDisplayName();
        }

        @Override
        public String getCitation() {
            return dataset.getCitation();
        }

        @Override
        public List<String> getAuthorNames() {
            return dataset.getLatestVersion().getDatasetAuthorNames();
        }
    }

    
}

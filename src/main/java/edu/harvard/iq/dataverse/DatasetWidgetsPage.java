package edu.harvard.iq.dataverse;

import edu.harvard.iq.dataverse.dataaccess.ImageThumbConverter;
import edu.harvard.iq.dataverse.dataset.DatasetThumbnail;
import edu.harvard.iq.dataverse.dataset.DatasetUtil;
import edu.harvard.iq.dataverse.engine.command.exception.CommandException;
import edu.harvard.iq.dataverse.engine.command.impl.UpdateDatasetCommand;
import edu.harvard.iq.dataverse.engine.command.impl.UpdateDatasetThumbnailCommand;
import edu.harvard.iq.dataverse.util.BundleUtil;
import edu.harvard.iq.dataverse.util.FileUtil;
import edu.harvard.iq.dataverse.util.JsfHelper;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

@ViewScoped
@Named("DatasetWidgetsPage")
public class DatasetWidgetsPage implements java.io.Serializable {

    private static final Logger logger = Logger.getLogger(DatasetWidgetsPage.class.getCanonicalName());

    @EJB
    DatasetServiceBean datasetService;

    @EJB
    DatasetVersionServiceBean datasetVersionService;

    @EJB
    DataFileServiceBean dataFileService;

    @EJB
    EjbDataverseEngine commandEngine;

    @Inject
    DataverseRequestServiceBean dvRequestService;

    private Long datasetId;
    private Dataset dataset;
    private List<DatasetThumbnail> datasetThumbnails;
    /**
     * A preview image of either the current or (potentially unsaved) future
     * thumbnail.
     */
    private DatasetThumbnail datasetThumbnail;
    private DataFile datasetFileThumbnailToSwitchTo;
    private UpdateDatasetThumbnailCommand updateDatasetThumbnailCommand;

    @Inject
    PermissionsWrapper permissionsWrapper;
    private final boolean considerDatasetLogoAsCandidate = false;

    public String init() {
        if (datasetId == null || datasetId.intValue() <= 0) {
            return permissionsWrapper.notFound();
        }
        dataset = datasetService.find(datasetId);
        if (dataset == null) {
            return permissionsWrapper.notFound();
        }
        /**
         * @todo Consider changing this to "can issue"
         * UpdateDatasetThumbnailCommand since it's really the only update you
         * can do from this page.
         */
        if (!permissionsWrapper.canIssueCommand(dataset, UpdateDatasetCommand.class)) {
            return permissionsWrapper.notAuthorized();
        }
        datasetThumbnails = DatasetUtil.getThumbnailCandidates(dataset, considerDatasetLogoAsCandidate);
        datasetThumbnail = DatasetUtil.getThumbnail(dataset, datasetVersionService, dataFileService);
        if (datasetThumbnail != null) {
            DataFile dataFile = datasetThumbnail.getDataFile();
            if (dataFile != null) {
                datasetFileThumbnailToSwitchTo = dataFile;
            }
        }
        return null;
    }

    public Long getDatasetId() {
        return datasetId;
    }

    public void setDatasetId(Long datasetId) {
        this.datasetId = datasetId;
    }

    public Dataset getDataset() {
        return dataset;
    }

    public void setDataset(Dataset dataset) {
        this.dataset = dataset;
    }

    public List<DatasetThumbnail> getDatasetThumbnails() {
        return datasetThumbnails;
    }

    public DatasetThumbnail getDatasetThumbnail() {
        return datasetThumbnail;
    }

    public void setDatasetThumbnail(DatasetThumbnail datasetThumbnail) {
        this.datasetThumbnail = datasetThumbnail;
    }

    public DataFile getDatasetFileThumbnailToSwitchTo() {
        return datasetFileThumbnailToSwitchTo;
    }

    public void setDatasetFileThumbnailToSwitchTo(DataFile datasetFileThumbnailToSwitchTo) {
        this.datasetFileThumbnailToSwitchTo = datasetFileThumbnailToSwitchTo;
    }

    public void setDataFileAsThumbnail() {
        logger.fine("setDataFileAsThumbnail clicked");
        updateDatasetThumbnailCommand = new UpdateDatasetThumbnailCommand(dvRequestService.getDataverseRequest(), dataset, UpdateDatasetThumbnailCommand.UserIntent.userHasSelectedDataFileAsThumbnail, datasetFileThumbnailToSwitchTo.getId(), null, null);
        String base64image = ImageThumbConverter.getImageThumbAsBase64(datasetFileThumbnailToSwitchTo, ImageThumbConverter.DEFAULT_CARDIMAGE_SIZE);
        datasetThumbnail = new DatasetThumbnail(base64image, datasetFileThumbnailToSwitchTo);
    }

    public void flagDatasetThumbnailForRemoval() {
        logger.fine("flagDatasetThumbnailForRemoval");
        updateDatasetThumbnailCommand = new UpdateDatasetThumbnailCommand(dvRequestService.getDataverseRequest(), dataset, UpdateDatasetThumbnailCommand.UserIntent.userWantsToRemoveThumbnail, null, null, null);
        datasetFileThumbnailToSwitchTo = null;
        datasetThumbnail = null;
    }

    public void handleImageFileUpload(FileUploadEvent event) {
        logger.fine("handleImageFileUpload clicked");
        UploadedFile uploadedFile = event.getFile();
        InputStream fileInputStream = null;
        try {
            fileInputStream = uploadedFile.getInputstream();
        } catch (IOException ex) {
            Logger.getLogger(DatasetWidgetsPage.class.getName()).log(Level.SEVERE, null, ex);
        }
        File file = null;
        try {
            file = FileUtil.inputStreamToFile(fileInputStream);
        } catch (IOException ex) {
            Logger.getLogger(DatasetWidgetsPage.class.getName()).log(Level.SEVERE, null, ex);
        }
        JsonObjectBuilder resultFromAttemptToStageDatasetLogoObjectBuilder = datasetService.writeDatasetLogoToStagingArea(dataset, file);
        JsonObject resultFromAttemptToStageDatasetLogoObject = resultFromAttemptToStageDatasetLogoObjectBuilder.build();
        String stagingFilePath = resultFromAttemptToStageDatasetLogoObject.getString(DatasetUtil.stagingFilePathKey);
        updateDatasetThumbnailCommand = new UpdateDatasetThumbnailCommand(dvRequestService.getDataverseRequest(), dataset, UpdateDatasetThumbnailCommand.UserIntent.userWantsToUseNonDatasetFile, null, fileInputStream, stagingFilePath);
        String base64image = null;
        try {
            base64image = FileUtil.rescaleImage(file);
            datasetThumbnail = new DatasetThumbnail(base64image, datasetFileThumbnailToSwitchTo);
        } catch (IOException ex) {
            Logger.getLogger(DatasetWidgetsPage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String save() {
        logger.fine("save clicked");
        if (updateDatasetThumbnailCommand == null) {
            logger.fine("The user clicked saved without making any changes.");
            return null;
        }
        try {
            DatasetThumbnail datasetThumbnailFromCommand = commandEngine.submit(updateDatasetThumbnailCommand);
            JsfHelper.addSuccessMessage(BundleUtil.getStringFromBundle("dataset.thumbnailsAndWidget.success"));
            return "/dataset.xhtml?persistentId=" + dataset.getGlobalId() + "&faces-redirect=true";
        } catch (CommandException ex) {
            String error = ex.getLocalizedMessage();
            /**
             * @todo Should this go in the ActionLogRecord instead?
             */
            // Username @dataverseAdmin experienced a problem executing UpdateDatasetThumbnailCommand on a DVObject {=[Dataset id:1377 ]} and saw this error: Just testing what an error would look like in the GUI.
            logger.info("Username " + updateDatasetThumbnailCommand.getRequest().getUser().getIdentifier() + " experienced a problem executing " + updateDatasetThumbnailCommand.getClass().getSimpleName() + " on a DVObject " + updateDatasetThumbnailCommand.getAffectedDvObjects() + " and saw this error: " + error);
            JsfHelper.addErrorMessage(error);
            return null;
        }
    }

    public String cancel() {
        logger.fine("cancel clicked");
        return "/dataset.xhtml?persistentId=" + dataset.getGlobalId() + "&faces-redirect=true";
    }

}

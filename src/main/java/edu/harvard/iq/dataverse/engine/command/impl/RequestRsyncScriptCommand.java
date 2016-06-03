package edu.harvard.iq.dataverse.engine.command.impl;

import edu.harvard.iq.dataverse.Dataset;
import edu.harvard.iq.dataverse.DatasetField;
import edu.harvard.iq.dataverse.authorization.Permission;
import edu.harvard.iq.dataverse.engine.command.AbstractVoidCommand;
import edu.harvard.iq.dataverse.engine.command.CommandContext;
import edu.harvard.iq.dataverse.engine.command.DataverseRequest;
import edu.harvard.iq.dataverse.engine.command.RequiredPermissions;
import edu.harvard.iq.dataverse.engine.command.exception.CommandException;
import com.jayway.restassured.response.Response;
import edu.harvard.iq.dataverse.authorization.users.AuthenticatedUser;
import edu.harvard.iq.dataverse.authorization.users.User;
import edu.harvard.iq.dataverse.engine.command.exception.CommandExecutionException;
import java.util.logging.Logger;

@RequiredPermissions(Permission.AddDataset)
public class RequestRsyncScriptCommand extends AbstractVoidCommand {

    private static final Logger logger = Logger.getLogger(RequestRsyncScriptCommand.class.getCanonicalName());

    private final Dataset dataset;
    private final DatasetField datasetField;
    private final DataverseRequest request;

    RequestRsyncScriptCommand(DataverseRequest requestArg, Dataset datasetArg, DatasetField datasetFieldArg) {
        super(requestArg, datasetArg);
        request = requestArg;
        dataset = datasetArg;
        datasetField = datasetFieldArg;
    }

    @Override
    protected void executeImpl(CommandContext ctxt) throws CommandException {
        System.out.println("RequestRsyncScriptCommand executing... found " + datasetField.getValue());
        // {"dep_email": "bob.smith@example.com", "uid": 42, "depositor_name": ["Smith", "Bob"], "lab_email": "john.doe@example.com", "datacite.resourcetype": "X-Ray Diffraction"}
        User user = request.getUser();
        if (!(user instanceof AuthenticatedUser)) {
            throw new CommandExecutionException("Authenticated users only.", this);
        }
        AuthenticatedUser au = (AuthenticatedUser) user;
        Response response;
        try {
            response = ctxt.dataCaptureModule().requestRsyncScriptCreation(au);
        } catch (Exception ex) {
            throw new CommandException("Problem retrieving rsync script from Data Capture Module: " + ex.getLocalizedMessage(), this);
        }
        int statusCode = response.getStatusCode();
        if (statusCode != 200) {
            logger.info("Problem retrieving rsync script from Data Capture Module. Status code was: " + statusCode);
        }
        String script = response.body().jsonPath().getString("script");
        if (script == null || script.isEmpty()) {
            logger.info("Problem retrieving rsync script from Data Capture Module. The script was null or empty.");
        }
        /**
         * @todo Put this in the database somewhere. Will I be able to query the
         * DCM at any time and GET the script again, based on an id?
         */
        logger.info("script: " + script);

    }

}

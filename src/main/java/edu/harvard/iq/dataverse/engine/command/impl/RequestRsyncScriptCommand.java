package edu.harvard.iq.dataverse.engine.command.impl;

import edu.harvard.iq.dataverse.Dataset;
import edu.harvard.iq.dataverse.DatasetField;
import edu.harvard.iq.dataverse.authorization.Permission;
import edu.harvard.iq.dataverse.engine.command.AbstractVoidCommand;
import edu.harvard.iq.dataverse.engine.command.CommandContext;
import edu.harvard.iq.dataverse.engine.command.DataverseRequest;
import edu.harvard.iq.dataverse.engine.command.RequiredPermissions;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import edu.harvard.iq.dataverse.authorization.users.AuthenticatedUser;
import edu.harvard.iq.dataverse.authorization.users.User;
import edu.harvard.iq.dataverse.engine.command.exception.PermissionException;
import java.util.Collections;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObjectBuilder;

/**
 * Always catch a RuntimeException when calling this command, which may occur on
 * any problem contacting the Data Capture Module! We have to throw a
 * RuntimeException because otherwise ctxt.engine().submit() will put "OK" for
 * "actiontype" in the actionlogrecord rather than "InternalError" if you throw
 * a CommandExecutionException.
 */
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
    protected void executeImpl(CommandContext ctxt) throws PermissionException, RuntimeException {
        // {"dep_email": "bob.smith@example.com", "uid": 42, "depositor_name": ["Smith", "Bob"], "lab_email": "john.doe@example.com", "datacite.resourcetype": "X-Ray Diffraction"}
        User user = request.getUser();
        if (!(user instanceof AuthenticatedUser)) {
            /**
             * @todo get Permission.AddDataset from above somehow rather than
             * duplicating it here.
             */
            throw new PermissionException("This command can only be called by an AuthenticatedUser, not " + user,
                    this, Collections.singleton(Permission.AddDataset), dataset);
        }
        AuthenticatedUser au = (AuthenticatedUser) user;
        HttpResponse<JsonNode> response;
        JsonObjectBuilder jab = Json.createObjectBuilder();
        // The general rule should be to always pass the user id and dataset id to the DCM.
        jab.add("userId", au.getId());
        jab.add("datasetId", dataset.getId());
        String errorPreamble = "User id " + au.getId() + " had a problem retrieving rsync script for dataset id " + dataset.getId() + " from Data Capture Module. ";
        try {
            response = ctxt.dataCaptureModule().requestRsyncScriptCreation(au, dataset, jab);
        } catch (Exception ex) {
            throw new RuntimeException(errorPreamble + ex.getLocalizedMessage(), ex);
        }
        int statusCode = response.getStatus();
        if (statusCode != 200) {
            /**
             * @todo is the body too big to fit in the actionlogrecord? The
             * column length on "info" is 1024. See also
             * https://github.com/IQSS/dataverse/issues/2669
             */
            throw new RuntimeException(errorPreamble + "Rather than 200 the status code was " + statusCode + ". The body was \'" + response.getBody() + "\'.");
        }
        /**
         * @todo Don't expect to get the script from ur.py (upload request). Go
         * fetch it from sr.py (script request) after a minute or so. (Cron runs
         * every minute.) Wait 90 seconds to be safe. Note that it's possible
         * for a different user id to call ur.py vs. sr.py
         */
        String script = response.getBody().getObject().getString("script");
        if (script == null || script.isEmpty()) {
            throw new RuntimeException(errorPreamble + "The script was null or empty.");
        }
        /**
         * @todo Put this in the database somewhere. Will I be able to query the
         * DCM at any time and GET the script again, based on an id?
         */
        logger.info("script: " + script);

    }

}

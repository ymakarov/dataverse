/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.dataverse.engine.command.impl;

import edu.harvard.iq.dataverse.Dataset;
import edu.harvard.iq.dataverse.DvObject;
import static edu.harvard.iq.dataverse.IdServiceBean.logger;
import edu.harvard.iq.dataverse.authorization.DataverseRole;
import edu.harvard.iq.dataverse.authorization.Permission;
import edu.harvard.iq.dataverse.authorization.RoleAssignee;
import edu.harvard.iq.dataverse.dataaccess.DataAccess;
import edu.harvard.iq.dataverse.dataaccess.StorageIO;
import edu.harvard.iq.dataverse.engine.command.AbstractCommand;
import edu.harvard.iq.dataverse.engine.command.CommandContext;
import edu.harvard.iq.dataverse.engine.command.DataverseRequest;
import edu.harvard.iq.dataverse.engine.command.RequiredPermissions;
import edu.harvard.iq.dataverse.engine.command.exception.CommandException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author sarahferry
 */

@RequiredPermissions(Permission.ManageDatasetPermissions)
public class UpdateSwiftACLRoleCommand extends AbstractCommand<DvObject>{
    private final RoleAssignee user;
    private final DataverseRole role;
    private final String action;
    private final DvObject defPoint;
    
    public UpdateSwiftACLRoleCommand(DataverseRequest aRequest, DvObject defPoint, RoleAssignee roleAssignee, DataverseRole role, String action) {
        super(aRequest, defPoint);
        this.user = roleAssignee;
        this.role = role;
        this.action = action;
        this.defPoint = defPoint;
    }

    @Override
    public DvObject execute(CommandContext ctxt) throws CommandException {
        logger.info("In UpdateSwiftACLRoleCommand");
        List<Dataset> dvContents = new ArrayList<>();
        if (defPoint.isInstanceofDataverse()){
            dvContents = ctxt.datasets().findByOwnerId(defPoint.getId());
        } else if (defPoint.isInstanceofDataset()) {
            dvContents.add((Dataset)defPoint);
        } else {
            throw new CommandException("Failed to update permissions - only applicable for dataset and dataverses.", this);
        }
        
        for (Dataset dataset : dvContents) {
            if (dataset.getStorageIdentifier() != null) {
                if (dataset.getStorageIdentifier().startsWith("swift://")) {
                    StorageIO<Dataset> dataAccess = null;
                    try {
                       dataAccess = DataAccess.getStorageIO(dataset);
                       dataAccess.updateDatasetPermissions(user, role, action);
                    } catch (IOException ex) {
                        throw new CommandException("Failed to update dataset permissions: " + ex.getMessage(), this);
                    }
                }
            } else {
                throw new CommandException("Failed to update permissions because the storage identifier was null", this);
            } 
        }
        return defPoint;   
    }
}

package edu.harvard.iq.dataverse.engine.command.impl;

import edu.harvard.iq.dataverse.DataFile;
import edu.harvard.iq.dataverse.Dataverse;
import static edu.harvard.iq.dataverse.IdServiceBean.logger;
import edu.harvard.iq.dataverse.RoleAssignment;
import edu.harvard.iq.dataverse.authorization.Permission;
import edu.harvard.iq.dataverse.engine.command.AbstractVoidCommand;
import edu.harvard.iq.dataverse.engine.command.CommandContext;
import edu.harvard.iq.dataverse.engine.command.DataverseRequest;
import edu.harvard.iq.dataverse.engine.command.exception.CommandException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Revokes a role for a user on a dataverse.
 * @author michael
 */
// no annotations here, since permissions are dynamically decided
public class RevokeRoleCommand extends AbstractVoidCommand {
	
	private final RoleAssignment toBeRevoked;

	public RevokeRoleCommand(RoleAssignment toBeRevoked, DataverseRequest aRequest) {
        // for data file check permission on owning dataset
        super(aRequest, toBeRevoked.getDefinitionPoint() instanceof DataFile ? toBeRevoked.getDefinitionPoint().getOwner() : toBeRevoked.getDefinitionPoint());
		this.toBeRevoked = toBeRevoked;
	}
	
	@Override
	protected void executeImpl(CommandContext ctxt) throws CommandException {
            logger.info("revoking role...");
            logger.info("dataset:" + toBeRevoked.getDefinitionPoint());
            logger.info("dataset id:" + toBeRevoked.getDefinitionPoint().getId());

            logger.info("id: " + ctxt.roleAssignees().getRoleAssignee(toBeRevoked.getAssigneeIdentifier()));
            
            ctxt.roles().revoke(toBeRevoked);
            ctxt.engine().submit(new UpdateSwiftACLRoleCommand(this.getRequest(), toBeRevoked.getDefinitionPoint(), ctxt.roleAssignees().getRoleAssignee(toBeRevoked.getAssigneeIdentifier()), toBeRevoked.getRole(), "revokeRole"));
	}
        
    @Override
    public Map<String, Set<Permission>> getRequiredPermissions() {
        // for data file check permission on owning dataset
        return Collections.singletonMap("",
                toBeRevoked.getDefinitionPoint() instanceof Dataverse ? Collections.singleton(Permission.ManageDataversePermissions)
                : Collections.singleton(Permission.ManageDatasetPermissions));
    }	
}

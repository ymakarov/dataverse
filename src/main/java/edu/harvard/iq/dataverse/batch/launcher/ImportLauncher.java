/*
   Copyright (C) 2005-2016, by the President and Fellows of Harvard College.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

   Dataverse Network - A web application to share, preserve and analyze research data.
   Developed at the Institute for Quantitative Social Science, Harvard University.
*/

package edu.harvard.iq.dataverse.batch.launcher;

import com.wordnik.swagger.annotations.*;
import edu.harvard.iq.dataverse.Dataset;
import edu.harvard.iq.dataverse.DatasetServiceBean;
import edu.harvard.iq.dataverse.Dataverse;
import edu.harvard.iq.dataverse.PermissionServiceBean;
import edu.harvard.iq.dataverse.api.AbstractApiBean;
import edu.harvard.iq.dataverse.authorization.Permission;
import edu.harvard.iq.dataverse.batch.job.ChecksumImportJob;
import edu.harvard.iq.dataverse.batch.job.FilesystemImportJob;
import edu.harvard.iq.dataverse.engine.command.DataverseRequest;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Stateless
@Path("import")
@Api(value = "/import", description = "import jobs")
public class ImportLauncher extends AbstractApiBean {

    @EJB
    PermissionServiceBean permissionService;

    @EJB
    DatasetServiceBean datasetService;

    @GET
    @Path("/dataset/filesystem")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Execute filesystem import job",
            notes = "This can be a long-running process.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success - import job was started"),
            @ApiResponse(code = 400, message = "Bad Request - can't find dataset, other exceptions"),
            @ApiResponse(code = 401, message = "Invalid Request - bad API key"),
            @ApiResponse(code = 500, message = "Server Error - Something went wrong")})
    public Response getFilesystemImport(@ApiParam(value = "Dataset ID", required = true) @QueryParam("datasetId") String dsId,
                              @ApiParam(value = "API key", required = true) @QueryParam("key") String key) {
        try {
            try {
                Dataset ds = datasetService.findByGlobalId(dsId);
                if (ds != null) {

                    DataverseRequest req = createDataverseRequest(findAuthenticatedUserOrDie());

                    if (isAuthorized(req, ds.getOwner())) {

                        FilesystemImportJob fsJob = new FilesystemImportJob(ds);
                        fsJob.setSilentMode(true);
                        fsJob.setUser(req.getUser());
                        String jobId = fsJob.build();

                        if (jobId != null) {
                            fsJob.execute();
                            return this.okResponseWithValue("FilesystemImportJob has been started: " + jobId);
                        } else {
                            return this.errorResponse(Response.Status.BAD_REQUEST,
                                    "Error creating FilesystemImportJob with dataset with ID: " + dsId);
                        }

                    } else {
                        return this.errorResponse(Response.Status.FORBIDDEN, "User is not authorized.");
                    }

                } else {
                    return this.errorResponse(Response.Status.BAD_REQUEST, "Can't find dataset with ID: " + dsId);
                }
            } catch (WrappedResponse wr) {
                return wr.getResponse();
            }
        } catch (Exception e) {
            return this.errorResponse(Response.Status.BAD_REQUEST, "Import Exception - " + e.getMessage());
        }
    }

    @GET
    @Path("/dataset/checksums")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Execute checksum import job",
            notes = "This can be a long-running process.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success - import job was started"),
            @ApiResponse(code = 400, message = "Bad Request - can't find dataset, other exceptions"),
            @ApiResponse(code = 401, message = "Invalid Request - bad API key"),
            @ApiResponse(code = 500, message = "Server Error - Something went wrong")})
    public Response getChecksumImport(@ApiParam(value = "Dataset ID", required = true) @QueryParam("datasetId") String dsId,
                              @ApiParam(value = "API key", required = true) @QueryParam("key") String key) {
        try {
            try {
                Dataset ds = datasetService.findByGlobalId(dsId);
                if (ds != null) {

                    DataverseRequest req = createDataverseRequest(findAuthenticatedUserOrDie());

                    if (isAuthorized(req, ds.getOwner())) {

                        ChecksumImportJob ckJob = new ChecksumImportJob(ds);
                        ckJob.setSilentMode(true);
                        ckJob.setUser(req.getUser());
                        String jobId = ckJob.build();

                        if (jobId != null) {
                            ckJob.execute();
                            return this.okResponseWithValue("ChecksumImportJob has been started: " + jobId);
                        } else {
                            return this.errorResponse(Response.Status.BAD_REQUEST,
                                    "Can't find a checksum file for dataset with ID: " + dsId);
                        }

                    } else {
                        return this.errorResponse(Response.Status.FORBIDDEN, "User is not authorized.");
                    }

                } else {
                    return this.errorResponse(Response.Status.BAD_REQUEST, "Can't find dataset with ID: " + dsId);
                }
            } catch (WrappedResponse wr) {
                return wr.getResponse();
            }
        } catch (Exception e) {
            return this.errorResponse(Response.Status.BAD_REQUEST, "ChecksumImportJob Exception, " + e.getMessage());
        }
    }

    /**
     *
     * @param dvReq
     * @param dv
     * @return
     */
    private boolean isAuthorized(DataverseRequest dvReq, Dataverse dv) {
        if (permissionService.requestOn(dvReq, dv).has(Permission.EditDataverse))
            return true;
        else
            return false;
    }

}
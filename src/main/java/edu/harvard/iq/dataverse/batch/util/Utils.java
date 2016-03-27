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

package edu.harvard.iq.dataverse.batch.util;

import edu.harvard.iq.dataverse.*;
import edu.harvard.iq.dataverse.actionlogging.ActionLogRecord;
import edu.harvard.iq.dataverse.actionlogging.ActionLogServiceBean;
import edu.harvard.iq.dataverse.authorization.users.AuthenticatedUser;
import edu.harvard.iq.dataverse.authorization.users.User;
import edu.harvard.iq.dataverse.batch.tools.reporting.HtmlJobReportFormatterDVN;
import org.apache.commons.io.FileUtils;
import org.easybatch.core.job.Job;
import org.easybatch.core.job.JobReport;

import javax.ejb.EJB;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.io.File;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

/**
 * Created by bmckinney on 3/24/16.
 */

public abstract class Utils {

    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    public static final String FILE_SEPARATOR = System.getProperty("file.separator");

    public static final String JAVA_IO_TMPDIR = System.getProperty("java.io.tmpdir");

    @EJB
    private static DataFileServiceBean fileService;

    @EJB
    private static UserNotificationServiceBean userNotificationService;

    @EJB
    private static ActionLogServiceBean actionLogServiceBean;

    private Utils() { }

    public static DataFile getDataFile(Dataset dataset, String path) {
        try {
            Context ctx = new InitialContext();
            fileService = (DataFileServiceBean)
                    ctx.lookup("java:module/DataFileServiceBean");
            return fileService.findByStorageIdandDatasetVersion(path, dataset.getLatestVersion());
        } catch (Exception e) {
            System.out.println("Error finding datafile: " + e.getMessage());
            return null;
        }
    }

    public static boolean alreadyImported(Dataset dataset, String path) {
        try {
            Context ctx = new InitialContext();
            fileService = (DataFileServiceBean)
                    ctx.lookup("java:module/DataFileServiceBean");
            if (fileService.findByStorageIdandDatasetVersion(path, dataset.getLatestVersion()) != null) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            System.out.println("Error finding datafile: " + e.getMessage());
            return false;
        }
    }

    /**
     * Create the datafile and corresponding file metadata.
     * @param file file
     * @return the datafile
     */
    public static DataFile createDataFile(Dataset dataset, File file) {

        DatasetVersion version = dataset.getLatestVersion();

        // create datafile
        String path = file.getAbsolutePath();
        String dsid = dataset.getIdentifier();
        String relativePath = path.substring(path.indexOf(dsid) + dsid.length() + 1);
        DataFile datafile = new DataFile("application/octet-stream");
        datafile.setStorageIdentifier(relativePath);
        datafile.setFilesize(file.length());
        datafile.setModificationTime(new Timestamp(new Date().getTime()));
        datafile.setCreateDate(new Timestamp(new Date().getTime()));
        datafile.setPermissionModificationTime(new Timestamp(new Date().getTime()));
        datafile.setOwner(dataset);
        datafile.setIngestDone();
        datafile.setmd5("Unknown");
        datafile = fileService.save(datafile);

        // set metadata and add to latest version
        FileMetadata fmd = new FileMetadata();
        fmd.setLabel(file.getName());
        fmd.setDataFile(datafile);
        datafile.getFileMetadatas().add(fmd);
        if (version.getFileMetadatas() == null) {
            version.setFileMetadatas(new ArrayList());
        }
        version.getFileMetadatas().add(fmd);
        fmd.setDatasetVersion(version);

        return datafile;
    }

    public static void saveDataFile(DataFile dataFile) {
        try {
            Context ctx = new InitialContext();
            fileService = (DataFileServiceBean)
                    ctx.lookup("java:module/DataFileServiceBean");
            fileService.save(dataFile);
        } catch (Exception e) {
            System.out.println("Error saving datafile: " + e.getMessage());
        }
    }

    public static void sendNotification(User user, Long dvObjId, UserNotification.Type type) {
        try {
            Context ctx = new InitialContext();
            userNotificationService = (UserNotificationServiceBean) ctx.lookup("java:module/UserNotificationServiceBean");
            userNotificationService.sendNotification(
                    (AuthenticatedUser) user,
                    new Timestamp(new Date().getTime()),
                    type, dvObjId);
        } catch (Exception e) {
            System.out.println("Error sending job notification: " + e.getMessage());
        }
    }

    public static void createActionLogRecord(JobReport report, User user) {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Context ctx = new InitialContext();
            actionLogServiceBean = (ActionLogServiceBean) ctx.lookup("java:module/ActionLogServiceBean");
            ActionLogRecord alr = new ActionLogRecord(ActionLogRecord.ActionType.Command, report.getParameters().getName());
            alr.setId(report.getParameters().getExecutionId());
            alr.setInfo(report.toString());
            alr.setUserIdentifier(user.getIdentifier());
            if (report.getStatus().toString().equalsIgnoreCase("COMPLETED")) {
                alr.setActionResult(ActionLogRecord.Result.OK);
            } else {
                alr.setActionResult(ActionLogRecord.Result.InternalError);
            }
            try {
                alr.setStartTime(formatter.parse(report.getFormattedStartTime()));
                alr.setEndTime(formatter.parse(report.getFormattedEndTime()));
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
            actionLogServiceBean.log(alr);
        } catch (Exception e) {
            System.out.println("Error creating action log record: " + e.getMessage());
        }
    }

    /**
     *
     * @param job
     * @param report
     */
    public static void saveHtmlReport(Job job, JobReport report) {
        String reportDir = FILE_SEPARATOR + "docroot" + FILE_SEPARATOR + "reports";
        Properties p = System.getProperties();
        String domainRoot = p.getProperty("com.sun.aas.instanceRoot");
        String fqdn = p.getProperty("dataverse.fqdn");
        if (domainRoot != null && !"".equals(domainRoot)) {
            File reportsDir = new File(domainRoot + reportDir);
            File reportHtml = new File(domainRoot + reportDir + FILE_SEPARATOR + job.getExecutionId() + ".html");
            reportsDir.mkdir();
            String htmlReport = new HtmlJobReportFormatterDVN().formatReport(report);
            try {
                FileUtils.writeStringToFile(reportHtml, htmlReport);
            } catch (Exception e) {
                System.out.println("Error saving report: " + e.getMessage());
            }
        }
    }

}

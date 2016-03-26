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

package edu.harvard.iq.dataverse.batch.job;

import edu.harvard.iq.dataverse.Dataset;
import edu.harvard.iq.dataverse.UserNotification;
import edu.harvard.iq.dataverse.authorization.users.User;
import edu.harvard.iq.dataverse.batch.processor.ChecksumProcessor;
import edu.harvard.iq.dataverse.batch.util.Utils;
import org.easybatch.core.job.Job;
import org.easybatch.core.job.JobBuilder;
import org.easybatch.core.job.JobExecutor;
import org.easybatch.core.job.JobReport;
import org.easybatch.flatfile.FlatFileRecordReader;
import org.easybatch.core.writer.StandardOutputRecordWriter;

import java.io.File;

public class ChecksumImportJob {

    private File dir;
    private Dataset dataset;
    private User user;

    private Job job;
    private String name;
    private String email;
    private boolean jmxMode = false;
    private boolean silentMode = false;
    private boolean strictMode = false;
    private long timeout;
    private long skip = 0;
    private long limit;
    private String checksumFilename = "files.sha";

    /**
     *
     * @param dataset
     */
    public ChecksumImportJob(Dataset dataset) {

        this.dataset = dataset;
        this.name = dataset.getIdentifier() + "-checksumimport";
        this.dir = dataset.getFileSystemDirectory().toFile();

    }

    public String build() {

        try {
            File checksums = new File(this.dir.getAbsolutePath() + Utils.FILE_SEPARATOR + checksumFilename);
            this.job = new JobBuilder()
                    .named(this.name)
                    .silentMode(this.silentMode)
                    .strictMode(this.strictMode)
                    .jmxMode(this.jmxMode)
                    .reader(new FlatFileRecordReader(checksums))
                    .processor(new ChecksumProcessor(this.dataset))
                    .writer(new StandardOutputRecordWriter())
                    .build();
            return this.job.getExecutionId();

        } catch (Exception e) {
            System.out.println("Error: can't find checksum file: " + this.dir.getAbsolutePath() + "/files.sha");
            return null;
        }

    }

    /**
     *
     * @return
     */
    public void execute() {

        JobReport report = JobExecutor.execute(job);
        // show report
        System.out.println(report);
        // save report as html
        Utils.saveHtmlReport(this.job, report);
        // send notification
        Utils.sendNotification( this.user, this.dataset.getLatestVersion().getId(), UserNotification.Type.CHECKSUMIMPORT);
        // add action log record
        Utils.createActionLogRecord(report, this.user);

    }

    // GETTER-SETTERS

    public String getJobId() {
        return this.job.getExecutionId();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public File getDir() {
        return dir;
    }

    public void setDir(File dir) {
        this.dir = dir;
    }

    public Dataset getDataset() {
        return dataset;
    }

    public void setDataset(Dataset dataset) {
        this.dataset = dataset;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isJmxMode() {
        return jmxMode;
    }

    public void setJmxMode(boolean jmxMode) {
        this.jmxMode = jmxMode;
    }

    public boolean isSilentMode() {
        return silentMode;
    }

    public void setSilentMode(boolean silentMode) {
        this.silentMode = silentMode;
    }

    public boolean isStrictMode() {
        return strictMode;
    }

    public void setStrictMode(boolean strictMode) {
        this.strictMode = strictMode;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public long getSkip() {
        return skip;
    }

    public void setSkip(long skip) {
        this.skip = skip;
    }

    public long getLimit() {
        return limit;
    }

    public void setLimit(long limit) {
        this.limit = limit;
    }

    public String getChecksumFilename() {
        return checksumFilename;
    }

    public void setChecksumFilename(String checksumFilename) {
        this.checksumFilename = checksumFilename;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

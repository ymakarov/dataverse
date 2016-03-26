package edu.harvard.iq.dataverse.batch.tools.reporting;


import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.easybatch.core.job.JobReport;
import org.easybatch.core.job.JobReportFormatter;

import java.io.StringWriter;
import java.util.Properties;

/**
 * Format a report into HTML format.
 *
 */
public class HtmlJobReportFormatterDVN implements JobReportFormatter<String> {

    /**
     * The template engine to render reports.
     */
    private VelocityEngine velocityEngine;

    public HtmlJobReportFormatterDVN() {
        Properties properties = new Properties();
        properties.put("resource.loader", "class");
        properties.put("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        velocityEngine = new VelocityEngine(properties);
        velocityEngine.init();
    }

    @Override
    public String formatReport(final JobReport jobReport) {
        String dsPath = jobReport.getParameters().getDataSource();
        dsPath = dsPath.substring(dsPath.indexOf("files/") + "files/".length());
        Template template = velocityEngine.getTemplate("/edu/harvard/iq/dataverse/HtmlReport.vm");
        StringWriter stringWriter = new StringWriter();
        Context context = new VelocityContext();
        context.put("report", jobReport);
        context.put("displayDatasource", dsPath);
        template.merge(context, stringWriter);
        return stringWriter.toString();
    }

}

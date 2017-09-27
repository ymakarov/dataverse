package edu.harvard.iq.dataverse.publicationannouncer;

import edu.harvard.iq.dataverse.workflow.stepproviderlib.Failure;
import edu.harvard.iq.dataverse.workflow.stepproviderlib.WorkflowContext;
import edu.harvard.iq.dataverse.workflow.stepproviderlib.WorkflowStep;
import edu.harvard.iq.dataverse.workflow.stepproviderlib.WorkflowStepResult;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

/**
 * A step that announces a dataset publication on Slack™.
 * @author michael
 */
public class SlackAnnouncer implements WorkflowStep {
    
    final String username, channel, url;

    public SlackAnnouncer(String username, String channel, String url) {
        this.username = username;
        this.channel = channel;
        this.url = url;
    }
    
    @Override
    public WorkflowStepResult run(WorkflowContext context) {
        String text = "Publishing a new dataset version: " + context.getDatasetVersionData().getDisplayName()
                      + " version " + context.getNextVersionNumber() + "." + context.getNextMinorVersionNumber()
                      + " " + context.getDatasetVersionData().getGlobalId();
        try {
            sendSlackMessage(url, text, ":dataverseman:");
        } catch (IOException ex) {
            // Failure here is not critical enough to stop the process.
            Logger.getLogger(SlackAnnouncer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return WorkflowStepResult.OK;
    }

    @Override
    public WorkflowStepResult resume(WorkflowContext context, Map<String, String> internalData, String externalData) {
        throw new UnsupportedOperationException("This step cannot resume.");
    }

    @Override
    public void rollback(WorkflowContext context, Failure reason) {
        String text = "Cancelled publication of " + context.getDatasetVersionData().getDisplayName()
                      + " version " + context.getNextVersionNumber() + "." + context.getNextMinorVersionNumber()
                      + " due to " + reason.getMessage() ;
        
        try {
            sendSlackMessage(url, text, ":pig_in_mud:");
        } catch (IOException ex) {
            Logger.getLogger(SlackAnnouncer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void sendSlackMessage( String channelUrl, String text, String icon ) throws UnsupportedEncodingException, IOException {
        StringBuilder json = new StringBuilder();
        /*
        "payload={"channel": "#webhook_test", 
                  "username": "dataverse", 
                  "text": "Test message #2.", 
                  "icon_emoji": ":dataverseman:"}
        */
        json.append("{");
        json.append("\"channel\":\"").append(channel).append("\", ");
        json.append("\"username\":\"").append(username).append("\", ");
        json.append("\"icon_emoji\":\"").append(icon).append("\", ");
        json.append("\"text\":\"").append(text.replaceAll("\"","\\\\\"")).append("\" ");
        json.append("}");
                
        CloseableHttpClient httpclient = HttpClients.createDefault();
        
        HttpPost httpPost = new HttpPost(channelUrl);
        List<NameValuePair> items = Arrays.asList( new BasicNameValuePair("payload", json.toString()));
        httpPost.setEntity(new UrlEncodedFormEntity(items) );
        CloseableHttpResponse response2 = httpclient.execute(httpPost);
        
        InputStreamReader rdr = new InputStreamReader(response2.getEntity().getContent());
        BufferedReader brdr = new BufferedReader(rdr);
        brdr.lines().forEach( System.out::println );
        
        System.out.println(json.toString());
    }
    
    
    public static void main(String[] args) throws IOException {
        new SlackAnnouncer("main", "#webhook_test", "https://hooks.slack.com/services/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").sendSlackMessage(
                "https://hooks.slack.com/services/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", 
                "Java test",
                ":ghost:");
    }
}

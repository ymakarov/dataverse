package edu.harvard.iq.dataverse.datacapturemodule;

import edu.harvard.iq.dataverse.authorization.users.AuthenticatedUser;
import java.util.logging.Logger;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import edu.harvard.iq.dataverse.Dataset;
import edu.harvard.iq.dataverse.settings.SettingsServiceBean;
import static edu.harvard.iq.dataverse.settings.SettingsServiceBean.Key.DataCaptureModuleUrl;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Named;
import javax.json.JsonObjectBuilder;

@Stateless
@Named
public class DataCaptureModuleServiceBean implements Serializable {

    private static final Logger logger = Logger.getLogger(DataCaptureModuleServiceBean.class.getCanonicalName());

    @EJB
    SettingsServiceBean settingsService;

    /**
     * @param user AuthenticatedUser
     * @return Unirest response as JSON or null.
     * @throws Exception Throws an exception if Data Capture Module URL hasn't
     * been configured or if the POST failed.
     */
    public HttpResponse<JsonNode> requestRsyncScriptCreation(AuthenticatedUser user, Dataset dataset, JsonObjectBuilder jab) throws Exception {
        String dcmBaseUrl = settingsService.getValueForKey(DataCaptureModuleUrl);
        if (dcmBaseUrl == null) {
            throw new Exception("Problem POSTing JSON to Data Capture Module. The '" + DataCaptureModuleUrl + "' setting has not been configured.");
        }
        String jsonString = jab.build().toString();
        logger.info("JSON to send to Data Capture Module: " + jsonString);
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.post(dcmBaseUrl + "/ur.py")
                    .body(jsonString)
                    .asJson();
            return jsonResponse;
        } catch (Exception ex) {
            throw new Exception("Problem POSTing JSON to Data Capture Module at " + dcmBaseUrl + " . " + ex.getLocalizedMessage());
        }

    }

}

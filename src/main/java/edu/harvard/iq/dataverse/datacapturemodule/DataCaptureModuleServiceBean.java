package edu.harvard.iq.dataverse.datacapturemodule;

import edu.harvard.iq.dataverse.authorization.users.AuthenticatedUser;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import edu.harvard.iq.dataverse.settings.SettingsServiceBean;
import static edu.harvard.iq.dataverse.settings.SettingsServiceBean.Key.DataCaptureModuleUrl;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Named;

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
    public HttpResponse<JsonNode> requestRsyncScriptCreation(AuthenticatedUser user) throws Exception {
        /**
         * @todo Move this to a "util" class.
         *
         * @todo Send user id instead of "userIdentifier" ... may need to add
         * more lookups by user database id to the "admin" API.
         *
         * @todo pass dataset id
         */
        JsonObjectBuilder jsonObject = Json.createObjectBuilder();
        jsonObject.add("userIdentifier", user.getAuthenticatedUserLookup().getPersistentUserId());
        jsonObject.add("uid", user.getAuthenticatedUserLookup().getPersistentUserId());
        String json = jsonObject.build().toString();
        String dcmBaseUrl = settingsService.getValueForKey(DataCaptureModuleUrl);
        if (dcmBaseUrl == null) {
            throw new Exception("Problem POSTing JSON to Data Capture Module. The '" + DataCaptureModuleUrl + "' setting has not been configured.");
        }
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.post(dcmBaseUrl + "/ur.py")
                    .body(json)
                    .asJson();
            return jsonResponse;
        } catch (Exception ex) {
            throw new Exception("Problem POSTing JSON to Data Capture Module at " + dcmBaseUrl + " . " + ex.getLocalizedMessage());
        }

    }

}

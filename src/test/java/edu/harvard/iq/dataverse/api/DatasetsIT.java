package edu.harvard.iq.dataverse.api;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;
import java.util.logging.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.AfterClass;
import static com.jayway.restassured.RestAssured.given;
import static junit.framework.Assert.assertEquals;
import com.jayway.restassured.path.json.JsonPath;
import java.util.List;
import java.util.Map;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.hamcrest.Matchers.startsWith;

public class DatasetsIT {

    private static final Logger logger = Logger.getLogger(DatasetsIT.class.getCanonicalName());
    private static String username1;
    private static String apiToken1;
    private static String dataverseAlias1;
    private static Integer datasetId1;

    @BeforeClass
    public static void setUpClass() {
        RestAssured.baseURI = UtilIT.getRestAssuredBaseUri();
    }

    @Test
    public void testCreateDataset() {

        Response createUser1 = UtilIT.createRandomUser();
//        createUser1.prettyPrint();
        username1 = UtilIT.getUsernameFromResponse(createUser1);
        apiToken1 = UtilIT.getApiTokenFromResponse(createUser1);

        Response createDataverse1Response = UtilIT.createRandomDataverse(apiToken1);
        createDataverse1Response.prettyPrint();
        dataverseAlias1 = UtilIT.getAliasFromResponse(createDataverse1Response);

        Response createDataset1Response = UtilIT.createRandomDatasetViaNativeApi(dataverseAlias1, apiToken1);
        createDataset1Response.prettyPrint();
        datasetId1 = UtilIT.getDatasetIdFromResponse(createDataset1Response);

    }

    /**
     * In order for this test to pass you must have the Data Capture Module
     * running:
     * https://github.com/sbgrid/data-capture-module/blob/master/api/dcm.py
     *
     * Configure :DataCaptureModuleUrl to point at wherever you are running the
     * DCM.
     */
    @Test
    public void testCreateDatasetWithDcmDependency() {

        Response createUser = UtilIT.createRandomUser();
        createUser.prettyPrint();
        String username = UtilIT.getUsernameFromResponse(createUser);
        String apiToken = UtilIT.getApiTokenFromResponse(createUser);

        Response createDataverseResponse = UtilIT.createRandomDataverse(apiToken);
        createDataverseResponse.prettyPrint();
        String dataverseAlias = UtilIT.getAliasFromResponse(createDataverseResponse);

        /**
         * @todo Make this configurable at runtime similar to
         * UtilIT.getRestAssuredBaseUri
         */
        Response createDatasetResponse = UtilIT.createDatasetWithDcmDependency(dataverseAlias, apiToken);
        createDatasetResponse.prettyPrint();
        Integer datasetId = UtilIT.getDatasetIdFromResponse(createDatasetResponse);

        Response getDatasetResponse = given()
                .header(UtilIT.API_TOKEN_HTTP_HEADER, apiToken)
                .get("/api/datasets/" + datasetId);
        getDatasetResponse.prettyPrint();
        getDatasetResponse.then().assertThat()
                .statusCode(200);

        final List<Map<String, ?>> dataTypeField = JsonPath.with(getDatasetResponse.body().asString())
                .get("data.latestVersion.metadataBlocks.citation.fields.findAll { it.typeName == 'dataType' }");
        logger.fine("dataTypeField: " + dataTypeField);
        assertThat(dataTypeField.size(), equalTo(1));
        assertEquals("dataType", dataTypeField.get(0).get("typeName"));
        assertEquals("controlledVocabulary", dataTypeField.get(0).get("typeClass"));
        assertEquals("X-Ray Diffraction", dataTypeField.get(0).get("value"));
        assertTrue(dataTypeField.get(0).get("multiple").equals(false));

        /**
         * @todo Also test user who doesn't have permission.
         */
        Response getRsyncScriptPermErrorGuest = given()
                .get("/api/datasets/" + datasetId + "/dataCaptureModule/rsync");
        getRsyncScriptPermErrorGuest.prettyPrint();
        getRsyncScriptPermErrorGuest.then().assertThat()
                .statusCode(401)
                .body("message", equalTo("User :guest is not permitted to perform requested action."));

        Response createNoPermsUser = UtilIT.createRandomUser();
        String noPermsUsername = UtilIT.getUsernameFromResponse(createNoPermsUser);
        String noPermsApiToken = UtilIT.getApiTokenFromResponse(createNoPermsUser);

        Response getRsyncScriptPermErrorNonGuest = given()
                .header(UtilIT.API_TOKEN_HTTP_HEADER, noPermsApiToken)
                .get("/api/datasets/" + datasetId + "/dataCaptureModule/rsync");
        getRsyncScriptPermErrorNonGuest.then().assertThat()
                .statusCode(401)
                .body("message", equalTo("User @" + noPermsUsername + " is not permitted to perform requested action."));

        Response getRsyncScript = given()
                .header(UtilIT.API_TOKEN_HTTP_HEADER, apiToken)
                .get("/api/datasets/" + datasetId + "/dataCaptureModule/rsync");
        getRsyncScript.prettyPrint();
        getRsyncScript.then().assertThat()
                .statusCode(200)
                .body("data.datasetId", equalTo(datasetId))
                .body("data.script", startsWith("#!"));

        Response attmeptToGetRsyncScriptForNonRsyncDataset = given()
                .header(UtilIT.API_TOKEN_HTTP_HEADER, apiToken1)
                .get("/api/datasets/" + datasetId1 + "/dataCaptureModule/rsync");
        attmeptToGetRsyncScriptForNonRsyncDataset.prettyPrint();
        attmeptToGetRsyncScriptForNonRsyncDataset.then().assertThat()
                .statusCode(404)
                .body("message", equalTo("An rsync script was not found for dataset id " + datasetId1));

        /**
         * Here we are pretending to be the Data Capture Module reporting on if
         * checksum validation success or failure. Don't notify the user yet
         * (too chatty). This should kick off crawling of the files so they are
         * imported into Dataverse. Once the crawling and importing is complete,
         * notify the user.
         *
         * @todo What authentication should be used here? The API token of the
         * user? (If so, pass the token in the initial upload request payload.)
         * Or should Dataverse be able to be configured so that it only will
         * receive these messages from trusted IP addresses? Should there be a
         * shared secret that's used for *all* requests from the Data Capture
         * Module to Dataverse?
         */
//        Response uploadSuccessful = given()
//                .header(UtilIT.API_TOKEN_HTTP_HEADER, apiToken)
//                // send json here: { "userId":"$bar", "datasetId" : "$foo" , "status" : "$status"}
//                //  status either "validation passed" or "validation failed"
//                .post("/api/dataCaptureModule/checksumValidation");
//        uploadSuccessful.prettyPrint();
        Response deleteDatasetResponse = UtilIT.deleteDatasetViaNativeApi(datasetId, apiToken);
        deleteDatasetResponse.prettyPrint();
        assertEquals(200, deleteDatasetResponse.getStatusCode());

        Response deleteDataverseResponse = UtilIT.deleteDataverse(dataverseAlias, apiToken);
        deleteDataverseResponse.prettyPrint();
        assertEquals(200, deleteDataverseResponse.getStatusCode());

        Response deleteUserResponse = UtilIT.deleteUser(username);
        deleteUserResponse.prettyPrint();
        assertEquals(200, deleteUserResponse.getStatusCode());

        UtilIT.deleteUser(noPermsUsername);

    }

    @Test
    public void testGetDdi() {
        String persistentIdentifier = "FIXME";
        String apiToken = "FIXME";
        Response nonDto = getDatasetAsDdiNonDto(persistentIdentifier, apiToken);
        nonDto.prettyPrint();
        assertEquals(403, nonDto.getStatusCode());

        Response dto = getDatasetAsDdiDto(persistentIdentifier, apiToken);
        dto.prettyPrint();
        assertEquals(403, dto.getStatusCode());
    }

    private Response getDatasetAsDdiNonDto(String persistentIdentifier, String apiToken) {
        Response response = given()
                .header(UtilIT.API_TOKEN_HTTP_HEADER, apiToken)
                .get("/api/datasets/ddi?persistentId=" + persistentIdentifier);
        return response;
    }

    private Response getDatasetAsDdiDto(String persistentIdentifier, String apiToken) {
        Response response = given()
                .header(UtilIT.API_TOKEN_HTTP_HEADER, apiToken)
                .get("/api/datasets/ddi?persistentId=" + persistentIdentifier + "&dto=true");
        return response;
    }

    @AfterClass
    public static void tearDownClass() {
        boolean disabled = false;

        if (disabled) {
            return;
        }

        Response deleteDatasetResponse = UtilIT.deleteDatasetViaNativeApi(datasetId1, apiToken1);
        deleteDatasetResponse.prettyPrint();
        assertEquals(200, deleteDatasetResponse.getStatusCode());

        Response deleteDataverse1Response = UtilIT.deleteDataverse(dataverseAlias1, apiToken1);
        deleteDataverse1Response.prettyPrint();
        assertEquals(200, deleteDataverse1Response.getStatusCode());

        Response deleteUser1Response = UtilIT.deleteUser(username1);
        deleteUser1Response.prettyPrint();
        assertEquals(200, deleteUser1Response.getStatusCode());

    }

}

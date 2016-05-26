package edu.harvard.iq.dataverse.api;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;
import java.util.logging.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.AfterClass;
import static com.jayway.restassured.RestAssured.given;
import com.jayway.restassured.http.ContentType;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.OK;
import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

    @Test
    public void testCitations() {
        Response createUser = UtilIT.createRandomUser();

        String username = UtilIT.getUsernameFromResponse(createUser);
        String apiToken = UtilIT.getApiTokenFromResponse(createUser);

        Response createDataverseResponse = UtilIT.createRandomDataverse(apiToken);
        createDataverseResponse.prettyPrint();
        String dataverseAlias = UtilIT.getAliasFromResponse(createDataverseResponse);
        assertEquals(CREATED.getStatusCode(), createDataverseResponse.getStatusCode());
        Response createDataset = UtilIT.createRandomDatasetViaNativeApi(dataverseAlias, apiToken);
        createDataset.prettyPrint();
        createDataset.then().assertThat()
                .statusCode(CREATED.getStatusCode());
        Integer datasetId = UtilIT.getDatasetIdFromResponse(createDataset);

        Response citation1 = UtilIT.getCitation(datasetId, apiToken);
        citation1.prettyPrint();
        citation1.then().assertThat()
                .statusCode(OK.getStatusCode())
                .contentType(ContentType.TEXT);
        /**
         * @todo Look into "Expected response body to be verified as JSON, HTML
         * or XML but content-type 'text/plain' is not supported out of the
         * box."
         */
        assertTrue(citation1.body().asString().contains("Darwin's Finches"));

        UtilIT.deleteDatasetViaNativeApi(datasetId, apiToken).then().assertThat()
                .statusCode(OK.getStatusCode());

        UtilIT.deleteDataverse(dataverseAlias, apiToken).then().assertThat()
                .statusCode(OK.getStatusCode());

        UtilIT.makeSuperUser(username).then().assertThat()
                .statusCode(OK.getStatusCode());

        UtilIT.deleteUser(username).then().assertThat()
                .statusCode(OK.getStatusCode());

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

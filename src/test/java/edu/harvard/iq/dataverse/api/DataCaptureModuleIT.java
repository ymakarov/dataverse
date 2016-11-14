package edu.harvard.iq.dataverse.api;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.json.JsonPath;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;

import static com.jayway.restassured.RestAssured.given;
import static junit.framework.Assert.fail;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.startsWith;

/**
 * Test for DCM Module
 */
public class DataCaptureModuleIT {

    private static ClassLoader classLoader = DataCaptureModuleIT.class.getClassLoader();

    // test properties
    private static String testName;
    private static String token;

    // dataset properties
    private static int dsId;
    private static String dsIdentifier;
    private static String dsGlobalId;


    private static Properties props = new Properties();
    private static final String API_TOKEN_HTTP_HEADER = "X-Dataverse-key";
    private static final String BUILTIN_USER_KEY = "burrito";

    private static String dcmUrl = "mock";

    @BeforeClass
    public static void setUpClass() throws Exception {

        // this allows for testing on dataverse staging servers via jvm setting
        String restAssuredBaseUri = "http://localhost:8080";
        String specifiedUri = System.getProperty("dataverse.test.baseurl");
        if (specifiedUri != null) {
            restAssuredBaseUri = specifiedUri;
        }
        System.out.println("Base URL for tests: " + restAssuredBaseUri +  "\n");
        RestAssured.baseURI = restAssuredBaseUri;

        // use mock dcm unless specified via jvm setting
        String specifiedDcmUrl = System.getProperty("dataverse.dcm.url");
        if (specifiedDcmUrl != null) {
            dcmUrl = specifiedDcmUrl;
        }
        System.out.println("DCM URL: " + dcmUrl + "\n");
    }

    @Before
    public void setUpDataverse() {

        try {

            // create random test name
            testName = UUID.randomUUID().toString().substring(0, 8);

            // create user and set token
            token = given()
                    .body("{" +
                            "   \"userName\": \"" + testName + "\"," +
                            "   \"firstName\": \"" + testName + "\"," +
                            "   \"lastName\": \"" + testName + "\"," +
                            "   \"email\": \"" + testName + "@mailinator.com\"" +
                            "}")
                    .contentType(ContentType.JSON)
                    .request()
                    .post("/api/builtin-users/secret/" + BUILTIN_USER_KEY)
                    .then().assertThat().statusCode(200)
                    .extract().jsonPath().getString("data.apiToken");
            System.out.println("TOKEN: " + token);

            // create dataverse
            given().body("{" +
                    "    \"name\": \"" + testName + "\"," +
                    "    \"alias\": \"" + testName + "\"," +
                    "    \"affiliation\": \"Test-Driven University\"," +
                    "    \"dataverseContacts\": [" +
                    "        {" +
                    "            \"contactEmail\": \"test@example.com\"" +
                    "        }," +
                    "        {" +
                    "            \"contactEmail\": \"test@example.org\"" +
                    "        }" +
                    "    ]," +
                    "    \"permissionRoot\": true," +
                    "    \"description\": \"test Description.\"" +
                    "}")
                    .contentType(ContentType.JSON).request()
                    .post("/api/dataverses/:root?key=" + token)
                    .then().assertThat().statusCode(201);
            System.out.println("DATAVERSE: http://localhost:8080/dataverse/" + testName);

            // create x-ray diffraction dataset and set id
            String json = IOUtils.toString(classLoader.getResourceAsStream("json/x-ray-diffraction.json"));
            dsId = given()
                    .header(API_TOKEN_HTTP_HEADER, token)
                    .body(json)
                    .contentType("application/json")
                    .post("/api/dataverses/" + testName + "/datasets")
                    .then().assertThat().statusCode(201)
                    .extract().jsonPath().getInt("data.id");
            System.out.println("DATASET PRIMARY ID: " + dsId);

            // get dataset identifier
            dsIdentifier = given()
                    .header(API_TOKEN_HTTP_HEADER, token)
                    .get("/api/datasets/" + dsId)
                    .then().assertThat().statusCode(200)
                    .extract().jsonPath().getString("data.identifier");
            System.out.println("IDENTIFIER: " + dsIdentifier);

            dsGlobalId = "doi:10.5072/FK2" + File.separator + dsIdentifier;

            // set the dcm url ("mock" unless otherwise specified)
            given()
                    .body(dcmUrl)
                    .contentType("application/json")
                    .put("api/admin/settings/:DataCaptureModuleUrl?key=" + token)
                    .then().assertThat().statusCode(200);
            System.out.println("DCM URL: " + dcmUrl);

            // set the file upload mechanisms: GUI and RSYNC
            given()
                    .header(API_TOKEN_HTTP_HEADER, token)
                    .body("GUI:RSYNC")
                    .contentType("text/plain")
                    .post("api/dataverses/" + testName + "/uploadmechanisms")
                    .then().assertThat().statusCode(200);
            System.out.println("UPLOAD MECHANISMS: GUI:RSYNC");

        } catch (IOException ioe) {
            System.out.println("Error creating test dataset: " + ioe.getMessage());
            fail();
        }
    }

    @AfterClass
    public static void tearDownClass() {
        RestAssured.reset();
    }

    @After
    public void tearDownDataverse() {
        // delete dataset
        given().header(API_TOKEN_HTTP_HEADER, token)
                .delete("/api/datasets/" + dsId)
                .then().assertThat().statusCode(200);
        // delete dataverse
        given().header(API_TOKEN_HTTP_HEADER, token)
                .delete("/api/dataverses/" + testName)
                .then().assertThat().statusCode(200);
        // delete user
        given().header(API_TOKEN_HTTP_HEADER, token)
                .delete("/api/admin/authenticatedUsers/" + testName + "/")
                .then().assertThat().statusCode(200);
    }


    @Test
    public void testSetAndGetRsyncScriptIsSuperUser() {

        // make user a super user
        given().post("/api/admin/superuser/" + testName).then().assertThat().statusCode(200);

        // set script
        given()
                .header(API_TOKEN_HTTP_HEADER, token)
                .body("Just a test")
                .post("/api/datasets/" + dsId + "/dataCaptureModule/rsync")
                .then().assertThat().statusCode(200)
                .body("data.script", equalTo("Just a test"));

        // get script
        if (dcmUrl.equalsIgnoreCase("mock")) {
            given()
                    .header(API_TOKEN_HTTP_HEADER, token)
                    .get("/api/datasets/" + dsId + "/dataCaptureModule/rsync")
                    .then().assertThat().statusCode(200)
                    .body("status", equalTo("OK"))
                    .body("data.script", equalTo("script goes here"));
        } else {
            given()
                    .header(API_TOKEN_HTTP_HEADER, token)
                    .get("/api/datasets/" + dsId + "/dataCaptureModule/rsync")
                    .then().assertThat().statusCode(200)
                    .body("status", equalTo("OK"))
                    .body("data.script", equalTo("Just a test"));
        }

    }

    @Test
    public void testSetAndGetRsyncScriptNotSuperUser() {
        // set script
        given()
                .header(API_TOKEN_HTTP_HEADER, token)
                .body("Just a test")
                .post("/api/datasets/" + dsId + "/dataCaptureModule/rsync")
                .then().assertThat().statusCode(403)
                .body("status", equalTo("ERROR"))
                .body("message", equalTo("Not a superuser"));
    }

    @Test
    public void testSetRsyncWhenNotSupported() {

        // make user a super user
        given()
                .post("/api/admin/superuser/" + testName)
                .then().assertThat().statusCode(200);

        // set the file upload mechanisms to GUI only
        given()
                .header(API_TOKEN_HTTP_HEADER, token)
                .body("GUI")
                .contentType("text/plain")
                .post("api/dataverses/" + testName + "/uploadmechanisms")
                .then().assertThat().statusCode(200);

        // set script
        given()
                .header(API_TOKEN_HTTP_HEADER, token)
                .body("Just a test")
                .post("/api/datasets/" + dsId + "/dataCaptureModule/rsync")
                .then().assertThat().statusCode(501) // not implemented
                .body("status", equalTo("ERROR"))
                .body("message", startsWith("Parent dataverse doesn't support rsync transfers"));

    }

    @Test
    public void testSetChecksumValidation() {

        int jobApiStatus = given()
                .header(API_TOKEN_HTTP_HEADER, token)
                .get("/api/batch/jobs").statusCode();

        // this is testable only if the batch job module is available
        if (jobApiStatus == 200) {

            // Create some files to import
            String dsDir = "/usr/local/glassfish4/glassfish/domains/domain1/files/10.5072/FK2/"
                    + dsIdentifier + File.separator;
            String file1 = "testfile.txt";
            String file2 = "subdir/testfile.txt";
            try {

                // create a single test file and put it in two places
                File file = createTestFile(dsDir, file1, 0.25);
                if (file != null) {
                    FileUtils.copyFile(file, new File(dsDir + file2));
                } else {
                    System.out.println("Unable to copy file: " + dsDir + file2);
                    fail();
                }

                // mock the checksum manifest
                String checksum1 = "asfdasdfasdfasdf";
                String checksum2 = "sgsdgdsgfsdgsdgf";
                if (file1 != null && file2 != null) {
                    PrintWriter pw = new PrintWriter(new FileWriter(dsDir + "/files.sha"));
                    pw.write(checksum1 + " " + file1);
                    pw.write("\n");
                    pw.write(checksum2 + " " + file2);
                    pw.write("\n");
                    pw.close();
                } else {
                    fail();
                }

            } catch (Exception e) {
                System.out.println("Error testSetChecksumValidation: " + e.getMessage());
                e.printStackTrace();
                fail();
            }

            // make user a super user
            given()
                    .post("/api/admin/superuser/" + testName)
                    .then().assertThat().statusCode(200);

            // set checksum validation
            String jobId =
                    given()
                            .header(API_TOKEN_HTTP_HEADER, token)
                            .body("{" +
                                    "    \"userId\": \"" + testName + "\"," +
                                    "    \"datasetId\": \"" + dsId + "\"," +
                                    "    \"datasetIdentifier\": \"" + testName + "\"," +
                                    "    \"status\": \"validation passed\"" +
                                    "}")
                            .contentType(ContentType.JSON).request()
                            .post("api/datasets/dataCaptureModule/checksumValidation")
                            .then().assertThat().statusCode(200)
                            .extract().jsonPath().getString("data.jobId");

            System.out.println("JOB ID: " + jobId);

            // wait for job to complete
            try {
                Thread.sleep(2000);
            } catch (Exception e) {
                System.out.println("Error waiting for job to complete: " + e.getMessage());
            }

            // check job result to make sure 2 files were committed
            given()
                    .header(API_TOKEN_HTTP_HEADER, token)
                    .get("/api/batch/job/" + jobId)
                    .then().assertThat().statusCode(200)
                    .body("steps[0].metrics.write_count", equalTo(2));

            // check dataset json for the 2 new files
            JsonPath dsPath = given()
                    .header(API_TOKEN_HTTP_HEADER, token)
                    .contentType(ContentType.JSON)
                    .get("/api/datasets/:persistentId?persistentId=" + dsGlobalId)
                    .then().assertThat().statusCode(200)
                    .extract().jsonPath();

            List<String> storageIds = new ArrayList<>();
            storageIds.add(dsPath.getString("data.latestVersion.files[0].dataFile.storageIdentifier"));
            storageIds.add(dsPath.getString("data.latestVersion.files[1].dataFile.storageIdentifier"));
            assert (storageIds.contains(file1));
            assert (storageIds.contains(file2));

            // delete test files
            try {
                FileUtils.deleteDirectory(new File(dsDir));
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        } else {
            System.out.println("SKIPPING TEST: "
                    + "testSetChecksumValidation is unavailable since batch job module isn't present.");
        }
    }


    // UTILS

    /***
     * Create a test file with a size in GB
     *
     * @param dir where to save the file (directory will be created if it doesn't exist)
     * @param name the test file name
     * @param size the desired size in GB
     * @return the file
     */
    static File createTestFile(String dir, String name, double size) {

        try {
            File myDir = new File(dir);
            Random random = new Random();
            boolean isDirCreated = myDir.exists();
            if (!isDirCreated) {
                isDirCreated = myDir.mkdirs();
            }
            if (isDirCreated) {
                File file = new File(dir + File.separator + name);
                long start = System.currentTimeMillis();
                PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(file), "UTF-8")), false);
                int counter = 0;
                while (true) {
                    String sep = "";
                    for (int i = 0; i < 100; i++) {
                        int number = random.nextInt(1000) + 1;
                        writer.print(sep);
                        writer.print(number / 1e3);
                        sep = " ";
                    }
                    writer.println();
                    if (++counter == 20000) {
                        //System.out.printf("Size: %.3f GB%n", file.length() / 1e9);
                        if (file.length() >= size * 1e9) {
                            writer.close();
                            break;
                        } else {
                            counter = 0;
                        }
                    }
                }
                long time = System.currentTimeMillis() - start;
                //System.out.printf("Took %.1f seconds to create a file of %.3f GB", time / 1e3, file.length() / 1e9);
                return file;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return null;
        }
        return null;
    }


}
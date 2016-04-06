package edu.harvard.iq.dataverse.batch.launcher;


import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;
import edu.harvard.iq.dataverse.api.UtilIT;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.*;
import org.junit.runner.RunWith;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.with;
import static junit.framework.Assert.assertEquals;

/**
 * Created by bmckinney on 3/30/16.
 */
@RunWith(Arquillian.class)
public class ImportLauncherIT {

    private static final Logger logger = Logger.getLogger(ImportLauncherIT.class.getCanonicalName());

    private static final String PRIVILEGED_USER = "src/test/resources/json/user-privileged.json";
    private static final String GUEST_USER = "src/test/resources/json/user-guest.json";
    private static final String TEST_DATAVERSE = "src/test/resources/json/complete-dataverse.json";
    private static final String TEST_DATASET = "src/test/resources/json/test-dataset.json";

    private static final String BUILT_IN_USER_KEY = "burrito";
    private static final String USERNAME_KEY = "userName";
    private static final String API_TOKEN_HTTP_HEADER = "X-Dataverse-key";

    private String dsid;
    private String dvAlias;
    private String privilegedUserToken;
    private String privilegedUserName;
    private String guestUserName;

    @Deployment
    public static WebArchive createDeployment() {

        WebArchive war = ShrinkWrap.create(ZipImporter.class, "test-dataverse.war")
                .importFrom(new File("target/dataverse-4.2.4.war"))
                .as(WebArchive.class);
        war.delete("WEB-INF/classes/META-INF/persistence.xml");
        war.addAsResource(new File("src/test/resources/glassfish/test-persistence.xml"), "META-INF/persistence.xml");
        return war;

    }

    @Before
    public void before() throws Exception{

        Response response;

        // BuiltinUsers.KEY
        response = given().body(BUILT_IN_USER_KEY).put("api/admin/settings/:BuiltinUsers.KEY");
        response.prettyPrint();

        // SUPER USER
        response = given()
                .body(new String(Files.readAllBytes(Paths.get(PRIVILEGED_USER))))
                .contentType(ContentType.JSON)
                .post("/api/builtin-users?key=" + BUILT_IN_USER_KEY);
        response.prettyPrint();
        privilegedUserName = JsonPath.from(response.asString()).getString("data.user.userName");
        privilegedUserToken = JsonPath.from(response.asString()).getString("data.apiToken");
        Response superUser = given().post("/api/admin/superuser/pete");
        superUser.prettyPrint();

        // DATAVERSE
        response = given()
                .body(Files.readAllBytes(Paths.get(TEST_DATAVERSE)))
                .contentType(ContentType.JSON)
                .when().post("/api/dataverses/:root?key=" + privilegedUserToken);
        response.prettyPrint();
        String dataverseId = JsonPath.from(response.asString()).getString("data.id");
        dvAlias = JsonPath.from(response.asString()).getString("data.alias");

        // DATASET
        response = given()
                .header(API_TOKEN_HTTP_HEADER, privilegedUserToken)
                .body(Files.readAllBytes(Paths.get(TEST_DATASET)))
                .contentType("application/json")
                .post("/api/dataverses/" + dvAlias + "/datasets");
        response.prettyPrint();
        dsid = JsonPath.from(response.asString()).getString("data.id");

        // GUEST USER
        response = given()
                .body(new String(Files.readAllBytes(Paths.get(GUEST_USER))))
                .contentType(ContentType.JSON)
                .post("/api/builtin-users?key=" + BUILT_IN_USER_KEY);
        response.prettyPrint();
        guestUserName = JsonPath.from(response.asString()).getString("data.user.userName");

    }

    @After
    public void after() {

        Response resp;

        resp =  given().delete("/api/datasets/" + dsid + "?key=" + privilegedUserToken );
        resp.prettyPrint();

        resp = given().delete("/api/dataverses/" + dvAlias + "?key=" + privilegedUserToken);
        resp.prettyPrint();

        resp = given().delete("/api/admin/authenticatedUsers/" + privilegedUserName + "/");
        resp.prettyPrint();

        resp = given().delete("/api/admin/authenticatedUsers/" + guestUserName + "/");
        resp.prettyPrint();

    }

    @RunAsClient
    @Test
    public void should_return_execution_id() {
        ImportLauncher importLauncher = new ImportLauncher();
        //javax.ws.rs.core.Response response = importLauncher.getFilesystemImport("doi:10.5072/FK2/NSMTIE","12345678");
        //System.out.println(response.getStatus());
        //System.out.println("Response: " + response.toString());

        //Assert.fail("Not yet implemented.");
    }


}

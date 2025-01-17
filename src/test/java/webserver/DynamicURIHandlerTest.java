package webserver;

import manager.UserManager;
import util.Utils;
import db.Database;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class DynamicURIHandlerTest {

    private DynamicURIHandler dynamicUriHandler;

    @BeforeEach
    void setUp() {
        dynamicUriHandler = new DynamicURIHandler();
    }

    /*
    @Test
    public void testHandleDynamicRequest() {
        String userId = "id";
        String nickname = "name";
        String password = "pw";
        String email = "email@email.com";

        String httpMethod = "GET";
        String query = String.format("/create?userId=%s&nickname=%s&password=%s&email=%s",
                userId,
                nickname,
                password,
                email);
        DataOutputStream dos = new DataOutputStream(System.out);

        UserManager userManager = new UserManager();
        //uriHandler.handleDynamicRequest(httpMethod, query, dos);


        assertThat(Database.findUserById(userId))
                .isNotNull()
                .extracting(User::getUserId, User::getName, User::getPassword, User::getEmail)
                .containsExactly(userId, nickname, password, email);

    }

    @Test
    public void testHandleStaticRequest_FileExists() {
        String query = "/test-file.html";
        File file = new File("src/main/resources/static/test-file.html");
        file.getParentFile().mkdirs();
        try (ByteArrayOutputStream ex = new ByteArrayOutputStream();
             DataOutputStream dos = new DataOutputStream(ex)) {
            file.createNewFile();

            DynamicURIHandler dynamicUriHandler = new DynamicURIHandler();
            dynamicUriHandler.handleStaticRequest(query, dos);

            String response = ex.toString();
            assertThat(response).contains("HTTP/1.1 200 OK");
        } catch (Exception e) {
            fail("Exception occurred during test: " + e.getMessage());
        } finally {
            file.delete();
        }
    }

    @Test
    public void testHandleStaticRequest_FileNotFound() {
        String query = "/non-existent-file.html";
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             DataOutputStream dos = new DataOutputStream(baos)) {

            DynamicURIHandler dynamicUriHandler = new DynamicURIHandler();
            dynamicUriHandler.handleStaticRequest(query, dos);

            String response = baos.toString();
            assertThat(response).contains("HTTP/1.1 404 Not Found");
        } catch (Exception e) {
            fail("Exception occurred during test: " + e.getMessage());
        }
    }

    @Test
    public void testHandleStaticRequest_DirectoryWithIndex() {
        String query = "/test-directory";
        File dir = new File("src/main/resources/static/test-directory");
        File indexFile = new File(dir, "index.html");
        dir.mkdirs();
        try (ByteArrayOutputStream ex = new ByteArrayOutputStream();
             DataOutputStream dos = new DataOutputStream(ex)) {
            indexFile.createNewFile();

            DynamicURIHandler dynamicUriHandler = new DynamicURIHandler();
            dynamicUriHandler.handleStaticRequest(query, dos);

            String response = ex.toString();
            assertThat(response).contains("HTTP/1.1 302 Found");
            assertThat(response).contains("Location: /test-directory/index.html");
        } catch (Exception e) {
            fail("Exception occurred during test: " + e.getMessage());
        } finally {
            indexFile.delete();
            dir.delete();
        }
    }

    @Test
    public void testHandleStaticRequest_DirectoryWithoutIndex() {
        String query = "/test-directory";
        File dir = new File("src/main/resources/static/test-directory");
        dir.mkdirs();
        try (ByteArrayOutputStream ex = new ByteArrayOutputStream();
             DataOutputStream dos = new DataOutputStream(ex)) {

            DynamicURIHandler dynamicUriHandler = new DynamicURIHandler();
            dynamicUriHandler.handleStaticRequest(query, dos);

            String response = ex.toString();
            assertThat(response).contains("HTTP/1.1 404 Not Found");
        } catch (Exception e) {
            fail("Exception occurred during test: " + e.getMessage());
        } finally {
            dir.delete();
        }
    }

    @Test
    public void testHandleStaticRequest_DirectoryRequests() {
        String query = Utils.removeLastSlash("/test1/");
        String expectedLocation = "/test1/index.html";

        File rootIndexFile = new File("src/main/resources/static/test1/index.html");
        rootIndexFile.getParentFile().mkdirs();

        try (ByteArrayOutputStream ex = new ByteArrayOutputStream();
             DataOutputStream dos = new DataOutputStream(ex)) {

            rootIndexFile.createNewFile();

            DynamicURIHandler dynamicUriHandler = new DynamicURIHandler();

            ex.reset();
            dynamicUriHandler.handleStaticRequest(query, dos);

            String response = ex.toString();
            assertThat(response).contains("HTTP/1.1 302 Found");
            assertThat(response).contains("Location: " + expectedLocation);

        } catch (Exception e) {
            fail("Exception occurred during test: " + e.getMessage());
        } finally {
            rootIndexFile.delete();
        }
    }

    @Test
    public void testHandleStaticRequest_NoRootRequest() {
        String query = "";
        File indexFile = new File("src/main/resources/static/index.html");
        indexFile.getParentFile().mkdirs();
        try (ByteArrayOutputStream ex = new ByteArrayOutputStream();
             DataOutputStream dos = new DataOutputStream(ex)) {
            indexFile.createNewFile();

            DynamicURIHandler dynamicUriHandler = new DynamicURIHandler();
            dynamicUriHandler.handleStaticRequest(query, dos);

            String response = ex.toString();
            assertThat(response).contains("HTTP/1.1 302 Found");
            assertThat(response).contains("Location: /index.html");
        } catch (Exception e) {
            fail("Exception occurred during test: " + e.getMessage());
        } finally {
            //indexFile.delete();
        }
    }

    */
}
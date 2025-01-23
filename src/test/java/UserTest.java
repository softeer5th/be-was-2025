import db.Database;
import db.connection.TestConnectionProvider;
import entrypoint.UserEntryPoint;
import model.User;
import org.junit.jupiter.api.*;
import webserver.exception.HTTPException;

import java.util.Objects;

public class UserTest {
    private final UserEntryPoint userEntryPoint;
    private final TestConnectionProvider conn;
    private final Database db;

    public UserTest() {
        this.conn = new TestConnectionProvider("test");
        this.db = new Database(conn);
        this.db.initTable();
        this.userEntryPoint = new UserEntryPoint(db);
    }

    @BeforeEach
    public void setUp() {
        this.db.initTable();
    }
    @AfterEach
    public void close() {
        this.conn.cleanUp();
    }
    @Test
    @DisplayName("중복된 ID")
    public void duplicatedId() {
        db.addUser(new User("1", "2", "3", "4"));
        Assertions.assertThrows(HTTPException.class, () -> {
            userEntryPoint.signUp("1", "5", "6");
        });
    }

    @Test
    @DisplayName("성공")
    public void signUp() {
        userEntryPoint.signUp("2", "5", "6");
        User actual = this.db.findAll().stream().
                filter(user -> Objects.equals(user.getUserId(), "2"))
                .findFirst().orElse(null);
        Assertions.assertNotNull(actual);
        Assertions.assertEquals("2", actual.getUserId());
    }
}

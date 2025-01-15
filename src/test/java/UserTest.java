import db.Database;
import entrypoint.UserEntryPoint;
import model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import webserver.exception.HTTPException;

public class UserTest {
    private static UserEntryPoint userEntryPoint;

    public UserTest() {
        this.userEntryPoint = new UserEntryPoint();
    }

    @Test
    @DisplayName("중복된 ID")
    public void duplicatedId() {
        Database.addUser(new User("1", "2", "3", "4"));
        Assertions.assertThrows(HTTPException.class, () -> {
            userEntryPoint.signUp("1", "5", "6");
        });
    }

    @Test
    @DisplayName("성공")
    public void signUp() {
        userEntryPoint.signUp("2", "5", "6");
        User actual = Database.findAll().stream().
                filter(user -> user.getUserId() == "2")
                .findFirst().orElse(null);
        Assertions.assertNotNull(actual);
        Assertions.assertEquals("2", actual.getUserId());
    }
}

package db;

import model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;


class DatabaseTest {

    @Test
    @DisplayName("ID로 사용자 조회")
    void findUserById() {
        // given
        User user1 = new User("u1", "1234", "user1", "u1@gmail.com");
        Database.addUser(user1);

        // when
        User foundUser = Database.findUserById("u1");

        // then
        assertThat(user1).isEqualTo(foundUser);
    }

    @Test
    @DisplayName("현재 저장된 사용자 조회")
    void findAll() {
        // given
        User user1 = new User("u1", "1234", "user1", "u1@gmail.com");
        User user2 = new User("u2", "4321", "user2", "u2@gmail.com");
        Database.addUser(user1);
        Database.addUser(user2);

        // when
        Collection<User> users = Database.findAll();

        // then
        assertThat(users).contains(user1, user2);
    }
}
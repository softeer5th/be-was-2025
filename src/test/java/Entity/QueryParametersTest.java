package Entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

class QueryParametersTest {
    private QueryParameters queryParameters;

    @BeforeEach
    void setUp() {
        this.queryParameters = new QueryParameters("email=u1@gmail.com&userId=u1&name=nick1&password=1234");
    }

    @Test
    void get() {
        assertThat(queryParameters.get("email").equals("u1@gmail.com")).isTrue();
        assertThat(queryParameters.get("userId").equals("u1")).isTrue();
        assertThat(queryParameters.get("name").equals("nick1")).isTrue();
        assertThat(queryParameters.get("password").equals("1234")).isTrue();

        assertThat(queryParameters.get("email").equals("u2@gmail.com")).isFalse();
        assertThat(queryParameters.get("userId").equals("u2")).isFalse();
        assertThat(queryParameters.get("name").equals("nick2")).isFalse();
        assertThat(queryParameters.get("password").equals("4321")).isFalse();
    }

    @Test
    void getKeySet() {
        Set<String> set = queryParameters.getKeySet();
        assertThat(set.contains("userId")).isTrue();
        assertThat(set.contains("password")).isTrue();
        assertThat(set.contains("email")).isTrue();
        assertThat(set.contains("name")).isTrue();

        assertThat(set.contains("userid")).isFalse();
        assertThat(set.contains("test")).isFalse();
    }
}
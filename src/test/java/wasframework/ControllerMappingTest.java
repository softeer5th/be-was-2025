package wasframework;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import webserver.httpserver.HttpMethod;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.*;


public class ControllerMappingTest {

    @Test
    @DisplayName("존재하지 않는 경로 테스트")
    public void testGetControllerMethod_notFound() {
        // given
        SampleController sc = new SampleController();
        List<Object> controllers = List.of(sc);
        ControllerMapping mapping = new ControllerMapping(controllers);

        // when
        ControllerMethod cm = mapping.getControllerMethod("/unknown", HttpMethod.GET);

        // then
        assertThat(cm).isNull();
    }

    @ParameterizedTest
    @EnumSource(value = HttpMethod.class, names = {"GET", "POST"})
    @DisplayName("지원되는 HTTP 메소드 테스트")
    public void testHttpMethodMatch(HttpMethod httpMethod) {
        // given
        SampleController sc = new SampleController();
        List<Object> controllers = List.of(sc);
        ControllerMapping mapping = new ControllerMapping(controllers);

        // when
        ControllerMethod cm = mapping.getControllerMethod("/hello", httpMethod);

        // then
        assertThat(cm).isNotNull();
        assertThat(sc).isEqualTo(cm.controller());
    }

    @ParameterizedTest
    @EnumSource(value = HttpMethod.class, names = {"PUT", "PATCH", "DELETE"})
    @DisplayName("지원하지 않는 HTTP 메소드 테스트")
    public void testHttpMethodMismatch(HttpMethod httpMethod) {
        // given
        SampleController sc = new SampleController();
        List<Object> controllers = List.of(sc);
        ControllerMapping mapping = new ControllerMapping(controllers);

        // when
        ControllerMethod cm = mapping.getControllerMethod("/hello", httpMethod);

        // then
        assertThat(cm).isNull();
    }

    @Test
    @DisplayName("경로 변수 포함 URL 매칭 테스트")
    public void testPathVariableMatching() {
        // given
        VarController vc = new VarController();
        List<Object> controllers = List.of(vc);
        ControllerMapping mapping = new ControllerMapping(controllers);

        // when
        ControllerMethod cm = mapping.getControllerMethod("/users/123", HttpMethod.GET);

        // then
        assertThat(cm).isNotNull();
        assertThat(cm.method().getName()).isEqualTo("getUser");
    }

    @Test
    @DisplayName("중복 매핑 시 IllegalArgumentException 발생 여부 확인")
    public void testDuplicateMappingValidation() {
        // given
        DuplicateController1 dc1 = new DuplicateController1();
        DuplicateController2 dc2 = new DuplicateController2();
        List<Object> controllers = List.of(dc1, dc2);

        // when, then
        assertThatThrownBy(() -> new ControllerMapping(controllers))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Controller method already exists");
    }

    private class SampleController {
        @Mapping(path = "/hello", method = HttpMethod.GET)
        public void hello() {}

        @Mapping(path = "/hello", method = HttpMethod.POST)
        public void helloPost() {}
    }
    private class VarController {
        @Mapping(path = "/users/{id}", method = HttpMethod.GET)
        public void getUser() {}
    }

    private class DuplicateController1 {
        @Mapping(path = "/duplicate", method = HttpMethod.GET)
        public void method1() {}
    }

    private class DuplicateController2 {
        @Mapping(path = "/duplicate", method = HttpMethod.GET)
        public void method2() {}
    }
}

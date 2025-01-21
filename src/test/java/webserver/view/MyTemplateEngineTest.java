package webserver.view;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import webserver.view.renderer.ForeachTagRenderer;
import webserver.view.renderer.IfTagRenderer;
import webserver.view.renderer.IncludeTagRenderer;
import webserver.view.renderer.TextTagRenderer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MyTemplateEngineTest {

    @Test
    @DisplayName("my-foreach 태그 렌더링")
    void test1() {
        // given
        TemplateEngine templateEngine = new MyTemplateEngine()
                .registerTagHandler(new ForeachTagRenderer("my-foreach"))
                .registerTagHandler(new TextTagRenderer("my-text"));
        String template = """
                <my-foreach items="users" item="user">
                    <div><my-text>${user.name}</my-text></div>
                </my-foreach>
                """;
        Map<String, Object> model = new HashMap<>();
        model.putAll(Map.of(
                        "users", List.of(
                                Map.of("name", "alice"),
                                Map.of("name", "bob")
                        )
                )
        );

        // when
        String actual = templateEngine.render(template, model);
        // then
        assertThat(actual).contains("<div>alice</div><div>bob</div>");
    }

    @ParameterizedTest
    @CsvSource({
            "'true', true",
            "'false', false",
            "'true&&true', true",
            "'true&&false', false",
            "'false&&false', false",
            "'true||true', true",
            "'true||false', true",
            "'false||false', false",
            "'true&&true||false', true",
            "'true&&false||false', false",
    })
    @DisplayName("my-if 태그 렌더링 - boolean 값")
    void test2(String condition, boolean contains) {
        // given
        TemplateEngine templateEngine = new MyTemplateEngine()
                .registerTagHandler(new IfTagRenderer("my-if"));
        String template = """
                <my-if condition="%s">
                    hello
                </my-if>
                """.formatted(condition);
        Map<String, Object> model = new HashMap<>();
        // when
        String actual = templateEngine.render(template, model);
        // then
        if (contains)
            assertThat(actual).contains("hello");
        else
            assertThat(actual).doesNotContain("hello");
    }


    @Test
    @DisplayName("my-if 태그 렌더링 - not")
    void test3() {
        // given
        TemplateEngine templateEngine = new MyTemplateEngine()
                .registerTagHandler(new IfTagRenderer("my-if"));
        String template = """
                <my-if condition="!true">
                    hello
                </my-if>
                """;
        Map<String, Object> model = new HashMap<>();
        // when
        String actual = templateEngine.render(template, model);
        // then
        assertThat(actual).doesNotContain("hello");
    }

    @Test
    @DisplayName("my-if 태그 렌더링 - nested object field")
    void test4() {
        // given
        TemplateEngine templateEngine = new MyTemplateEngine()
                .registerTagHandler(new IfTagRenderer("my-if"));
        String template = """
                <my-if condition="session.user.name">
                    hello
                </my-if>
                """;
        Map<String, Object> model = new HashMap<>();
        model.put("session", Map.of("user", Map.of("name", "alice")));
        // when
        String actual = templateEngine.render(template, model);
        // then
        assertThat(actual).contains("hello");
    }

    @Test
    @DisplayName("my-if 안에서 my-text 사용")
    void test5() {
        // given
        TemplateEngine templateEngine = new MyTemplateEngine()
                .registerTagHandler(new IfTagRenderer("my-if"))
                .registerTagHandler(new TextTagRenderer("my-text"));
        String template = """
                <my-if condition="session.user">
                   <li class="header__menu__item">
                       <my-text><a class="btn btn_contained btn_ghost" href="/users/${session.user.userId}">${session.user.name}</a>
                       </my-text>
                   </li>
                   <li class="header__menu__item">
                       <a class="btn btn_contained btn_size_s" href="/login">로그인</a>
                   </li>
                   <li class="header__menu__item">
                       <a class="btn btn_ghost btn_size_s" href="/registration">
                           회원 가입
                       </a>
                   </li>
                </my-if>
                """;
        Map<String, Object> model = new HashMap<>();
        model.put("session", Map.of("user", Map.of("name", "alice", "userId", "id1")));
        // when
        String actual = templateEngine.render(template, model);
        // then
        assertThat(actual).contains("""
                <a class="btn btn_contained btn_ghost" href="/users/id1">alice</a>
                """);
    }

    @Test
    @DisplayName("my-include, my-if, my-text 태그 렌더링")
    void test6() {
        // given
        TemplateFileReader fileReader = mock(TemplateFileReader.class);
        when(fileReader.read("header")).thenReturn("<header><my-text>${session.user.name}</my-text></header>");
        when(fileReader.read("body")).thenReturn("<body><my-text>${session.user.userId}</my-text></body>");

        TemplateEngine templateEngine = new MyTemplateEngine()
                .registerTagHandler(new IncludeTagRenderer("my-include", fileReader))
                .registerTagHandler(new TextTagRenderer("my-text"));

        String template = """
                <html>
                <my-include template="header"></my-include>
                <div>hello</div>
                <my-include template="body"></my-include>
                </html>
                """;

        Map<String, Object> model = new HashMap<>();
        model.put("session", Map.of("user", Map.of("name", "alice", "userId", "id1")));

        // when
        String actual = templateEngine.render(template, model);

        // then
        assertThat(actual).contains("<header>alice</header>");
        assertThat(actual).contains("<body>id1</body>");
        assertThat(actual).doesNotContain("<my-include template=\"header\"></my-include>");
        assertThat(actual).doesNotContain("<my-include template=\"body\"></my-include>");
    }

    @Test
    @DisplayName("my-foreach 태그 items 속성에 객체 탐색 경로 사용")
    void test7() {
        // given
        TemplateEngine templateEngine = new MyTemplateEngine()
                .registerTagHandler(new ForeachTagRenderer("my-foreach"))
                .registerTagHandler(new TextTagRenderer("my-text"));
        String template = """
                <my-foreach items="session.users" item="user">
                    <div><my-text>${user.name}</my-text></div>
                </my-foreach>
                """;
        Map<String, Object> model = new HashMap<>();
        model.put("session", Map.of(
                "users", List.of(
                        Map.of("name", "alice"),
                        Map.of("name", "bob")
                )
        ));

        // when
        String actual = templateEngine.render(template, model);

        // then
        assertThat(actual).contains("<div>alice</div><div>bob</div>");
    }
}
package webserver.router;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import webserver.handler.HttpHandler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

class PathRouterTest {
    @Test
    @DisplayName("default 핸들러 매핑")
    void test1() {
        // given
        var handler1 = mock(HttpHandler.class);
        var handler2 = mock(HttpHandler.class);
        var mapping = new PathRouter();

        // when
        mapping
                .setHandler("/a/b", handler1)
                .setDefaultHandler(handler2);

        //then
        assertThat(mapping.route("/c").handler()).isEqualTo(handler2);
    }

    @Test
    @DisplayName("핸들러 매핑")
    void test2() {
        // given
        var handler1 = mock(HttpHandler.class);
        var handler2 = mock(HttpHandler.class);
        var mapping = new PathRouter();

        // when
        mapping
                .setHandler("/a/b", handler1)
                .setHandler("/c/d", handler2);

        //then
        assertThat(mapping.route("/c/d").handler()).isEqualTo(handler2);
    }

    @Test
    @DisplayName("path variable 핸들러 매핑")
    void test3() {
        // given
        var handler1 = mock(HttpHandler.class);
        var handler2 = mock(HttpHandler.class);
        var handler3 = mock(HttpHandler.class);
        var mapping = new PathRouter();

        // when
        mapping
                .setHandler("/users/{id}", handler1)
                .setHandler("/users/{id}/post", handler2)
                .setHandler("/users/{id}/post/{postId}/comments/{commentId}", handler3);
        //then
        var result = mapping.route("/users/abcd");
        assertThat(result.handler()).isEqualTo(handler1);
        assertThat(result.pathVariables())
                .containsEntry("id", "abcd");

        result = mapping.route("/users/defg/post");
        assertThat(result.handler()).isEqualTo(handler2);
        assertThat(result.pathVariables())
                .containsEntry("id", "defg");


        result = mapping.route("/users/user1/post/post2/comments/comment3");
        assertThat(result.handler()).isEqualTo(handler3);
        assertThat(result.pathVariables())
                .containsEntry("id", "user1")
                .containsEntry("postId", "post2")
                .containsEntry("commentId", "comment3");
    }

    @Test
    @DisplayName("같은 위치의 segment에 다른 이름의 path variable 등록")
    void test4() {
        // given
        var handler1 = mock(HttpHandler.class);
        var handler2 = mock(HttpHandler.class);
        var mapping = new PathRouter();

        // when
        //then

        assertThatThrownBy(() -> {
            mapping
                    .setHandler("/users/{id}", handler1)
                    .setHandler("/users/{name}", handler2);
        })
                .isInstanceOf(Exception.class);

    }

    @Test
    @DisplayName("정적인 경로를 Path Variable 기반 동적 경로보다 우선순위로 처리")
    void test5() {
        // given
        var handler1 = mock(HttpHandler.class);
        var handler2 = mock(HttpHandler.class);
        var mapping = new PathRouter();

        // when
        mapping
                .setHandler("/users/{id}", handler1)
                .setHandler("/users/admin", handler2);
        //then
        var result = mapping.route("/users/admin");
        assertThat(result.handler()).isEqualTo(handler2);
        assertThat(result.pathVariables())
                .hasSize(0);


    }
}
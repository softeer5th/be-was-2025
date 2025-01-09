package webserver.handler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class HttpHandlerMappingTest {
    @Test
    @DisplayName("default 핸들러 매핑")
    void test1() {
        // given
        var handler1 = mock(HttpHandler.class);
        var handler2 = mock(HttpHandler.class);


        // when
        var mapping = new HttpHandlerMapping()
                .setHandler("/a/b", handler1)
                .setDefaultHandler(handler2);

        //then
        assertThat(mapping.getHandler("/c")).isEqualTo(handler2);
    }

    @Test
    @DisplayName("핸들러 매핑")
    void test2() {
        // given
        var handler1 = mock(HttpHandler.class);
        var handler2 = mock(HttpHandler.class);

        // when
        var mapping = new HttpHandlerMapping()
                .setHandler("/a/b", handler1)
                .setHandler("/c/d", handler2);

        //then
        assertThat(mapping.getHandler("/c/d")).isEqualTo(handler2);
    }

    @Test
    @DisplayName("자세한 경로의 핸들러 매핑")
    void test3() {
        // given
        var handler1 = mock(HttpHandler.class);
        var handler2 = mock(HttpHandler.class);
        var handler3 = mock(HttpHandler.class);

        // when
        var mapping = new HttpHandlerMapping()
                .setHandler("/a", handler1)
                .setHandler("/a/b/c", handler2)
                .setHandler("/a/b/c/d/e", handler3);

        //then
        assertThat(mapping.getHandler("/a/b/c/d")).isEqualTo(handler2);
        assertThat(mapping.getHandler("/a/b/c/d/e/f")).isEqualTo(handler3);
    }
}
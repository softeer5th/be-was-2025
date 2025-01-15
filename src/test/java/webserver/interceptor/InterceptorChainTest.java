package webserver.interceptor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import webserver.handler.HttpHandler;
import webserver.request.HttpRequest;
import webserver.response.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class InterceptorChainTest {
    HandlerInterceptor interceptor1, interceptor2, interceptor3, interceptor4;
    HttpRequest request;
    HttpResponse response;
    HttpHandler handler;

    @BeforeEach
    void setUp() {
        interceptor1 = mock(HandlerInterceptor.class);
        interceptor2 = mock(HandlerInterceptor.class);
        interceptor3 = mock(HandlerInterceptor.class);
        interceptor4 = mock(HandlerInterceptor.class);
        request = mock(HttpRequest.class);
        response = mock(HttpResponse.class);
        handler = mock(HttpHandler.class);
    }

    @Test
    @DisplayName("InterceptorChain 생성 및 실행 순서 검증")
    void interceptorChainExecutionOrder() {
        // given
        var chain = InterceptorChain
                .inbound()
                .add(interceptor1)
                .add(interceptor2)
                .outbound()
                .add(interceptor3)
                .add(interceptor4)
                .build();

        // when
        chain.execute(any(), handler);

        // then
        InOrder inOrder = inOrder(interceptor1, interceptor2, handler, interceptor3, interceptor4);
        inOrder.verify(interceptor1).preHandle(any());
        inOrder.verify(interceptor2).preHandle(any());
        inOrder.verify(handler).handle(any());
        inOrder.verify(interceptor3).postHandle(any(), any(), any());
        inOrder.verify(interceptor4).postHandle(any(), any(), any());
    }

    @Test
    @DisplayName("Interceptor 별로 preHandle에서 리턴한 Context가 postHandle로 들어오는지 검증")
    void interceptorChainContextPassing() {
        // given
        var chain = InterceptorChain
                .inbound()
                .add(interceptor1)
                .add(interceptor2)
                .outbound()
                .add(interceptor2)
                .add(interceptor1)
                .build();

        var context1 = new HandlerInterceptor.Context("1");
        var context2 = new HandlerInterceptor.Context("2");

        when(interceptor1.preHandle(any())).thenReturn(context1);
        when(interceptor2.preHandle(any())).thenReturn(context2);

        // when
        chain.execute(request, handler);

        // then
        verify(interceptor2).postHandle(any(), any(), refEq(context2));
        verify(interceptor1).postHandle(any(), any(), refEq(context1));
    }

    @Test
    @DisplayName("앞선 postHandle의 결과를 다음 postHandle이 인자로 받는지 검증")
    void interceptorChainPostHandleResultPassing() {
        // given
        var chain = InterceptorChain
                .inbound()
                .outbound()
                .add(interceptor3)
                .add(interceptor4)
                .build();

        var response1 = mock(HttpResponse.class);
        var response2 = mock(HttpResponse.class);
        var response3 = mock(HttpResponse.class);

        when(handler.handle(any())).thenReturn(response1);
        when(interceptor3.postHandle(any(), any(), any())).thenReturn(response2);
        when(interceptor4.postHandle(any(), any(), any())).thenReturn(response3);

        // when
        var response = chain.execute(request, handler);

        // then
        InOrder inOrder = inOrder(interceptor3, interceptor4);
        inOrder.verify(interceptor3).postHandle(any(), eq(response1), any());
        inOrder.verify(interceptor4).postHandle(any(), eq(response2), any());
        assertThat(response).isSameAs(response3);
    }
}
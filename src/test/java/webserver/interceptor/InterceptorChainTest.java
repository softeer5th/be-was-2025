package webserver.interceptor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
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
        chain.execute(request, handler);

        // then
        InOrder inOrder = inOrder(interceptor1, interceptor2, handler, interceptor3, interceptor4);
        inOrder.verify(interceptor1).preHandle(any(), any());
        inOrder.verify(interceptor2).preHandle(any(), any());
        inOrder.verify(handler).handle(any());
        inOrder.verify(interceptor3).postHandle(any(), any(), any());
        inOrder.verify(interceptor4).postHandle(any(), any(), any());
    }

    @Test
    @DisplayName("앞선 preHandle의 결과를 다음 preHandle이 인자로 받는지 검증")
    void interceptorChainPreHandleResultPassing() {
        // given
        var chain = InterceptorChain
                .inbound()
                .add(interceptor1)
                .add(interceptor2)
                .outbound()
                .build();

        var request1 = mock(HttpRequest.class);
        var request2 = mock(HttpRequest.class);
        var request3 = mock(HttpRequest.class);

        when(interceptor1.preHandle(any(), any())).thenReturn(request2);
        when(interceptor2.preHandle(any(), any())).thenReturn(request3);

        // when
        chain.execute(request1, handler);

        // then
        InOrder inOrder = inOrder(interceptor1, interceptor2, handler, interceptor3, interceptor4);
        inOrder.verify(interceptor1).preHandle(eq(request1), any());
        inOrder.verify(interceptor2).preHandle(eq(request2), any());
        inOrder.verify(handler).handle(eq(request3));
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

    @Test
    @DisplayName("InterceptorChain의 모든 기능 검증")
    void interceptorChainFullFunctionality() {
        // given
        var chain = InterceptorChain
                .inbound()
                .add(interceptor1)
                .add(interceptor2)
                .outbound()
                .add(interceptor2)
                .add(interceptor1)
                .build();

        var request1 = mock(HttpRequest.class);
        var request2 = mock(HttpRequest.class);
        var request3 = mock(HttpRequest.class);
        var response1 = mock(HttpResponse.class);
        var response2 = mock(HttpResponse.class);
        var response3 = mock(HttpResponse.class);
        ArgumentCaptor<HandlerInterceptor.Context> contextCaptor1 = ArgumentCaptor.captor();
        ArgumentCaptor<HandlerInterceptor.Context> contextCaptor2 = ArgumentCaptor.captor();

        when(interceptor1.preHandle(eq(request1), any())).thenReturn(request2);
        when(interceptor2.preHandle(eq(request2), any())).thenReturn(request3);
        when(handler.handle(request3)).thenReturn(response1);
        when(interceptor2.postHandle(eq(request3), eq(response1), any())).thenReturn(response2);
        when(interceptor1.postHandle(eq(request3), eq(response2), any())).thenReturn(response3);

        // when
        var result = chain.execute(request1, handler);

        // then
        InOrder inOrder = inOrder(interceptor1, interceptor2, handler, interceptor3, interceptor4);
        inOrder.verify(interceptor1).preHandle(eq(request1), contextCaptor1.capture());
        inOrder.verify(interceptor2).preHandle(eq(request2), contextCaptor2.capture());
        inOrder.verify(handler).handle(request3);
        inOrder.verify(interceptor2).postHandle(request3, response1, contextCaptor2.getValue());
        inOrder.verify(interceptor1).postHandle(request3, response2, contextCaptor1.getValue());
        assertThat(result).isSameAs(response3);
    }
}
package webserver.interceptor;

import webserver.handler.HttpHandler;
import webserver.request.HttpRequest;
import webserver.response.HttpResponse;

import java.util.*;

/**
 * 인터셉터의 실행 순서를 관리하는 클래스.<br>
 * 생성법<br>
 * <pre>
 * var chain = InterceptorChain
 *      .inbound()
 *          .add(interceptor1)
 *          .add(interceptor2)
 *      .outbound()
 *          .add(interceptor3)
 *          .add(interceptor4)
 *      .build();
 *
 * chain.execute(request, handler) 호출 시 실행 순서
 *      request----------------┐
 * 1. ┌-interceptor1.preHandle-┘
 * 2. └-interceptor2.preHandle-┐
 * 3. ┌----handler.handle------┘
 * 4. └-interceptor3.postHandle-┐
 * 5. ┌-interceptor4.postHandle-┘
 *    └----------------response
 * </pre>
 */
public class InterceptorChain {
    private final List<HandlerInterceptor> inboundOrder;
    private final List<HandlerInterceptor> outboundOrder;

    private InterceptorChain(List<HandlerInterceptor> inboundOrder, List<HandlerInterceptor> outboundOrder) {
        // 동시성 문제를 피하기 위해 불변으로 저장
        this.inboundOrder = Collections.unmodifiableList(inboundOrder);
        this.outboundOrder = Collections.unmodifiableList(outboundOrder);
    }

    public static InboundBuilder inbound() {
        return new InboundBuilder();
    }

    /**
     * 인터셉터 체인을 실행한다.
     *
     * @param request 처음 인터셉터의 preHandle 을 거칠 요청
     * @param handler HTTP 요청을 처리할 핸들러
     * @return 마지막 인터셉터의 postHandle 을 거친 응답
     */
    public HttpResponse execute(HttpRequest request, HttpHandler handler) {
        // interceptor 가 handler 실행 전후로 공유하는 context
        Map<HandlerInterceptor, HandlerInterceptor.Context> contextMap = new HashMap<>();

        // inbound order 대로 preHandle 실행
        for (HandlerInterceptor inboundInterceptor : inboundOrder) {
            HandlerInterceptor.Context context = new HandlerInterceptor.Context();
            request = inboundInterceptor.preHandle(request, context);
            // preHandle 실행 후 context를 contextMap에 저장
            contextMap.put(inboundInterceptor, context);
        }

        // handler 실행
        HttpResponse response = handler.handle(request);

        // outbound order 대로 postHandle 실행
        for (HandlerInterceptor outboundInterceptor : outboundOrder) {
            // preHandle에서 저장한 context를 가져와 postHandle 실행
            HandlerInterceptor.Context context = contextMap.get(outboundInterceptor);
            response = outboundInterceptor.postHandle(request, response, context);
        }
        return response;
    }


    public static class InboundBuilder {
        private final List<HandlerInterceptor> inboundOrder = new ArrayList<>();

        InboundBuilder() {
        }

        public OutboundBuilder outbound() {
            return new OutboundBuilder(inboundOrder);
        }

        public InboundBuilder add(HandlerInterceptor interceptor) {
            inboundOrder.add(interceptor);
            return this;
        }
    }


    public static class OutboundBuilder {
        private final List<HandlerInterceptor> inboundOrder;
        private final List<HandlerInterceptor> outboundOrder = new ArrayList<>();

        OutboundBuilder(List<HandlerInterceptor> inboundOrder) {
            this.inboundOrder = List.copyOf(inboundOrder);
        }

        public OutboundBuilder add(HandlerInterceptor interceptor) {
            outboundOrder.add(interceptor);
            return this;
        }

        public InterceptorChain build() {
            return new InterceptorChain(inboundOrder, outboundOrder);
        }
    }
}

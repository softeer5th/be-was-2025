package webserver.interceptor;

import webserver.handler.HttpHandler;
import webserver.request.HttpRequest;
import webserver.response.HttpResponse;

import java.util.*;

/* 인터셉터의 실행 순서를 관리하는 클래스.
 *  생성법
 *  var chain = InterceptorChain
 *   .inbound()
 *       .add(interceptor1)
 *       .add(interceptor2)
 *  .outbound()
 *       .add(interceptor3)
 *       .add(interceptor4)
 * .build();
 *
 * chain.execute(request, handler) 호출 시 실행 순서
 * 1. interceptor1.preHandle
 * 2. interceptor2.preHandle
 * 3. handler.handle
 * 4. interceptor3.postHandle
 * 5. interceptor4.postHandle
 * */
public class InterceptorChain {
    private final List<HandlerInterceptor> inboundOrder;
    private final List<HandlerInterceptor> outboundOrder;

    private InterceptorChain(List<HandlerInterceptor> inboundOrder, List<HandlerInterceptor> outboundOrder) {
        this.inboundOrder = Collections.unmodifiableList(inboundOrder);
        this.outboundOrder = Collections.unmodifiableList(outboundOrder);
    }

    public static InboundBuilder inbound() {
        return new InboundBuilder();
    }

    public HttpResponse execute(HttpRequest request, HttpHandler handler) {
        // interceptor 가 handler 실행 전후로 공유하는 context
        Map<HandlerInterceptor, HandlerInterceptor.Context> contextMap = new WeakHashMap<>();

        // inbound order 대로 preHandle 실행
        for (HandlerInterceptor inboundInterceptor : inboundOrder) {
            HandlerInterceptor.Context context = inboundInterceptor.preHandle(request);
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
        private final List<HandlerInterceptor> inboundOrder = Collections.synchronizedList(new ArrayList<>());

        public InboundBuilder add(HandlerInterceptor interceptor) {
            inboundOrder.add(interceptor);
            return this;
        }

        public OutboundBuilder outbound() {
            return new OutboundBuilder(inboundOrder);
        }
    }


    public static class OutboundBuilder {
        private final List<HandlerInterceptor> inboundOrder;
        private final List<HandlerInterceptor> outboundOrder = Collections.synchronizedList(new ArrayList<>());

        public OutboundBuilder(List<HandlerInterceptor> inboundOrder) {
            this.inboundOrder = Collections.synchronizedList(List.copyOf(inboundOrder));
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

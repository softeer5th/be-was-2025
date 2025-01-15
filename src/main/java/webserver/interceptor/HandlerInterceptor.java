package webserver.interceptor;

import webserver.request.HttpRequest;
import webserver.response.HttpResponse;

public interface HandlerInterceptor {

    // HttpHandler.handle() 이 처리되기 전 호출
    // 이전 preHandle 메서드에서 반환한 HttpRequest 객체를 인자로 받아 새로운 HttpRequest 객체를 반환
    // 인자로 넘어온 context 객체는 해당 요청에 한하여 postHandle 메서드와 공유되는 객체
    default HttpRequest preHandle(HttpRequest request, Context context) {
        return request;
    }

    // HttpHandler.handle() 이 처리된 후 호출
    // 이전 postHandle 메서드에서 반환한 HttpResponse 객체를 인자로 받아 새로운 HttpResponse 객체를 반환
    // 인자로 넘어온 context 객체는 해당 요청에 한하여 preHandle 메서드와 공유되는 객체
    default HttpResponse postHandle(HttpRequest request, HttpResponse response, Context context) {
        return response;
    }

    class Context {
        private Object data;

        public Context() {
        }

        public void set(Object value) {
            this.data = value;
        }

        public Object get() {
            return this.data;
        }

    }
}

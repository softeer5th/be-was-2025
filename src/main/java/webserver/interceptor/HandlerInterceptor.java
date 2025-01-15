package webserver.interceptor;

import webserver.request.HttpRequest;
import webserver.response.HttpResponse;

public interface HandlerInterceptor {

    // 요청이 처리되기 전 호출되고, 이후 같은 요청의 postHandle 메서드에서 공유할 수 있는 Context 객체를 반환
    Context preHandle(HttpRequest request);

    // 요청이 처리된 후 호출되고, 이전 preHandle 메서드에서 반환한 Context 객체를 인자로 받아 처리
    HttpResponse postHandle(HttpRequest request, HttpResponse response, Context context);

    record Context(Object data) {
        private static final Context EMPTY = new Context(null);

        public static Context empty() {
            return EMPTY;
        }
    }
}

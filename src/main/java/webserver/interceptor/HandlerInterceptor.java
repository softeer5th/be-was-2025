package webserver.interceptor;

import webserver.request.HttpRequest;
import webserver.response.HttpResponse;

/**
 * HTTP Handler 실행 전후에 request, response 객체에 간섭하여 추가적인 기능을 제공하는 클래스
 */
public interface HandlerInterceptor {


    /**
     * HttpHandler.handle() 이 처리되기 전 호출<br>
     * 이전 preHandle 메서드에서 반환한 HttpRequest 객체를 인자로 받아 새로운 HttpRequest 객체를 반환<br>
     *
     * @param request 이전 preHandle 메서드에서 반환한 HttpRequest 객체
     * @param context 해당 요청에 한하여 postHandle 메서드와 공유되는 객체
     * @return 새로운 HttpRequest 객체
     */
    default HttpRequest preHandle(HttpRequest request, Context context) {
        return request;
    }

    /**
     * HttpHandler.handle() 이 처리된 후 호출<br>
     * 이전 postHandle 메서드에서 반환한 HttpResponse 객체를 인자로 받아 새로운 HttpResponse 객체를 반환<br>
     *
     * @param request  HTTP 요청
     * @param response 이전 postHandle 메서드에서 반환한 HttpResponse 객체
     * @param context  해당 요청에 한하여 preHandle 메서드와 공유되는 객체
     * @return 새로운 HttpResponse 객체
     */
    default HttpResponse postHandle(HttpRequest request, HttpResponse response, Context context) {
        return response;
    }

    /**
     * 하나의 HTTP 요청-응답 사이클에서 HandlerInterceptor가 서로 공유하게 되는 데이터
     */
    class Context {
        private Object data;

        public Context() {
        }

        /**
         * Context에 데이터를 저장한다.
         *
         * @param value 저장할 데이터
         */
        public void set(Object value) {
            this.data = value;
        }

        /**
         * Context에 저장된 데이터를 반환한다.
         *
         * @return 저장된 데이터
         */
        public Object get() {
            return this.data;
        }

    }
}

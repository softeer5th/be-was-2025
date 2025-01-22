package webserver.view;

import webserver.enums.ContentType;
import webserver.interceptor.HandlerInterceptor;
import webserver.request.HttpRequest;
import webserver.response.HttpResponse;
import webserver.session.HttpSession;

import java.util.Map;


/**
 * 템플릿을 사용하는 응답을 가로채 템플릿 엔진을 통해 렌더링하여 응답 body로 보내는 인터셉터
 */
public class TemplateEngineInterceptor implements HandlerInterceptor {
    private static final String SESSION_ATTRIBUTE_NAME = "session";
    private final TemplateEngine templateEngine;
    private final TemplateFileReader templateFileReader;

    /**
     * 생성자
     *
     * @param templateEngine     렌더링에 사용할 템플릿 엔진
     * @param templateFileReader 템플릿 파일을 문자열로 읽어오는 객체
     */
    public TemplateEngineInterceptor(TemplateEngine templateEngine, TemplateFileReader templateFileReader) {
        this.templateEngine = templateEngine;
        this.templateFileReader = templateFileReader;
    }

    /**
     * HttpResponse를 확인하여 템플릿을 사용하는 응답인 경우 템플릿을 렌더링하여 응답 body로 설정
     *
     * @param request  HTTP 요청
     * @param response HTTP 응답
     * @param context  미사용
     * @return 템플릿을 렌더링한 응답. 템플릿을 사용하지 않은 경우 response 그대로 반환
     */
    @Override
    public HttpResponse postHandle(HttpRequest request, HttpResponse response, Context context) {
        // 응답이 템플릿을 사용하는지 확인
        ModelAndTemplate modelAndTemplate = response.getModelAndTemplate();
        if (modelAndTemplate == null) {
            return response;
        }
        String templateName = modelAndTemplate.getTemplateName();
        Map<String, Object> model = modelAndTemplate.getModel();
        // 세션 정보를 모델에 추가
        setSessionToModel(request, model);

        // 템플릿 파일을 읽어옴.
        String templateString = templateFileReader.read(templateName);
        // 템플릿 렌더링
        String renderedHtml = templateEngine.render(templateString, model);

        response.setBody(renderedHtml.getBytes(), ContentType.TEXT_HTML);
        return response;
    }

    // 세션 정보를 모델에 추가하는 메서드
    private void setSessionToModel(HttpRequest request, Map<String, Object> model) {
        HttpSession session = request.getSession();
        if (session != null)
            model.put(SESSION_ATTRIBUTE_NAME, session);
    }
}

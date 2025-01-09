package http.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import http.request.HttpRequest;
import http.response.HttpResponse;

public class FrontControllerServlet implements Servlet {

	// 싱글톤 보장해주기 위함.
	private static final FrontControllerServlet INSTANCE = new FrontControllerServlet();
	private static final String STATIC_RESOURCES = "/resources";
	private final Map<String, Servlet> controllerMap;

	public FrontControllerServlet() {
		controllerMap = new HashMap<>();

		// TODO: 해당 객체는 정적인 객체인데 서블릿를 추가해주는 위치가 바람직한가?
		controllerMap.put(STATIC_RESOURCES, new StaticResourceServlet());
		controllerMap.put("/", new HomeServlet());
		controllerMap.put("/registration", new RegisterServlet());
		controllerMap.put("/user/create", null);
	}

	public static FrontControllerServlet getInstance(){
		return INSTANCE;
	}

	@Override
	public void service(HttpRequest request, HttpResponse response) throws IOException {

		// 정적 리소스 처리
		if(isResourceRequest(request)) {
			Servlet servlet = controllerMap.get(STATIC_RESOURCES);
			servlet.service(request, response);
			return;
		}

		// 동적 리소스 처리
		Servlet servlet = controllerMap.get(request.getPath());
		servlet.service(request, response);
	}

	private boolean isResourceRequest(HttpRequest request) {
		return request.getPath().lastIndexOf('.') != -1;
	}
}

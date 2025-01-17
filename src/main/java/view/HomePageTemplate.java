package view;

import model.User;

public class HomePageTemplate {
	public static final String NOT_LOGIN_HEADER =
		"          <li class=\"header__menu__item\">\n"
			+ "            <a class=\"btn btn_contained btn_size_s\" href=\"/login\">로그인</a>\n"
			+ "          </li>\n"
			+ "          <li class=\"header__menu__item\">\n"
			+ "            <a class=\"btn btn_ghost btn_size_s\" href=\"/registration\">\n"
			+ "              회원 가입\n"
			+ "            </a>\n"
			+ "          </li>";

	// 로그인했을 때의 헤더 템플릿
	public static final String LOGIN_HEADER_TEMPLATE =
		"          <li class=\"header__menu__item\">\n"
			+ "            <a class=\"user-name\" href=\"/mypage\">{username}</a>\n"
			+ "          </li>\n"
			+ "          <li class=\"header__menu__item\">\n"
			+ "            <a class=\"btn btn_ghost btn_size_s\" href=\"/logout\">로그아웃</a>\n"
			+ "          </li>";

	public static String renderLoginPage(String body, User user) {
		return body.replace(NOT_LOGIN_HEADER, LOGIN_HEADER_TEMPLATE.replace("{username}", user.getUserId()));
	}
}

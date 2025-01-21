package tag;

public enum HeaderMenu {
    LOGIN("""
            <li class="header__menu__item">
              <a class="btn btn_contained btn_size_s" href="/login">로그인</a>
            </li>
            """),
    LOGOUT("""
            <form method="POST" action="/logout">
              <button class="btn btn_ghost btn_size_s" type="submit">로그아웃</button>
            </form>"""),
    SIGNUP("""
            <li class="header__menu__item">
              <a class="btn btn_ghost btn_size_s" href="/registration">
                회원 가입
              </a>
            </li>"""),
    WRITE("""
            <li class="header__menu__item">
              <a class="btn btn_contained btn_size_s" href="/article">글쓰기</a>
            </li>
            """);

    private final String tag;
    HeaderMenu(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    public static String getWelcomeTag(String userId) {
        return "<li class=\"header__menu__item\">\n" +
                "<a class=\"btn btn_ghost btn_size_s\" href=\"/mypage\">" +
                userId + "님, 환영합니다!</a> </li>";
    }

    public static String renderHeaderMenu(String sessionId, String userId, String readFile){
        String usernameTag = (sessionId != null && userId != null) ?
                getWelcomeTag(userId) : "";
        String loginWriteTag = (sessionId != null && userId != null) ? HeaderMenu.WRITE.getTag() : HeaderMenu.LOGIN.getTag();
        String signupLogoutTag = (sessionId != null && userId != null) ? HeaderMenu.LOGOUT.getTag() : HeaderMenu.SIGNUP.getTag();


        return readFile.replace("${username}", usernameTag)
                .replace("${loginWrite}", loginWriteTag)
                .replace("${signupLogout}", signupLogoutTag);
    }
}

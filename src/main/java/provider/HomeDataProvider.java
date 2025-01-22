package provider;

import db.transaction.TransactionTemplate;
import manager.ArticleManager;
import manager.UserManager;
import model.Article;
import model.User;

import java.util.Map;

public class HomeDataProvider implements DynamicDataProvider{
    private final TransactionTemplate transactionTemplate = TransactionTemplate.getInstance();
    private final UserManager userManager = UserManager.getInstance();
    private final ArticleManager articleManager = ArticleManager.getInstance();

    private static final String MENU = "MENU";
    private static final String USERNAME = "USERNAME";
    private static final String ARTICLE_USERNAME = "ARTICLE_USERNAME";
    private static final String ARTICLE_CONTENT = "ARTICLE_CONTENT";

    @Override
    public Model provideData(Map<String, Object> params) {
        Model model = new Model();

        if(params.containsKey("userId")){
            model.put(MENU, createMenuLogin());
            User user = transactionTemplate.execute(userManager::getUser, params.get("userId"));
            model.put(USERNAME, user.getName());
        }else{
            model.put(MENU, createMenuNotLogin());
        }

        Article article;
        if(params.containsKey("page")) {
            article = transactionTemplate.execute(articleManager::getArticle, Integer.parseInt((String)params.get("page")), 1);
        }else{
            article = transactionTemplate.execute(articleManager::getArticle, 0, 1);
        }

        model.put(ARTICLE_USERNAME, article.getUser().getName());
        model.put(ARTICLE_CONTENT, article.getContent());

        return model;
    }

    private String createMenuNotLogin(){
        StringBuilder sb = new StringBuilder();
        sb.append("<li class=\"header__menu__item\">\n");
        sb.append("\t<a class=\"btn btn_contained btn_size_s\" href=\"/login\">로그인</a>\n");
        sb.append("</li>\n");
        sb.append("<li class=\"header__menu__item\">\n");
        sb.append("\t<a class=\"btn btn_ghost btn_size_s\" href=\"/registration\">회원 가입</a>\n");
        sb.append("</li>\n");
        return sb.toString();
    }

    private String createMenuLogin(){
        StringBuilder sb = new StringBuilder();
        sb.append("<li class=\"header__menu__item\">\n");
        sb.append("\t<a class=\"btn btn_ghost btn_size_s\" href=\"/mypage\"><!-- USERNAME --></a>\n");
        sb.append("</li>\n");
        sb.append("<li class=\"header__menu__item\">\n");
        sb.append("\t<a class=\"btn btn_red btn_size_s\" href=\"/logout\">로그아웃</a>");
        sb.append("</li>\n");
        return sb.toString();
    }


}

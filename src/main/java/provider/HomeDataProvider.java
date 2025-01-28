package provider;

import db.transaction.TransactionTemplate;
import manager.ArticleManager;
import manager.UserManager;
import model.Article;
import model.User;

import java.util.Base64;
import java.util.Map;

public class HomeDataProvider implements DynamicDataProvider{
    private final TransactionTemplate transactionTemplate = TransactionTemplate.getInstance();
    private final UserManager userManager = UserManager.getInstance();
    private final ArticleManager articleManager = ArticleManager.getInstance();

    private static final String MENU = "MENU";
    private static final String USERNAME = "USERNAME";
    private static final String ARTICLE_USERNAME = "ARTICLE_USERNAME";
    private static final String ARTICLE_CONTENT = "ARTICLE_CONTENT";
    private static final String ARTICLE_USER_IMAGE = "ARTICLE_USER_IMAGE";
    private static final String ARTICLE_IMAGE = "ARTICLE_IMAGE";

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

        User articleUser = article.getUser();

        if(articleUser.getProfileImage() == null){
            model.put(ARTICLE_USER_IMAGE, createNullProfileImage());
        }else{
            String base64Image = Base64.getEncoder().encodeToString(articleUser.getProfileImage());
            model.put(ARTICLE_USER_IMAGE, createProfileImage(base64Image));
        }

        if(article.getImage() == null){
            model.put(ARTICLE_IMAGE, createNullArticleImage());
        }else{
            String base64Image = Base64.getEncoder().encodeToString(article.getImage());
            model.put(ARTICLE_IMAGE, createArticleImage(base64Image));
        }


        model.put(ARTICLE_USERNAME, articleUser.getName());
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

    private String createNullProfileImage(){
        StringBuilder sb = new StringBuilder();
        sb.append("<img class=\"post__account__img\" />");
        return sb.toString();
    }

    private String createProfileImage(String base64Image){
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("<img class=\"post__account__img\" src=\"data:image/jpeg;base64,%s\"/>", base64Image));
        return sb.toString();
    }

    private String createNullArticleImage(){
        StringBuilder sb = new StringBuilder();
        sb.append("<img class=\"post__img\" />");
        return sb.toString();
    }

    private String createArticleImage(String base64Image){
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("<img class=\"post__img\" src=\"data:image/jpeg;base64,%s\"/>", base64Image));
        return sb.toString();
    }


}

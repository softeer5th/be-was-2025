package db;

import model.Article;
import model.User;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ArticleStore {
    private static ConcurrentMap<String, Article> articles = new ConcurrentHashMap<>();

    public static void addArticle(Article article) {
        articles.put(article.getArticleId(), article);
    }

    public static Optional<Article> findArticleById(String id) {
        return Optional.ofNullable(articles.get(id));
    }

    public static List<Article> findAll() {
        return articles.values().stream().sorted(Comparator.comparing(Article::getArticleId).reversed()).toList();
    }

    public static void initDb() {
        String content = """
            우리는 시스템 아키텍처에 대한 일관성 있는 접근이 필요하며, 필요한
                        모든 측면은 이미 개별적으로 인식되고 있다고 생각합니다. 즉, 응답이
                        잘 되고, 탄력적이며 유연하고 메시지 기반으로 동작하는 시스템 입니다.
                        우리는 이것을 리액티브 시스템(Reactive Systems)라고 부릅니다.
                        리액티브 시스템으로 구축된 시스템은 보다 유연하고, 느슨한 결합을
                        갖고, 확장성 이 있습니다. 이로 인해 개발이 더 쉬워지고 변경 사항을
                        적용하기 쉬워집니다. 이 시스템은 장애 에 대해 더 강한 내성을 지니며,
                        비록 장애가 발생 하더라도, 재난이 일어나기 보다는 간결한 방식으로
                        해결합니다. 리액티브 시스템은 높은 응답성을 가지며 사용자 에게
                        효과적인 상호적 피드백을 제공합니다.
            """;
        User user = new User("0", "1", "account", null);
        Article article = new Article(content, user);
        ArticleStore.addArticle(article);
    }
}

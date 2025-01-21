package db;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import dto.Cursor;
import model.Article;

public class ArticleDatabase {
	private static final AtomicInteger index = new AtomicInteger(1);
	private static Map<Integer, Article> articles = new HashMap<>();

	public static void save(Article article) {

		article.setId(index.getAndIncrement());
		articles.put(article.getId(), article);
	}

	public static Cursor<Article> findNthArticle(Integer n) {
		if (n < 1 || n > articles.size()) {
			throw new IllegalArgumentException("N is out of bounds");
		}

		Optional<Article> foundArticle = articles.keySet().stream()
			.sorted((a, b) -> Integer.compare(b, a))
			.skip(n - 1)
			.findFirst()
			.map(articles::get);

		boolean hasPrevPage = n > 1;
		boolean hasNextPage = n < articles.size();

		Integer prevPage = hasPrevPage ? n - 1 : 0;

		Integer nextPage = hasNextPage ? n + 1 : 0;

		return new Cursor<>(foundArticle, prevPage, nextPage, hasPrevPage, hasNextPage);
	}
}

package handler;

import domain.Article;
import domain.ArticleDao;
import domain.CommentDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import webserver.request.HttpRequest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class IndexPageHandlerTest {
    private ArticleDao articleDao;
    private CommentDao commentDao;
    private IndexPageHandler indexPageHandler;

    @BeforeEach
    void setUp() {
        articleDao = mock(ArticleDao.class);
        commentDao = mock(CommentDao.class);
        indexPageHandler = new IndexPageHandler(articleDao, commentDao);
    }

    @Test
    @DisplayName("게시글이 있을 경우 메인 페이지 로드")
    void test1() {
        var request = mock(HttpRequest.class);
        var article = Article.create(mock(), "제목", "내용");
        when(articleDao.findLatestArticleId()).thenReturn(Optional.of(1L));
        when(articleDao.findArticleById(1L)).thenReturn(Optional.of(article));

        var response = indexPageHandler.handleGet(request);

        assertThat(response.getModelAndTemplate().getTemplateName()).isEqualTo("/index.html");
        assertThat(response.getStatusCode().statusCode).isEqualTo(200);
    }


    @Test
    @DisplayName("게시글이 없을 경우 빈 화면 페이지 로드")
    void test2() {
        HttpRequest request = mock(HttpRequest.class);
        when(articleDao.findLatestArticleId()).thenReturn(Optional.empty());

        var response = indexPageHandler.handleGet(request);

        assertThat(response.getStatusCode().statusCode).isEqualTo(200);
        assertThat(response.getModelAndTemplate().getTemplateName()).isEqualTo("/noArticle.html");
    }

}
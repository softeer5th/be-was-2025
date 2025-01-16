package util;

import exception.BaseException;
import exception.HttpErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class QueryUtilTest {

    @Test
    @DisplayName("쿼리문 파싱 성공")
    public void testHandleParseQuerySuccessfully() {
        String query = "key1=value1&key2=value2";
        Map<String, String> result = QueryUtil.parseQueryParams(query, 2);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result).containsEntry("key1", "value1");
        assertThat(result).containsEntry("key2", "value2");
    }

    @Test
    @DisplayName("쿼리문이 비어있는 경우")
    public void testHandleWhenQueryIsEmpty() {
        String query = "";

        assertThatThrownBy(() -> QueryUtil.parseQueryParams(query, 1))
                .isInstanceOf(BaseException.class)
                .hasMessageContaining(HttpErrorCode.INVALID_QUERY_PARAM.getMessage());

    }

    @Test
    @DisplayName("쿼리문과 파라미터로 주어진 사이즈가 일치하지 않는 경우")
    public void testHandleWhenQuerySizeDoesNotMatch() {
        String query = "key1=value1";

        assertThatThrownBy(() -> QueryUtil.parseQueryParams(query, 2))
                .isInstanceOf(BaseException.class)
                .hasMessageContaining(HttpErrorCode.INVALID_QUERY_PARAM.getMessage());
    }

    @Test
    @DisplayName("쿼리문의 짝이 안맞는 경우")
    public void testHandleWhenQueryPairIsInvalid() {
        String query = "key1=value1&key2";

        assertThatThrownBy(() -> QueryUtil.parseQueryParams(query, 2))
                .isInstanceOf(BaseException.class)
                .hasMessageContaining(HttpErrorCode.INVALID_QUERY_PARAM.getMessage());
    }
}

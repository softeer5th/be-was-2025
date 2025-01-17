package response;

import exception.ErrorCode;
import exception.ServerErrorException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpResponseTest {
    @Test
    @DisplayName("header를 name,value 쌍으로 설정하지 않으면 에러가 발생한다.")
    void setHeader(){
        HttpResponse httpResponse = new HttpResponse();

        Assertions.assertThatThrownBy(()->httpResponse.setHeader("key1","value1","key2"))
                .isInstanceOf(ServerErrorException.class)
                .hasMessage(ErrorCode.KEY_VALUE_SHOULD_BE_EVEN.getMessage());
    }

}
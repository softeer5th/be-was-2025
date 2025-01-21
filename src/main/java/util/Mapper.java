package util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

// jackson 라이브러리를 사용하여 객체를 변환하는 클래스
public class Mapper {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger log = LoggerFactory.getLogger(Mapper.class);

    public static <T> Optional<T> mapToObject(Map<String, String> map, Class<T> clazz) {
        try {
            return Optional.ofNullable(objectMapper.convertValue(map, clazz));
        } catch (IllegalArgumentException e) {
            log.error("Failed to convert map to object", e);
            return Optional.empty();
        }
    }
}

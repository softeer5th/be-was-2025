package util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

/**
 * 객체를 변환하는 클래스
 */
public class Mapper {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger log = LoggerFactory.getLogger(Mapper.class);

    /**
     * Map을 객체로 변환한다.
     *
     * @param map   변환할 Map
     * @param clazz 변환할 객체의 클래스. 기본 생성자 필요
     * @param <T>   변환할 객체의 타입
     * @return 변환된 객체
     */
    public static <T> Optional<T> mapToObject(Map<String, String> map, Class<T> clazz) {
        try {
            return Optional.ofNullable(objectMapper.convertValue(map, clazz));
        } catch (IllegalArgumentException e) {
            log.error("Failed to convert map to object", e);
            return Optional.empty();
        }
    }
}

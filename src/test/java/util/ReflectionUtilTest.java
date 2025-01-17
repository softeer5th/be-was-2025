package util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ReflectionUtilTest {
    @Test
    @DisplayName("중첩된 Map에서 값 가져오기")
    void test1() {
        // given
        var map = Map.of("key", Map.of("key2", "value"));

        // when
        var result = ReflectionUtil.recursiveCallGetter(map, "key.key2");

        // then
        assertThat(result).isPresent().hasValue("value");
    }

    @Test
    @DisplayName("중첩된 Object에서 값 가져오기")
    void test2() {
        // given
        var nestedObject = new NestedObject()
                .setA(new NestedObject()
                        .setB(new NestedObject()
                                .setC("value")));

        // when
        var result = ReflectionUtil.recursiveCallGetter(nestedObject, "a.b.c");

        // then
        assertThat(result).isPresent().hasValue("value");
    }

    @Test
    @DisplayName("중첩된 Record에서 값 가져오기")
    void test3() {
        // given
        var nestedRecord = new NestedRecord(new NestedRecord(new NestedRecord("value", null, null), null, null), null, null);

        // when
        var result = ReflectionUtil.recursiveCallGetter(nestedRecord, "a.a.a");

        // then
        assertThat(result).isPresent().hasValue("value");
    }

    @Test
    @DisplayName("중첩된 Map, Object, Record 에서 값 가져오기")
    void test4() {
        // given
        var map = Map.of("key", new NestedObject()
                .setA(new NestedRecord(null, new NestedObject()
                        .setC("value"), null)));

        // when
        var result = ReflectionUtil.recursiveCallGetter(map, "key.a.b.c");

        // then
        assertThat(result).isPresent().hasValue("value");
    }

    record NestedRecord(Object a, Object b, Object c) {
    }

    static class NestedObject {
        private Object a;
        private Object b;
        private Object c;

        public Object getA() {
            return a;
        }

        public NestedObject setA(Object a) {
            this.a = a;
            return this;
        }

        public Object getB() {
            return b;
        }

        public NestedObject setB(Object b) {
            this.b = b;
            return this;
        }

        public Object getC() {
            return c;
        }

        public NestedObject setC(Object c) {
            this.c = c;
            return this;
        }
    }

}
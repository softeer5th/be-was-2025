package util;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class AutoKeyGenerator {
    private static AtomicInteger key = new AtomicInteger(0);

    public static int getKey() {
        return key.getAndAdd(1);
    }
}

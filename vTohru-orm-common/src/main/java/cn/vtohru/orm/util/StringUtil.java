package cn.vtohru.orm.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class StringUtil {
    public static String join(Collection<?> collection, String joinKey) {
        return collection.stream().map(Object::toString).collect(Collectors.joining(joinKey));
    }

    public static String join(String joinKey, String... collection) {
        return Arrays.stream(collection).collect(Collectors.joining(joinKey));
    }

    public static String sha256Hex(String fieldName) {
        return null;
    }
}

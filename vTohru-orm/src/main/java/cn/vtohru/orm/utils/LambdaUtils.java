package cn.vtohru.orm.utils;

import cn.vtohru.orm.exception.OrmException;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * Utility class for composing SQL statements
 */
public class LambdaUtils {

    /**
     * eg: user_id -> userId
     *
     * @param value
     * @return
     */
    public static String toCamelName(String value) {
        String[] partOfNames = value.split("_");

        StringBuilder sb = new StringBuilder(partOfNames[0]);
        for (int i = 1; i < partOfNames.length; i++) {
            sb.append(partOfNames[i].substring(0, 1).toUpperCase());
            sb.append(partOfNames[i].substring(1));
        }
        return sb.toString();
    }

    /**
     * eg: userId -> user_id
     */
    public static String toUnderline(String value) {
        StringBuilder result = new StringBuilder();
        if (value != null && value.length() > 0) {
            result.append(value.substring(0, 1).toLowerCase());
            for (int i = 1; i < value.length(); i++) {
                String s = value.substring(i, i + 1);
                if (s.equals(s.toUpperCase())) {
                    result.append("_");
                    result.append(s.toLowerCase());
                } else {
                    result.append(s);
                }
            }
        }
        return result.toString();
    }

    public static String getLambdaFieldName(Serializable lambda) {
        SerializedLambda serializedLambda = computeSerializedLambda(lambda);
        return getLambdaFieldName(serializedLambda);
    }

    private static SerializedLambda computeSerializedLambda(Serializable lambda) {
        for (Class<?> cl = lambda.getClass(); cl != null; cl = cl.getSuperclass()) {
            try {
                Method m = cl.getDeclaredMethod("writeReplace");
                m.setAccessible(true);
                Object replacement = m.invoke(lambda);
                if (!(replacement instanceof SerializedLambda)) {
                    break; // custom interface implementation
                }
                return (SerializedLambda) replacement;
            } catch (Exception e) {
                throw new OrmException("get lambda column name fail");
            }
        }
        return null;
    }


    public static String capitalize(String input) {
        return input.substring(0, 1).toLowerCase() + input.substring(1, input.length());
    }

    /**
     * Convert a List to a generic array
     *
     * @param list list collection
     * @param <T>  generic
     * @return array
     */
    public static <T> T[] toArray(List<T> list) {
        T[] toR = (T[]) Array.newInstance(list.get(0).getClass(), list.size());
        for (int i = 0; i < list.size(); i++) {
            toR[i] = list.get(i);
        }
        return toR;
    }

    public static boolean isBasicType(Class<?> type) {
        return type.equals(char.class) ||
                type.equals(Character.class) ||
                type.equals(boolean.class) ||
                type.equals(Boolean.class) ||
                type.equals(byte.class) ||
                type.equals(Byte.class) ||
                type.equals(short.class) ||
                type.equals(Short.class) ||
                type.equals(int.class) ||
                type.equals(Integer.class) ||
                type.equals(long.class) ||
                type.equals(Long.class) ||
                type.equals(BigDecimal.class) ||
                type.equals(BigInteger.class) ||
                type.equals(Date.class) ||
                type.equals(String.class) ||
                type.equals(double.class) ||
                type.equals(Double.class) ||
                type.equals(float.class) ||
                type.equals(Float.class);
    }

    public static String methodToFieldName(String methodName) {
        return capitalize(methodName.replace("get", ""));
    }

    private static String getLambdaFieldName(SerializedLambda serializedLambda) {
        String methodName = serializedLambda.getImplMethodName();
        return methodToFieldName(methodName);
    }
}

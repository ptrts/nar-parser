package me.narparser.utils;

import org.apache.commons.io.IOUtils;

import java.io.IOException;

public class ResourceUtils {

    public static String getResourceAsString(String name) {
        Class<?> clientClass = StackTraceUtils.getClientClass(1);
        return getResourceAsString(clientClass, name, "UTF-8");
    }

    public static String getResourceAsString(String name, String encoding) {
        Class<?> clientClass = StackTraceUtils.getClientClass(1);
        return getResourceAsString(clientClass, name, encoding);
    }

    private static String getResourceAsString(Class<?> clientClass, String name, String encoding) {
        try {
            return IOUtils.toString(clientClass.getResource(name), encoding);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

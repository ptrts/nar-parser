package me.narparser.utils;

public class StackTraceUtils {

    public static String getMethodName(int levelsUpToClientMethod) {
        StackTraceElement element = getElement(levelsUpToClientMethod);
        return element.getMethodName();
    }

    private static StackTraceElement getElement(int levelsUpToClientMethod) {
        final StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        return trace[levelsUpToClientMethod + 1 /*Thread.getStackTrace()*/ + 1 /*this method*/];
    }

    public static Class<?> getClientClass(int levelsUpToClientMethod) {

        StackTraceElement element = getElement(levelsUpToClientMethod + 1 /*this method*/);

        Class<?> clazz;

        try {
            clazz = Class.forName(element.getClassName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return clazz;
    }
}

package org.xiaoheshan.hallo.boxing.pi.util;

/**
 * @author : _Chf
 * @since : 03-22-2018
 */
public class ConsoleUtil {

    private static final com.pi4j.util.Console INSTANCE;

    static {
        INSTANCE = new com.pi4j.util.Console();
        INSTANCE.title("<--  Hallo Boxing -->");
        INSTANCE.promptForExit();
    }

    private ConsoleUtil() {}

    public static com.pi4j.util.Console get() {
        return INSTANCE;
    }
}

package com.mycompany.dental.clinic.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Minimal JSON helper for flat, string-valued objects (e.g. request/response
 * bodies for the login endpoint). Not a general-purpose JSON library.
 */
public final class JsonUtil {

    private static final Pattern STRING_FIELD =
            Pattern.compile("\"(\\w+)\"\\s*:\\s*\"((?:\\\\.|[^\"\\\\])*)\"");
    private static final Pattern NUMBER_FIELD =
            Pattern.compile("\"(\\w+)\"\\s*:\\s*(-?\\d+(?:\\.\\d+)?)");

    private JsonUtil() {
    }

    public static Map<String, String> parseFlatObject(String json) {
        Map<String, String> result = new HashMap<>();
        if (json == null) {
            return result;
        }

        Matcher stringMatcher = STRING_FIELD.matcher(json);
        while (stringMatcher.find()) {
            result.put(stringMatcher.group(1), unescape(stringMatcher.group(2)));
        }

        Matcher numberMatcher = NUMBER_FIELD.matcher(json);
        while (numberMatcher.find()) {
            result.putIfAbsent(numberMatcher.group(1), numberMatcher.group(2));
        }

        return result;
    }

    public static String escape(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private static String unescape(String value) {
        return value.replace("\\\"", "\"").replace("\\\\", "\\");
    }
}

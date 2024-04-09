package com.darcy.zql.devtoolkit.service.utils;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class StringUtils {

    private static final String COMMA = ",";

    private static final Pattern CAMEL_PATTERN = Pattern.compile("[A-Z]([a-z\\d]+)?");    //字母、数字、下划线


    public static String getShortNick(String nick) {
        return nick.contains(":") ? nick.split(":")[1] : nick;
    }

    public static List<Long> splitDistinctIdStringByComma(String idString) {
        if (org.apache.commons.lang3.StringUtils.isBlank(idString)) {
            return Lists.newArrayList();
        }
        return Splitter.on(COMMA).omitEmptyStrings().splitToList(idString)
                .stream().map(Long::valueOf).distinct().collect(Collectors.toList());
    }

    public static List<Long> splitDistinctIdStringListByComma(List<String> idStrings) {
        return idStrings.stream().map(
                StringUtils::splitDistinctIdStringByComma
        ).flatMap(List::stream).distinct().collect(Collectors.toList());
    }

    public static String warpWithSquareBracket(String desc) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(desc)) {
            return "";
        }
        return "[" + desc + "]";
    }

    public static boolean equalsIgnoreEmpty(String str1, String str2) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(str1) && org.apache.commons.lang3.StringUtils.isEmpty(str2)) {
            return true;
        }

        return org.apache.commons.lang3.StringUtils.equals(str1, str2);
    }

    public static Boolean matchPairChar(String str, Character start, Character end) {
        char[] stack = new char[str.length()];
        int pos = 0;
        for (char c : str.toCharArray()) {
            if (start.equals(c) || end.equals(c)) {
                stack[pos] = c;
                pos++;

                if (pos > 1 && start.equals(stack[pos - 2]) && end.equals(stack[pos - 1])) {
                    pos -= 2;
                }
            }
        }
        return pos == 0;
    }

    public static String camelToUnderline(String camelString) {
        if (null == camelString || "".equals(camelString)) {
            return camelString;
        }
        camelString = String.valueOf(camelString.charAt(0)).toUpperCase().concat(camelString.substring(1));
        StringBuffer sb = new StringBuffer();
        Matcher matcher = CAMEL_PATTERN.matcher(camelString);
        while (matcher.find()) {
            String word = matcher.group();
            sb.append(word.toLowerCase());
            sb.append(matcher.end() == camelString.length() ? "" : "_");
        }
        return sb.toString();
    }

    public static String firstNotBlank(String... args) {
        for (String arg : args) {
            if (org.apache.commons.lang3.StringUtils.isNotBlank(arg)) {
                return arg;
            }
        }
        throw new NullPointerException(" all parameters are blank");
    }

}

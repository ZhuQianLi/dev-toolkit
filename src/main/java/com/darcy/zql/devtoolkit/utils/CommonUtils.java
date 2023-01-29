package com.darcy.zql.devtoolkit.utils;

import com.google.common.base.CaseFormat;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.Arrays;
import java.util.stream.Collectors;

public class CommonUtils {
    /**
     * 提取字符串中每个单词的首字母
     *
     * @param string extractWordFirstChar
     * @return ewfc
     */
    public static String extractWordFirstChar(String string) {
        string = lowerCamelToLowerUnderscore(string);
        String[] words = string.split("_");
        return Arrays.stream(words).map(word -> word.substring(0, 1)).collect(Collectors.joining());
    }

    /**
     * 转换字符串格式
     * lowerCamel --> lower_camel
     */
    public static String lowerCamelToLowerUnderscore(String string) {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, string);
    }

    /**
     * 转换字符串格式
     * UpperCamel --> upper_camel
     */
    public static String upperCamelToLowerUnderscore(String string) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, string);
    }

    /**
     * 弹窗提示
     */
    public static void showMessage(Project project, String title, String message) {
        Messages.showMessageDialog(project, message, title, Messages.getInformationIcon());
    }

    /**
     * 复制到粘贴板
     */
    public static void copyToClipboard(String content) {
        Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        systemClipboard.setContents(new StringSelection(content), null);
    }

}

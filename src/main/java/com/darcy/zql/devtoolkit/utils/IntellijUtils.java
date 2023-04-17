package com.darcy.zql.devtoolkit.utils;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

public class IntellijUtils {

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

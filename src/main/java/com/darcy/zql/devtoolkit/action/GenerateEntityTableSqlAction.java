package com.darcy.zql.devtoolkit.action;

import com.darcy.zql.devtoolkit.service.GenerateEntityTableSqlService;
import com.darcy.zql.devtoolkit.utils.Utils;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;


public class GenerateEntityTableSqlAction extends AnAction {

    private final GenerateEntityTableSqlService service = new GenerateEntityTableSqlService();

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        String tableSql = service.generateTableSql(event);
        Utils.copyToClipboard(tableSql);
        Utils.showMessage(event.getProject(), "生成表结构成功", "已复制到剪贴板");
    }

}

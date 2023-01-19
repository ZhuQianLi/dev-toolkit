package com.darcy.zql.devtoolkit.action;

import com.darcy.zql.devtoolkit.service.GenerateDaoFindSqlService;
import com.darcy.zql.devtoolkit.utils.Utils;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;


public class GenerateDaoFindSqlAction extends AnAction {

    private final GenerateDaoFindSqlService service = new GenerateDaoFindSqlService();

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        String findSql = service.generateFindAql(event);
        Utils.copyToClipboard(findSql);
        Utils.showMessage(event.getProject(), "生成相关查询成功", "已复制到剪贴板");
    }

}

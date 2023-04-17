package com.darcy.zql.devtoolkit.action;

import com.darcy.zql.devtoolkit.service.GenerateEntityTableSqlService;
import com.darcy.zql.devtoolkit.utils.IntellijUtils;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;


public class GenerateEntityTableSqlAction extends AnAction {

    private final GenerateEntityTableSqlService service = new GenerateEntityTableSqlService();

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        String tableSql = service.generateTableSql(event);
        IntellijUtils.copyToClipboard(tableSql);
        IntellijUtils.showMessage(event.getProject(), "生成表结构成功", "已复制到剪贴板");
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        DataContext dataContext = event.getDataContext();
        PsiElement psiElement = CommonDataKeys.PSI_ELEMENT.getData(dataContext);
        if (psiElement instanceof PsiClass) {
            PsiClass psiClass = (PsiClass) psiElement;
            if (psiClass.getAnnotation("javax.persistence.Entity") != null) {
                event.getPresentation().setEnabled(true);
                return;
            }
        }
        event.getPresentation().setEnabled(false);
    }
}

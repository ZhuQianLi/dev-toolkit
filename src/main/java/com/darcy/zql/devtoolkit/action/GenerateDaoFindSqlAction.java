package com.darcy.zql.devtoolkit.action;

import com.darcy.zql.devtoolkit.service.GenerateDaoFindSqlService;
import com.darcy.zql.devtoolkit.utils.IntellijUtils;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;


public class GenerateDaoFindSqlAction extends AnAction {

    private final GenerateDaoFindSqlService service = new GenerateDaoFindSqlService();

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        String findSql = service.generateFindAql(event);
        IntellijUtils.copyToClipboard(findSql);
        IntellijUtils.showMessage(event.getProject(), "生成相关查询成功", "已复制到剪贴板");
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        DataContext dataContext = event.getDataContext();
        PsiElement psiElement = CommonDataKeys.PSI_ELEMENT.getData(dataContext);
        if (psiElement instanceof PsiClass) {
            PsiClass psiClass = (PsiClass) psiElement;
            if (psiClass.getName() != null && psiClass.getName().endsWith("Dao")) {
                event.getPresentation().setEnabled(true);
                return;
            }
        }
        event.getPresentation().setEnabled(false);
    }
}

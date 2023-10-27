package com.darcy.zql.devtoolkit.action;

import com.darcy.zql.devtoolkit.service.InsertNotNullCodeService;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;


public class AddNotNullCodeAction extends AnAction {

    private final InsertNotNullCodeService service = new InsertNotNullCodeService();

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        service.insertNotNullCode(event);
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        DataContext dataContext = event.getDataContext();
        PsiElement psiElement = CommonDataKeys.PSI_ELEMENT.getData(dataContext);
        if (psiElement instanceof PsiClass) {
            PsiClass psiClass = (PsiClass) psiElement;
            if (psiClass.getName() != null && psiClass.getName().endsWith("Cmd")) {
                event.getPresentation().setEnabled(true);
                return;
            }
        }
        event.getPresentation().setEnabled(false);
    }

}

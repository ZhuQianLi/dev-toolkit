package com.zhuqianli.devtoolkit.action;

import com.intellij.openapi.actionSystem.*;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.zhuqianli.devtoolkit.service.InsertNotNullCodeService;
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
            if (psiClass.getName() != null && (psiClass.getName().endsWith("Cmd") || psiClass.getName().endsWith("Dto"))) {
                event.getPresentation().setEnabled(true);
                return;
            }
        }
        event.getPresentation().setEnabled(false);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

}

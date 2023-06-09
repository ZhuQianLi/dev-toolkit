package com.darcy.zql.devtoolkit.action;

import com.darcy.zql.devtoolkit.service.GenerateObjectConvertCodeService;
import com.darcy.zql.devtoolkit.utils.IntellijUtils;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GenerateObjectConvertCodeAction extends AnAction {

    private final GenerateObjectConvertCodeService service = new GenerateObjectConvertCodeService();

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        assert project != null;

        DataContext dataContext = event.getDataContext();
        PsiElement psiElement = CommonDataKeys.PSI_ELEMENT.getData(dataContext);
        PsiMethod psiMethod = (PsiMethod) psiElement;
        assert psiMethod != null;

        List<String> codes = service.generateMethodCode(project, psiMethod);

        IntellijUtils.writeJavaCodes(event, codes);
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        DataContext dataContext = event.getDataContext();
        PsiElement psiElement = CommonDataKeys.PSI_ELEMENT.getData(dataContext);
        event.getPresentation().setEnabled(psiElement instanceof PsiMethod);
    }

}

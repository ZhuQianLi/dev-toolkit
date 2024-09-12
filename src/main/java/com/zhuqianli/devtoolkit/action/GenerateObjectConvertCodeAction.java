package com.zhuqianli.devtoolkit.action;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.zhuqianli.devtoolkit.java.lang.JavaClassLoader;
import com.zhuqianli.devtoolkit.java.lang.JavaMethod;
import com.zhuqianli.devtoolkit.java.lang.psi.PsiClassLoaderImpl;
import com.zhuqianli.devtoolkit.java.lang.psi.PsiMethodImpl;
import com.zhuqianli.devtoolkit.service.GenerateObjectConvertCodeService;
import com.zhuqianli.devtoolkit.utils.IntellijUtils;
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

        JavaClassLoader classLoader = new PsiClassLoaderImpl(project);
        JavaMethod method = new PsiMethodImpl(psiMethod);

        List<String> codes = service.generateMethodCode(classLoader, method);

        IntellijUtils.writeJavaCodes(event, codes);
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        DataContext dataContext = event.getDataContext();
        PsiElement psiElement = CommonDataKeys.PSI_ELEMENT.getData(dataContext);
        event.getPresentation().setEnabled(psiElement instanceof PsiMethod);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

}

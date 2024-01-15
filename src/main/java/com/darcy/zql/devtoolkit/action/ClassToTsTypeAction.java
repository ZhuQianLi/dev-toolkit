package com.darcy.zql.devtoolkit.action;

import com.darcy.zql.devtoolkit.java.lang.JavaClassLoader;
import com.darcy.zql.devtoolkit.java.lang.psi.PsiClassLoaderImpl;
import com.darcy.zql.devtoolkit.service.ClassToTsTypeService;
import com.darcy.zql.devtoolkit.ui.ClassToTsTypeModal;
import com.darcy.zql.devtoolkit.utils.IntellijUtils;
import com.google.common.base.Preconditions;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;


public class ClassToTsTypeAction extends AnAction {

    private final ClassToTsTypeService service = new ClassToTsTypeService();

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        DataContext dataContext = event.getDataContext();
        PsiClass psiClass = (PsiClass) CommonDataKeys.PSI_ELEMENT.getData(dataContext);
        Preconditions.checkNotNull(psiClass);
        String className = psiClass.getQualifiedName();

        JavaClassLoader classLoader = new PsiClassLoaderImpl(event.getProject());

        String code = service.generate(classLoader, className);

        IntellijUtils.copyToClipboard(code);
        ClassToTsTypeModal classToTsTypeModal = new ClassToTsTypeModal(code);
        classToTsTypeModal.pack();
        classToTsTypeModal.setVisible(true);
    }

}

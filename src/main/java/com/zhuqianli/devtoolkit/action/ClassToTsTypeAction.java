package com.zhuqianli.devtoolkit.action;

import com.google.common.base.Preconditions;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.psi.PsiClass;
import com.zhuqianli.devtoolkit.java.lang.JavaClassLoader;
import com.zhuqianli.devtoolkit.java.lang.psi.PsiClassLoaderImpl;
import com.zhuqianli.devtoolkit.service.ClassToTsTypeService;
import com.zhuqianli.devtoolkit.utils.IntellijUtils;
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
        IntellijUtils.showMessage(event.getProject(), "生成TS类型代码成功", "已复制到剪贴板");
    }

}

package com.darcy.zql.devtoolkit.java.lang.psi;

import com.darcy.zql.devtoolkit.java.lang.JavaClass;
import com.darcy.zql.devtoolkit.java.lang.JavaClassLoader;
import com.darcy.zql.devtoolkit.utils.PsiClassUtils;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;

import javax.annotation.Nullable;

public class PsiClassLoaderImpl implements JavaClassLoader {

    Project project;

    public PsiClassLoaderImpl(Project project) {
        this.project = project;
    }

    @Nullable
    @Override
    public JavaClass forName(String name) {
        PsiClass psiClass = PsiClassUtils.getPsiClassByFullName(project, name);
        return new PsiClassImpl(psiClass);
    }

}

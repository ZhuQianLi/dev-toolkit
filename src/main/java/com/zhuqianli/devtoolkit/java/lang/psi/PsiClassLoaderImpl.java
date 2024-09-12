package com.zhuqianli.devtoolkit.java.lang.psi;

import com.zhuqianli.devtoolkit.java.lang.JavaClass;
import com.zhuqianli.devtoolkit.java.lang.JavaClassLoader;
import com.zhuqianli.devtoolkit.utils.StringUtils;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;

import org.jetbrains.annotations.Nullable;
import java.util.Objects;

public class PsiClassLoaderImpl implements JavaClassLoader {

    Project project;

    public PsiClassLoaderImpl(Project project) {
        this.project = project;
    }

    @Nullable
    @Override
    public JavaClass forName(String name) {
        String simpleName = StringUtils.substringAfterLast(name, ".");
        PsiClass[] psiClasses = PsiShortNamesCache.getInstance(project).getClassesByName(simpleName, GlobalSearchScope.allScope(project));
        for (PsiClass psiClass : psiClasses) {
            if (Objects.equals(psiClass.getQualifiedName(), name)) {
                return new PsiClassImpl(psiClass);
            }
        }
        return null;
    }

}

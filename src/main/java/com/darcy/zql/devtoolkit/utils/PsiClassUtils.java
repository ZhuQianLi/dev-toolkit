package com.darcy.zql.devtoolkit.utils;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiType;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class PsiClassUtils {

    /**
     * 获取PsiClass
     */
    public static PsiClass getPsiClassByPsiType(Project project, PsiType returnType) {
        PsiClass psiClass = getPsiClassByFullName(project, returnType.getCanonicalText());
        assert psiClass != null;
        return psiClass;
    }

    /**
     * 通过全类名，获取PsiClass
     */
    @Nullable
    public static PsiClass getPsiClassByFullName(Project project, String className) {
        String simpleName = StringUtils.substringAfterLast(className, ".");
        PsiClass[] psiClasses = PsiShortNamesCache.getInstance(project)
                .getClassesByName(simpleName, GlobalSearchScope.allScope(project));
        for (PsiClass psiClass : psiClasses) {
            if (Objects.equals(psiClass.getQualifiedName(), className)) {
                return psiClass;
            }
        }
        return null;
    }

}

package com.zhuqianli.devtoolkit.java.lang.psi;

import com.zhuqianli.devtoolkit.java.lang.*;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiEnumConstant;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PsiClassImpl implements JavaClass {

    PsiClass psiClass;

    public PsiClassImpl(PsiClass psiClass) {
        this.psiClass = psiClass;
    }

    @Override
    public String getSimpleName() {
        return psiClass.getName();
    }

    @Override
    public String getName() {
        return psiClass.getQualifiedName();
    }

    @Override
    public List<JavaField> getFields() {
        return Arrays.stream(psiClass.getAllFields()).map(PsiFieldImpl::new).collect(Collectors.toList());
    }

    public List<JavaMethod> getMethods() {
        return Arrays.stream(psiClass.getMethods()).map(PsiMethodImpl::new).collect(Collectors.toList());
    }

    @Override
    public JavaDocComment getDocComment() {
        return new PsiDocCommentImpl(psiClass.getDocComment());
    }

    @Override
    public boolean isEnum() {
        return psiClass.isEnum();
    }

    @Override
    public List<JavaEnumConstant> getEnumConstants() {
        return Arrays.stream(psiClass.getAllFields()).filter(f -> f instanceof PsiEnumConstant).map(PsiFieldImpl::new).collect(Collectors.toList());
    }

}

package com.zhuqianli.devtoolkit.java.lang.psi;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiModifierList;
import com.zhuqianli.devtoolkit.java.lang.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PsiFieldImpl implements JavaField, JavaEnumConstant {

    PsiField psiField;

    public PsiFieldImpl(PsiField psiField) {
        this.psiField = psiField;
    }

    @Override
    public String getName() {
        return psiField.getName();
    }

    @Override
    public JavaType getType() {
        return new PsiTypeImpl(psiField.getType());
    }

    @Override
    public JavaDocComment getDocComment() {
        return new PsiDocCommentImpl(psiField.getDocComment());
    }

    @Override
    public boolean isStatic() {
        PsiModifierList modifierList = psiField.getModifierList();
        return modifierList != null && modifierList.hasModifierProperty(PsiModifier.STATIC);
    }

    @Override
    public boolean isNullable() {
        return psiField.hasAnnotation("org.springframework.lang.Nullable") || psiField.hasAnnotation("javax.annotation.Nullable");
    }

    @Override
    public List<JavaAnnotation> getAnnotations() {
        return Arrays.stream(psiField.getAnnotations()).map(PsiAnnotationImpl::new).collect(Collectors.toList());
    }

    @Override
    public boolean existAnnotation(String annotation) {
        return psiField.getAnnotation(annotation) != null;
    }

}

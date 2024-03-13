package com.darcy.zql.devtoolkit.java.lang.psi;

import com.darcy.zql.devtoolkit.java.lang.JavaClass;
import com.darcy.zql.devtoolkit.java.lang.JavaEnumConstant;
import com.darcy.zql.devtoolkit.java.lang.JavaField;
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

    @Override
    public boolean isEnum() {
        return psiClass.isEnum();
    }

    @Override
    public List<JavaEnumConstant> getEnumConstants() {
        return Arrays.stream(psiClass.getAllFields()).filter(f -> f instanceof PsiEnumConstant).map(PsiFieldImpl::new).collect(Collectors.toList());
    }

}

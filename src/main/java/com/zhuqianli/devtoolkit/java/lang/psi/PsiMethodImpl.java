package com.zhuqianli.devtoolkit.java.lang.psi;

import com.zhuqianli.devtoolkit.java.lang.JavaMethod;
import com.zhuqianli.devtoolkit.java.lang.JavaParameter;
import com.zhuqianli.devtoolkit.java.lang.JavaType;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiModifierList;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PsiMethodImpl implements JavaMethod {

    PsiMethod psiMethod;

    public PsiMethodImpl(PsiMethod psiMethod) {
        this.psiMethod = psiMethod;
    }

    @Override
    public String getName() {
        return psiMethod.getName();
    }

    @Override
    public JavaType getReturnType() {
        return new PsiTypeImpl(psiMethod.getReturnType());
    }

    @Override
    public List<JavaParameter> getParameters() {
        return Arrays.stream(psiMethod.getParameterList().getParameters()).map(PsiParameterImpl::new).collect(Collectors.toList());
    }

    @Override
    public boolean isDefault() {
        PsiModifierList modifierList = psiMethod.getModifierList();
        return modifierList.hasModifierProperty(PsiModifier.DEFAULT);
    }

    @Override
    public boolean isStatic() {
        PsiModifierList modifierList = psiMethod.getModifierList();
        return modifierList.hasModifierProperty(PsiModifier.STATIC);
    }

}

package com.darcy.zql.devtoolkit.java.lang.psi;

import com.darcy.zql.devtoolkit.java.lang.JavaEnumConstant;
import com.darcy.zql.devtoolkit.java.lang.JavaField;
import com.darcy.zql.devtoolkit.java.lang.JavaType;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiModifierList;

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
    public boolean isStatic() {
        PsiModifierList modifierList = psiField.getModifierList();
        return modifierList != null && modifierList.hasModifierProperty(PsiModifier.STATIC);
    }

    @Override
    public boolean isNullable() {
        return psiField.getAnnotation("@Nullable") != null;
    }

}

package com.zhuqianli.devtoolkit.java.lang.psi;

import com.zhuqianli.devtoolkit.java.lang.JavaParameter;
import com.zhuqianli.devtoolkit.java.lang.JavaType;
import com.intellij.psi.PsiParameter;

public class PsiParameterImpl implements JavaParameter {

    PsiParameter psiParameter;

    public PsiParameterImpl(PsiParameter psiParameter) {
        this.psiParameter = psiParameter;
    }

    @Override
    public String getName() {
        return psiParameter.getName();
    }

    @Override
    public JavaType getType() {
        return new PsiTypeImpl(psiParameter.getType());
    }

}

package com.darcy.zql.devtoolkit.java.lang.psi;

import com.darcy.zql.devtoolkit.java.lang.JavaType;
import com.darcy.zql.devtoolkit.utils.StringUtils;
import com.darcy.zql.devtoolkit.valueobject.JavaPrimitiveType;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiType;

import java.util.Objects;

import static com.darcy.zql.devtoolkit.valueobject.JavaPrimitiveType.BigDecimal;
import static com.darcy.zql.devtoolkit.valueobject.JavaPrimitiveType.CANONICAL_TEXT_MAP;

public class PsiTypeImpl implements JavaType {

    PsiType psiType;

    public PsiTypeImpl(PsiType psiType) {
        this.psiType = psiType;
    }

    @Override
    public String getName() {
        return psiType.getCanonicalText();
    }

    @Override
    public String getSimpleName() {
        return psiType.getPresentableText();
    }

    @Override
    public JavaType getGenericsType() {
        PsiClassType psiClassType = (PsiClassType) psiType;
        return new PsiTypeImpl(psiClassType.getParameters()[0]);
    }

    @Override
    public boolean isString() {
        return Objects.equals(psiType.getCanonicalText(), JavaPrimitiveType.String.getCanonicalText());
    }

    @Override
    public boolean isLocalDate() {
        return Objects.equals(psiType.getCanonicalText(), JavaPrimitiveType.LocalDate.getCanonicalText());
    }

    @Override
    public boolean isLocalDateTime() {
        return Objects.equals(psiType.getCanonicalText(), JavaPrimitiveType.LocalDateTime.getCanonicalText());
    }

    @Override
    public boolean isBoolean() {
        String canonicalText = psiType.getCanonicalText();
        return Objects.equals(canonicalText, JavaPrimitiveType.Boolean.getCanonicalText()) || Objects.equals(canonicalText, JavaPrimitiveType.primitive_boolean.getCanonicalText());
    }

    @Override
    public boolean isInteger() {
        String canonicalText = psiType.getCanonicalText();
        return Objects.equals(canonicalText, JavaPrimitiveType.Integer.getCanonicalText()) || Objects.equals(canonicalText, JavaPrimitiveType.primitive_int.getCanonicalText());
    }

    @Override
    public boolean isLong() {
        String canonicalText = psiType.getCanonicalText();
        return Objects.equals(canonicalText, JavaPrimitiveType.Long.getCanonicalText()) || Objects.equals(canonicalText, JavaPrimitiveType.primitive_long.getCanonicalText());
    }

    @Override
    public boolean isBigDecimal() {
        return Objects.equals(psiType.getCanonicalText(), BigDecimal.getCanonicalText());
    }

    @Override
    public boolean isCollection() {
        return psiType.getSuperTypes().length == 1 && StringUtils.contains(psiType.getSuperTypes()[0].getCanonicalText(), "java.util.Collection");
    }

    @Override
    public boolean isEnum() {
        return psiType.getSuperTypes().length == 1 && psiType.getSuperTypes()[0].getPresentableText().startsWith("Enum");
    }

    @Override
    public boolean isCustom() {
        return !(isPrimitive() || isCollection());
    }

    @Override
    public boolean isPrimitive() {
        return CANONICAL_TEXT_MAP.containsKey(psiType.getCanonicalText()) || isEnum();
    }

}

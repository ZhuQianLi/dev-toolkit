package com.darcy.zql.devtoolkit.utils;

import com.intellij.psi.*;

public class JavaLangUtils {
    /**
     * 判断类型是否是枚举类型
     */
    public static boolean isEnumType(PsiType psiType) {
        return psiType.getSuperTypes().length == 1
                && psiType.getSuperTypes()[0].getPresentableText().startsWith("Enum");
    }

    /**
     * 判断字段是否有static修饰
     */
    public static boolean isStaticModifier(PsiField field) {
        PsiModifierList modifierList = field.getModifierList();
        if (modifierList == null) {
            return false;
        }
        return modifierList.hasModifierProperty(PsiModifier.STATIC);
    }

    /**
     * 判断字段是否有default修饰
     */
    public static boolean isDefaultModifier(PsiMethod method) {
        PsiModifierList modifierList = method.getModifierList();
        return modifierList.hasModifierProperty(PsiModifier.DEFAULT);
    }
}

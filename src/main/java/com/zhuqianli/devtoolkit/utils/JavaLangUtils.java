package com.zhuqianli.devtoolkit.utils;

import com.google.common.collect.Sets;
import com.intellij.psi.*;

import java.util.Set;

// todo 待重构
@Deprecated
public class JavaLangUtils {

    /**
     * 判断类型是否是枚举类型
     */
    public static boolean isEnumType(PsiType psiType) {
        return psiType.getSuperTypes().length == 1 && psiType.getSuperTypes()[0].getPresentableText()
                .startsWith("Enum");
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

    /**
     * java基础类型名称
     */
    private static final Set<String> JAVA_BASE_TYPE_NAME_SET = Sets.newHashSet("String", "LocalDateTime", "LocalDate",
            "LocalTime", "Boolean", "boolean", "Integer", "int", "Long", "long", "BigDecimal");

    /**
     * 判定类型是否是Java基础类型
     */
    public static boolean isJavaBaseType(PsiType type) {
        if (JAVA_BASE_TYPE_NAME_SET.contains(type.getPresentableText())) {
            return true;
        }
        if (isEnumType(type)) {
            return true;
        }
        return false;
    }

    /**
     * 判定类型是否在java包下
     */
    public static boolean isInJavaPackageType(PsiType type) {
        return type.getCanonicalText().startsWith("java.");
    }

}

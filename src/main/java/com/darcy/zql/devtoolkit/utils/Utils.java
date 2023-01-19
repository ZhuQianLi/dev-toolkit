package com.darcy.zql.devtoolkit.utils;

import com.google.common.base.CaseFormat;
import com.intellij.lang.jvm.annotation.JvmAnnotationAttribute;
import com.intellij.lang.jvm.annotation.JvmAnnotationConstantValue;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.*;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;
import com.intellij.psi.javadoc.PsiDocToken;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Utils {

    /**
     * 判断字段是否存在@Nullable
     */
    public static boolean existsNullable(PsiField psiField) {
        PsiAnnotation[] annotations = psiField.getAnnotations();
        for (PsiAnnotation annotation : annotations) {
            if (Objects.equals(annotation.getText(), "@Nullable")) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断字段是否存在@GeneratedValue
     */
    public static boolean existsGeneratedValue(PsiField psiField) {
        return psiField.getAnnotation("javax.persistence.GeneratedValue") != null;
    }

    /**
     * 提取类上的唯一约束字段
     * * @Table(uniqueConstraints = @UniqueConstraint(columnNames = {"userId", "tid"}))
     *
     * @return ["userId", "tid"]
     */
    public static List<String> extractUniqueKey(PsiClass psiClass) {
        PsiAnnotation tableAnnotation = psiClass.getAnnotation("javax.persistence.Table");
        if (tableAnnotation == null) {
            return Collections.emptyList();
        }
        PsiAnnotationMemberValue uniqueConstraints = tableAnnotation.findAttributeValue("uniqueConstraints");
        if (uniqueConstraints instanceof PsiAnnotation) {
            PsiAnnotationMemberValue columnNames = ((PsiAnnotation) uniqueConstraints).findAttributeValue(
                    "columnNames");
            if (columnNames == null) {
                return Collections.emptyList();
            }
            PsiAnnotationMemberValue[] columnNamesValues = ((PsiArrayInitializerMemberValue) columnNames).getInitializers();
            return Arrays.stream(columnNamesValues).map(columnNamesValue -> {
                PsiLiteralExpression expression = (PsiLiteralExpression) columnNamesValue;
                return Objects.requireNonNull(expression.getValue()).toString();
            }).collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * 提取字段上的注释
     */
    public static List<String> extractCommentDescription(PsiField field) {
        PsiDocComment docComment = field.getDocComment();
        return extractCommentDescription(docComment);
    }

    /**
     * 提取类上的注释
     */
    public static List<String> extractCommentDescription(PsiClass psiClass) {
        PsiDocComment docComment = psiClass.getDocComment();
        return extractCommentDescription(docComment);
    }

    /**
     * 提取PsiDocComment的注释
     */
    public static List<String> extractCommentDescription(PsiDocComment docComment) {
        if (docComment == null) {
            return Collections.emptyList();
        }
        List<String> comments = Lists.newArrayList();
        PsiElement[] descriptionElements = docComment.getDescriptionElements();
        for (PsiElement descriptionElement : descriptionElements) {
            if (descriptionElement instanceof PsiDocToken) {
                comments.add(descriptionElement.getText().trim());
            }
        }
        return comments;
    }

    /**
     * 提取注释中的标签 @see Trade#id -> Trade#id
     */
    public static List<String> extractCommentTagForAtSee(PsiField field) {
        PsiDocComment docComment = field.getDocComment();
        return extractCommentTagForAtSee(docComment);
    }

    /**
     * 提取注释中的标签 @see Trade#id -> Trade#id
     */
    public static List<String> extractCommentTagForAtSee(PsiDocComment docComment) {
        if (docComment == null) {
            return Collections.emptyList();
        }
        List<String> result = Lists.newArrayList();
        for (PsiDocTag tag : docComment.getTags()) {
            if (tag.getNameElement().getText().equals("@see")) {
                result.add(Objects.requireNonNull(tag.getValueElement()).getText());
            }
        }
        return result;
    }

    /**
     * 提取字符串中每个单词的首字母
     *
     * @param string extractWordFirstChar
     * @return ewfc
     */
    public static String extractWordFirstChar(String string) {
        string = lowerCamelToLowerUnderscore(string);
        String[] words = string.split("_");
        return Arrays.stream(words).map(word -> word.substring(0, 1)).collect(Collectors.joining());
    }

    /**
     * 转换字符串格式
     * lowerCamel --> lower_camel
     */
    public static String lowerCamelToLowerUnderscore(String string) {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, string);
    }

    /**
     * 转换字符串格式
     * LowerCamel --> lower_camel
     */
    public static String upperCamelToLowerUnderscore(String string) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, string);
    }

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
        return modifierList.hasModifierProperty("static");
    }

    /**
     * 判断字段是否有default修饰
     */
    public static boolean isDefaultModifier(PsiMethod method) {
        PsiModifierList modifierList = method.getModifierList();
        return modifierList.hasModifierProperty("default");
    }


    /**
     * 判断字段是否存在@Type(type = "Json")
     */
    public static boolean existsJsonType(PsiField field) {
        PsiAnnotation tableAnnotation = field.getAnnotation("org.hibernate.annotations.Type");
        if (tableAnnotation == null) {
            return false;
        }
        PsiAnnotationMemberValue typeValue = tableAnnotation.findAttributeValue("type");
        if (typeValue == null) {
            return false;
        }
        Object value = ((PsiLiteralExpression) typeValue).getValue();
        if (value == null) {
            return false;
        }
        return value.toString().equals("Json");
    }

    public static boolean existsQueryAnnotation(PsiMethod method) {
        for (PsiAnnotation annotation : method.getAnnotations()) {
            if (Objects.equals(annotation.getQualifiedName(), "org.springframework.data.jpa.repository.Query")) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    public static String extractQueryValue(PsiMethod method) {
        PsiAnnotation queryAnnotation = method.getAnnotation("org.springframework.data.jpa.repository.Query");
        if (queryAnnotation == null) {
            return null;
        }
        for (JvmAnnotationAttribute attribute : queryAnnotation.getAttributes()) {
            if (attribute.getAttributeName().equals("value")) {
                if (attribute.getAttributeValue() instanceof JvmAnnotationConstantValue) {
                    Object constantValue = ((JvmAnnotationConstantValue) attribute.getAttributeValue()).getConstantValue();
                    return constantValue != null ? constantValue.toString() : null;
                }
            }
        }
        return null;
    }

    public static void showMessage(Project project, String title, String message) {
        Messages.showMessageDialog(project, message, title, Messages.getInformationIcon());
    }

    public static void copyToClipboard(String content) {
        Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        systemClipboard.setContents(new StringSelection(content), null);
    }

}

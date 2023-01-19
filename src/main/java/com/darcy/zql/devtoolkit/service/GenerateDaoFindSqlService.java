package com.darcy.zql.devtoolkit.service;

import com.darcy.zql.devtoolkit.utils.JavaLangUtils;
import com.darcy.zql.devtoolkit.utils.PsiAnnotationUtils;
import com.darcy.zql.devtoolkit.utils.CommonUtils;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.impl.source.PsiClassImpl;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GenerateDaoFindSqlService {

    @NotNull
    public String generateFindAql(AnActionEvent event) {
        DataContext dataContext = event.getDataContext();
        PsiElement psiElement = CommonDataKeys.PSI_ELEMENT.getData(dataContext);
        PsiClassImpl psiClass = (PsiClassImpl) psiElement;
        assert psiClass != null;

        List<String> findSqls = new ArrayList<>();
        for (PsiMethod method : psiClass.getOwnMethods()) {
            if (PsiAnnotationUtils.existsQuery(method)) {
                String findSql = PsiAnnotationUtils.extractQueryValue(method);
                if (findSql != null) {
                    findSqls.add(findSql);
                } else {
                    findSqls.add("不支持生成相关查询 方法名【" + method.getName() + "】");
                }
            } else if (JavaLangUtils.isDefaultModifier(method)) {
                findSqls.add("不支持生成相关查询 方法名【" + method.getName() + "】");
            } else {
                findSqls.add(generateFindSqlByMethod(psiClass, method));
            }
        }

        return String.join("\n", findSqls);
    }

    private String generateFindSqlByMethod(PsiClassImpl psiClass, PsiMethod method) {
        // className: User -> tableName: user
        String className = psiClass.getName();
        String entityName = StringUtils.substringBefore(className, "Dao");
        String tableName = CommonUtils.lowerCamelToLowerUnderscore(entityName);
        // methodName: findByNameAndAge
        String methodName = method.getName();
        if (methodName.startsWith("findBy")) {
            String columnsString = StringUtils.substringAfter(methodName, "findBy");

            String whereSql = Arrays.stream(columnsString.split("And")).map(this::resolveWhereSql).collect(Collectors.joining(" and "));

            return String.format("select * from %s where %s;", tableName, whereSql);
        } else if (methodName.startsWith("existsBy")) {
            String columnsString = StringUtils.substringAfter(methodName, "existsBy");

            String whereSql = Arrays.stream(columnsString.split("And")).map(this::resolveWhereSql).collect(Collectors.joining(" and "));

            return String.format("select * from %s where %s limit 1;", tableName, whereSql);
        } else if (methodName.startsWith("countBy")) {
            String columnsString = StringUtils.substringAfter(methodName, "countBy");

            String whereSql = Arrays.stream(columnsString.split("And")).map(this::resolveWhereSql).collect(Collectors.joining(" and "));

            return String.format("select count(*) from %s where %s;", tableName, whereSql);
        } else {
            throw new RuntimeException("解析方法名异常【" + methodName + "】");
        }
    }

    private String resolveWhereSql(String columnName) {
        if (columnName.endsWith("In")) {
            String cname = StringUtils.substringBeforeLast(columnName, "In");
            return CommonUtils.upperCamelToLowerUnderscore(cname) + " in (?)";
        } else if (columnName.endsWith("NotIn")) {
            String cname = StringUtils.substringBeforeLast(columnName, "NotIn");
            return CommonUtils.upperCamelToLowerUnderscore(cname) + " not in (?)";
        } else if (columnName.endsWith("IsTrue")) {
            String cname = StringUtils.substringBeforeLast(columnName, "IsTrue");
            return CommonUtils.upperCamelToLowerUnderscore(cname) + " = true";
        } else if (columnName.endsWith("IsFalse")) {
            String cname = StringUtils.substringBeforeLast(columnName, "IsFalse");
            return CommonUtils.upperCamelToLowerUnderscore(cname) + " = false";
        } else if (columnName.endsWith("False")) {
            String cname = StringUtils.substringBeforeLast(columnName, "False");
            return CommonUtils.upperCamelToLowerUnderscore(cname) + " = false";
        } else if (columnName.endsWith("True")) {
            String cname = StringUtils.substringBeforeLast(columnName, "True");
            return CommonUtils.upperCamelToLowerUnderscore(cname) + " = false";
        } else if (columnName.endsWith("GreaterThanEqual")) {
            String cname = StringUtils.substringBeforeLast(columnName, "GreaterThanEqual");
            return CommonUtils.upperCamelToLowerUnderscore(cname) + " >= ?";
        } else if (columnName.endsWith("LessThanEqual")) {
            String cname = StringUtils.substringBeforeLast(columnName, "LessThanEqual");
            return CommonUtils.upperCamelToLowerUnderscore(cname) + " <= ?";
        } else if (columnName.endsWith("GreaterThan")) {
            String cname = StringUtils.substringBeforeLast(columnName, "GreaterThan");
            return CommonUtils.upperCamelToLowerUnderscore(cname) + " > ?";
        } else if (columnName.endsWith("LessThan")) {
            String cname = StringUtils.substringBeforeLast(columnName, "GreaterThan");
            return CommonUtils.upperCamelToLowerUnderscore(cname) + " < ?";
        } else if (columnName.endsWith("After")) {
            String cname = StringUtils.substringBeforeLast(columnName, "After");
            return CommonUtils.upperCamelToLowerUnderscore(cname) + " > ?";
        } else if (columnName.endsWith("Before")) {
            String cname = StringUtils.substringBeforeLast(columnName, "Before");
            return CommonUtils.upperCamelToLowerUnderscore(cname) + " < ?";
        } else if (columnName.endsWith("Between")) {
            String cname = StringUtils.substringBeforeLast(columnName, "Between");
            return CommonUtils.upperCamelToLowerUnderscore(cname) + " between ? and ?";
        } else {
            return CommonUtils.upperCamelToLowerUnderscore(columnName) + " = ?";
        }
    }
}

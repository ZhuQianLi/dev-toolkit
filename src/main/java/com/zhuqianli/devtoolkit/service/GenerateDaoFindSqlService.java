package com.zhuqianli.devtoolkit.service;

import com.zhuqianli.devtoolkit.utils.JavaLangUtils;
import com.zhuqianli.devtoolkit.utils.PsiAnnotationUtils;
import com.zhuqianli.devtoolkit.utils.StringUtils;
import com.google.common.base.MoreObjects;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.impl.source.PsiClassImpl;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
                findSql = findSql + ";";
                findSqls.add(MoreObjects.firstNonNull(findSql, "-- 不支持生成相关查询 方法名【" + method.getName() + "】"));
            } else if (JavaLangUtils.isDefaultModifier(method)) {
                findSqls.add("-- 不支持生成相关查询 方法名【" + method.getName() + "】");
            } else {
                String findSql = generateFindSqlByMethod(psiClass, method);
                findSqls.add(findSql);
            }
        }

        return String.join("\n", findSqls);
    }

    @Nullable
    private String generateFindSqlByMethod(PsiClassImpl psiClass, PsiMethod method) {
        // className: User -> tableName: user
        String className = psiClass.getName();
        String entityName = StringUtils.substringBefore(className, "Dao");
        String tableName = StringUtils.lowerCamelToLowerUnderscore(entityName);
        // methodName: findByNameAndAge
        String methodName = method.getName();

        Triple<String, String, String> triple = splitMethodName(methodName);
        String operate = triple.getLeft();
        String andFields = triple.getMiddle();
        String orderBy = triple.getRight();

        String whereSql = resolveWhereSql(andFields);
        String orderBySql = resolveOrderBySql(orderBy);

        if (operate.startsWith("findTop")) {
            String limitSize = StringUtils.substringAfterLast(operate, "findTop");
            limitSize = StringUtils.isNotEmpty(limitSize) ? limitSize : "1";
            return String.format("select * from %s %s %s limit %s;", tableName, whereSql, orderBySql, limitSize);
        }
        if (operate.startsWith("find")) {
            return String.format("select * from %s %s %s;", tableName, whereSql, orderBySql);
        }

        if (operate.startsWith("exists")) {
            return String.format("select * from %s %s %s limit 1;", tableName, whereSql, orderBySql);
        }

        if (operate.startsWith("count")) {
            return String.format("select count(*) from %s %s %s;", tableName, whereSql, orderBySql);
        }

        if (operate.startsWith("delete")) {
            return String.format("delete from %s %s %s;", tableName, whereSql, orderBySql);
        }

        throw new RuntimeException("解析方法名异常【" + methodName + "】");
    }

    /**
     * findTop100ByNameAndAgeOrderByIdDesc -> [findTop100, NameAndAge IdDesc]
     */
    private Triple<String, String, String> splitMethodName(String methonName) {
        String operate = StringUtils.substringBefore(methonName, "By");
        methonName = methonName.replace(operate + "By", StringUtils.EMPTY);
        if (methonName.contains("OrderBy")) {
            String[] splits = methonName.split("OrderBy");
            return Triple.of(operate, splits[0], splits[1]);
        } else {
            return Triple.of(operate, methonName, StringUtils.EMPTY);
        }
    }

    /**
     * IdDesc -> order by id desc
     */
    private String resolveOrderBySql(String orderBy) {
        if (StringUtils.isEmpty(orderBy)) {
            return StringUtils.EMPTY;
        }
        List<String> sorts = splitToSorts(orderBy);
        return "order by " + String.join(",", sorts);
    }

    /**
     * JobIdDescTimeAsc -> ["job_id desc","time asc"]
     */
    private List<String> splitToSorts(String orderBy) {
        List<String> sorts = new ArrayList<>();
        String[] split = orderBy.split("Asc|Desc");
        for (int i = 0; i < split.length; i++) {
            // 最后一个
            if (i + 1 == split.length) {
                String direction = StringUtils.substringAfterLast(orderBy, split[i]);
                direction = StringUtils.isNotEmpty(direction) ? direction : "Asc";
                sorts.add(StringUtils.upperCamelToLowerUnderscore(split[i]) + " " + direction.toLowerCase());
            } else {
                String direction = StringUtils.substringBetween(orderBy, split[i], split[i + 1]);
                direction = StringUtils.isNotEmpty(direction) ? direction : "Asc";
                sorts.add(StringUtils.upperCamelToLowerUnderscore(split[i]) + " " + direction.toLowerCase());
            }
        }
        return sorts;
    }

    /**
     * NameAndAge -> name = ? and age = ?
     */
    private String resolveWhereSql(String andFields) {
        if (StringUtils.isEmpty(andFields)) {
            return StringUtils.EMPTY;
        }
        String andSql = Arrays.stream(andFields.split("And")).map(this::resolveOneColumnSql).collect(Collectors.joining(" and "));
        return " where " + andSql + " ";
    }

    /**
     * JobIdIn -> job_id in (?)
     */
    private String resolveOneColumnSql(String columnName) {
        if (columnName.endsWith("In")) {
            String cname = StringUtils.substringBeforeLast(columnName, "In");
            return StringUtils.upperCamelToLowerUnderscore(cname) + " in (?)";
        } else if (columnName.endsWith("NotIn")) {
            String cname = StringUtils.substringBeforeLast(columnName, "NotIn");
            return StringUtils.upperCamelToLowerUnderscore(cname) + " not in (?)";
        } else if (columnName.endsWith("IsTrue")) {
            String cname = StringUtils.substringBeforeLast(columnName, "IsTrue");
            return StringUtils.upperCamelToLowerUnderscore(cname) + " = true";
        } else if (columnName.endsWith("IsFalse")) {
            String cname = StringUtils.substringBeforeLast(columnName, "IsFalse");
            return StringUtils.upperCamelToLowerUnderscore(cname) + " = false";
        } else if (columnName.endsWith("False")) {
            String cname = StringUtils.substringBeforeLast(columnName, "False");
            return StringUtils.upperCamelToLowerUnderscore(cname) + " = false";
        } else if (columnName.endsWith("True")) {
            String cname = StringUtils.substringBeforeLast(columnName, "True");
            return StringUtils.upperCamelToLowerUnderscore(cname) + " = false";
        } else if (columnName.endsWith("GreaterThanEqual")) {
            String cname = StringUtils.substringBeforeLast(columnName, "GreaterThanEqual");
            return StringUtils.upperCamelToLowerUnderscore(cname) + " >= ?";
        } else if (columnName.endsWith("LessThanEqual")) {
            String cname = StringUtils.substringBeforeLast(columnName, "LessThanEqual");
            return StringUtils.upperCamelToLowerUnderscore(cname) + " <= ?";
        } else if (columnName.endsWith("GreaterThan")) {
            String cname = StringUtils.substringBeforeLast(columnName, "GreaterThan");
            return StringUtils.upperCamelToLowerUnderscore(cname) + " > ?";
        } else if (columnName.endsWith("LessThan")) {
            String cname = StringUtils.substringBeforeLast(columnName, "LessThan");
            return StringUtils.upperCamelToLowerUnderscore(cname) + " < ?";
        } else if (columnName.endsWith("After")) {
            String cname = StringUtils.substringBeforeLast(columnName, "After");
            return StringUtils.upperCamelToLowerUnderscore(cname) + " > ?";
        } else if (columnName.endsWith("Before")) {
            String cname = StringUtils.substringBeforeLast(columnName, "Before");
            return StringUtils.upperCamelToLowerUnderscore(cname) + " < ?";
        } else if (columnName.endsWith("Between")) {
            String cname = StringUtils.substringBeforeLast(columnName, "Between");
            return StringUtils.upperCamelToLowerUnderscore(cname) + " between ? and ?";
        } else {
            return StringUtils.upperCamelToLowerUnderscore(columnName) + " = ?";
        }
    }
}

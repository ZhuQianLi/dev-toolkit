package com.darcy.zql.devtoolkit.service;

import com.darcy.zql.devtoolkit.utils.JavaLangUtils;
import com.darcy.zql.devtoolkit.utils.PsiAnnotationUtils;
import com.darcy.zql.devtoolkit.utils.PsiDocCommentUtils;
import com.darcy.zql.devtoolkit.utils.StringUtils;
import com.google.common.base.CaseFormat;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiType;
import org.apache.commons.compress.utils.Sets;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.EMPTY;

public class GenerateEntityTableSqlService {

    @NotNull
    public String generateTableSql(AnActionEvent event) {
        DataContext dataContext = event.getDataContext();
        PsiElement psiElement = CommonDataKeys.PSI_ELEMENT.getData(dataContext);
        PsiClass psiClass = (PsiClass) psiElement;
        assert psiClass != null;


        StringBuilder tableSql = new StringBuilder();

        String tableName = buildTableName(psiClass);
        tableSql.append(String.format("create table %s (\n", tableName));

        List<PsiField> allFields = sortFields(psiClass.getAllFields());
        for (PsiField field : allFields) {
            if (JavaLangUtils.isStaticModifier(field)) {
                continue;
            }
            tableSql.append(buildSqlForOneColumn(field));
        }

        String primaryKey = "primary key (`id`)";
        // 支持`@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"userId", "outerId"})})`
        String uniqueKey = buildSqlForUniqueKey(psiClass, tableName);
        tableSql.append(Lists.newArrayList(primaryKey, uniqueKey).stream().filter(StringUtils::isNotEmpty).collect(Collectors.joining(",\n")));

        tableSql.append(")\n");

        String tableComment = buildTableComment(psiClass);
        tableSql.append(String.format("ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC %s;\n", tableComment));
        return tableSql.toString();
    }

    private List<PsiField> sortFields(PsiField[] allFields) {
        Set<String> sortFirstFields = Sets.newHashSet("id", "version", "dxCreated", "dxModified");
        List<PsiField> firstFields = Arrays.stream(allFields).filter(field -> sortFirstFields.contains(field.getName())).collect(Collectors.toList());
        List<PsiField> lastFields = Arrays.stream(allFields).filter(field -> !sortFirstFields.contains(field.getName())).collect(Collectors.toList());
        return Lists.newArrayList(Iterables.concat(firstFields, lastFields));
    }

    /**
     * 获取class第一行注释，作为表注释
     */
    private String buildTableComment(PsiClass psiClass) {
        List<String> classComments = PsiDocCommentUtils.extractCommentDescription(psiClass);
        return classComments.isEmpty() ? EMPTY : String.format("comment='%s'", classComments.get(0));
    }

    private String buildSqlForUniqueKey(PsiClass psiClass, String tableName) {
        List<String> uniqueKey = PsiAnnotationUtils.extractUniqueKey(psiClass);
        if (uniqueKey.isEmpty()) {
            return EMPTY;
        }
        List<String> tableNameAndColumnNames = Lists.newArrayList(tableName);
        tableNameAndColumnNames.addAll(uniqueKey);
        String uniqueKeyName = tableNameAndColumnNames.stream().map(StringUtils::extractWordFirstChar).collect(Collectors.joining("_"));

        String columnNames = uniqueKey.stream().map(k -> String.format("`%s`", StringUtils.lowerCamelToLowerUnderscore(k))).collect(Collectors.joining(","));
        return String.format("unique key `uk_%s` (%s)", uniqueKeyName, columnNames);
    }

    private String buildTableName(PsiClass psiClass) {
        String className = psiClass.getName();
        assert className != null;
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, className);
    }

    private String buildSqlForOneColumn(PsiField field) {
        String fieldName = field.getName();
        String field_name = String.format("`%s`", StringUtils.lowerCamelToLowerUnderscore(fieldName));

        String mysqlType = psiTypeToMysqlType(field);
        // 支持`@Nullable`
        String nullable = PsiAnnotationUtils.existsNullable(field) ? "default null" : "not null";
        // 支持`@GeneratedValue(strategy = GenerationType.IDENTITY)`
        String autoIncrement = PsiAnnotationUtils.existsGeneratedValue(field) ? "auto_increment" : EMPTY;
        // 支持注释、支持`@see`
        String comment = buildColumnComment(field);
        // trade_id bigint unsigned not null auto_increment comment "ref:trade.tid, 订单id"
        return String.format("%s %s %s %s %s,\n", field_name, mysqlType, nullable, autoIncrement, comment);
    }

    private String buildColumnComment(PsiField field) {
        List<String> commentDescriptions = PsiDocCommentUtils.extractCommentDescription(field);
        if (isNotEmpty(commentDescriptions)) {
            List<String> atSeeTagValues = PsiDocCommentUtils.extractCommentTagForAtSee(field);
            if (isNotEmpty(atSeeTagValues)) {
                // comment "ref:trade.tid, 订单id"
                return String.format("comment 'ref:%s, %s'", atSeeTagValueToMysqlComment(atSeeTagValues.get(0)), commentDescriptions.get(0));
            } else {
                // comment "订单id"
                return String.format("comment '%s'", commentDescriptions.get(0));
            }
        }
        return EMPTY;
    }

    /**
     * com.test.Trade#tid -> trade.tid
     * Trade#tid -> trade.tid
     */
    private String atSeeTagValueToMysqlComment(String atSeeTagsValue) {
        // com.test.Trade#tid -> com.test.Trade + tid
        String[] split = atSeeTagsValue.split("#");
        // com.test.Trade -> Trade
        // Trade -> Trade
        String className = split[0].contains(".") ? StringUtils.substringAfterLast(split[0], ".") : split[0];
        // Trade + tid -> trade.tid
        String luClassName = StringUtils.upperCamelToLowerUnderscore(className);
        String luFieldName = StringUtils.lowerCamelToLowerUnderscore(split[1]);
        return luClassName + "." + luFieldName;
    }

    private String psiTypeToMysqlType(PsiField field) {
        PsiType psiType = field.getType();
        if (psiType.getPresentableText().equals("String")) {
            return "varchar(255)";
        } else if (psiType.getPresentableText().equals("LocalDateTime")) {
            return "datetime";
        } else if (psiType.getPresentableText().equals("Boolean")) {
            return "tinyint unsigned";
        } else if (psiType.getPresentableText().equals("Integer")) {
            return "int unsigned";
        } else if (psiType.getPresentableText().equals("Long")) {
            return "bigint unsigned";
        } else if (psiType.getPresentableText().equals("BigDecimal")) {
            return "decimal(19, 2)";
        } else if (JavaLangUtils.isEnumType(psiType)) {
            return "varchar(50)";
        } else if (PsiAnnotationUtils.existsJsonType(field)) {
            // 支持`@Type(type = "Json")`
            return "text";
        }
        return "javaType:" + psiType.getPresentableText();
    }

}

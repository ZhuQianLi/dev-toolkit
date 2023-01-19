package com.darcy.zql.devtoolkit.action;

import com.darcy.zql.devtoolkit.utils.Utils;
import com.google.common.base.CaseFormat;
import com.google.common.collect.Lists;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiType;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.List;
import java.util.stream.Collectors;

import static com.darcy.zql.devtoolkit.utils.Utils.*;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.EMPTY;

public class GenerateEntityTableSqlAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getProject();
        DataContext dataContext = event.getDataContext();
        PsiElement psiElement = CommonDataKeys.PSI_ELEMENT.getData(dataContext);
        PsiClass psiClass = (PsiClass) psiElement;
        assert psiClass != null;

        String tableSql = generateTableSql(psiClass);

        copyToClipboard(tableSql);

        showMessage(project, "生成表结构成功", "已复制到剪贴板");
    }

    @NotNull
    private String generateTableSql(PsiClass psiClass) {
        StringBuilder tableSql = new StringBuilder();

        String tableName = buildTableName(psiClass);
        tableSql.append(String.format("create table %s (\n", tableName));

        for (PsiField field : psiClass.getFields()) {
            if (isStaticModifier(field)) {
                continue;
            }
            tableSql.append(buildSqlForOneColumn(field));
        }

        String primaryKey = "primary key (`id`)";
        // 支持`@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"userId", "outerId"})})`
        String uniqueKey = buildSqlForUniqueKey(psiClass, tableName);
        tableSql.append(String.join(",\n", Lists.newArrayList(primaryKey, uniqueKey)));

        tableSql.append(")\n");

        String tableComment = buildTableComment(psiClass);
        tableSql.append(String.format(
                "ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci ROW_FORMAT=DYNAMIC %s;\n", tableComment));
        tableSql.append(String.format(
                "ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC %s;\n",
                tableComment));
        return tableSql.toString();
    }

    private String buildTableComment(PsiClass psiClass) {
        List<String> classComments = extractCommentDescription(psiClass);
        return classComments.isEmpty() ? EMPTY : String.format("comment='%s'", classComments.get(0));
    }

    private String buildSqlForUniqueKey(PsiClass psiClass, String tableName) {
        List<String> uniqueKey = extractUniqueKey(psiClass);
        if (uniqueKey.isEmpty()) {
            return EMPTY;
        }
        List<String> tableNameAndColumnNames = Lists.newArrayList(tableName);
        tableNameAndColumnNames.addAll(uniqueKey);
        String uniqueKeyName = tableNameAndColumnNames.stream().map(Utils::extractWordFirstChar)
                .collect(Collectors.joining("_"));

        String columnNames = uniqueKey.stream()
                .map(k -> String.format("`%s`", lowerCamelToLowerUnderscore(k)))
                .collect(Collectors.joining(","));
        return String.format("unique key `uk_%s` (%s)", uniqueKeyName, columnNames);
    }

    private String buildTableName(PsiClass psiClass) {
        String className = psiClass.getName();
        assert className != null;
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, className);
    }

    private String buildSqlForOneColumn(PsiField field) {
        String fieldName = field.getName();
        String field_name = String.format("`%s`", lowerCamelToLowerUnderscore(fieldName));

        String mysqlType = psiTypeToMysqlType(field);
        // 支持`@Nullable`
        String nullable = existsNullable(field) ? "default null" : "not null";
        // 支持`@GeneratedValue(strategy = GenerationType.IDENTITY)`
        String autoIncrement = existsGeneratedValue(field) ? "auto_increment" : EMPTY;
        // 支持注释、支持`@see`
        String comment = buildColumnComment(field);
        // trade_id bigint unsigned not null auto_increment comment "ref:trade.tid, 订单id"
        return String.format("%s %s %s %s %s,\n",
                field_name, mysqlType, nullable, autoIncrement, comment
        );
    }

    private String buildColumnComment(PsiField field) {
        List<String> commentDescriptions = extractCommentDescription(field);
        if (isNotEmpty(commentDescriptions)) {
            List<String> atSeeTagValues = extractCommentTagForAtSee(field);
            if (isNotEmpty(atSeeTagValues)) {
                // comment "ref:trade.tid, 订单id"
                return String.format("comment 'ref:%s, %s'",
                        atSeeTagValueToMysqlComment(atSeeTagValues.get(0)),
                        commentDescriptions.get(0));
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
        return upperCamelToLowerUnderscore(className) + "." + lowerCamelToLowerUnderscore(split[1]);
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
        } else if (isEnumType(psiType)) {
            return "varchar(50)";
        } else if (existsJsonType(field)) {
            // 支持`@Type(type = "Json")`
            return "text";
        }
        return "javaType:" + psiType.getPresentableText();
    }

}

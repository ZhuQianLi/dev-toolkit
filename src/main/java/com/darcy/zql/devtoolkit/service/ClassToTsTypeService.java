package com.darcy.zql.devtoolkit.service;

import com.darcy.zql.devtoolkit.java.lang.JavaClass;
import com.darcy.zql.devtoolkit.java.lang.JavaClassLoader;
import com.darcy.zql.devtoolkit.java.lang.JavaField;
import com.darcy.zql.devtoolkit.java.lang.JavaType;
import com.darcy.zql.devtoolkit.utils.StreamUtils;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.stream.Collectors;

public class ClassToTsTypeService {

    public String generate(JavaClassLoader classLoader, String className) {
        List<JavaClass> classes = resolveNeedGenerateClassDeeply(classLoader, className);
        classes = StreamUtils.filter(classes, StreamUtils.distinctByKey(JavaClass::getName));

        return classes.stream().map(this::generateByClass).collect(Collectors.joining("\n"));
    }

    private List<JavaClass> resolveNeedGenerateClassDeeply(JavaClassLoader classLoader, String className) {
        Preconditions.checkNotNull(className);
        List<JavaClass> classes = Lists.newArrayList();

        JavaClass rootClass = Preconditions.checkNotNull(classLoader.forName(className));
        classes.add(rootClass);

        for (JavaField field : rootClass.getFields()) {
            if (field.isStatic()) {
                continue;
            }
            JavaType type = field.getType();
            if (type.isCollection()) {
                type = type.getCollectionParameterType();
            }
            if (type.isJavaBaseType()) {
                continue;
            }
            List<JavaClass> fieldClasses = resolveNeedGenerateClassDeeply(classLoader, type.getName());
            classes.addAll(fieldClasses);
        }

        return classes;
    }

    private String generateByClass(JavaClass clazz) {
        if (clazz.isEnum()) {
            String enumNames = clazz.getEnumConstants().stream().map(enumConstant -> "'" + enumConstant.getName() + "'").collect(Collectors.joining(" | "));
            return "export type " + clazz.getSimpleName() + " = " + enumNames + "\n";
        } else {
            StringBuilder code = new StringBuilder();
            code.append("export interface ").append(clazz.getSimpleName()).append(" {\n");
            for (JavaField field : clazz.getFields()) {
                if (field.isStatic()) {
                    continue;
                }
                code.append(generateByField(field));
            }
            code.append("}\n");
            return code.toString();
        }
    }

    private String generateByField(JavaField field) {
        String fieldName = field.getName();
        String nullable = field.isNullable() ? "?" : "";
        String typeName = generateByType(field.getType());
        return "  " + fieldName + nullable + ": " + typeName + ";\n";
    }

    private String generateByType(JavaType type) {
        if (type.isString()) {
            return "string";
        } else if (type.isLocalDate()) {
            return "string";
        } else if (type.isLocalDateTime()) {
            return "string";
        } else if (type.isBoolean()) {
            return "boolean";
        } else if (type.isInteger()) {
            return "number";
        } else if (type.isLong()) {
            return "string";
        } else if (type.isBigDecimal()) {
            return "string";
        } else if (type.isCollection()) {
            JavaType parameterType = type.getCollectionParameterType();
            return generateByType(parameterType) + "[]";
        } else if (type.isCustom()) {
            return type.getSimpleName();
        }
        return "不支持" + type.getName();
    }

}

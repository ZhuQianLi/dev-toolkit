package com.zhuqianli.devtoolkit.service;

import com.zhuqianli.devtoolkit.java.lang.*;

import java.util.ArrayList;
import java.util.List;

import static com.zhuqianli.devtoolkit.utils.StringUtils.lowerCamelToUpperCamel;
import static com.zhuqianli.devtoolkit.utils.StringUtils.upperCamelToLowerCamel;
import static java.lang.String.format;

public class GenerateObjectConvertCodeService {

    public List<String> generateMethodCode(JavaClassLoader classLoader, JavaMethod method) {
        List<String> codes = new ArrayList<>();

        JavaClass targetClass = classLoader.forName(method.getReturnType().getName());
        assert targetClass != null;
        String targetClassName = targetClass.getName();
        String targetVariable = upperCamelToLowerCamel(targetClassName);
        codes.add(format("%s %s = new %s();", targetClassName, targetVariable, targetClassName));

        for (JavaField targetField : targetClass.getFields()) {
            if (targetField.isStatic()) {
                continue;
            }
            codes.add(generateSetFieldCode(classLoader, method, targetField));
        }

        return codes;
    }

    private String generateSetFieldCode(JavaClassLoader classLoader, JavaMethod method, JavaField targetField) {
        JavaType returnType = method.getReturnType();
        assert returnType != null;
        JavaClass targetClass = classLoader.forName(returnType.getName());
        assert targetClass != null;
        String targetVariable = upperCamelToLowerCamel(targetClass.getName());

        String targetFieldName = targetField.getName();
        for (JavaParameter sourceParameter : method.getParameters()) {
            if (sourceParameter.getType().isPrimitive()) {
                if (sourceParameter.getName().equals(targetFieldName)) {
                    return format("%s.set%s(%s);", targetVariable, lowerCamelToUpperCamel(targetFieldName),
                            sourceParameter.getName());
                }
            } else {
                JavaClass sourceClass = classLoader.forName(sourceParameter.getType().getName());
                assert sourceClass != null;
                for (JavaField sourceField : sourceClass.getFields()) {
                    if (sourceField.isStatic()) {
                        continue;
                    }
                    String sourceFieldName = sourceField.getName();
                    if (sourceFieldName.equals(targetFieldName)) {
                        return format("%s.set%s(%s.get%s());", targetVariable, lowerCamelToUpperCamel(targetFieldName),
                                sourceParameter.getName(), lowerCamelToUpperCamel(sourceFieldName));
                    }
                }
            }
        }
        return format("%s.set%s();", targetVariable, lowerCamelToUpperCamel(targetFieldName));
    }

}

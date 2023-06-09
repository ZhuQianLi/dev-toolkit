package com.darcy.zql.devtoolkit.service;

import com.darcy.zql.devtoolkit.utils.JavaLangUtils;
import com.darcy.zql.devtoolkit.utils.PsiClassUtils;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;

import java.util.ArrayList;
import java.util.List;

import static com.darcy.zql.devtoolkit.utils.StringUtils.lowerCamelToUpperCamel;
import static com.darcy.zql.devtoolkit.utils.StringUtils.upperCamelToLowerCamel;
import static java.lang.String.format;

public class GenerateObjectConvertCodeService {

    public List<String> generateMethodCode(Project project, PsiMethod psiMethod) {
        List<String> codes = new ArrayList<>();

        PsiType returnType = psiMethod.getReturnType();
        assert returnType != null;
        PsiClass targetClass = PsiClassUtils.getPsiClassByPsiType(project, returnType);
        String targetClassName = targetClass.getName();
        String targetVariable = upperCamelToLowerCamel(targetClassName);
        codes.add(format("%s %s = new %s();", targetClassName, targetVariable, targetClassName));

        for (PsiField targetField : targetClass.getFields()) {
            if (JavaLangUtils.isStaticModifier(targetField)) {
                continue;
            }
            codes.add(generateSetFieldCode(project, psiMethod, targetField));
        }

        return codes;
    }

    private String generateSetFieldCode(Project project, PsiMethod psiMethod, PsiField targetField) {
        PsiType returnType = psiMethod.getReturnType();
        assert returnType != null;
        PsiClass targetClass = PsiClassUtils.getPsiClassByPsiType(project, returnType);
        String targetVariable = upperCamelToLowerCamel(targetClass.getName());

        String targetFieldName = targetField.getName();
        for (PsiParameter sourceParameter : psiMethod.getParameterList().getParameters()) {
            if (JavaLangUtils.isJavaBaseType(sourceParameter.getType())) {
                if (sourceParameter.getName().equals(targetFieldName)) {
                    return format("%s.set%s(%s);", targetVariable, lowerCamelToUpperCamel(targetFieldName),
                            sourceParameter.getName());
                }
            } else {
                PsiClass sourceClass = PsiClassUtils.getPsiClassByPsiType(project, sourceParameter.getType());
                for (PsiField sourceField : sourceClass.getFields()) {
                    if (JavaLangUtils.isStaticModifier(sourceField)) {
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

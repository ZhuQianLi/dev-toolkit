package com.zhuqianli.devtoolkit.java.lang.psi;

import com.intellij.lang.jvm.annotation.JvmAnnotationAttribute;
import com.intellij.lang.jvm.annotation.JvmAnnotationAttributeValue;
import com.intellij.lang.jvm.annotation.JvmAnnotationConstantValue;
import com.intellij.psi.PsiAnnotation;
import com.zhuqianli.devtoolkit.java.lang.JavaAnnotation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PsiAnnotationImpl implements JavaAnnotation {

    PsiAnnotation psiAnnotation;

    public PsiAnnotationImpl(PsiAnnotation psiAnnotation) {
        this.psiAnnotation = psiAnnotation;
    }

    @Override
    public String getName() {
        return psiAnnotation.getQualifiedName();
    }

    @Override
    public Map<String, Object> getAttributeNameValueMap() {
        List<JvmAnnotationAttribute> attributes = psiAnnotation.getAttributes();
        Map<String, Object> result = new HashMap<>();
        for (JvmAnnotationAttribute attribute : attributes) {
            String attributeName = attribute.getAttributeName();
            JvmAnnotationAttributeValue attributeValue = attribute.getAttributeValue();
            if (attributeValue instanceof JvmAnnotationConstantValue) {
                Object constantValue = ((JvmAnnotationConstantValue) attribute.getAttributeValue()).getConstantValue();
                result.put(attributeName, constantValue);
            }
        }
        return result;
    }

}

package com.zhuqianli.devtoolkit.java.lang;

import java.util.List;

public interface JavaField {

    String getName();

    JavaType getType();

    JavaDocComment getDocComment();

    boolean isStatic();

    boolean isNullable();

    List<JavaAnnotation> getAnnotations();

    boolean existAnnotation(String annotation);

}

package com.zhuqianli.devtoolkit.java.lang;

public interface JavaField {

    String getName();

    JavaType getType();

    boolean isStatic();

    boolean isNullable();

    boolean existAnnotation(String annotation);

}

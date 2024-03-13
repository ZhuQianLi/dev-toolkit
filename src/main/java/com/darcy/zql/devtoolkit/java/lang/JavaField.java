package com.darcy.zql.devtoolkit.java.lang;

public interface JavaField {

    String getName();

    JavaType getType();

    boolean isStatic();

    boolean isNullable();

}

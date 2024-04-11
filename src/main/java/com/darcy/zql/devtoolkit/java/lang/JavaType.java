package com.darcy.zql.devtoolkit.java.lang;

public interface JavaType {

    String getName();

    String getSimpleName();

    JavaType getGenericsType();

    boolean isString();

    boolean isLocalDate();

    boolean isLocalDateTime();

    boolean isBoolean();

    boolean isInteger();

    boolean isLong();

    boolean isBigDecimal();

    boolean isCollection();

    boolean isEnum();

    boolean isCustom();

    boolean isPrimitive();

}

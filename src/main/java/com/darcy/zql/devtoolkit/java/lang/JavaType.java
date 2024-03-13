package com.darcy.zql.devtoolkit.java.lang;

public interface JavaType {

    String getName();

    String getSimpleName();

    boolean isString();

    boolean isLocalDate();

    boolean isLocalDateTime();

    boolean isBoolean();

    boolean isInteger();

    boolean isLong();

    boolean isBigDecimal();

    boolean isCollection();

    JavaType getCollectionParameterType();

    boolean isCustom();

    boolean isJavaBaseType();

}

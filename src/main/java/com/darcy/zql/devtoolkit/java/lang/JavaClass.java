package com.darcy.zql.devtoolkit.java.lang;

import java.util.List;

public interface JavaClass {

    String getSimpleName();

    String getName();

    List<JavaField> getFields();

    boolean isEnum();

    List<JavaEnumConstant> getEnumConstants();

}

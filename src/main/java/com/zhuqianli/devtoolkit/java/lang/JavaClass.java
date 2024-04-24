package com.zhuqianli.devtoolkit.java.lang;

import java.util.List;

public interface JavaClass {

    String getSimpleName();

    String getName();

    List<JavaField> getFields();

    List<JavaMethod> getMethods();

    JavaDocComment getDocComment();

    boolean isEnum();

    List<JavaEnumConstant> getEnumConstants();

}

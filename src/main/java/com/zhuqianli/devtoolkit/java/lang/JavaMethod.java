package com.zhuqianli.devtoolkit.java.lang;

import java.util.List;

public interface JavaMethod {

    String getName();

    JavaType getReturnType();

    List<JavaParameter> getParameters();

    boolean isDefault();

    boolean isStatic();

}

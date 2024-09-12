package com.zhuqianli.devtoolkit.java.lang;

import org.jetbrains.annotations.Nullable;

public interface JavaClassLoader {

    @Nullable
    JavaClass forName(String name);

}

package com.zhuqianli.devtoolkit.java.lang;

import javax.annotation.Nullable;

public interface JavaClassLoader {

    @Nullable
    JavaClass forName(String name);

}

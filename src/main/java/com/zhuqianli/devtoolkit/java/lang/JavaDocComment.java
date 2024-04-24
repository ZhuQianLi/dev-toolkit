package com.zhuqianli.devtoolkit.java.lang;

import java.util.List;

public interface JavaDocComment {

    List<String> getDescContent();

    List<String> getTagContent(String tagName);

}

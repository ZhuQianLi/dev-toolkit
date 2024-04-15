package com.zhuqianli.devtoolkit.valueobject;

import com.intellij.psi.PsiType;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum JavaPrimitiveType {
    String("java.lang.String"),
    LocalDate("java.time.LocalDate"),
    LocalDateTime("java.time.LocalDateTime"),
    LocalTime("java.time.LocalTime"),
    Boolean("java.lang.Boolean"),
    primitive_boolean("boolean"),
    Integer("java.lang.Integer"),
    primitive_int("int"),
    Long("java.lang.Long"),
    primitive_long("long"),
    BigDecimal("java.math.BigDecimal"),
    ;

    public static final Map<String, JavaPrimitiveType> CANONICAL_TEXT_MAP =
            Arrays.stream(JavaPrimitiveType.values()).collect(Collectors.toMap(JavaPrimitiveType::getCanonicalText, Function.identity()));

    /**
     * @see PsiType#getCanonicalText()
     */
    private final String canonicalText;

    JavaPrimitiveType(String canonicalText) {
        this.canonicalText = canonicalText;
    }

    public String getCanonicalText() {
        return canonicalText;
    }

}

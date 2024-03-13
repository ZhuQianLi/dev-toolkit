package com.darcy.zql.devtoolkit.valueobject;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum JavaBaseType {
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

    public static final Map<String, JavaBaseType> CANONICAL_TEXT_MAP =
            Arrays.stream(JavaBaseType.values()).collect(Collectors.toMap(JavaBaseType::getCanonicalText, Function.identity()));

    private final String canonicalText;

    JavaBaseType(String canonicalText) {
        this.canonicalText = canonicalText;
    }

    public String getCanonicalText() {
        return canonicalText;
    }

}

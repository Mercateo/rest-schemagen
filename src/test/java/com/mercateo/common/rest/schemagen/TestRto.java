package com.mercateo.common.rest.schemagen;

import java.math.BigDecimal;
import java.util.List;

import com.mercateo.common.rest.schemagen.plugin.PropertySchema;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@SuppressWarnings("unused")
public class TestRto {

    public static enum ENUM {
        VALUE_1, VALUE_2
    }

    public class NestedThing {
        private NestedThing nextThing;

        private String thisThing;
    }

    public String string;

    @NotNull
    public String requiredString;

    @IgnoreInRestSchema
    public String ignoredString;

    @Size(min = 5, max = 8)
    public String constrainedString;

    @Pattern(regexp = "^(the )?pattern$")
    public String patternString;

    public int integer;

    public BigDecimal bigDecimal;

    public boolean bool;

    public Boolean boxedBool;

    @PropertySchema(schemaGenerator = TestSchemaGenerator.class)
    public Object hasOwnGenerator;

    public ENUM anEnum;

    public List<List<String>> twoDimStringArray;

    public TestRto recursiveElement;

    public NestedThing nestedRecursiveElement;
}

package com.fasterxml.jackson.databind.jsontype.impl;

import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;

/**
 * Simple {@link PolymorphicTypeValidator} implementation used by {@link StdTypeResolverBuilder}
 * in cases where all subtypes for given base type are deemeded acceptable; usually because
 * user controls base type in question (and no serialization gadgets should exist).
 */
final class LaissezFaireSubTypeValidator
    extends PolymorphicTypeValidator.Base
{
    private static final long serialVersionUID = 1L;

    public final static LaissezFaireSubTypeValidator instance = new LaissezFaireSubTypeValidator(); 

    @Override
    public Validity validateBaseType(DatabindContext ctxt, JavaType baseType) {
        return Validity.INDETERMINATE;
    }

    @Override
    public Validity validateSubClassName(DatabindContext ctxt,
            JavaType baseType, String subClassName) {
        return Validity.ALLOWED;
    }

    @Override
    public Validity validateSubType(DatabindContext ctxt, JavaType baseType,
            JavaType subType) {
        return Validity.ALLOWED;
    }
}

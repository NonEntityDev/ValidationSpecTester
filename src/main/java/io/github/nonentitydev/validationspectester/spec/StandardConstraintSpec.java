/*
 * The MIT License
 *
 *   Copyright (c) 2025, Andre Silva (contact.nonentity@tutamail.com)
 *
 *   Permission is hereby granted, free of charge, to any person obtaining a copy
 *   of this software and associated documentation files (the "Software"), to deal
 *   in the Software without restriction, including without limitation the rights
 *   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   copies of the Software, and to permit persons to whom the Software is
 *   furnished to do so, subject to the following conditions:
 *
 *   The above copyright notice and this permission notice shall be included in
 *   all copies or substantial portions of the Software.
 *
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *   THE SOFTWARE.
 */
package io.github.nonentitydev.validationspectester.spec;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.objenesis.ObjenesisStd;

import java.lang.reflect.Field;

public final class StandardConstraintSpec implements AssertConstraintSpec {

    private final Object beanInstance;
    private final Validator validator;
    private Field field;
    private Class<?>[] groups;

    private StandardConstraintSpec() {
        throw new UnsupportedOperationException(
                "This class was not meant to be instantiated using its default constructor.");
    }

    private StandardConstraintSpec(Object beanInstance, Validator validator) {
        this.beanInstance = beanInstance;
        this.validator = validator;
    }

    public static AssertConstraintSpec givenBeanType(Class<?> beanType, Validator validator) {
        Object beanInstance = new ObjenesisStd().newInstance(beanType);
        return new StandardConstraintSpec(beanInstance, validator);
    }

    public static AssertConstraintSpec givenBeanType(Class<?> beanType) {
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            return givenBeanType(beanType, validatorFactory.getValidator());
        }
    }

    @Override
    public AssertConstraintSpec field(String fieldName) {
        this.field = FieldUtils.getField(this.beanInstance.getClass(), fieldName, true);
        return this;
    }

    @Override
    public AssertConstraintSpec withGroups(Class<?>... groups) {
        this.groups = groups;
        return this;
    }

    @Override
    public AssertConstraintSpec withNoGroups() {
        this.groups = null;
        return this;
    }
}

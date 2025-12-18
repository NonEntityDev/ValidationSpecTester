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

import io.github.nonentitydev.validationspectester.spec.asserts.BooleanConstraintsAssertion;
import io.github.nonentitydev.validationspectester.spec.asserts.CommonConstraintsAssertion;
import io.github.nonentitydev.validationspectester.spec.reflection.ReflectionHelper;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.lang.reflect.Field;
import java.util.function.Supplier;

public final class StandardConstraintSpec implements AssertConstraintSpec {

  private final Object beanInstance;
  private final Validator validator;
  private Field field;
  private Class<?>[] groups = new Class<?>[] {};
  private Supplier<Object> fieldInstanceSupplier;

  private StandardConstraintSpec() {
    throw new UnsupportedOperationException(
        "This class was not meant to be instantiated using its default constructor.");
  }

  private StandardConstraintSpec(Object beanInstance, Validator validator) {
    this.beanInstance = beanInstance;
    this.validator = validator;
  }

  public static AssertConstraintSpec givenBeanType(Class<?> beanType, Validator validator) {
    Object beanInstance = ReflectionHelper.newInstanceOfType(beanType);
    return new StandardConstraintSpec(beanInstance, validator);
  }

  public static AssertConstraintSpec givenBeanType(Class<?> beanType) {
    try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
      return givenBeanType(beanType, validatorFactory.getValidator());
    }
  }

  /** {@inheritDoc} */
  @Override
  public AssertConstraintSpec field(String fieldName) {
    this.field = ReflectionHelper.getFieldByName(this.beanInstance.getClass(), fieldName);
    this.fieldInstanceSupplier = () -> ReflectionHelper.newInstanceOfFieldType(this.field);
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public AssertConstraintSpec field(String fieldName, Supplier<Object> newInstanceSupplier) {
    this.field = ReflectionHelper.getFieldByName(this.beanInstance.getClass(), fieldName);
    this.fieldInstanceSupplier = newInstanceSupplier;
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public AssertConstraintSpec withGroups(Class<?>... groups) {
    this.groups = groups;
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public AssertConstraintSpec withNoGroups() {
    this.groups = new Class<?>[] {};
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public AssertConstraintSpec shouldNotBeNull(String expectedMessage, boolean exactMatch) {
    CommonConstraintsAssertion.shouldNotBeNull(
        this.beanInstance,
        this.field,
        this.fieldInstanceSupplier,
        this.validator,
        this.groups,
        expectedMessage,
        exactMatch);
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public AssertConstraintSpec shouldNotBeNull() {
    this.shouldNotBeNull("must not be null", true);
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public AssertConstraintSpec canBeNull(
      String expectedMessageNegativeScenario, boolean exactMatch) {
    CommonConstraintsAssertion.canBeNull(
        this.beanInstance,
        this.field,
        this.validator,
        this.groups,
        expectedMessageNegativeScenario,
        exactMatch);
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public AssertConstraintSpec canBeNull() {
    return this.canBeNull("must not be null", true);
  }

  /** {@inheritDoc} */
  @Override
  public AssertConstraintSpec shouldBeValidFor(Object value) {
    CommonConstraintsAssertion.shouldBeValidFor(
        this.beanInstance, this.field, value, this.validator, this.groups);
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public AssertConstraintSpec shouldBeTrue(String expectedMessage, boolean exactMatch) {
    BooleanConstraintsAssertion.shouldBe(
        this.beanInstance,
        this.field,
        Boolean.TRUE,
        expectedMessage,
        exactMatch,
        this.validator,
        this.groups);
    return null;
  }

  /** {@inheritDoc} */
  @Override
  public AssertConstraintSpec shouldBeTrue() {
    return this.shouldBeTrue("must be true", true);
  }

  /** {@inheritDoc} */
  @Override
  public AssertConstraintSpec shouldBeFalse(String expectedMessage, boolean exactMatch) {
    BooleanConstraintsAssertion.shouldBe(
        this.beanInstance,
        this.field,
        Boolean.FALSE,
        expectedMessage,
        exactMatch,
        this.validator,
        this.groups);
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public AssertConstraintSpec shouldBeFalse() {
    return this.shouldBeFalse("must be false", true);
  }
}

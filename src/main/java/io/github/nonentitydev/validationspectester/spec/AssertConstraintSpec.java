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

import java.util.function.Supplier;

/**
 * Establishes the common behaviour for any implementation of the helper class to assert validation
 * constraints of a bean type. The main goal is to have a class to help to assert the constraints
 * defined in a bean type by defining a "test by specification" DSL and eliminate the requirement of
 * scenario arrangement steps.
 */
public interface AssertConstraintSpec {

  /**
   * Defines the field that the assertions following this method will be asserting against.
   *
   * @param fieldName Name of the field to be tested.
   * @return Same object instance, providing a fluid api.
   */
  AssertConstraintSpec field(String fieldName);

  /**
   * Defines the field that the assertions following this method will be asserting against. Any
   * assertion that requires creating new instances of the field's type will use the provided
   * supplier.
   *
   * @param fieldName Name of the field to be tested.
   * @param newInstanceSupplier Supplier to create new instances of the field's type.
   * @return Same object instance, providing a fluid api.
   */
  AssertConstraintSpec field(String fieldName, Supplier<Object> newInstanceSupplier);

  /**
   * Defines validation group(s) that will be applied to every assertion following the invocation of
   * this method.
   *
   * @param groups Validation groups to be applied to following assertions.
   * @return Same object instance, providing a fluid api.
   */
  AssertConstraintSpec withGroups(Class<?>... groups);

  /**
   * Defines that no validation group will be applied to assertions following the invocation of this
   * method.
   *
   * @return Same object instance, providing a fluid api.
   */
  AssertConstraintSpec withNoGroups();

  /**
   * Asserts that the field won't accept null values.
   *
   * @param expectedMessage Expected constraint violation message.
   * @param exactMatch Flag if an exact match should be performed. Otherwise, either a substring or
   *     regex match will be performed.
   * @return Same object instance, providing a fluid api.
   */
  AssertConstraintSpec shouldNotBeNull(String expectedMessage, boolean exactMatch);

  /**
   * Asserts that the field won't accept null values. This method will look up by an exact match of
   * the default message 'must not be null'.
   *
   * @return Same object instance, providing a fluid api.
   */
  AssertConstraintSpec shouldNotBeNull();

  /**
   * Asserts that the field can accept null values.
   *
   * @param expectedMessageNegativeScenario Expected constraint violation message in case the
   *     assertion fails.
   * @param exactMatch Flag if an exact match should be performed. Otherwise, either a substring or
   *     regex match will be performed.
   * @return Same object instance, providing a fluid api.
   */
  AssertConstraintSpec canBeNull(String expectedMessageNegativeScenario, boolean exactMatch);

  /**
   * Asserts that the field can accept null values. This method will look up by an exact match of
   * the default message 'must not be null' in case the assertion fails.
   *
   * @return Same object instance, providing a fluid api.
   */
  AssertConstraintSpec canBeNull();

  /**
   * Asserts that the given value is valid for the current field under test. After set the received
   * value to the field, no constraint violations should be raised.
   *
   * @param value Value to be tested for validity.
   * @return Same object instance, providing a fluid api.
   */
  AssertConstraintSpec shouldBeValidFor(Object value);

  /**
   * Asserts that the field should be true (for boolean fields).
   *
   * @param expectedMessage Expected constraint violation message.
   * @param exactMatch Flag if an exact match should be performed. Otherwise, either a substring or
   *     regex match will be performed.
   * @return Same object instance, providing a fluid api.
   */
  AssertConstraintSpec shouldBeTrue(String expectedMessage, boolean exactMatch);

  /**
   * Asserts that the field should be true (for boolean fields). This method will look up by an
   * exact match of the default message 'must be true'.
   *
   * @return Same object instance, providing a fluid api.
   */
  AssertConstraintSpec shouldBeTrue();

  /**
   * Asserts that the field should be false (for boolean fields).
   *
   * @param expectedMessage Expected constraint violation message.
   * @param exactMatch Flag if an exact match should be performed. Otherwise, either a substring or
   *     regex match will be performed.
   * @return Same object instance, providing a fluid api.
   */
  AssertConstraintSpec shouldBeFalse(String expectedMessage, boolean exactMatch);

  /**
   * Asserts that the field should be false (for boolean fields). This method will look up by an
   * exact match of the default message 'must be false'.
   *
   * @return Same object instance, providing a fluid api.
   */
  AssertConstraintSpec shouldBeFalse();
}

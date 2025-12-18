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
package io.github.nonentitydev.validationspectester.spec.asserts;

import static io.github.nonentitydev.validationspectester.StandardAssertConstraintViolation.givenBeanInstance;

import io.github.nonentitydev.validationspectester.spec.reflection.ReflectionHelper;
import jakarta.validation.Validator;
import java.lang.reflect.Field;

/**
 * Provides assertion methods for boolean constraints such as {@code @AssertTrue} and
 * {@code @AssertFalse}.
 */
public abstract class BooleanConstraintsAssertion {

  private BooleanConstraintsAssertion() {}

  /**
   * Asserts that a boolean field behaves according to the specified allowed value and expected
   * validation message.
   *
   * <p>This method sets the field to the opposite of the allowed value and checks for a validation
   * error with the expected message. It then sets the field to the allowed value and verifies that
   * no such validation error exists.
   *
   * @param beanInstance the instance of the bean containing the field to be tested
   * @param field the field to be tested
   * @param allowedValue the boolean value that is expected to pass validation
   * @param expectedMessage the expected validation message when the field is set to the opposite of
   *     the allowed value
   * @param exactMatch whether to check for an exact match of the expected message or just
   *     containment
   * @param validator the validator to use for performing validations
   * @param groups optional validation groups to consider during validation
   */
  public static void shouldBe(
      Object beanInstance,
      Field field,
      Boolean allowedValue,
      String expectedMessage,
      boolean exactMatch,
      Validator validator,
      Class<?>... groups) {

    ReflectionHelper.setFieldValue(beanInstance, field, !allowedValue);
    if (exactMatch) {
      givenBeanInstance(beanInstance, validator, groups)
          .fieldHasError(field.getName(), expectedMessage);
    } else {
      givenBeanInstance(beanInstance, validator, groups)
          .fieldHasErrorContaining(field.getName(), expectedMessage);
    }

    ReflectionHelper.setFieldValue(beanInstance, field, allowedValue);
    if (exactMatch) {
      givenBeanInstance(beanInstance, validator, groups)
          .fieldHasNoneOfErrors(field.getName(), expectedMessage);
    } else {
      givenBeanInstance(beanInstance, validator, groups)
          .fieldHasNoneOfErrorsContaining(field.getName(), expectedMessage);
    }
  }
}

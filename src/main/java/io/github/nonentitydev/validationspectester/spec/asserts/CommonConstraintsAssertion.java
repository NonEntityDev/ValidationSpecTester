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
import java.util.function.Supplier;

public abstract class CommonConstraintsAssertion {

  private CommonConstraintsAssertion() {
    throw new UnsupportedOperationException("This class was not meant to be instantiated.");
  }

  /**
   * Asserts that the specified field on the given bean instance should not be null. It sets the
   * field to null and verifies that the expected validation error message is produced. Then, it
   * sets the field to a sample value provided by the instance supplier and verifies that no
   * validation error message is produced.
   *
   * @param beanInstance The instance of the bean containing the field to be tested
   * @param field The field to be tested for non-null constraint
   * @param instanceSupplier A supplier that provides a sample non-null value for the field
   * @param validator The Validator instance used for validation
   * @param groups The validation groups to be applied during validation
   * @param expectedMessage The expected validation error message when the field is null
   * @param exactMatch If true, checks for an exact match of the error message; if false, checks if
   *     the error message contains the expected message
   */
  public static void shouldNotBeNull(
      Object beanInstance,
      Field field,
      Supplier<Object> instanceSupplier,
      Validator validator,
      Class<?>[] groups,
      String expectedMessage,
      boolean exactMatch) {

    ReflectionHelper.setFieldValue(beanInstance, field, null);
    if (exactMatch) {
      givenBeanInstance(beanInstance, validator, groups)
          .fieldHasError(field.getName(), expectedMessage);
    } else {
      givenBeanInstance(beanInstance, validator, groups)
          .fieldHasErrorContaining(field.getName(), expectedMessage);
    }

    Object sampleValue = instanceSupplier.get();
    ReflectionHelper.setFieldValue(beanInstance, field, sampleValue);
    if (exactMatch) {
      givenBeanInstance(beanInstance, validator, groups)
          .fieldHasNoneOfErrors(field.getName(), expectedMessage);
    } else {
      givenBeanInstance(beanInstance, validator, groups)
          .fieldHasNoneOfErrorsContaining(field.getName(), expectedMessage);
    }
  }
}

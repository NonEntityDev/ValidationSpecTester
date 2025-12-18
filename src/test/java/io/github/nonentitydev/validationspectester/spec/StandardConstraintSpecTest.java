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

import static io.github.nonentitydev.validationspectester.spec.StandardConstraintSpec.givenBeanType;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.nonentitydev.validationspectester.fixtures.Contact;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Supplier;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({CommonConstraintAssertionsTest.class})
class StandardConstraintSpecTest {

  @Nested
  @DisplayName("Given I am preparing to assert the constraint specifications of a bean type")
  class BeforeAssertingTest {

    @Test
    @DisplayName("When I try to create an instance of StandardConstraintSpec")
    void cantBeInstantiated() {
      Constructor<?> constructor = StandardConstraintSpec.class.getDeclaredConstructors()[0];
      constructor.setAccessible(true);

      assertThatThrownBy(constructor::newInstance)
          .isInstanceOf(InvocationTargetException.class)
          .cause()
          .isInstanceOf(UnsupportedOperationException.class)
          .hasMessage("This class was not meant to be instantiated using its default constructor.");
    }

    @Test
    @DisplayName("When I initialize an instance with a default validator")
    @SuppressWarnings("unchecked")
    void initializingWithDefaultValidator() throws IllegalAccessException {
      AssertConstraintSpec spec = givenBeanType(Contact.class);

      Object beanInstance = FieldUtils.readField(spec, "beanInstance", true);
      assertThat(beanInstance).isNotNull().isInstanceOf(Contact.class);

      Object validator = FieldUtils.readField(spec, "validator", true);
      assertThat(validator).isNotNull().isInstanceOf(Validator.class);

      Field field = (Field) FieldUtils.readField(spec, "field", true);
      assertThat(field).isNull();

      Supplier<Object> fieldInstanceSupplier =
          (Supplier<Object>) FieldUtils.readField(spec, "fieldInstanceSupplier", true);
      assertThat(fieldInstanceSupplier).isNull();

      Class<?>[] groups = (Class<?>[]) FieldUtils.readField(spec, "groups", true);
      assertThat(groups).isEmpty();
    }

    @Test
    @DisplayName("When I initialize an instance with a custom validator")
    @SuppressWarnings("unchecked")
    void initializeWithCustomValidator() throws IllegalAccessException {
      try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
        Validator customValidator = validatorFactory.getValidator();
        AssertConstraintSpec spec = givenBeanType(Contact.class, customValidator);

        Object beanInstance = FieldUtils.readField(spec, "beanInstance", true);
        assertThat(beanInstance).isNotNull().isInstanceOf(Contact.class);

        Object validator = FieldUtils.readField(spec, "validator", true);
        assertThat(validator).isSameAs(customValidator);

        Field field = (Field) FieldUtils.readField(spec, "field", true);
        assertThat(field).isNull();

        Supplier<Object> fieldInstanceSupplier =
            (Supplier<Object>) FieldUtils.readField(spec, "fieldInstanceSupplier", true);
        assertThat(fieldInstanceSupplier).isNull();

        Class<?>[] groups = (Class<?>[]) FieldUtils.readField(spec, "groups", true);
        assertThat(groups).isEmpty();
      }
    }

    @Test
    @DisplayName("When I set the field to be tested without providing a custom instance supplier")
    @SuppressWarnings("unchecked")
    void setFieldWithoutCustomProvider() throws IllegalAccessException {
      AssertConstraintSpec spec = givenBeanType(Contact.class).field("email");

      Field field = (Field) FieldUtils.readField(spec, "field", true);
      assertThat(field.getName()).isEqualTo("email");

      Supplier<Object> supplier =
          (Supplier<Object>) FieldUtils.readField(spec, "fieldInstanceSupplier", true);
      assertThat(supplier).isNotNull();
    }

    @Test
    @DisplayName("When I set the field to be tested providing a custom instance supplier")
    @SuppressWarnings("unchecked")
    void setFieldWithCustomProvider() throws IllegalAccessException {
      Supplier<Object> supplier = String::new;
      AssertConstraintSpec spec = givenBeanType(Contact.class).field("email", supplier);

      Field field = (Field) FieldUtils.readField(spec, "field", true);
      assertThat(field.getName()).isEqualTo("email");

      Supplier<Object> retrievedSupplier =
          (Supplier<Object>) FieldUtils.readField(spec, "fieldInstanceSupplier", true);
      assertThat(retrievedSupplier).isSameAs(supplier);
    }

    @Test
    @DisplayName("When I set the validation groups")
    void setValidationGroups() throws IllegalAccessException {
      AssertConstraintSpec spec =
          givenBeanType(Contact.class).withGroups(Contact.ProfessionalContact.class);

      Class<?>[] groups = (Class<?>[]) FieldUtils.readField(spec, "groups", true);
      assertThat(groups).containsExactly(Contact.ProfessionalContact.class);
    }

    @Test
    @DisplayName("When I reset the validation groups to none")
    void resetValidationGroups() throws IllegalAccessException {
      AssertConstraintSpec spec =
          givenBeanType(Contact.class).withGroups(Contact.ProfessionalContact.class);

      Class<?>[] groups = (Class<?>[]) FieldUtils.readField(spec, "groups", true);
      assertThat(groups).containsExactly(Contact.ProfessionalContact.class);

      spec.withNoGroups();
      groups = (Class<?>[]) FieldUtils.readField(spec, "groups", true);
      assertThat(groups).isEmpty();
    }
  }
}

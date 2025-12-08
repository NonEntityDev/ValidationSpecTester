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
package dev.nonentity.validationspectester;

import static dev.nonentity.validationspectester.StandardAssertConstraintViolation.givenInstance;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dev.nonentity.validationspectester.fixtures.Contact;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class StandardAssertConstraintViolationTest {

  @Nested
  @DisplayName("Given I create a new instance of the StandardAssertConstraintViolation class")
  class ConstructorsTest {

    @Test
    @DisplayName("When using the default non-argument constructor")
    void unableToUsedDefaultConstructor() {
      Constructor<?> constructor =
          StandardAssertConstraintViolation.class.getDeclaredConstructors()[0];
      constructor.setAccessible(true);

      assertThatThrownBy(constructor::newInstance)
          .isInstanceOf(InvocationTargetException.class)
          .cause()
          .isInstanceOf(UnsupportedOperationException.class)
          .hasMessage("This class was not meant to be instantiated using its default constructor.");
    }
  }

  @Nested
  @DisplayName("Given I check that a bean instance field has expected violations")
  class FieldHasErrorTest {

    @Test
    @DisplayName("When the field is not found in the list of constraint violations")
    void fieldNotFound() {
      assertThatThrownBy(
              () -> givenInstance(new Contact()).fieldHasError("address", "must not be null"))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("Field address not found or has no violations.");
    }

    @Test
    @DisplayName("When no expected violation to assert is provided")
    void expectedViolationsNotProvided() {
      assertThatThrownBy(() -> givenInstance(new Contact()).fieldHasError("firstName"))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("No expected violation provided to assert.");
    }

    @Test
    @DisplayName("When expected violation to assert is provided as null")
    void expectedViolationsProvidedAsNull() {
      assertThatThrownBy(() -> givenInstance(new Contact()).fieldHasError("firstName", null))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("No expected violation provided to assert.");
    }

    @Test
    @DisplayName("When the field has the expected violation")
    void withMatchingViolation() {
      assertThatCode(
              () -> givenInstance(new Contact()).fieldHasError("firstName", "must not be null"))
          .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("When the field does not have the expected violation")
    void withoutMatchingViolation() {
      assertThatThrownBy(
              () ->
                  givenInstance(new Contact())
                      .fieldHasError("firstName", "length must be between 3 and 140"))
          .isInstanceOf(AssertionError.class)
          .hasMessage(
              "Field firstName expected to have the violations [length must be between 3 and 140], but violations [must not be null] were found.");
    }

    @Test
    @DisplayName("When the field has more violations than expected")
    void fieldHasMoreViolations() {
      Contact contact = new Contact();
      contact.setEmail("an");

      assertThatCode(
              () ->
                  givenInstance(contact)
                      .fieldHasError("email", "must be a well-formed email address"))
          .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("When the field has less violations than expected")
    void fieldHasLessViolations() {
      assertThatThrownBy(
              () ->
                  givenInstance(new Contact())
                      .fieldHasError(
                          "firstName", "must not be null", "length must be between 3 and 140"))
          .isInstanceOf(AssertionError.class)
          .hasMessage(
              "Field firstName expected to have the violations [length must be between 3 and 140, must not be null], but violations [must not be null] were found.");
    }

    @Test
    @DisplayName("When validation groups are received")
    void appliedValidationGroupsWhenReceived() {
      assertThatThrownBy(
              () -> givenInstance(new Contact()).fieldHasError("companyName", "must not be null"))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("Field companyName not found or has no violations.");

      assertThatCode(
              () ->
                  givenInstance(new Contact(), Contact.ProfessionalContact.class)
                      .fieldHasError("companyName", "must not be null"))
          .doesNotThrowAnyException();
    }
  }

  @Nested
  @DisplayName(
      "Given I check that a bean instance field has violations with the expected message fragments")
  class FieldHasErrorContainingTest {

    @Test
    @DisplayName("When the field is not found in the list of constraint violations")
    void fieldNotFound() {
      assertThatThrownBy(
              () -> givenInstance(new Contact()).fieldHasErrorContaining("address", "not be null"))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("Field address not found or has no violations.");
    }

    @Test
    @DisplayName("When no expected violation fragment to assert is provided")
    void expectedViolationsNotProvided() {
      assertThatThrownBy(() -> givenInstance(new Contact()).fieldHasErrorContaining("firstName"))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("No expected violation provided to assert.");
    }

    @Test
    @DisplayName("When expected violation fragments to assert is provided as null")
    void expectedViolationsProvidedAsNull() {
      assertThatThrownBy(
              () -> givenInstance(new Contact()).fieldHasErrorContaining("firstName", null))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("No expected violation provided to assert.");
    }

    @Test
    @DisplayName("When the field has a validation matching with one of the expected fragments")
    void withMatchingViolation() {
      assertThatCode(
              () ->
                  givenInstance(new Contact()).fieldHasErrorContaining("firstName", "not be null"))
          .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("When the field does not have a violation matching the expected fragment")
    void withoutMatchingViolation() {
      assertThatThrownBy(
              () ->
                  givenInstance(new Contact())
                      .fieldHasErrorContaining("firstName", "between 3 and 140"))
          .isInstanceOf(AssertionError.class)
          .hasMessage(
              "Field firstName expected to have violations matching [between 3 and 140], but violations [must not be null] were found.");
    }

    @Test
    @DisplayName("When the field has more violations than the expected fragments provided")
    void fieldHasMoreViolations() {
      Contact contact = new Contact();
      contact.setEmail("an");

      assertThatCode(
              () -> givenInstance(contact).fieldHasErrorContaining("email", "formed email address"))
          .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("When the field has less violations than expected fragments provided")
    void fieldHasLessViolations() {
      assertThatThrownBy(
              () ->
                  givenInstance(new Contact())
                      .fieldHasErrorContaining("firstName", "not be null", "between 3 and 140"))
          .isInstanceOf(AssertionError.class)
          .hasMessage(
              "Field firstName expected to have violations matching [between 3 and 140, not be null], but violations [must not be null] were found.");
    }

    @Test
    @DisplayName("When validation groups are received")
    void appliedValidationGroupsWhenReceived() {
      assertThatThrownBy(
              () ->
                  givenInstance(new Contact())
                      .fieldHasErrorContaining("companyName", "must not be null"))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("Field companyName not found or has no violations.");

      assertThatCode(
              () ->
                  givenInstance(new Contact(), Contact.ProfessionalContact.class)
                      .fieldHasErrorContaining("companyName", "not be null"))
          .doesNotThrowAnyException();
    }
  }
}

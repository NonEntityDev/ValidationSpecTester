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
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.nonentitydev.validationspectester.fixtures.Contact;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class CommonConstraintAssertionsTest {

  @Nested
  @DisplayName(
      "Given I am asserting that a field should not be null using the default constraint violation message")
  class ShouldNotBeNullWithDefaultMessageTest {

    @Test
    @DisplayName("When the field has the @NotNull constraint")
    void fieldCantBeNull() {
      AssertConstraintSpec spec = givenBeanType(Contact.class).field("firstName");
      assertThatCode(spec::shouldNotBeNull).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("When the field has the @NotNull constraint and I use a custom instance supplier")
    void fieldCantBeNullWithCustomSupplier() {
      AssertConstraintSpec spec =
          givenBeanType(Contact.class).field("firstName", () -> "SampleName");
      assertThatCode(spec::shouldNotBeNull).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("When the field does not have the @NotNull constraint")
    void fieldCanBeNull() {
      AssertConstraintSpec spec = givenBeanType(Contact.class).field("companyName");
      assertThatThrownBy(spec::shouldNotBeNull)
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("Field companyName not found or has no violations.");
    }

    @Test
    @DisplayName("When the field has the @NotNull constraint set for a validation group")
    void usingValidationGroups() {
      AssertConstraintSpec spec =
          givenBeanType(Contact.class)
              .field("companyName")
              .withGroups(Contact.ProfessionalContact.class);
      assertThatCode(spec::shouldNotBeNull).doesNotThrowAnyException();
    }
  }

  @Nested
  @DisplayName("Given I am asserting that a field should not be null with a custom message")
  class ShouldNotBeNullWithCustomMessageTest {

    @Test
    @DisplayName("When the field has the @NotNull constraint with a substring")
    void fieldCantBeNullWithCustomMessageSubstring() {
      AssertConstraintSpec spec = givenBeanType(Contact.class).field("firstName");
      assertThatCode(() -> spec.shouldNotBeNull("not be null", false)).doesNotThrowAnyException();
    }
  }

  @Nested
  @DisplayName(
      "Given I am asserting that a field can be null using the default constraint violation message")
  class CanBeNullWithDefaultMessageTest {

    @Test
    @DisplayName("When the field has the @NotNull constraint")
    void fieldCantBeNull() {
      AssertConstraintSpec spec = givenBeanType(Contact.class).field("firstName");
      assertThatThrownBy(spec::canBeNull)
          .isInstanceOf(AssertionError.class)
          .hasMessage(
              "Field firstName not expected to have any of violations [must not be null]. Violations found for the field: [must not be null]");
    }

    @Test
    @DisplayName("When the field has the @NotNull constraint and I use a custom instance supplier")
    void fieldCantBeNullWithCustomSupplier() {
      AssertConstraintSpec spec =
          givenBeanType(Contact.class).field("firstName", () -> "SampleName");
      assertThatThrownBy(spec::canBeNull)
          .isInstanceOf(AssertionError.class)
          .hasMessage(
              "Field firstName not expected to have any of violations [must not be null]. Violations found for the field: [must not be null]");
    }

    @Test
    @DisplayName("When the field does not have the @NotNull constraint")
    void fieldCanBeNull() {
      AssertConstraintSpec spec = givenBeanType(Contact.class).field("companyName");
      assertThatCode(spec::canBeNull).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("When the field has the @NotNull constraint set for a validation group")
    void fieldCantBeNullUsingValidationGroups() {
      AssertConstraintSpec spec =
          givenBeanType(Contact.class)
              .field("companyName")
              .withGroups(Contact.ProfessionalContact.class);
      assertThatThrownBy(spec::canBeNull)
          .isInstanceOf(AssertionError.class)
          .hasMessage(
              "Field companyName not expected to have any of violations [must not be null]. Violations found for the field: [must not be null]");
    }
  }

  @Nested
  @DisplayName("Given I am asserting that a field can be null with a custom message")
  class CanBeNullWithCustomMessageTest {

    @Test
    @DisplayName(
        "When the field has the @NotNull constraint with an expected negative message substring matching")
    void fieldCanBeNullWithCustomMessageSubstring() {
      AssertConstraintSpec spec = givenBeanType(Contact.class).field("firstName");
      assertThatThrownBy(() -> spec.canBeNull("not be null", false))
          .isInstanceOf(AssertionError.class)
          .hasMessage(
              "Field firstName not expected to have violations containing [not be null]. Violations found for the field: [must not be null]");
    }

    @Test
    @DisplayName(
        "When the field has the @NotNull constraint with an expected negative message exact matching")
    void fieldCanBeNullWithCustomMessageExactMatch() {
      AssertConstraintSpec spec = givenBeanType(Contact.class).field("firstName");
      assertThatThrownBy(() -> spec.canBeNull("must not be null", true))
          .isInstanceOf(AssertionError.class)
          .hasMessage(
              "Field firstName not expected to have any of violations [must not be null]. Violations found for the field: [must not be null]");
    }

    @Test
    @DisplayName(
        "When the field has the @NotNull constraint with an expected negative message regex pattern matching")
    void fieldCanBeNullWithCustomMessagePattern() {
      AssertConstraintSpec spec = givenBeanType(Contact.class).field("firstName");
      assertThatThrownBy(() -> spec.canBeNull("^.* not be null$", false))
          .isInstanceOf(AssertionError.class)
          .hasMessage(
              "Field firstName not expected to have violations containing [^.* not be null$]. Violations found for the field: [must not be null]");
    }

    @Test
    @DisplayName(
        "When the field has the @NotNull constraint set for a validation group with an expected negative message regex pattern matching")
    void fieldCanBeNullWithValidationGroups() {
      AssertConstraintSpec spec =
          givenBeanType(Contact.class)
              .field("companyName")
              .withGroups(Contact.ProfessionalContact.class);
      assertThatThrownBy(() -> spec.canBeNull("^.* not be null$", false))
          .isInstanceOf(AssertionError.class)
          .hasMessage(
              "Field companyName not expected to have violations containing [^.* not be null$]. Violations found for the field: [must not be null]");
    }

    @Test
    @DisplayName("When the field does not have the @NotNull constraint")
    void fieldCanBeNull() {
      AssertConstraintSpec spec = givenBeanType(Contact.class).field("companyName");
      assertThatCode(() -> spec.canBeNull("not be null", false)).doesNotThrowAnyException();
    }
  }

  @Nested
  @DisplayName("Given I am asserting that a field should be valid for a given value")
  class ShouldBeValidForTest {

    @Test
    @DisplayName("When the field value is valid")
    void fieldShouldBeValidFor() {
      AssertConstraintSpec spec = givenBeanType(Contact.class).field("firstName");
      assertThatCode(() -> spec.shouldBeValidFor("John")).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("When the field value is invalid")
    void fieldShouldNotBeValidFor() {
      AssertConstraintSpec spec = givenBeanType(Contact.class).field("firstName");
      assertThatThrownBy(() -> spec.shouldBeValidFor(null))
          .isInstanceOf(AssertionError.class)
          .hasMessage(
              "Field firstName not expected to have violations. Violations found: [must not be null]");
    }

    @Test
    @DisplayName("When the field value is valid when using a validation group")
    void fieldShouldBeValidForWithValidationGroups() {
      AssertConstraintSpec spec =
          givenBeanType(Contact.class)
              .field("companyName")
              .withGroups(Contact.ProfessionalContact.class);
      assertThatCode(() -> spec.shouldBeValidFor("Acme Corp")).doesNotThrowAnyException();
    }
  }
}

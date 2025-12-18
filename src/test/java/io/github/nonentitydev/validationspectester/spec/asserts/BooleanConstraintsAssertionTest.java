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

import static io.github.nonentitydev.validationspectester.spec.StandardConstraintSpec.givenBeanType;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.nonentitydev.validationspectester.fixtures.Contact;
import io.github.nonentitydev.validationspectester.spec.AssertConstraintSpec;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class BooleanConstraintsAssertionTest {

  @Nested
  @DisplayName("Given I am asserting that a field should be true")
  class ShouldBeTrueTest {

    @Test
    @DisplayName("When the field has the @AssertTrue constraint")
    void fieldShouldBeTrue() {
      AssertConstraintSpec spec = givenBeanType(Contact.class).field("agreeToTerms");
      assertThatCode(spec::shouldBeTrue).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("When the field does not have the @AssertTrue constraint")
    void shouldNotBeTrue() {
      AssertConstraintSpec spec = givenBeanType(Contact.class).field("active");
      assertThatThrownBy(spec::shouldBeTrue)
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("Field active not found or has no violations.");
    }

    @Test
    @DisplayName("When the field has the @AssertTrue constraint with a validation group")
    void fieldShouldBeTrueWithValidationGroups() {
      AssertConstraintSpec spec =
          givenBeanType(Contact.class)
              .field("vetted")
              .withGroups(Contact.AdministratorContact.class);
      assertThatCode(spec::shouldBeTrue).doesNotThrowAnyException();
    }
  }

  @Nested
  @DisplayName("Given I am asserting that a field should be true with a custom message")
  class ShouldBeTrueWithCustomMessageTest {

    @Test
    @DisplayName(
        "When the field has the @AssertTrue constraint with a custom message with an exact match")
    void fieldShouldBeTrueWithCustomMessage() {
      AssertConstraintSpec spec = givenBeanType(Contact.class).field("agreeToTerms");
      assertThatCode(() -> spec.shouldBeTrue("must be true", true)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName(
        "When the field has the @AssertTrue constraint with a custom message with a substring match")
    void fieldShouldBeTrueWithCustomMessageSubstring() {
      AssertConstraintSpec spec = givenBeanType(Contact.class).field("agreeToTerms");
      assertThatCode(() -> spec.shouldBeTrue("be true", false)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName(
        "When the field has the @AssertTrue constraint with a custom message with a regex patter match")
    void fieldShouldBeTrueWithCustomMessagePattern() {
      AssertConstraintSpec spec = givenBeanType(Contact.class).field("agreeToTerms");
      assertThatCode(() -> spec.shouldBeTrue("^.* be true$", false)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("When the field has the @AssertTrue constraint with a custom message not matching")
    void fieldShouldBeTrueWithCustomMessageNotMatching() {
      AssertConstraintSpec spec = givenBeanType(Contact.class).field("agreeToTerms");
      assertThatThrownBy(() -> spec.shouldBeTrue("be false", false))
          .isInstanceOf(AssertionError.class)
          .hasMessage(
              "Field agreeToTerms expected to have violations matching [be false], but violations [must be true] were found.");
    }

    @Test
    @DisplayName(
        "When the field has the @AssertTrue constraint with a custom message when using validation groups")
    void fieldShouldBeTrueWithCustomMessageAndValidationGroups() {
      AssertConstraintSpec spec =
          givenBeanType(Contact.class)
              .field("vetted")
              .withGroups(Contact.AdministratorContact.class);
      assertThatCode(() -> spec.shouldBeTrue("must be true", false)).doesNotThrowAnyException();
    }
  }

  @Nested
  @DisplayName("Given I am asserting that a field should be false")
  class ShouldBeFalseTest {

    @Test
    @DisplayName("When the field has the @AssertFalse constraint")
    void fieldShouldBeFalse() {
      AssertConstraintSpec spec = givenBeanType(Contact.class).field("deleted");
      assertThatCode(spec::shouldBeFalse).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("When the field does not have the @AssertFalse constraint")
    void shouldNotBeTrue() {
      AssertConstraintSpec spec = givenBeanType(Contact.class).field("active");
      assertThatThrownBy(spec::shouldBeFalse)
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("Field active not found or has no violations.");
    }

    @Test
    @DisplayName("When the field has the @AssertFalse constraint with a validation group")
    void fieldShouldBeTrueWithValidationGroups() {
      AssertConstraintSpec spec =
          givenBeanType(Contact.class).field("blocked").withGroups(Contact.CustomerContact.class);
      assertThatCode(spec::shouldBeFalse).doesNotThrowAnyException();
    }
  }

  @Nested
  @DisplayName("Given I am asserting that a field should be false with a custom message")
  class ShouldBeFalseWithCustomMessageTest {

    @Test
    @DisplayName(
        "When the field has the @AssertFalse constraint with a custom message with an exact match")
    void fieldShouldBeFalseWithCustomMessage() {
      AssertConstraintSpec spec = givenBeanType(Contact.class).field("deleted");
      assertThatCode(() -> spec.shouldBeFalse("must be false", true)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName(
        "When the field has the @AssertFalse constraint with a custom message with a substring match")
    void fieldShouldBeFalseWithCustomMessageSubstring() {
      AssertConstraintSpec spec = givenBeanType(Contact.class).field("deleted");
      assertThatCode(() -> spec.shouldBeFalse("be false", false)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName(
        "When the field has the @AssertFalse constraint with a custom message with a regex patter match")
    void fieldShouldBeFalseWithCustomMessagePattern() {
      AssertConstraintSpec spec = givenBeanType(Contact.class).field("deleted");
      assertThatCode(() -> spec.shouldBeFalse("^.* be false$", false)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName(
        "When the field has the @AssertFalse constraint with a custom message not matching")
    void fieldShouldBeFalseWithCustomMessageNotMatching() {
      AssertConstraintSpec spec = givenBeanType(Contact.class).field("deleted");
      assertThatThrownBy(() -> spec.shouldBeFalse("be true", false))
          .isInstanceOf(AssertionError.class)
          .hasMessage(
              "Field deleted expected to have violations matching [be true], but violations [must be false] were found.");
    }

    @Test
    @DisplayName(
        "When the field has the @AssertFalse constraint with a custom message when using validation groups")
    void fieldShouldBeFalseWithCustomMessageAndValidationGroups() {
      AssertConstraintSpec spec =
          givenBeanType(Contact.class).field("blocked").withGroups(Contact.CustomerContact.class);
      assertThatCode(() -> spec.shouldBeFalse("must be false", false)).doesNotThrowAnyException();
    }
  }
}

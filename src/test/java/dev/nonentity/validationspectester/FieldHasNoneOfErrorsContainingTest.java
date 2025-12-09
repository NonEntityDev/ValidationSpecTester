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
import static org.assertj.core.api.Assertions.*;

import dev.nonentity.validationspectester.fixtures.Contact;
import java.util.Arrays;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName(
    "Given I check that a bean instance field does not have any violation with the expected message fragments")
class FieldHasNoneOfErrorsContainingTest {
  static Stream<Arguments> argumentValidationScenarios() {
    return Stream.of(
        // -- Field name is null
        Arguments.of(
            "When field name is null",
            null,
            new String[] {"must not be null"},
            "No field name provided to assert."),
        // -- Field name is blank
        Arguments.of(
            "When field name is blank",
            "",
            new String[] {"must not be null"},
            "No field name provided to assert."),
        // -- Unexpected violations is null
        Arguments.of(
            "When unexpected violations is null",
            "address",
            null,
            "No violation provided to assert."),
        // -- Unexpected violations is empty
        Arguments.of(
            "When unexpected violations is empty",
            "address",
            new String[] {},
            "No violation provided to assert."));
  }

  @ParameterizedTest
  @MethodSource("argumentValidationScenarios")
  void assertArgumentValidation(
      String scenario, String fieldName, String[] violations, String expectedMessage) {
    assertThatThrownBy(
            () ->
                givenInstance(new Contact()).fieldHasNoneOfErrorsContaining(fieldName, violations))
        .withFailMessage(scenario)
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage(expectedMessage);
  }

  @Test
  @DisplayName("When there is no violation matching the received fragments")
  void noViolationMatches() {
    AssertConstraintViolation testHelper = givenInstance(new Contact());
    assertThat(testHelper.fieldHasNoneOfErrorsContaining("firstName", "between 3 and 140"))
        .isSameAs(testHelper);
  }

  static Stream<Arguments> violationMatchingScenarios() {
    return Stream.of(
        // -- Matching substring scenario
        Arguments.of(
            "When there is a violation matching by substring comparison",
            "firstName",
            new String[] {"not be null"},
            new String[] {"must not be null"}),
        // -- Matching regex scenario
        Arguments.of(
            "When there is a violation matching by regex pattern",
            "firstName",
            new String[] {"^.* not be null$"},
            new String[] {"must not be null"}),
        // -- Matching entire string
        Arguments.of(
            "When there is a violation matching the entire string.",
            "firstName",
            new String[] {"must not be null"},
            new String[] {"must not be null"}));
  }

  @ParameterizedTest
  @MethodSource("violationMatchingScenarios")
  @DisplayName("When there is no violation matching the received fragments")
  void assertViolationMatches(
      String scenario,
      String fieldName,
      String[] unexpectedViolations,
      String[] expectedActualViolations) {

    assertThatThrownBy(
            () ->
                givenInstance(new Contact())
                    .fieldHasNoneOfErrorsContaining(fieldName, unexpectedViolations))
        .withFailMessage(scenario)
        .isInstanceOf(AssertionError.class)
        .hasMessage(
            String.format(
                "Field %s not expected to have violations containing %s. Violations found for the field: %s",
                fieldName,
                Arrays.asList(unexpectedViolations),
                Arrays.asList(expectedActualViolations)));
  }

  @Test
  @DisplayName("When validation groups are received")
  void appliedValidationGroupsWhenReceived() {
    assertThatCode(
            () ->
                givenInstance(new Contact())
                    .fieldHasNoneOfErrorsContaining("companyName", "not be null"))
        .doesNotThrowAnyException();

    assertThatThrownBy(
            () ->
                givenInstance(new Contact(), Contact.ProfessionalContact.class)
                    .fieldHasNoneOfErrorsContaining("companyName", "^.* not be null$"))
        .isInstanceOf(AssertionError.class)
        .hasMessage(
            "Field companyName not expected to have violations containing [^.* not be null$]. Violations found for the field: [must not be null]");
  }
}

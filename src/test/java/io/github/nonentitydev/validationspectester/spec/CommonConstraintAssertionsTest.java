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

import io.github.nonentitydev.validationspectester.fixtures.Contact;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static io.github.nonentitydev.validationspectester.spec.StandardConstraintSpec.givenBeanType;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CommonConstraintAssertionsTest {

    @Nested
    @DisplayName("Given I am asserting that a field should not be null using the default constraint violation message")
    class ShouldNotBeNullWithDefaultMessageTest {

        @Test
        @DisplayName("When the field has the @NotNull constraint")
        void fieldCantBeNull() {
            AssertConstraintSpec spec = givenBeanType(Contact.class).field("firstName");
            assertThatCode(spec::shouldNotBeNull).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("When the field does not have the @NotNull constraint")
        void fieldCanBeNull() {
            AssertConstraintSpec spec = givenBeanType(Contact.class).field("companyName");
            assertThatThrownBy(spec::shouldNotBeNull
            ).isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Field companyName not found or has no violations.");
        }

        @Test
        @DisplayName("When the field has the @NotNull constraint set for a validation group")
        void usingValidationGroups() {
            AssertConstraintSpec spec = givenBeanType(Contact.class)
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
            assertThatCode(() -> spec.shouldNotBeNull("not be null", false))
                    .doesNotThrowAnyException();
        }


    }

}

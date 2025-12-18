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
package io.github.nonentitydev.validationspectester;

/**
 * Establishes the common behavior for any implementation of the helper class to assert constraint
 * violations, as designed in this library. The main goal to have a class to help with constraint
 * violation assertions is to abstract the application test suite from {@link
 * jakarta.validation.Validator} details, offering a high level API to assert violations,
 * eliminating repetitive logic to perform validations and look up for error in the result
 * violations.
 */
public interface AssertConstraintViolation {

    /**
     * Asserts that, after validating the bean instance, optionally using the validation groups, the
     * field has all the expected violations. This assertion will check for exact matches.
     *
     * @param fieldName          Name of the field to have violations being checked.
     * @param expectedViolations One or more violation messages expected for the field.
     * @return Same object instance, providing a fluid api.
     */
    AssertConstraintViolation fieldHasError(String fieldName, String... expectedViolations);

    /**
     * Assert that, after validating the bean instance, optionally using validation groups, the field
     * has violations containing messages containing the expected fragments. This assertion will check
     * for violation messages fully matching the received fragments, containing the received fragments
     * as substring or matching a regex pattern defined by the fragment.
     *
     * <p>Example:
     *
     * <pre>{@code
     * // Matching substring.
     * givenInstance(new Contact())
     *   .fieldHasErrorContaining("not be null"); // Will match "must not be null" string.
     *
     * // Matching regex pattern.
     * givenInstance(new Contact())
     *   .fieldHasErrorContaining("^.* not be null$"); // Will also match "must not be null" string.
     * }</pre>
     *
     * @param fieldName                   Name of the field to have violations being checked.
     * @param expectedViolationsFragments One or more violation message fragments expected for the
     *                                    field.
     * @return Same object instance, providing a fluid api.
     */
    AssertConstraintViolation fieldHasErrorContaining(
            String fieldName, String... expectedViolationsFragments);

    /**
     * Assert that, after validating the bean, optionally using validation groups, the field has none
     * of the received violations. The assertion will check for exact matches.
     *
     * @param fieldName            Name of the field to have violations being checked.
     * @param unexpectedViolations One or more violation messages not expected for the field.
     * @return Same object instance, providing a fluid api.
     */
    AssertConstraintViolation fieldHasNoneOfErrors(String fieldName, String... unexpectedViolations);

    /**
     * Assert that, after validating the bean, optionally using validation groups, the field has none
     * of the received violations. This assertion will check for violation messages fully matching the
     * received fragments, containing the received fragments as substring or matching a regex pattern
     * defined by the fragment.
     *
     * <p>Example:
     *
     * <pre>{@code
     * // Matching substring.
     * givenInstance(new Contact())
     *  .fieldHasNoneOfErrorsContaining("not be null"); // Will match "must not be null" string.
     *
     * // Matching regex pattern.
     * givenInstance(new Contact())
     *   .fieldHasNoneOfErrorsContaining("^.* not be null$"); // Will also match "must not be null" string.
     *
     * }</pre>
     *
     * @param fieldName            Name of the field to have violations being checked.
     * @param unexpectedViolations One or more violation messages not expected for the field.
     * @return Same object instance, providing a fluid api.
     */
    AssertConstraintViolation fieldHasNoneOfErrorsContaining(
            String fieldName, String... unexpectedViolations);

    /**
     * Assert that the field has no violations reported.
     *
     * @param fieldName Name of the field to have violations being checked.
     * @return Same object instance, providing a fluid api.
     */
    AssertConstraintViolation fieldHasNoError(String fieldName);


}

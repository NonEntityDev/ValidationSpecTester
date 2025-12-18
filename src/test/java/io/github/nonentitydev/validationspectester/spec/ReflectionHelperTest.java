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

import static org.assertj.core.api.Assertions.*;

import io.github.nonentitydev.validationspectester.fixtures.Contact;
import io.github.nonentitydev.validationspectester.fixtures.FieldSamples;
import io.github.nonentitydev.validationspectester.fixtures.PrototypeInterface;
import io.github.nonentitydev.validationspectester.spec.reflection.ReflectionErrorException;
import io.github.nonentitydev.validationspectester.spec.reflection.ReflectionHelper;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ReflectionHelperTest {

  @Nested
  @DisplayName("Given I am trying to the reference of a declared field from class using its name")
  class GetFieldByName {

    @Test
    @DisplayName("When the field is declared")
    void fieldIsDeclared() {
      Field firstNameField = ReflectionHelper.getFieldByName(Contact.class, "firstName");
      assertThat(firstNameField).isNotNull();
    }

    @Test
    @DisplayName("When the field is not declared")
    void fieldIsNotDeclared() {
      assertThatThrownBy(() -> ReflectionHelper.getFieldByName(Contact.class, "nonExistentField"))
          .isInstanceOf(ReflectionErrorException.class)
          .hasMessage(
              "Field 'nonExistentField' was not found in class 'io.github.nonentitydev.validationspectester.fixtures.Contact'.");
    }
  }

  @Nested
  @DisplayName("Given I am trying to create a new instance of a type")
  class NewInstanceOfTypeTest {

    @Test
    @DisplayName("When I provide a type with a default non-arg constructor")
    void defaultNoArgsConstructor() {
      assertThat(ReflectionHelper.newInstanceOfType(Contact.class)).isInstanceOf(Contact.class);
    }

    @Test
    @DisplayName("When I provide a type without a default no-arg constructor")
    void noDefaultNoArgsConstructor() {
      assertThat(ReflectionHelper.newInstanceOfType(FieldSamples.class))
          .isInstanceOf(FieldSamples.class);
    }

    @Test
    @DisplayName("When I provide an abstract type")
    void abstractType() {
      assertThatThrownBy(() -> ReflectionHelper.newInstanceOfType(PrototypeInterface.class))
          .isInstanceOf(ReflectionErrorException.class)
          .hasMessage(
              "It was not possible to create a new instance of type 'io.github.nonentitydev.validationspectester.fixtures.PrototypeInterface' even ignoring its constructor.");
    }

    static Stream<Arguments> rawCollectionTypesScenarios() {
      return Stream.of(
          Arguments.of("Raw ArrayList", ArrayList.class),
          Arguments.of("Raw HashMap", HashMap.class),
          Arguments.of("Raw HashSet", HashSet.class));
    }

    @ParameterizedTest
    @MethodSource("rawCollectionTypesScenarios")
    @DisplayName("When I am requesting a raw collection type instance")
    void rawArrayListType(String scenario, Class<?> collectionType) {
      assertThat(ReflectionHelper.newInstanceOfType(collectionType))
          .withFailMessage(
              "Scenario '%s' failed to create an instance of '%s'.",
              scenario, collectionType.getName())
          .isInstanceOf(collectionType);
    }
  }

  @Nested
  @DisplayName("Given I am trying to create a new instance of a field type")
  class NewsInstanceOfFieldTypeTest {

    static Stream<Arguments> fieldTypeScenarios() {
      return Stream.of(
          Arguments.of(
              "Primitive char field",
              ReflectionHelper.getFieldByName(FieldSamples.class, "primitiveCharField")),
          Arguments.of(
              "Primitive char array field",
              ReflectionHelper.getFieldByName(FieldSamples.class, "primitiveCharArrayField")),
          Arguments.of(
              "Wrapper character field",
              ReflectionHelper.getFieldByName(FieldSamples.class, "wrapperCharacterField")),
          Arguments.of(
              "String field", ReflectionHelper.getFieldByName(FieldSamples.class, "stringField")),
          Arguments.of(
              "String array field",
              ReflectionHelper.getFieldByName(FieldSamples.class, "stringArrayField")),
          Arguments.of(
              "Long field", ReflectionHelper.getFieldByName(FieldSamples.class, "longField")),
          Arguments.of(
              "Double field", ReflectionHelper.getFieldByName(FieldSamples.class, "doubleField")),
          Arguments.of(
              "Boolean field", ReflectionHelper.getFieldByName(FieldSamples.class, "booleanField")),
          Arguments.of(
              "Float field", ReflectionHelper.getFieldByName(FieldSamples.class, "floatField")),
          Arguments.of(
              "BigDecimal field",
              ReflectionHelper.getFieldByName(FieldSamples.class, "bigDecimalField")),
          Arguments.of(
              "Date field", ReflectionHelper.getFieldByName(FieldSamples.class, "dateField")),
          Arguments.of(
              "Calendar field",
              ReflectionHelper.getFieldByName(FieldSamples.class, "calendarField")),
          Arguments.of(
              "LocalDate field",
              ReflectionHelper.getFieldByName(FieldSamples.class, "localDateField")),
          Arguments.of(
              "LocalDateTime field",
              ReflectionHelper.getFieldByName(FieldSamples.class, "localDateTimeField")),
          Arguments.of(
              "List of objects field",
              ReflectionHelper.getFieldByName(FieldSamples.class, "listOfObjectsField")),
          Arguments.of(
              "Map of objects field",
              ReflectionHelper.getFieldByName(FieldSamples.class, "mapOfObjectsField")),
          Arguments.of(
              "Set of objects field",
              ReflectionHelper.getFieldByName(FieldSamples.class, "setOfObjectsField")),
          Arguments.of(
              "Enum field", ReflectionHelper.getFieldByName(FieldSamples.class, "enumField")),
          Arguments.of(
              "Contact object field",
              ReflectionHelper.getFieldByName(FieldSamples.class, "contactField")),
          Arguments.of(
              "Private constructor object field",
              ReflectionHelper.getFieldByName(FieldSamples.class, "privateConstructorObject")),
          Arguments.of(
              "Multi-arg constructor object field",
              ReflectionHelper.getFieldByName(FieldSamples.class, "multiArgConstructorClass")),
          Arguments.of(
              "Object field", ReflectionHelper.getFieldByName(FieldSamples.class, "objectField")),
          Arguments.of(
              "Inner class field",
              ReflectionHelper.getFieldByName(FieldSamples.class, "innerClassField")),
          Arguments.of(
              "Record field", ReflectionHelper.getFieldByName(FieldSamples.class, "recordField")),
          Arguments.of(
              "Raw ArrayList field",
              ReflectionHelper.getFieldByName(FieldSamples.class, "rawArrayListField")),
          Arguments.of(
              "Raw HashMap field",
              ReflectionHelper.getFieldByName(FieldSamples.class, "rawHashMapField")),
          Arguments.of(
              "Raw HashSet field",
              ReflectionHelper.getFieldByName(FieldSamples.class, "rawHashSetField")));
    }

    @ParameterizedTest
    @MethodSource("fieldTypeScenarios")
    void assertFieldTypeScenarios(String scenario, Field fieldScenario) {
      assertThat(ReflectionHelper.newInstanceOfFieldType(fieldScenario))
          .withFailMessage("Scenario '%s' failed to create an instance of the field.", scenario)
          .isNotNull();
    }

    @Test
    @DisplayName("When I provide an interface type or an abstract type")
    void interfaceInstantiation() {
      assertThatThrownBy(
              () ->
                  ReflectionHelper.newInstanceOfFieldType(
                      ReflectionHelper.getFieldByName(FieldSamples.class, "interfaceField")))
          .isInstanceOf(ReflectionErrorException.class)
          .hasMessage(
              "It was not possible to create a new instance of type 'io.github.nonentitydev.validationspectester.fixtures.PrototypeInterface' even ignoring its constructor.");
    }

    @Test
    @DisplayName("When I provide a field with nested generics")
    void typeWithNestedGenerics() {
      assertThatThrownBy(
              () ->
                  ReflectionHelper.newInstanceOfFieldType(
                      ReflectionHelper.getFieldByName(FieldSamples.class, "complexGenericField")))
          .isInstanceOf(ReflectionErrorException.class)
          .hasMessage(
              "It was not possible to create a new instance of type 'java.util.Map' even ignoring its constructor.");
    }
  }

  @Nested
  @DisplayName("Given I am setting the value of an object property")
  class SetFieldValueTest {

    @Test
    @DisplayName("When the field is accessible, even being private")
    void fieldExists() {
      Field field = ReflectionHelper.getFieldByName(Contact.class, "firstName");
      Contact contact = new Contact();

      ReflectionHelper.setFieldValue(contact, field, "John");

      assertThat(contact).hasFieldOrPropertyWithValue("firstName", "John");
    }

    @Test
    @DisplayName("When the field is not accessible")
    void fieldDoesNotExist() {
      Field field = ReflectionHelper.getFieldByName(Contact.class, "firstName");
      Contact contact = new Contact();

      assertThatThrownBy(() -> ReflectionHelper.setFieldValue(contact, field, 123))
          .isInstanceOf(ReflectionErrorException.class)
          .hasMessage(
              "It was not possible to set the value '123' into the field 'firstName' of type 'io.github.nonentitydev.validationspectester.fixtures.Contact'.");
    }
  }
}

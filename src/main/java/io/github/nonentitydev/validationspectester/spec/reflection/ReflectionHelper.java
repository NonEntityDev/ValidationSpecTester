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
package io.github.nonentitydev.validationspectester.spec.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.instancio.Instancio;
import org.instancio.exception.InstancioApiException;
import org.objenesis.ObjenesisStd;

/**
 * Offers a high level way for the rest of code to interact with reflection, abstracting it from
 * underlying libraries and implementation details.
 */
public abstract class ReflectionHelper {

  private ReflectionHelper() {}

  /**
   * Retrieves a field by its name from the specified bean type. If the field is not found, a {@link
   * ReflectionErrorException} is thrown.
   *
   * @param beanType Class type containing the field
   * @param fieldName Name of the field to be retrieved
   * @return the Field object representing the specified field
   */
  public static Field getFieldByName(Class<?> beanType, String fieldName) {
    Field field = FieldUtils.getField(beanType, fieldName, true);
    if (field == null) {
      throw new ReflectionErrorException(
          String.format("Field '%s' was not found in class '%s'.", fieldName, beanType.getName()));
    }
    return field;
  }

  private static Class<?>[] getGenericTypes(Field field) {
    List<Class<?>> generics = new ArrayList<>();
    if (field.getGenericType() instanceof ParameterizedType pt) {
      for (var typeArg : pt.getActualTypeArguments()) {
        if (typeArg instanceof Class<?> cls) {
          generics.add(cls);
        }
      }
    }
    return generics.toArray(new Class<?>[] {});
  }

  private static Object newInstanceHonoringConstructor(Class<?> type, Class<?>... generics) {
    try {
      if (List.class.isAssignableFrom(type)) {
        if ((generics == null) || (generics.length == 0)) {
          throw new ReflectionErrorException(
              "A list requires one generic type in order to be instantiated.");
        }
        return Instancio.createList(generics[0]);

      } else if (Map.class.isAssignableFrom(type)) {
        if ((generics == null) || (generics.length < 2)) {
          throw new ReflectionErrorException(
              "A map requires two generic types in order to be instantiated.");
        }
        return Instancio.createMap(generics[0], generics[1]);

      } else if (Set.class.isAssignableFrom(type)) {
        if ((generics == null) || (generics.length == 0)) {
          throw new ReflectionErrorException(
              "A set requires one generic type in order to be instantiated.");
        }
        return Instancio.createSet(generics[0]);

      } else {
        return Instancio.createBlank(type);
      }
    } catch (InstancioApiException ex) {
      throw new ReflectionErrorException(
          String.format(
              "It was not possible to create a new instance of type '%s' honoring the constructor.",
              type.getName()),
          ex);
    }
  }

  private static Object newInstanceIgnoringConstructor(Class<?> type) {
    try {
      return new ObjenesisStd().newInstance(type);
    } catch (InstantiationError ex) {
      throw new ReflectionErrorException(
          String.format(
              "It was not possible to create a new instance of type '%s' even ignoring its constructor.",
              type.getName()),
          ex);
    }
  }

  /**
   * Creates a new instance of the specified type using reflection. This method will try to honor
   * the type constructor. If not possible, the method will try to generate an instance without
   * honoring the constructors. If the both attempts fail, a {@link ReflectionErrorException} is
   * thrown.
   *
   * @param type Class type to create a new instance of
   * @return a new instance of the specified type
   */
  public static Object newInstanceOfType(Class<?> type) {
    try {
      return newInstanceHonoringConstructor(type);

    } catch (ReflectionErrorException honoringConstructorException) {
      try {
        return newInstanceIgnoringConstructor(type);

      } catch (ReflectionErrorException ignoringConstructorException) {
        ignoringConstructorException.addSuppressed(honoringConstructorException);
        throw ignoringConstructorException;
      }
    }
  }

  /**
   * Based on a class field, creates a new instance of the given type using reflection. This method
   * will try to honor the type constructor. If not possible, it will try to create a new instance
   * ignoring the constructor. If both attempts fail, a {@link ReflectionErrorException} is thrown.
   *
   * @param field Field being processed which a new instance of its type must be created.
   * @return a new instance of the specified type
   */
  public static Object newInstanceOfFieldType(Field field) {
    Class<?>[] generics = getGenericTypes(field);
    Class<?> fieldType = field.getType();
    try {
      return newInstanceHonoringConstructor(fieldType, generics);

    } catch (ReflectionErrorException honoringConstructorException) {
      try {
        return newInstanceIgnoringConstructor(fieldType);

      } catch (ReflectionErrorException ignoringConstructorException) {
        ignoringConstructorException.addSuppressed(honoringConstructorException);
        throw ignoringConstructorException;
      }
    }
  }

  /**
   * Sets the value of the specified field on the given bean instance.
   *
   * @param beanInstance the instance of the bean containing the field
   * @param field the field to set the value for
   * @param value the value to set
   */
  public static void setFieldValue(Object beanInstance, Field field, Object value) {
    try {
      FieldUtils.writeField(field, beanInstance, value, true);
    } catch (Exception ex) {
      throw new ReflectionErrorException(
          String.format(
              "It was not possible to set the value '%s' into the field '%s' of type '%s'.",
              value, field.getName(), beanInstance.getClass().getName()),
          ex);
    }
  }
}

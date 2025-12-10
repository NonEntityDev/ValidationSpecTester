# Validation Spec Tester

## Introduction

Validation Spec Tester is a small helper library to simplify testing Bean Validation (JSR 380 / Jakarta Bean Validation)
constraints in Java projects. It provides a concise, fluent API to assert constraint violations on bean fields and
improves test readability and diagnostics.

## Features

- Fluent assertions for constraint violations on bean fields
- Exact, substring and regex matching for violation messages
- Chained assertions for multiple fields in a single statement
- Clear failure messages that show expected and actual violations
- Small, test-focused API with no tight coupling to validation internals

## Quickstart

Add the library to your Maven project:

```xml
<dependency>
  <groupId>dev.nonentity</groupId>
  <artifactId>validation-spec-tester</artifactId>
  <version>0.1.0</version>
  <scope>test</scope>
</dependency>
```

## Usage

The library exposes a fluent entry point to run assertions against a bean instance. Typical methods (illustrative):

- `givenBeanInstance(Object bean)` — start assertions for a bean instance
- `.fieldHasError(String field, String expectedMessage)` — assert that a field has a violation matching the expected
  message
- `.fieldHasErrorContaining(String field, String substringOrRegex)` — substring or regex matching
- `.fieldHasNoneOfErrors(String field, String... messages)` — assert none of the provided messages appear as violations

Example (test-focused pseudocode):

```java
// Arrange
User user = new User();
user.setEmail("invalid");
user.setAge(-1);

// Act + Assert (fluent)
givenBeanInstance(user)
    .fieldHasError("email", "must be a well-formed email address")
    .fieldHasErrorContaining("age", "must be greater than")
    .fieldHasNoneOfErrors("username", "must not be null");
```

The assertions produce helpful failure messages that include both the expected and actual violations for the field.

## Assertion semantics

- Exact match: `fieldHasError` checks for a violation message that equals the provided string.
- Substring or regex: `fieldHasErrorContaining` accepts a literal substring or a regex (documentation of the method
  specifies behavior).
- Negative checks: `fieldHasNoneOfErrors` ensures none of the provided messages are present for the field.

## API Notes

- The library performs validation using the platform `Validator` behind the scenes. Tests do not need to interact
  directly with `ValidatorFactory` or `Validator`.
- Assertions are designed to be chainable and descriptive on failure.

## Building and testing

From the project root:

- Build: `mvn -DskipTests=false clean package`
- Run tests: `mvn test`

The project uses Maven and standard Java tooling. IntelliJ IDEA is fully supported.

## Compatibility

- Java: 17+
- Bean Validation: compatible with Jakarta Bean Validation / Hibernate Validator on the classpath

## Contributing

Contributions, bug reports and pull requests are welcome.

- Fork the repository
- Create a feature branch
- Add tests for new behavior
- Open a pull request with a short description of changes

Follow the repository code style and include unit tests for new features or bug fixes.

## License

Distributed under the MIT License. See `LICENSE` for details.

## Acknowledgements

Offers a compact testing API inspired by common testing patterns for bean validation to reduce boilerplate and improve
test clarity.

```
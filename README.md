# Validation Spec Tester

## Introduction.

Bean validation is great! A very simple but powerful declarative way to validate data on beans. However,
testing it can be tedious for engineers and cost ineffective for companies. This project aims to provide
a small library to help testing bean validation in any project.

## What is the problem?

But why testing bean validation is so unpleasant? The first issue is that we expose our code to details
of the bean validation api. Let's take as an example a simple code to validate a bean and look up for
a specific violation among the reported violations:

```java

@Test
void firstNameCannotBeNull() {

  // Arrange
  try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
    Validator validator = validatorFactory.getValidator();
    Contact bean = new Contact();

    // Act
    Set<ConstraintViolation<Contact>> violations = validator.validate(bean);
    Optional<ConstraintViolation<Contact>> matchingViolation = violations.stream()
            .fitler((ConstraintViolation violation) -> violation.getPropertyPath().toString().equals("firstName"))
            .filter((ConstraintViolation violation) -> violation.getMessage().equals("must not be null"))
            .findFirst();

    // Assert
    assertThat(matchingViolation).isNotEmpty();
  }

}
```

As we can see, we require a good amount of boilerplate code to perform the simplest lookup possible.
Our first goal is to offer a high level way to use the bean validation API without coupling our code.
So, our first helper is the ```AssertConstraintViolation``` which allow engineers to perform assertions
in a much more enjoyable and idiomatic way.

```java

@Test
void test() {
  // Arrange
  Contact bean = new Contact();
  bean.setEmail("admin");
  bean.setBirtDate(LocalDate.of(2050, 1, 3));

  // Act and Assert
  givenBeanInstance(bean)
          .fieldHasError("firstName", "must not be null")
          .fieldHasError("surname", "must not be null")
          .fieldHasErrorContaining("email", "^.* well-formed email address$") // Regex matching
          .fieldHasError("birthDate", "be in the past") // Substring matching
          .fieldHasNoneOfErrors("companyName", "must not be null", "length must be between 3 and 140"); // Multiple negative scenario
}
```

Assertions can be chained for multiple fields and each assertion can check for multiple violations. No more stream
blocks performing lookups, no more code initializing validator and a much more idiomatic code to check our validations.

Another aspect improved was the assertion errors messages that point exactly the reason why the test fail, while also
output the violations found for that bean instance field. The following are couple examples of assertion errors messages
output when test fails.

```
// When the field does not have the expected violation.
Field firstName expected to have the violations [length must be between 3 and 140], but violations [must not be null] were found."

// When the field has a violation that is not expected.
Field companyName not expected to have any of violations [must not be null]. Violations found for the field: [must not be null]
```


# ONoteReader

This project is about the *ONoteReader* application - an open source and non-profit project. It is a tool to convert an "Event Model" into a generic Java project.
The Event Model is provided via [oNote][onote]. They provide the option to create an export comprehensive Event Models in a short amount of time. In addition, a user can develop "Schemas" which add further technical logic to the model.

Its goal is to convert the Event Model including the Schemas into a versatile Java project with useful Java classes which closely represent the given model. Therefore, a user can save a lot of time, money and effort developing the same logic, which ONoteReader does within a fraction of a second. The project and any additional logic can easily be extended and implemented.

## How Does It Work

Every information contained within the JSON file will be used to generate the Java project. Commands, Events and Read Models and their relationship to each other will be generated this way alongside Entities which resemble the Schemas. They may represent Value Objects, Entities or Aggregates similar to those used in Domain-Driven Design ([DDD][ddd]). Each given Event Stream will be separated into an independent project (or package) - a Bounded Context.

## Prerequisites

The project was delevoped with [Maven][maven] and [JDK 11][jdk11]. External libraries used for the development are [Gson][gson] and [JUnit 5][junit5].

The target version of the project JAR file is Java Version 11 as well.

## Functionality & Features

- ONoteReader provides a simple but effective graphical user interface in the form of a desktop application.
- A user can select whether he wants to extend an already existing project or create a new project(s).
- A user can provide the project path where the project shall be generated and the path of the JSON file to use.
- A user can declare a new namespace, use a default namespace (main), or use the Schema namespace of the event model.
- The application supports internationalization (I18N) currently with the languages English (default) and German.

[maven]: https://maven.apache.org/
[gson]: https://github.com/google/gson
[junit5]: https://junit.org/junit5/docs/current/user-guide/
[jdk11]: https://www.oracle.com/java/technologies/downloads/#java11
[onote]: https://www.onote.com/
[ddd]: https://martinfowler.com/bliki/DomainDrivenDesign.html
